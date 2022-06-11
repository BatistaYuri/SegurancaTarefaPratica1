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
    private final GCM gcm;
    
    public static ControllerUsuario getInstancia(){
        if(instancia == null){
            instancia = new ControllerUsuario();
        }
        return instancia;
    }

    public ControllerUsuario() {
        this.scanner = new Scanner(System.in);
        this.gcm = GCM.getInstance();
    }
    
    public String getPBKDF2(String login, String senha) throws NoSuchAlgorithmException, NoSuchProviderException {
        String saltHashLogin = Util.getHash256(senha);
        return PBKDF2UtilBCFIPS.generateDerivedKey(senha, saltHashLogin);
    }
    
    public String messagemUsuario(String token,String code) {
        System.out.println("Digite sua pergunta de Sim ou Não:");
        String msg = scanner.nextLine();
        if(msg.toString().equals("exit")){
            return null;
        }
        String cifrada = this.gcm.cifrarGCM(token,code,msg);
        System.out.println("Msg cifrada pelo cliente = " + cifrada);
        return cifrada;
    }
    
    public void lerMsg(String token,String code, String msg){
        String defifrada = gcm.decifrarGCM(token, code, msg);
        System.out.println("Msg decifrada pelo cliente = " + defifrada);
       
    }
    
}
