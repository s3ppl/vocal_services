package pi.vocal.management;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;

import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.User;
import pi.vocal.user.Role;

public class AdminManagement {

	private static void verifyAdminUser(User admin)
			throws VocalServiceException {

		if (null == admin) {
			throw new VocalServiceException(ErrorCode.SESSION_INVALID);
		} else if (admin.getRole() != Role.ADMIN) {
			throw new VocalServiceException(ErrorCode.INVALID_USER_PERMISSIONS);
		}
	}

	private static List<User> getAllUsersFromDatabase() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		@SuppressWarnings("unchecked")
		List<User> users = session.createCriteria(User.class).list();

		session.getTransaction().commit();
		session.close();

		return users;
	}

	public static List<User> getAllUsers(UUID sessionId)
			throws VocalServiceException {

		verifyAdminUser(SessionManagement.getUserBySessionId(sessionId));
		return getAllUsersFromDatabase();
	}

	public static void setUserRole(UUID sessionId, long userId, Role newRole)
			throws VocalServiceException {

		verifyAdminUser(SessionManagement.getUserBySessionId(sessionId));
		User user = UserManagement.getUserById(userId);

		if (null != user) {
			user.setRole(newRole);

			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.update(user);
			session.getTransaction().commit();
			session.close();
		} else {
			throw new VocalServiceException(ErrorCode.INVALID_USER_ID);
		}
	}

}
