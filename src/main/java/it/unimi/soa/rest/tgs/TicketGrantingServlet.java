package it.unimi.soa.rest.tgs;

import com.google.gson.Gson;
import it.unimi.soa.message.tgs.MessageTGSRequest;
import it.unimi.soa.message.tgs.MessageTGSResponse;
import it.unimi.soa.rest.service.Service;
import it.unimi.soa.ticket.AuthenticatorTGSTicket;
import it.unimi.soa.ticket.TGSTicket;
import it.unimi.soa.utilities.CipherModule;
import it.unimi.soa.utilities.SharedPassword;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO
 */
public class TicketGrantingServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get info
        MessageTGSRequest messageTGSRequest = new Gson().fromJson(request.getReader().readLine(), MessageTGSRequest.class);

        // Get client info
        String ipAddr = request.getRemoteAddr() + request.getRemotePort();
        byte[] tgsEncryptedTicket = messageTGSRequest.getTgsEncryptedTicket();
        byte[] authenticatorEncryptedTicket = messageTGSRequest.getAuthenticatorEncryptedTicket();

        String ticketGrantingPassword = SharedPassword.getInstance().getASTGSKey();

        try {
            // Decrypt the ticket from the authentication server
            TGSTicket tgsTicket = new Gson().fromJson(
                    new String(CipherModule.decrypt(ticketGrantingPassword.toCharArray(), tgsEncryptedTicket)), TGSTicket.class);

            // Use the session key from the previous ticket to decrypt the authentication ticket
            AuthenticatorTGSTicket authenticatorTGSTicket = new Gson().fromJson(
                    new String(CipherModule.decrypt(tgsTicket.getClientTgsSessionKey().toCharArray(), authenticatorEncryptedTicket)), AuthenticatorTGSTicket.class);

            // Validate ticket
            if (validateTicket(tgsTicket, authenticatorTGSTicket, ipAddr)) {
                // Check permissions
                if (UserPermissionDB.getInstance().isAllowed(authenticatorTGSTicket.getUsername(), Service.valueOf(authenticatorTGSTicket.getService()))) {
                    MessageTGSResponse messageTGSResponse = new MessageTGSResponse().createJSONTicket(authenticatorTGSTicket.getUsername(), ipAddr, authenticatorTGSTicket.getService(), tgsTicket.getClientTgsSessionKey());
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().println(new Gson().toJson(messageTGSResponse));
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

    private boolean validateTicket(TGSTicket tgsTicket, AuthenticatorTGSTicket authenticatorTGSTicket, String ipAddr) {
        if (tgsTicket.getUsername().equals(authenticatorTGSTicket.getUsername()) &&
                tgsTicket.getIpAddr().equals(ipAddr) &&
                (tgsTicket.getOtp1().equals(authenticatorTGSTicket.getOtp()) || tgsTicket.getOtp2().equals(authenticatorTGSTicket.getOtp()))) {
            return tgsTicket.getTimestamp() + tgsTicket.getLifetime() > System.currentTimeMillis();
        } else {
            return false;
        }
    }
}