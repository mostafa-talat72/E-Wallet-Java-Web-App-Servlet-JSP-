# E-Wallet WhatsApp Sidecar Service

Free WhatsApp message sender for the E-Wallet app. It links your **personal**
WhatsApp account once (by scanning a QR code) and exposes a local HTTP endpoint
that the Java servlet app calls to send wallet **activation codes**.

No business account, no paid APIs — the messages are sent from your own number.

## Setup

Requires Node.js 18+.

```bash
cd whatsapp-bot
npm install
npm start
```

On first start a QR code is printed in the terminal.
Open WhatsApp on your phone → **Settings → Linked Devices → Link a Device**
and scan the QR. The session is saved in `whatsapp-bot/auth/`, so next time the
service starts it reconnects without asking for a QR again.

## API

| Method | Path      | Body / Params         | Response                  |
|--------|-----------|-----------------------|---------------------------|
| POST   | `/send`   | `{ "to": "2010XXXXXXXX", "text": "..." }` | `{ "ok": true }` |
| GET    | `/status` | —                     | `{ "connected", "phone" }` |

`to` must be the number in **international format** (country code without `+`),
e.g. an Egyptian number `01012345678` becomes `201012345678`.

Quick test:

```bash
curl -X POST http://localhost:3001/send -H "Content-Type: application/json" -d "{\"to\":\"201012345678\",\"text\":\"Test\"}"
```

## Config

- `PORT` — listening port, default `3001` (must match the URL in
  `WhatsAppMessageServiceImpl`).
- `SESSION_DIR` — session folder, default `./auth`. Delete it to re-link.

## Troubleshooting

- `NOT_CONNECTED` → scan the QR first, or wait for reconnection.
- Recipient must be a real WhatsApp number.
- If the session breaks, stop the service, delete the `auth/` folder, and
  re-link with a fresh QR.

## Legal note

This uses an unofficial WhatsApp client (Baileys). Use it only with your own
account and keep message volume low — WhatsApp may restrict accounts that send
large amounts of automated traffic.
