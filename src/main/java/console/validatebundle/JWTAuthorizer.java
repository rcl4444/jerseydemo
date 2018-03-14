package console.validatebundle;

import io.dropwizard.auth.Authorizer;

public class JWTAuthorizer implements Authorizer<MyUserPrincipal> {

	@Override
	public boolean authorize(MyUserPrincipal principal, String role) {
		
		if("good-guy".equals(principal.getName())){
			return true;
		}
		return false;
	}
}