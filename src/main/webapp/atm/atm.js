(function () {
  "use strict";

  var I18N = {
    en: {
      langLabel: "العربية",
      brand: "E-Wallet ATM",
      idleTitle: "Welcome",
      idleSub: "Insert your card to start",
      insertMsg: "Inserting card...",
      chooseTitle: "Choose transaction",
      deposit: "Deposit",
      depositD: "Cash you bring to the ATM",
      withdraw: "Withdraw",
      withdrawD: "Cash the ATM gives you",
      phoneTitle: "Enter phone number",
      phoneSub: "The number registered on your E-Wallet",
      codeTitle: "Enter transaction code",
      codeSub: "The 6-digit code shown in your wallet",
      procTitle: "Processing",
      proc1: "Contacting E-Wallet bank...",
      proc2: "Verifying code...",
      proc3: "Completing transaction...",
      okDep: "Deposit accepted",
      okDepD: "Your balance will be updated shortly",
      okWd: "Take your cash",
      okWdD: "This withdrawal is complete",
      failTitle: "Transaction failed",
      failD: "Check your phone number and transaction code, then try again.",
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
      r1: "This demo simulator only needs your phone number and the transaction code.",
      r2: "Demo code:",
      r2b: "112233",
      errPhone: "Phone must be 11 digits starting with 01",
      errCode: "Code must be 6 digits",
      detailsDep: "Cash received by the ATM",
      detailsWd: "Cash dispensed to you",
      stamp: "Demo Simulator"
    },
    ar: {
      langLabel: "English",
      brand: "صراف E-Wallet",
      idleTitle: "مرحباً بك",
      idleSub: "أدخل بطاقتك لبدء العملية",
      insertMsg: "جارٍ إدخال البطاقة...",
      chooseTitle: "اختر العملية",
      deposit: "إيداع",
      depositD: "نقود تُضاف داخل الماكينة",
      withdraw: "سحب",
      withdrawD: "نقود تخرج لك من الماكينة",
      phoneTitle: "أدخل رقم الهاتف",
      phoneSub: "الرقم المسجل في محفظة E-Wallet الخاصة بك",
      codeTitle: "أدخل كود العملية",
      codeSub: "الكود المكوّن من 6 أرقام الظاهر في محفظتك",
      procTitle: "جارٍ المعالجة",
      proc1: "جارٍ الاتصال ببنك E-Wallet...",
      proc2: "جارٍ التحقق من الكود...",
      proc3: "جارٍ إتمام العملية...",
      okDep: "تم قبول الإيداع",
      okDepD: "سيتم تحديث رصيدك خلال لحظات",
      okWd: "استلم أموالك",
      okWdD: "اكتملت عملية السحب",
      failTitle: "فشلت العملية",
      failD: "تحقق من رقم الهاتف وكود العملية ثم حاول مرة أخرى.",
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
      r1: "هذا المحاكي التجريبي يتطلب رقم هاتفك وكود العملية فقط.",
      r2: "الكود التجريبي:",
      r2b: "112233",
      errPhone: "رقم الهاتف يجب أن يكون 11 رقماً يبدأ بـ 01",
      errCode: "الكود يجب أن يكون 6 أرقام",
      detailsDep: "المبلغ المستلم منك داخل الماكينة",
      detailsWd: "المبلغ المُسحوب لصالحك",
      stamp: "محاكي تجريبي"
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
  ["idle", "carding", "choose", "phone", "code", "processing", "result", "thanks"].forEach(function (n) {
    screens[n] = $("scr-" + n);
  });

  var led = $("card-led");
  var cardVis = $("card-vis");
  var led2 = $("cash-led");
  var cashVis = $("cash-vis");

  var state = { name: "idle", entry: "", tx: null, ok: true, amount: 0, timer: null };
  var qp = (typeof URLSearchParams !== "undefined") ? new URLSearchParams(location.search) : null;
  state.presetTx = qp && (qp.get("tx") === "deposit" || qp.get("tx") === "withdraw") ? qp.get("tx") : null;

  /* ---------- screen switching ---------- */
  function show(name) {
    if (state.timer) { clearInterval(state.timer); state.timer = null; }
    state.name = name;
    Object.keys(screens).forEach(function (n) { screens[n].classList.toggle("active", n === name); });
    led.classList.toggle("on", name === "carding" || name === "choose" || name === "phone" || name === "code");
    led2.classList.toggle("on", name === "processing" || (name === "result" && state.ok && state.tx === "withdraw"));
    if (name === "result" && state.ok && state.tx === "withdraw") {
      cashVis.classList.add("out");
      cashVis.classList.remove("none");
    } else {
      cashVis.classList.remove("out");
      cashVis.classList.add("none");
    }
    if (name === "idle") cardVis.classList.add("gone");
    if (name === "carding") { cardVis.classList.remove("gone"); cardVis.classList.add("in"); }
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function setText(id, txt) { var e = $(id); if (e) e.textContent = txt; }

  function renderEntry() {
    var max = state.name === "phone" ? 11 : 6;
    var el = state.name === "phone" ? $("entry-phone") : $("entry-code");
    if (el) el.textContent = state.entry ? state.entry : "\u00A0";
    var dots = el ? el.parentElement.querySelector(".dots") : null;
    if (dots) {
      dots.innerHTML = "";
      for (var i = 0; i < max; i++) {
        var d = document.createElement("span");
        if (i < state.entry.length) d.classList.add("on");
        dots.appendChild(d);
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
      setText("res-extra", T(state.tx === "deposit" ? "detailsDep" : "detailsWd") + " · " + state.amount + " EGP");
      $("res-extra").style.display = "";
    } else {
      setText("res-title", T("failTitle"));
      setText("res-desc", state.failReason || T("failD"));
      $("res-extra").style.display = "none";
    }
  }

  /* ---------- flow ---------- */
  function insertCard() {
    show("carding");
    setText("carding-msg", T("insertMsg"));
    setTimeout(function () {
      if (state.presetTx) {
        state.tx = state.presetTx;
        state.entry = "";
        show("phone");
        renderEntry();
      } else {
        show("choose");
        setText("choose-title", T("chooseTitle"));
      }
    }, 1400);
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
      var pool = [100, 200, 500, 1000, 1500, 2000, 3000, 5000];
      state.amount = pool[Math.floor(Math.random() * pool.length)];
      state.ok = true;
      show("result");
      renderResult();
    }, 2600);
  }

  function pressEnter() {
    if (state.name === "idle") { insertCard(); return; }
    if (state.name === "phone") {
      if (!/^01\d{9}$/.test(state.entry)) { failGate(T("errPhone")); return; }
      state.entry = "";
      show("code");
      renderEntry();
      return;
    }
    if (state.name === "code") {
      if (!/^\d{6}$/.test(state.entry)) { failGate(T("errCode")); return; }
      state.entry = "";
      runProcessing();
    }
  }

  function pressDigit(d) {
    if (state.name !== "phone" && state.name !== "code") return;
    var max = state.name === "phone" ? 11 : 6;
    if (state.entry.length >= max) return;
    state.entry += d;
    renderEntry();
  }

  function pressClear() {
    if (state.name !== "phone" && state.name !== "code") return;
    state.entry = "";
    renderEntry();
  }

  function pressCancel() {
    if (state.name === "carding" || state.name === "processing") return;
    if (state.name === "idle" || state.name === "thanks") { show("idle"); return; }
    if (state.name === "result") { resetSession(); return; }
    if (state.name === "code") { state.entry = ""; show("phone"); renderEntry(); return; }
    if (state.name === "phone") { state.entry = ""; show("choose"); return; }
    show("choose");
  }

  function resetSession() {
    state.entry = "";
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
    setText("idle-title", T("idleTitle"));
    setText("idle-sub", T("idleSub"));
    setText("choose-title", T("chooseTitle"));
    setText("d-label", T("deposit"));
    setText("d-desc", T("depositD"));
    setText("w-label", T("withdraw"));
    setText("w-desc", T("withdrawD"));
    setText("phone-title", T("phoneTitle"));
    setText("phone-sub", T("phoneSub"));
    setText("code-title", T("codeTitle"));
    setText("code-sub", T("codeSub"));
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
    setText("action-idle-enter", T("enter"));
    setText("action-phone-ok", T("ok"));
    setText("action-phone-cancel", T("cancel"));
    setText("action-code-ok", T("ok"));
    setText("action-code-cancel", T("cancel"));
    setText("action-again", T("again"));
    setText("action-end", T("end"));
    setText("action-thanks", T("start"));
    setText("stamp", T("stamp"));
    if (state.entry !== "") renderEntry();
    if (state.name === "result") renderResult();
  }

  /* ---------- wiring ---------- */
  $("lang-btn").addEventListener("click", function () {
    lang = lang === "ar" ? "en" : "ar";
    try { localStorage.setItem("atm.lang", lang); } catch (e) {}
    applyLang();
  });

  var i;
  for (i = 0; i <= 9; i++) {
    $("key-" + i).addEventListener("click", (function (d) { return function () { pressDigit(d); }; })(String(i)));
  }
  $("key-clear").addEventListener("click", pressClear);
  $("key-cancel").addEventListener("click", pressCancel);
  $("key-cancel2").addEventListener("click", pressCancel);
  $("key-enter").addEventListener("click", pressEnter);

  $("action-idle-enter").addEventListener("click", pressEnter);
  $("action-phone-ok").addEventListener("click", pressEnter);
  $("action-phone-cancel").addEventListener("click", pressCancel);
  $("action-code-ok").addEventListener("click", pressEnter);
  $("action-code-cancel").addEventListener("click", pressCancel);
  $("action-deposit").addEventListener("click", function () { chooseTx("deposit"); });
  $("action-withdraw").addEventListener("click", function () { chooseTx("withdraw"); });
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