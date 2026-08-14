package com.ewallet.util;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TransactionValidator {

	public static final BigDecimal ATM_STEP = new BigDecimal(100);
	
	public static Map<String, String> validateSendMoney(String recipientPhone, BigDecimal amount, String pin) {
        Map<String, String> errors = new HashMap<>();
        UserWalletValidator.checkPhoneNumber(recipientPhone, errors);
        checkAmount(amount , errors);
        UserWalletValidator.checkPassword(pin, errors);
        return errors;
    }
	
	public static Map<String, String> parseSqlException(SQLException e) {
		Map<String, String> errors = new HashMap<>();
		String sqlState = e.getSQLState();
		if ("23000".equals(sqlState)) { // Integrity constraint violation
			String message = e.getMessage();
			if (message.contains("UQ_WALLET_CODE")) {
				errors.put("code", "err.otp.wrong");
			}
		}
		return errors;		
	}
	
	public static void checkAmount(BigDecimal amount, Map<String, String> errors) {
		if(amount == null) {
			errors.put("amount", "err.amount.require");
			return;
		} 
		
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			errors.put("amount", "err.amount.invalid");
		}
	}
	
	/**
	 * ATM deposits/withdrawals: amount must be a multiple of 100.
	 */
	public static void checkATMAmount(BigDecimal amount, Map<String, String> errors) {
		checkAmount(amount, errors);
		if (amount != null && amount.remainder(ATM_STEP).compareTo(BigDecimal.ZERO) != 0) {
			errors.put("amount", "err.amount.multiple100");
		}
	}

}
