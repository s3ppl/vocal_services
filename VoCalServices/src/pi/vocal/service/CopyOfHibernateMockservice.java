package pi.vocal.service;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Session;

import pi.vocal.persistence.HibernateUtil;

@Path("/dbservice")
public class CopyOfHibernateMockservice {

	@GET
	@Path("/testdb")
	@Produces(MediaType.APPLICATION_JSON)
	@Encoded
	public String testDb() throws UnsupportedEncodingException {
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		
		session.getTransaction().commit();
		session.close();

		return "test";
	}
	
}
