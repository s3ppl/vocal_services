package pi.vocal.management.returncodes;

/**
 * This class contains all successful feedbacks that occur while editing a
 * {@code User} or {@code event}
 * 
 * @author s3ppl
 * 
 */

public enum SuccessCode {

	// change user codes
	PASSWORD_CHANGED, FIRSTNAME_CHANGED, LASTNAME_CHANGED, GRADE_CHANGED, SCHOOL_LOCATION_CHANGED,

	// change event codes
	TITLE_CHANGED, DESCRIPTION_CHANGED, PERIOD_CHANGED, EVENTTYPE_CHANGED, ATTENDANCE_GRADES_CHANGED;
}
