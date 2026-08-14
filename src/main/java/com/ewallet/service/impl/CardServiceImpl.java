package com.ewallet.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.ewallet.model.Card;
import com.ewallet.service.CardService;

/**
 * JDBC implementation of {@link CardService} backed by the "cards" table, which
 * stores the bank cards linked to a wallet (number, holder, bank, expiry, CVV and
 * status). Deletion methods return false instead of throwing when nothing matched.
 */
public class CardServiceImpl implements CardService {

	DataSource dataSource;
	
	public CardServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Inserts a new card row into "cards" on behalf of a wallet.
	 * @return true when the row was inserted.
	 */
	@Override
	public boolean addCard(Card card)throws SQLException{
		String query = "INSERT INTO cards (wallet_id, card_number, card_name, card_holder_name, bank_name, expire_date, cvv)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		try(Connection connection = dataSource.getConnection(); 
				PreparedStatement preparedStatement = connection.prepareStatement(query); ){
			preparedStatement.setLong(1, card.getWalletId());
			preparedStatement.setString(2, card.getCardNumber());
			preparedStatement.setString(3, card.getCardName());
			preparedStatement.setString(4, card.getCardHolderName());
			preparedStatement.setString(5, card.getBankName());
			preparedStatement.setDate(6, card.getExpireDate());
			preparedStatement.setString(7, card.getCvv());
			
			preparedStatement.execute();
			return true;
		}catch(SQLException e) {
			throw e;
		}
	}
	
	/**
	 * Selects every row of "cards" belonging to a wallet and maps each to a Card.
	 * @return the wallet's cards, or an empty list when there are none.
	 */
	@Override
	public List<Card> getAllCardsByWalletId(long walletId) {
		String query = "Select * from cards where wallet_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)){
			preparedStatement.setLong(1, walletId);
			ResultSet resultSet = preparedStatement.executeQuery();
			List<Card> cards = new ArrayList<Card>();
			while(resultSet.next()) {
				Card card = new Card(
							resultSet.getLong("card_id"),
							resultSet.getLong("wallet_id"),
							resultSet.getString("card_number"),
							resultSet.getString("card_name"),
							resultSet.getString("card_holder_name"),
							resultSet.getString("bank_name"),
							resultSet.getDate("expire_date"),
							resultSet.getString("cvv"),
							resultSet.getInt("status"),
							resultSet.getTimestamp("created_at")
						);
				cards.add(card);
			}
			return cards;
		}catch(SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Updates the status column of one "cards" row (e.g. active / blocked).
	 * @return true when a row was updated.
	 */
	@Override
	public boolean updateCardStatus(Long cardId, int status) {
		String query = "update cards set status = ? where card_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);){
			preparedStatement.setInt(1, status);
			preparedStatement.setLong(2, cardId);
			return preparedStatement.executeUpdate() > 0;
		}catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deletes one "cards" row, scoped by both card id and owning wallet id so a caller
	 * can only remove its own cards.
	 * @return true when a row was deleted, false when nothing matched.
	 */
	@Override
	public boolean deleteCard(long cardId, long walletId) {
		String query = "Delete from cards Where card_id = ? AND wallet_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);){
			preparedStatement.setLong(1, cardId);
			preparedStatement.setLong(2, walletId);
			
			return preparedStatement.executeUpdate() > 0;	
		}catch(SQLException e) {
			return false;
		}
	}

	/**
	 * Deletes every "cards" row of a wallet (used when the wallet is closed).
	 * @return true when at least one row was deleted, false when there were none.
	 */
	@Override
	public boolean deleteAllCardsByWalletId(Long walletId){	
		String query = "Delete from cards Where wallet_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);){
			preparedStatement.setLong(1, walletId);

			return preparedStatement.executeUpdate() > 0;	
		}catch(SQLException e) {
			return false;
		}	}

	/**
	 * Selects a single "cards" row by its primary key and maps it to a Card.
	 * @return the card, or null when it does not exist.
	 */
	@Override
	public Card getCardByCardId(long cardId) {
		String query = "Select * from cards where card_id = ?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)){
			preparedStatement.setLong(1, cardId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				 return new Card(
							resultSet.getLong("card_id"),
							resultSet.getLong("wallet_id"),
							resultSet.getString("card_number"),
							resultSet.getString("card_name"),
							resultSet.getString("card_holder_name"),
							resultSet.getString("bank_name"),
							resultSet.getDate("expire_date"),
							resultSet.getString("cvv"),
							resultSet.getInt("status"),
							resultSet.getTimestamp("created_at")
						);
			}
		}catch(SQLException e)
		{
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * Selects a "cards" row by owning wallet id and card number (used to verify that a
	 * given card actually belongs to the wallet before it is used).
	 * @return the card, or null when no such card exists.
	 */
	@Override
	public Card getCardByWalletIdAndCardNumber(long walletId, String cardNumber) {
		String query = "Select * from cards where wallet_id = ? AND card_number =?";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)){
			preparedStatement.setLong(1, walletId);
			preparedStatement.setString(2, cardNumber);

			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				return new Card(
							resultSet.getLong("card_id"),
							resultSet.getLong("wallet_id"),
							resultSet.getString("card_number"),
							resultSet.getString("card_name"),
							resultSet.getString("card_holder_name"),
							resultSet.getString("bank_name"),
							resultSet.getDate("expire_date"),
							resultSet.getString("cvv"),
							resultSet.getInt("status"),
							resultSet.getTimestamp("created_at")
						);
			}
		}catch(SQLException e)
		{
			throw new RuntimeException(e);
		}
		return null;
	}
	

}
