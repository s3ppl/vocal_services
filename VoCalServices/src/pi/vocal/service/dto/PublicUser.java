package pi.vocal.service.dto;

import java.util.ArrayList;
import java.util.List;

import pi.vocal.event.EventType;
import pi.vocal.persistence.dto.Event;
import pi.vocal.user.Grade;
import pi.vocal.user.Location;
import pi.vocal.user.Role;

public class PublicUser {

	private String firstName;
	private String lastName;
	private String email;
	
	private Location schoolLocation;
	private Grade grade;
	
	private Role role;
	
	private List<UserEvent> userEvents = new ArrayList<>();
		
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
	public Location getSchoolLocation() {
		return schoolLocation;
	}
	public void setSchoolLocation(Location schoolLocation) {
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
	
	public void setEvents(List<Event> events) {
		UserEvent userEvent = null;
		for (Event event : events) {
			userEvent = new UserEvent();
			userEvent.setDescription(event.getDescription());
			userEvent.setEndDate(event.getEndDate());
			userEvent.setEventId(event.getEventId());
			userEvent.setEventType(event.getEventType());
			userEvent.setStartDate(event.getStartDate());
			userEvent.setTitle(event.getTitle());
		}
		
		this.userEvents.add(userEvent);
	}
	
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
