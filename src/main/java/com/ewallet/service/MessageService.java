package com.ewallet.service;

/**
 * Sends out-of-band messages (e.g. wallet activation codes) to a wallet user's
 * phone. Implementations may deliver through WhatsApp, SMS or any other
 * channel; the controller only depends on this abstraction.
 */
public interface MessageService {

	/**
	 * Sends a plain-text message to a phone number.
	 * @param phoneNumber the recipient's local phone number (e.g. 01012345678)
	 * @param text the message body
	 * @return true when the message was accepted for delivery.
	 */
	boolean send(String phoneNumber, String text);
}