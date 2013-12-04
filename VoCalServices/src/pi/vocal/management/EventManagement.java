package pi.vocal.management;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import pi.vocal.event.EventType;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.Event;
import pi.vocal.persistence.dto.User;
import pi.vocal.persistence.dto.UserAttendance;
import pi.vocal.user.Grade;

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
		Session session = HibernateUtil.getSessionFactory().openSession();

		List<User> users;
		UserAttendance userAttendance;
		for (Grade grade : event.getAttendantsGrades()) {
			users = UserManagement.getUsersByGrade(grade);

			for (User user : users) {
				userAttendance = new UserAttendance();
				userAttendance.setAttends(false);
				userAttendance.setEventId(event.getEventId());
				userAttendance.setUserId(user.getUserId());

				user.addUserAttendance(userAttendance);
				event.addUserAttendance(userAttendance);

				session.beginTransaction();
				session.update(user);
				session.update(event);
				session.save(userAttendance);
				session.getTransaction().commit();
			}
		}

		session.close();
	}

	public static Event getEventById(long id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Event event = (Event) session.get(Event.class, id);
		session.getTransaction().commit();
		session.close();

		return event;
	}

	// TODO add auto invite for all users with according grades!
	public static void createEvent(UUID sessionId, String title,
			String description, Long startDate, Long endDate, EventType type,
			boolean childrenMayAttend, boolean disciplesMayAttend,
			boolean trainersMayAttend, boolean mastersMayAttend)
			throws VocalServiceException {

		// check session validity and permissions
		User user = SessionManagement.getUserBySessionId(sessionId);
		if (null == user) {
			throw new VocalServiceException(ErrorCode.SESSION_INVALID);
		} /*
		 * else if (user.getRole() != Role.ADMIN && user.getRole() !=
		 * Role.MANAGER) { throw new
		 * VocalServiceException(ErrorCode.INVALID_USER_PERMISSIONS); }
		 */// FIXME uncomment permission checks after testing is done!

		Event event = createEventFromInput(title, description, startDate,
				endDate, type, childrenMayAttend, disciplesMayAttend,
				trainersMayAttend, mastersMayAttend);

		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.save(event);
			session.getTransaction().commit();
			session.close();
		} catch (HibernateException e) {
			// if (null != session && session.isOpen()) {
			// session.getTransaction().rollback();
			// }

			String errorMsg = "Could not create Account. Storing to the database failed. See nested Exception for further details.";
			logger.error(errorMsg, e);
			throw new VocalServiceException(ErrorCode.INTERNAL_ERROR, errorMsg,
					e);
		}

		inviteUsersToEvent(event);
	}

	@SuppressWarnings("unchecked")
	public static List<Event> getDateInterval(UUID sessionId, long startDate,
			long endDate) throws VocalServiceException {

		User user = SessionManagement.getUserBySessionId(sessionId);
		List<Event> events = new ArrayList<>();

		if (null != user) {
			Session session = HibernateUtil.getSessionFactory().openSession();

			Event event;
			for (UserAttendance ua : user.getUserAttendance()) {
				session.beginTransaction();
				event = (Event) session.get(Event.class, ua.getEventId());
				session.getTransaction().commit();

				if ((event.getStartDate() >= startDate && event.getStartDate() <= endDate)
						|| (event.getEndDate() >= startDate && event
								.getEndDate() <= endDate)) {
					
					events.add(event);
				}
			}
			
			session.close();
		} else {
			logger.warn("No user with the following session id could be found: "
					+ sessionId);
			throw new VocalServiceException(ErrorCode.SESSION_INVALID);
		}

		return events;
	}

}
