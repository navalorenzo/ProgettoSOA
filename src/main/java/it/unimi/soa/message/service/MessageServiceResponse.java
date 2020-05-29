package it.unimi.soa.message.service;

import it.unimi.soa.utilities.CipherModule;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * This message contains the timestamp challenge. This is needed by the client to verify the server identity. This is
 * the step #6.
 */
public class MessageServiceResponse {
    public byte[] timestampEncryptedChallenge;
    public String welcomeMessage;

    public MessageServiceResponse() {
    }

    public MessageServiceResponse createJSONToken(long timestamp, String message, String clientServerSessionKey)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException,
            InvalidKeySpecException {
        timestampEncryptedChallenge = CipherModule.encrypt(clientServerSessionKey.toCharArray(), String.valueOf(timestamp).getBytes());
        welcomeMessage = message;

        return this;
    }

    public byte[] getTimestampEncryptedChallenge() {
        return timestampEncryptedChallenge;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }
}
