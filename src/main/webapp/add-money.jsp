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

    <span class="d-none" id="ok-method-label"><fmt:message key="add.method.card"/></span>

    <div class="panel" id="card-panel">
      <div class="panel-body">
        <form class="validates" id="card-form" novalidate>
          <div class="row g-4">
            <div class="col-12">
              <label class="form-label" for="add-card"><fmt:message key="add.card.title"/></label>
              <input type="text" class="form-control form-control-lg" id="add-card" name="cardNumber"
                     placeholder="<fmt:message key="add.card.ph"/>" data-card-input required>
            </div>
            <div class="col-12 col-md-6">
              <label class="form-label" for="add-name"><fmt:message key="add.card.name"/></label>
              <input type="text" class="form-control" id="add-name" name="cardName"
                     placeholder="AHMED MOHAMED" required>
            </div>
            <div class="col-6 col-md-3">
              <label class="form-label" for="add-exp"><fmt:message key="add.card.exp"/></label>
              <input type="text" class="form-control" id="add-exp" name="expire" placeholder="MM/YY" data-exp required>
            </div>
            <div class="col-6 col-md-3">
              <label class="form-label" for="add-cvv"><fmt:message key="add.card.cvv"/></label>
              <div class="input-group">
                <input type="password" class="form-control" id="add-cvv" name="cvv" placeholder="•••" data-cvv required>
                <button class="input-group-text" type="button" data-toggle-pin="add-cvv" tabindex="-1"><i class="bi bi-eye"></i></button>
              </div>
            </div>
            <div class="col-12">
              <div class="form-check">
                <input class="form-check-input" type="checkbox" id="save-card" checked>
                <label class="form-check-label small fw-semibold text-muted" for="save-card">
                  <fmt:message key="add.card.save"/>
                </label>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>

    <div class="panel" id="amount-panel">
      <div class="panel-body">
        <div class="row g-4 align-items-end">
          <div class="col-12 col-md-6">
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
          <div class="col-12 col-md-6">
            <button type="button" class="btn btn-primary btn-lg w-100" data-add-confirm>
              <i class="bi bi-check2-circle"></i> <fmt:message key="add.confirm.title"/>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div id="add-success" class="panel d-none">
      <div class="panel-body">
        <div class="success-wrap">
          <div class="success-icon"><i class="bi bi-wallet2"></i></div>
          <h2 class="fw-bold mb-2"><fmt:message key="add.success.title"/></h2>
          <p class="text-muted"><fmt:message key="add.success.desc"/></p>
          <div class="receipt">
            <div class="receipt-row"><span><fmt:message key="add.method"/></span><strong id="ok-method">—</strong></div>
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
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>
