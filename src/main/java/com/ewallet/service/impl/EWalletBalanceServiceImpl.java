package com.ewallet.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import com.ewallet.model.WalletBalance;
import com.ewallet.service.EWalletBalanceService;

public class EWalletBalanceServiceImpl implements EWalletBalanceService {

	private DataSource dataSource;
	
	public EWalletBalanceServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
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
