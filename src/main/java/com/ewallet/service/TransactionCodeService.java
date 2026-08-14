package com.ewallet.service;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.ewallet.model.TransactionCode;

public interface TransactionCodeService {

	TransactionCode addTransactionCode(TransactionCode transactionCode) throws SQLException;

	TransactionCode getValidTransactionCodeByWalletIdAndCode(long walletIde) ;

	boolean updateTransactionCodeByWalletIdAndCode(TransactionCode transactionCode);

	
}
