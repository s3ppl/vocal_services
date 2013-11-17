package pi.vocal.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import pi.vocal.management.ErrorCode;
import pi.vocal.management.UserManagement;
import pi.vocal.management.exception.AccountCreationException;
import pi.vocal.service.dto.PublicUser;
import pi.vocal.user.Grade;
import pi.vocal.user.Location;

@Path("/UserMgmt")
public class UserService {
	
	public UserService() {
		System.out.println("foo");
	}

	// TODO change this method so that no PublicUser is required anymore - just pass the parameters to the UserManagement
	@GET
//	@POST
	@Path("/createUser")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<ErrorCode>> createAccount(@QueryParam("firstname") String firstName,
			@QueryParam("lastname") String lastName,
			@QueryParam("mail") String mail,
			@QueryParam("password") String password,
			@QueryParam("grade") Grade grade,
			@QueryParam("location") Location location) {
		
		List<ErrorCode> errors = new ArrayList<>();
		
		PublicUser user = new PublicUser();
		user.setEmail(mail);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setSchoolLocation(location);
		user.setGrade(grade);
		
		JsonResponse<List<ErrorCode>> response = new JsonResponse<>();
		response.setSuccess(1);
		
		try {
			UserManagement.createUser(user, password);
		} catch (AccountCreationException e) {
			errors.add(e.getErrorCode());
		}
		
		return response;
	}
	
	@GET
//	@POST
	@Path("/getUserById")
	@Produces(MediaType.APPLICATION_JSON)
	public PublicUser getUserById(@QueryParam("id") long userId) {
		return UserManagement.getUserById(userId);
	}

}
