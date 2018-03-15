package console.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.axonframework.commandhandling.gateway.CommandGateway;

import console.person.PersonCreateCommand;
import io.swagger.annotations.Api;

@Api(value="个人")
@Path("person")
public class PersonResource {

	private final CommandGateway Gw;

	@Inject
	public PersonResource(CommandGateway Gw) {
		this.Gw = Gw;
	}

	@GET
	@Path("create")
	public Response oauthApi() {
		this.Gw.sendAndWait(PersonCreateCommand.builder().name("1").age(1).build());
		return Response.ok().build();
	}
}