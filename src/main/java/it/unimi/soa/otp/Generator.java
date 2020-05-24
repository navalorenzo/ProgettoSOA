package it.unimi.soa.otp;

import com.google.common.base.Strings;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Generator {
    private TOTPConf conf;

    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HMAC_SHA1 = "HmacSHA1";

    public Generator(TOTPConf conf) {
        this.conf = conf;
    }

    public ArrayList<String> generateNow() {

        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse("04/12/2018 13:24:20");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long secondsEpoch = date1.getTime() / 1000L;
        System.out.println(secondsEpoch);
        /*Date date = new Date();
        long secondsEpoch = (date.getTime()) / 1000L;*/

        ArrayList<String> generated = new ArrayList<>();
        long period = this.getConf().getPeriod();
        for (long i = secondsEpoch - period; i < (secondsEpoch + period); i = i + 1) {
            long timeSlice = /*Math.floorDiv(i, period);*/ 51464208;
            //System.out.println(timeSlice);
            String paddedHex = Strings.padStart(Long.toHexString(timeSlice), 16, '0');
            byte[] m = DatatypeConverter.parseHexBinary(paddedHex);
            generated.add(calculateToken(m));
        }
        return generated;
    }

    public TOTPConf getConf() {
        return conf;
    }

    private String calculateToken(byte[] time) {
        Mac mac = null;
        SecretKeySpec secretKeySpec;
        String key = this.getConf().getSecret();
        String algo = null;
        if (this.getConf().getAlgorihtm() == "SHA1")
            algo = HMAC_SHA1;
        else if (this.getConf().getAlgorihtm() == "SHA256")
            algo = HMAC_SHA256;
        else if (this.getConf().getAlgorihtm() == "SHA512")
            algo = HMAC_SHA512;

        try {
            secretKeySpec = new SecretKeySpec(key.getBytes(), algo);
            mac = Mac.getInstance(algo);
            mac.init(secretKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Cannot calculate HMAC");
        }

        byte[] fullHmac = mac.doFinal(time);
        printByteArray(time);
        String a = bytesToHex(fullHmac);
        System.out.println(a);
        System.exit(1);

        //String a = "af16868fe5db00c15875f6a7f899f528ab805e9a";
        //System.out.println(a);
        char last = a.charAt(a.length() - 1);
        //System.out.println(last);
        int decimal = Integer.parseInt(String.valueOf(last), 16);
        //System.out.println(decimal);
        byte[] splitted = DatatypeConverter.parseHexBinary(a);
        //printByteArray(splitted);
        if (decimal < 4)
            decimal += 4;
        int[] got = {splitted[splitted.length - decimal] & 0x7f,
                splitted[splitted.length - decimal + 1] & 0xff,
                splitted[splitted.length - decimal + 2] & 0xff,
                splitted[splitted.length - decimal + 3] & 0xff};
        String ify = getIntToByteArray(got);
        long token = (long) (Integer.parseInt(ify, 16) % (Math.pow(10, this.getConf().getDigits())));
        System.out.println(token);
        /*
        String hex = a.substring(a.length() - decimal - 10, a.length() - decimal - 2);
        System.out.println(hex);
        BigInteger bi = new BigInteger(hex, 16);
        //System.out.println(bi);
        String token = bi.toString(10).substring(String.valueOf(bi).length() - this.getConf().getDigits());
        System.out.println(token);*/
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

