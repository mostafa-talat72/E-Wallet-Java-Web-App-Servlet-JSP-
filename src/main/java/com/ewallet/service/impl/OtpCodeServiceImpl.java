package com.ewallet.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.ewallet.model.OtpCode;
import com.ewallet.service.OtpCodeService;

/**
 * JDBC implementation of {@link OtpCodeService} backed by the "otp_codes"
 * table, which stores the 6-digit one-time codes issued to a wallet for
 * phone-ownership verification (wallet activation or PIN reset), including
 * code, purpose, expiry, attempts and used/expired flags.
 */
public class OtpCodeServiceImpl implements OtpCodeService {

	private DataSource dataSource;

	public OtpCodeServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Inserts a newly generated code into "otp_codes", then reloads the
	 * wallet's latest usable code (of the same purpose) so the caller receives
	 * the row fully populated.
	 */
	@Override
	public OtpCode addOtpCode(OtpCode otpCode) throws SQLException {
		String query = "INSERT INTO otp_codes (wallet_id, code, purpose) VALUES (?, ?, ?) ";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, otpCode.getWalletId());
			preparedStatement.setString(2, otpCode.getCode());
			preparedStatement.setString(3, otpCode.getPurpose());
			preparedStatement.execute();
			return getValidOtpCodeByWalletIdAndPurpose(
					otpCode.getWalletId(), otpCode.getPurpose());
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Selects the most recent "otp_codes" row of a wallet for a specific
	 * purpose ("ACTIVATION" or "RESET") that is still usable (not used, not
	 * expired, and with its 10-minute window still open). Expiry is decided by
	 * the database clock ({@code expires_at > CURRENT_TIMESTAMP}) on purpose:
	 * the DB and the JVM can run on different timezones, so comparing the stored
	 * timestamp against {@code System.currentTimeMillis()} would be wrong.
	 * Only the newest usable code of that purpose is ever returned.
	 * @return the usable code row, or null when there is none.
	 */
	@Override
	public OtpCode getValidOtpCodeByWalletIdAndPurpose(long walletId, String purpose) {
		String query = "SELECT * FROM otp_codes WHERE wallet_id = ? AND purpose = ? AND is_used = 0 AND is_Expire = 0 AND expires_at > CURRENT_TIMESTAMP ORDER BY created_at DESC";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, walletId);
			preparedStatement.setString(2, purpose);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return new OtpCode(
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
	 * Updates the state of a code in "otp_codes" (attempts counter and the
	 * used / expired flags), but only while the code is still usable; once
	 * a code has been consumed or expired the update is ignored.
	 * @return true when a matching usable code row was updated.
	 */
	@Override
	public boolean updateOtpCodeByWalletIdAndCode(OtpCode otpCode) {
		String query = "UPDATE otp_codes SET attempts = ?, is_used = ?, is_Expire = ? WHERE wallet_id = ? AND code = ? AND (is_used = 0 OR is_Expire = 0)";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, otpCode.getAttempts());
			preparedStatement.setInt(2, otpCode.getIsUsed());
			preparedStatement.setInt(3, otpCode.getIsExpire());
			preparedStatement.setLong(4, otpCode.getWalletId());
			preparedStatement.setString(5, otpCode.getCode());

			return preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
