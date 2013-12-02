package pi.vocal.management;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jboss.logging.Logger;

import pi.vocal.event.EventType;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.Event;
import pi.vocal.service.dto.PublicEvent;
import pi.vocal.user.Grade;

public class EventManagement {

	private final static Logger logger = Logger
			.getLogger(EventManagement.class);

	private EventManagement() {
	}

	private static List<ErrorCode> verifyEventInput(Event event) {
		List<ErrorCode> errors = new ArrayList<>();

		if (null == event.getTitle() || event.getTitle().isEmpty()) {
			errors.add(ErrorCode.TITLE_MISSING);
		}

		if (null == event.getStartDate()) {
			errors.add(ErrorCode.STARTDATE_MISSING);
		} else if (null == event.getEndDate()) {
			errors.add(ErrorCode.ENDDATE_MISSING);
		} else 	if (event.getStartDate() > event.getEndDate()) {
			errors.add(ErrorCode.STARTDATE_AFTER_ENDDATE);
		}

		if (event.getAttendantsGrades().size() < 1) {
			errors.add(ErrorCode.NO_ATTENDANCE_GRADE_SELECTED);
		}

		if (null == event.getEventType()) {
			errors.add(ErrorCode.EVENT_TYPE_MISSING);
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

	public static PublicEvent getEventById(long id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Event event = (Event) session.get(Event.class, id);
		session.getTransaction().commit();
		session.close();

		return new PublicEvent(event);
	}

	// TODO add auto invite for all users with according grades!
	public static PublicEvent createEvent(String title, String description,
			Long startDate, Long endDate, EventType type,
			boolean childrenMayAttend, boolean disciplesMayAttend,
			boolean trainersMayAttend, boolean mastersMayAttend)
			throws VocalServiceException {
		
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
			if (null != session && session.isOpen()) {
				session.getTransaction().rollback();
			}

			throw new VocalServiceException(
					ErrorCode.INTERNAL_ERROR,
					"Could not create Account. Storing to the database failed. See nested Exception for further details.",
					e);
		}

		return null;
	}

}
