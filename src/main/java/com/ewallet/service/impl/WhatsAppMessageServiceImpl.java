package com.ewallet.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.ewallet.service.MessageService;

/**
 * {@link MessageService} backed by the local WhatsApp sidecar service
 * (see {@code whatsapp-bot/} in the project root). The sidecar links the
 * developer's personal WhatsApp account via QR code and exposes a tiny HTTP
 * API ({@code POST /send}) that this client calls with the recipient number
 * in local format and the message text.
 *
 * <p>The endpoint URL can be overridden through the system property
 * {@code ewallet.whatsapp.url} (defaults to {@code http://localhost:3001/send}).
 */
public class WhatsAppMessageServiceImpl implements MessageService {

	private static final String DEFAULT_URL = "http://localhost:3001/send";

	private final String endpoint;

	public WhatsAppMessageServiceImpl() {
		this.endpoint = System.getProperty("ewallet.whatsapp.url", DEFAULT_URL);
	}

	/**
	 * Posts a JSON payload to the sidecar's /send endpoint and treats a
	 * 200 OK response carrying {@code "ok":true} as success. Network failures
	 * or a sidecar that is not connected simply return false.
	 */
	@Override
	public boolean send(String phoneNumber, String text) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(endpoint);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(8000);
			connection.setDoOutput(true);

			String payload = "{\"to\":\"" + toInternational(phoneNumber) + "\",\"text\":\"" + escape(text) + "\"}";
			try (OutputStream output = connection.getOutputStream()) {
				output.write(payload.getBytes(StandardCharsets.UTF_8));
			}

			int status = connection.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				return false;
			}
			return readBody(connection).contains("\"ok\":true");
		} catch (IOException e) {
			// Sidecar down or unreachable: report failure so the caller can fall back.
			return false;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * Converts a local Egyptian number (e.g. 01012345678) into international
	 * format (201012345678), which is what WhatsApp requires. Any number that
	 * does not start with a leading zero is passed through unchanged.
	 */
	private String toInternational(String phoneNumber) {
		String digits = phoneNumber.replaceAll("[^0-9]", "");
		if (digits.startsWith("0")) {
			return "20" + digits.substring(1);
		}
		return digits;
	}

	/**
	 * Reads the full response body as UTF-8 text.
	 */
	private String readBody(HttpURLConnection connection) throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			return builder.toString();
		}
	}

	/**
	 * Escapes the characters that would break the JSON payload (quotes,
	 * backslashes, newlines and the Arabic/BOM characters are handled by UTF-8).
	 */
	private String escape(String text) {
		return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
	}
}