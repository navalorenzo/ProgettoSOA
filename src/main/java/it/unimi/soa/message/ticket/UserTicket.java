package it.unimi.soa.message.ticket;

public class UserTicket {
    public String authenticator;
    public byte[] encryptedTicket;

    public UserTicket() {}

    public UserTicket(String authenticator, byte[] encryptedTicket) {
        this.authenticator = authenticator;
        this.encryptedTicket = encryptedTicket;
    }
}
