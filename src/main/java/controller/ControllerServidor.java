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
import utils.SCRYPT;
import utils.Util;

/**
 *
 * @author Yuri
 */
public class ControllerServidor {

    private static ControllerServidor instancia;
    private final GCM gcm;
    private final UsuarioDAO usuarioDAO;
    ArrayList<String> respostas;

    public static ControllerServidor getInstancia() {
        if (instancia == null) {
            instancia = new ControllerServidor();
        }
        return instancia;
    }

    public ControllerServidor() {
        this.gcm = GCM.getInstance();
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

    public String loginServidor(String login, String token) throws NoSuchAlgorithmException, NoSuchProviderException, WriterException, IOException {
        Usuario usuario = this.usuarioDAO.get(login);
        if (usuario == null) {
            System.out.println("Usuário não cadastrado!");
            return null;
        }
        String tokenScrypt = SCRYPT.generateDerivedKey(token, usuario.getSalt());
        if (!usuario.getSenha().toString().equals(tokenScrypt.toString())) {
            System.out.println("Senha incorreta");
            return null;
        }
        String TOTPcode = getTOTPCode(tokenScrypt);
        //String barCodeUrl = getGoogleAuthenticatorBarCode(tokenScrypt, "email@gmail.com", login);
        //createQRCode(barCodeUrl, "matrixURL.png", 246, 246);
        System.out.println("Procure o arquivo matrixCode.png no diretorio do projeto e leia o QR code para digitar o código");
        createQRCode(TOTPcode, "matrixCode.png", 246, 246);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entre o código de autenticação: ");
        System.out.println(TOTPcode);
        String code = scanner.nextLine();
        if (code.toString().equals(TOTPcode.toString())) {
            System.out.println("Logged in successfully");
            return code;
        } else {
            System.out.println("Invalid 2FA Code");
            return null;
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

    public String messagemServidor(String token, String code, String msg) {
        String defifrada = gcm.decifrarGCM(token, code, msg);
        System.out.println("Msg decifrada pelo servidor = " + defifrada);
        String cifrada = gcm.cifrarGCM(token, code, this.respostas.get((int) Math.round(Math.random())));
        System.out.println("Msg cifrada pelo servidor = " + cifrada);
        return cifrada;
    }

}
