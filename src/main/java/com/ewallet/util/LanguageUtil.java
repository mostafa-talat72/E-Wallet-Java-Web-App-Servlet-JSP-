package com.ewallet.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Tracks the UI language chosen by the user in the session and exposes it as a
 * query parameter so redirects keep the current language (en/ar).
 */
public class LanguageUtil {
	/**
	 * Returns "?lang=en" when the session language is English, otherwise
	 * "?lang=ar" (Arabic is the default for untracked sessions).
	 */
	public static String langQuery(HttpServletRequest req) {
	    Object lang = req.getSession().getAttribute("lang");
	    return lang != null && lang.equals("en") ? "?lang=en" : "?lang=ar";
	}
}
