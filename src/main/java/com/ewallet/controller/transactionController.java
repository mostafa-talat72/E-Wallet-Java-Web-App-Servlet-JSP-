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

import com.ewallet.model.ATM;
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

/**
 * Controller handling all payment and transaction operations of the wallet.
 *
 * URL mapping: /transactionController
 *
 * Exposed actions (via the "action" request parameter):
 *  - addMoney         : loads money onto the wallet using a registered card
 *  - transfer         : sends money from the wallet to another wallet
 *  - atmExecute       : performs an ATM cash deposit/withdraw (see the "type"
 *                       parameter: "deposit" or "withdraw"); responds with JSON
 *  - allTtransaction  : lists all transactions of the wallet account
 *  - (any other/missing): redirects to the error page
 *
 * Examples:
 *  http://localhost:8080/E-Wallet/transactionController?action=addMoney
 *  http://localhost:8080/E-Wallet/transactionController?action=transfer
 *  http://localhost:8080/E-Wallet/transactionController?action=atmExecute
 *  http://localhost:8080/E-Wallet/transactionController?action=allTtransaction
 */
@WebServlet("/transactionController")
public class transactionController extends HttpServlet {
	
	@Resource(name = "jdbc/ewallet/dBconnection")
	private DataSource dataSource;
		
private TransactionService transactionService;
	private TransactionExecutor transactionExecutor;

	
	/**
	 * Servlet initialization hook. Constructs the TransactionService used for
	 * reading transactions and the TransactionExecutor used for executing
	 * money movements within a database transaction.
	 */
	 @Override
    public void init() throws ServletException {
		 transactionService = new TransactionServiceImpl(dataSource);
		 transactionExecutor = new TransactionExecutor(dataSource);
    }

	/**
	 * GET entry point of the controller. Reads the "action" request parameter
	 * and dispatches to the matching action method. For the "atmExecute" action
	 * the "type" parameter decides whether a deposit or a withdrawal is run.
	 * Also handles POST requests because doPost delegates to doGet.
	 *
	 * @param request  the HTTP request
	 * @param response the HTTP response
	 * @throws ServletException if a forward/redirect fails
	 * @throws IOException      if the response cannot be written
	 */
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
			        // Unknown ATM operation type: reply with a JSON error message.
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


	/**
	 * POST entry point of the controller. Delegates all POST requests to
	 * doGet so that both HTTP verbs share the same action-dispatch logic.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	

	/**
	 * Handles the "addMoney" action: loads funds onto the wallet using a
	 * registered card. Verifies the user's PIN via a login check, validates
	 * the card ownership, executes the top-up, and refreshes the session
	 * balance. Re-renders the add-money page with the result.
	 */
	private void addMoney(HttpServletRequest request, HttpServletResponse response) {
		String pin = request.getParameter("pin");
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");

		boolean isBalanceAdded = false;
		try {
			// Verify the supplied PIN by attempting a login with it.
			Wallet checkWalletExist = new EWalletUserServiceImpl(dataSource).login(new Wallet(wallet.getPhoneNumber(), pin));
			if (checkWalletExist != null) {
				String cardNumber = request.getParameter("cardNumber");
				BigDecimal amount = new BigDecimal(request.getParameter("amount"));

				// The card must belong to the logged-in wallet.
				Card card = new CardServiceImpl(dataSource).getCardByWalletIdAndCardNumber(wallet.getWalletId(), cardNumber);
				if (card != null) {
					String transactionReference = transactionExecutor.addMoney(wallet.getWalletId(), card.getCardId(),cardNumber, amount);
					WalletBalance newBalance = new EWalletBalanceServiceImpl(dataSource).getWalletBalanceByWalletId(wallet.getWalletId());
					if (newBalance != null) {
						// Keep the session balance up to date and expose the receipt data to the JSP.
						request.getSession().setAttribute("walletBalance", newBalance);
						isBalanceAdded = true;
						request.setAttribute("done", "1");
						// Mask the card number for display: only the last 4 digits are shown.
						request.setAttribute("cardNumberTransaction", "•••• •••• •••• " + cardNumber.substring(12));
						request.setAttribute("transactionReference", transactionReference);
						request.setAttribute("created_at", new Timestamp(System.currentTimeMillis()));
					}
				}
			}
		} catch (NumberFormatException | TransactionExecutor.TxException e) {
			// Resolve a user-facing error key from the executed transaction exception.
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
	        // Reload the user's cards so the form can be re-rendered with them.
	        List<Card> cards = new CardServiceImpl(dataSource).getAllCardsByWalletId(wallet.getWalletId());
	        request.setAttribute("cards", cards);
	        request.getRequestDispatcher("add-money.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
	    } catch (ServletException | IOException e) {
	        e.printStackTrace();
	    }

	}


	
	
	/**
	 * Handles the "transfer" action: sends money from the wallet to another
	 * wallet. Validates the recipient, amount and PIN, looks up the recipient
	 * wallet, executes the transfer, and refreshes the session balance.
	 * Re-renders the send-money page with the result or the errors.
	 */
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
			// The recipient must exist and must not be the sender's own wallet.
			if(recipientWallet != null && !wallet.getPhoneNumber().equals(recipientPhone)) {
				try {
					// Verify the sender's PIN through a login check before moving money.
					Wallet checkWalletExist = new EWalletUserServiceImpl(dataSource).login(new Wallet(wallet.getPhoneNumber(), pin));
					if (checkWalletExist != null) {
						if(note.isEmpty()) {
							note = "From "+ wallet.getPhoneNumber() + " to " + recipientPhone;
						}
						String transactionReference = transactionExecutor.transfer(
								wallet.getWalletId(), recipientWallet.getWalletId(), amount, note);
						WalletBalance currWalletBalance = new EWalletBalanceServiceImpl(dataSource).getWalletBalanceByWalletId(wallet.getWalletId());
						if (currWalletBalance != null) {
							// Refresh the session balance and expose the receipt data to the JSP.
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
					// Amount-related business failures are shown next to the amount field.
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
			// Re-render the send-money form, preserving the input and the computed fee.
			request.setAttribute("recipientPhoneVal", recipientPhone);
			request.setAttribute("pinVal", pin);
			request.setAttribute("amountVal", amount);
			// Fee display: 0.1% of the transferred amount (amount / 1000).
			request.setAttribute("feesVal", amount.divide(new BigDecimal(1000)));
			request.setAttribute("noteVal", note);
	        request.getRequestDispatcher("send-money.jsp" + LanguageUtil.langQuery(request)).forward(request, response);
	    } catch (ServletException | IOException e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * Handles the "atmExecute" action with type "deposit": executes an ATM
	 * cash deposit for the wallet identified by the phone number, using the
	 * one-time transaction code. Responds with a JSON receipt or an error.
	 */
private void deposit(HttpServletRequest request, HttpServletResponse response) {
		 try {
		        long atmId      = Long.parseLong(request.getParameter("atmId"));
		        String phone    = request.getParameter("phone");       
		        String code     = request.getParameter("code");       
		        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
		        ATM atm = new ATMServiceImpl(dataSource).getATMById(atmId);
		        // Resolve the wallet from the phone number; an unknown number is rejected.
		        Wallet wallet = new EWalletUserServiceImpl(dataSource).getUserWalletByPhoneNumber(phone);
		        if(wallet==null) {
		        	throw new TransactionExecutor.TxException("err.atm.invalid_code");
		        }
		        String transactionReference = transactionExecutor.atmDeposit(atmId, wallet.getWalletId(), code, amount, atm.getAtmName());
		        // Success: return the received amount and the transaction reference as JSON.
		        response.setContentType("application/json;charset=UTF-8");
		        response.getWriter().write("{\"ok\":true,\"amount\":"+amount +",\"ref\":\"" + transactionReference +"\"}");
		    } catch (TransactionExecutor.TxException e) {
		        // Business failure (e.g. expired or wrong code): return its error key.
		        e.printStackTrace();
		        response.setContentType("application/json;charset=UTF-8");
		        try {
					response.getWriter().write("{\"ok\":false,\"error\":\"" + e.getErrorKey() + "\"}");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    } catch (Exception e) {
		        // Any unexpected failure falls back to the generic invalid-code error.
		        e.printStackTrace();
		        response.setContentType("application/json;charset=UTF-8");
		        try {
					response.getWriter().write("{\"ok\":false,\"error\":\"err.atm.invalid_code\"}");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    }		
	}

	/**
	 * Handles the "atmExecute" action with type "withdraw": executes an ATM
	 * cash withdrawal against the wallet identified by the phone number, using
	 * the one-time transaction code. Responds with a JSON receipt or an error.
	 */
	private void withdraw(HttpServletRequest request, HttpServletResponse response) {
		try {
	        long atmId      = Long.parseLong(request.getParameter("atmId"));
	        String phone    = request.getParameter("phone");       
	        String code     = request.getParameter("code");       
	        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
	        ATM atm = new ATMServiceImpl(dataSource).getATMById(atmId);
	        // Resolve the wallet from the phone number; an unknown number is rejected.
	        Wallet wallet = new EWalletUserServiceImpl(dataSource).getUserWalletByPhoneNumber(phone);
	        if(wallet==null) {
	        	throw new TransactionExecutor.TxException("err.atm.invalid_code");
	        }
	        String transactionReference = transactionExecutor.atmWithdraw(atmId, wallet.getWalletId(), code, amount, atm.getAtmName());
	        // Success: return the dispensed amount and the transaction reference as JSON.
	        response.setContentType("application/json;charset=UTF-8");
	        response.getWriter().write("{\"ok\":true,\"amount\":"+ amount +",\"ref\":\"" + transactionReference +"\"}");
	    } catch (TransactionExecutor.TxException e) {
	        // Business failure (e.g. expired or wrong code): return its error key.
	        e.printStackTrace();
	        response.setContentType("application/json;charset=UTF-8");
	        try {
				response.getWriter().write("{\"ok\":false,\"error\":\"" + e.getErrorKey() + "\"}");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    } catch (Exception e) {
	        // Any unexpected failure falls back to the generic invalid-code error.
	        e.printStackTrace();
	        response.setContentType("application/json;charset=UTF-8");
	        try {
				response.getWriter().write("{\"ok\":false,\"error\":\"err.atm.invalid_code\"}");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    }		
	}


	/**
	 * Handles the "allTtransaction" action: lists all transactions of the
	 * wallet's primary account. For every transaction the counterparty (other
	 * wallet, card or ATM) is resolved and displayed with a direction tag
	 * ("to"/"from") before forwarding to the transactions page.
	 */
	private void allTtransaction(HttpServletRequest request, HttpServletResponse response) {
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		// The wallet's primary account (account type 1) holds all wallet transactions.
		Account walletAccount = new AccountServiceImpl(dataSource).getAccountByRefereceIdAndTypeId(wallet.getWalletId(), 1);
		if(wallet != null && walletAccount != null) {
			List<Transaction> transactions = transactionService.getAllTransactions(walletAccount.getAccountId());
			List<Map.Entry<String, String>> toOrFromNames = new ArrayList<Map.Entry<String, String>>();
			for(Transaction transaction : transactions) {
				// Determine the counterparty account: the side that is not the wallet's own account.
				long accountId = 0;
				if(transaction.getToAccountId().equals(walletAccount.getAccountId())) {
					accountId = transaction.getFromAccountId();
				}else {
					accountId = transaction.getToAccountId();
				}
				String toOrfrom =accountId == transaction.getToAccountId()? "to" : "from";
				Account otherAccount = new AccountServiceImpl(dataSource).getAccountByAccountId(accountId); 
				if(otherAccount.getAccountTypeId() == 1) {
					// Account type 1: counterparty is another wallet, show its phone number.
					try {
						toOrFromNames.add(Map.entry(otherAccount.getStatus() == 0? transaction.getDescription() : new EWalletUserServiceImpl(dataSource).getUserWalletById(otherAccount.getReferenceId()).getPhoneNumber(),toOrfrom));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(otherAccount.getAccountTypeId() == 2) {
					// Account type 2: counterparty is a card, show it masked with the last 4 digits.
					toOrFromNames.add(Map.entry(otherAccount.getStatus() == 0? transaction.getDescription() : "Card •••• •••• •••• " + new CardServiceImpl(dataSource).getCardByCardId(otherAccount.getReferenceId()).getCardNumber().substring(12),toOrfrom));
				} else {
					// Otherwise the counterparty is an ATM, show its name.
					toOrFromNames.add(Map.entry(otherAccount.getStatus() == 0? transaction.getDescription() : new ATMServiceImpl(dataSource).getATMById(otherAccount.getReferenceId()).getAtmName(),toOrfrom));
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
