<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="withdraw.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="withdraw.subtitle"/></c:set>
<c:set var="activeMenu" value="withdraw"/>
<%@ include file="WEB-INF/partials/demo-data.jsp" %>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<main class="main-content">
  <div class="content-wrap" style="max-width:860px">

    <c:set var="pageActions">
      <span class="badge badge-warning"><i class="bi bi-bank2"></i> OTP</span>
    </c:set>
    <%@ include file="WEB-INF/partials/page-head.jsp" %>

    <div class="d-flex align-items-start gap-3 p-3 rounded-3 mb-4" style="background:var(--bg-soft)">
      <span class="flex-shrink-0 d-flex align-items-center justify-content-center rounded-circle" style="width:44px;height:44px;background:var(--primary-light);color:var(--primary)"><i class="bi bi-info-circle fs-5"></i></span>
      <div>
        <h6 class="fw-bold mb-1"><fmt:message key="withdraw.code.title"/></h6>
        <p class="text-muted small mb-0"><fmt:message key="withdraw.code.desc"/></p>
      </div>
    </div>

    <div class="stepper mb-4" data-stepper>
      <div class="step active" data-step-dot>
        <div class="step-circle"><i class="bi bi-check-lg"></i>1</div>
        <div class="step-label"><fmt:message key="withdraw.step1"/></div>
      </div>
      <div class="step" data-step-dot>
        <div class="step-circle"><i class="bi bi-check-lg"></i>2</div>
        <div class="step-label"><fmt:message key="withdraw.code.title"/></div>
      </div>
    </div>

    <div data-stepper>

      <div class="panel step-panel">
        <div class="panel-body">
          <form class="validates" novalidate>

            <label class="form-label" for="otp-amount"><fmt:message key="withdraw.amount"/> (<fmt:message key="common.currency"/>)</label>
            <input type="number" class="form-control form-control-lg mb-3" id="otp-amount" name="amount"
                   min="1" step="0.01" placeholder="0.00" required>
            <div class="d-flex gap-2 flex-wrap mb-4" data-amount-chips="otp-amount">
              <button type="button" class="btn btn-outline-line btn-sm chip" data-value="100">100</button>
              <button type="button" class="btn btn-outline-line btn-sm chip" data-value="200">200</button>
              <button type="button" class="btn btn-outline-line btn-sm chip" data-value="500">500</button>
              <button type="button" class="btn btn-outline-line btn-sm chip" data-value="1000">1,000</button>
              <button type="button" class="btn btn-outline-line btn-sm chip" data-value="5000">5,000</button>
            </div>

            <label class="form-label" for="otp-pin"><fmt:message key="withdraw.pinConfirm"/></label>
            <div class="input-group">
              <input type="password" class="form-control form-control-lg" id="otp-pin" name="pin"
                     placeholder="••••" data-pin-input inputmode="numeric" required>
              <button class="input-group-text" type="button" data-toggle-pin="otp-pin" tabindex="-1"><i class="bi bi-eye"></i></button>
            </div>

            <div class="d-flex justify-content-end mt-4">
              <button type="button" class="btn btn-primary btn-lg" data-next>
                <i class="bi bi-qr-code"></i> <fmt:message key="withdraw.generate"/>
              </button>
            </div>
          </form>
        </div>
      </div>

      <div class="panel step-panel d-none">
        <div class="panel-body">
          <div class="success-wrap">
            <div class="success-icon" style="background:linear-gradient(135deg,#fbbf24,#d97706)"><i class="bi bi-qr-code"></i></div>
            <h3 class="fw-bold mb-2"><fmt:message key="withdraw.code.title"/></h3>
            <p class="text-muted"><fmt:message key="withdraw.code.desc"/></p>
            <div class="code-box" style="max-width:280px">
              112233
            </div>
            <div class="d-flex justify-content-center gap-2 mb-3">
              <button type="button" class="btn btn-soft btn-sm" data-copy="112233">
                <i class="bi bi-clipboard"></i> <fmt:message key="common.copy"/>
              </button>
              <span class="timer-chip" data-countdown="600"><i class="bi bi-hourglass-split"></i> <fmt:message key="withdraw.code.expires"/> <span data-countdown-time></span></span>
            </div>
            <p class="text-muted small"><fmt:message key="common.amount"/>: <strong id="ok-otp-amount" data-fill="#otp-amount">—</strong></p>
            <p class="text-muted small"><fmt:message key="common.date"/>: <span style="direction:ltr">2026-08-04 15:30</span></p>
            <div class="d-flex justify-content-center gap-2 flex-wrap mt-2">
              <a href="${appURL}atm/index.html" class="btn btn-primary btn-lg">
                <i class="bi bi-bank2"></i> <fmt:message key="withdraw.goToAtm"/>
              </a>
              <a href="${appURL}home.jsp${qLang}" class="btn btn-outline-line btn-lg">
                <fmt:message key="withdraw.done"/>
              </a>
            </div>
          </div>
        </div>
      </div>

    </div>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>