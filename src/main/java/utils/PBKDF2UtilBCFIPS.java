package utils;

import java.security.Security;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

/**
 *
 * @author Yuri
 */
public class PBKDF2UtilBCFIPS {
        
    public static String generateDerivedKey(String password, String salt) {
        int addProvider;
        addProvider = Security.addProvider(new BouncyCastleFipsProvider());
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 5000, 128); // senha / salt / interacoes / tamanho
        SecretKeyFactory pbkdf2 = null;
        String derivedPass = null;
        try {
            pbkdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512", "BCFIPS");
            SecretKey sk = pbkdf2.generateSecret(spec);
            derivedPass = Hex.encodeHexString(sk.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return derivedPass;
    }
}
