package it.unimi.soa.message.service;

import it.unimi.soa.service.Service;

public class GrantingServiceTicket {
    public String username;
    public String ipAddr;
    public Service service;
    public long timestamp;
    public long lifetime;

    public GrantingServiceTicket() {}

    public GrantingServiceTicket(String username, String ipAddr, Service service, long timestamp, long lifetime) {
        this.username = username;
        this.ipAddr = ipAddr;
        this.service = service;
        this.timestamp = timestamp;
        this.lifetime = lifetime;
    }
}
