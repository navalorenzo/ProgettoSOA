package it.unimi.soa.message.as;

import com.google.gson.Gson;
import it.unimi.soa.ticket.TGSTicket;
import it.unimi.soa.utilities.CipherModule;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * This is a wrapper for the message sent by the AS to the client. It's content is encrypted and only the user can
 * decrypt it using it's password. This is the step #2 in the Kerberos protocol. This is the step #2.
 */
public class MessageAuthResponse {
    public byte[] tgsEncryptedTicket;
    public byte[] clientTgsEncryptedSessionKey;

    public MessageAuthResponse() {
    }

    public MessageAuthResponse createJSONToken(String username, String ipAddr, String ticketGrantingPassword, String userPassword)
            throws InvalidKeySpecException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            BadPaddingException,
            IllegalBlockSizeException,
            InvalidKeyException {
        final int LIFETIME = 60 * 60000;

        // Create ticket for the ticket-granting server
        long timestamp = System.currentTimeMillis();
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(timestamp);
        long clientTgsSessionKey = secureRandom.nextLong();

        // The clientTgsSessionKey is needed to prove that the person using a ticket is the same person to whom that ticket was issued.
        TGSTicket tgsTicket = new TGSTicket(username, ipAddr, timestamp, LIFETIME, String.valueOf(clientTgsSessionKey));

        // Encrypt for the ticket-granting server
        tgsEncryptedTicket = CipherModule.encrypt(ticketGrantingPassword.toCharArray(), new Gson().toJson(tgsTicket).getBytes());

        // Craft the ticket for the client containing the client-tgs session key

        // Message = ([TGSTicket]k_AS/TGS, [Authenticator]k_Client)

        // Encrypt for the client
        clientTgsEncryptedSessionKey = CipherModule.encrypt(userPassword.toCharArray(), String.valueOf(clientTgsSessionKey).getBytes());

        return this;
    }

    public byte[] getClientTgsEncryptedSessionKey() {
        return clientTgsEncryptedSessionKey;
    }

    public byte[] getTgsEncryptedTicket() {
        return tgsEncryptedTicket;
    }
}
