package it.unimi.soa.message.tgs;

import com.google.gson.Gson;
import it.unimi.soa.ticket.AuthenticatorTGSTicket;
import it.unimi.soa.utilities.CipherModule;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * TODO
 */
public class MessageTGSRequest {
    public byte[] tgsEncryptedTicket;
    public byte[] authenticatorEncryptedTicket;

    public MessageTGSRequest() {
    }

    public MessageTGSRequest createJSONToken(String username, String otp, String service, String clientTgsSessionKey, byte[] tgsEncryptedTicket)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException,
            InvalidKeySpecException {
        // Attach the TGS encrypted ticket
        this.tgsEncryptedTicket = tgsEncryptedTicket;

        // Create authenticator for the ticket-granting server
        AuthenticatorTGSTicket authenticatorTGSTicket = new AuthenticatorTGSTicket(username, otp, service);

        // Encrypt for the ticket-granting server
        authenticatorEncryptedTicket = CipherModule.encrypt(clientTgsSessionKey.toCharArray(), new Gson().toJson(authenticatorTGSTicket).getBytes());

        return this;
    }

    public byte[] getTgsEncryptedTicket() {
        return tgsEncryptedTicket;
    }

    public byte[] getAuthenticatorEncryptedTicket() {
        return authenticatorEncryptedTicket;
    }
}
