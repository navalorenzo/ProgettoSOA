package it.unimi.soa.otp;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.util.Date;

public class Generator {
    private TOTPConf conf;

    public Generator(TOTPConf conf) {
        this.conf = conf;
    }

    public String generateNow() {

        Date date = new Date();
        long millis = date.getTime();
        long secondsEpoch = millis / 1000L;

        System.out.println(millis);

        long period = this.getConf().getPeriod();
        long timeSlice = secondsEpoch / period;

        byte[] m = ByteBuffer.allocate(8).putLong(timeSlice).array();
        return calculateToken(m);
    }

    public TOTPConf getConf() {
        return conf;
    }

    private String calculateToken(byte[] time) {
        Mac mac = null;
        SecretKeySpec secretKeySpec;

        String key = this.getConf().getSecret();
        String algo = "Hmac" + this.getConf().getAlgorihtm();

        byte[] decoded = new Base32().decode(key);

        try {
            secretKeySpec = new SecretKeySpec(decoded, algo);
            mac = Mac.getInstance(algo);
            mac.init(secretKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Cannot calculate HMAC");
        }

        byte[] fullHmac = mac.doFinal(time);
        printByteArray(fullHmac);

        String a = bytesToHex(fullHmac);
        char last = a.charAt(a.length() - 1);
        int decimal = Integer.parseInt(String.valueOf(last), 16);
        byte[] splitted = DatatypeConverter.parseHexBinary(a);
        int[] got = {splitted[decimal] & 0x7f,
                splitted[decimal + 1] & 0xff,
                splitted[decimal + 2] & 0xff,
                splitted[decimal + 3] & 0xff};
        String ify = intArrayToByteString(got);
        long token = (long) (Integer.parseInt(ify, 16) % (Math.pow(10, this.getConf().getDigits())));

        return StringUtils.leftPad(String.valueOf(token), this.getConf().getDigits(), "0");
    }


    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public void printByteArray(byte[] bytes) {
        for (byte b : bytes)
            System.out.print(String.format("%02X ", b));
        System.out.println();
    }

    public String intArrayToByteString(int[] bytes) {
        String out = "";
        for (int i : bytes) {
            String hexval = Integer.toHexString(i);
            out += StringUtils.leftPad(hexval, 2, "0");
        }
        return out;
    }
}

