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
 * Controller exposing ATM information to the user-facing ATM pages.
 * Provides the list of registered ATMs for the map view and the
 * details of a single ATM for the ATM machine page.
 *
 * URL mapping: /atmController
 *
 * Exposed actions (via the "action" request parameter):
 *  - getAllATMs : loads all ATMs and forwards to the ATM map page
 *  - getATMById : loads a single ATM by id and forwards to the ATM machine page
 *  - (any other/missing): redirects to the error page
 *
 * Examples:
 *  http://localhost:8080/E-Wallet/atmController?action=getAllATMs
 *  http://localhost:8080/E-Wallet/atmController?action=getATMById&atmId=1
 */
@WebServlet("/atmController")
public class atmController extends HttpServlet {

	@Resource(name = "jdbc/ewallet/dBconnection")
	private DataSource dataSource;

	private ATMService atmService;
	private TransactionCodeService codeService;
	private TransactionService transactionService;

	/**
	 * Servlet initialization hook. Constructs the services used by this
	 * controller: the ATM service for ATM lookups, plus the transaction
	 * code and transaction services kept available for ATM operations.
	 */
	@Override
	public void init() throws ServletException {
		atmService = new ATMServiceImpl(dataSource);
		codeService = new TransactionCodeServiceImpl(dataSource);
		transactionService = new TransactionServiceImpl(dataSource);
	}

	/**
	 * GET entry point of the controller. Reads the "action" request parameter
	 * and dispatches to the matching action method. Also handles POST
	 * requests because doPost delegates to doGet.
	 *
	 * @param request  the HTTP request
	 * @param response the HTTP response
	 * @throws ServletException if a forward/redirect fails
	 * @throws IOException      if the response cannot be written
	 */
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

	/**
	 * POST entry point of the controller. Delegates all POST requests to
	 * doGet so that both HTTP verbs share the same action-dispatch logic.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Handles the "getAllATMs" action: loads all ATM machines from the
	 * service and forwards to the ATM map page for display.
	 */
	private void getAllATMs(HttpServletRequest request, HttpServletResponse response) {
		List<ATM> atms = atmService.getAllATMs();
		try {
			request.setAttribute("atms", atms);
			request.getRequestDispatcher("/atm/atm-map.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the "getATMById" action: loads a single ATM by its id and
	 * forwards to the ATM machine page, where deposits and withdrawals
	 * are performed.
	 */
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
