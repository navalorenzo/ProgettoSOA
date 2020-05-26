package it.unimi.soa.otp.server;

import org.apache.commons.codec.binary.Base32;

import java.util.Random;

public class Base32Secret {
    private static final int size = 20;

    public static String generate() {
        String key;
        Random rand = new Random(System.currentTimeMillis());

        byte[] data = new byte[size];
        rand.nextBytes(data);

        key = new String(new Base32().encode(data));

        return key;
    }
}
