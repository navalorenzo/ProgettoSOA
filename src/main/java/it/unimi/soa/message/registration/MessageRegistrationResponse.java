package it.unimi.soa.message.registration;

import com.google.gson.Gson;
import it.unimi.soa.ticket.OTPTicket;
import it.unimi.soa.utilities.CipherModule;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * This message contains the otp key (conf) for the user. The user can use the session key to decrypt the otp key.
 */
public class MessageRegistrationResponse {
    public byte[] otpEncryptedKey;

    public MessageRegistrationResponse() {}

    public MessageRegistrationResponse createJSONToken(String otpKey, String registrationSessionKey)
            throws InvalidKeySpecException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchPaddingException {
        // Create the ticket containing the OTP key

        OTPTicket registrationTicket = new OTPTicket(otpKey);

        // encrypt the ticket using the registration server public key (the SharedPassword class mocks the PKI)
        otpEncryptedKey = CipherModule.encrypt(registrationSessionKey.toCharArray(), new Gson().toJson(registrationTicket).getBytes());

        return this;
    }

    public byte[] getOtpEncryptedKey() {
        return otpEncryptedKey;
    }
}
