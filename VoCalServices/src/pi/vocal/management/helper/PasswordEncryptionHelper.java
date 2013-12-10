package pi.vocal.management.helper;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * This class is used for password encryption and therefore contains hashing and
 * {@code String} conversion functionality.
 * 
 * IMPORTANT NOTE: Most functions of this class were written by Jerry Orr and
 * these functions were marked as such using the "@author" annotation. He
 * published these functions on his blog in 2012:
 * "http://jerryorr.blogspot.de/2012/05/secure-password-storage-lots-of-donts.html"
 * 
 * Thanks to Jerry Orr for publishing this encryption algorithms that work like
 * a charm :-)
 * 
 * JavaDoc comments for his functions were added by me (s3ppl) to have a
 * complete code documentation!
 * 
 * @author s3ppl
 * 
 */
public class PasswordEncryptionHelper {

	/**
	 * Encodes a given {@code String} to his base64 representation.
	 * 
	 * @param input
	 *            The string to encode
	 * @return A base64 representation of the given String
	 * 
	 * @author s3ppl
	 */
	public static String convertToBase64(byte[] input) {
		return DatatypeConverter.printBase64Binary(input);
	}

	/**
	 * Converts a given {@code String}, that is encoded with base64 to a byte
	 * array.
	 * 
	 * @param input
	 *            The string to convert
	 * @return A byte array, representing the given {@code String}.
	 * 
	 * @author s3ppl
	 */
	public static byte[] convertFromBase64(String input) {
		return DatatypeConverter.parseBase64Binary(input);
	}

	/**
	 * Compares the hashed value of the attempted password to the encrypted
	 * password and returns the result as {@code boolean}.
	 * 
	 * @param attemptedPassword
	 *            The password that should be checked for its correctness
	 * @param encryptedPassword
	 *            The encrypted password that's the original
	 * @param salt
	 *            The salt the original password was encrypted with
	 * @return Returns {@code true} if the passwords match; {@code false}
	 *         otherwise
	 * @throws NoSuchAlgorithmException
	 *             Thrown if the encryption of the attemtedPassword fails
	 * @throws InvalidKeySpecException
	 *             Thrown if the encryption of the attemtedPassword fails
	 * 
	 * @author Jerry Orr
	 */
	public static boolean authenticate(String attemptedPassword,
			byte[] encryptedPassword, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Encrypt the clear-text password using the same salt that was used to
		// encrypt the original password
		byte[] encryptedAttemptedPassword = getEncryptedPassword(
				attemptedPassword, salt);

		// Authentication succeeds if encrypted password that the user entered
		// is equal to the stored hash
		return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
	}

	/**
	 * Encrypts a given password with the given salt using the
	 * "PBKDF2WithHmacSHA1" algorithm.
	 * 
	 * @param password
	 *            The password to encrypt
	 * @param salt
	 *            The salt the encryption should use
	 * @return The encrypted password as byte array
	 * @throws NoSuchAlgorithmException
	 *             Thrown if the "PBKDF2WithHmacSHA1" algorithm could not be
	 *             found
	 * @throws InvalidKeySpecException
	 *             Thrown if the creation of the secret key fails
	 * 
	 * @author Jerry Orr
	 */
	public static byte[] getEncryptedPassword(String password, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
		// specifically names SHA-1 as an acceptable hashing algorithm for
		// PBKDF2
		String algorithm = "PBKDF2WithHmacSHA1";
		// SHA-1 generates 160 bit hashes, so that's what makes sense here
		int derivedKeyLength = 160;
		// Pick an iteration count that works for you. The NIST recommends at
		// least 1,000 iterations:
		// http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
		// iOS 4.x reportedly uses 10,000:
		// http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
		int iterations = 20000;

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations,
				derivedKeyLength);

		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

		return f.generateSecret(spec).getEncoded();
	}

	/**
	 * Generates a new salt using the {@code SecureRandom} generator and the
	 * "SHA1PRNG" algorithm.
	 * 
	 * @return A newly created salt as byte array
	 * @throws NoSuchAlgorithmException
	 *             Thrown if the "SHA1PRNG" algorithm could not be found
	 * 
	 * @author Jerry Orr
	 */
	public static byte[] generateSalt() throws NoSuchAlgorithmException {
		// VERY important to use SecureRandom instead of just Random
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

		// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
		byte[] salt = new byte[8];
		random.nextBytes(salt);

		return salt;
	}
}
