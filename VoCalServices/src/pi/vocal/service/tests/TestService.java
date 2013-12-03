package pi.vocal.service.tests;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.hibernate.Session;

import pi.vocal.management.SessionManagement;
import pi.vocal.management.UserManagement;
import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.Event;
import pi.vocal.persistence.dto.User;
import pi.vocal.persistence.dto.UserAttendance;
import pi.vocal.service.EventService;
import pi.vocal.service.JsonResponse;
import pi.vocal.service.UserService;
import pi.vocal.service.dto.PublicUser;
import pi.vocal.user.Grade;
import pi.vocal.user.SchoolLocation;

@Path("/TestService")
public class TestService {

	@Context
	HttpServletRequest request;
	
	public TestService() {
		System.err.println("SERVICE STARTED!!!1111");
	}
	
	@GET
	@Path("/getUsersBygrade")
	public String getUsersByGrade() {
		UserManagement.getUsersByGrade(Grade.MASTER);		
		
		return "done...";
	}

	@GET
	@Path("/getUser")
	@Produces(MediaType.APPLICATION_JSON)
	public User getTrackInJSON(@QueryParam("test") String test) {
		User track = new User();
		track.setFirstName("Enter Sandman");
		track.setLastName("Metallica");

		track.setEmail(test);

		return track;
	}

	@GET
	@Path("/getUserResponse")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<?> getUser() {

		PublicUser user = new PublicUser();
		user.setEmail("foo@bar.de");

		JsonResponse<List<PublicUser>> response = new JsonResponse<>();
		response.setContent(Arrays.asList(user));
		response.setSuccess(true);

		return response;
	}

	@GET
	@Path("/getRequest")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRequest() throws UnsupportedEncodingException {
		request.getSession().setAttribute("foo", "bar");
		System.out.println(request.getSession().getId().length());

		return request.getSession().getId();
	}

	@GET
	@Path("/testRequest")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse<List<Object>> testRequest() {
		List<Object> myList = new ArrayList<>();
		myList.add(new User());
		myList.add("foo");

		JsonResponse<List<Object>> response = new JsonResponse<>();
		response.setContent(myList);
		response.setSuccess(true);

		return response;
	}

	@GET
	@Path("/createUserAndEvent")
	@Produces(MediaType.APPLICATION_JSON)
	public String autoCreateUserAndEvent() throws VocalServiceException {
		User user = new User();
		user.setEmail("foo@bar.de");
		user.setFirstName("hans");
		user.setLastName("bert");
		user.setGrade(Grade.DISCIPLE);
		user.setSchoolLocation(SchoolLocation.MUEHLACKER);
		String password = "foobar1";

		UserService us = new UserService();
		us.createAccount(user.getFirstName(), user.getLastName(),
				user.getEmail(), password, user.getGrade(),
				user.getSchoolLocation());

		Event event = new Event();
		event.addAttendantGrade(Grade.CHILD);
		event.setTitle("test event");
		event.setStartDate(System.currentTimeMillis());
		event.setEndDate(System.currentTimeMillis() + 10000);
//		event.setEventType(EventType.DEMO);

		UUID sessionId = (UUID) SessionManagement.login(user.getEmail(),
				password).get("sessionId");

		EventService es = new EventService();
		es.createEvent(sessionId, event.getTitle(), event.getDescription(),
				event.getStartDate(), event.getEndDate(), event.getEventType(),
				true, false, false, false);

		UserAttendance ua = new UserAttendance();

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

//		ua.setUser((User) session.get(User.class, 1L));
//		ua.setEvent((Event) session.get(Event.class, 1L));

		event = (Event) session.get(Event.class, 1L);
		System.out.println(event.getAttendantsGrades());

		session.save(ua);
		session.getTransaction().commit();
		session.close();

		return "done...";
	}

}
