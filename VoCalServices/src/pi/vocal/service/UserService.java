package pi.vocal.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import pi.vocal.management.ErrorCode;
import pi.vocal.management.UserManagement;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.user.Grade;
import pi.vocal.user.Location;

@Path("/UserMgmt")
public class UserService {

	private static final Logger LOGGER = Logger.getLogger(UserService.class);
	
	@GET
	// @POST
	@Path("/createUser")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<ErrorCode>> createAccount(
			@QueryParam("firstname") String firstName,
			@QueryParam("lastname") String lastName,
			@QueryParam("email") String email,
			@QueryParam("password") String password,
			@QueryParam("grade") Grade grade,
			@QueryParam("location") Location location) {

		List<ErrorCode> errors = null;

		JsonResponse<List<ErrorCode>> response = new JsonResponse<>();
		response.setSuccess(true);

		try {
			UserManagement.createUser(firstName, lastName, email, grade,
					location, password);
		} catch (VocalServiceException e) {
			if (null != e.getCause()) {
				LOGGER.error("Nested Exception", e.getCause());
			}

			errors = e.getErrorCodes();
			response.setSuccess(false);
		}

		response.setContent(errors);

		return response;
	}

	@GET
	@Path("/editUser")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<ErrorCode>> editAccount() {		
		return null;
	}
	
//	@GET
//	// @POST
//	@Path("/getUserById")
//	@Produces(MediaType.APPLICATION_JSON)
//	public PublicUser getUserById(@QueryParam("id") long userId) {
//		return UserManagement.getUserById(userId);
//	}
//
//	@GET
//	// @POST
//	@Path("/getUserByEmail")
//	@Produces(MediaType.APPLICATION_JSON)
//	public PublicUser getUserByEmail(@QueryParam("email") String email) {
//		return UserManagement.getUserByEmail(email);
//	}

}
