package it.unimi.soa.ticket;

public class OTPTicket {
    public String otpKey;

    public OTPTicket() {}

    public OTPTicket(String otpKey) {
        this.otpKey = otpKey;
    }

    public String getOtpKey() {
        return otpKey;
    }
}
