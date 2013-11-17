package pi.vocal.management;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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

	private static String convertToBase64(byte[] input) {
		return DatatypeConverter.printBase64Binary(input);
	}

	// TODO add error handling of invalid user attributes to this method. return a list of error codes or null
	public static void createUser(String firstName, String lastName, String email, Grade grade, Location schoolLocation, String password)
			throws AccountCreationException {

		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			
			User userDto = new User();
			userDto.setFirstName(firstName);
			userDto.setLastName(lastName);
			userDto.setEmail(email);
			userDto.setRole(Role.USER);
			userDto.setGrade(grade);
			userDto.setSchoolLocation(schoolLocation);

			byte[] pwSalt = PasswordEncryptionHelper.generateSalt();
			byte[] encryptedPw = PasswordEncryptionHelper.getEncryptedPassword(
					password, pwSalt);

			userDto.setPwHash(convertToBase64(encryptedPw));
			userDto.setPwSalt(convertToBase64(pwSalt));

			session.beginTransaction();
			session.save(userDto);
			session.getTransaction().commit();
		} catch (HibernateException | NoSuchAlgorithmException
				| InvalidKeySpecException he) {

			session.getTransaction().rollback();
			throw new AccountCreationException(
					ErrorCode.INTERNAL_ERROR,
					"Could not create Account. See nested Exception for further details.",
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
