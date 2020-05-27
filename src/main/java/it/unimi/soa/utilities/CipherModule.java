package it.unimi.soa.utilities;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * TODO
 */
public class CipherModule {

    private static final String KEY = "PBKDF2WithHmacSHA256";
    private static final String ALGO = "AES";
    private static final int ITERATION_COUNT = 65536;
    private static final int LENGHT = 256;

    public static byte[] cipher(int CYPHER_MODE, char[] password, byte[] data)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeySpecException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        // Encrypt for the ticket-granting server
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY);
        byte[] salt = {1};
        Cipher cipher = Cipher.getInstance(ALGO);

        KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, LENGHT);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey grantingServerKey = new SecretKeySpec(tmp.getEncoded(), ALGO);

        cipher.init(CYPHER_MODE, grantingServerKey);

        return cipher.doFinal(data);
    }

    public static byte[] encrypt(char[] password, byte[] data)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeySpecException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {
        return cipher(Cipher.ENCRYPT_MODE, password, data);
    }

    public static byte[] decrypt(char[] password, byte[] data)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeySpecException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {
        return cipher(Cipher.DECRYPT_MODE, password, data);
    }
}
