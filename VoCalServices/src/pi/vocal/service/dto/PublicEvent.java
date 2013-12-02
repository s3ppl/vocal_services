package pi.vocal.service.dto;

import java.util.List;
import java.util.Set;

import pi.vocal.event.EventType;
import pi.vocal.persistence.dto.Event;
import pi.vocal.persistence.dto.User;
import pi.vocal.persistence.dto.UserAttendance;
import pi.vocal.user.Grade;
import pi.vocal.user.SchoolLocation;
import pi.vocal.user.Role;

public class PublicEvent {
	private long startDate;
	private long endDate;
	
	private String title;
	private String description;
	
	private EventType eventType; 

	private long eventId;
	
	private List<EventUser> attendants;
	
	public PublicEvent() {}

	public PublicEvent(Event event) {
		this.startDate = event.getStartDate();
		this.endDate = event.getEndDate();
		
		this.title = event.getTitle();
		this.description = event.getDescription();
		
		this.eventType = event.getEventType();
		
		this.eventId = event.getEventId();
		
		this.setAttendents(event.getUserAttendance());
	}

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
	
	public List<EventUser> getAttendents() {
		return attendants;
	}
	
	public void setAttendents(Set<UserAttendance> userAttendances) {
		EventUser attendant = null;
		User user = null;
		for (UserAttendance userAttendance : userAttendances) {
			user = userAttendance.getUser();
			
			attendant = new EventUser();
			attendant.setEmail(user.getEmail());
			attendant.setFirstName(user.getFirstName());
			attendant.setGrade(user.getGrade());
			attendant.setLastName(user.getLastName());
			attendant.setRole(user.getRole());
			attendant.setSchoolLocation(user.getSchoolLocation());
			
			this.attendants.add(attendant);
		}
	}
	
	class EventUser {
		private String firstName;
		private String lastName;
		private String email;
		
		private SchoolLocation schoolLocation;
		private Grade grade;
		
		private Role role;

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
	}
}
