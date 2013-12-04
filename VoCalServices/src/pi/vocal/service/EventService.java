package pi.vocal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import pi.vocal.event.EventType;
import pi.vocal.management.ErrorCode;
import pi.vocal.management.EventManagement;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.persistence.dto.Event;
import pi.vocal.service.dto.JsonResponse;
import pi.vocal.service.dto.PublicEvent;
import pi.vocal.service.dto.PublicUser;

@Path("/EventMgmt")
public class EventService {

	private final static Logger logger = Logger.getLogger(EventService.class);

	@POST
	@Path("/createEvent")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<ErrorCode>> createEvent(
			@FormParam("sessionid") UUID sessionId,
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

		try {
			EventManagement.createEvent(sessionId, title, description,
					startDate, endDate, type, childrenMayAttend,
					disciplesMayAttend, trainersMayAttend, mastersMayAttend);
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

	@POST
	@Path("/getEventsBetween")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<?>> getEventsBetween(
			@FormParam("sessionid") UUID sessionId,
			@FormParam("startdate") long startDate,
			@FormParam("enddate") long endDate) {
		
		JsonResponse<List<?>> response = new JsonResponse<>();
		response.setSuccess(true);

		try {
			List<PublicEvent> resultEvents = new ArrayList<>();
			List<Event> internalEvents = EventManagement.getEventsBetween(
					sessionId, startDate, endDate);
			
			for (Event event : internalEvents) {
				resultEvents.add(new PublicEvent(event));
			}

			response.setContent(resultEvents);
		} catch (VocalServiceException e) {
			response.setSuccess(false);
			response.setContent(e.getErrorCodes());
		}
		
		return response;
	}
}
