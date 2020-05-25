package it.unimi.soa.message.ticket;

import it.unimi.soa.service.Service;

public class MessageTicketRequest {
    public String username;
    public String authenticator;
    public Service service;
    public byte[] ticket;

    public MessageTicketRequest() {}

    public MessageTicketRequest(String username, String authenticator, Service service, byte[] ticket) {
        this.username = username;
        this.authenticator = authenticator;
        this.service = service;
        this.ticket = ticket;
    }
}
