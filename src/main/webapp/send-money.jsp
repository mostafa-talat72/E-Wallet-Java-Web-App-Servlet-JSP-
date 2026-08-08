<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="send.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="send.subtitle"/></c:set>
<c:set var="activeMenu" value="send"/>
<%@ include file="WEB-INF/partials/demo-data.jsp" %>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<main class="main-content">
  <div class="content-wrap" style="max-width:860px">

    <c:set var="pageActions">
      <span class="badge badge-info"><i class="bi bi-lightning-charge-fill"></i> <fmt:message key="tx.type.transfer"/></span>
    </c:set>
    <%@ include file="WEB-INF/partials/page-head.jsp" %>

    <div data-stepper>
      <div class="stepper mb-4">
        <div class="step active" data-step-dot>
          <div class="step-circle"><i class="bi bi-check-lg"></i>1</div>
          <div class="step-label"><fmt:message key="send.recipient"/></div>
        </div>
        <div class="step" data-step-dot>
          <div class="step-circle"><i class="bi bi-check-lg"></i>2</div>
          <div class="step-label"><fmt:message key="send.review"/></div>
        </div>
        <div class="step" data-step-dot>
          <div class="step-circle"><i class="bi bi-check-lg"></i>3</div>
          <div class="step-label"><fmt:message key="send.otp.title"/></div>
        </div>
      </div>

      <div class="panel step-panel">
        <div class="panel-body">
          <form class="validates" novalidate>
            <div class="mb-4">
              <label class="form-label" for="recipient"><fmt:message key="send.recipient"/></label>
              <input type="tel" class="form-control form-control-lg${not empty err.recipient ? ' is-invalid' : ''}" id="recipient" name="recipient"
                     value="${fn:escapeXml(param.recipient)}"
                     placeholder="<fmt:message key="send.recipientPh"/>" data-phone required>
              <c:if test="${not empty err.recipient}"><div class="form-error show"><fmt:message key="${err.recipient}"/></div></c:if>
              <div class="mt-2 d-flex gap-2 flex-wrap">
                <button type="button" class="btn btn-outline-line btn-sm" data-contact="recipient" data-number="01123456789">01123456789</button>
                <button type="button" class="btn btn-outline-line btn-sm" data-contact="recipient" data-number="01098765432">01098765432</button>
              </div>
            </div>
            <div class="mb-4">
              <label class="form-label" for="amount"><fmt:message key="common.amount"/> (<fmt:message key="common.currency"/>)</label>
              <input type="number" class="form-control form-control-lg${not empty err.amount ? ' is-invalid' : ''}" id="amount" name="amount"
                     value="${fn:escapeXml(param.amount)}"
                     min="1" step="0.01" placeholder="0.00" required>
              <c:if test="${not empty err.amount}"><div class="form-error show"><fmt:message key="${err.amount}"/></div></c:if>
              <div class="mt-2 d-flex gap-2 flex-wrap" data-amount-chips="amount">
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="50">50</button>
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="100">100</button>
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="500">500</button>
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="1000">1,000</button>
              </div>
            </div>
            <div class="mb-4">
              <label class="form-label" for="note"><fmt:message key="send.note"/></label>
              <input type="text" class="form-control${not empty err.note ? ' is-invalid' : ''}" id="note" name="note"
                     value="${fn:escapeXml(param.note)}"
                     placeholder="<fmt:message key="send.notePh"/>" maxlength="200">
              <c:if test="${not empty err.note}"><div class="form-error show"><fmt:message key="${err.note}"/></div></c:if>
            </div>
            <div class="d-flex justify-content-between align-items-center">
              <span class="small text-muted fw-semibold">
                <fmt:message key="common.available"/>:
                <strong class="text-success"><fmt:formatNumber value="${sessionScope.wallet.status}" pattern="#,##0.00"/> <fmt:message key="common.currency"/></strong>
              </span>
              <button type="button" class="btn btn-primary btn-lg" data-next>
                <fmt:message key="common.continue"/> <i class="bi bi-arrow-left"></i>
              </button>
            </div>
          </form>
        </div>
      </div>

      <div class="panel step-panel d-none">
        <div class="panel-head">
          <h5 class="panel-title"><i class="bi bi-receipt"></i> <fmt:message key="send.preview.title"/></h5>
        </div>
        <div class="panel-body">
          <div class="receipt" style="max-width:none">
            <div class="receipt-row">
              <span><fmt:message key="send.preview.to"/></span>
              <strong style="direction:ltr" id="preview-phone" data-fill="#recipient">—</strong>
            </div>
            <div class="receipt-row">
              <span><fmt:message key="common.amount"/></span>
              <strong id="preview-amount" data-fill="#amount">—</strong>
            </div>
            <div class="receipt-row">
              <span><fmt:message key="send.fees"/></span>
              <strong>0.00 <fmt:message key="common.currency"/></strong>
            </div>
            <div class="receipt-row">
              <span><fmt:message key="send.total"/></span>
              <strong class="text-success" id="preview-total" data-fill="#amount">—</strong>
            </div>
            <div class="receipt-row">
              <span><fmt:message key="send.note"/></span>
              <strong id="preview-note" data-fill="#note">—</strong>
            </div>
          </div>
          <div class="d-flex justify-content-between gap-3">
            <button type="button" class="btn btn-outline-line btn-lg" data-prev>
              <i class="bi bi-arrow-right"></i> <fmt:message key="common.back"/>
            </button>
            <button type="button" class="btn btn-primary btn-lg" data-next>
              <fmt:message key="common.confirm"/> <i class="bi bi-shield-check"></i>
            </button>
          </div>
        </div>
      </div>

      <div class="panel step-panel d-none">
        <div class="panel-body text-center">
          <div class="success-wrap" style="padding-bottom:0">
            <div class="success-icon" style="width:72px;height:72px;font-size:2.2rem"><i class="bi bi-shield-lock"></i></div>
            <h3 class="fw-bold"><fmt:message key="send.otp.title"/></h3>
            <p class="text-muted">
              <fmt:message key="send.otp.desc"/>
            </p>
            <p class="text-muted small mb-3"><fmt:message key="send.preview.to"/> <strong style="direction:ltr" id="otp-phone" data-fill="#recipient">—</strong></p>
              <div class="otp-wrap">
              <div class="otp-row" dir="ltr">
                <input type="password" class="otp-input${not empty err.otp ? ' is-invalid' : ''}" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
              </div>
              <c:if test="${not empty err.otp}"><div class="form-error show" style="margin-top:.5rem"><fmt:message key="${err.otp}"/></div></c:if>
              <button type="button" class="btn btn-sm btn-outline-line mt-2" data-toggle-otp aria-label="Show PIN">
                <i class="bi bi-eye"></i>
              </button>
            </div>
            <p class="text-muted small"><fmt:message key="common.pin"/></p>
            <div class="d-flex justify-content-center gap-3 mb-4">
              <button type="button" class="btn btn-outline-line btn-lg" data-prev>
                <i class="bi bi-arrow-right"></i> <fmt:message key="common.back"/>
              </button>
              <button type="button" class="btn btn-primary btn-lg" data-otp-verify="#send-success">
                <fmt:message key="common.confirm"/> <i class="bi bi-check-lg"></i>
              </button>
            </div>
            <p class="text-muted small mt-2 mb-0" style="direction:ltr">Demo PIN: <b>123456</b></p>
          </div>
        </div>
      </div>
    </div>

    <div id="send-success" class="panel d-none">
      <div class="panel-body">
        <div class="success-wrap">
          <div class="success-icon"><i class="bi bi-check-lg"></i></div>
          <h2 class="fw-bold mb-2"><fmt:message key="send.success.title"/></h2>
          <p class="text-muted"><fmt:message key="send.success.desc"/></p>
          <div class="receipt">
            <div class="receipt-row"><span><fmt:message key="send.preview.to"/></span><strong style="direction:ltr" id="ok-phone" data-fill="#recipient">—</strong></div>
            <div class="receipt-row"><span><fmt:message key="common.amount"/></span><strong id="ok-amount" data-fill="#amount">—</strong></div>
            <div class="receipt-row"><span><fmt:message key="common.ref"/></span><strong class="ref-code">TX-882135</strong></div>
            <div class="receipt-row"><span><fmt:message key="common.date"/></span><strong style="direction:ltr">2026-08-04 15:20</strong></div>
          </div>
          <div class="d-flex justify-content-center gap-2 flex-wrap">
            <a href="${appURL}send-money.jsp${qLang}" class="btn btn-primary btn-lg">
              <i class="bi bi-send"></i> <fmt:message key="send.newTx"/>
            </a>
            <a href="${appURL}home.jsp${qLang}" class="btn btn-outline-line btn-lg">
              <fmt:message key="nav.dashboard"/>
            </a>
          </div>
        </div>
      </div>
    </div>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>
