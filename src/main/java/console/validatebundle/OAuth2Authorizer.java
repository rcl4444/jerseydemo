package console.validatebundle;

import io.dropwizard.auth.Authorizer;

public class OAuth2Authorizer implements Authorizer<OAuthPrincipal> {

	@Override
	public boolean authorize(OAuthPrincipal principal, String role) {
		
		if("管理员".equals(principal.getName())){
			return true;
		}
		return false;
	}
}
