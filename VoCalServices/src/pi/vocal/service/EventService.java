package pi.vocal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pi.vocal.event.EventType;
import pi.vocal.management.EventManagement;
import pi.vocal.management.SessionManagement;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.helper.ResultConstants;
import pi.vocal.management.returncodes.ErrorCode;
import pi.vocal.persistence.dto.Event;
import pi.vocal.persistence.dto.User;
import pi.vocal.service.dto.JsonResponse;
import pi.vocal.service.dto.PublicEvent;

/**
 * This class contains all WebServices for managing {@code Event}s.
 * 
 * @author s3ppl
 * 
 */

@Path("/EventMgmt")
public class EventService {

	/**
	 * WebService used to create new {@code Event}s. This WebService needs
	 * either manager or admin privileges.
	 * 
	 * @param sessionId
	 *            The sessionId of the user that tries to create an
	 *            {@code Event}
	 * @param title
	 *            The title of the {@code Event} to create
	 * @param description
	 *            The description of the {@code Event} to create
	 * @param startDate
	 *            The startDate of the {@code Event} to create in UNIX-time
	 * @param endDate
	 *            The endDate of the {@code Event} to create in UNIX-time
	 * @param type
	 *            The title of the {@code Event} to create
	 * @param childrenMayAttend
	 *            Should be {@code true} if children may attend this event;
	 *            {@code false} otherwise
	 * @param disciplesMayAttend
	 *            Should be {@code true} if disciples may attend this event;
	 *            {@code false} otherwise
	 * @param trainersMayAttend
	 *            Should be {@code true} if trainers may attend this event;
	 *            {@code false} otherwise
	 * @param mastersMayAttend
	 *            Should be {@code true} if masters may attend this event;
	 *            {@code false} otherwise
	 * @return If the {@code Event} creation was successful, a
	 *         {@code JsonResponse} containing an empty {@code List} will be
	 *         returned. Otherwise the contained {@code List} will hold all
	 *         {@code ErrorCode}s, that occurred while trying to create this
	 *         {@code Event}.
	 */
	@POST
	@Path("/createEvent")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<ErrorCode>> createEvent(@FormParam("sessionid") UUID sessionId,
			@FormParam("title") String title, @FormParam("description") String description,
			@FormParam("startdate") Long startDate, @FormParam("enddate") Long endDate,
			@FormParam("type") EventType type, @FormParam("child") boolean childrenMayAttend,
			@FormParam("disciple") boolean disciplesMayAttend, @FormParam("trainer") boolean trainersMayAttend,
			@FormParam("master") boolean mastersMayAttend) {

		JsonResponse<List<ErrorCode>> response = new JsonResponse<>();
		response.setSuccess(true);

		List<ErrorCode> errors = null;

		try {
			EventManagement.createEvent(sessionId, title, description, startDate, endDate, type, childrenMayAttend,
					disciplesMayAttend, trainersMayAttend, mastersMayAttend);
		} catch (VocalServiceException e) {
			errors = e.getErrorCodes();
			response.setSuccess(false);
		}

		response.setContent(errors);

		return response;
	}

	/**
	 * WebService to edit existing {@code Event}s by a {@code User}. To use this
	 * WebService, manager or admin privileges are needed. Fields of the
	 * {@code Event} that don't change, should be given as {@code null}.
	 * 
	 * @param sessionId
	 *            The sessionId of the {@code User} that wants to edit the given
	 *            {@code Event}
	 * @param eventId
	 *            The id of the {@code Event} to edit
	 * @param title
	 *            The new title of the {@code Event}
	 * @param description
	 *            The new description of the {@code Event}
	 * @param startDate
	 *            The new startDate of the {@code Event} in UNIX-time
	 * @param endDate
	 *            The new endDate of the {@code Event} in UNIX-time
	 * @param type
	 *            The new type of the {@code Event}
	 * @param childrenMayAttend
	 *            Should be {@code true} if children may attend this event;
	 *            {@code false} otherwise
	 * @param disciplesMayAttend
	 *            Should be {@code true} if disciples may attend this event;
	 *            {@code false} otherwise
	 * @param trainersMayAttend
	 *            Should be {@code true} if trainers may attend this event;
	 *            {@code false} otherwise
	 * @param mastersMayAttend
	 *            Should be {@code true} if masters may attend this event;
	 *            {@code false} otherwise
	 * @return If the edition of the given {@code Event} was successful, a
	 *         {@code JsonResponse} containing a {@code Map} will be returned.
	 *         This {@code Map} contains a {@code List} of {@code SuccessCode}s
	 *         according to the changes made and the updated {@code Event}.
	 * 
	 *         If the edition of the {@code Event} fails, the
	 *         {@code JsonResponse} containing a {@code List} of
	 *         {@code ErrorCode}s according to the occurred errors.
	 */
	@POST
	@Path("/editEvent")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<?> editEvent(@FormParam("sessionid") UUID sessionId, @FormParam("eventid") long eventId,
			@FormParam("title") String title, @FormParam("description") String description,
			@FormParam("startdate") Long startDate, @FormParam("enddate") Long endDate,
			@FormParam("type") EventType type, @FormParam("child") boolean childrenMayAttend,
			@FormParam("disciple") boolean disciplesMayAttend, @FormParam("trainer") boolean trainersMayAttend,
			@FormParam("master") boolean mastersMayAttend) {

		try {
			JsonResponse<Map<Enum<ResultConstants>, Object>> response = new JsonResponse<>();
			Map<Enum<ResultConstants>, Object> result = EventManagement.editEvent(sessionId, eventId, title,
					description, startDate, endDate, type, childrenMayAttend, disciplesMayAttend, trainersMayAttend,
					mastersMayAttend);

			// convert contained persistent event to a public event
			PublicEvent publicEvent = new PublicEvent((Event) result.get(ResultConstants.EDITEVENT_EVENT_KEY));
			result.put(ResultConstants.EDITEVENT_EVENT_KEY, publicEvent);
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
	 * WebService that returns an {@code Event} according to the given id.
	 * 
	 * @param sessionId
	 *            The sessionId of the {@code User} that wants to get an
	 *            {@code Event}
	 * @param eventId
	 *            The id of the {@code Event} to find
	 * @return If an {@code Event} according to the given id was found an
	 *         {@code JsonResponse} containing the {@code Event} represented by
	 *         its {@code PublicEvent} representation will be returned.
	 *         Otherwise a {@code List} of {@code ErrorCode}s will be returned.
	 */
	@POST
	@Path("/getEventById")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<?> getEventById(@FormParam("sessionid") UUID sessionId, @FormParam("eventid") long eventId) {
		User user = SessionManagement.getUserBySessionId(sessionId);
		
		// get persistent event according to the
		Event persistentEvent = EventManagement.getEventById(eventId);

		if (null != user && null != persistentEvent) {
			JsonResponse<PublicEvent> response = new JsonResponse<>();
			response.setSuccess(true);

			PublicEvent publicEvent = new PublicEvent(persistentEvent);
			response.setContent(publicEvent);

			return response;
		} else {
			JsonResponse<List<ErrorCode>> errorResponse = new JsonResponse<>();
			
			List<ErrorCode> errorCodes = new ArrayList<>();			
			if (null == user) {
				errorCodes.add(ErrorCode.SESSION_INVALID);
			}
			
			if (null == persistentEvent) {
				errorCodes.add(ErrorCode.INVALID_EVENT_ID);
			}
			
			errorResponse.setSuccess(false);
			errorResponse.setContent(errorCodes);

			return errorResponse;
		}
	}

	/**
	 * Looks up all events of the calling {@code User}, that are running within
	 * the given time period.
	 * 
	 * @param sessionId
	 *            The sessionId of the {@code User} thats {@code Event}s will be
	 *            looked up
	 * @param startDate
	 *            The startDate of the period, the {@code User} wants to look up
	 *            in UNIX-time
	 * @param endDate
	 *            The endDate of the period, the {@code User} wants to look up
	 *            in UNIX-time
	 * @return If the lookup was successful, a {@code JsonResponse} containing a
	 *         {@code List} of {@code Event}s will be returned. Otherwise the
	 *         {@code JsonResponse} contains a {@code List} of {@code ErrorCode}
	 *         s according to the occurred errors.
	 */
	@POST
	@Path("/getEventsBetween")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<?>> getEventsBetween(@FormParam("sessionid") UUID sessionId,
			@FormParam("startdate") long startDate, @FormParam("enddate") long endDate) {

		JsonResponse<List<?>> response = new JsonResponse<>();
		response.setSuccess(true);

		// wrap all events to PublicEvents
		try {
			List<PublicEvent> resultEvents = new ArrayList<>();
			List<Event> internalEvents = EventManagement.getEventsBetween(sessionId, startDate, endDate);

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
