/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.tarefapraticacom;
import com.google.zxing.WriterException;
import controller.Controller;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;


/**
 *
 * @author Yuri
 */
public class TarefaPraticaCOM {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, WriterException, IOException {
        Controller.getInstancia().init();
    }
}
