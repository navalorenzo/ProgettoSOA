package main.java.otp;

public class TOTPConf {
    private String secret; //Secret key [REQUIRED]
    private String label; // Label [REQUIRED]
    private String issuer; //Name of issuer [OPTIONAL]
    private String algorihtm; //SHA1, SHA256, SHA512  [OPTIONAL]
    private int digits; //6 or 8 [OPTIONAL]
    private int period; //Seconds for a valid token [OPTIONAL]

    public TOTPConf(String secret, String label) {
        this.secret = secret;
        this.label = label;
        this.issuer = getLabel();
        this.algorihtm = "SHA1";
        this.digits = 3;
        this.period = 30;
    }


    @Override
    public String toString() {
        String code = "otpauth://totp/" + this.getLabel() +
                "?secret=" + this.getSecret() +
                "&issuer=" + this.getIssuer() +
                "&algorithm=" + this.getAlgorihtm() +
                "&digits=" + this.getDigits() +
                "&period=" + this.getPeriod();
        return code;
    }


    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setAlgorihtm(String algorihtm) {
        this.algorihtm = algorihtm;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getSecret() {
        return secret;
    }

    public String getAlgorihtm() {
        return algorihtm;
    }

    public int getDigits() {
        return digits;
    }

    public int getPeriod() {
        return period;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getLabel() {
        return label;
    }
}
