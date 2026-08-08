package com.ewallet.controller;

import java.io.IOException;
import java.io.PrintWriter;

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


/**
 * Servlet implementation class walletController
 */
/*http://localhost:8080/E-Wallet/walletController?action=signup
 *http://localhost:8080/E-Wallet/walletController?action=login
 *http://localhost:8080/E-Wallet/walletController?action=updateUserWallet
 *http://localhost:8080/E-Wallet/walletController?action=updateUserWalletPin
 *http://localhost:8080/E-Wallet/walletController?action=deleteUserWallet
 *http://localhost:8080/E-Wallet/walletController
 *http://localhost:8080/E-Wallet/walletController?action=ascls
 * */
@WebServlet("/walletController")
public class walletController extends HttpServlet {
	
	@Resource(name = "jdbc/ewallet/dBconnection")
	private DataSource dataSource;
	
	private EWalletUserService eWalletUserService;
    PrintWriter outPrinter = null;

	
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
			default:
				response.sendRedirect("error.jsp"+langQuery(request));
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
		Wallet newWallet = new Wallet(phoneNumber, nationalId, fullName, pin,salt);
		newWallet = eWalletUserService.signup(newWallet);
		
		if(newWallet != null) {
			
			EWalletBalanceService eWalletBalanceService = new EWalletBalanceServiceImpl(dataSource);
			
			eWalletBalanceService.createWalletBalance(new WalletBalance(newWallet.getWalletId()));
			
			AccountService accountService = new AccountServiceImpl(dataSource);
			accountService.addAcount(new Account(1, newWallet.getWalletId()));
			try {
				response.sendRedirect("login.jsp" + langQuery(request));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
		    request.setAttribute("phoneNumberErr", phoneNumber);
		    request.setAttribute("pinErr", pin);
		    try {
				request.getRequestDispatcher("login.jsp" +  langQuery(request)).forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}		}
	}


	private void login(HttpServletRequest request, HttpServletResponse response) {
		
		String phoneNumber = request.getParameter("phone");
		String pin = request.getParameter("pin");
		Wallet wallet = new Wallet(phoneNumber, pin);
		
		wallet = eWalletUserService.login(wallet);
		
		if(wallet != null) {
			request.getSession().setAttribute("wallet", wallet);
			WalletBalance walletBalance = new EWalletBalanceServiceImpl(dataSource).getWalletBalanceByWalletId(wallet.getWalletId());
			request.getSession().setAttribute("walletBalance", walletBalance);
	        try {
				response.sendRedirect("home.jsp" + langQuery(request));
			} catch (IOException e) {
				e.printStackTrace();
			} 
		} else {
		    request.setAttribute("loginError", "err.login.failed");
		    request.setAttribute("phoneNumberErr", phoneNumber);
		    request.setAttribute("pinErr", pin);
		    try {
				request.getRequestDispatcher("login.jsp" +  langQuery(request)).forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
		
	}


	private void updateUserWallet(HttpServletRequest request, HttpServletResponse response) {

		String fullName = request.getParameter("fullName");
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		wallet.setFullName(fullName);
		System.out.println("Updating wallet with ID: " + wallet.getWalletId() + " and new full name: " + fullName);

		wallet = eWalletUserService.updateUserWallet(wallet);
		if(wallet != null) {
			request.getSession().setAttribute("wallet", wallet);
			try {
				response.sendRedirect("profile.jsp" + langQuery(request));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			outPrinter.println("Wallet update failed. Please try again.");
		}
	}

	private void updateUserWalletPin(HttpServletRequest request, HttpServletResponse response) {
		String currentPin = request.getParameter("curPin");
		String newPin = request.getParameter("newPin");
		String newPinConfirm = request.getParameter("newPin2");
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");

		if(!wallet.getPinHash().equals(currentPin)) {
			outPrinter.println("Current PIN is incorrect.");
			return;
		}else if(!newPin.equals(newPinConfirm)) {
			outPrinter.println("New PIN and confirmation do not match.");
			return;
		}
		wallet = new Wallet(wallet.getWalletId(), wallet.getFullName(), newPin, wallet.getSalt());
		wallet = eWalletUserService.updateUserWallet(wallet);
		if(wallet != null) {
			request.getSession().setAttribute("wallet", wallet);
			try {
				response.sendRedirect("profile.jsp" + langQuery(request));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			outPrinter.println("Wallet PIN update failed. Please try again.");
		}		
	}

	private void deleteUserWallet(HttpServletRequest request, HttpServletResponse response) {
		String phoneNumber = request.getParameter("phone");
		String pin = request.getParameter("pin");
		
		
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		
		Wallet deletedWallet = new Wallet(phoneNumber, pin);

		if(eWalletUserService.login(deletedWallet) == null) {
			outPrinter.println("Invalid credentials. Please check your phone number and PIN.");
			return;
		}
		
		boolean deleteWalletBalance = new EWalletBalanceServiceImpl(dataSource).deleteWalletBalanceByWalletId(wallet.getWalletId());
		if(!deleteWalletBalance) {
			outPrinter.println("Failed to delete wallet balance. Please try again.");
			return;
		}
		
		boolean deleteAccount = new AccountServiceImpl(dataSource).deleteAccountByRefereceIdAndTypeId(wallet.getWalletId(), 1);
		
		if(!deleteAccount) {
			outPrinter.println("Failed to delete account. Please try again.");
			return;
		}
		
		boolean isDeleted = eWalletUserService.deleteUserWallet(wallet, deletedWallet);
		
		if(isDeleted) {
			request.getSession().invalidate();
			try {
				response.sendRedirect("login.jsp" + langQuery(request));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			outPrinter.println("Wallet deletion failed. Please check your credentials.");
		}
	}

	
	private String langQuery(HttpServletRequest req) {
	    Object lang = req.getSession().getAttribute("lang");
	    return lang != null && lang.equals("en") ? "?lang=en" : "?lang=ar";
	}

}
