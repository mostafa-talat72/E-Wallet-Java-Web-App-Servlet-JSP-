package com.ewallet.service;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.ewallet.model.TransactionCode;

/**
 * Service that owns the transaction (OTP) codes used to authorize money movements
 * from a wallet: code generation, retrieval of the latest usable code and state updates.
 */
public interface TransactionCodeService {

	/**
	 * Persists a newly generated transaction code for a wallet.
	 * @return the stored (usable) code row, or null on failure.
	 */
	TransactionCode addTransactionCode(TransactionCode transactionCode) throws SQLException;

	/**
	 * Loads the most recent code of a wallet that is still valid (not used, not expired).
	 * @return the usable code row, or null when there is none.
	 */
	TransactionCode getValidTransactionCodeByWalletIdAndCode(long walletIde) ;

	/**
	 * Updates the state of a code (attempts counter, used / expired flags).
	 * @return true when a matching, still-usable code row was updated.
	 */
	boolean updateTransactionCodeByWalletIdAndCode(TransactionCode transactionCode);

	
}
