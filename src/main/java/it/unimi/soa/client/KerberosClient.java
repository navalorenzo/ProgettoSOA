package it.unimi.soa.client;


public class KerberosClient {
/*    public static void main(String[] args) {
        // init jersey it.unimi.soa.client

        // ask for authentication
        Scanner reader = new Scanner(System.in);
        System.out.print("Username: ");
        String username = reader.nextLine();
        System.out.print("Password: ");
        String password = reader.nextLine();

        // request auth
        System.out.println(requestAuth(username).authTicket);

    }

    /*private static MessageAuthToken requestAuth(String username) {
        String path = "/auth/login";
        String argument = new Gson().toJson(new MessageLogin(username));
        WebResource resource = it.unimi.soa.client.resource(SERVER_URL + ":" + SERVER_PORT + path);
        return new Gson().fromJson(resource.type("application/json").post(ClientResponse.class, argument).getEntity(String.class), MessageAuthToken.class);
    }*/
}
