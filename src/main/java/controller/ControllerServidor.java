/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import entidade.Usuario;
import entidade.UsuarioDAO;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import utils.GCM;
import utils.PBKDF2UtilBCFIPS;
import utils.SCRYPT;
import utils.Util;

/**
 *
 * @author Yuri
 */
public class ControllerServidor {

    private static ControllerServidor instancia;
    private final UsuarioDAO usuarioDAO;
    ArrayList<String> respostas;
    private String token;
    private String tokenScrypt;
    private String code;
    private String key;

    public static ControllerServidor getInstancia() {
        if (instancia == null) {
            instancia = new ControllerServidor();
        }
        return instancia;
    }

    public ControllerServidor() {
        this.usuarioDAO = UsuarioDAO.getInstancia();
        this.respostas = new ArrayList<>();
        respostas.add("Sim!");
        respostas.add("Não!");
    }

    public void cadastraServidor(String login, String token) throws NoSuchAlgorithmException {
        String salt = Util.getSalt();
        String tokenScrypt = SCRYPT.generateDerivedKey(token, salt);
        this.usuarioDAO.put(new Usuario(login, tokenScrypt, salt));
    }

    public boolean loginServidor(String login, String token) throws NoSuchAlgorithmException, NoSuchProviderException, WriterException, IOException {
        Usuario usuario = this.usuarioDAO.get(login);
        if (usuario == null) {
            System.out.println("Usuário não cadastrado!");
            return false;
        }
        this.tokenScrypt = SCRYPT.generateDerivedKey(token, usuario.getSalt());
        if (!usuario.getSenha().toString().equals(tokenScrypt.toString())) {
            System.out.println("Senha incorreta");
            return false;
        }
        this.token = token;
        return true;

    }

    public void twoFA() throws WriterException, IOException {
        String TOTPcode = getTOTPCode(this.tokenScrypt);
        System.out.println("Procure o arquivo matrixCode.png no diretorio do projeto e leia o QR code para digitar o código");
        createQRCode(TOTPcode, "matrixCode.png", 246, 246);
        System.out.println(TOTPcode);
        System.out.println("Entre o código de autenticação: ");
    }

    public boolean validate2FA(String code) {
        String newTOTPcode = getTOTPCode(tokenScrypt);
        if (code.toString().equals(newTOTPcode.toString())) {
            System.out.println("Logged in successfully");
            this.code = code;
            return true;
        } else {
            System.out.println("Invalid 2FA Code");
            return false;
        }
    }

    public Collection<Usuario> getUsuarios() {
        return this.usuarioDAO.getList();
    }

    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void createQRCode(String barCodeData, String filePath, int height, int width)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
                width, height);
        try ( FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }

    public String lerResponderMessagem(String msg) {
        if (this.key == null) {
            this.key = PBKDF2UtilBCFIPS.generateDerivedKey(this.token, this.code);
        }
        String defifrada = GCM.decifrarGCM(this.key, this.code, msg);
        System.out.println("Msg decifrada pelo servidor = " + defifrada);
        String cifrada = GCM.cifrarGCM(this.key, this.code, this.respostas.get((int) Math.round(Math.random())));
        System.out.println("Msg cifrada pelo servidor = " + cifrada);
        return cifrada;
    }

}
