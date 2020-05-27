package it.unimi.soa.ticket;

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
