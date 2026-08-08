package com.ewallet.service;

import com.ewallet.model.WalletBalance;

public interface EWalletBalanceService {
	
	WalletBalance createWalletBalance(WalletBalance walletBalance);
	
	WalletBalance updateWalletBalance(WalletBalance walletBalance);
	
	WalletBalance getWalletBalanceByWalletId(Long walletId);
	
	boolean deleteWalletBalanceByWalletId(Long walletId);
}
