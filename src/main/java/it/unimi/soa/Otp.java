package it.unimi.soa;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Otp {
    public static void main(String[]args) throws NoSuchAlgorithmException, InvalidKeyException {

        byte[] secret=new Base32().decode("pofarscduptmdh6jaoglnda2ryk77jva");
        long counter=51464208;
        // Signing Key
        SecretKeySpec signKey = new SecretKeySpec(secret, "HmacSHA1");

        // Preparing the byte array to sign
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(counter);

        // Signing the byte array
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(buffer.array());

        // Determines the offset of recovering the last byte (20-th) and then performing a bitwise and with
        // 00000000 00000000 00000000 00001111
        int offset = hash[19] & 0xF;

        // Requpero the first element starting from the offset and performing a bitwise and with
        // 00000000 00000000 00000000 00000000 00000000 00000000 00000000 01111111
        long truncatedHash = hash[offset] & 0x7F;

        for (int i = 1; i < 4; i++) {
            // Perform the shift left 8 bits
            truncatedHash <<= 8;

            // Get the next element of the sequence, and performing a bitwise and with
            // 00000000 00000000 00000000 00000000 00000000 00000000 00000000 11111111
            truncatedHash |= hash[offset + i] & 0xFF;
        }

        // Return the 6 digit least significant
       System.out.println(truncatedHash %= 1000000);
    }
}
