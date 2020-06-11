package it.unimi.soa.message.registration;

import com.google.gson.Gson;
import it.unimi.soa.rest.service.Service;
import it.unimi.soa.ticket.RegistrationTicket;
import it.unimi.soa.utilities.CipherModule;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

/**
 * This message contains the registration info encrypted using the Registration server's public key. Only this server
 * can read the content.
 */
public class MessageRegistrationRequest {
    public byte[] registrationEncryptedTicket;

    public MessageRegistrationRequest() {}

    public MessageRegistrationRequest createJSONToken(String username, String password, String registrationSessionKey, ArrayList<Service> services)
            throws InvalidKeySpecException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchPaddingException, IOException {
        // Get user info and create the registration ticket
        RegistrationTicket registrationTicket = new RegistrationTicket(username, password, String.valueOf(registrationSessionKey), services);

        // encrypt the ticket using the registration server public key (the SharedPassword class mocks the PKI)
        PublicKey registrationPublicKey = CipherModule.readAsymmetricPublicKey("src/resources/public.der");
        registrationEncryptedTicket = CipherModule.encryptUsingPublicKey(registrationPublicKey, new Gson().toJson(registrationTicket).getBytes());

        return this;
    }

    public byte[] getRegistrationEncryptedTicket() {
        return registrationEncryptedTicket;
    }
}
