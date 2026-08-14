package com.ewallet.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Date helpers for card expiry: parsing a month/year pair from the add-card
 * form, formatting it back for pre-filling, and checking whether a card has
 * expired.
 */
public class DateUtil {
	/**
	 * Converts an "expiry month" + "expiry year" pair into a java.sql.Date
	 * pointing at the first day of that month (how the DB stores card expiry).
	 */
	public static Date convertExpirationDate(String expMonth, String expYear) {

	    int month = Integer.parseInt(expMonth.trim());
	    int year = Integer.parseInt(expYear.trim());

	    YearMonth expiration = YearMonth.of(year, month);

	    return Date.valueOf(expiration.atDay(1));
	}
	
	/**
	 * Returns the expiry month as a zero-padded string (e.g. "03") for form
	 * pre-filling.
	 */
	public static String getExpirationMonth(Date expireDate) {
	    LocalDate date = expireDate.toLocalDate();
	    return String.format("%02d", date.getMonthValue());
	}

	/**
	 * Returns the expiry year as a plain string for form pre-filling.
	 */
	public static String getExpirationYear(Date expireDate) {
	    LocalDate date = expireDate.toLocalDate();
	    return String.valueOf(date.getYear());
	}
	
	
	/**
	 * A card is expired when its expiry month is not strictly after the
	 * current month: a null date is treated as expired, and a card expiring in
	 * the running month is already considered unusable.
	 */
	public static boolean isExpired(Date expireDate) {

	    if (expireDate == null) {
	        return true;
	    }

	    LocalDate expiration = expireDate.toLocalDate();
	    LocalDate current = LocalDate.now();

	    return expiration.getYear() < current.getYear()
	            || (expiration.getYear() == current.getYear()
	            && expiration.getMonthValue() <= current.getMonthValue());
	}
}
