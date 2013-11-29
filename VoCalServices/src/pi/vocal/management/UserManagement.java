package pi.vocal.management;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.xml.bind.DatatypeConverter;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.helper.PasswordEncryptionHelper;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.User;
import pi.vocal.service.dto.PublicUser;
import pi.vocal.user.Grade;
import pi.vocal.user.SchoolLocation;
import pi.vocal.user.Role;

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

	// TODO this description sucks
	/**
	 * Converts a given String to base64-encoding.
	 * 
	 * @param input
	 *            The string to encode
	 * @return A base64 representation of the given String
	 */
	private static String convertToBase64(byte[] input) {
		return DatatypeConverter.printBase64Binary(input);
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

		if (null == user.getEmail() || user.getEmail().isEmpty()) {
			errorCodes.add(ErrorCode.EMAIL_MISSING);
		} else if (!user.getEmail().matches(EMAIL_MATCH_STRING)) {
			errorCodes.add(ErrorCode.EMAIL_INVALID);
		} else if (!(null == getUserByEmail(user.getEmail()))) {
			errorCodes.add(ErrorCode.EMAIL_ALREADY_IN_USE);
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

			userDto.setPwHash(convertToBase64(encryptedPw));
			userDto.setPwSalt(convertToBase64(pwSalt));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new VocalServiceException(
					ErrorCode.INTERNAL_ERROR,
					"Could not create Account. Password hashing or salting failed. See nested Exception for further details.",
					e);
		}

		return userDto;
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
			if (null != session && session.isOpen()) {
				session.getTransaction().rollback();
			}

			throw new VocalServiceException(
					ErrorCode.INTERNAL_ERROR,
					"Could not create Account. Storing to the database failed. See nested Exception for further details.",
					he);
		}
	}

	public static void changePassword(String old, String new1, String new2)
			throws VocalServiceException {

		if (!new1.equals(new2)) {
			throw new VocalServiceException(ErrorCode.PASSWORDS_DONT_MATCH);
		}
	}

	/**
	 * Find a user in the database using his database id.
	 * 
	 * @param id
	 *            The id of the user to find
	 * @return The PublicUser object of the user according to the given id
	 */
	public static PublicUser getUserById(long id) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		User user = (User) session.get(User.class, id);
		session.getTransaction().commit();
		session.close();

		return new PublicUser(user);
	}

	public static List<SuccessCode> editUser(UUID sessionId, String firstName,
			String lastName, SchoolLocation location) throws VocalServiceException {

		List<SuccessCode> result = new ArrayList<>();
		User user = SessionManagement.getUserBySessionId(sessionId);
		
		if (null != user) {
			if (null != firstName && !user.getFirstName().equals(firstName)) {
				user.setFirstName(firstName);
				result.add(SuccessCode.FIRSTNAME_CHANGED);
			}
			
			if (null != lastName && !user.getLastName().equals(lastName)) {
				user.setLastName(lastName);
				result.add(SuccessCode.LASTNAME_CHANGED);
			}
			
			if (null != location && user.getSchoolLocation() != location) {
				user.setSchoolLocation(location);
				result.add(SuccessCode.SCHOOL_LOCATION_CHANGED);
			}
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.update(user);
			session.getTransaction().commit();
			session.close();
		} else {
			throw new VocalServiceException(ErrorCode.SESSION_INVALID);
		}

		return result;
	}

}
