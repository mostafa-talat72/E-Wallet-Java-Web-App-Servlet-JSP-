// ---------------------------------------------------------------------------
// ATM machine screen flow ("ATM mode" of the E-Wallet web app).
//
// The ATM is a single-page state machine: each state maps to one screen
// element (scr-<id>) and show() is the single place where screens are switched.
//
//   idle       - attract screen (press ENTER to begin)
//   unavail    - card services unavailable (placeholder branch)
//   lang       - language selection, persisted in localStorage
//   ewallet    - wallet services entry point
//   choose     - transaction type picker (deposit / withdraw)
//   phone      - collect the 11-digit wallet phone number
//   code       - collect the 9-digit OTP code
//   amount     - collect the amount (must be a multiple of 100)
//   processing - animated wait while the backend is called
//   result     - success / failure outcome with the ATM reference
//   thanks     - session ended, take your card
//
// The on-screen numeric keypad drives pressDigit / pressClear / pressEnter /
// pressCancel and mirrors the physical keyboard (digits, Enter, Backspace,
// Escape). Validation happens inside pressEnter per screen (phone format,
// 9-digit code, amount rules). At the end of the flow runProcessing() submits
// the transaction with fetch() to transactionController?action=atmExecute and
// maps server error codes (e.g. err.atm.codeNotFound) onto the errAtm* keys of
// the I18N dictionary via errKey().
//
// All visible text is rendered through the T() translation lookup, which
// switches between the EN and AR dictionaries and toggles the dir attribute.
// ---------------------------------------------------------------------------
(function () {
  "use strict";

  // English and Arabic dictionaries for every label shown on the ATM.
  var I18N = {
    en: {
      langLabel: "العربية",
      brand: "E-Wallet ATM",
      welcomeTitle: "Welcome",
      cardLabel: "Card services",
      cardDesc: "Use your ATM card",
      esvcLabel: "Electronic services",
      esvcDesc: "Wallet & mobile services",
      unavailTitle: "Currently unavailable",
      unavailDesc: "Card services are not available at the moment",
      langTitle: "Select language",
      langArDesc: "Continue in Arabic",
      langEnDesc: "Continue in English",
      ewalletTitle: "Electronic wallet",
      ewalletLabel: "E-Wallet services",
      ewalletDesc: "Deposit / withdraw with OTP code",
      chooseTitle: "Choose transaction",
      deposit: "Deposit",
      depositD: "Cash you bring to the ATM",
      withdraw: "Withdraw",
      withdrawD: "Cash the ATM gives you",
      phoneTitle: "Enter phone number",
      phoneSub: "The number registered on your E-Wallet",
      codeTitle: "Enter secret code",
      codeSub: "The 9-digit OTP code shown in your wallet",
      amountTitle: "Enter amount",
      amountSub: "Amount in multiples of 100 EGP",
      cur: "EGP",
      procTitle: "Processing",
      proc1: "Contacting E-Wallet bank...",
      proc2: "Verifying code...",
      proc3: "Completing transaction...",
      okDep: "Deposit accepted",
      okDepD: "Your balance will be updated shortly",
      okWd: "Take your cash",
      okWdD: "This withdrawal is complete",
      failTitle: "Transaction failed",
      failD: "Check your data and try again.",
      thanksTitle: "Thank you",
      thanksD: "Come back any time.",
      thanksNote: "Please take your card",
      again: "New transaction",
      end: "End session",
      ok: "OK",
      cancel: "CANCEL",
      back: "BACK",
      enter: "ENTER",
      clear: "CLEAR",
      start: "START",
      r1: "Electronic services only need your phone number and the OTP code.",
      r2: "Use the 9-digit code from your wallet",
      r2b: "OTP",
      errPhone: "Phone must be 11 digits starting with 01",
      errCode: "Code must be 9 digits",
      errAmount: "Enter a valid amount",
      errAmount100: "Amount must be a multiple of 100",
      errAtmInvalid: "Invalid request",
      errAtmNotFound: "ATM not found",
      errAtmPhoneNotFound: "Phone number is not registered",
      errAtmCodeNotFound: "Transaction code not found",
      errAtmPhoneMismatch: "This code does not belong to this phone number",
      errAtmCodeUsed: "This code has already been used",
      errAtmCodeExpired: "This code has expired",
      errAtmCodeLocked: "This code is locked after too many attempts",
      errAtmTypeMismatch: "This operation does not match the code type",
      errAtmInsufficient: "Insufficient balance",
      errAtmFailed: "Operation failed",
      errAtmNetwork: "Unable to reach the bank",
      detailsDep: "Cash received by the ATM",
      detailsWd: "Cash dispensed to you"
    },
    ar: {
      langLabel: "English",
      brand: "صراف E-Wallet",
      welcomeTitle: "مرحباً بك",
      cardLabel: "خدمات باستخدام البطاقة",
      cardDesc: "استخدم بطاقة الصراف الخاصة بك",
      esvcLabel: "خدمات إلكترونية",
      esvcDesc: "خدمات المحفظة والهاتف المحمول",
      unavailTitle: "غير متاحة حالياً",
      unavailDesc: "خدمات البطاقة غير متاحة في الوقت الحالي",
      langTitle: "اختر اللغة",
      langArDesc: "المتابعة باللغة العربية",
      langEnDesc: "المتابعة باللغة الإنجليزية",
      ewalletTitle: "محفظة إلكترونية",
      ewalletLabel: "خدمات المحفظة الإلكترونية",
      ewalletDesc: "إيداع / سحب برمز التحقق",
      chooseTitle: "اختر العملية",
      deposit: "إيداع",
      depositD: "نقود تُضاف داخل الماكينة",
      withdraw: "سحب",
      withdrawD: "نقود تخرج لك من الماكينة",
      phoneTitle: "أدخل رقم الهاتف",
      phoneSub: "الرقم المسجل في محفظة E-Wallet الخاصة بك",
      codeTitle: "أدخل الكود السري",
      codeSub: "كود التحقق المكوّن من 9 أرقام الظاهر في محفظتك",
      amountTitle: "أدخل المبلغ",
      amountSub: "المبلغ بالجنيه المصري (100 أو مضاعفاتها)",
      cur: "ج.م",
      procTitle: "جارٍ المعالجة",
      proc1: "جارٍ الاتصال ببنك E-Wallet...",
      proc2: "جارٍ التحقق من الكود...",
      proc3: "جارٍ إتمام العملية...",
      okDep: "تم قبول الإيداع",
      okDepD: "سيتم تحديث رصيدك خلال لحظات",
      okWd: "استلم أموالك",
      okWdD: "اكتملت عملية السحب",
      failTitle: "فشلت العملية",
      failD: "تحقق من بياناتك وحاول مرة أخرى.",
      thanksTitle: "شكراً لك",
      thanksD: "يسعدنا عودتك دائماً.",
      thanksNote: "تذكر استلام بطاقتك",
      again: "عملية جديدة",
      end: "إنهاء الجلسة",
      ok: "موافق",
      cancel: "إلغاء",
      back: "رجوع",
      enter: "إدخال",
      clear: "مسح",
      start: "بدء",
      r1: "الخدمات الإلكترونية تتطلب رقم هاتفك ورمز التحقق فقط.",
      r2: "استخدم الكود المكوّن من 9 أرقام من محفظتك",
      r2b: "OTP",
      errPhone: "رقم الهاتف يجب أن يكون 11 رقماً يبدأ بـ 01",
      errCode: "الكود يجب أن يكون 9 أرقام",
      errAmount: "أدخل مبلغاً صحيحاً",
      errAmount100: "المبلغ يجب أن يكون 100 أو مضاعفاتها",
      errAtmInvalid: "طلب غير صالح",
      errAtmNotFound: "الماكينة غير موجودة",
      errAtmPhoneNotFound: "رقم الهاتف غير مسجل",
      errAtmCodeNotFound: "كود العملية غير موجود",
      errAtmPhoneMismatch: "هذا الكود لا يخص رقم الهاتف",
      errAtmCodeUsed: "هذا الكود مستخدم من قبل",
      errAtmCodeExpired: "انتهت صلاحية هذا الكود",
      errAtmCodeLocked: "هذا الكود مقفول بعد محاولات كثيرة",
      errAtmTypeMismatch: "هذه العملية لا تطابق نوع الكود",
      errAtmInsufficient: "رصيد غير كافٍ",
      errAtmFailed: "فشلت العملية",
      errAtmNetwork: "تعذر الوصول إلى البنك",
      detailsDep: "المبلغ المستلم منك داخل الماكينة",
      detailsWd: "المبلغ المُسحوب لصالحك"
    }
  };

  // Active language, restored from localStorage on load (default: "en").
  var lang = (function () {
    var saved;
    try { saved = localStorage.getItem("atm.lang"); } catch (e) { saved = null; }
    return saved === "en" || saved === "ar" ? saved : "en";
  })();

  // Translation lookup with EN fallback; unknown keys render as themselves.
  var T = function (k) { return (I18N[lang] && I18N[lang][k]) || I18N.en[k] || k; };

  var $ = function (id) { return document.getElementById(id); };
  // Collect every screen element (scr-idle, scr-choose, ...) into one map so
  // show() can toggle them all in a single pass.
  var screens = {};
  ["idle", "unavail", "lang", "ewallet", "choose", "phone", "code", "amount", "processing", "result", "thanks"].forEach(function (n) {
    screens[n] = $("scr-" + n);
  });

  var led = $("card-led");
  var cardVis = $("card-vis");
  var led2 = $("cash-led");
  var cashVis = $("cash-vis");

  // Shared mutable state for the whole flow: current screen, the entry typed
  // on the keypad, the chosen transaction type, last outcome and reference,
  // the processing-line interval timer, and the collected phone / code.
  var state = { name: "idle", entry: "", tx: null, ok: true, amount: 0, timer: null, phone: "", code: "" };
  // URL parameters allow opening the ATM in a preset state, e.g. with the
  // transaction type pre-selected (?tx=withdraw) or bound to a specific machine.
  var qp = (typeof URLSearchParams !== "undefined") ? new URLSearchParams(location.search) : null;
  state.presetTx = qp && (qp.get("tx") === "deposit" || qp.get("tx") === "withdraw") ? qp.get("tx") : null;
  state.atmId = qp && qp.get("atmId") ? qp.get("atmId") : "1";

  // Converts a server error code into the matching dictionary key so the ATM
  // can show a localized message. E.g. "err.atm.codeNotFound" becomes
  // "errAtmCodeNotFound", which exists in both the EN and AR dictionaries.
  function errKey(k) {
    if (!k) return null;
    var tail = k.split(".").pop();
    return "errAtm" + tail.charAt(0).toUpperCase() + tail.slice(1);
  }

  // Formats a number with thousands separators, e.g. 12000 -> "12,000".
  function fmtNum(n) {
    return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  }

  /* ---------- screen switching ---------- */
  // Central screen switch: cancels any running timer, toggles the target
  // screen's "active" class, updates the card / cash LEDs and the cash-slot
  // state, hides the card on the idle screen, and scrolls back to the top.
  function show(name) {
    if (state.timer) { clearInterval(state.timer); state.timer = null; }
    state.name = name;
    Object.keys(screens).forEach(function (n) { screens[n].classList.toggle("active", n === name); });
    led.classList.toggle("on", name === "choose" || name === "phone" || name === "code" || name === "amount");
    led2.classList.toggle("on", name === "processing" || (name === "result" && state.ok && state.tx === "withdraw"));
    if (name === "result" && state.ok && state.tx === "withdraw") {
      cashVis.classList.add("out");
      cashVis.classList.remove("none");
    } else {
      cashVis.classList.remove("out");
      cashVis.classList.add("none");
    }
    if (name === "idle") cardVis.classList.add("gone");
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  // Sets the text content of an element when it exists.
  function setText(id, txt) { var e = $(id); if (e) e.textContent = txt; }

  // Maximum number of digits accepted by the current entry screen
  // (phone: 11, code: 9, amount: 7).
  function entryMax() {
    if (state.name === "phone") return 11;
    if (state.name === "code") return 9;
    if (state.name === "amount") return 7;
    return 0;
  }

  // Redraws the keypad entry line: the amount is formatted with the currency
  // label, and for phone / code a row of dots marks the digits typed so far.
  function renderEntry() {
    var max = entryMax();
    if (max === 0) return;
    var el = state.name === "phone" ? $("entry-phone") : (state.name === "code" ? $("entry-code") : $("entry-amount"));
    if (!el) return;
    if (state.name === "amount") {
      el.textContent = state.entry ? T("cur") + " " + fmtNum(parseInt(state.entry, 10)) : "\u00A0";
    } else {
      el.textContent = state.entry ? state.entry : "\u00A0";
    }
    var dots = el.parentElement.querySelector(".dots");
    if (dots) {
      dots.innerHTML = "";
      if (state.name !== "amount") {
        for (var i = 0; i < max; i++) {
          var d = document.createElement("span");
          if (i < state.entry.length) d.classList.add("on");
          dots.appendChild(d);
        }
      }
    }
  }

  /* ---------- result screen ---------- */
  // Fills the result screen: success shows a checkmark with the amount and ATM
  // reference, failure shows a cross with the localized reason.
  function renderResult() {
    var icon = $("res-icon");
    icon.className = "result-icon " + (state.ok ? "ok" : "bad");
    icon.innerHTML = state.ok ? "&#10003;" : "&#10005;";
    if (state.ok) {
      setText("res-title", state.tx === "deposit" ? T("okDep") : T("okWd"));
      setText("res-desc", state.tx === "deposit" ? T("okDepD") : T("okWdD"));
      setText("res-extra", T(state.tx === "deposit" ? "detailsDep" : "detailsWd") + " \u00B7 " + fmtNum(state.amount) + " " + T("cur") + " \u00B7 " + state.ref);
      $("res-extra").style.display = "";
    } else {
      setText("res-title", T("failTitle"));
      setText("res-desc", state.failReason || T("failD"));
      $("res-extra").style.display = "none";
    }
  }

  /* ---------- flow ---------- */
  // Switches the active language, persists it and re-renders every label.
  function setLang(l) {
    lang = l;
    try { localStorage.setItem("atm.lang", lang); } catch (e) {}
    applyLang();
  }

  // Starts a transaction of the given type and moves to the phone screen.
  function chooseTx(tx) {
    state.tx = tx;
    state.entry = "";
    show("phone");
    renderEntry();
  }

  // Aborts the flow with a failure: stores the reason and shows the result
  // screen in its error state.
  function failGate(reason) {
    state.ok = false;
    state.failReason = reason;
    show("result");
    renderResult();
  }

  // Plays the rotating "Processing..." lines for ~2.6 seconds, then executes
  // the transaction and lands on the result screen.
  function runProcessing() {
    show("processing");
    var lines = [T("proc1"), T("proc2"), T("proc3")];
    var i = 0;
    setText("proc-line", lines[0]);
    state.timer = setInterval(function () {
      i++;
      if (i < lines.length) setText("proc-line", lines[i]);
    }, 700);
    setTimeout(function () {
      if (state.timer) { clearInterval(state.timer); state.timer = null; }
      // Execute the transaction against the backend; the short delay before the
      // call lets the processing animation read as a real machine sequence.
      var url = "/E-Wallet/transactionController?action=atmExecute"
        + "&atmId=" + encodeURIComponent(state.atmId)
        + "&phone=" + encodeURIComponent(state.phone)
        + "&code=" + encodeURIComponent(state.code)
        + "&type=" + encodeURIComponent(state.tx)
        + "&amount=" + encodeURIComponent(state.amount);
      fetch(url)
        .then(function (r) { return r.json(); })
        .then(function (data) {
          if (data && data.ok) {
            state.ok = true;
            // The backend may adjust the amount; show its final value and the
            // ATM reference on the result screen.
            state.amount = data.amount;
            state.ref = data.ref;
          } else {
            state.ok = false;
            // Map the server error code (e.g. err.atm.codeNotFound) to its
            // dictionary key so the user sees a localized readable message.
            state.failReason = T(errKey(data && data.error)) || (data && data.error) || T("errAtmFailed");
          }
          show("result");
          renderResult();
        })
        .catch(function () {
          state.ok = false;
          state.failReason = T("errAtmNetwork");
          show("result");
          renderResult();
        });
    }, 2600);
  }

  // ENTER key / OK button: advances the flow, validating the current screen's
  // input first. The idle screen boots the machine, each entry screen captures
  // its field and moves on, and the code screen launches the transaction.
  function pressEnter() {
    if (state.name === "idle") { show("lang"); return; }
    if (state.name === "unavail") { show("idle"); return; }
    if (state.name === "ewallet") { show("choose"); return; }
    if (state.name === "phone") {
      if (!/^01\d{9}$/.test(state.entry)) { failGate(T("errPhone")); return; }
      state.phone = state.entry;
      state.entry = "";
      show("amount");
      renderEntry();
      return;
    }
    if (state.name === "amount") {
      var amt = parseInt(state.entry, 10);
      if (!(amt > 0)) { failGate(T("errAmount")); return; }
      if (amt % 100 !== 0) { failGate(T("errAmount100")); return; }
      state.amount = amt;
      state.entry = "";
      show("code");
      renderEntry();
      return;
    }
    if (state.name === "code") {
      if (!/^\d{9}$/.test(state.entry)) { failGate(T("errCode")); return; }
      state.code = state.entry;
      state.entry = "";
      runProcessing();
    }
  }

  // Appends a digit to the current entry, up to the screen's maximum length.
  function pressDigit(d) {
    if (state.name !== "phone" && state.name !== "code" && state.name !== "amount") return;
    var max = entryMax();
    if (state.entry.length >= max) return;
    state.entry += d;
    renderEntry();
  }

  // Clears the current entry (BACKSPACE / CLEAR key).
  function pressClear() {
    if (state.name !== "phone" && state.name !== "code" && state.name !== "amount") return;
    state.entry = "";
    renderEntry();
  }

  // CANCEL / BACK key: walks one screen backwards through the flow (or resets
  // to the transaction chooser / idle screen, depending on where the user is).
  function pressCancel() {
    if (state.name === "processing") return;
    if (state.name === "idle" || state.name === "thanks" || state.name === "unavail" || state.name === "lang") { show("idle"); return; }
    if (state.name === "ewallet") { show("lang"); return; }
    if (state.name === "choose") { show("ewallet"); return; }
    if (state.name === "phone") { state.entry = ""; show("choose"); return; }
    if (state.name === "amount") { state.entry = ""; show("phone"); return; }
    if (state.name === "code") { state.entry = ""; show("amount"); return; }
    if (state.name === "result") { resetSession(); return; }
  }

  // Clears all collected data and returns to the transaction chooser so a new
  // transaction can start immediately.
  function resetSession() {
    state.entry = "";
    state.phone = "";
    state.code = "";
    state.amount = 0;
    state.ok = true;
    show("choose");
  }

  // Ends the session and shows the thank-you screen ("take your card").
  function endSession() {
    state.entry = "";
    show("thanks");
  }

  /* ---------- language ---------- */
  // Applies the current language everywhere: sets the document direction and
  // language attributes, fills every translatable element, and re-renders the
  // dynamic entry / result areas if they are on screen.
  function applyLang() {
    document.documentElement.setAttribute("dir", lang === "ar" ? "rtl" : "ltr");
    document.documentElement.setAttribute("lang", lang);
    setText("lang-btn", T("langLabel"));
    setText("brand-label", T("brand"));
    setText("welcome-title", T("welcomeTitle"));
    setText("card-label", T("cardLabel"));
    setText("card-desc", T("cardDesc"));
    setText("esvc-label", T("esvcLabel"));
    setText("esvc-desc", T("esvcDesc"));
    setText("unavail-title", T("unavailTitle"));
    setText("unavail-desc", T("unavailDesc"));
    setText("lang-title", T("langTitle"));
    setText("lang-ar-desc", T("langArDesc"));
    setText("lang-en-desc", T("langEnDesc"));
    setText("ewallet-title", T("ewalletTitle"));
    setText("ewallet-label", T("ewalletLabel"));
    setText("ewallet-desc", T("ewalletDesc"));
    setText("choose-title", T("chooseTitle"));
    setText("d-label", T("deposit"));
    setText("d-desc", T("depositD"));
    setText("w-label", T("withdraw"));
    setText("w-desc", T("withdrawD"));
    setText("phone-title", T("phoneTitle"));
    setText("phone-sub", T("phoneSub"));
    setText("code-title", T("codeTitle"));
    setText("code-sub", T("codeSub"));
    setText("amount-title", T("amountTitle"));
    setText("amount-sub", T("amountSub"));
    setText("proc-title", T("procTitle"));
    setText("a-label", T("again"));
    setText("e-label", T("end"));
    setText("t-label", T("thanksTitle"));
    setText("t-desc", T("thanksD"));
    setText("t-note", T("thanksNote"));
    setText("rules-a", T("r1"));
    setText("rules-b", T("r2") + "  " + T("r2b"));
    setText("key-enter", T("enter"));
    setText("key-clear", T("clear"));
    setText("key-cancel", T("back"));
    setText("key-cancel2", T("cancel"));
    setText("action-phone-ok", T("ok"));
    setText("action-phone-cancel", T("cancel"));
    setText("action-code-ok", T("ok"));
    setText("action-code-cancel", T("cancel"));
    setText("action-amount-ok", T("ok"));
    setText("action-amount-cancel", T("cancel"));
    setText("action-unavail-ok", T("ok"));
    setText("action-again", T("again"));
    setText("action-end", T("end"));
    setText("action-thanks", T("start"));
    if (state.entry !== "") renderEntry();
    if (state.name === "result") renderResult();
  }

  /* ---------- wiring ---------- */
  // Screen buttons and the on-screen keypad are wired to the flow functions
  // above; the keyboard listener mirrors them for a physical keypad.
  $("lang-btn").addEventListener("click", function () {
    setLang(lang === "ar" ? "en" : "ar");
  });

  var i;
  for (i = 0; i <= 9; i++) {
    $("key-" + i).addEventListener("click", (function (d) { return function () { pressDigit(d); }; })(String(i)));
  }
  $("key-clear").addEventListener("click", pressClear);
  $("key-cancel").addEventListener("click", pressCancel);
  $("key-cancel2").addEventListener("click", pressCancel);
  $("key-enter").addEventListener("click", pressEnter);

  $("action-card").addEventListener("click", function () { show("unavail"); });
  $("action-eservices").addEventListener("click", function () { show("lang"); });
  $("action-unavail-ok").addEventListener("click", function () { show("idle"); });
  $("action-lang-ar").addEventListener("click", function () { setLang("ar"); show("ewallet"); });
  $("action-lang-en").addEventListener("click", function () { setLang("en"); show("ewallet"); });
  $("action-ewallet").addEventListener("click", function () { show("choose"); });
  $("action-deposit").addEventListener("click", function () { chooseTx("deposit"); });
  $("action-withdraw").addEventListener("click", function () { chooseTx("withdraw"); });
  $("action-phone-ok").addEventListener("click", pressEnter);
  $("action-phone-cancel").addEventListener("click", pressCancel);
  $("action-code-ok").addEventListener("click", pressEnter);
  $("action-code-cancel").addEventListener("click", pressCancel);
  $("action-amount-ok").addEventListener("click", pressEnter);
  $("action-amount-cancel").addEventListener("click", pressCancel);
  $("action-again").addEventListener("click", resetSession);
  $("action-end").addEventListener("click", endSession);
  $("action-thanks").addEventListener("click", function () { show("idle"); });

  document.addEventListener("keydown", function (e) {
    if (/^\d$/.test(e.key)) { pressDigit(e.key); return; }
    if (e.key === "Enter") { pressEnter(); return; }
    if (e.key === "Backspace") { pressClear(); return; }
    if (e.key === "Escape") { pressCancel(); }
  });

  /* ---------- init ---------- */
  // Boot the machine: render in the saved language and start on the idle screen.
  applyLang();
  show("idle");
})();