package com.ewallet.service;

import java.util.List;

import com.ewallet.model.Transaction;

public interface TransactionService {
	boolean addTransaction(Transaction transaction);
	
	List<Transaction> getAllTransactions(long accountId);	
	
}
