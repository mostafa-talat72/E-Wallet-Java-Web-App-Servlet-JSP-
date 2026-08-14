package com.ewallet.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import com.ewallet.model.Account;
import com.ewallet.service.AccountService;

public class AccountServiceImpl implements AccountService {
	
	private DataSource dataSource;
	
	public AccountServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean addAcount(Account account) {
		String query = "INSERT INTO accounts (account_type_id, reference_id)"
				+ " VALUES (?, ?)";
		
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1,account.getAccountTypeId());
			preparedStatement.setLong(2, account.getReferenceId());
			preparedStatement.execute();
			return true;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
			
	}

	@Override
	public boolean updateAccountStatusByAccountId(long accountId) {
		String query = "Update accounts set status = 0 WHERE account_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, accountId);
			return preparedStatement.executeUpdate() > 0;	
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean updateAccountStatusByRefereceIdAndTypeId(long referenceId, int accountTypeId) {
		String query = "Update accounts set status = 0 WHERE reference_id = ? AND account_type_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, referenceId);
			preparedStatement.setInt(2, accountTypeId);
			return preparedStatement.executeUpdate() > 0;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Account getAccountByRefereceIdAndTypeId(long referenceId, int accountTypeId) {
		String query = "Select * from accounts WHERE reference_id = ? AND account_type_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, referenceId);
			preparedStatement.setInt(2, accountTypeId);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				return new Account(
						resultSet.getLong("account_id"),
						resultSet.getInt("account_type_id"),
						resultSet.getLong("reference_id"),
						resultSet.getInt("status")
						);
			}
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	@Override
	public Account getAccountByAccountId(long accountId) {
		String query = "Select * from accounts WHERE account_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, accountId);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				return new Account(
						resultSet.getLong("account_id"),
						resultSet.getInt("account_type_id"),
						resultSet.getLong("reference_id"),
						resultSet.getInt("status")
						);
			}
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

}
