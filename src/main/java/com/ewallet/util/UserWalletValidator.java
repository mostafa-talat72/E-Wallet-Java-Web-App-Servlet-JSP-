package com.ewallet.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Form validation helpers for the wallet signup, login, update, delete and
 * forgot-PIN flows. Each method returns a map of field name -> i18n error key,
 * or parses a DB constraint violation into the same field error keys.
 */
public class UserWalletValidator {
	
	/**
	 * Validates every field of the signup form (full name, phone number,
	 * national ID, PIN and PIN confirmation) and returns field error keys.
	 */
	public static Map<String, String> validateForSignup(String fullName, String nationalId, String phoneNumber, String password, String confirmPassword) {
        Map<String, String> errors = new HashMap<>();
        checkFullName(fullName, errors);
        checkPhoneNumber(phoneNumber, errors);
        checkNationalId(nationalId, errors);
        checkPassword(password, errors);
        checkConfirmPassword(password, confirmPassword, errors);
        return errors;
    }
	
	/**
	 * Validates the login form fields (phone number and PIN).
	 */
	public static Map<String, String> validateForLogin(String phoneNumber, String password) {
		Map<String, String> errors = new HashMap<>();
		checkPhoneNumber(phoneNumber, errors);
		checkPassword(password, errors);
		return errors;
	}
	
	/**
	 * Validates the profile-update form (full name only).
	 */
	public static Map<String, String> validateForUpdateInfo(String fullName) {
		Map<String, String> errors = new HashMap<>();
		checkFullName(fullName, errors);
		return errors;
	}	
	
	/**
	 * Validates the change-PIN form (new PIN plus its confirmation).
	 */
	public static Map<String, String> validateForUpdatePin(String password, String confirmPassword) {
		Map<String, String> errors = new HashMap<>();
		checkPassword(password, errors);
		checkConfirmPassword(password, confirmPassword, errors);
		return errors;
	}
	
	/**
	 * Validates the wallet-deletion form: the current PIN must be supplied.
	 */
	public static Map<String, String> validateForDelete(String password) {
		Map<String, String> errors = new HashMap<>();
		checkPassword(password, errors);
		return errors;
	}
	
	/**
	 * Validates the forgot-PIN form (phone number and national ID pair).
	 */
	public static Map<String, String> validateForForgotPin(String phoneNumber, String nationalId) {
		Map<String, String> errors = new HashMap<>();
		checkPhoneNumber(phoneNumber, errors);
		checkNationalId(nationalId, errors);
		return errors;
	}
	
	// DB constraints mirrored by the checks below (as declared in the schema):
	// CONSTRAINT CHECK_PHONE_NUMBER_LENGTH CHECK (REGEXP_LIKE(phone_number, '^[0-9]{11}$')),
	// CONSTRAINT CHECK_NATIONAL_ID_LENGTH CHECK (REGEXP_LIKE(national_id, '^[0-9]{14}$')),
	// CONSTRAINT UQ_PHONE UNIQUE(phone_number),
	
	/**
	 * Translates a database integrity-constraint violation (SQL state 23000)
	 * into field error keys, so the form errors mirror the DB constraints.
	 */
	public static Map<String, String> parseSqlException(SQLException e) {
		Map<String, String> errors = new HashMap<>();
		String sqlState = e.getSQLState();
		if ("23000".equals(sqlState)) { // Integrity constraint violation
			String message = e.getMessage();
			if (message.contains("UQ_PHONE")) {
				errors.put("phoneNumber", "err.phone.exists");
			} else if (message.contains("CHECK_PHONE_NUMBER_LENGTH")) {
				errors.put("phoneNumber", "err.phone.length");
			} else if (message.contains("CHECK_NATIONAL_ID_LENGTH")) {
				errors.put("nationalId", "err.nationalId.invalid");
			}
		}
		return errors;		
	}

	/**
	 * Internal: full name must be 3-50 characters after trimming.
	 */
	private static void checkFullName(String fullName, Map<String, String> errors) {
		if(fullName == null || fullName.trim().isEmpty()) {
			errors.put("fullName", "err.fullName.required");
			return;
		} 
		String trimmedFullName = fullName.trim();
		if(trimmedFullName.length() < 3 || trimmedFullName.length() > 50) {
			errors.put("fullName", "err.fullName.length");
		}
	}

	/**
	 * Phone number must be 11 digits and start with "01" (local mobile prefix).
	 */
	public static void checkPhoneNumber(String phoneNumber, Map<String, String> errors) {
		if(phoneNumber == null || phoneNumber.trim().isEmpty()) {
			errors.put("phoneNumber", "err.phone.required");
			return;
		} 
		String trimmedPhoneNumber = phoneNumber.trim();
		if(!trimmedPhoneNumber.matches("\\d{11}")) {
			errors.put("phoneNumber", "err.phone.length");
			return;
		}		
		
		if(!trimmedPhoneNumber.startsWith("01")) {
			errors.put("phoneNumber", "err.phone.start");
		}
	}
	
	/**
	 * Internal: national ID must be exactly 14 digits.
	 */
	private static void checkNationalId(String nationalId, Map<String, String> errors) {
		if(nationalId == null || nationalId.trim().isEmpty()) {
			errors.put("nationalId", "err.nationalId.required");
			return;
		} 
		String trimmedNationalId = nationalId.trim();
		if(!trimmedNationalId.matches("\\d{14}")) {
			errors.put("nationalId", "err.nationalId.invalid");
		}
	}

	/**
	 * PIN must be exactly 6 digits (numeric pin pad contract).
	 */
	public static void checkPassword(String password, Map<String, String> errors) {
		if(password == null || password.trim().isEmpty()) {
			errors.put("pin", "err.pin.required");
			return;
		} 
		String trimmedPassword = password.trim();
		if(trimmedPassword.length() != 6) {
			errors.put("pin", "err.pin.length");
			return;
		}		
		if(!trimmedPassword.matches("\\d{6}")) {
			errors.put("pin", "err.pin.digits");
		}
	}

	/**
	 * Internal: confirmation field must be present and equal to the PIN.
	 */
	private static void checkConfirmPassword(String password, String confirmPassword, Map<String, String> errors) {
		if(confirmPassword == null || confirmPassword.trim().isEmpty()) {
			errors.put("pinConfirm", "err.pinConfirm.required");
			return;
		} 
		String trimmedConfirmPassword = confirmPassword.trim();
		if(!trimmedConfirmPassword.equals(password)) {
			errors.put("pinConfirm", "err.pinConfirm.match");
		}		
		
	}

}
