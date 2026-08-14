package com.ewallet.service;

import java.sql.SQLException;
import java.util.List;

import com.ewallet.model.Card;

/**
 * Service that owns bank card records linked to wallets: creation, listing,
 * status changes, deletion and lookups by card id or card number.
 */
public interface CardService {
	
	/**
	 * Registers a new card for a wallet.
	 * @return true when the card row was inserted.
	 */
	boolean addCard(Card card)throws SQLException;
	
	/**
	 * Lists every card registered to a wallet.
	 * @return the cards of the wallet (empty list when there are none).
	 */
	List<Card> getAllCardsByWalletId(long walletId);

	/**
	 * Changes the status of a single card (e.g. active/blocked).
	 * @return true when a row was updated.
	 */
	boolean updateCardStatus(Long cardId, int status);
	
	/**
	 * Removes one card, scoped to the owning wallet for safety.
	 * @return true when a row was deleted.
	 */
	boolean deleteCard(long cardId, long walletId);
	
	/**
	 * Removes every card of a wallet (used when the wallet is closed).
	 * @return true when at least one row was deleted.
	 */
	boolean deleteAllCardsByWalletId(Long walletId);
	
	/**
	 * Loads a card by its primary key.
	 * @return the card row, or null if it does not exist.
	 */
	Card getCardByCardId(long cardId);
	
	/**
	 * Loads a card belonging to a wallet by its card number.
	 * @return the card row, or null if it does not exist.
	 */
	Card getCardByWalletIdAndCardNumber(long walletId, String cardNumber);
}
