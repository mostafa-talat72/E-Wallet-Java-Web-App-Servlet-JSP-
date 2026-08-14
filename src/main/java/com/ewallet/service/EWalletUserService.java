package com.ewallet.service;

import java.sql.SQLException;

import com.ewallet.model.Wallet;

public interface EWalletUserService {
	
	Wallet signup(Wallet wallet)throws SQLException;
	
	Wallet login(Wallet wallet)throws SQLException;
	
	Wallet updateUserWallet(Wallet wallet)throws SQLException;
	
	Wallet updateUserWalletPin(Wallet wallet, String newPin)throws SQLException;
	
	boolean deleteUserWallet(Wallet wallet, Wallet deletedWallet)throws SQLException;
	
	Wallet getUserWalletById(long id)throws SQLException;
	
	Wallet getUserWalletByPhoneNumber(String phoneNumber)throws SQLException;
	

}
