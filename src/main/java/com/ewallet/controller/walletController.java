package com.ewallet.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.annotation.Resource;
import javax.sql.DataSource;

import com.ewallet.model.Account;
import com.ewallet.model.Wallet;
import com.ewallet.model.WalletBalance;
import com.ewallet.service.AccountService;
import com.ewallet.service.EWalletBalanceService;
import com.ewallet.service.EWalletUserService;
import com.ewallet.service.impl.AccountServiceImpl;
import com.ewallet.service.impl.EWalletBalanceServiceImpl;
import com.ewallet.service.impl.EWalletUserServiceImpl;
import com.ewallet.util.LanguageUtil;
import com.ewallet.util.UserWalletValidator;


/**
 * Controller handling all wallet user account management operations.
 *
 * URL mapping: /walletController
 *
 * Exposed actions (via the "action" request parameter):
 *  - signup              : registers a new wallet user account
 *  - login               : authenticates an existing wallet user
 *  - updateUserWallet    : updates the wallet user's full name
 *  - updateUserWalletPin : changes the wallet user's 6-digit PIN
 *  - deleteUserWallet    : permanently deletes a wallet user account
 *  - logout              : invalidates the current user session
 *  - (any other/missing) : redirects to the error page
 *
 * Examples:
 *  http://localhost:8080/E-Wallet/walletController?action=signup
 *  http://localhost:8080/E-Wallet/walletController?action=login
 *  http://localhost:8080/E-Wallet/walletController?action=updateUserWallet
 *  http://localhost:8080/E-Wallet/walletController?action=updateUserWalletPin
 *  http://localhost:8080/E-Wallet/walletController?action=deleteUserWallet
 *  http://localhost:8080/E-Wallet/walletController?action=logout
 */
@WebServlet("/walletController")
public class walletController extends HttpServlet {
	
	@Resource(name = "jdbc/ewallet/dBconnection")
	private DataSource dataSource;
	
	private EWalletUserService eWalletUserService;

	
	 /**
	 * Servlet initialization hook.
	 * Looks up the JDBC DataSource injected via @Resource and
	 * constructs the EWalletUserService used by all action methods.
	 */
	 @Override
    public void init() throws ServletException {
	 eWalletUserService = new EWalletUserServiceImpl(dataSource);
    }

	
	/**
	 * GET entry point of the controller.
	 * Reads the "action" request parameter and dispatches to the
	 * matching private action method. Also acts as the handler for
	 * POST requests because doPost simply delegates to doGet.
	 *
	 * @param request  the HTTP request
	 * @param response the HTTP response
	 * @throws ServletException if a forward/redirect fails
	 * @throws IOException      if the response cannot be written
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if(action == null) {
			action = "notFoundPage";
		}
		
		switch(action) {
			case "signup":
				signup(request, response);
				break;
			case "login":
				login(request, response);
				break;
			case "updateUserWallet":
				updateUserWallet(request, response);
				break;
			case "updateUserWalletPin":
				updateUserWalletPin(request, response);
			break;
			case "deleteUserWallet":
				deleteUserWallet(request, response);
				break;
			case "logout":
				logout(request, response);
				break;
			default:
				response.sendRedirect("error.jsp" + LanguageUtil.langQuery(request));
				break;
						
		}
		
		
	}


	/**
	 * POST entry point of the controller.
	 * Delegates all POST requests to doGet so that both HTTP verbs
	 * are handled by the same action-dispatch logic.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	
	/**
	 * Handles the "signup" action: registers a new wallet user.
	 * Validates the submitted form fields, creates the wallet record,
	 * then initializes a linked wallet balance and a primary account
	 * before redirecting to the login page.
	 */
	private void signup(HttpServletRequest request, HttpServletResponse response) {
		String phoneNumber = request.getParameter("phone");
		String nationalId = request.getParameter("nationalId");
		String fullName = request.getParameter("fullName");
		String pin = request.getParameter("pin");
		String pinConfirm = request.getParameter("pinConfirm");

		Map<String, String> errors = UserWalletValidator.validateForSignup(fullName,nationalId, phoneNumber, pin, pinConfirm);
		
		if(errors.isEmpty()){
			Wallet newWallet = new Wallet(phoneNumber, nationalId, fullName, pin,"");
			
			try {
				newWallet = eWalletUserService.signup(newWallet);
			} catch (SQLException e) {
				// Map database constraints (e.g. duplicate phone/national ID) to user-facing errors.
				errors = UserWalletValidator.parseSqlException(e);
			}
			
			if(errors.isEmpty() && newWallet != null) {
				// Provision the new wallet with a zero balance and a primary account (type 1).
				EWalletBalanceService eWalletBalanceService = new EWalletBalanceServiceImpl(dataSource);
				eWalletBalanceService.createWalletBalance(new WalletBalance(newWallet.getWalletId()));
				
				AccountService accountService = new AccountServiceImpl(dataSource);
				accountService.addAcount(new Account(1, newWallet.getWalletId()));
				try {
					response.sendRedirect("login.jsp" + LanguageUtil.langQuery(request));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
			if(newWallet == null) {
				errors.put("siginUpErr", "err.generic");
			}
		}
		
		if(!errors.isEmpty()) {
			// Re-render the registration form, keeping the user's input and error messages.
			request.setAttribute("errors", errors);
			request.setAttribute("fullNameErrVal", fullName);
			request.setAttribute("nationalIdErrVal", nationalId);
			request.setAttribute("phoneNumberErrVal", phoneNumber);
			request.setAttribute("pinErrVal", pin);
			request.setAttribute("pinConfirmErrVal", pinConfirm);
		    try {
				request.getRequestDispatcher("register.jsp" +  LanguageUtil.langQuery(request)).forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Handles the "login" action: authenticates a wallet user.
	 * Builds a Wallet from the submitted phone number and PIN, attempts
	 * a login against the service layer, and on success stores the wallet
	 * and its current balance in the session before redirecting to the home page.
	 */
	private void login(HttpServletRequest request, HttpServletResponse response) {
		// If the user is already logged in, reuse the session wallet's phone and hashed PIN
		// instead of asking for credentials again.
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		String phoneNumber = wallet == null? request.getParameter("phone"): wallet.getPhoneNumber();
		String pin =  wallet == null? request.getParameter("pin") : wallet.getPinHash();
		wallet = new Wallet(phoneNumber, pin);
		
		Map<String, String> errors = UserWalletValidator.validateForLogin(phoneNumber, pin);
		
		if(errors.isEmpty())  {
			try {
				wallet = eWalletUserService.login(wallet);
			} catch (SQLException e) {
				errors = UserWalletValidator.parseSqlException(e);
			}
			
			if(errors.isEmpty() && wallet != null) {
				// Store the authenticated wallet and its latest balance in the session.
				request.getSession().setAttribute("wallet", wallet);
				WalletBalance walletBalance = new EWalletBalanceServiceImpl(dataSource).getWalletBalanceByWalletId(wallet.getWalletId());
				request.getSession().setAttribute("walletBalance", walletBalance);
		        try {
					response.sendRedirect("home.jsp" + LanguageUtil.langQuery(request));
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
			
			if(wallet == null) {
				errors.put("loginErr", "err.login.failed");
			}
		}
		
		if(!errors.isEmpty()) {
			// Re-render the login form with the error messages and the previously typed values.
			request.setAttribute("errors", errors);
			request.setAttribute("phoneNumberErr", phoneNumber);
			request.setAttribute("pinErr", pin);
		    try {
				request.getRequestDispatcher("login.jsp" +  LanguageUtil.langQuery(request)).forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
		
	}


	/**
	 * Handles the "updateUserWallet" action: updates the full name of the
	 * logged-in wallet user. Validates the new name, persists the change,
	 * and refreshes the wallet object held in the session.
	 */
	private void updateUserWallet(HttpServletRequest request, HttpServletResponse response) {

		String fullName = request.getParameter("fullName");
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");

		Map<String, String> errors = UserWalletValidator.validateForUpdateInfo(fullName);
		
		if(errors.isEmpty()) {
			try {
				wallet.setFullName(fullName);
				wallet = eWalletUserService.updateUserWallet(wallet);
				
			} catch (SQLException e) {
				errors = UserWalletValidator.parseSqlException(e);
			}
			
			if(errors.isEmpty() && wallet != null) {
				// Update the session wallet with the refreshed data returned by the service.
				request.getSession().setAttribute("wallet", wallet);
			}
			else if(wallet == null) {
				errors.put("updateInfoErr", "err.generic");
			}
		}
		
		if(!errors.isEmpty()) {
			request.setAttribute("errors", errors);
			request.setAttribute("fullNameErrVal", fullName);
		}
		
		try {
			request.getRequestDispatcher("profile.jsp" +  LanguageUtil.langQuery(request)).forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the "updateUserWalletPin" action: changes the wallet user's PIN.
	 * First verifies the current PIN by attempting a login with it, then
	 * persists the new PIN and refreshes the session wallet object.
	 */
	private void updateUserWalletPin(HttpServletRequest request, HttpServletResponse response) {
		String currentPin = request.getParameter("curPin");
		String newPin = request.getParameter("newPin");
		String newPinConfirm = request.getParameter("newPin2");
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		Map<String, String> errors = UserWalletValidator.validateForUpdatePin(newPin, newPinConfirm);

		// Verify the user's current PIN by simulating a login with the entered PIN.
		Wallet check = new Wallet(wallet.getPhoneNumber(), currentPin);
		try {
			if (eWalletUserService.login(check) == null) {
				errors.put("curPinErr", "err.curPin.wrong");
			}
		} catch (SQLException e) {
			errors = UserWalletValidator.parseSqlException(e);
		}

		if(errors.isEmpty()) {
			try {
				wallet = eWalletUserService.updateUserWalletPin(wallet, newPin);
			} catch (SQLException e) {
				errors = UserWalletValidator.parseSqlException(e);
			}
			
			if(errors.isEmpty() && wallet != null) {
				// Keep the session wallet in sync with the newly hashed PIN.
				request.getSession().setAttribute("wallet", wallet);
			} else if(wallet == null) {
				errors.put("updatePinErr", "err.generic");
			}
		}
		
		if(!errors.isEmpty()) {
			// Preserve the form values and validation errors for re-rendering the profile page.
			 request.setAttribute("errors", errors);
			 request.setAttribute("curPinVal", currentPin);
			 request.setAttribute("newPinErrVal", currentPin);
			 request.setAttribute("newPinConfirmErrVal", currentPin);
		}
		
		try {
			request.getRequestDispatcher("profile.jsp" +  LanguageUtil.langQuery(request)).forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the "deleteUserWallet" action: permanently deletes a wallet user account.
	 * Re-authenticates the user with the submitted phone number and PIN, then deletes
	 * the account and closes the session before redirecting to the login page.
	 */
	private void deleteUserWallet(HttpServletRequest request, HttpServletResponse response) {
		String phoneNumber = request.getParameter("phone");
		String pin = request.getParameter("pin");
		
		Map<String, String> errors = UserWalletValidator.validateForLogin(phoneNumber, pin);
		
		if(errors.isEmpty()) {
		
			Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
			
			Wallet deletedWallet = new Wallet(phoneNumber, pin);
	
			try {
				// Re-authenticate with the supplied credentials before allowing deletion.
				deletedWallet = eWalletUserService.login(deletedWallet);
			} catch (SQLException e) {
				errors = UserWalletValidator.parseSqlException(e);
			}
			
			if(errors.isEmpty() && deletedWallet != null) {
				
				boolean isDeleted = false;
				try {
					isDeleted = eWalletUserService.deleteUserWallet(wallet, deletedWallet);
				} catch (SQLException e) {
					errors = UserWalletValidator.parseSqlException(e);
				}
	
				if(errors.isEmpty() && isDeleted) {
					// Account deleted: destroy the session and send the user back to login.
					request.getSession().invalidate();
					try {
						response.sendRedirect("login.jsp" + LanguageUtil.langQuery(request));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else if(isDeleted == false) {
					errors.put("deletedError", "err.delete.failed");
				}
			}else if(deletedWallet == null) {
				errors.put("deletedError", "err.login.failed");
			}
		}
		
		if(!errors.isEmpty()) {
			// Re-render the profile page with the errors and the submitted values.
			 request.setAttribute("errors", errors);
			 request.setAttribute("delPhoneNumberErrVall", phoneNumber);
			 request.setAttribute("delPinErrVal", pin);

		    try {
				request.getRequestDispatcher("profile.jsp" +  LanguageUtil.langQuery(request)).forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Handles the "logout" action: ends the current user session
	 * and redirects to the login page.
	 */
	private void logout(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().invalidate();
		try {
			response.sendRedirect("login.jsp" + LanguageUtil.langQuery(request));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
