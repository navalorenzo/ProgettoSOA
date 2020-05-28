package it.unimi.soa.message.registration;

import com.google.gson.Gson;
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

public class MessageRegistrationRequest {
    public byte[] registrationEncryptedTicket;

    public MessageRegistrationRequest() {}

    public MessageRegistrationRequest createJSONToken(String username, String password, String registrationSessionKey)
            throws InvalidKeySpecException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchPaddingException, IOException {
        // Get user info and create the registration ticket
        RegistrationTicket registrationTicket = new RegistrationTicket(username, password, String.valueOf(registrationSessionKey));

        // encrypt the ticket using the registration server public key (the SharedPassword class mocks the PKI)
        PublicKey registrationPublicKey = CipherModule.readAsymmetricPublicKey("src/resources/public.der");
        registrationEncryptedTicket = CipherModule.encryptUsingPublicKey(registrationPublicKey, new Gson().toJson(registrationTicket).getBytes());

        return this;
    }

    public byte[] getRegistrationEncryptedTicket() {
        return registrationEncryptedTicket;
    }
}
