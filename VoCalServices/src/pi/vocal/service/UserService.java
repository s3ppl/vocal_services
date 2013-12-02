package pi.vocal.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import pi.vocal.management.ErrorCode;
import pi.vocal.management.UserManagement;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.user.Grade;
import pi.vocal.user.SchoolLocation;

// TODO change QueryParam to FormParam for POST-Requests!
/**
 * 
 * @author s3ppl
 * 
 */
@Path("/UserMgmt")
public class UserService {
	private static final Logger LOGGER = Logger.getLogger(UserService.class);

	/**
	 * This webservice creates a new user account with the given information.
	 * 
	 * @param firstName
	 *            The firstname of the user
	 * @param lastName
	 *            The lastname of the user
	 * @param email
	 *            The email address of the user
	 * @param password
	 *            The password of the user
	 * @param grade
	 *            The grade of the user according to his basis belt color
	 * @param location
	 *            The location of the school, the user trains
	 * @return A list of {@code ErrorCode} which contains all errors, the users
	 *         input has, like no password.
	 */
	@POST
	@Path("/createUser")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<ErrorCode>> createAccount(
			@FormParam("firstname") String firstName,
			@FormParam("lastname") String lastName,
			@FormParam("email") String email,
			@FormParam("password") String password,
			@FormParam("grade") Grade grade,
			@FormParam("schoollocation") SchoolLocation location) {

		List<ErrorCode> errors = null;

		LOGGER.debug("Firstname Parameter: " + firstName);
		LOGGER.debug("Lastname Parameter: " + lastName);
		LOGGER.debug("Email Parameter: " + email);
		LOGGER.debug("Password Parameter: " + password);
		LOGGER.debug("Grade Parameter: " + grade);
		LOGGER.debug("Location Parameter: " + location);

		JsonResponse<List<ErrorCode>> response = new JsonResponse<>();
		response.setSuccess(true);

		try {
			UserManagement.createUser(firstName, lastName, email, grade,
					location, password);
		} catch (VocalServiceException e) {
			if (e.getErrorCodes().size() == 1
					&& e.getErrorCodes().get(0) == ErrorCode.INTERNAL_ERROR) {

				LOGGER.error(
						"An internal error occurred. See nested exceptions for details.",
						e.getCause());
			}

			errors = e.getErrorCodes();
			response.setSuccess(false);
		}

		response.setContent(errors);

		return response;
	}

	@POST
	@Path("/editUser")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<?> editAccount(@FormParam("sessionId") UUID sessionId,
			@FormParam("firstname") String firstName,
			@FormParam("lastname") String lastName,
			@FormParam("password") String password,
			@FormParam("schoollocation") SchoolLocation location) {

		try {
			JsonResponse<Map<String, Object>> response = new JsonResponse<>();
			Map<String, Object> result = null;

			result = UserManagement.editUser(sessionId, firstName, lastName,
					location);
			response.setSuccess(true);
			response.setContent(result);

			return response;
		} catch (VocalServiceException e) {
			JsonResponse<List<ErrorCode>> errorResponse = new JsonResponse<>();
			errorResponse.setSuccess(false);
			errorResponse.setContent(e.getErrorCodes());

			return errorResponse;
		}
	}

	@GET
	// @POST
	@Path("/deleteUser")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<?>> deleteAccount() {
		// TODO implement me!
		return null;
	}

}
