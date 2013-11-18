package pi.vocal.service.tests;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import pi.vocal.persistence.dto.User;
import pi.vocal.service.JsonResponse;
import pi.vocal.service.dto.PublicUser;

@Path("/TestService")
public class TestService {
	
	@Context
	HttpServletRequest request;
	
	@GET
	@Path("/getUser")
	@Produces(MediaType.APPLICATION_JSON)
	public User getTrackInJSON(@QueryParam("test") String test) {
		User track = new User();
		track.setFirstName("Enter Sandman");
		track.setLastName("Metallica");
		
		track.setEmail(test);
 
		return track;
	}
	
	@GET
	@Path("/getUserResponse")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<?> getUser() {
		
		PublicUser user = new PublicUser();
		user.setEmail("foo@bar.de");
 
		JsonResponse<List<PublicUser>> response = new JsonResponse<>();
		response.setContent(Arrays.asList(user));
		response.setSuccess(true);
		
		return response;
	}
	
	@GET
	@Path("/getRequest")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRequest() throws UnsupportedEncodingException {
		request.getSession().setAttribute("foo", "bar");
		System.out.println(request.getSession().getId().length());
			
		return request.getSession().getId();
	}
	
}
