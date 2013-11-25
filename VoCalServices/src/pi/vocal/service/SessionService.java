package pi.vocal.service;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import pi.vocal.management.ErrorCode;
import pi.vocal.management.SessionManagement;
import pi.vocal.management.exception.VocalServiceException;

@Path("/SessionMgmt")
public class SessionService {
	private static final Logger LOGGER = Logger.getLogger(SessionService.class);

	@GET
//	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<?> login(@QueryParam("email") String email,
			@QueryParam("password") String password) {
		JsonResponse<String> response = new JsonResponse<>();
		response.setSuccess(true);

		try {
			response.setContent(SessionManagement.login(email, password)
					.toString());
		} catch (VocalServiceException e) {
			if (e.getErrorCodes().size() == 1
					&& e.getErrorCodes().get(0) == ErrorCode.INTERNAL_ERROR) {
				
				LOGGER.error(
						"Login failed. See nested exceptions for further detail.",
						e);
			}

			JsonResponse<List<ErrorCode>> errorResponse = new JsonResponse<>();
			errorResponse.setSuccess(false);
			errorResponse.setContent(e.getErrorCodes());

			return errorResponse;
		}

		return response;
	}

	@GET
//	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<ErrorCode> logout(@QueryParam("id") String id) {
		JsonResponse<ErrorCode> response = new JsonResponse<>();
		response.setSuccess(true);

		try {
			UUID sessionId = UUID.fromString(id);
			SessionManagement.logout(sessionId);
		} catch (IllegalArgumentException e) {
			LOGGER.error(
					"The given session id had an invalid format. See nested exceptions for further details",
					e);

			response.setSuccess(false);
			response.setContent(ErrorCode.INTERNAL_ERROR);
		}

		return response;
	}
}
