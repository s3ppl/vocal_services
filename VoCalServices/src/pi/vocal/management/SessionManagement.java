package pi.vocal.management;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.helper.PasswordEncryptionHelper;
import pi.vocal.management.helper.ResultConstants;
import pi.vocal.management.returncodes.ErrorCode;
import pi.vocal.persistence.dto.User;

/**
 * This class handles the login and logout of a {@code User}.
 * 
 * @author s3ppl
 * 
 */
public class SessionManagement {
	private final static Logger logger = Logger.getLogger(SessionManagement.class);

	/**
	 * Maximum amount of tries to create a unique session id
	 */
	private static final int MAX_SESSION_ID_CREATION_CYCLES = 20000;

	/**
	 * The current user sessions are stored in this map, where the key is the id
	 * of the session
	 */
	private static Map<UUID, User> sessions = new ConcurrentHashMap<UUID, User>();

	/**
	 * Private constructor since all methods are static.
	 */
	private SessionManagement() {
	}

	/**
	 * Searches for a given session id in the sessions map.
	 * 
	 * @param sessionId
	 *            The id to search
	 * @return {@code true} if the id is found, {@code false} otherwise
	 */
	private static boolean sessionIdExists(UUID sessionId) {
		boolean exists = false;

		for (UUID key : sessions.keySet()) {
			if (key.equals(sessionId)) {
				exists = true;
				break;
			}
		}

		return exists;
	}

	/**
	 * Removes the session id of the {@code User} with given mail address if
	 * any. Needed if the {@code User} logs in with a different device without
	 * logging off first.
	 * 
	 * @param email
	 *            The email address of the user that should be removed if
	 *            already logged in
	 */
	private static void removeAlreadyLoggedInUser(String email) {
		for (UUID id : sessions.keySet()) {
			if (sessions.get(id).getEmail().equals(email)) {
				logger.warn("User was already logged in. Removing session id: " + id.toString());
				sessions.remove(id);
			}
		}
	}

	/**
	 * Generates a unique session id. If the generated id is already in use, a
	 * new will be generated.
	 * 
	 * @return A unique session id
	 * @throws VocalServiceException
	 *             Thrown if the maximum attempts of creation a unique session
	 *             id get reached.
	 */
	private static UUID generateSessionId() throws VocalServiceException {
		UUID sessionId = null;
		boolean done = false;
		int count = 0;

		while (!done) {
			sessionId = UUID.randomUUID();

			if (!sessionIdExists(sessionId)) {
				done = true;
			} else if (count >= MAX_SESSION_ID_CREATION_CYCLES) {
				logger.error("Failed to create a unique sessionId after max. cycles! Max cycles were: "
						+ MAX_SESSION_ID_CREATION_CYCLES);
				throw new VocalServiceException(ErrorCode.INTERNAL_ERROR);
			}
		}

		return sessionId;
	}

	/**
	 * Handles the login of user. Therefore a unique session id will be created.
	 * If the user was already logged in, his old session will be removed and a
	 * new session will be created.
	 * 
	 * @param email
	 *            The email address of the user
	 * @param password
	 *            The password of the user
	 * @return A map containing a newly created session id and user according to
	 *         the given mail address
	 * @throws VocalServiceException
	 *             Thrown if either the authentication of the user failed or an
	 *             internal error occurred.
	 */
	public synchronized static Map<Enum<ResultConstants>, Object> login(String email, String password)
			throws VocalServiceException {

		// make sure the user won't get logged in twice
		removeAlreadyLoggedInUser(email);

		// get the according user from the database
		User user = UserManagement.getUserByEmail(email);
		Map<Enum<ResultConstants>, Object> result = new HashMap<Enum<ResultConstants>, Object>();

		try {
			boolean success = false;

			if (null != user) {
				// convert the users password and try to authenticate
				byte[] userPwHash = PasswordEncryptionHelper.convertFromBase64(user.getPwHash());
				byte[] userPwSalt = PasswordEncryptionHelper.convertFromBase64(user.getPwSalt());
				success = PasswordEncryptionHelper.authenticate(password, userPwHash, userPwSalt);
			}

			if (!success) {
				throw new VocalServiceException(ErrorCode.AUTHENTICATION_FAILED);
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.error("Password encryption failed. See nested Exception for details.", e);
			throw new VocalServiceException(ErrorCode.INTERNAL_ERROR, e);
		}

		// add the users session
		UUID sessionId = generateSessionId();
		sessions.put(sessionId, user);

		result.put(ResultConstants.LOGIN_USER_KEY, user);
		result.put(ResultConstants.LOGIN_SESSIONID_KEY, sessionId);

		return result;
	}

	/**
	 * Handles the logout of a user by removing him from the sessions map.
	 * 
	 * @param sessionId
	 *            The session id of the user to log out.
	 */
	public synchronized static void logout(UUID sessionId) {
		if (null != sessionId && sessions.containsKey(sessionId)) {
			sessions.remove(sessionId);
		}
	}

	/**
	 * Returns the according user to the given session id.
	 * 
	 * @param id
	 *            The session id of the user to get
	 * @return The user according to the given id or null if no user exists or
	 *         the given id was null
	 */
	public synchronized static User getUserBySessionId(UUID id) {
		if (null == id) {
			return null;
		}

		return sessions.get(id);
	}

	/**
	 * Updates an existing user object that is stored in the session
	 * 
	 * @param sessionId
	 *            The sessionId of the user to update
	 * @param user
	 *            The user to update
	 */
	public synchronized static void updateSessionUser(UUID sessionId, User user) {
		// check for id existence to ensure 'overwrite only'
		if (sessions.containsKey(sessionId)) {
			sessions.put(sessionId, user);
		}
	}
}
