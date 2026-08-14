package com.ewallet.service.impl;

import java.sql.*;

import com.ewallet.model.Wallet;
import com.ewallet.service.EWalletUserService;
import com.ewallet.util.PinUtil;

import javax.sql.DataSource;

public class EWalletUserServiceImpl implements EWalletUserService {
	
	
	private DataSource dataSource;
	
	public EWalletUserServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Wallet signup(Wallet wallet) throws SQLException {
		String salt = PinUtil.generateSalt();
		String pinHash = PinUtil.hash(wallet.getPinHash(), salt);
		String query = "INSERT INTO wallets (phone_number, national_id,full_name, pin_hash, salt)"
				+ " VALUES (?, ?, ?, ?, ?)";
		
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, wallet.getPhoneNumber());
			preparedStatement.setString(2, wallet.getNationalId());
			preparedStatement.setString(3, wallet.getFullName());
			preparedStatement.setString(4, pinHash);
			preparedStatement.setString(5, salt);
			
			preparedStatement.execute();
			
			wallet = getUserWalletByPhoneNumber(wallet.getPhoneNumber());
			return wallet;
			
		}catch(SQLException e) {
			throw e;
		}
		
	}

	@Override
	public Wallet login(Wallet wallet)  throws SQLException {
		Wallet found = getUserWalletByPhoneNumber(wallet.getPhoneNumber());
		if (found == null) {
			return null;
		}

		String storedHash = found.getPinHash();
		String storedSalt = found.getSalt();

		if (!PinUtil.hash(wallet.getPinHash(), storedSalt).equals(storedHash)) {
			return null;
		}
		return found;
	}

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

	@Override
	public Wallet updateUserWalletPin(Wallet wallet, String newPin) throws SQLException {
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

	@Override
	public boolean deleteUserWallet(Wallet wallet, Wallet deletedWallet)  throws SQLException {
		// atomic cascade: remove everything that references the wallet
		// (transaction codes, balance, cards) then the wallet row itself.
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

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

			int rowsAffected;
			String query = "DELETE FROM wallets WHERE phone_number = ? AND pin_hash = ? AND wallet_id = ?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, deletedWallet.getPhoneNumber());
				preparedStatement.setString(2, deletedWallet.getPinHash());
				preparedStatement.setLong(3, wallet.getWalletId());
				rowsAffected = preparedStatement.executeUpdate();
			}
			if (rowsAffected == 0) {
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
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

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
