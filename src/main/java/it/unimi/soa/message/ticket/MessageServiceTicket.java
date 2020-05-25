package it.unimi.soa.message.ticket;

import com.google.gson.Gson;
import it.unimi.soa.message.service.GrantingServiceTicket;
import it.unimi.soa.service.Service;
import it.unimi.soa.utilities.CipherModule;
import it.unimi.soa.utilities.SharedPassword;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class MessageServiceTicket {
    public String username;
    public byte[] serviceTicket;

    public MessageServiceTicket() {}

    public MessageServiceTicket createJSONTicket(String username, String ipAddr, Service service) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        final int LIFETIME = 60 * 60000;

        // create ticket for the ticket-granting server
        long timestamp = System.currentTimeMillis();
        GrantingServiceTicket grantingServiceTicket = new GrantingServiceTicket(username, ipAddr, service, timestamp, LIFETIME);

        // encrypt the ticket for the service server
        this.username = username;
        this.serviceTicket = CipherModule.encrypt(SharedPassword.getTGSSSKey().toCharArray(), new Gson().toJson(grantingServiceTicket).getBytes());
        return this;
    }
}
