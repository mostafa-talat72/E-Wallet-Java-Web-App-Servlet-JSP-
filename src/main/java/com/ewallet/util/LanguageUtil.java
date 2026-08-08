package com.ewallet.util;

import javax.servlet.http.HttpServletRequest;

public class LanguageUtil {
	public static String langQuery(HttpServletRequest req) {
	    Object lang = req.getSession().getAttribute("lang");
	    return lang != null && lang.equals("en") ? "?lang=en" : "?lang=ar";
	}
}
