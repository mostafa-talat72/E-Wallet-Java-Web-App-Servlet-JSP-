package com.ewallet.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.ewallet.model.ATM;
import com.ewallet.service.ATMService;

/**
 * JDBC implementation of {@link ATMService} backed by the "atms" table, which lists
 * the physical ATM locations (name, location, map coordinates) available to users
 * for cash withdrawals and map display. Only active ATMs (status = 1) are served.
 */
public class ATMServiceImpl implements ATMService {

	private DataSource dataSource;

	public ATMServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Selects every active row of "atms" (status = 1), ordered by id, and maps each to an ATM.
	 * @return the active ATMs, or an empty list when there are none.
	 */
	@Override
	public List<ATM> getAllATMs() {
		String query = "SELECT * FROM atms WHERE status = 1 ORDER BY atm_id";
		List<ATM> atms = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				atms.add(new ATM(
						resultSet.getLong("atm_id"),
						resultSet.getString("atm_name"),
						resultSet.getString("atm_location"),
						resultSet.getInt("status"),
						resultSet.getTimestamp("created_at"),
						resultSet.getDouble("mapX"),
						resultSet.getDouble("mapY")));
			}
			return atms;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Selects a single active "atms" row (status = 1) by its primary key.
	 * @return the ATM, or null when it is unknown or inactive.
	 */
	@Override
	public ATM getATMById(long atmId) {
		String query = "SELECT * FROM atms WHERE atm_id = ? AND status = 1";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setLong(1, atmId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return new ATM(
						resultSet.getLong("atm_id"),
						resultSet.getString("atm_name"),
						resultSet.getString("atm_location"),
						resultSet.getInt("status"),
						resultSet.getTimestamp("created_at"),
						resultSet.getDouble("mapX"),
						resultSet.getDouble("mapY"));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
}
