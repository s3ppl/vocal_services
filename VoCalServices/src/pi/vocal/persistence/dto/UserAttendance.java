package pi.vocal.persistence.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="user_attendance")
public class UserAttendance implements Serializable {
	private static final long serialVersionUID = 160088057122789206L;

	private boolean attends;

	@Id
	@GeneratedValue
	private long userAttendanceId;
	
//	@Id
	@JoinColumn(name="userId")
//	private User user;
	private long userId;
	
//	@Id
	@JoinColumn(name="eventId")
//	private Event event;
	private long eventId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public boolean isAttends() {
		return attends;
	}

	public void setAttends(boolean attends) {
		this.attends = attends;
	}

//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//
//	public Event getEvent() {
//		return event;
//	}
//
//	public void setEvent(Event event) {
//		this.event = event;
//	}

	public long getUserAttendanceId() {
		return userAttendanceId;
	}
	
	
}
