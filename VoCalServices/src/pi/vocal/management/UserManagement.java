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
import pi.vocal.user.Role;

public class UserManagement {

	private static String convertToBase64(byte[] input) {
		return DatatypeConverter.printBase64Binary(input);
	}

	public static void createUser(PublicUser user, String password) throws AccountCreationException {

		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();

			User userDto = new User();
			userDto.setFirstName(user.getFirstName());
			userDto.setLastName(user.getLastName());
			userDto.setEmail(user.getEmail());
			userDto.setRole(Role.USER);

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
			
			throw new AccountCreationException("Could not create Account. See nested Exception for further details.", he);
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
