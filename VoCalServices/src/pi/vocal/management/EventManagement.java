package pi.vocal.management;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import pi.vocal.event.EventType;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.Event;
import pi.vocal.service.dto.PublicEvent;

public class EventManagement {
	
	private static List<ErrorCode> verifyEventInput(Event event) {
		List<ErrorCode> errors = new ArrayList<>();
		
		if (null == event.getTitle() || event.getTitle().isEmpty()) {
			errors.add(ErrorCode.TITLE_MISSING);
		}
		
		if (event.getStartDate() > event.getEndDate()) {
			errors.add(ErrorCode.STARTDATE_AFTER_ENDDATE);
		} else if (event.getStartDate() == 0l) {
			// TODO ask joe what he sends, when the field is empty
		} else if (event.getEndDate() == 0l) {
			// TODO ask joe what he sends, when the field is empty			
		}
		
		if (null == event.getEventType()) {
			errors.add(ErrorCode.EVENT_TYPE_MISSING);
		}
		
		return errors;
	}
	
	private static Event createEventFromInput(String title, String description,
			long startDate, long endDate, EventType type) throws VocalServiceException {
		
		Event eventDto = new Event();
		eventDto.setTitle(title);
		eventDto.setDescription(description);
		eventDto.setEventType(type);
		eventDto.setStartDate(startDate);
		eventDto.setEndDate(endDate);
		
		List<ErrorCode> errorCodes = verifyEventInput(eventDto);
		
		if (null != errorCodes && errorCodes.size() > 0) {
			throw new VocalServiceException(errorCodes, "Event creation failed due to invalid input.");
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

	public static PublicEvent createEvent(String title, String description,
			long startDate, long endDate, EventType type)
			throws VocalServiceException {
		
		Event event = createEventFromInput(title, description, startDate, endDate, type);
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
