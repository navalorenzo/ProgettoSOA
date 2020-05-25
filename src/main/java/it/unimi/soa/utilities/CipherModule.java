package it.unimi.soa.utilities;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class CipherModule {

    public static byte[] cipher(int CYPHER_MODE, char[] password, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // encrypt for the ticket-granting server
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] salt = {1};
        Cipher cipher = Cipher.getInstance("AES");

        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey grantingServerKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        cipher.init(CYPHER_MODE, grantingServerKey);

        return cipher.doFinal(data);
    }

    public static byte[] encrypt(char[] password, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return cipher(Cipher.ENCRYPT_MODE, password, data);
    }

    public static byte[] decrypt(char[] password, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return cipher(Cipher.DECRYPT_MODE, password, data);
    }
}
