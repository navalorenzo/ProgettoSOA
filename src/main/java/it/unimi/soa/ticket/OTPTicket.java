package it.unimi.soa.ticket;

/**
 * This ticket contains the otp key which can be used by the client otp-generation application. This ticket is encrypted
 * using the session key between the Client and the Registration server.
 */
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
