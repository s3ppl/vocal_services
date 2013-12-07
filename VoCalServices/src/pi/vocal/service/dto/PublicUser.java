package pi.vocal.service.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import pi.vocal.event.EventType;
import pi.vocal.management.EventManagement;
import pi.vocal.persistence.dto.Event;
import pi.vocal.persistence.dto.User;
import pi.vocal.persistence.dto.UserAttendance;
import pi.vocal.user.Grade;
import pi.vocal.user.SchoolLocation;
import pi.vocal.user.Role;

/**
 * This class is the public representation of a {@code User}. It is used by the
 * webservices and contains all fields of the persistent user except for
 * password data.
 * 
 * NOTE: Due to cycles in the relational mapping between {@code User} and
 * {@code event}, this public representation has its own kind of event in form
 * of a nested class.
 * 
 * @author s3ppl
 * 
 */
public class PublicUser {
	private static final Logger logger = Logger.getLogger(PublicEvent.class);
	
	private String firstName;
	private String lastName;
	private String email;

	private SchoolLocation schoolLocation;
	private Grade grade;

	private Role role;

	private List<UserEvent> userEvents = new ArrayList<>();

	/**
	 * Default constructor. Needed by Jackson for parsing.
	 */
	public PublicUser() {
	}

	/**
	 * Constructor that copies all relevant data of the given user in this
	 * object.
	 * 
	 * @param user
	 *            The user that should be published
	 */
	public PublicUser(User user) {
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.schoolLocation = user.getSchoolLocation();
		this.grade = user.getGrade();
		this.role = user.getRole();

		setEvents(user.getUserAttendance());
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public SchoolLocation getSchoolLocation() {
		return schoolLocation;
	}

	public void setSchoolLocation(SchoolLocation schoolLocation) {
		this.schoolLocation = schoolLocation;
	}

	public Grade getGrade() {
		return grade;
	}

	public void setGrade(Grade grade) {
		this.grade = grade;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<UserEvent> getEvents() {
		return userEvents;
	}

	/**
	 * Converts all persistent events to a custom event. Afterwards those custom
	 * events will be stored in this {@code PublicUser} object.
	 * 
	 * @param attendedEvents
	 *            The events the user to publish attends
	 */
	public void setEvents(Set<UserAttendance> attendedEvents) {
		UserEvent userEvent = null;
		Event event = null;
		
		for (UserAttendance ua : attendedEvents) {
			event = EventManagement.getEventById(ua.getEventId());
			
			userEvent = new UserEvent();
			userEvent.setDescription(event.getDescription());
			userEvent.setEndDate(event.getEndDate());
			userEvent.setEventId(event.getEventId());
			userEvent.setEventType(event.getEventType());
			userEvent.setStartDate(event.getStartDate());
			userEvent.setTitle(event.getTitle());
			
			this.userEvents.add(userEvent);
		}		
	}

	/**
	 * This class is the same as the persistent {@code Event} except for its
	 * user list to avoid cyclic dependencies while being parsed by Jackson.
	 * 
	 * @author s3ppl
	 * 
	 */
	class UserEvent {
		private long startDate;
		private long endDate;

		private String title;
		private String description;

		private EventType eventType;
		private long eventId;

		public long getStartDate() {
			return startDate;
		}

		public void setStartDate(long startDate) {
			this.startDate = startDate;
		}

		public long getEndDate() {
			return endDate;
		}

		public void setEndDate(long endDate) {
			this.endDate = endDate;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public EventType getEventType() {
			return eventType;
		}

		public void setEventType(EventType eventType) {
			this.eventType = eventType;
		}

		public long getEventId() {
			return eventId;
		}

		public void setEventId(long eventId) {
			this.eventId = eventId;
		}
	}
}
