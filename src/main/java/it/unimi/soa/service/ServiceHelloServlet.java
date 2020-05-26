package it.unimi.soa.service;

import com.google.gson.Gson;
import it.unimi.soa.message.service.GrantingServiceTicket;
import it.unimi.soa.message.ticket.MessageServiceTicket;
import it.unimi.soa.utilities.CipherModule;
import it.unimi.soa.utilities.SharedPassword;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServiceHelloServlet extends HttpServlet {
    private final Service service = Service.HELLO;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MessageServiceTicket messageServiceTicket = new Gson().fromJson(request.getReader().readLine(), MessageServiceTicket.class);

        // get client info
        String ipAddr = request.getRemoteAddr() + request.getRemotePort();
        String username = messageServiceTicket.username;
        byte[] serviceTicket = messageServiceTicket.serviceTicket;

        String serviceServerPassword = SharedPassword.getInstance().getTGSSSKey(service.toString());

        try {
            // decrypt message and validate
            byte[] decryptedTicket = CipherModule.decrypt(serviceServerPassword.toCharArray(), messageServiceTicket.serviceTicket);
            GrantingServiceTicket grantingServiceTicket = new Gson().fromJson(new String(decryptedTicket), GrantingServiceTicket.class);

            // validate ticket
            if (validateTicket(grantingServiceTicket, ipAddr, username)) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(String.format("{ \"status\": \"ok %s, you are logged in %s service\"}", username, service));
            } else {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("{ \"status\": \"the ticket is not valid\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{ \"status\": \"fail\"}");
        }
    }

    private boolean validateTicket(GrantingServiceTicket grantingServiceTicket, String ipAddr, String username) {
        if (grantingServiceTicket.ipAddr.equals(ipAddr) && grantingServiceTicket.username.equals(username) && grantingServiceTicket.service.equals(Service.HELLO)) {
            return grantingServiceTicket.timestamp + grantingServiceTicket.lifetime > System.currentTimeMillis();
        }
        return false;
    }
}
