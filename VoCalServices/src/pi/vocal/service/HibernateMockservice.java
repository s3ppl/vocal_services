package pi.vocal.service;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.Event;
import pi.vocal.persistence.dto.User;
import pi.vocal.user.Location;

@Path("/dbservice")
public class HibernateMockservice {

	@GET
	@Path("/testdb")
	@Produces(MediaType.APPLICATION_JSON)
	public String testDb() throws UnsupportedEncodingException {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		
		User user1 = new User();
		user1.setEmail("my@mail.com");
		user1.setSchoolLocation(Location.STUTTGART);
		session.save(user1);
		
		Event event1 = new Event();
		event1.setAttendants(Arrays.asList(user1));
		session.save(event1);
		
		session.getTransaction().commit();
		session.close();
		
//		session = HibernateUtil.getSessionFactory().openSession();
//		session.beginTransaction();
//		
//		user1 = (User) session.get(User.class, 1L);
//		System.out.println(user1.getSchoolLocation());
//		
//		session.close();

		return "test";
	}
	
}
