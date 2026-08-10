package com.ewallet.service;

import java.sql.SQLException;
import java.util.List;

import com.ewallet.model.Card;

public interface CardService {
	
	boolean addCard(Card card)throws SQLException;
	
	List<Card> getAllCardsByWalletId(long walletId);

	boolean updateCardStatus(Long cardId, int status);
	
	boolean deleteCard(long cardId, long walletId);
	
	boolean deleteAllCardsByWalletId(Long walletId);
	
	Card getCardByCardId(long cardId);
	
	Card getCardByWalletIdAndCardNumber(long walletId, String cardNumber);
}
