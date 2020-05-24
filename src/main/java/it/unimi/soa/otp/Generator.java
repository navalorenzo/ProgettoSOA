package it.unimi.soa.otp;

import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Generator {
    private TOTPConf conf;

    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HMAC_SHA1 = "HmacSHA1";

    public Generator(TOTPConf conf) {
        this.conf = conf;
    }

    public String generateNow() {

        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse("04/12/2018 13:24:20");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long secondsEpoch = date1.getTime() / 1000L;

        /*
        Date date = new Date();
        long secondsEpoch = (date.getTime()) / 1000L;
*/
        long period = this.getConf().getPeriod();

        long timeSlice = secondsEpoch / period;
        String paddedHex = Strings.padStart(Long.toHexString(timeSlice), 16, '0');

        byte[] m = DatatypeConverter.parseHexBinary(paddedHex);
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
        // Da qui in giu tutto ok
        //String a = "af16868fe5db00c15875f6a7f899f528ab805e9a";

        String a = bytesToHex(fullHmac);

        char last = a.charAt(a.length() - 1);
        int decimal = Integer.parseInt(String.valueOf(last), 16);
        byte[] splitted = DatatypeConverter.parseHexBinary(a);
        if (decimal < 4)
            decimal += 4;
        int[] got = {splitted[splitted.length - decimal] & 0x7f,
                splitted[splitted.length - decimal + 1] & 0xff,
                splitted[splitted.length - decimal + 2] & 0xff,
                splitted[splitted.length - decimal + 3] & 0xff};
        String ify = getIntToByteArray(got);
        long token = (long) (Integer.parseInt(ify, 16) % (Math.pow(10, this.getConf().getDigits())));

        System.out.println("Token:" + token);

        return String.valueOf(token);
        //return toHexString(mac.doFinal(time.getBytes()));
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

    public String getByteArray(byte[] bytes) {
        String out = "";
        for (byte b : bytes) {
            out += String.format("%02X", b);
        }
        return out;
    }

    public void printByteArray(byte[] bytes) {
        for (byte b : bytes)
            System.out.print(String.format("%02X ", b));
        System.out.println();
    }

    public String getIntToByteArray(int[] bytes) {
        String out = "";
        for (int i : bytes) {
            out += Integer.toHexString(i);
        }
        return out;
    }


    public void printIntToByteArray(int[] bytes) {
        for (int i : bytes)
            System.out.print(Integer.toHexString(i) + " ");
        System.out.println();
    }
}

