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
 * 
 * 
 * @author s3ppl
 * 
 */

// TODO comment this class
public class EventManagement {
	private final static Logger logger = Logger
			.getLogger(EventManagement.class);

	private EventManagement() {
	}

	private static List<ErrorCode> verifyEventInput(Event event) {
		List<ErrorCode> errors = new ArrayList<>();

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

		if (null == event.getEventType()) {
			errors.add(ErrorCode.EVENT_TYPE_MISSING);
			logger.warn("No event type selected!");
		}

		return errors;
	}

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

	private static Event createEventFromInput(String title, String description,
			Long startDate, Long endDate, EventType type,
			boolean childrenMayAttend, boolean disciplesMayAttend,
			boolean trainersMayAttend, boolean mastersMayAttend)
			throws VocalServiceException {

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

		if (null != errorCodes && errorCodes.size() > 0) {
			throw new VocalServiceException(errorCodes,
					"Event creation failed due to invalid input.");
		}

		return eventDto;
	}

	private static void inviteUsersToEvent(Event event) {
		List<User> users = null;
		UserAttendance userAttendance = null;

		for (Grade grade : event.getAttendantsGrades()) {
			users = UserManagement.getUsersByGrade(grade);

			for (User user : users) {
				userAttendance = new UserAttendance();
				userAttendance.setAttends(false);
				userAttendance.setEventId(event.getEventId());
				userAttendance.setUserId(user.getUserId());

				user.addUserAttendance(userAttendance);
				event.addUserAttendance(userAttendance);

				Session session = HibernateUtil.getSessionFactory()
						.openSession();
				session.beginTransaction();
				session.save(userAttendance);
				session.update(user);
				session.update(event);
				session.getTransaction().commit();
				session.flush();
				session.close();
				
				logger.debug("user has " + user.getUserAttendance().size() + " events");
			}
		}
	}

	private static boolean userHasPermission(User user)
			throws VocalServiceException {

		if (user.getRole() != Role.ADMIN && user.getRole() != Role.MANAGER) {
			return false;
		}

		return true;
	}

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

	public static Event getEventById(long id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Event event = (Event) session.get(Event.class, id);
		session.getTransaction().commit();
		session.close();

		return event;
	}

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

		Event event = createEventFromInput(title, description, startDate,
				endDate, type, childrenMayAttend, disciplesMayAttend,
				trainersMayAttend, mastersMayAttend);

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

		// invite all users having the according grades
		inviteUsersToEvent(event);
	}

	public static List<Event> getEventsBetween(UUID sessionId, long startDate,
			long endDate) throws VocalServiceException {

		User user = SessionManagement.getUserBySessionId(sessionId);
		List<Event> events = new ArrayList<>();

		if (null != user) {
			Event event;
			for (UserAttendance ua : user.getUserAttendance()) {
				Session session = HibernateUtil.getSessionFactory()
						.openSession();
				session.beginTransaction();
				event = (Event) session.get(Event.class, ua.getEventId());
				session.getTransaction().commit();
				session.close();

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

	public static Map<Enum<ResultConstants>, Object> editEvent(UUID sessionId,
			long eventId, String title, String description, Long startDate,
			Long endDate, EventType type, boolean childrenMayAttend,
			boolean disciplesMayAttend, boolean trainersMayAttend,
			boolean mastersMayAttend) throws VocalServiceException {

		Map<Enum<ResultConstants>, Object> result = new HashMap<>();

		User user = SessionManagement.getUserBySessionId(sessionId);
		if (null == user) {
			throw new VocalServiceException(ErrorCode.SESSION_INVALID);
		} else if (!userHasPermission(user)) {
			throw new VocalServiceException(ErrorCode.INVALID_USER_PERMISSIONS);
		}

		List<SuccessCode> successCodes = null;
		Event event = EventManagement.getEventById(eventId);
		if (event != null) {
			successCodes = updateEventAttributes(event, title, description,
					startDate, endDate, type, childrenMayAttend,
					disciplesMayAttend, trainersMayAttend, mastersMayAttend);

			if (successCodes.contains(SuccessCode.ATTENDANCE_GRADES_CHANGED)) {
				inviteUsersToEvent(event);
			}

			result.put(ResultConstants.EDITEVENT_SUCCESSCODES_KEY, successCodes);
			result.put(ResultConstants.EDITEVENT_EVENT_KEY, event);

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
