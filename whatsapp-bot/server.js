/*
 * E-Wallet WhatsApp sidecar service.
 *
 * Links your personal WhatsApp account once (scan the QR with WhatsApp >
 * Linked Devices), then exposes a tiny local HTTP API that the Java servlet
 * app uses to send messages (e.g. wallet activation codes).
 *
 * Endpoints:
 *   POST /send   body: { "to": "2010XXXXXXXX", "text": "..." }  -> { ok: true }
 *   GET  /status -> { connected: true/false, qrShown: bool, phone: ... }
 *
 * Note: this uses an unofficial WhatsApp client (Baileys). Use it only with
 * your own account; WhatsApp may restrict automation, so keep it low-volume.
 */

const { default: makeWASocket, useMultiFileAuthState, DisconnectReason, fetchLatestBaileysVersion } = require('@whiskeysockets/baileys');
const qrcode = require('qrcode-terminal');
const pino = require('pino');
const express = require('express');

const PORT = process.env.PORT || 3001;
const SESSION_DIR = process.env.SESSION_DIR || './auth';
const app = express();

let sock = null;
let connected = false;
let lastQr = null;

async function connect() {
  const { state, saveCreds } = await useMultiFileAuthState(SESSION_DIR);
  const { version } = await fetchLatestBaileysVersion();

  sock = makeWASocket({
    version,
    auth: state,
    printQRInTerminal: false,
    logger: pino({ level: 'silent' }),
    browser: ['E-Wallet Bot', 'Chrome', '1.0.0'],
  });

  sock.ev.on('creds.update', saveCreds);

  sock.ev.on('connection.update', (update) => {
    const { connection, lastDisconnect, qr } = update;

    if (qr && !connected) {
      lastQr = qr;
      console.log('\n=== Scan this QR with WhatsApp > Linked Devices ===');
      qrcode.generate(qr, { small: true });
    }

    if (connection === 'open') {
      connected = true;
      console.log('WhatsApp connected - ready to send codes');
    }

    if (connection === 'close') {
      connected = false;
      const statusCode = lastDisconnect?.error?.output?.statusCode;
      if (statusCode === DisconnectReason.loggedOut) {
        console.log('Logged out - delete the ./auth folder and re-link');
      } else {
        console.log('Connection lost, reconnecting in 5s...');
        setTimeout(connect, 5000);
      }
    }
  });
}

app.use(express.json());

app.post('/send', async (req, res) => {
  try {
    const { to, text } = req.body || {};
    if (!connected || !sock) {
      return res.status(503).json({ ok: false, error: 'NOT_CONNECTED' });
    }
    if (!to || !text) {
      return res.status(400).json({ ok: false, error: 'MISSING_PARAMS' });
    }
    const phone = String(to).replace(/[^0-9]/g, '');
    if (phone.length < 10) {
      return res.status(400).json({ ok: false, error: 'INVALID_NUMBER' });
    }
    await sock.sendMessage(phone + '@s.whatsapp.net', { text: String(text) });
    res.json({ ok: true });
  } catch (err) {
    res.status(500).json({ ok: false, error: String(err.message || err) });
  }
});

app.get('/status', (req, res) => {
  res.json({ connected, qrShown: !!lastQr, phone: sock?.user?.id || null });
});

connect();
app.listen(PORT, () => {
  console.log('E-Wallet WhatsApp service listening on http://localhost:' + PORT);
});