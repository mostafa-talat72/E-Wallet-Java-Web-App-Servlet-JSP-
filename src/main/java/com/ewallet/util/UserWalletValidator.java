package com.ewallet.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserWalletValidator {
	
	public static Map<String, String> validateForSignup(String fullName, String nationalId, String phoneNumber, String password, String confirmPassword) {
        Map<String, String> errors = new HashMap<>();
        checkFullName(fullName, errors);
        checkPhoneNumber(phoneNumber, errors);
        checkNationalId(nationalId, errors);
        checkPassword(password, errors);
        checkConfirmPassword(password, confirmPassword, errors);
        return errors;
    }
	
	public static Map<String, String> validateForLogin(String phoneNumber, String password) {
		Map<String, String> errors = new HashMap<>();
		checkPhoneNumber(phoneNumber, errors);
		checkPassword(password, errors);
		return errors;
	}
	
	public static Map<String, String> validateForUpdateInfo(String fullName, String password, String confirmPassword) {
		Map<String, String> errors = new HashMap<>();
		checkFullName(fullName, errors);
		return errors;
	}	
	
	public static Map<String, String> validateForUpdatePin(String password, String confirmPassword) {
		Map<String, String> errors = new HashMap<>();
		checkPassword(password, errors);
		checkConfirmPassword(password, confirmPassword, errors);
		return errors;
	}
	
	public static Map<String, String> validateForDelete(String password) {
		Map<String, String> errors = new HashMap<>();
		checkPassword(password, errors);
		return errors;
	}
	
	public static Map<String, String> validateForForgotPin(String phoneNumber, String nationalId) {
		Map<String, String> errors = new HashMap<>();
		checkPhoneNumber(phoneNumber, errors);
		checkNationalId(nationalId, errors);
		return errors;
	}
	
	// CONSTRAINT CHECK_PHONE_NUMBER_LENGTH CHECK (REGEXP_LIKE(phone_number, '^[0-9]{11}$')),
	// CONSTRAINT CHECK_NATIONAL_ID_LENGTH CHECK (REGEXP_LIKE(national_id, '^[0-9]{14}$')),
	// CONSTRAINT UQ_PHONE UNIQUE(phone_number),
	
	public static Map<String, String> parseSqlException(SQLException e) {
		Map<String, String> errors = new HashMap<>();
		String sqlState = e.getSQLState();
		if ("23000".equals(sqlState)) { // Integrity constraint violation
			String message = e.getMessage();
			if (message.contains("UQ_PHONE")) {
				errors.put("phoneNumber", "Phone number already exists.");
			} else if (message.contains("CHECK_PHONE_NUMBER_LENGTH")) {
				errors.put("phoneNumber", "Phone number must be exactly 11 digits.");
			} else if (message.contains("CHECK_NATIONAL_ID_LENGTH")) {
				errors.put("nationalId", "National ID must be exactly 14 digits.");
			}
		}
		return errors;		
	}

	private static void checkFullName(String fullName, Map<String, String> errors) {
		if(fullName == null || fullName.trim().isEmpty()) {
			errors.put("fullName", "Full name is required.");
			return;
		} 
		String trimmedFullName = fullName.trim();
		if(trimmedFullName.length() < 3 || trimmedFullName.length() > 50) {
			errors.put("fullName", "Full name must be between 3 and 50 characters.");
		}
	}

	private static void checkPhoneNumber(String phoneNumber, Map<String, String> errors) {
		if(phoneNumber == null || phoneNumber.trim().isEmpty()) {
			errors.put("phoneNumber", "Phone number is required.");
			return;
		} 
		String trimmedPhoneNumber = phoneNumber.trim();
		if(!trimmedPhoneNumber.matches("\\d{11}")) {
			errors.put("phoneNumber", "Phone number must be exactly 11 digits.");
		}		
		
		if(!trimmedPhoneNumber.startsWith("01")) {
			errors.put("phoneNumber", "Phone number must start with '01'.");
		}
	}
	
	private static void checkNationalId(String nationalId, Map<String, String> errors) {
		if(nationalId == null || nationalId.trim().isEmpty()) {
			errors.put("nationalId", "National ID is required.");
			return;
		} 
		String trimmedNationalId = nationalId.trim();
		if(!trimmedNationalId.matches("\\d{14}")) {
			errors.put("nationalId", "National ID must be exactly 14 digits.");
		}
	}

	private static void checkPassword(String password, Map<String, String> errors) {
		if(password == null || password.trim().isEmpty()) {
			errors.put("password", "Password is required.");
			return;
		} 
		String trimmedPassword = password.trim();
		if(trimmedPassword.length() != 6) {
			errors.put("password", "Password must be exactly 6 characters.");
		}		
		
		if(!trimmedPassword.matches("\\d{6}")) {
			errors.put("password", "Password must contain only digits.");
		}
	}

	private static void checkConfirmPassword(String password, String confirmPassword, Map<String, String> errors) {
		if(confirmPassword == null || confirmPassword.trim().isEmpty()) {
			errors.put("confirmPassword", "Confirm password is required.");
			return;
		} 
		String trimmedConfirmPassword = confirmPassword.trim();
		if(!trimmedConfirmPassword.equals(password)) {
			errors.put("confirmPassword", "Passwords do not match.");
		}		
		
	}

}
