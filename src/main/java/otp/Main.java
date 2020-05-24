package otp;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        //Setup a configuration
        TOTPConf conf = new TOTPConf("pofarsdcuptmdh6jaoglnda2ryk77jva", "label");
        conf.setAlgorihtm("SHA1");
        conf.setDigits(6);
        conf.setIssuer("Issuer");
        conf.setPeriod(30);

        System.out.println(conf.toString());

        //generate the main.java.totp.QRCode
        QRCode qrcode = new QRCode(conf, 300);
        //qrcode.show();

        //Generate the 2FA code
        Generator gen = new Generator(conf);
        ArrayList<String> list = gen.generateNow();
        for (String s : list) {
            // System.out.println(s);
        }
    }
}
