package console.resources;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.dhatim.dropwizard.jwt.cookie.authentication.DefaultJwtCookiePrincipal;
import org.dhatim.dropwizard.jwt.cookie.authentication.DontRefreshSession;
import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookiePrincipal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import console.validatebundle.BasicPrincipal;
import console.validatebundle.OAuthPrincipal;
import io.dropwizard.auth.Auth;

/** 授权api
 * */
@Path("test")
public class ActionsResource {

	ObjectMapper mapper = new ObjectMapper(); 
	
	@POST
	@Path("oauth")
	@RolesAllowed("")
	public Response oauthApi(@Auth OAuthPrincipal user) throws JsonProcessingException{
		
		if(StringUtils.isEmpty(user.getName())){
			return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.ok(mapper.writeValueAsString(user)).build();
	}
	
	@POST
	@Path("base")
	public Response baseApi(@Auth BasicPrincipal user) throws JsonProcessingException{
		
		if(StringUtils.isEmpty(user.getAddress())){
			return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.ok(mapper.writeValueAsString(user)).build();
	}
	
	@POST
	@Path("cookielogin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public DefaultJwtCookiePrincipal login(@Context ContainerRequestContext requestContext, String name){
	    DefaultJwtCookiePrincipal principal = new DefaultJwtCookiePrincipal(name);
	    principal.addInContext(requestContext);
	    return principal;
	}

	@POST
	@Path("cookielogout")
	public void logout(@Context ContainerRequestContext requestContext){
	    JwtCookiePrincipal.removeFromContext(requestContext);
	}

	@POST
	@Path("getPrincipal")
	@Produces(MediaType.APPLICATION_JSON)
	public DefaultJwtCookiePrincipal getPrincipal(@Auth DefaultJwtCookiePrincipal principal){
	    return principal;
	}

	@POST
	@Path("idempotent")
	@Produces(MediaType.APPLICATION_JSON)
	@DontRefreshSession
	public DefaultJwtCookiePrincipal getSubjectWithoutRefreshingSession(@Auth DefaultJwtCookiePrincipal principal){
	    return principal;
	}

	@POST
	@Path("restricted")
	@RolesAllowed("admin")
	public String getRestrisctedResource(){
	    return "SuperSecretStuff";
	}
}
