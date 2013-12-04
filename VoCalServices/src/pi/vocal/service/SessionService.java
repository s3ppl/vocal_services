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
import pi.vocal.service.dto.PublicUser;

@Path("/SessionMgmt")
public class SessionService {
	private static final Logger log = Logger.getLogger(SessionService.class);

	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<?> login(@FormParam("email") String email,
			@FormParam("password") String password) {

		log.debug("email: " + email);
		log.debug("password: " + password);

		JsonResponse<Map<Enum<ResultConstants>, Object>> response = new JsonResponse<>();
		response.setSuccess(true);

		try {
			Map<Enum<ResultConstants>, Object> result = SessionManagement
					.login(email, password);
			result.put(ResultConstants.LOGIN_USER_KEY, new PublicUser(
					(User) result.get(ResultConstants.LOGIN_USER_KEY)));
			response.setContent(result);
		} catch (Exception e) { // TODO keep as exception!?
			// if (e.getErrorCodes().size() == 1
			// && e.getErrorCodes().get(0) == ErrorCode.INTERNAL_ERROR) {
			//
			// log.error(
			// "Login failed. See nested exceptions for further detail." +
			// e.getStackTrace());
			// }

			JsonResponse<List<ErrorCode>> errorResponse = new JsonResponse<>();
			errorResponse.setSuccess(false);

			if (e instanceof VocalServiceException)
				errorResponse.setContent(((VocalServiceException) e)
						.getErrorCodes());

			return errorResponse;
		}

		return response;
	}

	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public void logout(@FormParam("sessionid") UUID id) {
		SessionManagement.logout(id);
	}
}
