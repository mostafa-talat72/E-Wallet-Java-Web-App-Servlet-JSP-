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

<%--
  ADD MONEY PAGE (authenticated)
  Purpose: top up the wallet balance using a saved bank card.
  Access: logged-in users only; saved cards are loaded via cardController.
  Controller: posts to /E-Wallet/transactionController?action=addMoney; when no
  cards exist yet the page redirects to cardController?action=getAllCards.
  Displays: 3-step wizard (choose card, amount, confirm + PIN) and a
  success/error receipt after submission.
--%>
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

    <%-- Add-money form: wraps the 3-step wizard and posts to transactionController?action=addMoney. --%>
    <form id="addMoneyForm" class="validates" action="${appURL}transactionController?action=addMoney" method="post" novalidate>
    <%-- 3-step wizard (stepper): pick card, enter amount, confirm with PIN. --%>
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

      <%-- Step 1: choose a saved (non-expired) card; the picker writes card details into hidden fields. --%>
        <div class="panel step-panel">
        <div class="panel-body">
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
                        <!-- Only the last 4 digits are rendered; the full number is stored in the data-number attribute. -->
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
        </div>
      </div>

      <div class="panel step-panel d-none">
        <div class="panel-body">
            <div class="mb-4">
              <%-- Step 2: enter the amount to add, with quick-amount chips. --%>
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
          </div>
          <%-- Step 3: review the receipt (card + amount), then confirm with the wallet PIN. --%>
          <div class="mb-4 mt-4 text-center">
            <label class="form-label" for="pin"><fmt:message key="add.pin.title"/></label>
            <p class="text-muted mb-3" style="font-size:.9rem"><fmt:message key="add.pin.desc"/></p>
            <div class="input-group input-group-lg mx-auto" style="max-width:280px">
              <input type="password" class="form-control text-center" id="pin" name="pin"
                     placeholder="••••••" value="${fn:escapeXml(param.pin)}"
                     data-pin-input inputmode="numeric" dir="ltr" autocomplete="off" required>
              <button class="input-group-text" type="button" data-toggle-pin="pin" tabindex="-1">
                <i class="bi bi-eye"></i>
              </button>
            </div>
          </div>
          <div class="d-flex justify-content-between align-items-center mt-4">
            <button type="button" class="btn btn-outline-line btn-lg" data-prev>
              <i class="bi bi-arrow-right"></i> <fmt:message key="common.back"/>
            </button>
            <button type="submit" class="btn btn-primary btn-lg" data-finish>
              <i class="bi bi-check-lg"></i> <fmt:message key="common.confirm"/>
            </button>
          </div>
        </div>
      </div>

    </div>
    </form>

    <%-- Result panel (hidden by default): success or error receipt shown after submission. --%>
    <div class="panel shadow-sm d-none" data-done>
      <div class="panel-body">
        <div class="success-wrap">
          <c:choose>
            <c:when test="${not empty error}">
              <div class="success-icon" style="background:var(--danger)"><i class="bi bi-x-lg"></i></div>
              <h2 class="fw-bold mb-2"><fmt:message key="add.fail.title"/></h2>
              <p class="text-muted"><fmt:message key="add.fail.desc"/></p>
              <div class="form-alert" style="max-width:440px;margin:0 auto 1rem;text-align:start" role="alert">
                <i class="bi bi-exclamation-circle-fill"></i>
                <span><fmt:message key="${error}"/></span>
              </div>
            </c:when>
            <c:otherwise>
              <div class="success-icon"><i class="bi bi-wallet2"></i></div>
              <h2 class="fw-bold mb-2"><fmt:message key="add.success.title"/></h2>
              <p class="text-muted"><fmt:message key="add.success.desc"/></p>
              <c:if test="${not empty success}">
                <div class="form-alert" style="max-width:440px;margin:0 auto 1rem;text-align:start" role="alert">
                  <i class="bi bi-check-circle-fill"></i>
                  <span><fmt:message key="add.success.done"/></span>
                </div>
              </c:if>
            </c:otherwise>
          </c:choose>
          <div class="receipt">
            <div class="receipt-row"><span><fmt:message key="add.method"/></span><strong><fmt:message key="add.method.card"/></strong></div>
            <div class="receipt-row"><span><fmt:message key="cards.cardId"/></span><strong dir="ltr" style="font-family:monospace;letter-spacing:1px"><c:out value="${cardNumberTransaction}" default="—"/></strong></div>
            <div class="receipt-row"><span><fmt:message key="common.amount"/></span><strong class="amount-selected" data-fill="#amount">—</strong></div>
            <div class="receipt-row"><span><fmt:message key="common.ref"/></span><strong class="ref-code"><%= request.getAttribute("txRef") != null? request.getAttribute("txRef") : request.getAttribute("transactionReference") != null? request.getAttribute("transactionReference") : "—" %></strong></div>
            <div class="receipt-row"><span><fmt:message key="common.date"/></span><strong style="direction:ltr"><%= request.getAttribute("txDate") != null? request.getAttribute("txDate") : request.getAttribute("created_at") != null? request.getAttribute("created_at") : "—" %></strong></div>
          </div>
          <div class="d-flex justify-content-center gap-2 flex-wrap">
            <a href="${appURL}cardController?action=getAllCards&redirect=add-money" class="btn btn-primary btn-lg"><fmt:message key="common.new"/> <fmt:message key="add.title"/></a>
            <a href="${appURL}home.jsp${qLang}" class="btn btn-outline-line btn-lg"><fmt:message key="nav.dashboard"/></a>
          </div>
        </div>
      </div>
    </div>

  </div>
</main>



<%@ include file="WEB-INF/partials/footer.jsp" %>

<%-- If the controller set a done/error flag, hide the wizard and reveal the result panel on load. --%>

<%
	String doneFlag = (String) request.getAttribute("done");
	String errFlag = (String) request.getAttribute("error");
	if ((doneFlag != null && !doneFlag.isEmpty()) || (errFlag != null && !errFlag.isEmpty())) {
%>
<script>
  document.getElementById("addMoneyForm").classList.add("d-none");
  var donePanel = document.querySelector("[data-done]");
  if (donePanel) donePanel.classList.remove("d-none");
  window.scrollTo({ top: 0, behavior: "smooth" });
</script>
<%
	}
%>