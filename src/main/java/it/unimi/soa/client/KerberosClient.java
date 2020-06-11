package it.unimi.soa.client;

import com.google.gson.Gson;
import it.unimi.soa.message.as.MessageAuthRequest;
import it.unimi.soa.message.as.MessageAuthResponse;
import it.unimi.soa.message.registration.MessageRegistrationRequest;
import it.unimi.soa.message.registration.MessageRegistrationResponse;
import it.unimi.soa.message.service.MessageServiceRequest;
import it.unimi.soa.message.service.MessageServiceResponse;
import it.unimi.soa.message.tgs.MessageTGSRequest;
import it.unimi.soa.message.tgs.MessageTGSResponse;
import it.unimi.soa.otp.client.QRCode;
import it.unimi.soa.rest.service.Service;
import it.unimi.soa.ticket.AuthenticatorServerTicket;
import it.unimi.soa.ticket.OTPTicket;
import it.unimi.soa.utilities.CipherModule;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * This is the client
 */
public class KerberosClient {
    public static final String TARGET = "http://localhost:8090";

    private static String username = null;
    private static String password = null;

    public static void main(String[] args) {
        // Init client
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(TARGET);

        boolean validSelection = false;
        while (!validSelection) {
            Scanner reader = new Scanner(System.in);
            System.out.println("###############################");
            System.out.println("1) Register");
            System.out.println("2) Service");
            System.out.print("Please, choose an option: ");
            String mode = reader.nextLine();

            switch (mode) {
                case "1":
                    System.out.println("-----------------------");
                    register(target);
                    System.out.println("-----------------------");
                    break;
                case "2":
                    System.out.println("-----------------------");
                    login(target);
                    System.out.println("-----------------------");
                    break;
                default:
                    System.out.println("Invalid choice! Please, try again");
                    continue;
            }

            System.out.println("1) Continue");
            System.out.println("Any) Exit");
            System.out.print("Please, choose the option: ");
            mode = reader.nextLine();

            switch (mode) {
                case "1":
                    continue;
                default:
                    System.out.println("Goodbye! :)");
                    System.exit(0);
            }
        }

    }

    public static void register(WebTarget target) {
        // Ask for user informations
        Scanner reader = new Scanner(System.in);
        System.out.print("Username: ");
        String username = reader.nextLine();
        System.out.print("Password: ");
        String password = reader.nextLine();

        // Get auth for services
        System.out.print("Please, insert services (HELLO,BANK,...): ");
        List<String> stringServices = Arrays.asList(reader.nextLine().split(","));
        ArrayList<Service> services = new ArrayList<>();
        for(String stringService : stringServices)
            services.add(Service.valueOf(stringService));

        // Create the registration message
        try {
            // Generate the session key for the registration process
            long timestamp = System.currentTimeMillis();
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.setSeed(timestamp);
            long registrationSessionKey = secureRandom.nextLong();

            MessageRegistrationRequest messageRegistrationRequest = new MessageRegistrationRequest().createJSONToken(username, password, String.valueOf(registrationSessionKey), services);

            // Request registration
            MessageRegistrationResponse messageRegistrationResponse = requestRegistration(target, messageRegistrationRequest);

            // Get OTP key and show the QR code
            byte[] otpEncryptedKey = messageRegistrationResponse.getOtpEncryptedKey();
            OTPTicket otpTicket = new Gson().fromJson(new String(CipherModule.decrypt(String.valueOf(registrationSessionKey).toCharArray(), otpEncryptedKey)), OTPTicket.class);

            QRCode qrcode = new QRCode(otpTicket.getOtpKey(), 300);
            qrcode.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not encrypt the ticket!");
            return;
        }

    }

    public static void login(WebTarget target) {
        // Ask for authentication
        Scanner reader = new Scanner(System.in);

        if(username == null || password == null) {
            System.out.print("Username: ");
            username = reader.nextLine();
            System.out.print("Password: ");
            password = reader.nextLine();
        }

        System.out.println("Available services: " + Arrays.toString(Service.values()));
        System.out.print("Service: ");
        String service = reader.nextLine();

        // Request auth
        MessageAuthResponse messageAuthResponse = requestAuth(target, new MessageAuthRequest(username));

        // Extract the ticket and the session key
        byte[] tgsEncryptedTicket = messageAuthResponse.getTgsEncryptedTicket();
        byte[] clientTgsEncryptedSessionKey = messageAuthResponse.getClientTgsEncryptedSessionKey();

        // Decrypt the client-tgs session key in order to encrypt the communication with the tgs
        byte[] clientTgsSessionKey = new byte[0];
        try {
            clientTgsSessionKey = CipherModule.decrypt(password.toCharArray(), clientTgsEncryptedSessionKey);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not decrypt the ticket!");
            return;
        }

        System.out.println("Authentication: OK");

        // Get the OTP code from the user
        System.out.print("OTP: ");
        String otp = reader.nextLine();

        // Send server-ticket request to the ticket-granting server

        MessageTGSRequest messageTGSRequest = null;
        try {
            messageTGSRequest = new MessageTGSRequest().createJSONToken(username, otp, service, new String(clientTgsSessionKey), tgsEncryptedTicket);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not encrypt the ticket!");
            return;
        }

        MessageTGSResponse messageTGSResponse = requestTicket(target, messageTGSRequest);

        // Send the service request
        byte[] serviceEncryptedTicket = messageTGSResponse.getServiceEncryptedTicket();
        byte[] clientServerEncryptedSessionKey = messageTGSResponse.getClientServerEncryptedSessionKey();

        // Decrypt the client-tgs session key in order to encrypt the communication with the tgs
        byte[] clientServerSessionKey = new byte[0];
        try {
            clientServerSessionKey = CipherModule.decrypt(
                    new String(clientTgsSessionKey).toCharArray(), clientServerEncryptedSessionKey);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not decrypt the ticket!");
            return;
        }

        System.out.println("Ticket granting: OK");

        // Create the message for the service server
        MessageServiceRequest messageServiceRequest = null;
        try {
            messageServiceRequest = new MessageServiceRequest().createJSONToken(username, new String(clientServerSessionKey), serviceEncryptedTicket);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not encrypt the ticket!");
            return;
        }

        MessageServiceResponse messageServiceResponse = requestService(target, service, messageServiceRequest);

        // Check service response
        String receivedTimestamp;
        String previousTimestamp;
        try {
            receivedTimestamp = new String(CipherModule.decrypt(
                    new String(clientServerSessionKey).toCharArray(), messageServiceResponse.getTimestampEncryptedChallenge()));
            previousTimestamp = String.valueOf(
                    new Gson().fromJson(
                            new String(CipherModule.decrypt(
                                    new String(clientServerSessionKey).toCharArray(), messageServiceRequest.getClientServerEncryptedSessionKey())), AuthenticatorServerTicket.class).getTimestamp());
        } catch (Exception e) {
            System.err.println("Could not decrypt the ticket!");
            return;
        }

        System.out.println("Service validation: " + receivedTimestamp.equals(previousTimestamp));

        System.out.println("+++++++++++++++++++++++++");
        System.out.println("Message: " + messageServiceResponse.getWelcomeMessage());
        System.out.println("+++++++++++++++++++++++++");
    }

    private static MessageAuthResponse requestAuth(WebTarget target, MessageAuthRequest messageAuthRequest) {
        target = target.path("auth");
        String argument = new Gson().toJson(messageAuthRequest);

        Response response = target.request().post(Entity.entity(argument, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("The request failed: " + response.getStatus());
            System.out.println("Error: " + response.readEntity(String.class));
            System.exit(1);
        }

        return new Gson().fromJson(response.readEntity(String.class), MessageAuthResponse.class);
    }

    private static MessageTGSResponse requestTicket(WebTarget target, MessageTGSRequest messageTGSRequest) {
        target = target.path("ticket");
        String argument = new Gson().toJson(messageTGSRequest);

        Response response = target.request().post(Entity.entity(argument, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("The request failed: " + response.getStatus());
            System.out.println("Error: " + response.readEntity(String.class));
            System.exit(1);
        }

        return new Gson().fromJson(response.readEntity(String.class), MessageTGSResponse.class);
    }

    private static MessageServiceResponse requestService(WebTarget target, String service, MessageServiceRequest messageServiceRequest) {
        target = target.path("service/" + service.toLowerCase());
        String argument = new Gson().toJson(messageServiceRequest);

        Response response = target.request().post(Entity.entity(argument, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("The request failed: " + response.getStatus());
            System.out.println("Error: " + response.readEntity(String.class));
            System.exit(1);
        }

        return new Gson().fromJson(response.readEntity(String.class), MessageServiceResponse.class);
    }

    private static MessageRegistrationResponse requestRegistration(WebTarget target, MessageRegistrationRequest messageRegistrationRequest) {
        target = target.path("registration");
        String argument = new Gson().toJson(messageRegistrationRequest);

        Response response = target.request().post(Entity.entity(argument, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("The request failed: " + response.getStatus());
            System.out.println("Error: " + response.readEntity(String.class));
            System.exit(1);
        }
        return new Gson().fromJson(response.readEntity(String.class), MessageRegistrationResponse.class);
    }
}
