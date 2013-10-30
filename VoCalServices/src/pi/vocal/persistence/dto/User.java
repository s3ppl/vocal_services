package pi.vocal.persistence.dto;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;

import pi.vocal.user.Grade;
import pi.vocal.user.Location;

@Entity
// table name needed to avoid case sensitivity errors in mysql DB
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
public class User implements Serializable {

	private static final long serialVersionUID = 6684430286499464672L;

	@Id
	@GeneratedValue
	private long userId;
	
	private String firstName;
	private String lastName;
	private String email;
	
	private Location schoolLocation;
	private Grade grade;
	
	private String pwHash;
	private String pwSalt;
	
	@ManyToMany
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
	
	public String getPrename() {
		return firstName;
	}
	
	public void setPrename(String prename) {
		this.firstName = prename;
	}
	
	public String getSurname() {
		return lastName;
	}
	
	public void setSurname(String surname) {
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
	
}
