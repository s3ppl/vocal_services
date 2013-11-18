package pi.vocal.management;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import pi.vocal.management.exception.AccountCreationException;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.User;
import pi.vocal.service.dto.PublicUser;
import pi.vocal.user.Grade;
import pi.vocal.user.Location;
import pi.vocal.user.Role;

public class UserManagement {

	private static final int MIN_PW_LEN = 6;

	private static String convertToBase64(byte[] input) {
		return DatatypeConverter.printBase64Binary(input);
	}

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
		}

		if (null == user.getGrade()) {
			errorCodes.add(ErrorCode.GRADE_MISSING);
		}

		if (null == user.getSchoolLocation()) {
			errorCodes.add(ErrorCode.SCHOOL_MISSING);
		}

		if (null == password || password.isEmpty()) {
			errorCodes.add(ErrorCode.PASSWORD_MISSING);
		} else if (password.length() < MIN_PW_LEN) {
			errorCodes.add(ErrorCode.PASSWORD_TOO_SHORT);
		}

		return errorCodes;
	}

	private static User createUserFromUserInput(String firstName,
			String lastName, String email, Grade grade,
			Location schoolLocation, String password)
			throws AccountCreationException {

		User userDto = new User();
		userDto.setFirstName(firstName);
		userDto.setLastName(lastName);
		userDto.setEmail(email);
		userDto.setRole(Role.USER);
		userDto.setGrade(grade);
		userDto.setSchoolLocation(schoolLocation);

		List<ErrorCode> errorCodes = verifyUserInput(userDto, password);

		if (errorCodes.size() > 0) {
			throw new AccountCreationException(errorCodes,
					"Account creation failed due to invalid user input.");
		}

		byte[] pwSalt;
		try {
			pwSalt = PasswordEncryptionHelper.generateSalt();
			byte[] encryptedPw = PasswordEncryptionHelper.getEncryptedPassword(
					password, pwSalt);

			userDto.setPwHash(convertToBase64(encryptedPw));
			userDto.setPwSalt(convertToBase64(pwSalt));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new AccountCreationException(
					ErrorCode.INTERNAL_ERROR,
					"Could not create Account. Password hashing or salting failed. See nested Exception for further details.",
					e);
		}

		return userDto;
	}

	// TODO add error handling of invalid user attributes to this method. return
	// a list of error codes or null
	public static void createUser(String firstName, String lastName,
			String email, Grade grade, Location schoolLocation, String password)
			throws AccountCreationException {

		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();

			User userDto = createUserFromUserInput(firstName, lastName, email,
					grade, schoolLocation, password);

			session.beginTransaction();
			session.save(userDto);
			session.getTransaction().commit();
		} catch (HibernateException he) {
			session.getTransaction().rollback();
			throw new AccountCreationException(
					ErrorCode.INTERNAL_ERROR,
					"Could not create Account. Storing in the database failed. See nested Exception for further details.",
					he);
		}
	}

	public static PublicUser getUserById(long id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		User user = (User) session.get(User.class, id);
		session.getTransaction().commit();

		return new PublicUser(user);
	}

}
