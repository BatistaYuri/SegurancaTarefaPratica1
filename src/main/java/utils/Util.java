/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

/**
 *
 * @author Yuri
 */
public class Util {

    private static String digits = "0123456789abcdef";

    public static String toString(byte[] bytes) {
        return toString(bytes, bytes.length);
    }

    public static String toString(byte[] bytes, int length) {
        char[] chars = new char[length];
        for (int i = 0; i != chars.length; i++) {
            chars[i] = (char) (bytes[i] & 0xff);
        }
        return new String(chars);
    }

    public static String toHex(byte[] data) {
        return toHex(data, data.length);
    }

    public static String toHex(byte[] data, int length) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i != length; i++) {
            int v = data[i] & 0xff;

            buf.append(digits.charAt(v >> 4));
            buf.append(digits.charAt(v & 0xf));
        }

        return buf.toString();
    }

    public static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        //SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Hex.encodeHexString(salt);
    }
    
    public static String getHash256(String senha) throws NoSuchAlgorithmException, NoSuchProviderException {
         // Instanciar um novo Security provider
        int addProvider = Security.addProvider(new BouncyCastleFipsProvider());
        MessageDigest hash = MessageDigest.getInstance("SHA256", "BCFIPS");
        byte [] valorHash2 = hash.digest(senha.getBytes());
        return org.bouncycastle.util.encoders.Hex.toHexString(valorHash2);
    }

}
