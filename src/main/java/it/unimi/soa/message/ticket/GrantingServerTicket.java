package it.unimi.soa.message.ticket;

public class GrantingServerTicket {
    public String username;
    public String ipAddr;
    public String timestamp;
    public String lifetime;
    public String authenticator;

    public GrantingServerTicket() {}

    public GrantingServerTicket(String username, String ipAddr, String timestamp, String lifetime, String authenticator) {
        this.username = username;
        this.ipAddr = ipAddr;
        this.timestamp = timestamp;
        this.lifetime = lifetime;
        this.authenticator = authenticator;
    }
}
