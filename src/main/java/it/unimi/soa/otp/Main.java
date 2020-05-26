package it.unimi.soa.otp;

import it.unimi.soa.otp.client.QRCode;
import it.unimi.soa.otp.server.GeneratorsDb;

public class Main {
    public static void main(String[] args) {

        String userid = "user1";

        String conf = GeneratorsDb.getInstance().register(userid);

        //Lato client
        QRCode qrcode = new QRCode(conf, 300);
        qrcode.show();
        GeneratorsDb.getInstance();
    }
}
