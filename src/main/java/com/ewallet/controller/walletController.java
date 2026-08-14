package com.ewallet.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.annotation.Resource;
import javax.sql.DataSource;

import com.ewallet.model.Account;
import com.ewallet.model.ActivationCode;
import com.ewallet.model.Wallet;
import com.ewallet.model.WalletBalance;
import com.ewallet.service.AccountService;
import com.ewallet.service.ActivationCodeService;
import com.ewallet.service.EWalletBalanceService;
import com.ewallet.service.EWalletUserService;
import com.ewallet.service.MessageService;
import com.ewallet.service.impl.AccountServiceImpl;
import com.ewallet.service.impl.ActivationCodeServiceImpl;
import com.ewallet.service.impl.EWalletBalanceServiceImpl;
import com.ewallet.service.impl.EWalletUserServiceImpl;
import com.ewallet.service.impl.WhatsAppMessageServiceImpl;
import com.ewallet.util.LanguageUtil;
import com.ewallet.util.TransactionUtil;
import com.ewallet.util.UserWalletValidator;


/**
 * Controller handling all wallet user account management operations.
 *
 * URL mapping: /walletController
 *
 * Exposed actions (via the "action" request parameter):
 *  - signup              : registers a new wallet user account
 *  - login               : authenticates an existing wallet user
 *  - activate            : verifies the WhatsApp activation code and unlocks the wallet
 *  - resendActivation    : issues and sends a fresh activation code
 *  - forgotPin           : requests a PIN reset — verifies the phone, issues + sends a WhatsApp code
 *  - resetPin            : verifies the WhatsApp reset code and stores a new PIN
 *  - updateUserWallet    : updates the wallet user's full name
 *  - updateUserWalletPin : changes the wallet user's 6-digit PIN
 *  - deleteUserWallet    : permanently deletes a wallet user account
 *  - logout              : invalidates the current user session
 *  - (any other/missing) : redirects to the error page
 *
 * Examples:
 *  http://localhost:8080/E-Wallet/walletController?action=signup
 *  http://localhost:8080/E-Wallet/walletController?action=login
 *  http://localhost:8080/E-Wallet/walletController?action=activate
 *  http://localhost:8080/E-Wallet/walletController?action=resendActivation
 *  http://localhost:8080/E-Wallet/walletController?action=forgotPin
 *  http://localhost:8080/E-Wallet/walletController?action=resetPin
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
	private ActivationCodeService activationCodeService;
	private MessageService messageService;

	
	 /**
	 * Servlet initialization hook.
	 * Looks up the JDBC DataSource injected via @Resource and
	 * constructs the services used by all action methods.
	 */
	 @Override
    public void init() throws ServletException {
	 eWalletUserService = new EWalletUserServiceImpl(dataSource);
	 activationCodeService = new ActivationCodeServiceImpl(dataSource);
	 messageService = new WhatsAppMessageServiceImpl();
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
			case "activate":
				activate(request, response);
				break;
			case "resendActivation":
				resendActivation(request, response);
				break;
			case "forgotPin":
				forgotPin(request, response);
				break;
			case "resetPin":
				resetPin(request, response);
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
				// Issue an activation code, send it on WhatsApp, then send the new
				// owner to the activation page; the wallet stays locked (status = 0)
				// until they prove the phone number.
				try {
					ActivationCode activationCode = new ActivationCode(newWallet.getWalletId(),
							TransactionUtil.generateActivationCode());
					activationCode = activationCodeService.addActivationCode(activationCode);
					boolean sent = messageService.send(newWallet.getPhoneNumber(),
							"Your E-Wallet activation code is " + activationCode.getCode());
					request.getSession().setAttribute("pendingActivationWalletId", newWallet.getWalletId());
					if (!sent) {
						// WhatsApp unreachable: still show the code on the page as a fallback.
						request.getSession().setAttribute("activationFallbackCode", activationCode.getCode());
					}
					response.sendRedirect("activate.jsp" + LanguageUtil.langQuery(request));
				} catch (SQLException e) {
					// Code could not be persisted: fall back to the shared error
					// rendering below (register.jsp).
					errors = UserWalletValidator.parseSqlException(e);
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
				if (wallet.getStatus() == 0) {
					// Inactive wallet: never open a session — send the owner to the
					// activation page to prove the phone number with the WhatsApp code.
					request.getSession().setAttribute("pendingActivationWalletId", wallet.getWalletId());
			        try {
						response.sendRedirect("activate.jsp" + LanguageUtil.langQuery(request));
					} catch (IOException e) {
						e.printStackTrace();
					}
			        return;
				}
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
	 * Handles the "activate" action: verifies the 6-digit activation code the
	 * owner received on WhatsApp. On success the wallet is unlocked (status = 1),
	 * the code is consumed and the user is logged in; failures (wrong code,
	 * expired code, too many attempts) re-render the activation page with an error.
	 */
	private void activate(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		Long pendingWalletId = (Long) request.getSession().getAttribute("pendingActivationWalletId");
		Map<String, String> errors = new HashMap<>();

		if (pendingWalletId == null) {
			// No pending activation in this session (page opened directly).
			errors.put("activationErr", "err.activation.invalidSession");
		} else if (code == null || !code.matches("\\d{6}")) {
			errors.put("codeErr", "err.activation.invalidFormat");
		} else {
			try {
				ActivationCode stored = activationCodeService.getValidActivationCodeByWalletIdAndPurpose(pendingWalletId, "ACTIVATION");
				if (stored == null) {
					// No usable code: none was created yet, or its 10-minute window
					// has passed (expiry is decided by the DB clock in the query).
					errors.put("codeErr", "err.activation.expired");
				} else if (stored.getAttempts() >= 3) {
					// Code permanently locked after the maximum number of attempts.
					errors.put("codeErr", "err.activation.locked");
				} else if (!stored.getCode().equals(code)) {
					// Wrong code: increment the attempts counter and fail.
					stored.setAttempts(stored.getAttempts() + 1);
					activationCodeService.updateActivationCodeByWalletIdAndCode(stored);
					errors.put("codeErr", "err.activation.wrong");
				} else {
					// Correct code: consume it and unlock the wallet.
					stored.setAttempts(stored.getAttempts() + 1);
					stored.setIsUsed(1);
					stored.setIsExpire(1);
					activationCodeService.updateActivationCodeByWalletIdAndCode(stored);

					Wallet wallet = eWalletUserService.getUserWalletById(pendingWalletId);
					wallet = eWalletUserService.activateWallet(wallet);
					if (wallet != null) {
						// Activation complete: open the session like a successful login.
						request.getSession().removeAttribute("pendingActivationWalletId");
						request.getSession().removeAttribute("activationFallbackCode");
						request.getSession().setAttribute("wallet", wallet);
						WalletBalance walletBalance = new EWalletBalanceServiceImpl(dataSource)
								.getWalletBalanceByWalletId(wallet.getWalletId());
						request.getSession().setAttribute("walletBalance", walletBalance);
						response.sendRedirect("home.jsp" + LanguageUtil.langQuery(request));
						return;
					}
					errors.put("activationErr", "err.generic");
				}
			} catch (SQLException e) {
				errors = UserWalletValidator.parseSqlException(e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!errors.isEmpty()) {
			// Re-render the activation page with the error messages.
			request.setAttribute("errors", errors);
			try {
				request.getRequestDispatcher("activate.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles the "resendActivation" action: consumes the current valid code
	 * (if any), issues a fresh one, sends it on WhatsApp and returns the user
	 * to the activation page; the fresh code has a brand new attempts counter.
	 */
	private void resendActivation(HttpServletRequest request, HttpServletResponse response) {
		Long pendingWalletId = (Long) request.getSession().getAttribute("pendingActivationWalletId");
		if (pendingWalletId == null) {
			try {
				response.sendRedirect("login.jsp" + LanguageUtil.langQuery(request));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		try {
			// Invalidate any still-usable activation code so only the new one can be entered.
			ActivationCode existing = activationCodeService.getValidActivationCodeByWalletIdAndPurpose(pendingWalletId, "ACTIVATION");
			if (existing != null) {
				existing.setAttempts(3);
				existing.setIsUsed(1);
				existing.setIsExpire(1);
				activationCodeService.updateActivationCodeByWalletIdAndCode(existing);
			}
			ActivationCode fresh = new ActivationCode(pendingWalletId, TransactionUtil.generateActivationCode());
			fresh = activationCodeService.addActivationCode(fresh);

			Wallet wallet = eWalletUserService.getUserWalletById(pendingWalletId);
			if (wallet == null) {
				// Wallet disappeared between requests: drop the pending state.
				request.getSession().removeAttribute("pendingActivationWalletId");
				response.sendRedirect("login.jsp" + LanguageUtil.langQuery(request));
				return;
			}
			boolean sent = messageService.send(wallet.getPhoneNumber(),
					"Your E-Wallet activation code is " + fresh.getCode());
			request.getSession().removeAttribute("activationFallbackCode");
			if (!sent) {
				request.getSession().setAttribute("activationFallbackCode", fresh.getCode());
			}
			request.setAttribute("resent", true);
			request.getRequestDispatcher("activate.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
		} catch (SQLException e) {
			Map<String, String> errors = UserWalletValidator.parseSqlException(e);
			request.setAttribute("errors", errors);
			try {
				request.getRequestDispatcher("activate.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
			} catch (ServletException | IOException ex) {
				ex.printStackTrace();
			}
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the "forgotPin" action: starts the PIN-reset flow.
	 * Verifies the submitted phone number, resolves the wallet, consumes any
	 * still-valid code and issues + sends a fresh 6-digit WhatsApp code exactly
	 * like the activation flow, then moves the user to the code page. Calling it
	 * again without a "phone" parameter (the resend link) reuses the phone that
	 * was stored in the session during the first request.
	 */
	private void forgotPin(HttpServletRequest request, HttpServletResponse response) {
		String phoneNumber = request.getParameter("phone");
		String sessionPhone = (String) request.getSession().getAttribute("pendingResetPhone");
		boolean resend = phoneNumber == null && sessionPhone != null;
		if (resend) {
			// Resend: reuse the phone number stored when the flow was started.
			phoneNumber = sessionPhone;
		}

		Map<String, String> errors = UserWalletValidator.validateForResetRequest(phoneNumber);

		if (errors.isEmpty()) {
			try {
				Wallet wallet = eWalletUserService.getUserWalletByPhoneNumber(phoneNumber);
				if (wallet == null) {
					errors.put("phoneNumber", "err.phone.notFound");
				} else {
					// Invalidate any still-usable RESET code so only the new one can be entered.
					// (An ACTIVATION code, if any, is left untouched.)
					ActivationCode existing = activationCodeService.getValidActivationCodeByWalletIdAndPurpose(wallet.getWalletId(), "RESET");
					if (existing != null) {
						existing.setAttempts(3);
						existing.setIsUsed(1);
						existing.setIsExpire(1);
						activationCodeService.updateActivationCodeByWalletIdAndCode(existing);
					}
					ActivationCode resetCode = new ActivationCode(wallet.getWalletId(), TransactionUtil.generateActivationCode());
					resetCode.setPurpose("RESET");
					resetCode = activationCodeService.addActivationCode(resetCode);

					boolean sent = messageService.send(wallet.getPhoneNumber(),
							"Your E-Wallet PIN reset code is " + resetCode.getCode());
					request.getSession().setAttribute("pendingResetWalletId", wallet.getWalletId());
					request.getSession().setAttribute("pendingResetPhone", wallet.getPhoneNumber());
					request.getSession().removeAttribute("resetFallbackCode");
					if (!sent) {
						// WhatsApp unreachable: still show the code on the page as a fallback.
						request.getSession().setAttribute("resetFallbackCode", resetCode.getCode());
					}
					request.setAttribute("resent", resend);
					request.getRequestDispatcher("forgot-pin-code.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
					return;
				}
			} catch (SQLException e) {
				errors = UserWalletValidator.parseSqlException(e);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}

		if (!errors.isEmpty()) {
			// Re-render the request form with the validation errors.
			request.setAttribute("errors", errors);
			request.setAttribute("phoneNumberErr", phoneNumber);
			try {
				request.getRequestDispatcher("forgot-pin.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles the "resetPin" action: finishes the PIN-reset flow.
	 * Verifies the 6-digit WhatsApp reset code the same way the activation flow
	 * verifies its code (single still-valid row, attempts counter, expiry decided
	 * by the DB clock). On success the code is consumed and the new PIN is stored
	 * via {@link EWalletUserService#updateUserWalletPin(Wallet, String)}; the user
	 * is then sent back to the login page.
	 */
	private void resetPin(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String newPin = request.getParameter("newPin");
		String newPinConfirm = request.getParameter("newPin2");
		Long pendingResetWalletId = (Long) request.getSession().getAttribute("pendingResetWalletId");
		Map<String, String> errors = new HashMap<>();

		if (pendingResetWalletId == null) {
			// No pending reset in this session (page opened directly).
			errors.put("resetErr", "err.reset.invalidSession");
		} else if (code == null || !code.matches("\\d{6}")) {
			errors.put("codeErr", "err.reset.invalidFormat");
		} else {
			errors.putAll(UserWalletValidator.validateForUpdatePin(newPin, newPinConfirm));
			if (errors.isEmpty()) {
				try {
					ActivationCode stored = activationCodeService.getValidActivationCodeByWalletIdAndPurpose(pendingResetWalletId, "RESET");
					if (stored == null) {
						// No usable code: none was issued, or its 10-minute window has passed.
						errors.put("codeErr", "err.reset.expired");
					} else if (stored.getAttempts() >= 3) {
						errors.put("codeErr", "err.reset.locked");
					} else if (!stored.getCode().equals(code)) {
						// Wrong code: increment the attempts counter and fail.
						stored.setAttempts(stored.getAttempts() + 1);
						activationCodeService.updateActivationCodeByWalletIdAndCode(stored);
						errors.put("codeErr", "err.reset.wrong");
					} else {
						// Correct code: consume it and rotate the PIN.
						stored.setAttempts(stored.getAttempts() + 1);
						stored.setIsUsed(1);
						stored.setIsExpire(1);
						activationCodeService.updateActivationCodeByWalletIdAndCode(stored);

						Wallet wallet = eWalletUserService.getUserWalletById(pendingResetWalletId);
						wallet = eWalletUserService.updateUserWalletPin(wallet, newPin);
						if (wallet != null) {
							// Reset complete: clear the pending state and go back to login.
							request.getSession().removeAttribute("pendingResetWalletId");
							request.getSession().removeAttribute("pendingResetPhone");
							request.getSession().removeAttribute("resetFallbackCode");
							String langSuffix = LanguageUtil.langQuery(request).substring(1);
							response.sendRedirect("login.jsp?resetSuccess=1&" + langSuffix);
							return;
						}
						errors.put("resetErr", "err.generic");
					}
				} catch (SQLException e) {
					errors = UserWalletValidator.parseSqlException(e);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (!errors.isEmpty()) {
			// Re-render the code page with the error messages and the typed values.
			request.setAttribute("errors", errors);
			request.setAttribute("codeErrVal", code);
			request.setAttribute("newPinErrVal", newPin);
			request.setAttribute("newPin2ErrVal", newPinConfirm);
			try {
				request.getRequestDispatcher("forgot-pin-code.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
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
