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
import pi.vocal.management.ErrorCode;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.persistence.dto.User;
import pi.vocal.service.dto.JsonResponse;
import pi.vocal.service.dto.PublicUser;
import pi.vocal.user.Role;

@Path("/AdminMgmt")
public class AdminService {

	@POST
	@Path("/getAllUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<?>> getAllUsers(
			@FormParam("sessionid") UUID sessionId) {

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

	@POST
	@Path("/setUserRole")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<ErrorCode>> setUserRole(
			@FormParam("sessionid") UUID sessionId,
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
