package pi.vocal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pi.vocal.management.AdminManagement;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.returncodes.ErrorCode;
import pi.vocal.persistence.dto.User;
import pi.vocal.service.dto.JsonResponse;
import pi.vocal.service.dto.PublicUser;
import pi.vocal.user.Role;

/**
 * This class contains all WebServices only an admin user can do.
 * 
 * @author s3ppl
 * 
 */

@Path("/AdminMgmt")
public class AdminService {

	/**
	 * This WebService return all {@code User} objects as a {@code List},
	 * wrapped in a {@code JsonResponse}. This service needs admin privileges to
	 * call.
	 * 
	 * @param sessionId
	 *            The sessionId of the user that tries to get all {@code User}s
	 * @return Returns a {@code JsonResponse} containing a {@code List} of all
	 *         {@code User}s
	 */
	@POST
	@Path("/getAllUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<?>> getAllUsers(@FormParam("sessionid") UUID sessionId) {

		JsonResponse<List<?>> response = new JsonResponse<>();
		response.setSuccess(true);

		try {
			List<PublicUser> users = new ArrayList<>();
			List<User> internalUsers = AdminManagement.getAllUsers(sessionId);

			for (User internalUser : internalUsers) {
				users.add(new PublicUser(internalUser));
			}

			response.setContent(users);
		} catch (VocalServiceException e) {
			response.setSuccess(false);
			response.setContent(e.getErrorCodes());
		}

		return response;
	}

	/**
	 * This WebService sets the {@code Role} of a specific user, identified
	 * through his userId. This services needs admin privileges to use.
	 * 
	 * @param sessionId
	 *            The sessionId of the calling user
	 * @param userId
	 *            The userId of the user, thats {@code Role} should be changed
	 * @param newRole
	 *            The new {@code Role} to set for the specific user
	 * @return Returns a {@code JsonResponse} containing a {@code List} of
	 *         {@code ErrorCode}s if an error occurred. If no error occurs, an
	 *         empty {@code List} will be returned
	 */
	@POST
	@Path("/setUserRole")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<ErrorCode>> setUserRole(@FormParam("sessionid") UUID sessionId,
			@FormParam("userid") long userId, @FormParam("role") Role newRole) {

		JsonResponse<List<ErrorCode>> response = new JsonResponse<>();
		response.setSuccess(true);

		try {
			AdminManagement.setUserRole(sessionId, userId, newRole);
		} catch (VocalServiceException e) {
			response.setSuccess(false);
			response.setContent(e.getErrorCodes());
		}

		return response;
	}

}
