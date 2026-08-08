<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
              <label class="form-label"><fmt:message key="add.card.saved"/></label>
              <div class="row g-3" id="saved-cards">
                <c:forEach var="card" items="${cards}">
                  <div class="col-12 col-sm-6 col-md-4">
                    <div class="bank-card picker-card theme-${card.tone}" data-saved-card
                         data-number="${card.number}" data-name="${card.name}" data-bank="${card.bank}"
                         data-label="${card.label}"
                         data-exp-m="${card.expire.substring(0,2)}" data-exp-y="20${card.expire.substring(3)}"
                         data-cvv="${card.cvv}">
                      <span class="picker-check"><i class="bi bi-check-lg"></i></span>
                      <div class="card-bg"></div>
                      <div class="card-top">
                        <span class="card-brand"><i class="bi bi-wallet2"></i> E-Wallet</span>
                        <span class="badge badge-white">${not empty card.label ? card.label : card.bank}</span>
                      </div>
                      <div class="card-number" dir="ltr">•••• •••• •••• ${card.number.substring(12)}</div>
                      <div class="card-bottom">
                        <div class="card-holder">
                          <small><fmt:message key="cards.holder"/></small>
                          <strong>${card.name}</strong>
                        </div>
                        <div class="text-end">
                          <small class="d-block opacity-75" style="font-size:.62rem"><fmt:message key="cards.expires"/></small>
                          <strong style="font-family:monospace;letter-spacing:1px">${card.expire}</strong>
                        </div>
                      </div>
                    </div>
                  </div>
                </c:forEach>
              </div>
              <div class="d-flex justify-content-between align-items-center mt-3">
                <button type="button" class="btn btn-sm btn-outline-line d-none" data-cancel-card>
                  <i class="bi bi-x-lg"></i> <fmt:message key="common.cancel"/>
                </button>
                <a class="btn btn-sm btn-primary-soft" href="${appURL}cards.jsp${qLang}&addCard=1">
                  <i class="bi bi-plus-lg"></i> <fmt:message key="add.card.manual"/>
                </a>
              </div>
            </div>
            <div class="mb-4">
              <label class="form-label"><fmt:message key="add.card.title"/></label>
              <div class="otp-wrap" id="card-parts">
                <div class="otp-row card-parts" dir="ltr">
                  <input type="text" class="otp-input card-part${not empty err.cardNumber ? ' is-invalid' : ''}" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 1" required>
                  <input type="text" class="otp-input card-part" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 2" required>
                  <input type="text" class="otp-input card-part" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 3" required>
                  <input type="text" class="otp-input card-part" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 4" required>
                </div>
              </div>
              <c:if test="${not empty err.cardNumber}"><div class="form-error show" style="margin-top:.5rem"><fmt:message key="${err.cardNumber}"/></div></c:if>
            </div>
            <div class="row g-4 mb-4">
              <div class="col-12 col-md-6">
                <label class="form-label" for="add-label"><fmt:message key="cards.label"/></label>
                <input type="text" class="form-control${not empty err.label ? ' is-invalid' : ''}" id="add-label" name="label"
                       value="${fn:escapeXml(param.label)}"
                       placeholder="Salary card" maxlength="30">
                <c:if test="${not empty err.label}"><div class="form-error show"><fmt:message key="${err.label}"/></div></c:if>
              </div>
              <div class="col-12 col-md-6">
                <label class="form-label" for="add-name"><fmt:message key="add.card.name"/></label>
                <input type="text" class="form-control${not empty err.cardName ? ' is-invalid' : ''}" id="add-name" name="cardName"
                       value="${fn:escapeXml(param.cardName)}"
                       placeholder="AHMED MOHAMED" required>
                <c:if test="${not empty err.cardName}"><div class="form-error show"><fmt:message key="${err.cardName}"/></div></c:if>
              </div>
              <div class="col-6 col-md-3">
                <label class="form-label"><fmt:message key="add.card.exp"/></label>
                <div class="d-flex gap-2">
                  <select class="form-select${not empty err.expDate ? ' is-invalid' : ''}" id="add-exp-m" name="expMonth" data-exp-m required>
                    <option value="" selected disabled><fmt:message key="add.card.mm"/></option>
                  </select>
                  <select class="form-select${not empty err.expDate ? ' is-invalid' : ''}" id="add-exp-y" name="expYear" data-exp-y required>
                    <option value="" selected disabled><fmt:message key="add.card.yy"/></option>
                  </select>
                </div>
                <c:if test="${not empty err.expDate}"><div class="form-error show"><fmt:message key="${err.expDate}"/></div></c:if>
              </div>
              <div class="col-6 col-md-3">
                <label class="form-label" for="add-cvv"><fmt:message key="add.card.cvv"/></label>
                <div class="input-group">
                  <input type="password" class="form-control${not empty err.cvv ? ' is-invalid' : ''}" id="add-cvv" name="cvv" placeholder="•••" data-cvv maxlength="3" inputmode="numeric" required>
                  <button class="input-group-text" type="button" data-toggle-pin="add-cvv" tabindex="-1"><i class="bi bi-eye"></i></button>
                </div>
                <c:if test="${not empty err.cvv}"><div class="form-error show"><fmt:message key="${err.cvv}"/></div></c:if>
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
              <input type="number" class="form-control form-control-lg${not empty err.amount ? ' is-invalid' : ''}" id="amount" name="amount"
                     value="${fn:escapeXml(param.amount)}"
                     min="1" step="0.01" placeholder="0.00" required>
              <c:if test="${not empty err.amount}"><div class="form-error show"><fmt:message key="${err.amount}"/></div></c:if>
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
                <input type="password" class="otp-input${not empty err.pin ? ' is-invalid' : ''}" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
                <input type="password" class="otp-input" data-otp maxlength="1" inputmode="numeric" dir="ltr">
              </div>
              <c:if test="${not empty err.pin}"><div class="form-error show" style="margin-top:.5rem"><fmt:message key="${err.pin}"/></div></c:if>
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

    <div class="modal fade" id="cvvModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered" style="max-width:360px">
        <div class="modal-content" style="border-radius:18px;border:0;box-shadow:var(--shadow)">
          <div class="modal-header" style="border-bottom:1px solid var(--border)">
            <h5 class="modal-title fw-bold"><fmt:message key="add.cvv.title"/></h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body text-center">
            <div class="success-icon" style="width:56px;height:56px;font-size:1.6rem;margin:0 auto .75rem"><i class="bi bi-shield-lock"></i></div>
            <p class="text-muted small mb-3" id="cvv-card-info" dir="ltr" style="font-weight:700;letter-spacing:1px">•••• ••••</p>
            <label class="form-label d-block"><fmt:message key="add.card.cvv"/></label>
            <div class="input-group mx-auto" style="max-width:150px">
              <input type="password" class="form-control text-center" id="modal-cvv" placeholder="•••" data-cvv maxlength="3" inputmode="numeric">
              <button class="input-group-text" type="button" data-toggle-pin="modal-cvv" tabindex="-1"><i class="bi bi-eye"></i></button>
            </div>
          </div>
          <div class="modal-footer" style="border-top:1px solid var(--border)">
            <button type="button" class="btn btn-outline-line" data-bs-dismiss="modal"><fmt:message key="common.cancel"/></button>
            <button type="button" class="btn btn-primary" data-cvv-confirm>
              <fmt:message key="common.confirm"/> <i class="bi bi-check-lg"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>
