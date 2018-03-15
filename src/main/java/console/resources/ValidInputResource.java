package console.resources;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotEmpty;

import console.dto.InputDto;
import console.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "webapi输入参数校验")
@Path("/valid")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ValidInputResource {

	final UserRepository repository;
	final String uniqueStr;

	@Inject
	public ValidInputResource(UserRepository repository) {
		this.repository = repository;
		this.uniqueStr = UUID.randomUUID().toString();
	}

	@Context
	HttpServletRequest request;

	@ApiOperation("测试di")
	@GET
	@Path("/usercount")
	public Response userCount() throws UnsupportedEncodingException {
		return Response.ok().entity(String.format("(%s)%s:%s", this.uniqueStr, this.repository.getUniqueFlag(),
				this.repository.countUser())).type(MediaType.TEXT_HTML).build();
	}

	@ApiOperation("输入参数校验1")
	@POST
	@Path("/input/{id}")
	public Response input(@PathParam("id") Integer id, @Valid InputDto dto) {

		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return Response.ok().entity(repository.countUser()).build();
	}

	@ApiOperation("输入参数校验2")
	@GET
	@Path("/validate")
	public Response validateStudent(
			@NotEmpty @Size(min = 2, max = 25, message = "firstName Length should be between 2 and 25") @QueryParam("firstName") String firstName,
			@NotEmpty @Size(min = 2, max = 25, message = "lastName Length should be between 2 and 25") @QueryParam("lastName") String lastName,
			@NotEmpty @Min(value = 15, message = "age should not be less that 15") @QueryParam("age") String age)
			throws ValidationException {

		return Response.ok().build();
	}
}