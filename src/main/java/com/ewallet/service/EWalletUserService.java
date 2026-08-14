package com.ewallet.service;

import java.sql.SQLException;

import com.ewallet.model.Wallet;

/**
 * Service that owns the wallet lifecycle: account signup, login (PIN verification),
 * profile updates, PIN rotation, account deletion and wallet lookups.
 */
public interface EWalletUserService {
	
	/**
	 * Registers a new wallet by hashing its PIN and persisting the wallet row.
	 * @return the fully populated wallet as stored, or null if registration failed.
	 */
	Wallet signup(Wallet wallet)throws SQLException;
	
	/**
	 * Authenticates a wallet by phone number and by verifying the supplied PIN against the stored hash.
	 * @return the matching wallet on success, null when the phone number is unknown or the PIN is wrong.
	 */
	Wallet login(Wallet wallet)throws SQLException;
	
	/**
	 * Updates the wallet profile fields (full name, PIN hash and salt) for the given wallet.
	 * @return the refreshed wallet row, or null if no row matched.
	 */
	Wallet updateUserWallet(Wallet wallet)throws SQLException;
	
	/**
	 * Rotates the wallet PIN: generates a fresh salt, hashes the new PIN and stores both.
	 * @return the refreshed wallet row, or null if no row matched.
	 */
	Wallet updateUserWalletPin(Wallet wallet, String newPin)throws SQLException;
	
	/**
	 * Activates a previously inactive wallet (status 0 -> 1) once the owner
	 * proves the phone number with a valid activation code.
	 * @return the refreshed wallet row, or null if no row matched.
	 */
	Wallet activateWallet(Wallet wallet)throws SQLException;
	
	/**
	 * Deletes a wallet together with all dependent rows (transaction codes, balance, cards)
	 * in a single transaction, after verifying the credentials carried by deletedWallet.
	 * @return true when the wallet was removed, false when the credentials did not match.
	 */
	boolean deleteUserWallet(Wallet wallet, Wallet deletedWallet)throws SQLException;
	
	/**
	 * Loads a wallet by its primary key.
	 * @return the wallet row, or null if it does not exist.
	 */
	Wallet getUserWalletById(long id)throws SQLException;
	
	/**
	 * Loads a wallet by its unique phone number.
	 * @return the wallet row, or null if it does not exist.
	 */
	Wallet getUserWalletByPhoneNumber(String phoneNumber)throws SQLException;
	

}
