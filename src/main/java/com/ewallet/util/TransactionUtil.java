package com.ewallet.util;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates the one-time passcodes (OTP) used to authorize transactions
 * and the 6-digit codes used to activate new wallets.
 */
public class TransactionUtil {
	/**
	 * Returns a random 9-digit code. ThreadLocalRandom is used for
	 * thread-safety and performance; the bound [100_000_000, 1_000_000_000)
	 * guarantees the result always has exactly 9 digits.
	 */
	public static String generateTransactionCode() {
		  long code = ThreadLocalRandom.current()
		            .nextLong(100_000_000L, 1_000_000_000L);

		    return Long.toString(code);
	}

	/**
	 * Returns a random 6-digit OTP code (used for wallet activation and
	 * PIN-reset codes). ThreadLocalRandom is used for thread-safety and
	 * performance; the bound [100_000, 1_000_000) guarantees the result always
	 * has exactly 6 digits.
	 */
	public static String generateOtpCode() {
		long code = ThreadLocalRandom.current()
		            .nextLong(100_000L, 1_000_000L);

	    return Long.toString(code);
	}
}
