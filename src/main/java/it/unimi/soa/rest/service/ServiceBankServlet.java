package it.unimi.soa.rest.service;

import com.google.gson.Gson;
import it.unimi.soa.message.service.MessageServiceRequest;
import it.unimi.soa.message.service.MessageServiceResponse;
import it.unimi.soa.ticket.AuthenticatorServerTicket;
import it.unimi.soa.ticket.ServiceTicket;
import it.unimi.soa.utilities.CipherModule;
import it.unimi.soa.utilities.SharedPassword;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServiceBankServlet extends HttpServlet {
    private final Service service = Service.BANK;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MessageServiceRequest messageServiceRequest = new Gson().fromJson(
                request.getReader().readLine(), MessageServiceRequest.class);

        // Get client info
        String ipAddr = request.getRemoteAddr();
        byte[] serviceEncryptedTicket = messageServiceRequest.getServiceEncryptedTicket();
        byte[] clientServerEncryptedSessionKey = messageServiceRequest.getClientServerEncryptedSessionKey();

        String serviceServerPassword = SharedPassword.getInstance().getTGSSSKey(service.toString());

        try {
            // Decrypt the service ticket
            ServiceTicket serviceTicket = new Gson().fromJson(
                    new String(CipherModule.decrypt(
                            serviceServerPassword.toCharArray(), serviceEncryptedTicket)), ServiceTicket.class);

            // Decrypt the user authenticator ticket using the password contained in the service ticket
            AuthenticatorServerTicket authenticatorServerTicket = new Gson().fromJson(
                    new String(CipherModule.decrypt(
                            serviceTicket.getClientServerSessionKey().toCharArray(), clientServerEncryptedSessionKey)), AuthenticatorServerTicket.class);

            // Validate ticket
            if (validateTicket(serviceTicket, authenticatorServerTicket, ipAddr)) {
                MessageServiceResponse messageServiceResponse = new MessageServiceResponse().createJSONToken(
                        authenticatorServerTicket.getTimestamp(), "Welcome to the Bank!", serviceTicket.getClientServerSessionKey());
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(new Gson().toJson(messageServiceResponse));
            } else {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("{ \"status\": \"the ticket is not valid\"}");
            }
        } catch (Exception e) {
            System.err.println("ServiceHelloServlet error");
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{ \"status\": \"fail\"}");
        }
    }

    private boolean validateTicket(ServiceTicket serviceTicket, AuthenticatorServerTicket authenticatorServerTicket, String ipAddr) {
        if (serviceTicket.getIpAddr().equals(ipAddr) &&
                serviceTicket.getUsername().equals(authenticatorServerTicket.getUsername()) &&
                serviceTicket.getService().equals(service.toString())) {
            return serviceTicket.getTimestamp() + serviceTicket.getLifetime() > System.currentTimeMillis();
        }
        return false;
    }
}
