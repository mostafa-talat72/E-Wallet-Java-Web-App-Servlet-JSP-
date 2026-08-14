package com.ewallet.service;

import java.util.List;

import com.ewallet.model.ATM;

public interface ATMService {

	List<ATM> getAllATMs();

	ATM getATMById(long atmId);
}
