package utils;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.fips.Scrypt;
import org.bouncycastle.util.Strings;
import org.bouncycastle.crypto.KDFCalculator;

public class SCRYPT { // Exemplo de Scrypt com BCFIPS

    // Adaptado de https://downloads.bouncycastle.org/fips-java/BC-FJA-UserGuide-1.0.2.pdf
    public static byte[] useScryptKDF(char[] password,
            byte[] salt, int costParameter, int blocksize, int parallelizationParam) {

        KDFCalculator<Scrypt.Parameters> calculator
                = new Scrypt.KDFFactory()
                        .createKDFCalculator(
                                Scrypt.ALGORITHM.using(salt, costParameter, blocksize, parallelizationParam,
                                        Strings.toUTF8ByteArray(password)));
        byte[] output = new byte[32];
        calculator.generateBytes(output);
        return output;
    }

    public static String generateDerivedKey(String token, String salt) {
        int costParameter = 2048; // N custo que afeta uso de memória e CPU

        int blocksize = 8; // r tamanho do bloco

        int parallelizationParam = 1; // p parametro de paralelizacao

        byte[] derivedKeyFromScrypt;
        derivedKeyFromScrypt = SCRYPT.useScryptKDF(token.toCharArray(), salt.getBytes(),
                costParameter,
                blocksize, parallelizationParam);

        return Hex.encodeHexString(derivedKeyFromScrypt);
    }
}
