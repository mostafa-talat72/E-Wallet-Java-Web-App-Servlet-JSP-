package com.ewallet.service.impl;

import java.sql.*;

import com.ewallet.model.Wallet;
import com.ewallet.service.EWalletUserService;
import com.ewallet.util.PinUtil;

import javax.sql.DataSource;

/**
 * JDBC implementation of {@link EWalletUserService} backed by the "wallets" table.
 * <p>
 * PIN security: the raw PIN is never stored or logged. Every PIN change generates a
 * per-user random salt (via {@link PinUtil#generateSalt()}) and stores
 * {@code SHA-256(pin + salt)} in the pin_hash column next to the salt, so identical
 * PINs produce different hashes across users. Login re-hashes the supplied PIN with
 * the stored salt and compares against the stored hash.
 * <p>
 * deleteUserWallet performs an atomic cascade delete: all rows that reference the
 * wallet (transaction_codes, wallet_balances, cards) plus the wallet row itself are
 * removed inside a single transaction, which is rolled back entirely if the wallet
 * row cannot be deleted (e.g. wrong credentials).
 */
public class EWalletUserServiceImpl implements EWalletUserService {
	
	
	private DataSource dataSource;
	
	public EWalletUserServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Inserts a new row into "wallets" with a freshly salted SHA-256 hash of the PIN,
	 * then reloads the row by phone number to return the complete stored wallet.
	 */
	@Override
	public Wallet signup(Wallet wallet) throws SQLException {
		// Salt is generated per user so two wallets with the same PIN never share a hash.
		String salt = PinUtil.generateSalt();
		String pinHash = PinUtil.hash(wallet.getPinHash(), salt);
		String query = "INSERT INTO wallets (phone_number, national_id,full_name, pin_hash, salt)"
				+ " VALUES (?, ?, ?, ?, ?)";
		
		// try-with-resources guarantees the connection and statement are closed on every path.
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, wallet.getPhoneNumber());
			preparedStatement.setString(2, wallet.getNationalId());
			preparedStatement.setString(3, wallet.getFullName());
			preparedStatement.setString(4, pinHash);
			preparedStatement.setString(5, salt);
			
			preparedStatement.execute();
			
			// Reload so the caller receives the row exactly as persisted (ids, timestamps, defaults).
			wallet = getUserWalletByPhoneNumber(wallet.getPhoneNumber());
			return wallet;
			
		}catch(SQLException e) {
			throw e;
		}
		
	}

	/**
	 * Authenticates against the "wallets" table: resolves the wallet by phone number,
	 * then verifies the supplied PIN by re-hashing it with the stored per-user salt.
	 */
	@Override
	public Wallet login(Wallet wallet)  throws SQLException {
		Wallet found = getUserWalletByPhoneNumber(wallet.getPhoneNumber());
		if (found == null) {
			// Unknown phone number: fail closed with no hint about which credential was wrong.
			return null;
		}

		String storedHash = found.getPinHash();
		String storedSalt = found.getSalt();

		if (!PinUtil.hash(wallet.getPinHash(), storedSalt).equals(storedHash)) {
			// Wrong PIN: the hash of (pin + storedSalt) does not match the stored hash.
			return null;
		}
		return found;
	}

	/**
	 * Updates the editable profile columns of a "wallets" row (full_name, pin_hash,
	 * salt) and stamps updated_at, then reloads the row by id.
	 */
	@Override
	public Wallet updateUserWallet(Wallet wallet) throws SQLException {
		String query = "UPDATE wallets SET full_name = ?, pin_hash = ?, salt = ? , updated_at = ? WHERE wallet_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, wallet.getFullName());
			preparedStatement.setString(2, wallet.getPinHash());
			preparedStatement.setString(3, wallet.getSalt());

			preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setLong(5, wallet.getWalletId());
			
			int rowsAffected = preparedStatement.executeUpdate();

			if(rowsAffected > 0) {
				return getUserWalletById(wallet.getWalletId());
			}
		}catch(SQLException e) {
			throw e;
		}
		
		return null;
	}

	/**
	 * Rotates the PIN of a "wallets" row: generates a new per-user salt, stores the
	 * salted SHA-256 hash of the new PIN alongside it and stamps updated_at.
	 */
	@Override
	public Wallet updateUserWalletPin(Wallet wallet, String newPin) throws SQLException {
		// New salt on every change, so the hash never leaks whether the PIN was reused.
		String salt = PinUtil.generateSalt();
		String pinHash = PinUtil.hash(newPin, salt);
		String query = "UPDATE wallets SET pin_hash = ?, salt = ?, updated_at = ? WHERE wallet_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, pinHash);
			preparedStatement.setString(2, salt);
			preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setLong(4, wallet.getWalletId());

			int rowsAffected = preparedStatement.executeUpdate();

			if(rowsAffected > 0) {
				return getUserWalletById(wallet.getWalletId());
			}
		}catch(SQLException e) {
			throw e;
		}

		return null;
	}

	/**
	 * Atomically removes a wallet: deletes every dependent row (transaction_codes,
	 * wallet_balances, cards), marks the wallet's account row as disabled instead of
	 * deleting it (keeps transaction history), then deletes the "wallets" row itself —
	 * all within one transaction whose commit is skipped and rolled back if the final
	 * DELETE matches no row (credentials in deletedWallet did not verify).
	 */
	@Override
	public boolean deleteUserWallet(Wallet wallet, Wallet deletedWallet)  throws SQLException {
		// atomic cascade: remove everything that references the wallet
		// (transaction codes, balance, cards) then the wallet row itself.
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			// Child rows are deleted one table at a time, always scoped by wallet_id.
			try (PreparedStatement ps = connection.prepareStatement("DELETE FROM transaction_codes WHERE wallet_id = ?")) {
				ps.setLong(1, wallet.getWalletId());
				ps.executeUpdate();
			}
			try (PreparedStatement ps = connection.prepareStatement("DELETE FROM wallet_balances WHERE wallet_id = ?")) {
				ps.setLong(1, wallet.getWalletId());
				ps.executeUpdate();
			}
			try (PreparedStatement ps = connection.prepareStatement("DELETE FROM cards WHERE wallet_id = ?")) {
				ps.setLong(1, wallet.getWalletId());
				ps.executeUpdate();
			}
			// keep the wallet account row for transaction history, just disable it
			try (PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET status = 0 WHERE reference_id = ? AND account_type_id = 1")) {
				ps.setLong(1, wallet.getWalletId());
				ps.executeUpdate();
			}

			// The DELETE has a WHERE clause that re-verifies the supplied credentials:
			// falling through with rowsAffected == 0 means nothing was removed.
			int rowsAffected;
			String query = "DELETE FROM wallets WHERE phone_number = ? AND pin_hash = ? AND wallet_id = ?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, deletedWallet.getPhoneNumber());
				preparedStatement.setString(2, deletedWallet.getPinHash());
				preparedStatement.setLong(3, wallet.getWalletId());
				rowsAffected = preparedStatement.executeUpdate();
			}
			if (rowsAffected == 0) {
				// Credentials did not match: undo all the child deletes performed above.
				connection.rollback();
				return false;
			}

			connection.commit();
			return true;
		}catch(SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			throw e;
		} finally {
			// Always release the connection back to the pool, even on failure.
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Selects a single row from "wallets" by its primary key and maps it to a Wallet.
	 * @return the wallet, or null when the id does not exist.
	 */
	@Override
	public Wallet getUserWalletById(long id) throws SQLException {
String query = "SELECT * FROM wallets WHERE wallet_id = ?";
		
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, id);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				return new Wallet(
					resultSet.getLong("wallet_id"),
					resultSet.getString("phone_number"),
					resultSet.getString("national_id"),
					resultSet.getString("full_name"),
					resultSet.getString("pin_hash"),
					resultSet.getString("salt"),
					resultSet.getInt("status"),
					resultSet.getTimestamp("created_at"),
					resultSet.getTimestamp("updated_at")
				);
			}
			
		}catch(SQLException e) {
			throw e;
		}
		
		return null;
	}

	/**
	 * Selects a single row from "wallets" by its unique phone number and maps it to a Wallet.
	 * @return the wallet, or null when the number is not registered.
	 */
	@Override
	public Wallet getUserWalletByPhoneNumber(String phoneNumber) throws SQLException {
		String query = "SELECT * FROM wallets WHERE phone_number = ?";
		
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, phoneNumber);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				return new Wallet(
					resultSet.getLong("wallet_id"),
					resultSet.getString("phone_number"),
					resultSet.getString("national_id"),
					resultSet.getString("full_name"),
					resultSet.getString("pin_hash"),
					resultSet.getString("salt"),
					resultSet.getInt("status"),
					resultSet.getTimestamp("created_at"),
					resultSet.getTimestamp("updated_at")
				);
			}
			
		}catch(SQLException e) {
			throw e;
		}
		
		return null;
	}

}
