package com.ewallet.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.ewallet.model.Account;
import com.ewallet.model.Card;
import com.ewallet.model.Transaction;
import com.ewallet.model.Wallet;
import com.ewallet.model.WalletBalance;
import com.ewallet.service.TransactionService;
import com.ewallet.service.impl.ATMServiceImpl;
import com.ewallet.service.impl.AccountServiceImpl;
import com.ewallet.service.impl.CardServiceImpl;
import com.ewallet.service.impl.EWalletBalanceServiceImpl;
import com.ewallet.service.impl.EWalletUserServiceImpl;
import com.ewallet.service.impl.TransactionExecutor;
import com.ewallet.service.impl.TransactionServiceImpl;
import com.ewallet.util.LanguageUtil;
import com.ewallet.util.TransactionValidator;
import com.ewallet.util.UserWalletValidator;

/*http://localhost:8080/E-Wallet/transactionControllerr?action=addMoney
 *http://localhost:8080/E-Wallet/transactionController?action=deposit
 *http://localhost:8080/E-Wallet/transactionController?action=transfer
 *http://localhost:8080/E-Wallet/transactionController?action=atmExecute
 *http://localhost:8080/E-Wallet/transactionController?action=allTtransaction
 *http://localhost:8080/E-Wallet/transactionController
 *http://localhost:8080/E-Wallet/transactionController?action=ascls
 * */
@WebServlet("/transactionController")
public class transactionController extends HttpServlet {
	
	@Resource(name = "jdbc/ewallet/dBconnection")
	private DataSource dataSource;
		
private TransactionService transactionService;
	private TransactionExecutor transactionExecutor;

	
	 @Override
    public void init() throws ServletException {
		 transactionService = new TransactionServiceImpl(dataSource);
		 transactionExecutor = new TransactionExecutor(dataSource);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if(action==null) {
			action = "notFoundPage";
		}
		
		switch(action) {
			case "addMoney":
				addMoney(request, response);
				break;
			case "atmExecute":
			    String type = request.getParameter("type");
			    if ("withdraw".equals(type)) {
			        withdraw(request, response);
			    } else if ("deposit".equals(type)) {
			        deposit(request, response);
			    } else {
			        response.setContentType("application/json;charset=UTF-8");
					response.getWriter().write("{\"ok\":false,\"error\":\"err.atm.invalid_code\"}");
			    }
			    break;
			    
			case "transfer":
				transfer(request, response);
				break;
			case "allTtransaction":
				allTtransaction(request, response);
				break;
			default:
				response.sendRedirect("error.jsp" + LanguageUtil.langQuery(request));
				break;
						
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	

	private void addMoney(HttpServletRequest request, HttpServletResponse response) {
		String pin = request.getParameter("pin");
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");

		boolean isBalanceAdded = false;
		try {
			Wallet checkWalletExist = new EWalletUserServiceImpl(dataSource).login(new Wallet(wallet.getPhoneNumber(), pin));
			if (checkWalletExist != null) {
				String cardNumber = request.getParameter("cardNumber");
				BigDecimal amount = new BigDecimal(request.getParameter("amount"));

				Card card = new CardServiceImpl(dataSource).getCardByWalletIdAndCardNumber(wallet.getWalletId(), cardNumber);
				if (card != null) {
					String transactionReference = transactionExecutor.addMoney(wallet.getWalletId(), card.getCardId(), amount);
					WalletBalance newBalance = new EWalletBalanceServiceImpl(dataSource).getWalletBalanceByWalletId(wallet.getWalletId());
					if (newBalance != null) {
						request.getSession().setAttribute("walletBalance", newBalance);
						isBalanceAdded = true;
						request.setAttribute("done", "1");
						request.setAttribute("cardNumberTransaction", "•••• •••• •••• " + cardNumber.substring(12));
						request.setAttribute("transactionReference", transactionReference);
						request.setAttribute("created_at", new Timestamp(System.currentTimeMillis()));
					}
				}
			}
		} catch (NumberFormatException | TransactionExecutor.TxException e) {
			e.printStackTrace();
			request.setAttribute("error", e instanceof TransactionExecutor.TxException
					? ((TransactionExecutor.TxException) e).getErrorKey() : "err.payment.failed");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (!isBalanceAdded) {
			request.setAttribute("error", "err.payment.failed");
		}
		
		 try {
	        List<Card> cards = new CardServiceImpl(dataSource).getAllCardsByWalletId(wallet.getWalletId());
	        request.setAttribute("cards", cards);
	        request.getRequestDispatcher("add-money.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
	    } catch (ServletException | IOException e) {
	        e.printStackTrace();
	    }

	}


	
	
	private void transfer(HttpServletRequest request, HttpServletResponse response) {
		String recipientPhone = request.getParameter("recipient");
		BigDecimal amount = new BigDecimal(request.getParameter("amount"));
		String note = request.getParameter("note");
		String pin = request.getParameter("pin");
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		boolean isBalanceAdded = false;

		Map<String,String> errors = TransactionValidator.validateSendMoney(recipientPhone, amount, pin);
		if(errors.isEmpty()) {
			Wallet recipientWallet = null;
			try {
				 recipientWallet = new EWalletUserServiceImpl(dataSource).getUserWalletByPhoneNumber(recipientPhone);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(recipientWallet != null && !wallet.getPhoneNumber().equals(recipientPhone)) {
				try {
					Wallet checkWalletExist = new EWalletUserServiceImpl(dataSource).login(new Wallet(wallet.getPhoneNumber(), pin));
					if (checkWalletExist != null) {
						String transactionReference = transactionExecutor.transfer(
								wallet.getWalletId(), recipientWallet.getWalletId(), amount, note);
						WalletBalance currWalletBalance = new EWalletBalanceServiceImpl(dataSource).getWalletBalanceByWalletId(wallet.getWalletId());
						if (currWalletBalance != null) {
							request.getSession().setAttribute("walletBalance", currWalletBalance);
							isBalanceAdded = true;
							request.setAttribute("done", "1");
							request.setAttribute("sendWallet", wallet.getPhoneNumber());
							request.setAttribute("recipientWallet", recipientWallet.getPhoneNumber());
							request.setAttribute("transactionReference", transactionReference);
							request.setAttribute("created_at", new Timestamp(System.currentTimeMillis()));
						}
					}
				} catch (TransactionExecutor.TxException e) {
					if ("err.amount.insufficient".equals(e.getErrorKey()) || "err.amount.invalid".equals(e.getErrorKey())) {
						errors.put("amount", e.getErrorKey());
					} else {
						request.setAttribute("error", e.getErrorKey());
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(!isBalanceAdded) {
			request.setAttribute("error", "err.payment.failed");
			if(errors.isEmpty())
			{
				request.setAttribute("errors", errors);
			}

		}
		
		 try {
			request.setAttribute("recipientPhoneVal", recipientPhone);
			request.setAttribute("pinVal", pin);
			request.setAttribute("amountVal", amount);
			request.setAttribute("feesVal", amount.divide(new BigDecimal(1000)));
			request.setAttribute("noteVal", note);
	        request.getRequestDispatcher("send-money.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
	    } catch (ServletException | IOException e) {
	        e.printStackTrace();
	    }
	}

private void deposit(HttpServletRequest request, HttpServletResponse response) {
		 try {
		        long atmId      = Long.parseLong(request.getParameter("atmId"));
		        String phone    = request.getParameter("phone");       
		        String code     = request.getParameter("code");       
		        BigDecimal amount = new BigDecimal(request.getParameter("amount"));

		        Wallet wallet = new EWalletUserServiceImpl(dataSource).getUserWalletByPhoneNumber(phone);
		        if(wallet==null) {
		        	throw new TransactionExecutor.TxException("err.atm.invalid_code");
		        }
		        String transactionReference = transactionExecutor.atmDeposit(atmId, wallet.getWalletId(), code, amount);
		        response.setContentType("application/json;charset=UTF-8");
		        response.getWriter().write("{\"ok\":true,\"amount\":"+amount +",\"ref\":\"" + transactionReference +"\"}");
		    } catch (TransactionExecutor.TxException e) {
		        e.printStackTrace();
		        response.setContentType("application/json;charset=UTF-8");
		        try {
					response.getWriter().write("{\"ok\":false,\"error\":\"" + e.getErrorKey() + "\"}");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    } catch (Exception e) {
		        e.printStackTrace();
		        response.setContentType("application/json;charset=UTF-8");
		        try {
					response.getWriter().write("{\"ok\":false,\"error\":\"err.atm.invalid_code\"}");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    }		
	}

	private void withdraw(HttpServletRequest request, HttpServletResponse response) {
		try {
	        long atmId      = Long.parseLong(request.getParameter("atmId"));
	        String phone    = request.getParameter("phone");       
	        String code     = request.getParameter("code");       
	        BigDecimal amount = new BigDecimal(request.getParameter("amount"));

	        Wallet wallet = new EWalletUserServiceImpl(dataSource).getUserWalletByPhoneNumber(phone);
	        if(wallet==null) {
	        	throw new TransactionExecutor.TxException("err.atm.invalid_code");
	        }
	        String transactionReference = transactionExecutor.atmWithdraw(atmId, wallet.getWalletId(), code, amount);
	        response.setContentType("application/json;charset=UTF-8");
	        response.getWriter().write("{\"ok\":true,\"amount\":"+ amount +",\"ref\":\"" + transactionReference +"\"}");
	    } catch (TransactionExecutor.TxException e) {
	        e.printStackTrace();
	        response.setContentType("application/json;charset=UTF-8");
	        try {
				response.getWriter().write("{\"ok\":false,\"error\":\"" + e.getErrorKey() + "\"}");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.setContentType("application/json;charset=UTF-8");
	        try {
				response.getWriter().write("{\"ok\":false,\"error\":\"err.atm.invalid_code\"}");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    }		
	}


	private void allTtransaction(HttpServletRequest request, HttpServletResponse response) {
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		Account walletAccount = new AccountServiceImpl(dataSource).getAccountByRefereceIdAndTypeId(wallet.getWalletId(), 1);
		if(wallet != null && walletAccount != null) {
			List<Transaction> transactions = transactionService.getAllTransactions(walletAccount.getAccountId());
			List<Map.Entry<String, String>> toOrFromNames = new ArrayList<Map.Entry<String, String>>();
			for(Transaction transaction : transactions) {
				long accountId = 0;
				if(transaction.getToAccountId().equals(walletAccount.getAccountId())) {
					accountId = transaction.getFromAccountId();
				}else {
					accountId = transaction.getToAccountId();
				}
				String toOrfrom =accountId == transaction.getToAccountId()? "to" : "from";
				Account otherAccount = new AccountServiceImpl(dataSource).getAccountByAccountId(accountId); 
				if(otherAccount.getAccountTypeId() == 1) {
					try {
						toOrFromNames.add(Map.entry(new EWalletUserServiceImpl(dataSource).getUserWalletById(otherAccount.getReferenceId()).getPhoneNumber(),toOrfrom));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(otherAccount.getAccountTypeId() == 2) {
					toOrFromNames.add(Map.entry("Card •••• •••• •••• " + new CardServiceImpl(dataSource).getCardByCardId(otherAccount.getReferenceId()).getCardNumber().substring(12),toOrfrom));
				} else {
					toOrFromNames.add(Map.entry(new ATMServiceImpl(dataSource).getATMById(otherAccount.getReferenceId()).getAtmName(),toOrfrom));
				}
			}
			
			request.setAttribute("transactions", transactions);
			request.setAttribute("toOrFromNames", toOrFromNames);
	        try {
				request.getRequestDispatcher("transactions.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

}
