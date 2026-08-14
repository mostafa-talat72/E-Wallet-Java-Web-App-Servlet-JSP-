package com.ewallet.service;

import java.sql.SQLException;

import com.ewallet.model.ActivationCode;

/**
 * Service that owns the wallet activation codes: generation persistence,
 * retrieval of the latest usable code and state updates (attempts counter,
 * used / expired flags).
 */
public interface ActivationCodeService {

	/**
	 * Persists a newly generated activation code for a wallet.
	 * @return the stored (usable) code row, or null on failure.
	 */
	ActivationCode addActivationCode(ActivationCode activationCode) throws SQLException;

	/**
	 * Loads the most recent code of a wallet that is still valid
	 * (not used, not expired).
	 * @return the usable code row, or null when there is none.
	 */
	ActivationCode getValidActivationCodeByWalletId(long walletId);

	/**
	 * Updates the state of a code (attempts counter, used / expired flags).
	 * @return true when a matching, still-usable code row was updated.
	 */
	boolean updateActivationCodeByWalletIdAndCode(ActivationCode activationCode);
}