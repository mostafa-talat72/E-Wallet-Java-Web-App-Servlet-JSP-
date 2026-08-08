package com.ewallet.service;

import com.ewallet.model.Account;

public interface AccountService {
	boolean addAcount(Account account);
	
	boolean deleteAccountByAccountId(long accountId);
	
	boolean deleteAccountByRefereceIdAndTypeId(long referenceId, int accountTypeId);
}
