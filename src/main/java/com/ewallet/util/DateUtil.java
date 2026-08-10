package com.ewallet.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;

public class DateUtil {
	public static Date convertExpirationDate(String expMonth, String expYear) {

	    int month = Integer.parseInt(expMonth.trim());
	    int year = Integer.parseInt(expYear.trim());

	    YearMonth expiration = YearMonth.of(year, month);

	    return Date.valueOf(expiration.atDay(1));
	}
	
	public static String getExpirationMonth(Date expireDate) {
	    LocalDate date = expireDate.toLocalDate();
	    return String.format("%02d", date.getMonthValue());
	}

	public static String getExpirationYear(Date expireDate) {
	    LocalDate date = expireDate.toLocalDate();
	    return String.valueOf(date.getYear());
	}
	
	
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
