package com.ewallet.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.ewallet.model.ActivationCode;
import com.ewallet.service.ActivationCodeService;

/**
 * JDBC implementation of {@link ActivationCodeService} backed by the
 * "activation_codes" table, which stores the 6-digit codes issued to a new
 * wallet for phone-ownership verification (code, expiry, attempts and
 * used/expired flags).
 */
public class ActivationCodeServiceImpl implements ActivationCodeService {

	private DataSource dataSource;

	public ActivationCodeServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Inserts a newly generated code into "activation_codes", then reloads the
	 * wallet's latest usable code (of the same purpose) so the caller receives
	 * the row fully populated.
	 */
	@Override
	public ActivationCode addActivationCode(ActivationCode activationCode) throws SQLException {
		String query = "INSERT INTO activation_codes (wallet_id, code, purpose) VALUES (?, ?, ?) ";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, activationCode.getWalletId());
			preparedStatement.setString(2, activationCode.getCode());
			preparedStatement.setString(3, activationCode.getPurpose());
			preparedStatement.execute();
			return getValidActivationCodeByWalletIdAndPurpose(
					activationCode.getWalletId(), activationCode.getPurpose());
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Selects the most recent "ACTIVATION" code of a wallet that is still
	 * usable — see {@link #getValidActivationCodeByWalletIdAndPurpose}.
	 * @return the usable code row, or null when there is none.
	 */
	@Override
	public ActivationCode getValidActivationCodeByWalletId(long walletId) {
		return getValidActivationCodeByWalletIdAndPurpose(walletId, "ACTIVATION");
	}

	/**
	 * Selects the most recent "activation_codes" row of a wallet for a specific
	 * purpose ("ACTIVATION" or "RESET") that is still usable (not used, not
	 * expired, and with its 10-minute window still open). Expiry is decided by
	 * the database clock ({@code expires_at > CURRENT_TIMESTAMP}) on purpose:
	 * the DB and the JVM can run on different timezones, so comparing the stored
	 * timestamp against {@code System.currentTimeMillis()} would be wrong.
	 * Only the newest usable code of that purpose is ever returned.
	 * @return the usable code row, or null when there is none.
	 */
	@Override
	public ActivationCode getValidActivationCodeByWalletIdAndPurpose(long walletId, String purpose) {
		String query = "SELECT * FROM activation_codes WHERE wallet_id = ? AND purpose = ? AND is_used = 0 AND is_Expire = 0 AND expires_at > CURRENT_TIMESTAMP ORDER BY created_at DESC";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, walletId);
			preparedStatement.setString(2, purpose);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return new ActivationCode(
						resultSet.getLong("code_id"),
						resultSet.getLong("wallet_id"),
						resultSet.getString("code"),
						resultSet.getString("purpose"),
						resultSet.getTimestamp("created_at"),
						resultSet.getTimestamp("expires_at"),
						resultSet.getInt("attempts"),
						resultSet.getInt("is_used"),
						resultSet.getInt("is_expire"));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * Updates the state of a code in "activation_codes" (attempts counter and
	 * the used / expired flags), but only while the code is still usable; once
	 * a code has been consumed or expired the update is ignored.
	 * @return true when a matching usable code row was updated.
	 */
	@Override
	public boolean updateActivationCodeByWalletIdAndCode(ActivationCode activationCode) {
		String query = "UPDATE activation_codes SET attempts = ?, is_used = ?, is_Expire = ? WHERE wallet_id = ? AND code = ? AND (is_used = 0 OR is_Expire = 0)";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, activationCode.getAttempts());
			preparedStatement.setInt(2, activationCode.getIsUsed());
			preparedStatement.setInt(3, activationCode.getIsExpire());
			preparedStatement.setLong(4, activationCode.getWalletId());
			preparedStatement.setString(5, activationCode.getCode());

			return preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}