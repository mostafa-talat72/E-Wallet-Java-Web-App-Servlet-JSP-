package com.ewallet.service;

import com.ewallet.model.Wallet;

public interface EWalletUserService {
	
	Wallet signup(Wallet wallet);
	
	Wallet login(Wallet wallet);
	
	Wallet updateUserWallet(Wallet wallet);
	
	boolean deleteUserWallet(Wallet wallet, Wallet deletedWallet);
	
	Wallet getUserWalletById(long id);
	
	Wallet getUserWalletByPhoneNumber(String phoneNumber);
	

}
