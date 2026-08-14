package com.ewallet.util;

import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;
import java.time.YearMonth;

/**
 * Form validation helpers for the add-card flow: card number, CVV and expiry
 * date, plus translation of DB card constraints into user-facing error
 * messages.
 */
public class CardValidator {
	/**
	 * Validates all fields of the add-card form and returns the field error
	 * map.
	 */
	public static Map<String, String> validateForAddCard(String cardNumber, String cvv, String expMonth, String expYear){
		Map<String, String> errors = new HashMap<>();
        checkCardNumber(cardNumber, errors);
        checkCVV(cvv, errors);
        checkExpirDate(expMonth, expYear, errors);
        return errors;
	}
	/*
	 * DB constraints mirrored by the checks below (as declared in the schema):
	 * CONSTRAINT CHECK_CARD_NUMBER_LENGTH CHECK (REGEXP_LIKE(card_number, '^[0-9]{16}$')),
	 * CONSTRAINT CHECK_CVV_LENGTH CHECK (REGEXP_LIKE(cvv, '^[0-9]{3}$')),
	 * CONSTRAINT UQ_CARD_NUMBER_WALLET UNIQUE(wallet_id,card_number)
	 * */
	/**
	 * Translates a database integrity-constraint violation (SQL state 23000)
	 * into a plain-English error message per constraint.
	 */
	public static Map<String, String> parseSqlException(SQLException e) {
		Map<String, String> errors = new HashMap<>();
		String sqlState = e.getSQLState();
		if ("23000".equals(sqlState)) { // Integrity constraint violation
			String message = e.getMessage();
			if (message.contains("CHECK_CARD_NUMBER_LENGTH")) {
				errors.put("cardNumber", "Card Number must be exactly 16 digits.");
			}else if (message.contains("UQ_CARD_NUMBER_WALLET")) {
				errors.put("cardNumber", "Card Number is already exist");
			} else if (message.contains("CHECK_CVV_LENGTH")) {
				errors.put("cvv", "CVV must be exactly 3 digits.");
			} 
		}
		return errors;		
	}
	
	/**
	 * Card number must be exactly 16 digits.
	 */
	public static void checkCardNumber(String cardNumber, Map<String, String> errors) {
		if(cardNumber == null || cardNumber.isEmpty() || cardNumber.trim().isEmpty()) {
			errors.put("cardNumber", "Card Number is required.");
			return;
		}
		String trimmedCardNumber = cardNumber.trim();
		if(!trimmedCardNumber.matches("\\d{16}")) {
			errors.put("cardNumber", "Card Number must be exactly 16 digits.");
		}
	}

	/**
	 * CVV must be exactly 3 digits.
	 */
	public static void checkCVV(String cvv, Map<String, String> errors) {
		if(cvv == null || cvv.isEmpty() || cvv.trim().isEmpty()) {
			errors.put("cvv", "CVV is required.");
			return;
		}
		String trimmedCVV = cvv.trim();
		if(!trimmedCVV.matches("\\d{3}")) {
			errors.put("cvv", "CVV must be exactly 3 digits.");
		}
	}
	
	/**
	 * Expiry month and year must both be present and must point strictly after
	 * the current month (a card expiring this month is already rejected).
	 */
	public static void checkExpirDate(String expMonth, String expYear, Map<String, String> errors) {
		if(expMonth == null || expMonth.isEmpty() || expMonth.trim().isEmpty() ||
				expYear == null	|| expYear.isEmpty() || expYear.trim().isEmpty()) {
			errors.put("expirDate", "Expire Month and Expire Year are required.");
			return;
		}
		String trimmedExpMonth = expMonth.trim();
		String trimmedExpYear = expYear.trim();

		int month = Integer.parseInt(trimmedExpMonth);
		int year = Integer.parseInt(trimmedExpYear);

		YearMonth currentMonth = YearMonth.now();
		YearMonth expirationMonth = YearMonth.of(year, month);

		if (!expirationMonth.isAfter(currentMonth)) {
		    errors.put("expirDate", "Expire Date is expired");
		} 
	}
}
