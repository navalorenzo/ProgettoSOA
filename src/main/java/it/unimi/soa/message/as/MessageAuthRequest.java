package it.unimi.soa.message.as;

/**
 * TODO
 */
public class MessageAuthRequest {
    public String username;

    public MessageAuthRequest() {
    }

    public MessageAuthRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
