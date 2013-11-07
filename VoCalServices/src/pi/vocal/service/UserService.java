package pi.vocal.service;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import pi.vocal.management.UserManagement;
import pi.vocal.management.exception.AccountCreationException;
import pi.vocal.service.dto.PublicUser;
import pi.vocal.user.Grade;
import pi.vocal.user.Location;

@Path("/UserMgmt")
public class UserService {

	@GET
	@Path("/createUser")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<String> createAccount(@QueryParam("firstname") String firstName,
			@QueryParam("lastname") String lastName,
			@QueryParam("mail") String mail,
			@QueryParam("password") String password,
			@QueryParam("grade") Grade grade,
			@QueryParam("location") Location location) {
		
		PublicUser user = new PublicUser();
		user.setEmail(mail);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setSchoolLocation(location);
		
		JsonResponse<String> response = new JsonResponse<>();
		response.setSucess(1);
		
		try {
			UserManagement.createUser(user, password);
		} catch (AccountCreationException e) {
			response.setSucess(0);
		}
		
		return response;
	}
	
	@GET
	@Path("/getUserById")
	@Produces(MediaType.APPLICATION_JSON)
	public PublicUser getUserById(@QueryParam("id") long userId) {
		return UserManagement.getUserById(userId);
	}

}
