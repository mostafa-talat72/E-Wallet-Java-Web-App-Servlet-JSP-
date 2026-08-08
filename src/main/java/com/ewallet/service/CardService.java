package com.ewallet.service;

import java.sql.SQLException;
import java.util.List;

import com.ewallet.model.Card;

public interface CardService {
	
	boolean addCard(Card card)throws SQLException;
	
	boolean updateCardStatus(Card card)throws SQLException;
	
	boolean deleteCard(Card card)throws SQLException;
	
	boolean deleteAllCardsByWalletId(Long walletId)throws SQLException;
	
	Card getCardByCardId(long cardId)throws SQLException;
	
	List<Card> getAllCardsByWalletId(long walletId)throws SQLException;

}
