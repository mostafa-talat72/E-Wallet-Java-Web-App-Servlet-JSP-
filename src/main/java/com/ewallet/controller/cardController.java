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
 * Servlet implementation class cardController
 */

/*http://localhost:8080/E-Wallet/cardController?action=addCard
 *http://localhost:8080/E-Wallet/cardController?action=deleteCard
 *http://localhost:8080/E-Wallet/cardController?action=getAllCards
 *http://localhost:8080/E-Wallet/cardController?action=deleteAllCards
 *http://localhost:8080/E-Wallet/cardController?action=updateCardStatus
 *http://localhost:8080/E-Wallet/cardController
 *http://localhost:8080/E-Wallet/cardController?action=ascls
 * */
@WebServlet("/cardController")
public class cardController extends HttpServlet {
	
	@Resource(name = "jdbc/ewallet/dBconnection")
	private DataSource dataSource;
	
	private CardService cardService;
	
	 @Override
    public void init() throws ServletException {
		 cardService = new CardServiceImpl(dataSource);
    }

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

	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void addCard(HttpServletRequest request, HttpServletResponse response) {
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
			Date expireDate = DateUtil.convertExpirationDate(expMonth, expYear);

			boolean isAdded = false;
			Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
			Card newCard = new Card(wallet.getWalletId(), cardNumber,cardName,bankName, cardHolderName,expireDate,cvv);
			try {
				isAdded = cardService.addCard(newCard);
				
			}catch(SQLException e)
			{
				errors = CardValidator.parseSqlException(e);
			}
			
			if(isAdded) {
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

	private void getAllCards(HttpServletRequest request, HttpServletResponse response) {
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		String redirect = request.getParameter("redirect");
		List<Card> cards = cardService.getAllCardsByWalletId(wallet.getWalletId());
		try {
			request.setAttribute("cards", cards);
			request.getRequestDispatcher(redirect + ".jsp" + LanguageUtil.langQuery(request)).forward(request, response);
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteCard(HttpServletRequest request, HttpServletResponse response) {
		long cardId =Long.parseLong(request.getParameter("cardId"));
		Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
		boolean deleteAccount = new AccountServiceImpl(dataSource).deleteAccountByRefereceIdAndTypeId(cardId, 2);

		if(deleteAccount)
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
