package pi.vocal.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import pi.vocal.service.dto.PublicUser;
import pi.vocal.user.Grade;

@Path("/UserMgmt")
public class UserService {

	@POST
	@Path("/createUser")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean createAccount(@QueryParam("firstname") String firstName,
			@QueryParam("lastname") String lastName,
			@QueryParam("mail") String mail,
			@QueryParam("password") String password,
			@QueryParam("grade") Grade grade) {
		
		return false;
	}
	
	@POST
	@Path("/getUserById")
	@Produces(MediaType.APPLICATION_JSON)
	public PublicUser getUserById(long userId) {
		
		return null;
	}

}
