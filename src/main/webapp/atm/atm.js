(function () {
  "use strict";

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

  var lang = (function () {
    var saved;
    try { saved = localStorage.getItem("atm.lang"); } catch (e) { saved = null; }
    return saved === "en" || saved === "ar" ? saved : "en";
  })();

  var T = function (k) { return (I18N[lang] && I18N[lang][k]) || I18N.en[k] || k; };

  var $ = function (id) { return document.getElementById(id); };
  var screens = {};
  ["idle", "unavail", "lang", "ewallet", "choose", "phone", "code", "amount", "processing", "result", "thanks"].forEach(function (n) {
    screens[n] = $("scr-" + n);
  });

  var led = $("card-led");
  var cardVis = $("card-vis");
  var led2 = $("cash-led");
  var cashVis = $("cash-vis");

  var state = { name: "idle", entry: "", tx: null, ok: true, amount: 0, timer: null, phone: "", code: "" };
  var qp = (typeof URLSearchParams !== "undefined") ? new URLSearchParams(location.search) : null;
  state.presetTx = qp && (qp.get("tx") === "deposit" || qp.get("tx") === "withdraw") ? qp.get("tx") : null;
  state.atmId = qp && qp.get("atmId") ? qp.get("atmId") : "1";

  function errKey(k) {
    if (!k) return null;
    var tail = k.split(".").pop();
    return "errAtm" + tail.charAt(0).toUpperCase() + tail.slice(1);
  }

  function fmtNum(n) {
    return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  }

  /* ---------- screen switching ---------- */
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

  function setText(id, txt) { var e = $(id); if (e) e.textContent = txt; }

  function entryMax() {
    if (state.name === "phone") return 11;
    if (state.name === "code") return 9;
    if (state.name === "amount") return 7;
    return 0;
  }

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
  function setLang(l) {
    lang = l;
    try { localStorage.setItem("atm.lang", lang); } catch (e) {}
    applyLang();
  }

  function chooseTx(tx) {
    state.tx = tx;
    state.entry = "";
    show("phone");
    renderEntry();
  }

  function failGate(reason) {
    state.ok = false;
    state.failReason = reason;
    show("result");
    renderResult();
  }

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
            state.amount = data.amount;
            state.ref = data.ref;
          } else {
            state.ok = false;
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

  function pressDigit(d) {
    if (state.name !== "phone" && state.name !== "code" && state.name !== "amount") return;
    var max = entryMax();
    if (state.entry.length >= max) return;
    state.entry += d;
    renderEntry();
  }

  function pressClear() {
    if (state.name !== "phone" && state.name !== "code" && state.name !== "amount") return;
    state.entry = "";
    renderEntry();
  }

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

  function resetSession() {
    state.entry = "";
    state.phone = "";
    state.code = "";
    state.amount = 0;
    state.ok = true;
    show("choose");
  }

  function endSession() {
    state.entry = "";
    show("thanks");
  }

  /* ---------- language ---------- */
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
  applyLang();
  show("idle");
})();