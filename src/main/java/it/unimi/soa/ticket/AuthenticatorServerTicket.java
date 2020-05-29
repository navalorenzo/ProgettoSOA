package it.unimi.soa.ticket;

/**
 * This ticket contains the info for the Service server. The timestamp is used as a challenge to prove the server
 * identity.
 */
public class AuthenticatorServerTicket {
    public String username;
    public long timestamp;

    public AuthenticatorServerTicket() {}

    public AuthenticatorServerTicket(String username, long timestamp) {
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
