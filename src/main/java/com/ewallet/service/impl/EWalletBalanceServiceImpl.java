package com.ewallet.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import com.ewallet.model.WalletBalance;
import com.ewallet.service.EWalletBalanceService;

/**
 * JDBC implementation of {@link EWalletBalanceService} backed by the "wallet_balances"
 * table, which stores the available and held (locked) amounts of every wallet. SQL
 * errors surface as RuntimeExceptions because this interface declares no checked
 * exceptions.
 */
public class EWalletBalanceServiceImpl implements EWalletBalanceService {

	private DataSource dataSource;
	
	public EWalletBalanceServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Inserts a zeroed "wallet_balances" row for the given wallet, then reloads it so
	 * the caller gets the row exactly as stored (defaults applied by the database).
	 */
	@Override
	public WalletBalance createWalletBalance(WalletBalance walletBalance) {
		String query = "INSERT INTO wallet_balances (wallet_id)"
				+ " VALUES (?)";
		
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, walletBalance.getWalletId());
			preparedStatement.execute();
			walletBalance = getWalletBalanceByWalletId(walletBalance.getWalletId());
			return walletBalance;
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Writes the new available and held balances of a wallet into "wallet_balances"
	 * and stamps updated_at, then reloads the row.
	 * @return the refreshed balance row, or null when the wallet has no balance row.
	 */
	@Override
	public WalletBalance updateWalletBalance(WalletBalance walletBalance) {
		String query = "Update wallet_balances set available_balance = ?, held_balance = ?, updated_at = ? Where wallet_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)){
			preparedStatement.setBigDecimal(1, walletBalance.getAvailableBalance());
			preparedStatement.setBigDecimal(2, walletBalance.getHeldBalance());
			preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setLong(4, walletBalance.getWalletId());
			if(preparedStatement.executeUpdate() > 0)
				return getWalletBalanceByWalletId(walletBalance.getWalletId());
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * Selects the "wallet_balances" row of a wallet.
	 * @return the balance row, or null when the wallet has none.
	 */
	@Override
	public WalletBalance getWalletBalanceByWalletId(Long walletId) {
		String query = "SELECT * FROM wallet_balances WHERE wallet_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, walletId);
			var resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				return new WalletBalance(
					resultSet.getLong("wallet_id"),
					resultSet.getBigDecimal("available_balance"),
					resultSet.getBigDecimal("held_balance"),
					resultSet.getTimestamp("updated_at")
				);
			}
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * Deletes the "wallet_balances" row of a wallet (used when the wallet is closed).
	 * @return true when a row was deleted.
	 */
	@Override
	public boolean deleteWalletBalanceByWalletId(Long walletId) {
		String query = "DELETE FROM wallet_balances WHERE wallet_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, walletId);
			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0;
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
