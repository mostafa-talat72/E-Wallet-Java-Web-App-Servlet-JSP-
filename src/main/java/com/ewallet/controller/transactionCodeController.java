package com.ewallet.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.ewallet.model.TransactionCode;
import com.ewallet.model.Wallet;
import com.ewallet.model.WalletBalance;
import com.ewallet.service.TransactionCodeService;
import com.ewallet.service.TransactionService;
import com.ewallet.service.impl.EWalletBalanceServiceImpl;
import com.ewallet.service.impl.EWalletUserServiceImpl;
import com.ewallet.service.impl.TransactionCodeServiceImpl;
import com.ewallet.service.impl.TransactionServiceImpl;
import com.ewallet.util.LanguageUtil;
import com.ewallet.util.TransactionUtil;
import com.ewallet.util.TransactionValidator;

/**
  *http://localhost:8080/E-Wallet/transactionCodeController?action=generateCode
   *http://localhost:8080/E-Wallet/transactionCodeController?action=updateCodeStatus
 */
@WebServlet("/transactionCodeController")
public class transactionCodeController extends HttpServlet {
	@Resource(name = "jdbc/ewallet/dBconnection")
	private DataSource dataSource;
		
	private TransactionCodeService transactionCodeService;

	 @Override
   public void init() throws ServletException {
		 transactionCodeService = new TransactionCodeServiceImpl(dataSource);
   }
	 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if(action==null) {
			action = "notFoundPage";
		}
		switch(action) {
			case "generateCode":
			    generateCode(request, response);
			    break;
			case "updateCodeStatus":
				updateCodeStatus(request, response);
			    break;
			default:
				response.sendRedirect("error.jsp" + LanguageUtil.langQuery(request));
				break;
						
		}
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	private void generateCode(HttpServletRequest request, HttpServletResponse response) {
		BigDecimal amount = new BigDecimal(request.getParameter("amount"));
		String pin = request.getParameter("pin");
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		Wallet checkWallet = null;
		Map<String,String> errors = new HashMap<String, String>();
		boolean isGeneratedCode = false;
		try {
			checkWallet = new EWalletUserServiceImpl(dataSource).login(new Wallet(wallet.getPhoneNumber(), pin));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(checkWallet != null) {
			WalletBalance currWalletBalance = new EWalletBalanceServiceImpl(dataSource).getWalletBalanceByWalletId(wallet.getWalletId());
			TransactionValidator.checkAmount(amount, errors);
			if(currWalletBalance != null && errors.isEmpty()) {

				TransactionCode transactionCode = new TransactionCode(wallet.getWalletId(), null, amount);
				while(true) {
					try {
						String code =TransactionUtil.generateTransactionCode();
						transactionCode.setCode(code);
						transactionCode =  new TransactionCodeServiceImpl(dataSource).addTransactionCode(transactionCode);
						isGeneratedCode = true;
						request.setAttribute("done", "1");
						request.setAttribute("transactionCodeVal", transactionCode.getCode());
						request.setAttribute("created_at", transactionCode.getCreatedAt());
						request.setAttribute("expires_at", transactionCode.getExpiresAt());
						request.setAttribute("amountVal", amount);
						break;
					}catch (SQLException e) {
						String msg = e.getMessage();

						if (msg != null && msg.contains("UQ_WALLET_CODE")) {
							transactionCode.setCode(null);
							continue;
						}
						break;
					}
				}
			}
		}
		if(!isGeneratedCode) {
			request.setAttribute("error", "err.otp.wrong");
			if(errors.isEmpty())
			{
				request.setAttribute("errors", errors);
			}

		}
		
		 try {
			request.setAttribute("pinVal", pin);
			request.setAttribute("amountVal", amount);

	        request.getRequestDispatcher("atmotp.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
	    } catch (ServletException | IOException e) {
	        e.printStackTrace();
	    }
	}
	

	private void updateCodeStatus(HttpServletRequest request, HttpServletResponse response) {
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		String code = request.getParameter("code");
		TransactionCode transactionCode = null;
		boolean isCodeUpdated = false;

		if(wallet != null && code != null) {
			transactionCode = transactionCodeService.getValidTransactionCodeByWalletIdAndCode(wallet.getWalletId());
		}

		if(transactionCode != null) {
			transactionCode.setIsExpire(1);
			transactionCode.setIsUsed(1);
			
			boolean isUpdated = transactionCodeService.updateTransactionCodeByWalletIdAndCode(transactionCode);
			if(isUpdated) {
				isCodeUpdated = true;
			}
		}

		if(isCodeUpdated) {
			request.setAttribute("error", "err.otp.expired");
		} else {
			request.setAttribute("error", "err.otp.wrong");
		}

		try {
	        request.getRequestDispatcher("atmotp.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
	    } catch (ServletException | IOException e) {
	        e.printStackTrace();
	    }
	}


}
