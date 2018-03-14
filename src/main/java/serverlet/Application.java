package serverlet;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import serverlet.intercept.MyInterceptionBinder;

/**
 * @author Michal Gajdos
 */
@ApplicationPath("/")
public class Application extends ResourceConfig {

    public Application() {
        packages("serverlet");

        // Register Interception Service.
        // Comment if you want to register HK2 services via hk2-inhabitant-generator (don't forget to enable it in pom.xml).
        register(new MyInterceptionBinder());
    }
}
