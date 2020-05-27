package it.unimi.soa.message.service;

import it.unimi.soa.utilities.CipherModule;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * TODO
 */
public class MessageServiceResponse {
    public byte[] timestampEncryptedChallenge;

    public MessageServiceResponse() {
    }

    public MessageServiceResponse createJSONToken(long timestamp, String clientServerSessionKey)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException,
            InvalidKeySpecException {
        timestampEncryptedChallenge = CipherModule.encrypt(clientServerSessionKey.toCharArray(), String.valueOf(timestamp).getBytes());

        return this;
    }

    public byte[] getTimestampEncryptedChallenge() {
        return timestampEncryptedChallenge;
    }
}
