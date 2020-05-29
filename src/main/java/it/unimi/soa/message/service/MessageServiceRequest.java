package it.unimi.soa.message.service;

import com.google.gson.Gson;
import it.unimi.soa.ticket.AuthenticatorServerTicket;
import it.unimi.soa.utilities.CipherModule;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * This message contains the encrypted ticket and the session key which will be used to encrypt the communications with
 * the Service server. This is the step #3
 */
public class MessageServiceRequest {
    public byte[] serviceEncryptedTicket;
    public byte[] clientServerEncryptedSessionKey;

    public MessageServiceRequest() {
    }

    public MessageServiceRequest createJSONToken(String username, String clientServerSessionKey, byte[] serviceEncryptedTicket)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException,
            InvalidKeySpecException {

        // Attach the service encrypted ticket
        this.serviceEncryptedTicket = serviceEncryptedTicket;

        // Create authenticator for the ticket-granting server
        long timestamp = System.currentTimeMillis();
        AuthenticatorServerTicket authenticatorServerTicket = new AuthenticatorServerTicket(username, timestamp);

        // Encrypt for the ticket-granting server
        clientServerEncryptedSessionKey = CipherModule.encrypt(clientServerSessionKey.toCharArray(), new Gson().toJson(authenticatorServerTicket).getBytes());

        return this;
    }

    public byte[] getServiceEncryptedTicket() {
        return serviceEncryptedTicket;
    }

    public byte[] getClientServerEncryptedSessionKey() {
        return clientServerEncryptedSessionKey;
    }
}
