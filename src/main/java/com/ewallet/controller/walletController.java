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
import com.ewallet.service.impl.CardServiceImpl;
import com.ewallet.service.impl.EWalletBalanceServiceImpl;
import com.ewallet.service.impl.EWalletUserServiceImpl;
import com.ewallet.util.LanguageUtil;
import com.ewallet.util.UserWalletValidator;


/**
 * Servlet implementation class walletController
 */
/*http://localhost:8080/E-Wallet/walletController?action=signup
 *http://localhost:8080/E-Wallet/walletController?action=login
 *http://localhost:8080/E-Wallet/walletController?action=updateUserWallet
 *http://localhost:8080/E-Wallet/walletController?action=updateUserWalletPin
 *http://localhost:8080/E-Wallet/walletController?action=deleteUserWallet
 *http://localhost:8080/E-Wallet/walletController?action=logout
 *http://localhost:8080/E-Wallet/walletController
 *http://localhost:8080/E-Wallet/walletController?action=ascls
 * */
@WebServlet("/walletController")
public class walletController extends HttpServlet {
	
	@Resource(name = "jdbc/ewallet/dBconnection")
	private DataSource dataSource;
	
	private EWalletUserService eWalletUserService;

	
	 @Override
    public void init() throws ServletException {
	 eWalletUserService = new EWalletUserServiceImpl(dataSource);
    }

	
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


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	
	private void signup(HttpServletRequest request, HttpServletResponse response) {
		String phoneNumber = request.getParameter("phone");
		String nationalId = request.getParameter("nationalId");
		String fullName = request.getParameter("fullName");
		String pin = request.getParameter("pin");
		String pinConfirm = request.getParameter("pinConfirm");
		String salt = "123456789mnjlhgk";
		
		Map<String, String> errors = UserWalletValidator.validateForSignup(fullName,nationalId, phoneNumber, pin, pinConfirm);
		
		if(errors.isEmpty()){
			Wallet newWallet = new Wallet(phoneNumber, nationalId, fullName, pin,salt);
			
			try {
				newWallet = eWalletUserService.signup(newWallet);
			} catch (SQLException e) {
				errors = UserWalletValidator.parseSqlException(e);
			}
			
			if(errors.isEmpty() && newWallet != null) {
				
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


	private void login(HttpServletRequest request, HttpServletResponse response) {
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

	private void updateUserWalletPin(HttpServletRequest request, HttpServletResponse response) {
		String currentPin = request.getParameter("curPin");
		String newPin = request.getParameter("newPin");
		String newPinConfirm = request.getParameter("newPin2");
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		Map<String, String> errors = UserWalletValidator.validateForUpdatePin(newPin, newPinConfirm);
		if(!wallet.getPinHash().equals(currentPin)) {
			errors.put("curPinErr", "err.curPin.wrong");
		}
		if(errors.isEmpty()) {
			wallet = new Wallet(wallet.getWalletId(), wallet.getFullName(), newPin, wallet.getSalt());
			
			try {
				wallet = eWalletUserService.updateUserWallet(wallet);
			} catch (SQLException e) {
				errors = UserWalletValidator.parseSqlException(e);
			}
			
			if(errors.isEmpty() && wallet != null) {
				request.getSession().setAttribute("wallet", wallet);
			} else if(wallet == null) {
				errors.put("updatePinErr", "err.generic");
			}
		}
		
		if(!errors.isEmpty()) {
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

	private void deleteUserWallet(HttpServletRequest request, HttpServletResponse response) {
		String phoneNumber = request.getParameter("phone");
		String pin = request.getParameter("pin");
		
		Map<String, String> errors = UserWalletValidator.validateForLogin(phoneNumber, pin);
		
		if(errors.isEmpty()) {
		
			Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
			
			Wallet deletedWallet = new Wallet(phoneNumber, pin);
	
			try {
				deletedWallet = eWalletUserService.login(deletedWallet);
			} catch (SQLException e) {
				errors = UserWalletValidator.parseSqlException(e);
			}
			
			if(errors.isEmpty() && deletedWallet != null) {
				
				boolean deleteWalletBalance = new EWalletBalanceServiceImpl(dataSource).deleteWalletBalanceByWalletId(wallet.getWalletId());
				
				boolean updateAccountStatus = new AccountServiceImpl(dataSource).updateAccountStatusByRefereceIdAndTypeId(wallet.getWalletId(), 1);
				
				boolean deleteAllCards= new CardServiceImpl(dataSource).deleteAllCardsByWalletId(wallet.getWalletId());

				if(!deleteWalletBalance || !updateAccountStatus || !deleteAllCards) {
					errors.put("deletedError", "err.delete.failed");
					
				}else {
					boolean isDeleted = false;
					try {
						isDeleted = eWalletUserService.deleteUserWallet(wallet, deletedWallet);
					} catch (SQLException e) {
						errors = UserWalletValidator.parseSqlException(e);
					}
	
					if(errors.isEmpty() && isDeleted) {
						request.getSession().invalidate();
						try {
							response.sendRedirect("login.jsp" + LanguageUtil.langQuery(request));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else if(isDeleted == false) {
						errors.put("deletedError", "err.delete.failed");
					}
				}
			}else if(deletedWallet == null) {
				errors.put("deletedError", "err.login.failed");
			}
		}
		
		if(!errors.isEmpty()) {
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
	
	private void logout(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().invalidate();
		try {
			response.sendRedirect("login.jsp" + LanguageUtil.langQuery(request));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
