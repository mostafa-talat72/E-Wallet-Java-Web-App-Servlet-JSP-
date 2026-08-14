package com.ewallet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Session guard applied to every request. Pages that require authentication
 * (all wallet operations) are only reachable when the session holds a
 * "wallet" attribute; otherwise the user is redirected to the login page.
 *
 * <p>Public paths that bypass the guard:
 * <ul>
 *   <li>root "/" and empty paths</li>
 *   <li>static assets under /assets/ (CSS, JS, images)</li>
 *   <li>the ATM machine/map flow: /atm/* pages and the atmController</li>
 *   <li>the public JSPs: index, login, register, error and activate</li>
 *   <li>whitelisted controller actions (wallet login/signup/activation flow, ATM execute)</li>
 * </ul>
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

	/**
	 * Lifecycle hook: this stateless guard has nothing to initialize.
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	/**
	 * Guards the request: public URIs pass through untouched; anything else
	 * requires an authenticated session holding the "wallet" attribute, else
	 * the user is redirected to the login page.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// Request path relative to the application's context path
		String uri = req.getRequestURI().substring(req.getContextPath().length());

		if (isPublic(req, uri)) {
			// Public paths never require a session
			chain.doFilter(request, response);
			return;
		}

		// Everything else needs a session with a logged-in wallet
		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("wallet") == null) {
			// Not authenticated: send the user back to the login page
			res.sendRedirect(req.getContextPath() + "/login.jsp");
			return;
		}

		chain.doFilter(request, response);
	}

	/**
	 * Decides whether the URI can be reached without logging in. Order matters:
	 * static assets and the ATM flow are whitelisted first, then the specific
	 * public JSPs, then specific controller actions (matched by their "action"
	 * parameter).
	 */
	private boolean isPublic(HttpServletRequest request, String uri) {
		// The root path "/" and empty URIs are always allowed
		if (uri == null || uri.isEmpty() || "/".equals(uri)) {
			return true;
		}
		// static assets (CSS, JS, images) must load on public pages too
		if (uri.startsWith("/assets/")) {
			return true;
		}
		// public ATM pages (machine + map flow, no login needed)
		if (uri.startsWith("/atm/") || uri.contains("atmController")) {
			return true;
		}
		// public pages
		if (uri.endsWith("index.jsp") || uri.endsWith("login.jsp")
				|| uri.endsWith("register.jsp") || uri.endsWith("error.jsp")
				|| uri.endsWith("activate.jsp")
				|| uri.endsWith("forgot-pin.jsp") || uri.endsWith("forgot-pin-code.jsp")) {
			return true;
		}
		// controllers with public actions
		if (uri.contains("transactionController")) {
			// Executing a deposit/withdrawal on the ATM machine is public
			return "atmExecute".equals(request.getParameter("action"));
		}
		if (uri.contains("walletController")) {
			// Anonymous actions: login, signup, the wallet activation flow and
			// the forgot-PIN flow (both only need session values, never a wallet).
			String action = request.getParameter("action");
			return "login".equals(action) || "signup".equals(action)
					|| "activate".equals(action) || "resendActivation".equals(action)
					|| "forgotPin".equals(action) || "resetPin".equals(action);
		}
		return false;
	}

	/**
	 * Lifecycle hook: no cleanup needed for this stateless guard.
	 */
	@Override
	public void destroy() {
	}
}