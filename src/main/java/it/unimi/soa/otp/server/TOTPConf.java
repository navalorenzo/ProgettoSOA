package it.unimi.soa.otp.server;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.ArrayList;


/**
 * A standard configuration of TOTP.
 * Generate the string configuration for the QRCode (client side) and is usedd by Generator (server side).
 */
public class TOTPConf {
    private String secret; //Secret key [REQUIRED]
    private String label; // Label [REQUIRED]
    private String issuer; //Name of issuer [OPTIONAL]
    private String algorihtm; //SHA1, SHA256, SHA512  [OPTIONAL]
    private int digits; //6 or 8 [OPTIONAL]
    private int period; //Seconds for a valid token [OPTIONAL]
    private final int BACKUP_CODES = 10;

    public TOTPConf(String secret, String label) {
        this.secret = secret;
        this.label = label;
        this.issuer = getLabel();
        this.algorihtm = "SHA1";
        this.digits = 3;
        this.period = 30;
    }

    /**
     * Should be sent to the client and used in an authenticator app.
     *
     * @return
     */
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

    public ArrayList<String> genBackupCodes() {
        ArrayList<String> tokens = new ArrayList<>();
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(this.getSecret().getBytes());
        for (int i = 0; i < BACKUP_CODES; i++) {
            String randomcode = String.valueOf(secureRandom.nextInt());
            tokens.add(DigestUtils.sha1Hex(randomcode));
        }
        return tokens;
    }
}
