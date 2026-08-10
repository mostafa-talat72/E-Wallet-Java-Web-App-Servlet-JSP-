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

public class CardServiceImpl implements CardService {

	DataSource dataSource;
	
	public CardServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
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
