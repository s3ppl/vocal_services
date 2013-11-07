package pi.vocal.persistence.dto;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import pi.vocal.user.Grade;
import pi.vocal.user.Location;
import pi.vocal.user.Role;

@Entity
// table name needed to avoid case sensitivity errors in mysql DB
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
public class User implements Serializable {

	private static final long serialVersionUID = 6684430286499464672L;

	@Id
	@GeneratedValue
	private long userId;
	
	@Column(nullable=false)
	private String firstName;
	
	@Column(nullable=false)
	private String lastName;
	
	@Column(nullable=false)
	private String email;
	
	@Column(nullable=false)
	private Location schoolLocation;
	
	@Column(nullable=false)
	private Grade grade;
	
	@Column(nullable=false)
	private String pwHash;
	
	@Column(nullable=false)
	private String pwSalt;
	
	@Column(nullable=false)
	private Role role;
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name="event_attendance",
			joinColumns={@JoinColumn(name="userId")},
			inverseJoinColumns={@JoinColumn(name="eventId")})
	private List<Event> events;
	
	public Location getSchoolLocation() {
		return schoolLocation;
	}
	
	public void setSchoolLocation(Location schoolLocation) {
		this.schoolLocation = schoolLocation;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String prename) {
		this.firstName = prename;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String surname) {
		this.lastName = surname;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Grade getGrade() {
		return grade;
	}
	
	public void setGrade(Grade grade) {
		this.grade = grade;
	}
	
	public String getPwHash() {
		return pwHash;
	}
	
	public void setPwHash(String pwHash) {
		this.pwHash = pwHash;
	}
	
	public String getPwSalt() {
		return pwSalt;
	}
	
	public void setPwSalt(String pwSalt) {
		this.pwSalt = pwSalt;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
	
}
