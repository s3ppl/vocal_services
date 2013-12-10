package pi.vocal.management;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;

import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.returncodes.ErrorCode;
import pi.vocal.persistence.HibernateUtil;
import pi.vocal.persistence.dto.User;
import pi.vocal.user.Role;

/**
 * This class contains all functions an administrator can use.
 * 
 * @author s3ppl
 * 
 */
public class AdminManagement {

	/**
	 * Checks the given admin user for null and admin permissions.
	 * 
	 * @param admin
	 *            The {@code User} object to check for admin permissions
	 * @throws VocalServiceException
	 *             Thrown if either the given {@code User} object is null it has
	 *             no admin permissions
	 */
	private static void verifyAdminUser(User admin)
			throws VocalServiceException {

		if (null == admin) {
			throw new VocalServiceException(ErrorCode.SESSION_INVALID);
		} else if (admin.getRole() != Role.ADMIN) {
			throw new VocalServiceException(ErrorCode.INVALID_USER_PERMISSIONS);
		}
	}

	/**
	 * Reads all {@code User}s from the database and returns them as a
	 * {@code List}.
	 * 
	 * @return A {@code List} of all users stored in the database.
	 */
	private static List<User> getAllUsersFromDatabase() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		@SuppressWarnings("unchecked")
		List<User> users = session.createCriteria(User.class).list();

		session.getTransaction().commit();
		session.close();

		return users;
	}

	/**
	 * Gets all {@code User}s from the database. Requires administration
	 * permissions.
	 * 
	 * @param sessionId
	 *            The sessionId of the {@code User} that wants to get all
	 *            {@code User}s.
	 * @return A {@code List} of all {@code Users} stored in the database.
	 * @throws VocalServiceException
	 *             Thrown if the demanding {@code User} could not be found or
	 *             has insufficient permissions
	 */
	public static List<User> getAllUsers(UUID sessionId)
			throws VocalServiceException {

		verifyAdminUser(SessionManagement.getUserBySessionId(sessionId));
		return getAllUsersFromDatabase();
	}

	/**
	 * Changes the {@code Role} of the {@code User} with the given userId.
	 * Requires administration permissions.
	 * 
	 * @param sessionId
	 *            The sessionId of the {@code User} that wants to set the
	 *            {@code Role} of another {@code User}
	 * @param userId
	 *            The userId of the {@code User} thats {@code Role} should be
	 *            changed
	 * @param newRole
	 *            The {@code Role} to set
	 * @throws VocalServiceException
	 *             Thrown if either the the demanding {@code User} could not be
	 *             found, has insufficient permissions or the {@code User} thats
	 *             {@code Role} should be changed could not be found
	 */
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
