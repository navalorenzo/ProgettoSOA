package it.unimi.soa.message.auth;

import com.google.gson.Gson;
import it.unimi.soa.message.ticket.GrantingServerTicket;
import it.unimi.soa.message.ticket.MessageTicketRequest;
import it.unimi.soa.message.ticket.UserTicket;
import it.unimi.soa.utilities.CipherModule;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class MessageAuthToken {
    public String authTicket;

    public MessageAuthToken() {}

    public MessageAuthToken(String authTicket) {
        this.authTicket = authTicket;
    }

    public MessageAuthToken createJSONToken(String username, String ipAddr, String ticketGrantingPassword, String userPassword) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        final int LIFETIME = 60 * 60000;

        // create ticket for the ticket-granting server
        long timestamp = System.currentTimeMillis();
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(timestamp);
        long authenticator = secureRandom.nextLong();

        /* the authenticator is needed to prove that the person using a ticket is the same person to whom that ticket was issued. */
        GrantingServerTicket ticket = new GrantingServerTicket(username, ipAddr, String.valueOf(timestamp), String.valueOf(LIFETIME), String.valueOf(authenticator));

        // encrypt for the ticket-granting server
        byte[] grantingServerTicket = CipherModule.encrypt(ticketGrantingPassword.toCharArray(), new Gson().toJson(ticket).getBytes());

        // add authenticator to the client
        String authenticatorTicket = new Gson().toJson(new UserTicket(String.valueOf(authenticator), grantingServerTicket));

        /* Message = [[TICKET]k_AS/TGS, Authenticator]k_Client */

        // encrypt for the client
        byte[] clientTicket = CipherModule.encrypt(userPassword.toCharArray(), authenticatorTicket.getBytes());

        // create message for the ticket-granting server
        authTicket = new Gson().toJson(new MessageTicketRequest(clientTicket));
        return this;
    }
}
