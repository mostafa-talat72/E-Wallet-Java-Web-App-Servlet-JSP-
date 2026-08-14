package com.ewallet.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.ewallet.model.Account;
import com.ewallet.model.ATM;
import com.ewallet.model.Transaction;
import com.ewallet.model.TransactionCode;
import com.ewallet.model.Wallet;
import com.ewallet.model.WalletBalance;
import com.ewallet.service.AccountService;
import com.ewallet.service.ATMService;
import com.ewallet.service.EWalletBalanceService;
import com.ewallet.service.EWalletUserService;
import com.ewallet.service.TransactionCodeService;
import com.ewallet.service.TransactionService;
import com.ewallet.service.impl.AccountServiceImpl;
import com.ewallet.service.impl.ATMServiceImpl;
import com.ewallet.service.impl.EWalletBalanceServiceImpl;
import com.ewallet.service.impl.EWalletUserServiceImpl;
import com.ewallet.service.impl.TransactionCodeServiceImpl;
import com.ewallet.service.impl.TransactionServiceImpl;
import com.ewallet.util.LanguageUtil;
import com.ewallet.util.TransactionUtil;

/**
 * http://localhost:8080/E-Wallet/atmController?action=getAllATMs
 * http://localhost:8080/E-Wallet/atmController?action=execute&atmId=1&phone=0111...&code=123456&type=deposit
 */
@WebServlet("/atmController")
public class atmController extends HttpServlet {

	@Resource(name = "jdbc/ewallet/dBconnection")
	private DataSource dataSource;

	private ATMService atmService;
	private TransactionCodeService codeService;
	private TransactionService transactionService;

	@Override
	public void init() throws ServletException {
		atmService = new ATMServiceImpl(dataSource);
		codeService = new TransactionCodeServiceImpl(dataSource);
		transactionService = new TransactionServiceImpl(dataSource);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action == null)
			action = "notFoundPage";

		switch (action) {
			case "getAllATMs":
				getAllATMs(request, response);
				break;
			case "getATMById":
				getATMById(request, response);
				break;
			default:
				response.sendRedirect("error.jsp" + LanguageUtil.langQuery(request));
				break;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void getAllATMs(HttpServletRequest request, HttpServletResponse response) {
		List<ATM> atms = atmService.getAllATMs();
		try {
			request.setAttribute("atms", atms);
			request.getRequestDispatcher("/atm/atm-map.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	private void getATMById(HttpServletRequest request, HttpServletResponse response) throws IOException {
		long atmId = Long.parseLong(request.getParameter("atmId"));
		ATM atm = atmService.getATMById(atmId);
		if(atm != null) {
			request.setAttribute("atm", atm);
			try {
				request.getRequestDispatcher("/atm/atm-machine.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
			} catch (ServletException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	
}
