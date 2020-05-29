package it.unimi.soa.message.as;

/**
 * This message is just a wrapper, it contains the username. This is the step #1.
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
