package pi.vocal.management;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.DatatypeConverter;

import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.helper.PasswordEncryptionHelper;
import pi.vocal.persistence.dto.User;

public class SessionManagement {

	private static final int MAX_SESSION_ID_CREATION_CYCLES = 20000;

	private static Map<UUID, User> sessions = new ConcurrentHashMap<UUID, User>();

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

	private static byte[] convertFromBase64(String input) {
		return DatatypeConverter.parseBase64Binary(input);
	}

	public synchronized static UUID login(String email, String password)
			throws VocalServiceException {

		User user = UserManagement.getUserByEmail(email);

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
			throw new VocalServiceException(ErrorCode.INTERNAL_ERROR, e);
		}

		// add the users session
		UUID sessionId = generateSessionId();
		sessions.put(sessionId, user);

		return sessionId;
	}

	public synchronized static void logout(UUID sessionId) {
		sessions.remove(sessionId);
	}

	public synchronized static User getUserBySessionId(long id) {
		return sessions.get(id);
	}
}
