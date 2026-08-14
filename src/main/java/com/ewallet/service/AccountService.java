package com.ewallet.service;

import com.ewallet.model.Account;

public interface AccountService {
	boolean addAcount(Account account);
	
	boolean updateAccountStatusByAccountId(long accountId);
	
	boolean updateAccountStatusByRefereceIdAndTypeId(long referenceId, int accountTypeId);

	Account getAccountByRefereceIdAndTypeId(long referenceId, int accountTypeId);
	Account getAccountByAccountId(long accountId);
}
