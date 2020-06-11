package it.unimi.soa.utilities;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * This is the module used for symmetric/asymmetric encryption and decryption
 */
public class CipherModule {

    private static final String KEY = "PBKDF2WithHmacSHA256";
    private static final String ALGOAES = "AES";
    private static final int ITERATION_COUNT = 65536;
    private static final int LENGHT = 256;
    private static final String ALGORSA = "RSA";

    // Symmetric

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
        Cipher cipher = Cipher.getInstance(ALGOAES);

        KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, LENGHT);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey grantingServerKey = new SecretKeySpec(tmp.getEncoded(), ALGOAES);

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

    // Asymmetric

    public static PrivateKey readAsymmetricPrivateKey(String privateKeyFile) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] keyBytes = Files.readAllBytes(new File(privateKeyFile).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

        return KeyFactory.getInstance(ALGORSA).generatePrivate(spec);
    }

    public static PublicKey readAsymmetricPublicKey(String publicKeyFile) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] keyBytes = Files.readAllBytes(new File(publicKeyFile).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);

        return KeyFactory.getInstance(ALGORSA).generatePublic(spec);
    }

    public static byte[] encryptUsingPublicKey(PublicKey publicKey, byte[] data)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptUsingPrivateKey(PrivateKey privateKey, byte[] data)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }
}
