package com.ewallet.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

import javax.sql.DataSource;

import com.ewallet.model.TransactionCode;
import com.ewallet.service.TransactionCodeService;

public class TransactionCodeServiceImpl implements TransactionCodeService {

	private DataSource dataSource;
	private Random random = new Random();

	public TransactionCodeServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public TransactionCode addTransactionCode(TransactionCode transactionCode) throws SQLException {
		String query = "INSERT INTO transaction_codes (wallet_id, code, amount) VALUES (?, ?, ?) ";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, transactionCode.getWalletId());
			preparedStatement.setString(2, transactionCode.getCode());
			preparedStatement.setBigDecimal(3, transactionCode.getAmount());
			preparedStatement.execute();
			return getValidTransactionCodeByWalletIdAndCode(transactionCode.getWalletId());
		} catch (SQLException e) {
			throw e;
		}
	}

	@Override
	public TransactionCode getValidTransactionCodeByWalletIdAndCode(long walletId) {
		String query = "SELECT * FROM transaction_codes WHERE wallet_id = ? AND is_used = 0 And is_Expire = 0 order by created_at desc";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, walletId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return new TransactionCode(
						resultSet.getLong("code_id"),
						resultSet.getLong("wallet_id"),
						resultSet.getString("code"),
						resultSet.getBigDecimal("amount"),
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

	@Override
	public boolean updateTransactionCodeByWalletIdAndCode(TransactionCode transactionCode) {
		String query = "UPDATE transaction_codes SET attempts = ?, is_used = ?, is_Expire = ? WHERE wallet_id = ? AND code = ? AND (is_used = 0 OR is_Expire = 0)";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, transactionCode.getAttempts());
			preparedStatement.setInt(2, transactionCode.getIsUsed());
			preparedStatement.setInt(3, transactionCode.getIsExpire());
			preparedStatement.setLong(4, transactionCode.getWalletId());
			preparedStatement.setString(5, transactionCode.getCode());

			return preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	
}
