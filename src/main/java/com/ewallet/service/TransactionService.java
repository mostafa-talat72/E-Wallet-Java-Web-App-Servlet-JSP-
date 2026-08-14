package com.ewallet.service;

import java.util.List;

import com.ewallet.model.Transaction;

/**
 * Service that owns transaction records: recording money movements between
 * accounts and reading the history of a given account.
 */
public interface TransactionService {
	/**
	 * Persists a single transaction (sender, receiver, type, status, amount, fees...).
	 * @return true when the row was inserted.
	 */
	boolean addTransaction(Transaction transaction);
	
	/**
	 * Reads the full history of an account, both as sender and as receiver, newest first.
	 * @return the transaction list (empty when there is no history).
	 */
	List<Transaction> getAllTransactions(long accountId);	
	
}
