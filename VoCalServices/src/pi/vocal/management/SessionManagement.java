package pi.vocal.management;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import pi.vocal.management.exception.VocalServiceException;
import pi.vocal.management.helper.PasswordEncryptionHelper;
import pi.vocal.persistence.dto.User;

public class SessionManagement {

	private static Map<String, User> sessions;

	private static String generateSessionId() {
		return "";
	}

	private static byte[] convertFromBase64(String input) {
		return DatatypeConverter.parseBase64Binary(input);
	}

	public static String login(String email, String password)
			throws VocalServiceException {

		User user = UserManagement.getUserByEmail(email);

		try {
			byte[] userPwHash = convertFromBase64(user.getPwHash());
			byte[] userPwSalt = convertFromBase64(user.getPwSalt());
			
			boolean success = PasswordEncryptionHelper.authenticate(password,
					userPwHash, userPwSalt);

			if (!success) {
				throw new VocalServiceException(ErrorCode.AUTHENTICATION_FAILED);
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new VocalServiceException(ErrorCode.INTERNAL_ERROR, e);
		}

		return generateSessionId();
	}

	public static void logout() {
	}

	public static User getUserBySessionId(long id) {
		return sessions.get(id);
	}
}
