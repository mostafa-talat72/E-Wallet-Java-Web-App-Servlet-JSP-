package com.ewallet.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.ewallet.model.Transaction;
import com.ewallet.service.TransactionService;

/**
 * JDBC implementation of {@link TransactionService} backed by the "transactions"
 * table, which stores every money movement between accounts (type, status, amount,
 * fees, reference number) together with sender / receiver account ids.
 */
public class TransactionServiceImpl implements TransactionService {
	
	private DataSource dataSource;
	
	public TransactionServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Inserts a single row into "transactions" describing one money movement.
	 * @return true when the row was inserted.
	 */
	@Override
	public boolean addTransaction(Transaction transaction) {
		String query = "Insert into transactions (from_account_id, to_account_id, transaction_type_id, transaction_status_id, amount, fees, reference_number, description) " + 
				"Values(?,?,?,?,?,?,?,?)";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);){
			preparedStatement.setLong(1, transaction.getFromAccountId());
			preparedStatement.setLong(2, transaction.getToAccountId());
			preparedStatement.setLong(3, transaction.getTransactionTypeId());
			preparedStatement.setLong(4, transaction.getTransactionStatusId());
			preparedStatement.setBigDecimal(5, transaction.getAmount());
			preparedStatement.setBigDecimal(6, transaction.getFees());
			preparedStatement.setString(7, transaction.getReferenceNumber());
			preparedStatement.setString(8, transaction.getDescription());

			 preparedStatement.execute();
			 return true;
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Selects the "transactions" rows in which the account appears either as sender or
	 * as receiver, ordered newest first, and maps each to a Transaction.
	 * @return the account's transaction history, or an empty list when there is none.
	 */
	@Override
	public List<Transaction> getAllTransactions(long accountId) {
		String query = "Select * from transactions where from_account_id = ? OR to_account_id = ? order by created_at desc";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);){
			
			preparedStatement.setLong(1, accountId);
			preparedStatement.setLong(2, accountId);
			ResultSet resultSet = preparedStatement.executeQuery();
			List<Transaction> transactions = new ArrayList<Transaction>();
			
			while(resultSet.next()) {
				Transaction transaction = new Transaction(
						resultSet.getLong("transaction_id"),
						resultSet.getLong("from_account_id"),
						resultSet.getLong("to_account_id"),
						resultSet.getLong("transaction_type_id"),
						resultSet.getLong("transaction_status_id"),
						resultSet.getBigDecimal("amount"),
						resultSet.getBigDecimal("fees"),
						resultSet.getString("reference_number"),
						resultSet.getString("description"),
						resultSet.getTimestamp("created_at"),
						resultSet.getTimestamp("updated_at")
						);
				transactions.add(transaction);
			}
			return transactions;
			
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	
}
