package it.unimi.soa.otp;

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
