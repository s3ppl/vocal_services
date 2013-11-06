package pi.vocal.service.dto;

import java.util.List;

import org.codehaus.jackson.annotate.JsonManagedReference;

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
	
	@JsonManagedReference
	private List<PublicEvent> events;
	
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
	public List<PublicEvent> getEvents() {
		return events;
	}
	public void setEvents(List<PublicEvent> events) {
		this.events = events;
	}
}
