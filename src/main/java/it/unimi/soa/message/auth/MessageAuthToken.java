package it.unimi.soa.message.auth;

import com.google.gson.Gson;
import it.unimi.soa.message.ticket.MessageTicketRequest;
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
        String ticket = username + "#" + ipAddr + "#" + timestamp + "#" + LIFETIME + "#" + authenticator;

        // encrypt for the ticket-granting server
        byte[] grantingServerTicket = CipherModule.encrypt(ticketGrantingPassword.toCharArray(), ticket.getBytes());

        // add authenticator to the client
        byte[] authenticatorTicket = new byte[grantingServerTicket.length + ("#" + authenticator).getBytes().length];
        System.arraycopy(grantingServerTicket, 0, authenticatorTicket, 0, grantingServerTicket.length);
        System.arraycopy(String.valueOf(authenticator).getBytes(), 0, authenticatorTicket, grantingServerTicket.length, String.valueOf(authenticator).getBytes().length);

        /* Message = [[TICKET]k_AS/TGS, Authenticator]k_Client */

        // encrypt for the client
        byte[] clientTicket = CipherModule.encrypt(userPassword.toCharArray(), authenticatorTicket);

        // create message for the ticket-granting server
        authTicket = new Gson().toJson(new MessageTicketRequest(clientTicket));
        return this;
    }
}
