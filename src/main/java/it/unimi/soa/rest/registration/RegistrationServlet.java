package it.unimi.soa.rest.registration;

import com.google.gson.Gson;
import it.unimi.soa.message.registration.MessageRegistrationRequest;
import it.unimi.soa.message.registration.MessageRegistrationResponse;
import it.unimi.soa.otp.server.GeneratorsDb;
import it.unimi.soa.rest.as.UserDB;
import it.unimi.soa.rest.service.Service;
import it.unimi.soa.rest.tgs.UserPermissionDB;
import it.unimi.soa.ticket.RegistrationTicket;
import it.unimi.soa.utilities.CipherModule;
import it.unimi.soa.utilities.SharedPassword;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.*;

public class RegistrationServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MessageRegistrationRequest messageRegistrationRequest = new Gson().fromJson(request.getReader().readLine(), MessageRegistrationRequest.class);

        // Get client info
        byte[] registrationEncryptedTicket = messageRegistrationRequest.getRegistrationEncryptedTicket();

        // Get private key
        RegistrationTicket registrationTicket = null;
        try {
            PrivateKey registrationPrivateKey = SharedPassword.getInstance().getRegistrationPrivateKey();
            // Decrypt the message
            registrationTicket = new Gson().fromJson(new String(CipherModule.decryptUsingPrivateKey(registrationPrivateKey, registrationEncryptedTicket)), RegistrationTicket.class);
        } catch (Exception e) {
            System.err.println("Could not decypt the message");
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{ \"status\": \"fail\"}");
        }

        try {
            // Get user info from the received packet
            String username = registrationTicket.getUsername();
            String password = registrationTicket.getPassword();
            String registrationSessionKey = registrationTicket.getRegistrationSessionKey();
            ArrayList<Service> services = registrationTicket.getServices();

            // Register the user
            UserDB.getInstance().register(username, password);
            String conf = GeneratorsDb.getInstance().register(username);

            // Add permissions for user
            for(Service service : services) {
                if(asList(Service.values()).contains(service)) {
                    UserPermissionDB.getInstance().addPermission(username, service);
                }
            }

            // Create response packet
            MessageRegistrationResponse messageRegistrationResponse = new MessageRegistrationResponse().createJSONToken(conf, registrationSessionKey);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(new Gson().toJson(messageRegistrationResponse));
        } catch (Exception e) {
            System.err.println("No user found");
            e.printStackTrace();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{ \"status\": \"fail\"}");
        }
    }
}

