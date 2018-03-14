package console.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 格式化输出
 * */
public class DateNotSpecifiedServletFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		responseContext.getEntityAnnotations();
		if (JsonValue.class.isAssignableFrom(responseContext.getEntityClass())) {
			JsonValue entity = (JsonValue) responseContext.getEntity();
			Map<String, Object> json = new HashMap<String, Object>();
			json.put("status", responseContext.getStatus());
			json.put("response", (JsonValue) entity);
			responseContext.setStatus(Response.Status.OK.getStatusCode());
			responseContext.setEntity(json);
		}
	}
}