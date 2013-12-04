package pi.vocal.management;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.helper.PasswordEncryptionHelper;
import pi.vocal.management.helper.ResultConstants;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.User;
import pi.vocal.user.Grade;
import pi.vocal.user.Role;
import pi.vocal.user.SchoolLocation;

public class UserManagement {

	// TODO add logging

	/**
	 * Minimum length a password must have
	 */
	private static final int MIN_PW_LEN = 6;

	/**
	 * Regular expression to match an email address
	 */
	private static final String EMAIL_MATCH_STRING = "^[0-9A-Za-z_\\-.]+@[0-9A-Za-z_\\-.]+\\.[0-9A-Za-z_\\-.]+$";

	/**
	 * Private constructor since all methods are static.
	 */
	private UserManagement() {
	}

	/**
	 * Gets the user according to the given email address from the database.
	 * 
	 * @param email
	 *            The email address of the user to get
	 * @return The User object from the database according to the given mail
	 *         address
	 */
	public static User getUserByEmail(String email) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		User user = (User) session.createCriteria(User.class)
				.add(Restrictions.eq("email", email)).uniqueResult();

		session.getTransaction().commit();
		session.close();

		return user;
	}

	@SuppressWarnings("unchecked")
	public static List<User> getUsersByGrade(Grade grade) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		List<User> result = session.createCriteria(User.class)
				.add(Restrictions.eq("grade", grade)).list();
		session.getTransaction().commit();
		session.close();

		return result;
	}

	/**
	 * Verifies the input the user made while creating an account. Checks for
	 * empty or invalid inputs.
	 * 
	 * @param user
	 *            The user object that holds the input to verify
	 * @param password
	 *            The password the user entered
	 * @return A list of error codes that contains all errors that occurred. If
	 *         all values are correct, the list will be empty.
	 */
	private static List<ErrorCode> verifyUserInput(User user, String password) {
		List<ErrorCode> errorCodes = new ArrayList<>();

		if (null == user.getFirstName() || user.getFirstName().isEmpty()) {
			errorCodes.add(ErrorCode.FIRSTNAME_MISSING);
		}

		if (null == user.getLastName() || user.getLastName().isEmpty()) {
			errorCodes.add(ErrorCode.LASTNAME_MISSING);
		}

		if (null == user.getGrade() || user.getGrade() == Grade.NOT_SELECTED) {
			errorCodes.add(ErrorCode.GRADE_MISSING);
		}

		if (null == user.getSchoolLocation()
				|| user.getSchoolLocation() == SchoolLocation.NOT_SELECTED) {

			errorCodes.add(ErrorCode.SCHOOL_LOCATION_MISSING);
		}

		if (null == password || password.isEmpty()) {
			errorCodes.add(ErrorCode.PASSWORD_MISSING);
		} else if (password.length() < MIN_PW_LEN) {
			errorCodes.add(ErrorCode.PASSWORD_TOO_SHORT);
		}

		if (null == user.getEmail() || user.getEmail().isEmpty()) {
			errorCodes.add(ErrorCode.EMAIL_MISSING);
		} else if (!user.getEmail().matches(EMAIL_MATCH_STRING)) {
			errorCodes.add(ErrorCode.EMAIL_INVALID);
		} else if (null != getUserByEmail(user.getEmail())) {
			errorCodes.add(ErrorCode.EMAIL_ALREADY_IN_USE);
		}

		return errorCodes;
	}

	/**
	 * Creates a user object from the given input. Also adds password hash and
	 * salt for database storing.
	 * 
	 * @param firstName
	 *            The firstname the user entered
	 * @param lastName
	 *            The lastname of the user
	 * @param email
	 *            The email of the user
	 * @param grade
	 *            The grade of the user
	 * @param schoolLocation
	 *            The location of the school the user trains
	 * @param password
	 *            The password of the user
	 * @return A user object containing the entered data of the user
	 * @throws VocalServiceException
	 *             Thrown if any value the user entered is invalid or an
	 *             internal error occurred
	 */
	private static User createUserFromUserInput(String firstName,
			String lastName, String email, Grade grade,
			SchoolLocation schoolLocation, String password)
			throws VocalServiceException {

		User userDto = new User();
		userDto.setFirstName(firstName);
		userDto.setLastName(lastName);
		userDto.setEmail(email);
		userDto.setRole(Role.USER);
		userDto.setGrade(grade);
		userDto.setSchoolLocation(schoolLocation);
		userDto.addUserAttendance(null);

		List<ErrorCode> errorCodes = verifyUserInput(userDto, password);

		// if any value was invalid, throw an exception containing the errors
		if (null != errorCodes && errorCodes.size() > 0) {
			throw new VocalServiceException(errorCodes,
					"Account creation failed due to invalid user input.");
		}

		// create and set password hash and salt
		try {
			byte[] pwSalt = PasswordEncryptionHelper.generateSalt();
			byte[] encryptedPw = PasswordEncryptionHelper.getEncryptedPassword(
					password, pwSalt);

			userDto.setPwSalt(PasswordEncryptionHelper.convertToBase64(pwSalt));
			userDto.setPwHash(PasswordEncryptionHelper
					.convertToBase64(encryptedPw));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new VocalServiceException(
					ErrorCode.INTERNAL_ERROR,
					"Could not create Account. Password hashing or salting failed. See nested Exception for further details.",
					e);
		}

		return userDto;
	}

	/**
	 * Checks the correctness of the given password.
	 * 
	 * @param user
	 *            The user according to the password to check
	 * @param password
	 *            The password to verify
	 * @return Returns {@code true} if the given password matches the stored
	 *         password of the user; {@code false} is returned otherwise.
	 * @throws VocalServiceException
	 *             Thrown if the encryption of the given password fails
	 */
	private static boolean verifyCurrentPassword(User user, String password)
			throws VocalServiceException {

		boolean result = false;
		try {
			byte[] userPwHash = PasswordEncryptionHelper.convertFromBase64(user
					.getPwHash());
			byte[] userPwSalt = PasswordEncryptionHelper.convertFromBase64(user
					.getPwSalt());
			result = PasswordEncryptionHelper.authenticate(password,
					userPwHash, userPwSalt);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new VocalServiceException(ErrorCode.INTERNAL_ERROR, e);
		}

		return result;
	}

	/**
	 * Creates a new user in the database.
	 * 
	 * @param firstName
	 *            The firstname the user entered
	 * @param lastName
	 *            The lastname of the user
	 * @param email
	 *            The email of the user
	 * @param grade
	 *            The grade of the user
	 * @param schoolLocation
	 *            The location of the school the user trains
	 * @param password
	 *            The password of the user
	 * @throws VocalServiceException
	 *             Thrown if the creation of the user fails due to invalid input
	 *             or an internal error
	 */
	public static void createUser(String firstName, String lastName,
			String email, Grade grade, SchoolLocation schoolLocation,
			String password) throws VocalServiceException {

		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			User userDto = createUserFromUserInput(firstName, lastName, email,
					grade, schoolLocation, password);

			session.beginTransaction();
			session.save(userDto);
			session.getTransaction().commit();
			session.close();
		} catch (HibernateException he) {
			// if (null != session && session.isOpen()
			// && null != session.getTransaction()) {
			//
			// session.getTransaction().rollback();
			// }

			throw new VocalServiceException(
					ErrorCode.INTERNAL_ERROR,
					"Could not create Account. Storing to the database failed. See nested Exception for further details.",
					he);
		}
	}

	/**
	 * Changes the password of the user that matches the given sessionId and
	 * stores it to the database.
	 * 
	 * @param sessionId
	 *            The sessionId of the user, thats password should be changed
	 * @param oldPassword
	 *            The current password of the user
	 * @param newPassword1
	 *            The new password, that should be stored to the database
	 * @param newPassword2
	 *            The new password again to avoid typos
	 * @return Returns a {@code SuccessCode} that confirms that the password was
	 *         changed correctly
	 * @throws VocalServiceException
	 *             Thrown if: a) a given password was null b) the two new
	 *             passwords don't match c) the new password is too short d) the
	 *             current password was incorrect e) the given sessionId could
	 *             not be found
	 */
	public static SuccessCode changePassword(UUID sessionId,
			String oldPassword, String newPassword1, String newPassword2)
			throws VocalServiceException {

		if (null == newPassword1 || null == newPassword2
				|| !newPassword1.equals(newPassword2)) {
			throw new VocalServiceException(ErrorCode.PASSWORDS_DONT_MATCH);
		} else if (newPassword1.length() < MIN_PW_LEN) {
			throw new VocalServiceException(ErrorCode.PASSWORD_TOO_SHORT);
		}

		User user = SessionManagement.getUserBySessionId(sessionId);
		if (null != user) {
			if (null == oldPassword
					|| !verifyCurrentPassword(user, oldPassword)) {

				throw new VocalServiceException(ErrorCode.AUTHENTICATION_FAILED);
			}

			// update user object with new password
			try {
				// generate new password and salt for storing
				byte[] newPwSalt = PasswordEncryptionHelper.generateSalt();
				byte[] newPwHash = PasswordEncryptionHelper
						.getEncryptedPassword(newPassword1, newPwSalt);
				String encodedPw = PasswordEncryptionHelper
						.convertToBase64(newPwHash);
				String encodedSalt = PasswordEncryptionHelper
						.convertToBase64(newPwSalt);

				user.setPwHash(encodedPw);
				user.setPwSalt(encodedSalt);

				// persist the changed user object
				Session session = HibernateUtil.getSessionFactory()
						.openSession();
				session.beginTransaction();
				session.update(user);
				session.getTransaction().commit();
				session.close();
			} catch (NoSuchAlgorithmException | InvalidKeySpecException
					| HibernateException e) {
				throw new VocalServiceException(ErrorCode.INTERNAL_ERROR, e);
			}
		} else {
			throw new VocalServiceException(ErrorCode.SESSION_INVALID);
		}

		return SuccessCode.PASSWORD_CHANGED;
	}

	/**
	 * Find a user in the database using his database id.
	 * 
	 * @param id
	 *            The id of the user to find
	 * @return The PublicUser object of the user according to the given id
	 */
	// TODO change comment!
	public static User getUserById(long id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		User user = (User) session.get(User.class, id);
		session.getTransaction().commit();
		session.close();

		return user;
	}

	/**
	 * Changes an existing {@code User} in the database.
	 * 
	 * @param sessionId
	 *            The session id of the user, that wants to change his
	 *            attributes
	 * @param firstName
	 *            The new firstname of the user. May be null
	 * @param lastName
	 *            The new lastname of the user. May be null
	 * @param location
	 *            The new location of the user. May be null
	 * @return Returns either a map with the updated user object and a list of
	 *         {@code SuccessCode}s
	 * @throws VocalServiceException
	 *             Thrown if the user with the given session id could not be
	 *             found
	 */
	public static Map<Enum<ResultConstants>, Object> editUser(UUID sessionId,
			String firstName, String lastName, SchoolLocation location)
			throws VocalServiceException {

		Map<Enum<ResultConstants>, Object> result = new HashMap<Enum<ResultConstants>, Object>();
		List<SuccessCode> successCodes = new ArrayList<>();

		User user = SessionManagement.getUserBySessionId(sessionId);

		if (null != user) {
			if (null != firstName && !user.getFirstName().equals(firstName)) {
				user.setFirstName(firstName);
				successCodes.add(SuccessCode.FIRSTNAME_CHANGED);
			}

			if (null != lastName && !user.getLastName().equals(lastName)) {
				user.setLastName(lastName);
				successCodes.add(SuccessCode.LASTNAME_CHANGED);
			}

			if (null != location && user.getSchoolLocation() != location) {
				user.setSchoolLocation(location);
				successCodes.add(SuccessCode.SCHOOL_LOCATION_CHANGED);
			}

			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.update(user);
			session.getTransaction().commit();
			session.close();

			result.put(ResultConstants.EDITUSER_SUCCESSCODES_KEY, successCodes);
			result.put(ResultConstants.EDITUSER_USER_KEY, user);
		} else {
			throw new VocalServiceException(ErrorCode.SESSION_INVALID);
		}

		return result;
	}

}
