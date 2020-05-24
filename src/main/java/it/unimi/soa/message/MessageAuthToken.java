package it.unimi.soa.message;

public class MessageAuthToken {
    public String authTicket;

    public MessageAuthToken() {}

    public MessageAuthToken(String authTicket) {
        this.authTicket = authTicket;
    }
}
