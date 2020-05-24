package it.unimi.soa.otp;

public class Main {
    public static void main(String[] args) {

        //Setup a configuration
        TOTPConf conf = new TOTPConf("pofarscduptmdh6jaoglnda2ryk77jva", "label");


        //TOTPConf conf = new TOTPConf("pofarscduptmdh6jaoglnda2ryk77jva", "label");


        conf.setAlgorihtm("SHA1");
        conf.setDigits(6);
        conf.setIssuer("Issuer");
        conf.setPeriod(30);

        QRCode qrcode = new QRCode(conf, 300);
        //qrcode.show();

        //Generate the 2FA code
        Generator gen = new Generator(conf);
        String token = gen.generateNow();

    }
}
