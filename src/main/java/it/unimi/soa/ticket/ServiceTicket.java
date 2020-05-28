package it.unimi.soa.ticket;

public class ServiceTicket {
    public String username;
    public String ipAddr;
    public String service;
    public long timestamp;
    public long lifetime;
    public String clientServerSessionKey;

    public ServiceTicket() {}

    public ServiceTicket(String username, String ipAddr, String service, long timestamp, long lifetime, String clientServerSessionKey) {
        this.username = username;
        this.ipAddr = ipAddr;
        this.service = service;
        this.timestamp = timestamp;
        this.lifetime = lifetime;
        this.clientServerSessionKey = clientServerSessionKey;
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public String getService() {
        return service;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getLifetime() {
        return lifetime;
    }

    public String getClientServerSessionKey() {
        return clientServerSessionKey;
    }
}
