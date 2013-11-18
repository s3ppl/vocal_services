package pi.vocal.management;

import org.hibernate.Session;

import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.Event;
import pi.vocal.service.dto.PublicEvent;

public class EventManagement {

	public static PublicEvent getEventById(long id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Event event = (Event) session.get(Event.class, id);
		session.getTransaction().commit();
		session.close();
		
		return new PublicEvent(event);
	}
	
}
