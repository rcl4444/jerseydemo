package console.validatebundle;

import io.dropwizard.auth.Authorizer;

public class ExampleAuthorizer implements Authorizer<BasicPrincipal> {
    @Override
    public boolean authorize(BasicPrincipal user, String role) {
    	
        return user.getName().equals("good-guy") && role.equals("ADMIN");
    }
}