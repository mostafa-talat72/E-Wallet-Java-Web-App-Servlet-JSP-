# E-Wallet — Java Web Application (Servlets & JSP)

A full **mobile-wallet simulator** built with classic Java EE technologies: **Servlets, JSP, JSTL**, backed by an **Oracle** database, deployed on **Apache Tomcat 9**. The system lets a registered user manage a phone-number based wallet: add money from a saved card, send money to another wallet, generate one-time ATM codes (OTP), and use those codes on a simulated **ATM machine screen** to deposit or withdraw cash — all with bilingual UI (English / Arabic).

---

## Table of Contents

1. [Features](#features)
2. [Tech Stack](#tech-stack)
3. [Architecture](#architecture)
4. [Project Structure](#project-structure)
5. [Database Schema](#database-schema)
6. [Financial Rules](#financial-rules)
7. [Security](#security)
8. [Endpoints (Controllers)](#endpoints-controllers)
9. [Page Map](#page-map)
10. [i18n & RTL](#i18n--rtl)
11. [Setup & Run](#setup--run)
12. [The ATM Flow (step by step)](#the-atm-flow-step-by-step)
13. [Known Limitations & Future Work](#known-limitations--future-work)

---

## Features

| Area | What it does |
|---|---|
| **Wallet accounts** | Signup with phone number + national ID + PIN, login, profile update, change PIN, delete account (atomic cascade). |
| **Wallet activation** | New wallets start **inactive** (status 0): a 6-digit code is sent to the owner's WhatsApp (free, via a local Baileys sidecar using a personal WhatsApp account), and the wallet unlocks only after the code is entered (`activate.jsp`). Login is blocked for inactive wallets. |
| **Cards** | Add / delete bank cards (16-digit number, CVV, expiry), list saved cards, add money to the wallet from a card. |
| **Transfer** | Send money to any registered wallet by phone, with a 0.1% fee and an optional note. |
| **ATM OTP codes** | Generate a one-time 6-figure code for a given amount; the code has a 10-minute countdown, is single-use and is consumed atomically. |
| **ATM machine** | A public self-service kiosk simulation (`atm/atm-machine.jsp`) where a user enters phone + OTP + amount to **deposit** or **withdraw** cash from a specific ATM. |
| **ATM map** | Find ATMs by location (`atm/atm-map.jsp`). |
| **Transactions history** | Full list with type filters (all / deposit / withdraw / transfer), text search, and **client-side pagination** (8 rows/page, page windows with ellipsis). |
| **i18n + RTL** | English / Arabic with full RTL layout, persisted via `?lang=en|ar` in the session; every message lives in `messages*.properties` bundles. |
| **Session security** | `AuthFilter` protects every page except the public ones (login, register, activate, error, ATM pages). |

---

## Tech Stack

- **Java 8+** (compiled and verified with JDK 26)
- **Jakarta/Javax Servlet API 4.0**, **JSP 2.3**, **JSTL 1.2**
- **Apache Tomcat 9.0.x**
- **Oracle Database 11g+** (`ojdbc8.jar`) — database access through a **JNDI connection pool** (`jdbc/ewallet/dBconnection`, a Tomcat `DataSource`)
- **Bootstrap 5** (RTL + LTR layouts), **Bootstrap Icons**, **Chart.js** (dashboard chart)
- **javax.mail 1.6.2** (vendor jar included; reserved for e-mail features)
- **Node.js 18+ sidecar** (`whatsapp-bot/`): sends the activation codes through your
  personal WhatsApp account via [Baileys](https://github.com/whiskeysockets/baileys)
  (unofficial client — low volume only)

---

## Architecture

```
┌──────────────────────────── Browser  ────────────────────────────┐
│  JSP pages (views)  +  main.js / atm.js (client logic)            │
└────────────────────────────────┬──────────────────────────────────┘
                                 │ HTTP (GET/POST) — public pages go through AuthFilter
┌────────────────────────────────▼──────────────────────────────────┐
│  Controllers (Servlets)        com.ewallet.controller             │
│  walletController · transactionController · transactionCode        │
│  Controller · cardController · atmController                       │
└───────┬──────────────────────┬──────────────────────┬─────────────┘
        │                      │                      │
┌───────▼──────────┐  ┌────────▼──────────┐   ┌───────▼─────────────┐
│ Service layer    │  │ TransactionExecutor│  │ Utils               │
│ com.ewallet.service│  │ (atomic money ops) │  │ PinUtil, Validators│
│ (interfaces +    │  │ addMoney/transfer/ │  │ TransactionUtil,   │
│  impl classes)   │  │ atmDeposit/Withdraw│  │ LanguageUtil, ...   │
└───────┬──────────┘  └────────┬──────────┘   └─────────────────────┘
        │                      │
┌───────▼──────────────────────▼──────────────┐
│ JDBC + JNDI DataSource   (Oracle via ojdbc8) │
└──────────────────────────────────────────────┘
```

The classic three-layer pattern is preserved, with two deliberate design decisions:

1. **Services own the domain** — `EWalletUserServiceImpl`, `CardServiceImpl`, `AccountServiceImpl`, … perform CRUD and business logic. The controllers only translate HTTP requests → service calls → view attributes.
2. **`TransactionExecutor` owns money movement** — every operation that moves money (add money, transfer, ATM deposit/withdraw) runs on a **single JDBC connection** with `setAutoCommit(false)` and an explicit `commit()`/`rollback()`. The transaction row, both balance updates, and the OTP-code invalidation either all succeed or all roll back (see [Security](#security)).

---

## Project Structure

```
src/main/
├── java/com/ewallet/
│   ├── controller/            # Servlets (request routing)
│   │   ├── walletController.java          # signup, login, activate, resendActivation,
│   │   │                                  # update profile, change PIN, delete, logout
│   │   ├── transactionController.java     # addMoney, atmExecute (deposit/withdraw), transfer, history
│   │   ├── transactionCodeController.java # generateCode, updateCodeStatus
│   │   ├── cardController.java            # addCard, getAllCards, deleteCard, updateCardStatus
│   │   └── atmController.java             # getAllATMs, getATMById
│   ├── filter/
│   │   └── AuthFilter.java                # session guard (@WebFilter("/*"))
│   ├── model/                 # DB-mapped entities (Wallet, Card, ActivationCode, …)
│   ├── service/               # service interfaces (+ MessageService for WhatsApp)
│   ├── service/impl/          # implementations + TransactionExecutor (atomic money ops)
│   │                          # + WhatsAppMessageServiceImpl (HTTP client for the sidecar)
│   └── util/                  # PinUtil, UserWalletValidator, TransactionValidator,
│                              # CardValidator, TransactionUtil, LanguageUtil, DateUtil
└── webapp/                    # web root
    ├── *.jsp                  # pages (login, register, activate, home, profile,
    │                          # add-money, send-money, transactions, atmotp, cards, error, index)
    ├── atm/                   # atm-machine.jsp (kiosk UI) + atm-map.jsp + atm.js + atm.css
    ├── assets/                # global css/js, Bootstrap & Chart.js vendors
    ├── WEB-INF/
    │   ├── web.xml            # welcome-file, session timeout, error pages
    │   ├── partials/          # shared JSP includes (head, navbar, footer, lang, …)
    │   ├── classes/ewallet/i18n/  # messages.properties / _en / _ar bundles
    │   └── lib/               # ojdbc8.jar, jstl-1.2.jar, javax.mail, activation
whatsapp-bot/                  # Node sidecar: personal-WhatsApp QR link + POST /send
sql/
├── schema.sql                 # Oracle DDL (all tables incl. activation_codes)
└── seed-data.sql              # lookup data, demo ATMs and their type-3 accounts
```

---

## Database Schema

> The schema ships as Oracle DDL in `sql/schema.sql`; `sql/seed-data.sql` populates the
> lookup tables, demo ATMs and their type-3 accounts. Constraint names referenced by the
> validators: `CHECK_CARD_NUMBER_LENGTH`, `CHECK_CVV_LENGTH`, `UQ_CARD_NUMBER_WALLET`,
> `UQ_WALLET_CODE`, `UQ_ACTIVATION_WALLET_CODE`, plus the FKs from `transaction_codes`
> and `activation_codes` into `wallets`.

| Table | Purpose | Key columns |
|---|---|---|
| `wallets` | Registered wallets | `wallet_id`, `phone_number` (unique), `national_id`, `full_name`, **`pin_hash`**, **`salt`**, `status` (0 = pending activation, 1 = active), `created_at`, `updated_at` |
| `wallet_balances` | Per-wallet balance | `wallet_id`, `available_balance`, `held_balance`, `updated_at` |
| `accounts` | Ledger accounts (see account types) | `account_id`, `account_type_id`, `reference_id`, `status` |
| `account_types` | 1 = Wallet, 2 = Card, 3 = ATM | `account_type_id`, `name` |
| `cards` | Saved bank cards | `card_id`, `wallet_id`, `card_number` (16 digits, unique per wallet), `card_name`, `card_holder_name`, `bank_name`, `expire_date`, `cvv`, `status` |
| `transactions` | Money-movement ledger | `from_account_id`, `to_account_id`, `transaction_type_id`, `transaction_status_id`, `amount`, `fees`, `reference_number` (unique `TX-….`), `description`, `created_at` |
| `transaction_types` | 1 = Deposit, 2 = Withdraw, 3 = Transfer | `transaction_type_id`, `name` |
| `transaction_status` | 1 = Pending, 2 = Success, 3 = Failed, 4 = Cancelled, 5 = Expired | `transaction_status_id`, `name` |
| `transaction_codes` | One-time ATM OTP codes | `code_id`, `wallet_id`, `code` (6 digits, unique per wallet), `amount`, `created_at`, `expires_at`, `attempts`, `is_used`, `is_expire` |
| `activation_codes` | One-time 6-digit activation codes | `code_id`, `wallet_id`, `code` (6 digits, unique per wallet), `created_at`, `expires_at`, `attempts`, `is_used`, `is_expire` |
| `atms` | Registered ATM machines | `atm_id`, `atm_name`, `atm_location`, `mapX`, `mapY`, `status` |

### Why an `accounts` layer?

Every row in `transactions` references **accounts**, not wallets directly. A wallet owns one
account (type 1), each card owns one account (type 2), and each ATM owns one account (type 3).
This is what allows debits/credits between a wallet, its card, and an ATM machine using the
**same ledger table**. When a wallet is deleted its account is **disabled** (`status = 0`),
not removed, so the transaction history stays intact.

---

## Financial Rules

| Operation | Fee | Business rules |
|---|---|---|
| **Add money** (card → wallet) | `0` | Card and wallet must exist. Applies PIN verification. |
| **Transfer** (wallet → wallet) | `amount / 1000` (0.1%) | Cannot send to self; recipient must exist; balance must cover amount + fee; applies PIN verification. |
| **ATM deposit** (ATM → wallet) | `0` | Requires a valid, unused, unexpired OTP code whose amount equals the deposit. |
| **ATM withdraw** (wallet → ATM) | `amount / 100` (1%) | Same OTP rules + sufficient balance for amount + fee. |

Insufficient balance returns the i18n error `err.amount.insufficient` (transfer) or
`err.atm.insufficient` (ATM), never a partial execution.

---

## Security

### 1. PIN hashing (never stored in plain text)
- On **signup** the service draws a **16-byte `SecureRandom` salt** and stores
  `SHA-256("salt" + ":" + "pin")` hex-encoded in `wallets.pin_hash` (`PinUtil`).
- On **login** the service reads the wallet by phone, recomputes the digest with the
  stored salt and compares it against `pin_hash` — the raw PIN never touches the DB.
- Every write path that validates a PIN (login, change PIN, add money, transfer,
  generate code, delete account) goes through the service’s `login()` verification.
- **Why hashing belongs to the service layer:** the controller passes the raw PIN and the
  service (not the controller) owns salt generation, hashing and storage — see `EWalletUserServiceImpl.signup / login / updateUserWalletPin`.

### 2. Session guard (`AuthFilter`, `@WebFilter("/*")`)
- Without a session containing `wallet`, every request is redirected to `login.jsp`.
- **Public paths only:** `/`, `index.jsp`, `login.jsp`, `register.jsp`,
  `activate.jsp`, `error.jsp`, `/assets/*`, the whole `atm/` directory +
  `atmController`, the `atmExecute` action of `transactionController` (the ATM
  machine is a public kiosk), and the `login`/`signup`/`activate`/`resendActivation`
  actions of `walletController` (the activation flow needs no session wallet).

### 3. Wallet activation (phone-ownership proof)
- New wallets are created **inactive** (`status = 0` — changed DDL default and
  explicit in `signup`), so a new registration can never log in until activated.
- Signup issues a 6-digit code into `activation_codes` (10-minute expiry, max
  3 attempts, unique per wallet) and sends it on WhatsApp through the sidecar.
- `action=activate` verifies the code (format → still-valid row → attempts →
  match), consumes it and flips `wallets.status` to 1. "Valid" is decided by the
  **database clock** (`expires_at > CURRENT_TIMESTAMP`), never by the JVM clock,
  because the DB and the JVM can run in different timezones (Cairo is UTC+2 in the
  DB's tz data but UTC+3 in a modern JVM — a JVM-side expiry check would reject
  every fresh code). Wrong codes increment `attempts`; after 3 the code is locked
  forever.
- `action=resendActivation` consumes the old valid code and issues + sends a
  fresh one (fresh attempts counter).
- Login of an inactive wallet redirects to `activate.jsp` instead of opening a
  session.

### 4. Atomic money movements (`TransactionExecutor`)
- One `Connection`, `setAutoCommit(false)`, then `commit()` or `rollback()`.
- The OTP invalidation is **inside the same transaction** as the ledger insert and the
  balance update. `markCodeUsed` runs
  `UPDATE … WHERE … AND (is_used = 0 OR is_Expire = 0)`, so a second concurrent use of the
  same code updates 0 rows → exception → the whole transaction rolls back.
- `deleteUserWallet` on `EWalletUserServiceImpl` performs an **atomic cascade** on a single
  connection: `transaction_codes` → `wallet_balances` → `cards` → disable wallet account →
  delete the wallet row (credential-guarded). Previously the wallet row failed to delete
  because the OTP codes table referenced it (FK violation) and the cascade ran on four
  separate connections.

### 5. Error keys are i18n-safe
`TxException` propagates bundle keys (`err.atm.codeNotFound`, `err.amount.insufficient`, …)
which the JSP pages and the ATM machine JS map back to translated messages. No raw English
hard-coded in controllers.

---

## Endpoints (Controllers)

All controllers are mapped with `@WebServlet` and use `doGet` → `doPost`.

### `walletController`
| action | Method | Description |
|---|---|---|
| `signup` | POST | Validates + creates wallet (server-side hashing, status 0), creates balance & account, issues + sends the activation code |
| `login` | POST | Verifies PIN (salted hash), stores `wallet` + `walletBalance` in session (inactive wallets are redirected to activation) |
| `activate` | POST | Verifies the 6-digit WhatsApp code, unlocks the wallet (status 1) and opens the session |
| `resendActivation` | GET | Consumes the old code and issues + sends a fresh one |
| `updateUserWallet` | POST | Update full name |
| `updateUserWalletPin` | POST | Verify current PIN, generate new salt+hash via service |
| `deleteUserWallet` | POST | Cascade-delete wallet (atomic) and invalidate session |
| `logout` | GET/POST | Invalidate session → `login.jsp` |

### `transactionController`
| action | Method | Description |
|---|---|---|
| `addMoney` | POST | Card → wallet deposit (PIN verified, atomic) |
| `atmExecute&type=deposit` | POST | ATM deposit via OTP code (atomic) |
| `atmExecute&type=withdraw` | POST | ATM withdraw via OTP code (atomic, 1% fee) |
| `transfer` | POST | Wallet → wallet transfer (PIN verified, 0.1% fee, atomic) |
| `allTtransaction` | GET | Build transaction history (`transactions.jsp`) |

### `transactionCodeController`
| action | Method | Description |
|---|---|---|
| `generateCode` | POST | PIN-verified OTP generation; retries on duplicate code (unique constraint `UQ_WALLET_CODE`) |
| `updateCodeStatus` | GET | Marks the code used/expired (called by the countdown timer) |

### `cardController`
| action | Method | Description |
|---|---|---|
| `addCard` | POST | Validates (16 digits, CVV 3, future expiry) and saves a card + its type-2 account |
| `getAllCards` | GET | Lists the wallet’s cards (`cards.jsp`) |
| `deleteCard` | POST | Deletes a card + deactivates its account |
| `updateCardStatus` | GET/POST | Enable/disable (activates/deactivates the type-2 account) |

### `atmController`
| action | Method | Description |
|---|---|---|
| `getAllATMs` | GET | Lists ATMs for the map |
| `getATMById` | GET | Single ATM detail |

---

## Page Map

| Page | Audience | Purpose / endpoint it talks to |
|---|---|---|
| `index.jsp` | public | Redirects to `login.jsp` |
| `login.jsp` | public | Login form → `walletController?action=login` |
| `register.jsp` | public | Signup form → `walletController?action=signup` |
| `activate.jsp` | public | Enter the 6-digit WhatsApp code → `walletController?action=activate`; resend via `action=resendActivation` |
| `home.jsp` | logged in | Dashboard: balance, quick actions, mini chart (demo data) |
| `profile.jsp` | logged in | Update name, change PIN, delete account → `walletController` |
| `add-money.jsp` | logged in | Deposit from a saved card → `transactionController?action=addMoney` |
| `send-money.jsp` | logged in | Transfer form with live fee preview → `transactionController?action=transfer` |
| `transactions.jsp` | logged in | Ledger table + filters + search + client-side pagination (8/page) |
| `atmotp.jsp` | logged in | Generate OTP code (amount + PIN) → `transactionCodeController?action=generateCode`; 10:00 countdown → `updateCodeStatus` |
| `cards.jsp` | logged in | Card grid, add/delete modals → `cardController` |
| `error.jsp` | public | Global 404 / Throwable error page (web.xml) |
| `atm/atm-machine.jsp` | public | Self-service ATM kiosk (see ATM flow) |
| `atm/atm-map.jsp` | public | ATM locations + link to the machine |

Shared UI lives in `WEB-INF/partials/` (`head`, `navbar`, `footer`, `page-head`, `lang`, `demo-data`).

---

## i18n & RTL

- Every page includes `WEB-INF/partials/lang.jsp` **first**; it resolves `?lang=en|ar`,
  persists the choice in the session (default **Arabic**), and exposes `lang`, `dir`
  (`ltr`/`rtl`), `appURL` and `qLang` to all pages.
- `fmt:setBundle` loads `ewallet.i18n.messages` from `WEB-INF/classes/ewallet/i18n/`.
  Three bundles exist: `messages.properties` (fallback = English), `messages_en.properties`,
  `messages_ar.properties`.
- Controllers carry the language across redirects with `LanguageUtil.langQuery(request)`
  (returns `?lang=en` or `?lang=ar`).
- The ATM machine has its own JS dictionaries (`atm.js`: `T("…")` with EN/AR maps) and the
  server error codes (`err.atm.*`) are mapped through `errKey()` to those dictionaries.

---

## Setup & Run

### Prerequisites
- **JDK 8+** (verified with JDK 26)
- **Tomcat 9.0.x**
- **Oracle database** (the app was developed against Oracle; `ojdbc8.jar` is already in `WEB-INF/lib`)

### 1. Create the Oracle schema
Run the shipped scripts in order:

```
sqlplus ewallet/password@localhost:1521/XE @sql/schema.sql
sqlplus ewallet/password@localhost:1521/XE @sql/seed-data.sql
```

`schema.sql` creates every table (including `activation_codes`) and
`seed-data.sql` inserts the lookup rows (account types, transaction types,
transaction statuses), three demo ATMs and one type-3 account per ATM.

### 2. Configure the JNDI DataSource in Tomcat
The servlets inject `@Resource(name = "jdbc/ewallet/dBconnection")`. In
`$CATALINA_HOME/conf/context.xml` (or a context fragment):

```xml
<Context>
  <Resource name="jdbc/ewallet/dBconnection"
            auth="Container"
            type="javax.sql.DataSource"
            driverClassName="oracle.jdbc.OracleDriver"
            url="jdbc:oracle:thin:@localhost:1521:XE"
            username="ewallet"
            password="your-password"
            maxTotal="20"
            maxIdle="10"
            maxWaitMillis="10000" />
</Context>
```

### 3. Start the WhatsApp sidecar (for activation codes)
```bash
cd whatsapp-bot
npm install
npm start
```
On first start a **QR code** is printed in the terminal — scan it from your phone
(WhatsApp → Settings → Linked Devices → Link a Device). The session is stored in
`whatsapp-bot/auth/`, so it reconnects automatically afterwards. Test it:

```bash
curl -X POST http://localhost:3001/send -H "Content-Type: application/json" -d "{\"to\":\"201012345678\",\"text\":\"Test\"}"
```

The Java app calls this service automatically after every signup
(`WhatsAppMessageServiceImpl`, URL overridable with `-Dewallet.whatsapp.url=…`).
If the sidecar is down the activation page **falls back to showing the code on
screen**, so the app stays usable while developing without WhatsApp.

### 4. Deploy
Option A — copy the `src/main/webapp` content (after compiling `src/main/java` into
`WEB-INF/classes`) into `webapps/E-Wallet`.  
Option B — import the folder as a **Dynamic Web Project** in Eclipse / IntelliJ, set
**Target runtime = Tomcat 9**, and run on the server.

### 5. Open
```
http://localhost:8080/E-Wallet/
```
You’ll land on the login page. Register a wallet (Egyptian phone format suggested:
11 digits), **enter the 6-digit WhatsApp activation code**, then explore.

> **Note:** Tomcat 9 with the `javax.*` (not `jakarta.*`) packages is required — the
> code imports `javax.servlet`, `javax.annotation.Resource`, etc.

---

## The ATM Flow (step by step)

1. **Phone app — generate OTP** (`atmotp.jsp`): the user enters an amount + PIN; the
   controller verifies the PIN, checks sufficient balance (for withdraw), generates a
   6-digit code, and (re)stores it in `transaction_codes`. The page displays it with a
   **10-minute countdown** (`data-countdown="600"`). When it hits zero the page
   auto-navigates to `updateCodeStatus` which expires the code.
2. **ATM kiosk** (`atm/atm-machine.jsp` + `atm.js`): public screen — the user picks
   Deposit/Withdraw, types phone, types the 6-digit code, types the amount, confirms.
3. `atm.js` calls `transactionController?action=atmExecute&type=deposit|withdraw`
   (`fetch`, JSON). The server validates wallet existence, the code (match, not used,
   not expired, same amount) and balance, then `TransactionExecutor`:
   - finds the ATM account (type 3) and the wallet account (type 1),
   - inserts the ledger row,
   - updates the wallet balance (withdraw minus 1% fee),
   - marks the code used — all inside one transaction,
   - commits.
4. The screen shows success (amount + reference `TX-…`) or maps the server error key to a
   bilingual message. “New transaction” resets the kiosk.

---

## Known Limitations & Future Work

- **Client-side pagination** on the transactions page: the server loads the full history;
  switch to server-side paging (`OFFSET … FETCH NEXT`) when rows grow into thousands.
- The **WhatsApp sidecar uses an unofficial client** (Baileys) and your personal number.
  It is fine for development/low volume, but WhatsApp may restrict accounts used for
  automation — for production, replace `MessageService` with an official provider
  (Twilio, Meta Cloud API, …).
- `javax.mail` is bundled but **no e-mail feature is wired yet** (candidate: PIN recovery).
- **HTTPS:** the group is protected against DB leaks via hashing; protect the transport by
  enabling TLS in Tomcat (production deployments).
- The dashboard chart in `home.jsp` uses demo data (`demo-data.jsp` partial) — replace with
  real aggregation when needed.
- `deleteUserWallet` disables (not deletes) the wallet account to preserve ledger history;
  if you need to purge history too, add a cascade for `transactions` and `accounts`.

---

© E-Wallet project — built as a learning-grade production-pattern e-wallet on Servlets & JSP.