package pi.vocal.management;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.DatatypeConverter;

import org.jboss.logging.Logger;

import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.helper.PasswordEncryptionHelper;
import pi.vocal.persistence.dto.User;

/**
 * This class handles the login and logout of a user.
 * 
 * @author s3ppl
 * 
 */
public class SessionManagement {
	private final static Logger LOGGER = Logger.getLogger(SessionManagement.class);

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
	 * Removes the session id of the user with given mail address if any.
	 * 
	 * @param email
	 *            The email address of the user that should be removed if
	 *            already logged in
	 */
	private static void removeAlreadyLoggedInUser(String email) {
		for (UUID id : sessions.keySet()) {
			if (sessions.get(id).getEmail().equals(email)) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("User was already logged in. Remove session id: " + id.toString());
				}
				
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
				throw new VocalServiceException(ErrorCode.INTERNAL_ERROR,
						new RuntimeException(
								"Failed to create a unique session id!"));
			}
		}

		return sessionId;
	}

	/**
	 * Converts a given {@code String}, that is encoded with base64 to a byte
	 * array.
	 * 
	 * @param input
	 *            The string to convert
	 * @return A byte array, representing the given {@code String}.
	 */
	private static byte[] convertFromBase64(String input) {
		return DatatypeConverter.parseBase64Binary(input);
	}

	/**
	 * Handles the login of user.
	 * 
	 * @param email
	 *            The email address of the user
	 * @param password
	 *            The password of the user
	 * @return A new session id, that will be used after the login
	 * @throws VocalServiceException
	 *             Thrown if either the authentication of the user failed or an
	 *             internal error occurred.
	 */
	public synchronized static Map<String, Object> login(String email, String password)
			throws VocalServiceException {

		// make sure the user won't get logged in twice
		removeAlreadyLoggedInUser(email);
		
		// get the according user from the database
		User user = UserManagement.getUserByEmail(email);
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			boolean success = false;

			if (null != user) {
				byte[] userPwHash = convertFromBase64(user.getPwHash());
				byte[] userPwSalt = convertFromBase64(user.getPwSalt());

				success = PasswordEncryptionHelper.authenticate(password,
						userPwHash, userPwSalt);
			}

			if (!success) {
				throw new VocalServiceException(ErrorCode.AUTHENTICATION_FAILED);
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			LOGGER.error("Password encryption failed.", e);
			throw new VocalServiceException(ErrorCode.INTERNAL_ERROR, e);
		}

		// add the users session
		UUID sessionId = generateSessionId();
		sessions.put(sessionId, user);

		result.put("user", user);
		result.put("sessionId",	sessionId);
		
		return result;
	}

	/**
	 * Handles the logout of a user by removing him from the sessions map.
	 * 
	 * @param sessionId
	 *            The session id of the user to log out.
	 */
	public synchronized static void logout(UUID sessionId) {
		sessions.remove(sessionId);
	}

	/**
	 * Returns the according user to the given session id.
	 * 
	 * @param id
	 *            The session id of the user to get
	 * @return The user according to the given id or null if no user exists or the given id was null
	 */
	public synchronized static User getUserBySessionId(UUID id) {
		if (null == id) {
			return null;
		}
		
		return sessions.get(id);
	}
}
