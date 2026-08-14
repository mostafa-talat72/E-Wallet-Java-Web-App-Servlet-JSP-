package com.ewallet.service;

import java.util.List;

import com.ewallet.model.ATM;

/**
 * Service that owns the ATM directory: listing active ATM locations and
 * looking up a single ATM for map and cash-withdrawal flows.
 */
public interface ATMService {

	/**
	 * Lists every active ATM, ordered by id.
	 * @return the active ATMs (empty list when there are none).
	 */
	List<ATM> getAllATMs();

	/**
	 * Loads a single active ATM by its id.
	 * @return the ATM row, or null if it is unknown or inactive.
	 */
	ATM getATMById(long atmId);
}
