package it.unimi.soa.ticket;

public class OTPTicket {
    public String otpKey;

    // TODO: potrebbero servire anche i codici di backup, ma bisogna controllare prima

    public OTPTicket() {}

    public OTPTicket(String otpKey) {
        this.otpKey = otpKey;
    }

    public String getOtpKey() {
        return otpKey;
    }
}
