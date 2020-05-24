
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Server {

    public static void main(String[] args) {

        URI baseUri = UriBuilder.fromUri("http://127.0.0.1/").port(8050).build();
        ResourceConfig config = new ResourceConfig(RestController.class);
        org.eclipse.jetty.server.Server server = JettyHttpContainerFactory.createServer(baseUri, config);
    }
}