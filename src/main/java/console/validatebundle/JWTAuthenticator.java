package console.validatebundle;

import static java.math.BigDecimal.ONE;

import java.util.Optional;

import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;

import io.dropwizard.auth.Authenticator;

public class JWTAuthenticator implements Authenticator<JwtContext, MyUserPrincipal> {

    @Override
    public Optional<MyUserPrincipal> authenticate(JwtContext context) {
        // Provide your own implementation to lookup users based on the principal attribute in the
        // JWT Token. E.g.: lookup users from a database etc.
        // This method will be called once the token's signature has been verified

        // In case you want to verify different parts of the token you can do that here.
        // E.g.: Verifying that the provided token has not expired.

        // All JsonWebTokenExceptions will result in a 401 Unauthorized response.

        try {
            final String subject = context.getJwtClaims().getSubject();
            if ("good-guy".equals(subject)) {
                return Optional.of(new MyUserPrincipal(ONE, "good-guy"));
            }
            return Optional.empty();
        }
        catch (MalformedClaimException e) { return Optional.empty(); }
    }
}