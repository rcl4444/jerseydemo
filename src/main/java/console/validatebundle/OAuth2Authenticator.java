package console.validatebundle;

import java.util.Optional;

import javax.inject.Inject;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class OAuth2Authenticator implements Authenticator<String,OAuthPrincipal> {
	
	@Inject
	public OAuth2Authenticator(){

	}

	public Optional<OAuthPrincipal> authenticate(String credentials) throws AuthenticationException {
		
		if("secret".equals(credentials)){
			OAuthPrincipal user = new OAuthPrincipal();
			user.setPersonName("王大棒");
			user.setUserName("管理员");
			return Optional.of(user);
		}
		return Optional.empty();
	}
}
