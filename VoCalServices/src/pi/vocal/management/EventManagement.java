package pi.vocal.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import pi.vocal.event.EventType;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.helper.ResultConstants;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.Event;
import pi.vocal.persistence.dto.User;
import pi.vocal.persistence.dto.UserAttendance;
import pi.vocal.user.Grade;
import pi.vocal.user.Role;

/**
 * This class contains all functions needed to manage {@code Event}s. Most of
 * its functions are for being used by a WebService.
 * 
 * @author s3ppl
 * 
 */
public class EventManagement {
	private final static Logger logger = Logger
			.getLogger(EventManagement.class);

	/**
	 * Private constructor since all methods are static.
	 */
	private EventManagement() {
	}

	/**
	 * Checks the validity of the fields of the given {@code Event}. All errors
	 * that were found will be returned as a {@code List} of {@code ErrorCode}s.
	 * 
	 * @param event
	 *            The {@code Event} to check
	 * @return Returns a {@code List} of {@code ErrorCode} containing all found
	 *         violations
	 */
	private static List<ErrorCode> verifyEventInput(Event event) {
		List<ErrorCode> errors = new ArrayList<>();

		logger.info("Verifying event with title: " + event.getTitle());

		if (null == event.getTitle() || event.getTitle().isEmpty()) {
			errors.add(ErrorCode.TITLE_MISSING);
			logger.warn("Title of event missing!");
		}

		if (null == event.getStartDate()) {
			errors.add(ErrorCode.STARTDATE_MISSING);
			logger.warn("Startdate of event missing!");
		} else if (null == event.getEndDate()) {
			errors.add(ErrorCode.ENDDATE_MISSING);
			logger.warn("Enddate of event missing!");
		} else if (event.getStartDate() > event.getEndDate()) {
			errors.add(ErrorCode.STARTDATE_AFTER_ENDDATE);
			logger.warn("Startdate was after endate!");
		}

		if (event.getAttendantsGrades().size() < 1) {
			errors.add(ErrorCode.NO_ATTENDANCE_GRADE_SELECTED);
			logger.warn("No attendances grades selected!");
		}

		if (null == event.getEventType()
				|| event.getEventType() == EventType.NOT_SELECTED) {
			
			errors.add(ErrorCode.EVENT_TYPE_MISSING);
			logger.warn("No event type selected!");
		}

		return errors;
	}

	/**
	 * Creates a {@code Set} of {@code Grade}s that contains all {@code Grade}s
	 * that are marked as {@code true}.
	 * 
	 * @param childrenMayAttend
	 *            Should be {@code true} if {@code Grade.CHILD} should be added
	 *            to the {@code Set}
	 * @param disciplesMayAttend
	 *            Should be {@code true} if {@code Grade.DISCIPLE} should be
	 *            added to the {@code Set}
	 * @param trainersMayAttend
	 *            Should be {@code true} if {@code Grade.TRAINER} should be
	 *            added to the {@code Set}
	 * @param mastersMayAttend
	 *            Should be {@code true} if {@code Grade.MASTER} should be added
	 *            to the {@code Set}
	 * @return Returns a {@code Set} of {@code Grade}s that were marked as
	 *         {@code true}
	 */
	private static Set<Grade> createSetOfAttendanceGrades(
			boolean childrenMayAttend, boolean disciplesMayAttend,
			boolean trainersMayAttend, boolean mastersMayAttend) {

		Set<Grade> attendanceGrades = new HashSet<>();

		if (childrenMayAttend) {
			attendanceGrades.add(Grade.CHILD);
		}

		if (disciplesMayAttend) {
			attendanceGrades.add(Grade.DISCIPLE);
		}

		if (trainersMayAttend) {
			attendanceGrades.add(Grade.TRAINER);
		}

		if (mastersMayAttend) {
			attendanceGrades.add(Grade.MASTER);
		}

		return attendanceGrades;
	}

	/**
	 * Creates a {@code Event} object using the given input. Afterwards the
	 * given input gets verified.
	 * 
	 * @param title
	 *            The title of the {@code Event} to create
	 * @param description
	 *            The description of the {@code Event} to create. May be null
	 * @param startDate
	 *            The startDate of the {@code Event} to create
	 * @param endDate
	 *            The endDate of the {@code Event} to create
	 * @param type
	 *            The {@code EventType} of the {@code Event} to create
	 * @param childrenMayAttend
	 *            should be {@code true} if children may attend this
	 *            {@code Event}
	 * @param disciplesMayAttend
	 *            should be {@code true} if disciples may attend this
	 *            {@code Event}
	 * @param trainersMayAttend
	 *            should be {@code true} if trainers may attend this
	 *            {@code Event}
	 * @param mastersMayAttend
	 *            should be {@code true} if masters may attend this
	 *            {@code Event}
	 * @return A newly created {@code Event} object from the given input
	 * @throws VocalServiceException
	 *             Thrown if the verification of the given input fails
	 */
	private static Event createEventFromInput(String title, String description,
			Long startDate, Long endDate, EventType type,
			boolean childrenMayAttend, boolean disciplesMayAttend,
			boolean trainersMayAttend, boolean mastersMayAttend)
			throws VocalServiceException {

		// create event object from the given input
		Event eventDto = new Event();
		eventDto.setTitle(title);
		eventDto.setDescription(description);
		eventDto.setEventType(type);
		eventDto.setStartDate(startDate);
		eventDto.setEndDate(endDate);

		eventDto.setAttendantsGrades(createSetOfAttendanceGrades(
				childrenMayAttend, disciplesMayAttend, trainersMayAttend,
				mastersMayAttend));

		List<ErrorCode> errorCodes = verifyEventInput(eventDto);

		// if any error occurred, throw an exception
		if (null != errorCodes && errorCodes.size() > 0) {
			throw new VocalServiceException(errorCodes,
					"Event creation failed due to invalid input.");
		}

		return eventDto;
	}

	/**
	 * Invites all {@code User}s in the database that are having a {@code Grade}
	 * contained in the given {@code Event} by adding an according
	 * {@code UserAttendance} to them.
	 * 
	 * @param event
	 *            The {@code Event} the {@code User}s should be invited to
	 */
	private static void inviteUsersToEvent(Event event) {
		List<User> users = null;
		UserAttendance userAttendance = null;

		for (Grade grade : event.getAttendantsGrades()) {
			users = UserManagement.getUsersByGrade(grade);

			// create a new UserAttendance object for each user with an
			// according grade
			for (User user : users) {
				userAttendance = new UserAttendance();
				userAttendance.setAttends(false);
				userAttendance.setEventId(event.getEventId());
				userAttendance.setUserId(user.getUserId());

				user.addUserAttendance(userAttendance);
				event.addUserAttendance(userAttendance);

				// persist the UserAttendance and update the references of the
				// event and the current user
				Session session = HibernateUtil.getSessionFactory()
						.openSession();
				session.beginTransaction();
				session.save(userAttendance);
				session.update(user);
				session.update(event);
				session.getTransaction().commit();
				session.flush();
				session.close();
			}
		}
	}

	/**
	 * Checks a {@code User} for having the correct permissions to manage an
	 * {@code Event}.
	 * 
	 * @param user
	 *            The {@code User} object to check
	 * @return Returns {@code true} if the {@code User} has the permissions to
	 *         manage {@code Event}s; false is returned otherwise
	 */
	private static boolean userHasPermission(User user) {
		if (user.getRole() != Role.ADMIN && user.getRole() != Role.MANAGER) {
			return false;
		}

		return true;
	}

	/**
	 * Updates a given {@code Event} with the new fields, that are given. All
	 * changed fields will be returned as a {@code List} of {@code SuccessCode}s
	 * for feedback.
	 * 
	 * @param event
	 *            The {@code Event} object to update
	 * @param title
	 *            The new title of the {@code Event}. May be null if no change
	 *            should be done
	 * @param description
	 *            The new description of the {@code Event}. May be null if no
	 *            change should be done
	 * @param startDate
	 *            The startDate title of the {@code Event}. May be null if no
	 *            change should be done
	 * @param endDate
	 *            The new endDate of the {@code Event}. May be null if no change
	 *            should be done
	 * @param type
	 *            The new {@code EventType} of the {@code Event}. May be null if
	 *            no change should be done
	 * @param childrenMayAttend
	 *            Should be {@code true} if children may attend this
	 *            {@code Event}
	 * @param disciplesMayAttend
	 *            Should be {@code true} if disciples may attend this
	 *            {@code Event}
	 * @param trainersMayAttend
	 *            Should be {@code true} if trainers may attend this
	 *            {@code Event}
	 * @param mastersMayAttend
	 *            Should be {@code true} if masters may attend this
	 *            {@code Event}
	 * @return Returns a {@code List} of {@code SuccessCode}s according to the
	 *         changes made
	 * @throws VocalServiceException
	 *             Thrown if the startDate is greater the endDate after the
	 *             changes were made
	 */
	private static List<SuccessCode> updateEventAttributes(Event event,
			String title, String description, Long startDate, Long endDate,
			EventType type, boolean childrenMayAttend,
			boolean disciplesMayAttend, boolean trainersMayAttend,
			boolean mastersMayAttend) throws VocalServiceException {

		List<SuccessCode> successCodes = new ArrayList<>();

		if (null != title && !title.isEmpty()) {
			event.setTitle(title);
			successCodes.add(SuccessCode.TITLE_CHANGED);
		}

		if (null != description && !description.isEmpty()) {
			event.setDescription(description);
			successCodes.add(SuccessCode.DESCRIPTION_CHANGED);
		}

		// use the 'old' value of startDate and endDate if given dates are null
		long tmpStart = startDate != null ? startDate : event.getStartDate();
		long tmpEnd = endDate != null ? endDate : event.getEndDate();
		if (tmpStart >= tmpEnd) {
			throw new VocalServiceException(ErrorCode.STARTDATE_AFTER_ENDDATE);
		} else {
			event.setStartDate(tmpStart);
			event.setEndDate(tmpEnd);
			successCodes.add(SuccessCode.PERIOD_CHANGED);
		}

		if (null != type) {
			event.setEventType(type);
			successCodes.add(SuccessCode.EVENTTYPE_CHANGED);
		}

		if (childrenMayAttend) {
			event.getAttendantsGrades().add(Grade.CHILD);
			successCodes.add(SuccessCode.ATTENDANCE_GRADES_CHANGED);
		}

		if (disciplesMayAttend) {
			event.getAttendantsGrades().add(Grade.DISCIPLE);
			successCodes.add(SuccessCode.ATTENDANCE_GRADES_CHANGED);
		}

		if (trainersMayAttend) {
			event.getAttendantsGrades().add(Grade.TRAINER);
			successCodes.add(SuccessCode.ATTENDANCE_GRADES_CHANGED);
		}

		if (mastersMayAttend) {
			event.getAttendantsGrades().add(Grade.MASTER);
			successCodes.add(SuccessCode.ATTENDANCE_GRADES_CHANGED);
		}

		return successCodes;
	}

	/**
	 * Reads an {@code Event} from the database determined by its id.
	 * 
	 * @param id
	 *            The id of the {@code Event} to search
	 * @return The {@code Event} according to the id given. If no {@code Event}
	 *         for the given id is found, null will be returned
	 */
	public static Event getEventById(long id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Event event = (Event) session.get(Event.class, id);
		session.getTransaction().commit();
		session.close();

		return event;
	}

	/**
	 * Creates an {@code Event} from the given input and stores it to the
	 * database.
	 * 
	 * @param sessionId
	 *            The sessionId of the {@code User} that wants to create the
	 *            {@code Event}
	 * @param title
	 *            The title of the {@code Event} to create
	 * @param description
	 *            The description of the {@code Event} to create
	 * @param startDate
	 *            The startDate of the {@code Event} to create
	 * @param endDate
	 *            The endDate of the {@code Event} to create
	 * @param type
	 *            The {@code EventType} of the {@code Event} to create
	 * @param childrenMayAttend
	 *            Should be {@code true} if children may attend the
	 *            {@code Event}; false otherwise
	 * @param disciplesMayAttend
	 *            Should be {@code true} if disciples may attend the
	 *            {@code Event}; false otherwise
	 * @param trainersMayAttend
	 *            Should be {@code true} if trainers may attend the
	 *            {@code Event}; false otherwise
	 * @param mastersMayAttend
	 *            Should be {@code true} if masters may attend the {@code Event}
	 *            ; false otherwise
	 * @throws VocalServiceException
	 *             Thrown if event creation fails due to missing administration
	 *             privileges, invalid field-values for the {@code Event} or if
	 *             an internal error occurs while storing the {@code Event} to
	 *             the database
	 */
	public static void createEvent(UUID sessionId, String title,
			String description, Long startDate, Long endDate, EventType type,
			boolean childrenMayAttend, boolean disciplesMayAttend,
			boolean trainersMayAttend, boolean mastersMayAttend)
			throws VocalServiceException {

		// check session validity and permissions
		User user = SessionManagement.getUserBySessionId(sessionId);
		if (null == user) {
			throw new VocalServiceException(ErrorCode.SESSION_INVALID);
		} else if (!userHasPermission(user)) {
			throw new VocalServiceException(ErrorCode.INVALID_USER_PERMISSIONS);
		}

		// create the event
		Event event = createEventFromInput(title, description, startDate,
				endDate, type, childrenMayAttend, disciplesMayAttend,
				trainersMayAttend, mastersMayAttend);

		// persist the event
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.save(event);
			session.getTransaction().commit();
			session.flush();
			session.close();
		} catch (HibernateException e) {
			String errorMsg = "Could not create Account. Storing to the database failed. See nested Exception for further details.";
			logger.error(errorMsg, e);
			throw new VocalServiceException(ErrorCode.INTERNAL_ERROR, errorMsg,
					e);
		}

		// invite all users having the according grades to attend
		inviteUsersToEvent(event);
	}

	/**
	 * Searches and returns all {@code Event}s within a given time interval of a
	 * {@code User}. Thereby all {@code Event}s are included that either start
	 * or end within the interval or run through the whole given interval.
	 * 
	 * @param sessionId
	 *            The sessionId of the {@code User} that requests the
	 *            {@code Event}s
	 * @param startDate
	 *            The startDate of the interval-time
	 * @param endDate
	 *            The endDate of the interval in UNIX-time
	 * @return Returns a {@code List} of {@code Event}s that lie within the
	 *         given interval
	 * @throws VocalServiceException
	 *             Thrown if the given sessionId could not be found
	 */
	public static List<Event> getEventsBetween(UUID sessionId, long startDate,
			long endDate) throws VocalServiceException {

		// get the user according the given sessionId
		User user = SessionManagement.getUserBySessionId(sessionId);
		List<Event> events = new ArrayList<>();

		if (null != user) {
			Event event;
			for (UserAttendance ua : user.getUserAttendance()) {

				// get current event from the database
				Session session = HibernateUtil.getSessionFactory()
						.openSession();
				session.beginTransaction();
				event = (Event) session.get(Event.class, ua.getEventId());
				session.getTransaction().commit();
				session.close();

				// check if the current event lies within the interval
				if ((event.getStartDate() >= startDate && event.getStartDate() <= endDate)
						|| (event.getEndDate() >= startDate && event
								.getEndDate() <= endDate)
						|| (event.getStartDate() < startDate && event
								.getEndDate() > endDate)) {

					events.add(event);
				}

			}
		} else {
			logger.warn("No user with the following session id could be found: "
					+ sessionId);
			throw new VocalServiceException(ErrorCode.SESSION_INVALID);
		}

		return events;
	}

	/**
	 * Changes an existing {@code Event} in the database. If the change is
	 * successful, a {@code Map} containing the {@code List} of
	 * {@code SuccessCode}s, according to the changes made, and of the updated
	 * {@code Event} will be returned.
	 * 
	 * @param sessionId
	 *            The sessionId of the {@code User} that wants to change the
	 *            {@code Event}
	 * @param eventId
	 *            The id of the {@code Event} to change
	 * @param title
	 *            The new title of the {@code Event}. May be null if no changes
	 *            should be made
	 * @param description
	 *            The new description of the {@code Event}. May be null if no
	 *            changes should be made
	 * @param startDate
	 *            The new startDate of the {@code Event}. May be null if no
	 *            changes should be made
	 * @param endDate
	 *            The new endDate of the {@code Event}. May be null if no
	 *            changes should be made
	 * @param type
	 *            The new {@code EventType} of the {@code Event}. May be null if
	 *            no changes should be made
	 * @param childrenMayAttend
	 *            Should be {@code true} if children may attend this
	 *            {@code Event}; false otherwise
	 * @param disciplesMayAttend
	 *            Should be {@code true} if disciples may attend this
	 *            {@code Event}; false otherwise
	 * @param trainersMayAttend
	 *            Should be {@code true} if trainers may attend this
	 *            {@code Event}; false otherwise
	 * @param mastersMayAttend
	 *            Should be {@code true} if masters may attend this
	 *            {@code Event}; false otherwise
	 * @return A {@code Map} containing a {@code List} of {@code SuccessCode}s
	 *         and the updated {@code Event}
	 * @throws VocalServiceException
	 *             Thrown if the sessionId or the eventId could not be found,
	 *             the according {@code User} to the sessionId has insufficient
	 *             permissions or the input made by the {@code User} was invalid
	 */
	public static Map<Enum<ResultConstants>, Object> editEvent(UUID sessionId,
			long eventId, String title, String description, Long startDate,
			Long endDate, EventType type, boolean childrenMayAttend,
			boolean disciplesMayAttend, boolean trainersMayAttend,
			boolean mastersMayAttend) throws VocalServiceException {

		Map<Enum<ResultConstants>, Object> result = new HashMap<>();

		// get the user according the given sessionId
		User user = SessionManagement.getUserBySessionId(sessionId);

		// check user and his permissions
		if (null == user) {
			throw new VocalServiceException(ErrorCode.SESSION_INVALID);
		} else if (!userHasPermission(user)) {
			throw new VocalServiceException(ErrorCode.INVALID_USER_PERMISSIONS);
		}

		// get the event according the given eventId
		Event event = EventManagement.getEventById(eventId);
		if (event != null) {
			List<SuccessCode> successCodes = null;

			// update the event
			successCodes = updateEventAttributes(event, title, description,
					startDate, endDate, type, childrenMayAttend,
					disciplesMayAttend, trainersMayAttend, mastersMayAttend);

			// if attendance flags were changed - invite users
			if (successCodes.contains(SuccessCode.ATTENDANCE_GRADES_CHANGED)) {
				inviteUsersToEvent(event);
			}

			result.put(ResultConstants.EDITEVENT_SUCCESSCODES_KEY, successCodes);
			result.put(ResultConstants.EDITEVENT_EVENT_KEY, event);

			// persist the updated event
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.update(event);
			session.getTransaction().commit();
			session.flush();
			session.close();
		} else {
			throw new VocalServiceException(ErrorCode.INVALID_EVENT_ID);
		}

		return result;
	}

}
