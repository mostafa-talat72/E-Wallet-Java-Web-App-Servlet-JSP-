package com.ewallet.service;

import java.sql.SQLException;

import com.ewallet.model.OtpCode;

/**
 * Service that owns the one-time OTP codes: generation persistence, retrieval
 * of the latest usable code for a purpose (wallet activation or PIN reset) and
 * state updates (attempts counter, used / expired flags).
 */
public interface OtpCodeService {

	/**
	 * Persists a newly generated OTP code for a wallet.
	 * @return the stored (usable) code row, or null on failure.
	 */
	OtpCode addOtpCode(OtpCode otpCode) throws SQLException;

	/**
	 * Loads the most recent code of a wallet for a specific purpose
	 * ("ACTIVATION" or "RESET") that is still valid (not used, not expired).
	 * @return the usable code row, or null when there is none.
	 */
	OtpCode getValidOtpCodeByWalletIdAndPurpose(long walletId, String purpose);

	/**
	 * Updates the state of a code (attempts counter, used / expired flags).
	 * @return true when a matching, still-usable code row was updated.
	 */
	boolean updateOtpCodeByWalletIdAndCode(OtpCode otpCode);
}
