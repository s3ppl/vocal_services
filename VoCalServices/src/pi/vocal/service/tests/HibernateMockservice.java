package pi.vocal.service.tests;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import pi.vocal.event.EventType;
import pi.vocal.management.helper.PasswordEncryptionHelper;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.Event;
import pi.vocal.persistence.dto.User;
import pi.vocal.user.Grade;
import pi.vocal.user.SchoolLocation;
import pi.vocal.user.Role;

@Path("/dbservice")
public class HibernateMockservice {

	private String convertToBase64(byte[] input) {
		return DatatypeConverter.printBase64Binary(input);
	}

	@GET
	@Path("/testdb")
	@Produces(MediaType.APPLICATION_JSON)
	public String testDb() {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();

		User user1 = new User();
		user1.setEmail("my@mail.com");
		user1.setSchoolLocation(SchoolLocation.STUTTGART);
		user1.setFirstName("foo");
		user1.setLastName("bar");
		user1.setGrade(Grade.DISCIPLE);
		user1.setSchoolLocation(SchoolLocation.MUEHLACKER);
		user1.setRole(Role.USER);

		try {
			byte[] pwSalt = PasswordEncryptionHelper.generateSalt();
			byte[] encryptedPw = PasswordEncryptionHelper.getEncryptedPassword(
					"foo", pwSalt);

			user1.setPwSalt(convertToBase64(pwSalt));
			user1.setPwHash(convertToBase64(encryptedPw));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}

		Event event1 = new Event();
		event1.setEventType(EventType.DEMO);
		event1.setStartDate(System.currentTimeMillis());
		event1.setEndDate(System.currentTimeMillis() + 1000);
		event1.setTitle("MyEvent");
//		event1.setAttendants(Arrays.asList(user1));
//		user1.setEvents(Arrays.asList(event1));

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
	public User getUser() throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		User user = (User) session.get(User.class, 1L);
		
		// FIXME why cant the session be closed?
		// session.close();

		if (null != user) {
			System.out.println("user=" + user);
			System.out.println("hash=" + user.getPwHash());
			System.out.println("salt=" + user.getPwSalt());
		}

		System.out.println(PasswordEncryptionHelper.authenticate("foo", user
				.getPwHash().getBytes(), user.getPwSalt().getBytes()));

		return user;
	}

	@GET
	@Path("/getEvent")
	@Produces(MediaType.APPLICATION_JSON)
	public Event getEvent() throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Event event = (Event) session.get(Event.class, 1L);
		// session.close();

		assert (!event.equals(null));

		// System.out.println(PasswordEncryptionHelper.authenticate("foo",
		// user.getPwHash().getBytes(), user.getPwSalt().getBytes()));

		return event;
	}
}
