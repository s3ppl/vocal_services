package pi.vocal.service.tests;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import pi.vocal.management.ErrorCode;
import pi.vocal.management.SessionManagement;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.persistence.dto.User;
import pi.vocal.service.JsonResponse;
import pi.vocal.service.dto.PublicUser;

@Path("/TestService")
public class TestService {
	
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
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<?> login(@QueryParam("email") String email, @QueryParam("password") String password) {
		JsonResponse<String> response = new JsonResponse<>();
		response.setSuccess(true);
		
		try {
			response.setContent(SessionManagement.login(email, password));
		} catch (VocalServiceException e) {
			JsonResponse<List<ErrorCode>> errorResponse = new JsonResponse<>();
			errorResponse.setSuccess(false);
			errorResponse.setContent(e.getErrorCodes());
			
			return errorResponse;
		}
		
		return response;		
	}
	
	
//	@GET
//	@Path("/getLocations")
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<String> getLocations() {
//		
//		List<String> locList = new ArrayList<String>();
//		locList.add("eins");
//		locList.add("zwei");
//		locList.add("drei");
//		locList.add("vier");
//		
//		return locList;
//	}
	
}
