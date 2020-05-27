package it.unimi.soa.message.tgs;

import com.google.gson.Gson;
import it.unimi.soa.ticket.ServiceTicket;
import it.unimi.soa.utilities.CipherModule;
import it.unimi.soa.utilities.SharedPassword;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * TODO
 */
public class MessageTGSResponse {
    public byte[] serviceEncryptedTicket;
    public byte[] clientServerEncryptedSessionKey;

    public MessageTGSResponse() {
    }

    public MessageTGSResponse createJSONTicket(String username, String ipAddr, String service, String clientTgsSessionKey)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException,
            InvalidKeySpecException {
        final int LIFETIME = 30 * 60000;

        // create ticket for the service server
        long timestamp = System.currentTimeMillis();
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(timestamp);
        long clientServerSessionKey = secureRandom.nextLong();

        // Create ticket for the service server
        ServiceTicket serviceTicket = new ServiceTicket(username, ipAddr, service, timestamp, LIFETIME, String.valueOf(clientServerSessionKey));

        // Encrypt the ticket for the service server
        String password = SharedPassword.getInstance().getTGSSSKey(service);
        serviceEncryptedTicket = CipherModule.encrypt(password.toCharArray(), (new Gson().toJson(serviceTicket)).getBytes());

        // Encrypt the client-service session key
        clientServerEncryptedSessionKey = CipherModule.encrypt(clientTgsSessionKey.toCharArray(), String.valueOf(clientServerSessionKey).getBytes());

        return this;
    }

    public byte[] getServiceEncryptedTicket() {
        return serviceEncryptedTicket;
    }

    public byte[] getClientServerEncryptedSessionKey() {
        return clientServerEncryptedSessionKey;
    }
}
