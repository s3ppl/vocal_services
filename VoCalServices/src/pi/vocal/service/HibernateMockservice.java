package pi.vocal.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import pi.vocal.event.EventType;
import pi.vocal.management.PasswordEncryptionHelper;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.Event;
import pi.vocal.persistence.dto.User;
import pi.vocal.user.Location;

@Path("/dbservice")
public class HibernateMockservice {

	@GET
	@Path("/testdb")
	@Produces(MediaType.APPLICATION_JSON)
	public String testDb() {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();

		User user1 = new User();
		user1.setEmail("my@mail.com");
		user1.setSchoolLocation(Location.STUTTGART);

		String pwSalt = "";
		try {
			pwSalt = new String(PasswordEncryptionHelper.generateSalt());

			user1.setPwSalt(pwSalt);
			user1.setPwHash(new String(PasswordEncryptionHelper
					.getEncryptedPassword("foo", pwSalt.getBytes())));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Event event1 = new Event();
		event1.setEventType(EventType.DEMO);
		event1.setAttendants(Arrays.asList(user1));
		user1.setEvents(Arrays.asList(event1));

		session.save(user1);
		session.save(event1);

		session.getTransaction().commit();
		session.flush();
		session.close();

		return user1.toString();
	}

	@GET
	@Path("/getUser")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser() throws NoSuchAlgorithmException, InvalidKeySpecException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		User user = (User)session.get(User.class, 1L);
//		session.close();
		
		System.out.println("user=" + user);
		System.out.println("hash=" + user.getPwHash());
		System.out.println("salt=" + user.getPwSalt());
		
		assert(!user.equals(null));
		
		System.out.println(PasswordEncryptionHelper.authenticate("foo", user.getPwHash().getBytes(), user.getPwSalt().getBytes()));
		
		return user;
	}
	
	@GET
	@Path("/getEvent")
	@Produces(MediaType.APPLICATION_JSON)
	public Event getEvent() throws NoSuchAlgorithmException, InvalidKeySpecException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Event event = (Event)session.get(Event.class, 1L);
//		session.close();
		
		assert(!event.equals(null));
		
//		System.out.println(PasswordEncryptionHelper.authenticate("foo", user.getPwHash().getBytes(), user.getPwSalt().getBytes()));
		
		return event;
	}
}
