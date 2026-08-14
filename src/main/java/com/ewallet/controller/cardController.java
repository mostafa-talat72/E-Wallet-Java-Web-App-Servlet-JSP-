package com.ewallet.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.taglibs.standard.tag.el.fmt.RequestEncodingTag;

import com.ewallet.model.Account;
import com.ewallet.model.Card;
import com.ewallet.model.Wallet;
import com.ewallet.service.AccountService;
import com.ewallet.service.CardService;
import com.ewallet.service.EWalletUserService;
import com.ewallet.service.impl.AccountServiceImpl;
import com.ewallet.service.impl.CardServiceImpl;
import com.ewallet.service.impl.EWalletUserServiceImpl;
import com.ewallet.util.CardValidator;
import com.ewallet.util.DateUtil;
import com.ewallet.util.LanguageUtil;

/**
 * Controller handling the management of payment cards registered by a
 * wallet user. Cards are used as funding sources when adding money to
 * the wallet balance.
 *
 * URL mapping: /cardController
 *
 * Exposed actions (via the "action" request parameter):
 *  - addCard          : registers a new card for the logged-in wallet
 *  - getAllCards      : lists the cards of the logged-in wallet
 *  - deleteCard       : removes one of the user's cards
 *  - updateCardStatus : toggles the active/frozen status of a card
 *  - (any other/missing): redirects to the error page
 *
 * Examples:
 *  http://localhost:8080/E-Wallet/cardController?action=addCard
 *  http://localhost:8080/E-Wallet/cardController?action=deleteCard
 *  http://localhost:8080/E-Wallet/cardController?action=getAllCards
 *  http://localhost:8080/E-Wallet/cardController?action=updateCardStatus
 */
@WebServlet("/cardController")
public class cardController extends HttpServlet {
	
	@Resource(name = "jdbc/ewallet/dBconnection")
	private DataSource dataSource;
	
	private CardService cardService;
	
	/**
	 * Servlet initialization hook. Constructs the CardService used by all
	 * card-related actions.
	 */
	 @Override
    public void init() throws ServletException {
		 cardService = new CardServiceImpl(dataSource);
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
		if(action == null)
			action = "notFoundPage";
		
		switch(action) {
			case "addCard":
				addCard(request, response);
				break;
			case "getAllCards":
				getAllCards(request, response);
				break;
			case "deleteCard":
				deleteCard(request, response);
				break;
			case "updateCardStatus":
				updateCardStatus(request, response);
				break;
			default:
				response.sendRedirect("error.jsp"+ LanguageUtil.langQuery(request));
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
	 * Handles the "addCard" action: registers a new payment card for the
	 * logged-in wallet. Reassembles the card number from its four input
	 * parts, validates the card details, persists the card and links a
	 * bank account (account type 2) to it. Re-renders the cards page
	 * with the updated card list or the validation errors.
	 */
	private void addCard(HttpServletRequest request, HttpServletResponse response) {
		// The card number is entered by the user as four groups of four digits.
		String cardPart1 = request.getParameter("cardPart1");
		String cardPart2 = request.getParameter("cardPart2");
		String cardPart3 = request.getParameter("cardPart3");
		String cardPart4 = request.getParameter("cardPart4");

		String cardNumber = cardPart1 + cardPart2 + cardPart3 + cardPart4;
		String cardName = request.getParameter("cardName");
		String cardHolderName = request.getParameter("cardHolderName");		
		String bankName = request.getParameter("bankName");
		String expMonth = request.getParameter("expMonth");
		String expYear = request.getParameter("expYear");
		
		String cvv = request.getParameter("cvv");
		Map<String, String> errors = CardValidator.validateForAddCard(cardNumber, cvv, expMonth, expYear);
		
		if(errors.isEmpty()) {
			// Convert the month/year fields into a single expiration date.
			Date expireDate = DateUtil.convertExpirationDate(expMonth, expYear);

			boolean isAdded = false;
			Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
			Card newCard = new Card(wallet.getWalletId(), cardNumber,cardName,bankName, cardHolderName,expireDate,cvv);
			try {
				isAdded = cardService.addCard(newCard);
				
			}catch(SQLException e)
			{
				// Map database constraints (e.g. a duplicated card number) to user-facing errors.
				errors = CardValidator.parseSqlException(e);
			}
			
			if(isAdded) {
				// Link a bank account (account type 2) that references this card.
				AccountService accountService = new AccountServiceImpl(dataSource);
				try {
					accountService.addAcount(new Account(2, cardService.getCardByWalletIdAndCardNumber(wallet.getWalletId(),cardNumber).getCardId()));
					
				}catch(Throwable e) {
					e.printStackTrace();
				}
				request.setAttribute("cards", cardService.getAllCardsByWalletId(wallet.getWalletId()));
			}
			else {
				errors.put("addCardErr", "err.generic");
			}
		}
		
		if(!errors.isEmpty()) {
			// Preserve the form values and error messages for re-rendering the page.
			request.setAttribute("errors", errors);
			request.setAttribute("cardPart1ErrVal", cardPart1);
			request.setAttribute("cardPart2ErrVal", cardPart2);
			request.setAttribute("cardPart3ErrVal", cardPart3);
			request.setAttribute("cardPart4ErrVal", cardPart4);
			request.setAttribute("cardNameErrVal", cardName);
			request.setAttribute("cardHolderNameErrVal", cardHolderName);
			request.setAttribute("bankNameErrVal", bankName);
			request.setAttribute("expMonthErrVal", expMonth);
			request.setAttribute("expYearErrVal", expYear);
			request.setAttribute("cvvErrVal", cvv);
		}
		try {
			request.getRequestDispatcher("cards.jsp" +  LanguageUtil.langQuery(request)).forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the "getAllCards" action: fetches all cards of the logged-in
	 * wallet and forwards to the page given by the "redirect" parameter.
	 */
	private void getAllCards(HttpServletRequest request, HttpServletResponse response) {
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		String redirect = request.getParameter("redirect");
		List<Card> cards = cardService.getAllCardsByWalletId(wallet.getWalletId());
		try {
			request.setAttribute("cards", cards);
			// The "redirect" parameter determines which page renders the card list.
			request.getRequestDispatcher(redirect + ".jsp" + LanguageUtil.langQuery(request)).forward(request, response);
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles the "deleteCard" action: deletes one of the user's cards.
	 * First deactivates the linked bank account, then removes the card
	 * itself and re-renders the cards page with the remaining cards.
	 */
	private void deleteCard(HttpServletRequest request, HttpServletResponse response) {
		long cardId =Long.parseLong(request.getParameter("cardId"));
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		// Deactivate the linked bank account (account type 2) before deleting the card.
		boolean updateAccountStatus = new AccountServiceImpl(dataSource).updateAccountStatusByRefereceIdAndTypeId(cardId, 2);

		if(updateAccountStatus)
		{
			boolean isDeleted = cardService.deleteCard(cardId, wallet.getWalletId());
			
			if(isDeleted) {
				request.setAttribute("cards", cardService.getAllCardsByWalletId(wallet.getWalletId()));
			}
		}
		
		try {
			request.getRequestDispatcher("cards.jsp" +  LanguageUtil.langQuery(request)).forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
		
	}


	/**
	 * Handles the "updateCardStatus" action: toggles the status of a card
	 * (e.g. activate or freeze it) and re-renders the cards page with the
	 * updated card list.
	 */
	private void updateCardStatus(HttpServletRequest request, HttpServletResponse response) {
		long cardId = Long.parseLong(request.getParameter("cardId"));
		int status = Integer.parseInt(request.getParameter("status"));
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");

		boolean isUpdated = cardService.updateCardStatus(cardId, status);
		if(isUpdated) {
			request.setAttribute("cards", cardService.getAllCardsByWalletId(wallet.getWalletId()));
		}
		
		try {
			request.getRequestDispatcher("cards.jsp" +  LanguageUtil.langQuery(request)).forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

}
