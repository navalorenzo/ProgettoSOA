package it.unimi.soa.otp;

public class Main {
    public static void main(String[] args) {

        // Generate a new random key
        String newSecret = Base32Secret.generate();

        // Setup a configuration
        TOTPConf conf = new TOTPConf(newSecret, "Label");
        conf.setAlgorihtm("SHA1");
        conf.setDigits(6);
        conf.setIssuer("Issuer");
        conf.setPeriod(30);

        // Get a QRCode
        QRCode qrcode = new QRCode(conf, 300);
        qrcode.show();

        // 2FA code
        Generator gen = new Generator(conf);
        gen.run();
    }
}
