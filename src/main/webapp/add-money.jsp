<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="add.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="add.subtitle"/></c:set>
<c:set var="activeMenu" value="add"/>
<%@ include file="WEB-INF/partials/demo-data.jsp" %>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<main class="main-content">
  <div class="content-wrap" style="max-width:860px">

    <c:set var="pageActions">
      <span class="badge badge-success"><i class="bi bi-credit-card-2-front"></i> <fmt:message key="add.method.card"/></span>
    </c:set>
    <%@ include file="WEB-INF/partials/page-head.jsp" %>

    <div data-stepper>
      <div class="stepper mb-4">
        <div class="step active" data-step-dot>
          <div class="step-circle"><i class="bi bi-check-lg"></i>1</div>
          <div class="step-label"><fmt:message key="add.card.title"/></div>
        </div>
        <div class="step" data-step-dot>
          <div class="step-circle"><i class="bi bi-check-lg"></i>2</div>
          <div class="step-label"><fmt:message key="add.amount.title"/></div>
        </div>
        <div class="step" data-step-dot>
          <div class="step-circle"><i class="bi bi-check-lg"></i>3</div>
          <div class="step-label"><fmt:message key="add.pin.title"/></div>
        </div>
      </div>

      <div class="panel step-panel">
        <div class="panel-body">
          <form class="validates" novalidate>
            <div class="mb-4">
              <label class="form-label"><fmt:message key="add.card.title"/></label>
              <div class="otp-wrap" id="card-parts">
                <div class="otp-row card-parts" dir="ltr">
                  <input type="text" class="otp-input card-part" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 1" required>
                  <input type="text" class="otp-input card-part" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 2" required>
                  <input type="text" class="otp-input card-part" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 3" required>
                  <input type="text" class="otp-input card-part" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 4" required>
                </div>
              </div>
            </div>
            <div class="row g-4 mb-4">
              <div class="col-12 col-md-6">
                <label class="form-label" for="add-name"><fmt:message key="add.card.name"/></label>
                <input type="text" class="form-control" id="add-name" name="cardName"
                       placeholder="AHMED MOHAMED" required>
              </div>
              <div class="col-6 col-md-3">
                <label class="form-label"><fmt:message key="add.card.exp"/></label>
                <div class="d-flex gap-2">
                  <select class="form-select" id="add-exp-m" name="expMonth" data-exp-m required>
                    <option value="" selected disabled><fmt:message key="add.card.mm"/></option>
                  </select>
                  <select class="form-select" id="add-exp-y" name="expYear" data-exp-y required>
                    <option value="" selected disabled><fmt:message key="add.card.yy"/></option>
                  </select>
                </div>
              </div>
              <div class="col-6 col-md-3">
                <label class="form-label" for="add-cvv"><fmt:message key="add.card.cvv"/></label>
                <div class="input-group">
                  <input type="password" class="form-control" id="add-cvv" name="cvv" placeholder="•••" data-cvv maxlength="3" inputmode="numeric" required>
                  <button class="input-group-text" type="button" data-toggle-pin="add-cvv" tabindex="-1"><i class="bi bi-eye"></i></button>
                </div>
              </div>
            </div>
            <div class="form-check mb-4">
              <input class="form-check-input" type="checkbox" id="save-card" checked>
              <label class="form-check-label small fw-semibold text-muted" for="save-card">
                <fmt:message key="add.card.save"/>
              </label>
            </div>
            <div class="d-flex justify-content-end">
              <button type="button" class="btn btn-primary btn-lg" data-next>
                <fmt:message key="common.continue"/> <i class="bi bi-arrow-left"></i>
              </button>
            </div>
          </form>
        </div>
      </div>

      <div class="panel step-panel d-none">
        <div class="panel-body">
          <form class="validates" novalidate>
            <div class="mb-4">
              <label class="form-label" for="amount"><fmt:message key="add.amount.title"/> (<fmt:message key="common.currency"/>)</label>
              <input type="number" class="form-control form-control-lg" id="amount" name="amount"
                     min="1" step="0.01" placeholder="0.00" required>
              <div class="mt-2 d-flex gap-2 flex-wrap" data-amount-chips="amount">
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="50">50</button>
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="100">100</button>
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="500">500</button>
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="1000">1,000</button>
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="5000">5,000</button>
              </div>
            </div>
            <div class="d-flex justify-content-between align-items-center">
              <button type="button" class="btn btn-outline-line btn-lg" data-prev>
                <i class="bi bi-arrow-right"></i> <fmt:message key="common.back"/>
              </button>
              <button type="button" class="btn btn-primary btn-lg" data-next>
                <fmt:message key="common.continue"/> <i class="bi bi-arrow-left"></i>
              </button>
            </div>
          </form>
        </div>
      </div>

      <div class="panel step-panel d-none">
        <div class="panel-body text-center">
          <div class="success-wrap" style="padding-bottom:0">
            <div class="success-icon" style="width:72px;height:72px;font-size:2.2rem"><i class="bi bi-shield-lock"></i></div>
            <h3 class="fw-bold"><fmt:message key="add.pin.title"/></h3>
            <p class="text-muted">
              <fmt:message key="add.pin.desc"/>
            </p>
            <p class="text-muted small mb-3"><fmt:message key="common.amount"/>: <strong id="pin-amount" data-fill="#amount">—</strong></p>
            <div class="d-flex align-items-center justify-content-center gap-2 mb-1">
              <label class="form-label mb-0"><fmt:message key="common.pin"/></label>
              <button type="button" class="btn btn-sm btn-outline-line" data-toggle-otp aria-label="Show PIN">
                <i class="bi bi-eye"></i>
              </button>
            </div>
            <div class="otp-wrap">
              <div class="otp-row" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
              </div>
            </div>
            <div class="d-flex justify-content-center gap-3 mb-4">
              <button type="button" class="btn btn-outline-line btn-lg" data-prev>
                <i class="bi bi-arrow-right"></i> <fmt:message key="common.back"/>
              </button>
              <button type="button" class="btn btn-primary btn-lg" data-next>
                <fmt:message key="common.confirm"/> <i class="bi bi-check-lg"></i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="panel step-panel d-none">
        <div class="panel-body">
          <div class="success-wrap">
            <div class="success-icon"><i class="bi bi-wallet2"></i></div>
            <h2 class="fw-bold mb-2"><fmt:message key="add.success.title"/></h2>
            <p class="text-muted"><fmt:message key="add.success.desc"/></p>
            <div class="receipt">
              <div class="receipt-row"><span><fmt:message key="add.method"/></span><strong><fmt:message key="add.method.card"/></strong></div>
              <div class="receipt-row"><span><fmt:message key="common.amount"/></span><strong id="ok-amount" data-fill="#amount">—</strong></div>
              <div class="receipt-row"><span><fmt:message key="common.ref"/></span><strong class="ref-code">TX-882136</strong></div>
              <div class="receipt-row"><span><fmt:message key="common.date"/></span><strong style="direction:ltr">2026-08-04 15:25</strong></div>
            </div>
            <div class="d-flex justify-content-center gap-2 flex-wrap">
              <a href="${appURL}add-money.jsp${qLang}" class="btn btn-primary btn-lg"><fmt:message key="common.new"/> <fmt:message key="add.title"/></a>
              <a href="${appURL}home.jsp${qLang}" class="btn btn-outline-line btn-lg"><fmt:message key="nav.dashboard"/></a>
            </div>
          </div>
        </div>
      </div>

    </div>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>
