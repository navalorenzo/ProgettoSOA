package it.unimi.soa.ticket;

import com.google.gson.Gson;
import it.unimi.soa.message.ticket.GrantingServerTicket;
import it.unimi.soa.message.ticket.MessageTicketRequest;
import it.unimi.soa.message.ticket.MessageServiceTicket;
import it.unimi.soa.service.Service;
import it.unimi.soa.utilities.CipherModule;
import it.unimi.soa.utilities.SharedPassword;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class TicketGrantingServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get info
        MessageTicketRequest messageTicketRequest = new Gson().fromJson(request.getReader().readLine(), MessageTicketRequest.class);

        // get client info
        String ipAddr = request.getRemoteAddr() + request.getRemotePort();
        String username = messageTicketRequest.username;
        String authenticator = messageTicketRequest.authenticator;
        Service service = messageTicketRequest.service;
        byte[] ticket = messageTicketRequest.ticket;

        String ticketGrantingPassword = SharedPassword.getASTGSKey();

        try {
            // validate ticket
            if(validateTicket(ticketGrantingPassword, ticket, username, authenticator, ipAddr)) {
                // check permissions
                if(UserPermissionDB.getInstance().isAllowed(username, service)) {
                    MessageServiceTicket messageServiceTicket = new MessageServiceTicket().createJSONTicket(username, ipAddr, service);
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().println(new Gson().toJson(messageServiceTicket));
                } else {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("{ \"status\": \"this user is not allowed\"}");
                }
            } else {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("{ \"status\": \"the ticket is not valid\"}");
            }

        } catch (Exception e) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{ \"status\": \"fail\"}");
        }
    }

    private boolean validateTicket(String ticketGrantingPassword, byte[] ticket, String username, String authenticator, String ipAddr) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        GrantingServerTicket grantingServerTicket = new Gson().fromJson(new String(CipherModule.decrypt(ticketGrantingPassword.toCharArray(), ticket)), GrantingServerTicket.class);
        if(grantingServerTicket.authenticator.equals(authenticator) && grantingServerTicket.username.equals(username) && grantingServerTicket.ipAddr.equals(ipAddr)) {
            return Long.parseLong(grantingServerTicket.timestamp) + Long.parseLong(grantingServerTicket.lifetime) > System.currentTimeMillis();
        } else {
            return false;
        }
    }
}