package com.ewallet.service.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import com.ewallet.model.Account;
import com.ewallet.model.TransactionCode;
import com.ewallet.service.TransactionCodeService;
import com.ewallet.util.TransactionUtil;

/**
 * Executes money movements atomically: a single connection with
 * setAutoCommit(false) so that the transaction record, the balance updates
 * and the OTP code invalidation either all succeed or all roll back.
 */
public class TransactionExecutor {

	public static class TxException extends Exception {
		private static final long serialVersionUID = 1L;
		private final String errorKey;

		public TxException(String errorKey) {
			super(errorKey);
			this.errorKey = errorKey;
		}

		public String getErrorKey() {
			return errorKey;
		}
	}

	private static final BigDecimal WITHDRAW_FEE_RATE = new BigDecimal(100);
	private static final BigDecimal TRANSFER_FEE_RATE = new BigDecimal(1000);

	private final DataSource dataSource;
	private final TransactionCodeService codeService;

	public TransactionExecutor(DataSource dataSource) {
		this.dataSource = dataSource;
		this.codeService = new TransactionCodeServiceImpl(dataSource);
	}

	public String addMoney(long walletId, long cardId,String cardNumber, BigDecimal amount) throws TxException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			Account from = findAccount(conn, cardId, 2);
			Account to = findAccount(conn, walletId, 1);
			if (from == null || to == null) {
				throw new TxException("err.atm.failed");
			}
			BigDecimal balance = findBalance(conn, walletId);
			if (balance == null) {
				throw new TxException("err.atm.failed");
			}

			String ref = "TX-" + TransactionUtil.generateTransactionCode();
			insertTransaction(conn, from.getAccountId(), to.getAccountId(), 1L, amount, BigDecimal.ZERO, ref, "Card •••• •••• •••• " + cardNumber.substring(12));
			updateBalance(conn, walletId, balance.add(amount));

			conn.commit();
			return ref;
		} catch (SQLException e) {
			rollback(conn);
			e.printStackTrace();
			throw new TxException("err.payment.failed");
		} finally {
			close(conn);
		}
	}

	public String transfer(long fromWalletId, long toWalletId, BigDecimal amount, String description) throws TxException {
		BigDecimal fees = amount.divide(TRANSFER_FEE_RATE);
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			Account from = findAccount(conn, fromWalletId, 1);
			Account to = findAccount(conn, toWalletId, 1);
			if (from == null || to == null) {
				throw new TxException("err.recipient.notFound");
			}
			BigDecimal senderBalance = findBalance(conn, fromWalletId);
			BigDecimal receiverBalance = findBalance(conn, toWalletId);
			if (senderBalance == null || receiverBalance == null) {
				throw new TxException("err.amount.invalid");
			}
			if (senderBalance.compareTo(amount.add(fees)) < 0) {
				throw new TxException("err.amount.insufficient");
			}
			
		
			String ref = "TX-" + TransactionUtil.generateTransactionCode();
			insertTransaction(conn, from.getAccountId(), to.getAccountId(), 3L, amount, fees, ref, description);
			updateBalance(conn, fromWalletId, senderBalance.subtract(amount.add(fees)));
			updateBalance(conn, toWalletId, receiverBalance.add(amount));

			conn.commit();
			return ref;
		} catch (SQLException e) {
			rollback(conn);
			e.printStackTrace();
			throw new TxException("err.payment.failed");
		} finally {
			close(conn);
		}
	}

	public String atmDeposit(long atmId, long walletId, String enteredCode, BigDecimal amount, String atmName) throws TxException {
		TransactionCode txCode = codeService.getValidTransactionCodeByWalletIdAndCode(walletId);
		System.out.println(enteredCode +" "+ txCode.getCode());
		if (txCode == null) {
			throw new TxException("err.atm.codeNotFound");
		}
		if (!enteredCode.equals(txCode.getCode()) || !amount.equals(txCode.getAmount())) {
			throw new TxException("err.atm.codeNotFound");
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			Account from = findAccount(conn, atmId, 3);
			Account to = findAccount(conn, walletId, 1);
			if (from == null || to == null) {
				throw new TxException("err.atm.failed");
			}
			BigDecimal balance = findBalance(conn, walletId);
			if (balance == null) {
				throw new TxException("err.atm.failed");
			}

			String ref = "TX-" + TransactionUtil.generateTransactionCode();
			insertTransaction(conn, from.getAccountId(), to.getAccountId(), 1L, amount, BigDecimal.ZERO, ref, atmName);
			updateBalance(conn, walletId, balance.add(amount));
			if (!markCodeUsed(conn, walletId, enteredCode)) {
				throw new TxException("err.atm.codeUsed");
			}

			conn.commit();
			return ref;
		} catch (SQLException e) {
			rollback(conn);
			e.printStackTrace();
			throw new TxException("err.atm.failed");
		} finally {
			close(conn);
		}
	}

	public String atmWithdraw(long atmId, long walletId, String enteredCode, BigDecimal amount, String atmName) throws TxException {
		TransactionCode txCode = codeService.getValidTransactionCodeByWalletIdAndCode(walletId);
		if (txCode == null) {
			throw new TxException("err.atm.codeNotFound");
		}
		if (!enteredCode.equals(txCode.getCode()) || !amount.equals(txCode.getAmount())) {
			throw new TxException("err.atm.codeNotFound");
		}
		BigDecimal fees = amount.divide(WITHDRAW_FEE_RATE);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			Account from = findAccount(conn, walletId, 1);
			Account to = findAccount(conn, atmId, 3);
			if (from == null || to == null) {
				throw new TxException("err.atm.failed");
			}
			BigDecimal balance = findBalance(conn, walletId);
			if (balance == null) {
				throw new TxException("err.atm.failed");
			}
			if (balance.compareTo(amount.add(fees)) < 0) {
				throw new TxException("err.atm.insufficient");
			}

			String ref = "TX-" + TransactionUtil.generateTransactionCode();
			insertTransaction(conn, from.getAccountId(), to.getAccountId(), 2L, amount, fees, ref, atmName);
			updateBalance(conn, walletId, balance.subtract(amount.add(fees)));
			if (!markCodeUsed(conn, walletId, enteredCode)) {
						throw new TxException("err.atm.codeUsed");
			}

			conn.commit();
			return ref;
		} catch (SQLException e) {
			rollback(conn);
			e.printStackTrace();
			throw new TxException("err.atm.failed");
		} finally {
			close(conn);
		}
	}

	/* ---------------------- helpers ---------------------- */

	private Account findAccount(Connection conn, long referenceId, int accountTypeId) throws SQLException {
		String query = "SELECT account_id, account_type_id, reference_id, status FROM accounts WHERE reference_id = ? AND account_type_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setLong(1, referenceId);
			ps.setInt(2, accountTypeId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new Account(
							rs.getLong("account_id"),
							rs.getInt("account_type_id"),
							rs.getLong("reference_id"),
							rs.getInt("status"));
				}
			}
		}
		return null;
	}

	private BigDecimal findBalance(Connection conn, long walletId) throws SQLException {
		String query = "SELECT available_balance, held_balance FROM wallet_balances WHERE wallet_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setLong(1, walletId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getBigDecimal("available_balance");
				}
			}
		}
		return null;
	}

	private void updateBalance(Connection conn, long walletId, BigDecimal newAvailable) throws SQLException {
		String query = "UPDATE wallet_balances SET available_balance = ?, updated_at = ? WHERE wallet_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setBigDecimal(1, newAvailable);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setLong(3, walletId);
			ps.executeUpdate();
		}
	}

	private void insertTransaction(Connection conn, long fromAccountId, long toAccountId, long typeId,
			BigDecimal amount, BigDecimal fees, String ref, String description) throws SQLException {
		String query = "INSERT INTO transactions (from_account_id, to_account_id, transaction_type_id, transaction_status_id, amount, fees, reference_number, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setLong(1, fromAccountId);
			ps.setLong(2, toAccountId);
			ps.setLong(3, typeId);
			ps.setLong(4, 2L);
			ps.setBigDecimal(5, amount);
			ps.setBigDecimal(6, fees);
			ps.setString(7, ref);
			ps.setString(8, description);
			ps.executeUpdate();
		}
	}

	private boolean markCodeUsed(Connection conn, long walletId, String code) throws SQLException {
		String query = "UPDATE transaction_codes SET attempts = attempts + 1, is_used = 1, is_Expire = 1 WHERE wallet_id = ? AND code = ? AND (is_used = 0 OR is_Expire = 0)";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setLong(1, walletId);
			ps.setString(2, code);
			return ps.executeUpdate() > 0;
		}
	}

	private void rollback(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}