package com.ewallet.model;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Maps to the {@code activation_codes} table. Stores the one-time 6-digit
 * code issued to a wallet for phone-ownership proof, either to activate a new
 * wallet ({@code purpose = "ACTIVATION"}) or to reset a forgotten PIN
 * ({@code purpose = "RESET"}). A code is single-use, expires after
 * {@code expiresAt} and becomes permanently invalid after a maximum number
 * of {@code attempts}.
 */
public class ActivationCode {

	// Identity and ownership
	private Long codeId;
	private Long walletId;

	// The 6-digit activation payload
	private String code;

	// What the code authorizes: "ACTIVATION" (new wallet) or "RESET" (forgot PIN)
	private String purpose;

	// Validity window
	private Timestamp createdAt;
	private Timestamp expiresAt;

	// Usage state
	private Integer attempts;
	private Integer isUsed;
	private Integer isExpire;

	public ActivationCode() {
	}

	/**
	 * Creates a new activation code (INSERT) for a wallet; expiry, attempts
	 * and usage flags are set by the database. Defaults to the "ACTIVATION"
	 * purpose — the forgot-PIN flow overrides it via {@code setPurpose}.
	 */
	public ActivationCode(Long walletId, String code) {
		this.walletId = walletId;
		this.code = code;
		this.purpose = "ACTIVATION";
	}

	/**
	 * Full constructor used when a code row is read back from the database,
	 * including its purpose, validity window and usage state.
	 */
	public ActivationCode(Long codeId, Long walletId, String code, String purpose,
			Timestamp createdAt, Timestamp expiresAt,
			Integer attempts, Integer isUsed, Integer isExpire) {
		this.codeId = codeId;
		this.walletId = walletId;
		this.code = code;
		this.purpose = purpose;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
		this.attempts = attempts;
		this.isUsed = isUsed;
		this.isExpire = isExpire;
	}

	public Long getCodeId() {
		return codeId;
	}

	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}

	public Long getWalletId() {
		return walletId;
	}

	public void setWalletId(Long walletId) {
		this.walletId = walletId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Timestamp expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Integer getAttempts() {
		return attempts;
	}

	public void setAttempts(Integer attempts) {
		this.attempts = attempts;
	}

	public Integer getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Integer isUsed) {
		this.isUsed = isUsed;
	}

	public Integer getIsExpire() {
		return isExpire;
	}

	public void setIsExpire(Integer isExpire) {
		this.isExpire = isExpire;
	}

	@Override
	public String toString() {
		return "ActivationCode{" +
				"codeId=" + codeId +
				", walletId=" + walletId +
				", code='" + code + '\'' +
				", purpose='" + purpose + '\'' +
				", createdAt=" + createdAt +
				", expiresAt=" + expiresAt +
				", attempts=" + attempts +
				", isUsed=" + isUsed +
				", isExpire=" + isExpire +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ActivationCode)) return false;
		ActivationCode that = (ActivationCode) o;
		return Objects.equals(codeId, that.codeId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(codeId);
	}
}