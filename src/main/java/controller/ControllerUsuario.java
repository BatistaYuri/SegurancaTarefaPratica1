/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Scanner;
import utils.GCM;
import utils.PBKDF2UtilBCFIPS;
import utils.Util;

/**
 *
 * @author Yuri
 */
public class ControllerUsuario {

    private static ControllerUsuario instancia;
    private final Scanner scanner;
    private String token;
    private String code;
    private String key;

    public static ControllerUsuario getInstancia() {
        if (instancia == null) {
            instancia = new ControllerUsuario();
        }
        return instancia;
    }

    public ControllerUsuario() {
        this.scanner = new Scanner(System.in);
    }

    public String getPBKDF2(String login, String senha) throws NoSuchAlgorithmException, NoSuchProviderException {
        String saltHashLogin = Util.getHash256(senha);
        this.token = PBKDF2UtilBCFIPS.generateDerivedKey(senha, saltHashLogin);
        return this.token;
    }
    
    public String twoFA(){
        Scanner scanner = new Scanner(System.in);
        String code = scanner.nextLine();
        this.code = code;
        this.key = null;
        return code;
    }

    public String enviarMensagem() {
        System.out.println("Digite sua pergunta de resposta Sim ou Não:");
        String msg = scanner.nextLine();
        if (msg.toString().equals("exit")) {
            return null;
        }
        if (this.key == null) {
            this.key = PBKDF2UtilBCFIPS.generateDerivedKey(this.token, this.code);
        }
        String cifrada = GCM.cifrarGCM(this.key, this.code, msg);
        System.out.println("Msg cifrada pelo cliente = " + cifrada);
        return cifrada;
    }

    public void lerMensagem(String msg) {
        String defifrada = GCM.decifrarGCM(this.key, this.code, msg);
        System.out.println("Msg decifrada pelo cliente = " + defifrada);

    }

}
