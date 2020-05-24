package it.unimi.soa.otp;

public class Main {
    public static void main(String[] args) {

        String newSecret = Base32Secret.generate();
        System.out.println(newSecret);

        //Setup a configuration
        TOTPConf conf = new TOTPConf("gmcizgkc5qxtjh3g6p33d4iiuak3fmly", "Label");
        conf.setAlgorihtm("SHA1");
        conf.setDigits(6);
        conf.setIssuer("Issuer");
        conf.setPeriod(30);

        // Generation of QRCode
        QRCode qrcode = new QRCode(conf, 300);
        qrcode.show();

        //Generate the 2FA code
        Generator gen = new Generator(conf);
        String token;

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println();
            }
            token = gen.generateNow();
            System.out.println(token);
        }
    }
}
