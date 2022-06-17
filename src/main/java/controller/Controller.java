/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.google.zxing.WriterException;
import entidade.Usuario;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

/**
 *
 * @author Yuri
 */
public class Controller {

    private static Controller instancia;
    private final Scanner scanner;
    private final ControllerUsuario controllerUsuario;
    private final ControllerServidor controllerServidor;
    ArrayList<String> respostas;

    public static Controller getInstancia() {
        if (instancia == null) {
            instancia = new Controller();
        }
        return instancia;
    }

    public Controller() {
        this.scanner = new Scanner(System.in);
        this.controllerUsuario = ControllerUsuario.getInstancia();
        this.controllerServidor = ControllerServidor.getInstancia();
    }

    public void init() throws NoSuchAlgorithmException, NoSuchProviderException, WriterException, IOException {
        System.out.println("[1] Login");
        System.out.println("[2] Cadastrar Usuário");
        System.out.println("[3] Listar Usuários");
        System.out.println("[4] Sair");
        String opcao = scanner.nextLine();

        switch (opcao) {
            case "1":
                this.login();
                break;
            case "2":
                this.cadastrar();
                break;
            case "3":
                this.listar();
                break;
            default:
                System.exit(0);
                break;
        }
    }
    
    public void cadastrar() throws NoSuchAlgorithmException, NoSuchProviderException, WriterException, IOException {
        System.out.println("Novo login:");
        String login = scanner.nextLine();
        System.out.println("Nova senha:");
        String senha = scanner.nextLine();

        String token = this.controllerUsuario.getPBKDF2(login, senha);
        this.controllerServidor.cadastraServidor(login, token);
        System.out.println("Novo usuário cadastrado!");
        this.init();
    }

    public void login() throws NoSuchAlgorithmException, NoSuchProviderException, WriterException, IOException {
        System.out.println("Login:");
        String login = scanner.nextLine();
        System.out.println("Senha:");
        String senha = scanner.nextLine();

        String token = this.controllerUsuario.getPBKDF2(login, senha);

        this.loginFirstFactor(login, token);
        this.loginSecondFactor();
        this.mensagem();
    }

    public void loginFirstFactor(String login, String token) throws NoSuchAlgorithmException, NoSuchProviderException, WriterException, IOException {
        boolean firstFactor = this.controllerServidor.loginServidor(login, token);
        if (!firstFactor) {
            this.login();
            return;
        }
    }

    public void loginSecondFactor() throws WriterException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        this.controllerServidor.twoFA();
        String code = this.controllerUsuario.twoFA();
        boolean secondFactor = this.controllerServidor.validate2FA(code);
        if (!secondFactor) {
            this.login();
            return;
        }
    }

    public void listar() throws NoSuchAlgorithmException, NoSuchProviderException, WriterException, IOException {
        Collection<Usuario> usuarios = controllerServidor.getUsuarios();
        System.out.println("Usuários:");
        usuarios.forEach(usuario -> System.out.println(usuario.getLogin()));
        this.init();
    }
    
    public void mensagem () throws NoSuchAlgorithmException, NoSuchProviderException, WriterException, IOException {
        String msg = "";
        String msgServidor = null;
        while (msg != null) {
            if (msgServidor != null) {
                this.controllerUsuario.lerMensagem(msgServidor);
            }
            msg = this.controllerUsuario.enviarMensagem();
            if (msg != null) {
                msgServidor = this.controllerServidor.lerResponderMensagem(msg);
            }
        }
        this.init();
    }

}
