package it.unimi.soa.message.ticket;

public class EncryptedTicket {
    public byte[] encryptedTicket;

    public EncryptedTicket() {}

    // TODO: serve davvero questa classe???
    public EncryptedTicket(byte[] encryptedTicket) {
        this.encryptedTicket = encryptedTicket;
    }
}
