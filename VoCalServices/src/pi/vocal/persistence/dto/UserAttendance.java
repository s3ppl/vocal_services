package pi.vocal.persistence.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="user_attendance")
public class UserAttendance implements Serializable {
	// TODO implement the join table for user and event in this class to be able to add custom columns

	private static final long serialVersionUID = 160088057122789206L;

	private boolean attends;

	@Id
	@ManyToOne
	@JoinColumn(name="userId")
	private User user;
	
	@Id
	@ManyToOne
	@JoinColumn(name="eventId")
	private Event event;

	public boolean isAttends() {
		return attends;
	}

	public void setAttends(boolean attends) {
		this.attends = attends;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
	
}
