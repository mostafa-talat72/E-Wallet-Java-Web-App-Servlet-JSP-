package com.ewallet.service;

import com.ewallet.model.WalletBalance;

/**
 * Service that owns the wallet balance records: creation, updates of the
 * available/held balances and lookups keyed by wallet id.
 */
public interface EWalletBalanceService {
	
	/**
	 * Inserts a new balance row for the given wallet (initialized to zero).
	 * @return the stored balance row, or null if insertion failed.
	 */
	WalletBalance createWalletBalance(WalletBalance walletBalance);
	
	/**
	 * Refreshes the available and held balances of a wallet and stamps the updated_at column.
	 * @return the refreshed balance row, or null if no row matched.
	 */
	WalletBalance updateWalletBalance(WalletBalance walletBalance);
	
	/**
	 * Reads the balance row belonging to a wallet.
	 * @return the balance row, or null if the wallet has no balance record.
	 */
	WalletBalance getWalletBalanceByWalletId(Long walletId);
	
	/**
	 * Deletes the balance row of a wallet.
	 * @return true when a row was deleted.
	 */
	boolean deleteWalletBalanceByWalletId(Long walletId);
}
