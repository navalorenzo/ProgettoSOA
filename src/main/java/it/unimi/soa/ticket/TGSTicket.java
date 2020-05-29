package it.unimi.soa.ticket;

import it.unimi.soa.otp.server.GeneratorsDb;

import java.util.ArrayList;

/**
 * This ticket is built by the AS and is encrypted using the TGS key. Only the TGS can read this ticket and use the
 * the content to prove the client identity.
 */
public class TGSTicket {
    public String username;
    public String ipAddr;
    public long timestamp;
    public long lifetime;
    public String otp1;
    public String otp2;
    public String clientTgsSessionKey;

    public TGSTicket() {}

    public TGSTicket(String username, String ipAddr, long timestamp, long lifetime, String clientTgsSessionKey) {
        this.username = username;
        this.ipAddr = ipAddr;
        this.timestamp = timestamp;
        this.lifetime = lifetime;
        this.clientTgsSessionKey = clientTgsSessionKey;

        ArrayList<String> validTokens = GeneratorsDb.getInstance().generateTokenForUser(username);
        this.otp1 = validTokens.get(0);
        this.otp2 = validTokens.get(1);
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getLifetime() {
        return lifetime;
    }

    public String getOtp1() {
        return otp1;
    }

    public String getOtp2() {
        return otp2;
    }

    public String getClientTgsSessionKey() {
        return clientTgsSessionKey;
    }
}
