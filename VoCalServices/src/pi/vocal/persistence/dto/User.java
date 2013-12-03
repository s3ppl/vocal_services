package pi.vocal.persistence.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import pi.vocal.user.Grade;
import pi.vocal.user.Role;
import pi.vocal.user.SchoolLocation;

/**
 * JavaBean like class, that will be used by Hibernate to store users.
 * 
 * NOTE: Table name has to be manually added, since the used MySql database has
 * issues with capitals.
 * 
 * @author s3ppl
 * 
 */

@Entity
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
@JsonIgnoreProperties({"pwHash", "pwSalt"})
public class User implements Serializable {

	private static final long serialVersionUID = 6684430286499464672L;

	@Id
	@GeneratedValue
	private long userId;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private SchoolLocation schoolLocation;

	@Column(nullable = false)
	private Grade grade;

	@Column(nullable = false)
	private String pwHash;

	@Column(nullable = false)
	private String pwSalt;

	@Column(nullable = false)
	private Role role;

	@OneToMany(mappedBy = "userId", fetch = FetchType.EAGER)
	private Set<UserAttendance> userAttendances;

	public long getUserId() {
		return userId;
	}

	public SchoolLocation getSchoolLocation() {
		return schoolLocation;
	}

	public void setSchoolLocation(SchoolLocation schoolLocation) {
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

	public Set<UserAttendance> getUserAttendance() {
		return userAttendances;
	}

	public void setUserAttendance(Set<UserAttendance> userAttendances) {
		this.userAttendances = userAttendances;
	}

	public void addUserAttendance(UserAttendance userAttendance) {
		if (null == this.userAttendances) {
			this.userAttendances = new HashSet<>();
		}

		this.userAttendances.add(userAttendance);
	}

}
