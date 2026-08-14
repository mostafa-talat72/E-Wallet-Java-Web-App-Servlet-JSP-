package com.ewallet.util;
import java.util.concurrent.ThreadLocalRandom;

public class TransactionUtil {
	public static String generateTransactionCode() {
		  long code = ThreadLocalRandom.current()
		            .nextLong(100_000_000L, 1_000_000_000L);

		    return Long.toString(code);
	}
}
