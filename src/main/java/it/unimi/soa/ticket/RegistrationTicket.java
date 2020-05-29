package it.unimi.soa.ticket;

/**
 * This ticket contains the registration information of the user. This ticket is encrypted with the Registration server
 * public key and contains the session key which should be used by the server to encrypt the otp key in the response
 * message.
 */
public class RegistrationTicket {
    public String username;
    public String password;
    public String registrationSessionKey;

    public RegistrationTicket() {}

    public RegistrationTicket(String username, String password, String registrationSessionKey) {
        this.username = username;
        this.password = password;
        this.registrationSessionKey = registrationSessionKey;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRegistrationSessionKey() {
        return registrationSessionKey;
    }
}
