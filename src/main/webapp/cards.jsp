<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="cards.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="cards.subtitle"/></c:set>
<c:set var="activeMenu" value="cards"/>
<%@ include file="WEB-INF/partials/demo-data.jsp" %>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<main class="main-content">
  <div class="content-wrap">

    <c:set var="pageActions">
      <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCardModal">
        <i class="bi bi-plus-lg"></i> <fmt:message key="cards.add"/>
      </button>
    </c:set>
    <%@ include file="WEB-INF/partials/page-head.jsp" %>

    <div class="row g-4" id="cards-grid">
      <c:forEach var="card" items="${cards}">
        <div class="col-12 col-md-6 col-xl-4" data-card-widget>
          <div class="bank-card theme-${card.tone}">
            <div class="card-bg"></div>
            <div class="card-top">
              <span class="card-brand"><i class="bi bi-wallet2"></i> E-Wallet</span>
              <span class="badge badge-white">${not empty card.label ? card.label : card.bank}</span>
            </div>
            <div class="card-number">${card.number.substring(0,4)} •••• •••• ${card.number.substring(12)}</div>
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
          <div class="d-flex align-items-center justify-content-between mt-3">
            <c:choose>
              <c:when test="${card.status == 1}">
                <span class="badge badge-success"><i class="bi bi-check-circle"></i> <fmt:message key="common.active"/></span>
              </c:when>
              <c:otherwise>
                <span class="badge badge-neutral"><fmt:message key="common.inactive"/></span>
              </c:otherwise>
            </c:choose>
            <div class="d-flex gap-2">
              <form action="${appURL}walletController?action=toggleCard" method="post" data-card-toggle-form>
                <input type="hidden" name="cardNumber" value="${card.number}">
                <input type="hidden" name="status" value="${card.status}">
                <label class="form-check form-switch m-0" title="toggle">
                  <input class="form-check-input" type="checkbox" data-card-toggle ${card.status == 1 ? 'checked' : ''}>
                </label>
              </form>
              <button type="button" class="btn btn-danger-soft btn-icon-sm" data-delete-card data-card-number="${card.number}">
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
<%
		Map<String, String> err = (Map<String, String>) request.getAttribute("errors");
		String cardNumberErr = "";
		String expMonthErr = "";
		String expYearErr = "";
		String cvvErr = "";
		String addCardErr = "";
		
		String cardNumberErrVal = request.getAttribute("cardNumberErrVal") == null? "" : (String) request.getAttribute("cardNumberErrVal");
		String cardNameErrVal = request.getAttribute("cardNameErrVal") == null? "" :(String) request.getAttribute("cardNameErrVal");
		String cardHolderNameErrVal = request.getAttribute("cardHolderNameErrVal") == null? "" :(String) request.getAttribute("cardHolderNameErrVal");
		String bankNameErrVal = request.getAttribute("bankNameErrVal") == null? "" :(String) request.getAttribute("bankNameErrVal");
		String expMonthErrVal = request.getAttribute("expMonthErrVal") == null? "" :(String) request.getAttribute("expMonthErrVal");
		String expYearErrVal = request.getAttribute("expYearErrVal") == null? "" :(String) request.getAttribute("expYearErrVal");
		String cvvErrVal = request.getAttribute("cvvErrVal") == null? "" :(String) request.getAttribute("cvvErrVal");

	
		
		if(err != null) {
		    for(Map.Entry<String, String> entry : err.entrySet()) {
		        String key = entry.getKey();
		        String value = entry.getValue();
		        
		        if("cardNumber".equals(key)) {
		        	cardNumberErr = value;
		        } else if("phoneNumber".equals(key)) {
		        	phoneNumberErr = value;
		        } else if("nationalId".equals(key)) {
		            nationalIdErr = value;
		        } else if("pin".equals(key)) {
		            pinErr = value;
		        }else if("pinConfirm".equals(key)) {
		            pinConfirmErr = value;
		        }else if("siginUpErr".equals(key)){
		        	siginUpErr = value;
		        }
		    }
		}
	%>
    <div class="modal fade" id="addCardModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" style="border-radius:18px;border:0;box-shadow:var(--shadow)">
          <div class="modal-header" style="border-bottom:1px solid var(--border)">
            <h5 class="modal-title fw-bold"><fmt:message key="cards.modal.title"/></h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form id="card-add-form" action="/E-Wallet/cardController?action=addCard" class="validates" novalidate>
              <div class="mb-3">
                <label class="form-label"><fmt:message key="cards.cardId"/></label>
                <div class="otp-wrap" id="m-card-parts">
                  <div class="otp-row card-parts" dir="ltr">
                    <input type="text" name="cardPart1" class="otp-input card-part${not empty err.cardNumber ? ' is-invalid' : ''}" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 1" required>
                    <input type="text" name="cardPart2" class="otp-input card-part" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 2" required>
                    <input type="text" name="cardPart3" class="otp-input card-part" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 3" required>
                    <input type="text" name="cardPart4" class="otp-input card-part" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 4" required>
                  </div>
                </div>
                <c:if test="${not empty err.cardNumber}"><div class="form-error show" style="margin-top:.5rem"><fmt:message key="${err.cardNumber}"/></div></c:if>
              </div>
              <div class="mb-3">
                <label class="form-label"><fmt:message key="cards.label"/></label>
                <input type="text" class="form-control${not empty err.label ? ' is-invalid' : ''}" id="m-label" name="cardName" value="${fn:escapeXml(param.label)}" placeholder="Salary card" maxlength="30">
                <c:if test="${not empty err.label}"><div class="form-error show"><fmt:message key="${err.label}"/></div></c:if>
              </div>
              <div class="mb-3">
                <label class="form-label"><fmt:message key="add.card.name"/></label>
                <input type="text" class="form-control${not empty err.name ? ' is-invalid' : ''}" id="m-name" name="cardHolderName" value="${fn:escapeXml(param.name)}" placeholder="AHMED MOHAMED" required>
                <c:if test="${not empty err.name}"><div class="form-error show"><fmt:message key="${err.name}"/></div></c:if>
              </div>
              <div class="mb-3">
                <label class="form-label"><fmt:message key="cards.bankName"/></label>
                <input type="text" class="form-control${not empty err.bank ? ' is-invalid' : ''}" id="m-bank" name="bankName" value="${fn:escapeXml(param.bank)}" placeholder="Banque Misr" required>
                <c:if test="${not empty err.bank}"><div class="form-error show"><fmt:message key="${err.bank}"/></div></c:if>
              </div>
              <div class="row g-3 mb-3">
                <div class="col-6">
                  <label class="form-label"><fmt:message key="add.card.exp"/></label>
                  <div class="d-flex gap-2">
                    <select class="form-select${not empty err.expDate ? ' is-invalid' : ''}" id="m-exp-m" name="expMonth" data-exp-m required>
                      <option value="" selected disabled><fmt:message key="add.card.mm"/></option>
                    </select>
                    <select class="form-select${not empty err.expDate ? ' is-invalid' : ''}" id="m-exp-y" name="expYear" data-exp-y required>
                      <option value="" selected disabled><fmt:message key="add.card.yy"/></option>
                    </select>
                  </div>
                  <c:if test="${not empty err.expDate}"><div class="form-error show"><fmt:message key="${err.expDate}"/></div></c:if>
                </div>
                <div class="col-6">
                  <label class="form-label" for="m-cvv"><fmt:message key="add.card.cvv"/></label>
                  <div class="input-group">
                    <input type="password" class="form-control${not empty err.cvv ? ' is-invalid' : ''}" id="m-cvv" name="cvv" placeholder="•••" data-cvv maxlength="3" inputmode="numeric" required>
                    <button class="input-group-text" type="button" data-toggle-pin="m-cvv" tabindex="-1"><i class="bi bi-eye"></i></button>
                  </div>
                  <c:if test="${not empty err.cvv}"><div class="form-error show"><fmt:message key="${err.cvv}"/></div></c:if>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer" style="border-top:1px solid var(--border)">
            <button type="button" class="btn btn-outline-line" data-bs-dismiss="modal"><fmt:message key="common.cancel"/></button>
            <button type="submit"  class="btn btn-primary" data-add-card><fmt:message key="common.save"/></button>
          </div>
        </div>
      </div>
    </div>

  <div class="modal fade" id="deleteCardModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" style="border-radius:18px;border:0;box-shadow:var(--shadow)">
          <div class="modal-header" style="border-bottom:1px solid var(--border)">
            <h5 class="modal-title fw-bold"><fmt:message key="cards.delete"/></h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form id="card-delete-form" action="${appURL}walletController?action=deleteCard" method="post">
              <input type="hidden" name="cardNumber" id="delCardNumber">
              <p class="mb-0"><fmt:message key="cards.deleteConfirm"/></p>
            </form>
          </div>
          <div class="modal-footer" style="border-top:1px solid var(--border)">
            <button type="button" class="btn btn-outline-line" data-bs-dismiss="modal"><fmt:message key="common.cancel"/></button>
            <button type="submit" form="card-delete-form" class="btn btn-danger-soft" style="color:#fff;background:var(--danger)">
              <i class="bi bi-trash"></i> <fmt:message key="common.delete"/>
            </button>
          </div>
        </div>
      </div>
    </div>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>
