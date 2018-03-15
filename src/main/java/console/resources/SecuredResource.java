package console.resources;

import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import com.google.common.collect.ImmutableMap;

import console.validatebundle.MyUserPrincipal;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;

@Api(value="jwt授权码")
@Path("/jwt")
@Produces(APPLICATION_JSON)
public class SecuredResource {

    private final byte[] tokenSecret;
    
    public SecuredResource(byte[] tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    @GET
    @Path("/generate-expired-token")
    public Map<String, String> generateExpiredToken() throws JoseException {
        final JwtClaims claims = new JwtClaims();
        claims.setExpirationTimeMinutesInTheFuture(20);
        claims.setSubject("good-guy");

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(HMAC_SHA256);
        jws.setKey(new HmacKey(tokenSecret));

        try {
            return singletonMap("token", jws.getCompactSerialization());
        }
        catch (JoseException e) { throw e; }
    }

    @GET
    @Path("/generate-valid-token")
    public Map<String, String> generateValidToken() throws JoseException {
        final JwtClaims claims = new JwtClaims();
        claims.setSubject("good-guy");
        claims.setExpirationTimeMinutesInTheFuture(30);

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(HMAC_SHA256);
        jws.setKey(new HmacKey(tokenSecret));

        try {
            return singletonMap("token", jws.getCompactSerialization());
        }
        catch (JoseException e) { throw e; }
    }

    @GET
    @Path("/check-token")
	@RolesAllowed("")
    public Map<String, Object> get(@Auth MyUserPrincipal user) {
        return ImmutableMap.<String, Object>of("username", user.getName(), "id", user.getId());
    }
}