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
 * Session guard: only the public pages (login, register, error, ATM machine/map
 * and their controllers) can be reached without a logged-in wallet.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String uri = req.getRequestURI().substring(req.getContextPath().length());

		if (isPublic(req, uri)) {
			chain.doFilter(request, response);
			return;
		}

		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("wallet") == null) {
			res.sendRedirect(req.getContextPath() + "/login.jsp");
			return;
		}

		chain.doFilter(request, response);
	}

	private boolean isPublic(HttpServletRequest request, String uri) {
		if (uri == null || uri.isEmpty() || "/".equals(uri)) {
			return true;
		}
		// static assets
		if (uri.startsWith("/assets/")) {
			return true;
		}
		// public ATM pages (machine + map flow, no login needed)
		if (uri.startsWith("/atm/") || uri.contains("atmController")) {
			return true;
		}
		// public pages
		if (uri.endsWith("index.jsp") || uri.endsWith("login.jsp")
				|| uri.endsWith("register.jsp") || uri.endsWith("error.jsp")) {
			return true;
		}
		// controllers with public actions
		if (uri.contains("transactionController")) {
			return "atmExecute".equals(request.getParameter("action"));
		}
		if (uri.contains("walletController")) {
			String action = request.getParameter("action");
			return "login".equals(action) || "signup".equals(action);
		}
		return false;
	}

	@Override
	public void destroy() {
	}
}