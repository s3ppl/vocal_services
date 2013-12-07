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

import pi.vocal.management.ErrorCode;
import pi.vocal.management.SessionManagement;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.helper.ResultConstants;
import pi.vocal.persistence.dto.User;
import pi.vocal.service.dto.JsonResponse;
import pi.vocal.service.dto.PublicUser;

/**
 * This class is a WebService, that provides login and logout functions. All
 * functions need their parameters given within a HTTP POST request.
 * 
 * @author s3ppl
 * 
 */
@Path("/SessionMgmt")
public class SessionService {
	private static final Logger logger = Logger.getLogger(SessionService.class);

	/**
	 * Logs a {@code User} into the system and returns a {@code Map} containing
	 * the {@code PublicUser} object and a sessionId. In case an error occurs,
	 * the {@code Map} is replaced by a {@code List} of {@code ErrorCode}s.
	 * 
	 * @param email
	 *            The email of the {@code User} needed for the login
	 * @param password
	 *            The password of the {@code User} needed for the login
	 * @return Either a {@code Map} with the according {@code User} object and a
	 *         sessionId or a {@code List} of {@code ErrorCodes}
	 */
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<?> login(@FormParam("email") String email,
			@FormParam("password") String password) {

		JsonResponse<Map<Enum<ResultConstants>, Object>> response = new JsonResponse<>();
		response.setSuccess(true);

		try {
			Map<Enum<ResultConstants>, Object> result = SessionManagement
					.login(email, password);
			result.put(ResultConstants.LOGIN_USER_KEY, new PublicUser(
					(User) result.get(ResultConstants.LOGIN_USER_KEY)));
			response.setContent(result);
		} catch (Exception e) { // TODO keep as exception!?
			JsonResponse<List<ErrorCode>> errorResponse = new JsonResponse<>();
			errorResponse.setSuccess(false);

			if (e instanceof VocalServiceException) {
				errorResponse.setContent(((VocalServiceException) e)
						.getErrorCodes());
			} else {
				logger.error("Unexpected Exception!", e);
			}

			return errorResponse;
		}

		return response;
	}

	/**
	 * Logs a {@code User} out of the system and deletes his session.
	 * 
	 * @param id
	 *            The id of the session for the {@code User} to logout
	 */
	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public void logout(@FormParam("sessionid") UUID id) {
		SessionManagement.logout(id);
	}
}
