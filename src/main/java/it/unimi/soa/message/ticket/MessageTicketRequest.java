package it.unimi.soa.message.ticket;

public class MessageTicketRequest {
    public byte[] encryptedTicket;

    public MessageTicketRequest() {}

    public MessageTicketRequest(byte[] encryptedTicket) {
        this.encryptedTicket = encryptedTicket;
    }
}
