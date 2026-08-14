package com.ewallet.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * PIN hashing utilities. A PIN is never stored in plain text: a random salt is
 * generated per wallet and the stored value is the SHA-256 digest of
 * "salt:pin", hex-encoded.
 */
public class PinUtil {

	/** Cryptographically strong random source for salt generation. */
	private static final SecureRandom RANDOM = new SecureRandom();

	/** Utility class: prevents instantiation. */
	private PinUtil() {
	}

	/**
	 * Generates a 16-byte random salt from SecureRandom and hex-encodes it to
	 * 32 lowercase hex characters.
	 */
	public static String generateSalt() {
		byte[] bytes = new byte[16];
		RANDOM.nextBytes(bytes);
		StringBuilder sb = new StringBuilder();
		// Hex-encode each byte as two lowercase hex digits (e.g. "1a")
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	/**
	 * Computes the SHA-256 digest over "salt:pin" (UTF-8) and returns it as a
	 * lowercase hex string. The per-wallet salt makes two equal PINs produce
	 * different digests. SHA-256 is always present in the JRE, so a failure to
	 * load it is wrapped as an unchecked exception.
	 */
	public static String hash(String pin, String salt) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			// Digest input: salt + ":" + pin, encoded as UTF-8 bytes
			byte[] hashed = digest.digest((salt + ":" + pin).getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			// Hex-encode the digest bytes, two hex characters per byte
			for (byte b : hashed) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}