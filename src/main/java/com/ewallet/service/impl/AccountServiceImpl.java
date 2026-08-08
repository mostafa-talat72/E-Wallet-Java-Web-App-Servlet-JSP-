package com.ewallet.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
	public boolean deleteAccountByAccountId(long accountId) {
		String query = "DELETE FROM accounts WHERE account_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, accountId);
			return preparedStatement.executeUpdate() > 0;	
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean deleteAccountByRefereceIdAndTypeId(long referenceId, int accountTypeId) {
		String query = "DELETE FROM accounts WHERE reference_id = ? AND account_type_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, referenceId);
			preparedStatement.setInt(2, accountTypeId);
			return preparedStatement.executeUpdate() > 0;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
