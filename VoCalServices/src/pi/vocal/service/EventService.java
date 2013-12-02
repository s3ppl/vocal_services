package pi.vocal.service;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import pi.vocal.event.EventType;
import pi.vocal.management.ErrorCode;
import pi.vocal.management.EventManagement;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.user.Grade;

@Path("/EventMgmt")
public class EventService {
	private static final Logger logger = Logger.getLogger(EventService.class);

	@POST
	@Path("/createEvent")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<ErrorCode>> createEvent(
			@FormParam("title") String title,
			@FormParam("description") String description,
			@FormParam("startdate") Long startDate,
			@FormParam("enddate") Long endDate,
			@FormParam("type") EventType type,
			@FormParam("child") boolean childrenMayAttend,
			@FormParam("disciple") boolean disciplesMayAttend,
			@FormParam("trainer") boolean trainersMayAttend,
			@FormParam("master") boolean mastersMayAttend) {

		JsonResponse<List<ErrorCode>> response = new JsonResponse<>();
		response.setSuccess(true);

		List<ErrorCode> errors = null;
		
		logger.debug("foobar!!!!11111einself");

		try {
			EventManagement.createEvent(title, description, startDate, endDate,
					type, childrenMayAttend, disciplesMayAttend,
					trainersMayAttend, mastersMayAttend);
		} catch (VocalServiceException e) {
			if (e.getErrorCodes().contains(ErrorCode.INTERNAL_ERROR)) {
				logger.error(
						"An internal error occurred. See nested exceptions for details.",
						e.getCause());
			}

			errors = e.getErrorCodes();
			response.setSuccess(false);
		}

		response.setContent(errors);

		return response;
	}

}
