package pi.vocal.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import pi.vocal.management.UserManagement;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.helper.ResultConstants;
import pi.vocal.management.returncodes.ErrorCode;
import pi.vocal.management.returncodes.SuccessCode;
import pi.vocal.persistence.dto.User;
import pi.vocal.service.dto.JsonResponse;
import pi.vocal.service.dto.PublicUser;
import pi.vocal.user.Grade;
import pi.vocal.user.SchoolLocation;

/**
 * WebService that contains all user relevant methods such as creating and
 * editing.
 * 
 * @author s3ppl
 * 
 */
@Path("/UserMgmt")
public class UserService {
	private static final Logger LOGGER = Logger.getLogger(UserService.class);

	/**
	 * This WebService creates a new user account with the given information.
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
	 *         input had, like no password.
	 */
	@POST
	@Path("/createUser")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<ErrorCode>> createAccount(@FormParam("firstname") String firstName,
			@FormParam("lastname") String lastName, @FormParam("email") String email,
			@FormParam("password") String password, @FormParam("grade") Grade grade,
			@FormParam("schoollocation") SchoolLocation location) {

		List<ErrorCode> errors = null;

		JsonResponse<List<ErrorCode>> response = new JsonResponse<>();
		response.setSuccess(true);

		try {
			UserManagement.createUser(firstName, lastName, email, grade, location, password);
		} catch (VocalServiceException e) {
			if (e.getErrorCodes().size() == 1 && e.getErrorCodes().get(0) == ErrorCode.INTERNAL_ERROR) {

				LOGGER.error("An internal error occurred. See nested exceptions for details.", e.getCause());
			}

			errors = e.getErrorCodes();
			response.setSuccess(false);
		}

		response.setContent(errors);

		return response;
	}

	/**
	 * This WebService changes the fields of an existing {@code User} in the
	 * database. Afterwards the changed {@code User} object will be returned
	 * within a {@code Map} that is wrapped within a {@code JsonResponse}. Also
	 * contained in this {@code Map} is a {@code List} of {@code SuccessCode}s
	 * according to the changes done.
	 * 
	 * In case an error occured, a {@code List} of {@code ErrorCode}s will be
	 * returned within the {@code JsonResponse} instead of the {@code Map}.
	 * 
	 * @param sessionId
	 *            The sessionId of the {@code User} to change
	 * @param firstName
	 *            The new firstname of the {@code User}. May be null
	 * @param lastName
	 *            The new lastname of the {@code User}. May be null
	 * @param location
	 *            The new location of the {@code User}. May be null
	 * @return
	 */
	@POST
	@Path("/editUser")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<?> editAccount(@FormParam("sessionid") UUID sessionId,
			@FormParam("firstname") String firstName, @FormParam("lastname") String lastName,
			@FormParam("schoollocation") SchoolLocation location) {

		try {
			JsonResponse<Map<Enum<ResultConstants>, Object>> response = new JsonResponse<>();
			Map<Enum<ResultConstants>, Object> result = null;

			result = UserManagement.editUser(sessionId, firstName, lastName, location);

			// replace the persistent user object with a PublicUser
			result.put(ResultConstants.EDITUSER_USER_KEY,
					new PublicUser((User) result.get(ResultConstants.EDITUSER_USER_KEY)));

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

	/**
	 * Changes the password of an existing {@code User}.
	 * 
	 * If the password change is done successfully an according
	 * {@code SuccessCode} is returned within a {@code JsonResponse}. Otherwise
	 * the {@code JsonResponse} contains a {@code List} of {@code ErrorCode}s
	 * that provide information about what went wrong.
	 * 
	 * @param sessionId
	 *            The sessionId of the {@code User} thats password should get
	 *            changed
	 * @param oldPassword
	 *            The current password of the {@code User}
	 * @param newPassword1
	 *            The new password of the {@code User}
	 * @param newPassword2
	 *            The new password of the {@code User} to detect typos
	 * @return Either a {@code JsonResponse} containing an {@code SuccessCode}
	 *         if the change worked or a {@code List} of {@code ErrorCode}s
	 *         containg information about what went wrong
	 */
	@POST
	@Path("/changeUserPassword")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<?> changePassword(@FormParam("sessionid") UUID sessionId,
			@FormParam("oldpassword") String oldPassword, @FormParam("newpassword1") String newPassword1,
			@FormParam("newpassword2") String newPassword2) {

		try {
			JsonResponse<SuccessCode> response = new JsonResponse<>();
			response.setSuccess(true);
			response.setContent(UserManagement.changePassword(sessionId, oldPassword, newPassword1, newPassword2));

			return response;
		} catch (VocalServiceException e) {
			JsonResponse<List<ErrorCode>> errorResponse = new JsonResponse<>();
			errorResponse.setContent(e.getErrorCodes());
			errorResponse.setSuccess(false);
			return errorResponse;
		}
	}

	/**
	 * Sets the attendance of a {@code User} at a given {@code Event}.
	 * 
	 * @param sessionId
	 *            The sessionId of the {@code User} that wants to change is
	 *            {@code Event} attendance
	 * @param eventId
	 *            The id of the {@code Event} the {@code User} wants to change
	 *            his attendance for
	 * @param attends
	 *            Either {@code true} if the {@code User} wants to attend or
	 *            {@code false} if not
	 * @return Returns a {@code List} of {@code ErrorCode} if anything went
	 *         wrong while trying to change the attendance of the {@code User}
	 */
	@POST
	@Path("/setEventAttendance")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<ErrorCode>> attendEvent(@FormParam("sessionid") UUID sessionId,
			@FormParam("eventid") long eventId, @FormParam("attends") boolean attends) {

		JsonResponse<List<ErrorCode>> response = new JsonResponse<>();
		response.setSuccess(true);

		try {
			UserManagement.setEventAttendance(sessionId, eventId, attends);
		} catch (VocalServiceException e) {
			response.setSuccess(false);
			response.setContent(e.getErrorCodes());
		}

		return response;
	}

}
