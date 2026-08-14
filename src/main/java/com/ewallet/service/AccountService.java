package com.ewallet.service;

import com.ewallet.model.Account;

/**
 * Service that owns the accounts table, the generic linkage rows that bind a
 * domain entity (wallet, card, ...) to account types used by the transaction engine.
 */
public interface AccountService {
	/**
	 * Creates an account row linking an account type to a reference entity.
	 * @return true when the row was inserted.
	 */
	boolean addAcount(Account account);
	
	/**
	 * Disables (status = 0) the account identified by its primary key.
	 * @return true when a row was updated.
	 */
	boolean updateAccountStatusByAccountId(long accountId);
	
	/**
	 * Disables (status = 0) the account matching a reference entity and account type.
	 * @return true when a row was updated.
	 */
	boolean updateAccountStatusByRefereceIdAndTypeId(long referenceId, int accountTypeId);

	/**
	 * Loads the account that links a reference entity to an account type.
	 * @return the account row, or null if none matches.
	 */
	Account getAccountByRefereceIdAndTypeId(long referenceId, int accountTypeId);
	/**
	 * Loads an account by its primary key.
	 * @return the account row, or null if it does not exist.
	 */
	Account getAccountByAccountId(long accountId);
}
