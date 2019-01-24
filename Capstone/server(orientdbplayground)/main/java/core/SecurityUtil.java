package core;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * A simple security class for generating salts and hashing passwords. Implements Java's native
 * PBKDF2WithHmacSHA1 algorithm.
 */
public class SecurityUtil {

  private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
  private static final int BYTES = 256;
  private static final int ITERATIONS = 65536;

  /**
   * Generates a random salt. Intended for use with {@link SecurityUtil#hash(String, byte[])}.
   *
   * @param size Number of bytes to generate.
   * @return A randomly-generated salt of the specified size.
   */
  public static byte[] salt(int size) {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[size];
    random.nextBytes(salt);
    return salt;
  }

  /**
   * Generates a secure hash of the provided password and salt using {@link SecurityUtil#ALGORITHM}.
   * Implements {@link SecurityUtil#ITERATIONS}.
   *
   * @param password Plaintext password to be hashed.
   * @param salt Salt to be combined with the password and hashed.
   * @return A hash of size {@link SecurityUtil#BYTES} derived from the provided password and salt.
   */
  public static byte[] hash(String password, byte[] salt) {
    byte[] result = null;
    try {
      KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, BYTES);
      SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
      result =  factory.generateSecret(spec).getEncoded();
    } catch (Exception e) {
      e.printStackTrace(); // Should never happen.
    }
    return result;
  }
}