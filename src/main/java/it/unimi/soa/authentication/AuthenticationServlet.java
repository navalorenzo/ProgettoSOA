package it.unimi.soa.authentication;

import com.google.gson.Gson;
import it.unimi.soa.message.auth.MessageAuthRequest;
import it.unimi.soa.message.auth.MessageAuthToken;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MessageAuthRequest messageAuthRequest = new Gson().fromJson(request.getReader().readLine(), MessageAuthRequest.class);

        // get client info
        String ipAddr = request.getRemoteAddr() + request.getRemotePort();
        String userPassword = UserDB.getInstance().getPassword(messageAuthRequest.username);

        // TODO: PLACE THIS IN A VAULT
        String ticketGrantingPassword = "paperino";

        // create response packet
        try {
            MessageAuthToken messageAuthToken = new MessageAuthToken().createJSONToken(messageAuthRequest.username, ipAddr, ticketGrantingPassword, userPassword);
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(new Gson().toJson(messageAuthToken));
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{ \"status\": \"fail\"}");
        }
    }
}