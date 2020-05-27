package it.unimi.soa.ticket;

public class AuthenticatorTGSTicket {
    public String username;
    public String otp;
    public String service;

    public AuthenticatorTGSTicket() {}

    public AuthenticatorTGSTicket(String username, String otp, String service) {
        this.username = username;
        this.otp = otp;
        this.service = service;
    }

    public String getUsername() {
        return username;
    }

    public String getOtp() {
        return otp;
    }

    public String getService() {
        return service;
    }
}
