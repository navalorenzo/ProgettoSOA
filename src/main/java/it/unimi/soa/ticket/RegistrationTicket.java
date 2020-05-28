package it.unimi.soa.ticket;

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
