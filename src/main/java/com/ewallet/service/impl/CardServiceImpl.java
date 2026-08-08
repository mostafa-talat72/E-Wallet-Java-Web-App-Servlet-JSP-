package com.ewallet.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
	public boolean updateCardStatus(Card card)throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteCard(Card card)throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteAllCardsByWalletId(Long walletId)throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Card getCardByCardId(long cardId)throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Card> getAllCardsByWalletId(long walletId)throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
