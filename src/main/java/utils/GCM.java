/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author Yuri
 */
public class GCM {

    private static GCM instance;
    private static Util util;

    public static GCM getInstance() {
        if (instance == null) {
            instance = new GCM();
        }
        return instance;
    }

    public String cifrarGCM(String token, String code, String msg) {
        try {
            System.out.println("Msg = " + msg);

            //  chave (K)
            byte[] K = org.apache.commons.codec.binary.Hex.decodeHex(token.toCharArray());
            //  texto plano (P)
            byte[] P = msg.getBytes();
            //  nonce (IV)
            byte[] N = org.apache.commons.codec.binary.Hex.decodeHex(code.toCharArray());
            Cipher in = Cipher.getInstance("AES/GCM/NoPadding", "BCFIPS");
            Key key = new SecretKeySpec(K, "AES");
            GCMParameterSpec​ gcmParameters = new GCMParameterSpec​(128, N);

            in.init(Cipher.ENCRYPT_MODE, key, gcmParameters);
            byte[] enc = in.doFinal(P);

            return Hex.encodeHexString(enc);

        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | DecoderException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    ;
    
    public String decifrarGCM(String token, String code, String msg) {
        try {
            //  chave (K)
            byte[] K = org.apache.commons.codec.binary.Hex.decodeHex(token.toCharArray());
            //  texto plano (P)
            byte[] P = msg.getBytes();
            //  nonce (IV)
            byte[] N = org.apache.commons.codec.binary.Hex.decodeHex(code.toCharArray());

            Key key = new SecretKeySpec(K, "AES");
            GCMParameterSpec​ gcmParameters = new GCMParameterSpec​(128, N);

            Cipher out = Cipher.getInstance("AES/GCM/NoPadding", "BCFIPS");
            out.init(Cipher.DECRYPT_MODE, key, gcmParameters);

            byte[] out2 = out.doFinal(Hex.decodeHex(msg.toCharArray()));
            return Util.toString(out2);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | DecoderException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
;

}
