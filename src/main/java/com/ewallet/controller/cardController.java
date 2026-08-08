package com.ewallet.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.ewallet.model.Card;
import com.ewallet.model.Wallet;
import com.ewallet.service.CardService;
import com.ewallet.service.EWalletUserService;
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
			case "deleteCard":
				deleteCard(request, response);
				break;
			case "getAllCards":
				deleteAllCards(request, response);
				break;
			case "deleteAllCards":
				deleteAllCards(request, response);
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
		String cardNumber = request.getParameter("cardPart1") + request.getParameter("cardPart2")
        + request.getParameter("cardPart3") + request.getParameter("cardPart4");		
		String cardName = request.getParameter("cardName");
		String cardHolderName = request.getParameter("cardHolderName");		
		String bankName = request.getParameter("bankName");
		String expMonth = request.getParameter("expMonth");
		String expYear = request.getParameter("expYear");
		
		Date expireDate = DateUtil.convertExpirationDate(expMonth, expYear);
		String cvv = request.getParameter("cvv");
		Map<String, String> errors = CardValidator.validateForAddCard(cardNumber, cvv, expMonth, expYear);
		
		if(errors.isEmpty()) {
			boolean isAdded = false;
			Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
			Card newCard = new Card(wallet.getWalletId(), cardNumber,cardName,bankName, cardHolderName,expireDate,cvv);
			try {
				isAdded = cardService.addCard(newCard);
			}catch(SQLException e)
			{
				errors = CardValidator.parseSqlException(e);
			}
			
			if(!isAdded) {
				errors.put("addCardErr", "err.generic");
			}
		}
		
		if(!errors.isEmpty()) {
			request.setAttribute("errors", errors);
			request.setAttribute("cardNumberErrVal", cardNumber);
			request.setAttribute("cardNameErrVal", cardName);
			request.setAttribute("cardHolderNameErrVal", cardHolderName);
			request.setAttribute("bankNameErrVal", bankName);
			request.setAttribute("expMonthErrVal", expMonth);
			request.setAttribute("expYearErrVal", expYear);
			request.setAttribute("cvvErrVal", cvv);

		    try {
				request.getRequestDispatcher("cards.jsp" +  LanguageUtil.langQuery(request) + "#addCardModal").forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void deleteCard(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	private void deleteAllCards(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	private void updateCardStatus(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

}
