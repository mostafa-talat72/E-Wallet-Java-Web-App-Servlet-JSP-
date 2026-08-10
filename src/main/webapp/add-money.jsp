<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.ewallet.model.Card" %>
<%@ page import="com.ewallet.util.DateUtil" %>

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
<%
	List<Card> cards = (ArrayList<Card>) request.getAttribute("cards");
	if (cards == null) {
		response.sendRedirect("cardController?action=getAllCards&redirect=add-money");
		return;
	}
%>
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
          <div class="step-label"><fmt:message key="add.confirm.title"/></div>
        </div>
      </div>

      <div class="panel step-panel">
        <div class="panel-body">
          <form class="validates" novalidate>
            <div class="mb-4">
              <label class="form-label"><fmt:message key="add.card.saved"/></label>
              <%
              if(cards.isEmpty()){
              %>
                <div class="empty-message text-center py-5">
                  <i class="bi bi-credit-card-2-front" style="font-size:2rem;opacity:.4"></i>
                  <p class="text-muted mb-3" style="margin-top:.5rem"><fmt:message key="add.card.none"/></p>
                  <a class="btn btn-primary-soft" href="${appURL}cardController?action=getAllCards&redirect=cards">
                    <i class="bi bi-plus-lg"></i> <fmt:message key="cards.add"/>
                  </a>
                </div>
              <%
              }else{
              %>
                <div class="row g-3" id="saved-cards">
                  <%
                  for(Card card : cards){
                    if(!DateUtil.isExpired(card.getExpireDate())){
                  %>
                    <div class="col-12 col-sm-6 col-md-4">
                      <div class="bank-card picker-card theme-blue" data-saved-card
                           data-number="<%= card.getCardNumber() %>" data-name="<%= card.getCardName() == null? "" : card.getCardName() %>"
                           data-bank="${card.bank}" data-label="<%= card.getBankName() == null? "" : card.getBankName() %>"
                           data-holder="<%= card.getCardHolderName() == null? "" : card.getCardHolderName() %>"
                           data-exp-m="<%= DateUtil.getExpirationMonth(card.getExpireDate()) %>" data-exp-y="<%= DateUtil.getExpirationYear(card.getExpireDate()) %>"
                           data-cvv="<%= card.getCvv() %>">
                        <span class="picker-check"><i class="bi bi-check-lg"></i></span>
                        <div class="card-bg"></div>
                        <div class="card-top">
                          <span class="card-brand"><i class="bi bi-wallet2"></i><%= card.getBankName() == null? "" : card.getBankName() %></span>
                          <span class="badge badge-white"><%= card.getCardName() == null? "" : card.getCardName() %></span>
                        </div>
                        <div class="card-number" dir="ltr">•••• •••• •••• <%= card.getCardNumber().substring(12) %></div>
                        <div class="card-bottom">
                          <div class="card-holder">
                            <small><fmt:message key="cards.holder"/></small>
                            <strong><%= card.getCardHolderName() == null? "" : card.getCardHolderName() %></strong>
                          </div>
                          <div class="text-end">
                            <small class="d-block opacity-75" style="font-size:.62rem"><fmt:message key="cards.expires"/></small>
                            <strong style="font-family:monospace;letter-spacing:1px"><%= DateUtil.getExpirationMonth(card.getExpireDate()) %>/<%= DateUtil.getExpirationYear(card.getExpireDate()) %></strong>
                          </div>
                        </div>
                      </div>
                    </div>
                  <%
                    }
                  }
                  %>
                </div>
                <div class="d-flex justify-content-between align-items-center mt-3">
                  <button type="button" class="btn btn-sm btn-outline-line d-none" data-cancel-card>
                    <i class="bi bi-x-lg"></i> <fmt:message key="common.cancel"/>
                  </button>
                  <a class="btn btn-sm btn-primary-soft ms-auto" href="${appURL}cardController?action=getAllCards&redirect=cards">
                    <i class="bi bi-plus-lg"></i> <fmt:message key="add.card.manual"/>
                  </a>
                </div>
                <input type="hidden" name="cardNumber" id="add-card-number">
                <input type="hidden" name="label" id="add-label">
                <input type="hidden" name="holder" id="add-holder">
                <input type="hidden" name="cvv" id="add-cvv">
              <%
              }
              %>
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
              <div class="amount-input">
                <span class="currency-sign"><fmt:message key="common.currency"/></span>
                <input type="number" class="form-control form-control-lg" id="amount" name="amount"
                       value="${fn:escapeXml(param.amount)}" min="1" step="0.01" placeholder="0.00" required>
              </div>
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
        <div class="panel-body">
          <div class="receipt">
            <div class="receipt-row"><span><fmt:message key="add.method"/></span><strong><fmt:message key="add.method.card"/></strong></div>
            <div class="receipt-row"><span><fmt:message key="cards.bankName"/></span><strong id="ok-bank" data-fill="#add-label">—</strong></div>
            <div class="receipt-row"><span><fmt:message key="cards.cardId"/></span><strong id="ok-number" dir="ltr" data-fill="#add-card-number" data-fill-mask="cc" style="font-family:monospace;letter-spacing:1px">—</strong></div>
            <div class="receipt-row"><span><fmt:message key="cards.holder"/></span><strong id="ok-holder" data-fill="#add-holder">—</strong></div>
            <div class="receipt-row"><span><fmt:message key="common.amount"/></span><strong class="amount-selected" id="ok-amount" data-fill="#amount">—</strong></div>
            <div class="receipt-row"><span><fmt:message key="common.ref"/></span><strong class="ref-code" id="ok-ref">TX-<span id="ok-ref-num">------</span></strong></div>
          </div>
          <div class="d-flex justify-content-between align-items-center mt-4">
            <button type="button" class="btn btn-outline-line btn-lg" data-prev>
              <i class="bi bi-arrow-right"></i> <fmt:message key="common.back"/>
            </button>
            <button type="button" class="btn btn-primary btn-lg" data-finish>
              <i class="bi bi-check-lg"></i> <fmt:message key="common.confirm"/>
            </button>
          </div>
        </div>
      </div>

      <div class="panel step-panel d-none" data-done>
        <div class="panel-body">
          <div class="success-wrap">
            <div class="success-icon"><i class="bi bi-wallet2"></i></div>
            <h2 class="fw-bold mb-2"><fmt:message key="add.success.title"/></h2>
            <p class="text-muted"><fmt:message key="add.success.desc"/></p>
            <div class="d-flex justify-content-center gap-2 flex-wrap">
              <a href="${appURL}cardController?action=getAllCards&redirect=add-money" class="btn btn-primary btn-lg"><fmt:message key="common.new"/> <fmt:message key="add.title"/></a>
              <a href="${appURL}home.jsp${qLang}" class="btn btn-outline-line btn-lg"><fmt:message key="nav.dashboard"/></a>
            </div>
          </div>
        </div>
      </div>

    </div>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>