package console.validatebundle;

import java.util.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class ExampleAuthenticator implements Authenticator<BasicCredentials, BasicPrincipal> {
    @Override
    public Optional<BasicPrincipal> authenticate(BasicCredentials credentials) throws AuthenticationException {
    	
        if ("secret".equals(credentials.getPassword())) {
            return Optional.of(new BasicPrincipal());
        }
        return Optional.empty();
    }
}
