<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="../WEB-INF/partials/lang.jsp" %>
<!DOCTYPE html>
<html lang="${lang}" dir="${dir}">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="E-Wallet ATM Simulator">
  <title><fmt:message key="atm.modal.title"/></title>
  <link rel="icon" href="../assets/img/favicon.svg" type="image/svg+xml">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Cairo:wght@400;600;700;800;900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="atm/atm.css">
</head>
<body>

  <button class="lang-toggle" id="lang-btn" type="button">العربية</button>

  <div class="atm-machine">
    <div class="atm-vents"><span></span><span></span><span></span><span></span><span></span></div>

    <div class="atm-brand">
      <span class="dot"></span>
      <span id="brand-label">E-Wallet ATM</span>
      <c:if test="${not empty atm}">
        <span class="atm-title-big">${fn:escapeXml(atm.atmName)} &middot; ${fn:escapeXml(atm.atmLocation)}</span>
      </c:if>
    </div>

    <!-- ================= SCREEN ================= -->
    <div class="atm-screen">
      <div class="screen-inner" id="atm-screen">

        <!-- welcome / services -->
        <div class="screen active" id="scr-idle">
          <div class="sc-title"><span class="sc-symbol">&#9883;</span> <span id="welcome-title">Welcome</span></div>
          <div class="sc-center" style="justify-content:flex-start">
            <div class="choices">
              <button class="choice" type="button" id="action-card">
                <span class="ci">&#128179;</span>
                <span><strong id="card-label">Card services</strong><small id="card-desc">Use your ATM card</small></span>
              </button>
              <button class="choice" type="button" id="action-eservices">
                <span class="ci">&#128241;</span>
                <span><strong id="esvc-label">Electronic services</strong><small id="esvc-desc">Wallet &amp; mobile services</small></span>
              </button>
            </div>
          </div>
        </div>

        <!-- card services unavailable -->
        <div class="screen" id="scr-unavail">
          <div class="sc-title"><span class="sc-symbol">&#9888;</span> <span id="unavail-title">Currently unavailable</span></div>
          <div class="sc-center">
            <p class="entry-hint" id="unavail-desc">Card services are not available at the moment</p>
          </div>
          <div class="sc-actions">
            <button class="sc-btn" type="button" id="action-unavail-ok">OK</button>
          </div>
        </div>

        <!-- language selection -->
        <div class="screen" id="scr-lang">
          <div class="sc-title"><span class="sc-symbol">&#127760;</span> <span id="lang-title">Select language</span></div>
          <div class="sc-center" style="justify-content:flex-start">
            <div class="choices">
              <button class="choice" type="button" id="action-lang-ar">
                <span class="ci">AR</span>
                <span><strong>العربية</strong><small id="lang-ar-desc"></small></span>
              </button>
              <button class="choice" type="button" id="action-lang-en">
                <span class="ci">EN</span>
                <span><strong>English</strong><small id="lang-en-desc"></small></span>
              </button>
            </div>
          </div>
        </div>

        <!-- choose e-wallet service -->
        <div class="screen" id="scr-ewallet">
          <div class="sc-title"><span class="sc-symbol">&#128176;</span> <span id="ewallet-title">Electronic wallet</span></div>
          <div class="sc-center" style="justify-content:flex-start">
            <div class="choices">
              <button class="choice" type="button" id="action-ewallet">
                <span class="ci">&#128179;</span>
                <span><strong id="ewallet-label">E-Wallet services</strong><small id="ewallet-desc">Deposit / withdraw with OTP code</small></span>
              </button>
            </div>
          </div>
        </div>

        <!-- choose transaction -->
        <div class="screen" id="scr-choose">
          <div class="sc-title"><span class="sc-symbol">&#9881;</span> <span id="choose-title">Choose transaction</span></div>
          <div class="sc-center" style="justify-content:flex-start">
            <div class="choices">
              <button class="choice" type="button" id="action-deposit">
                <span class="ci">&#8595;</span>
                <span><strong id="d-label">Deposit</strong><small id="d-desc">Cash you bring to the ATM</small></span>
              </button>
              <button class="choice" type="button" id="action-withdraw">
                <span class="ci">&#8593;</span>
                <span><strong id="w-label">Withdraw</strong><small id="w-desc">Cash the ATM gives you</small></span>
              </button>
            </div>
          </div>
        </div>

        <!-- phone entry -->
        <div class="screen" id="scr-phone">
          <div class="sc-title"><span class="sc-symbol">&#9742;</span> <span id="phone-title">Enter phone number</span></div>
          <div class="sc-center" style="justify-content:flex-start">
            <div class="entry-big" id="entry-phone">&nbsp;</div>
            <div class="dots"></div>
            <p class="entry-hint" id="phone-sub">The number registered on your E-Wallet</p>
          </div>
          <div class="sc-actions">
            <button class="sc-btn ghost" type="button" id="action-phone-cancel">CANCEL</button>
            <button class="sc-btn" type="button" id="action-phone-ok">OK</button>
          </div>
        </div>

        <!-- secret code entry -->
        <div class="screen" id="scr-code">
          <div class="sc-title"><span class="sc-symbol">&#9673;</span> <span id="code-title">Enter secret code</span></div>
          <div class="sc-center" style="justify-content:flex-start">
            <div class="entry-big" id="entry-code">&nbsp;</div>
            <div class="dots"></div>
            <p class="entry-hint" id="code-sub">The 9-digit OTP code shown in your wallet</p>
          </div>
          <div class="sc-actions">
            <button class="sc-btn ghost" type="button" id="action-code-cancel">CANCEL</button>
            <button class="sc-btn" type="button" id="action-code-ok">OK</button>
          </div>
        </div>

        <!-- amount entry -->
        <div class="screen" id="scr-amount">
          <div class="sc-title"><span class="sc-symbol">&#128181;</span> <span id="amount-title">Enter amount</span></div>
          <div class="sc-center" style="justify-content:flex-start">
            <div class="entry-big" id="entry-amount">&nbsp;</div>
            <div class="dots"></div>
            <p class="entry-hint" id="amount-sub">Amount in Egyptian pounds</p>
          </div>
          <div class="sc-actions">
            <button class="sc-btn ghost" type="button" id="action-amount-cancel">CANCEL</button>
            <button class="sc-btn" type="button" id="action-amount-ok">OK</button>
          </div>
        </div>

        <!-- processing -->
        <div class="screen" id="scr-processing">
          <div class="sc-title"><span class="sc-symbol">&#9201;</span> <span id="proc-title">Processing</span></div>
          <div class="sc-center">
            <div class="spinner"></div>
            <p class="proc-line" id="proc-line"></p>
          </div>
        </div>

        <!-- result (success or failed) -->
        <div class="screen" id="scr-result">
          <div class="sc-center">
            <div class="result-icon ok" id="res-icon">&#10003;</div>
            <h2 class="sc-title" style="margin:0" id="res-title">Take your cash</h2>
            <p class="sc-sub" id="res-desc" style="margin:0">This withdrawal is complete</p>
            <p class="sc-sub" id="res-extra" style="margin:0;color:var(--amber)"></p>
          </div>
          <div class="sc-actions">
            <button class="sc-btn ghost" type="button" id="action-again">New transaction</button>
            <button class="sc-btn" type="button" id="action-end">End session</button>
          </div>
        </div>

        <!-- thanks / end -->
        <div class="screen" id="scr-thanks">
          <div class="sc-center">
            <div class="entry-big">&#9786;</div>
            <h2 class="sc-title" style="margin:0" id="t-label">Thank you</h2>
            <p class="sc-sub" id="t-desc" style="margin:0">Come back any time.</p>
            <p class="thanks-note" id="t-note">Please take your card</p>
          </div>
          <div class="sc-actions">
            <button class="sc-btn" type="button" id="action-thanks">START</button>
          </div>
        </div>

      </div>
      <div class="scanlines"></div>
    </div>

    <!-- ================= CARD SLOT ================= -->
    <div class="atm-card-slot">
      <span class="led" id="card-led"></span>
      <div class="slot-track"><div class="card-vis gone" id="card-vis"></div></div>
      <span class="led" id="card-led2"></span>
    </div>

    <!-- ================= KEYPAD ================= -->
    <div class="atm-keypad-wrap">
      <div class="keypad">
        <button class="key" type="button" id="key-1">1</button>
        <button class="key" type="button" id="key-2">2</button>
        <button class="key" type="button" id="key-3">3</button>
        <button class="key" type="button" id="key-4">4</button>
        <button class="key" type="button" id="key-5">5</button>
        <button class="key" type="button" id="key-6">6</button>
        <button class="key" type="button" id="key-7">7</button>
        <button class="key" type="button" id="key-8">8</button>
        <button class="key" type="button" id="key-9">9</button>
        <button class="key fn" type="button" id="key-clear">CLEAR</button>
        <button class="key" type="button" id="key-0">0</button>
        <button class="key fn" type="button" id="key-cancel">BACK</button>
      </div>
      <div class="side-col">
        <button class="key fn danger" type="button" id="key-cancel2">CANCEL</button>
        <button class="key fn enter" type="button" id="key-enter">ENTER</button>
      </div>
    </div>

    <!-- ================= CASH SLOT ================= -->
    <div class="atm-cash-slot">
      <span class="led2" id="cash-led"></span>
      <div class="slot-track2"><div class="cash-vis none" id="cash-vis"><span class="n">EGP</span></div></div>
      <span class="led2" id="cash-led2"></span>
    </div>

    <div class="atm-foot">
      <c:choose>
        <c:when test="${not empty atm}">
          <span>${fn:escapeXml(atm.atmName)}</span>
          <span>${fn:escapeXml(atm.atmLocation)}</span>
        </c:when>
        <c:otherwise>
          <span>E-Wallet ATM</span>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="rules">
      <div id="rules-a">Electronic services only need your phone number and the OTP code.</div>
      <div id="rules-b" style="margin-top:4px">Use the 9-digit code shown in your E-Wallet app.</div>
    </div>
  </div>

  <script src="atm/atm.js"></script>
</body>
</html>