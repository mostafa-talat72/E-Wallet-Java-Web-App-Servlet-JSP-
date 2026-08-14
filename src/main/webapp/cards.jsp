<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.ewallet.model.Card" %>
<%@ page import="com.ewallet.util.DateUtil" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="cards.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="cards.subtitle"/></c:set>
<c:set var="activeMenu" value="cards"/>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<%--
  CARDS PAGE (authenticated)
  Purpose: manage saved bank cards — list, activate/deactivate, add and delete.
  Access: logged-in users only; the card list is loaded via cardController.
  Controllers: updateCardStatus, addCard, deleteCard.
  Displays: card grid (masked numbers), status switches, add-card modal and
  delete-card confirmation modal.
--%>

<%
	List<Card> cards = (ArrayList<Card>) request.getAttribute("cards");
%>

<main class="main-content">
  <div class="content-wrap">

    <c:set var="pageActions">
      <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCardModal">
        <i class="bi bi-plus-lg"></i> <fmt:message key="cards.add"/>
      </button>
    </c:set>
    <%@ include file="WEB-INF/partials/page-head.jsp" %>

    <%-- Saved cards grid: masked number, bank, holder, expiry, status switch and delete button per card. --%>
    <div class="row g-4" id="cards-grid">
     <%
     	if(cards != null && !cards.isEmpty()){
     		for(Card card : cards){
     %>
	        <div class="col-12 col-md-6 col-xl-4" data-card-widget>
	          <div class="bank-card theme-blue">
	            <div class="card-bg"></div>
	            <div class="card-top">
	              <span class="card-brand"><i class="bi bi-wallet2"></i> <%= card.getBankName() == null? "" : card.getBankName()  %></span>
	              <span class="badge badge-white"><%= card.getCardName() == null? "" : card.getCardName() %></span>
	            </div>
	            <!-- Only the first and last 4 digits are shown; the middle is masked. -->
            <div class="card-number"><%= card.getCardNumber().substring(0,4) %> •••• •••• <%= card.getCardNumber().substring(12) %></div>
	            <div class="card-bottom">
	              <div class="card-holder">
	                <small><fmt:message key="cards.holder"/></small>
	                <strong><%= card.getCardHolderName() == null? "" : card.getCardHolderName() %></strong>
	              </div>
	              <div class="text-end">
	                <small class="d-block opacity-75" style="font-size:.62rem"><fmt:message key="cards.expires"/></small>
	                <strong style="font-family:monospace;letter-spacing:1px">
	                <%=DateUtil.getExpirationMonth(card.getExpireDate()) %>/<%=DateUtil.getExpirationYear(card.getExpireDate()) %></strong>
	              </div>
	            </div>
	          </div>
	          <div class="d-flex align-items-center justify-content-between mt-3">
	            <span class="badge <%= card.getStatus() == 1 ? "badge-success" : "badge-neutral" %>" data-card-status>
	            	<% if(card.getStatus() == 1){ %>
	            	<i class="bi bi-check-circle"></i> <fmt:message key="common.active"/>
	            	<% } else { %>
	            	<fmt:message key="common.inactive"/>
	            	<% } %>
	            </span>
	            <div class="d-flex gap-2">
	              <form action="/E-Wallet/cardController?action=updateCardStatus" method="post" data-card-toggle-form>
	                <input type="hidden" name="cardId" value="<%= card.getCardId() %>">
	                <input type="hidden" name="status" value="<%= card.getStatus() %>">
	                <label class="form-check form-switch m-0" title="toggle">
	                  <input class="form-check-input" type="checkbox" data-card-toggle <%= card.getStatus() == 1? "checked" : "" %>>
	                </label>
	              </form>
	              <button type="button" class="btn btn-danger-soft btn-icon-sm" data-delete-card
	                      data-card-id="<%= card.getCardId() %>"
	                      data-card-number="<%= card.getCardNumber() %>"
	                      data-card-label="<%= card.getCardName() %>"
	                      data-card-holder="<%= card.getCardHolderName() %>"
	                      data-card-bank="<%= card.getBankName() %>"
	                      data-card-expire="<%= DateUtil.getExpirationMonth(card.getExpireDate()) %>/<%= DateUtil.getExpirationYear(card.getExpireDate()) %>">
	                <i class="bi bi-trash"></i>
	              </button>
	            </div>
	          </div>
	        </div>
      <%
     		}
     	}
      %>
    </div>
<%
		Map<String, String> err = (Map<String, String>) request.getAttribute("errors");
		String cardNumberErr = "";
		String expirDateErr = "";
		String cvvErr = "";
		String addCardErr = "";
		
		String cardPart1ErrVal = request.getAttribute("cardPart1ErrVal") == null? "" : (String) request.getAttribute("cardPart1ErrVal");
		String cardPart2ErrVal = request.getAttribute("cardPart2ErrVal") == null? "" : (String) request.getAttribute("cardPart2ErrVal");
		String cardPart3ErrVal = request.getAttribute("cardPart3ErrVal") == null? "" : (String) request.getAttribute("cardPart3ErrVal");
		String cardPart4ErrVal = request.getAttribute("cardPart4ErrVal") == null? "" : (String) request.getAttribute("cardPart4ErrVal");
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
		        } else if("cvv".equals(key)) {
		        	cvvErr = value;
		        } else if("expirDate".equals(key)) {
		        	expirDateErr = value;
		        } else if("addCardErr".equals(key)) {
		        	addCardErr = value;
		        }
		    }
		}
	%>
    <%-- Add-card modal: the scriptlet above restored last-submitted values and per-field errors. --%>
    <div class="modal fade" id="addCardModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" style="border-radius:18px;border:0;box-shadow:var(--shadow)">
          <div class="modal-header" style="border-bottom:1px solid var(--border)">
            <h5 class="modal-title fw-bold"><fmt:message key="cards.modal.title"/></h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <%-- Add-card form: 4-part card number, label, holder, bank, expiry (MM/YY) and CVV; posts to cardController?action=addCard. --%>
            <form id="card-add-form" action="/E-Wallet/cardController?action=addCard" method="post" class="validates" novalidate>
               <% 
		          	if(!addCardErr.isEmpty()){
		         %>
			       <div class="form-alert" style="text-align:start" role="alert">
			       <i class="bi bi-exclamation-circle-fill"></i>
			       <span><fmt:message key="<%= addCardErr %>"/></span>
			       </div>
		        <% 
		          	}
		         %>
              
              <div class="mb-3">
                <label class="form-label"><fmt:message key="cards.cardId"/></label>
                <div class="otp-wrap" id="m-card-parts">
                  <div class="otp-row card-parts" dir="ltr">
                    <input type="text" name="cardPart1" class="otp-input card-part<%= !cardNumberErr.isEmpty()? " is-invalid":"" %>" 
                    value="<%= cardPart1ErrVal %>" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 1" required>
                    <input type="text" name="cardPart2" class="otp-input card-part<%= !cardNumberErr.isEmpty()? " is-invalid":"" %>"
                    value="<%= cardPart2ErrVal %>" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 2" required>
                    <input type="text" name="cardPart3" class="otp-input card-part<%= !cardNumberErr.isEmpty()? " is-invalid":"" %>"
                    value="<%= cardPart3ErrVal %>" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 3" required>
                    <input type="text" name="cardPart4" class="otp-input card-part<%= !cardNumberErr.isEmpty()? " is-invalid":"" %>"
                    value="<%= cardPart4ErrVal %>" data-card-part maxlength="4" inputmode="numeric" aria-label="card part 4" required>
                  </div>
                </div>
               <% 
          		if(!cardNumberErr.isEmpty()){
          		%>
				<div class="form-error show" style="margin-top:.5rem"><fmt:message key="<%= cardNumberErr %>"/></div>
				 <% 
		          	}
		         %>
              </div>
              <div class="mb-3">
                <label class="form-label"><fmt:message key="cards.label"/></label>
                <input type="text" class="form-control" id="m-label" name="cardName"
                 value="<%= cardNameErrVal %>" placeholder="Salary card" maxlength="30">
                
              </div>
              <div class="mb-3">
                <label class="form-label"><fmt:message key="add.card.name"/></label>
                <input type="text" class="form-control" id="m-name" name="cardHolderName" 
                value="<%= cardHolderNameErrVal %>" placeholder="AHMED MOHAMED" required>
              </div>
              <div class="mb-3">
                <label class="form-label"><fmt:message key="cards.bankName"/></label>
                <input type="text" class="form-control" id="m-bank" name="bankName"
                 value="<%= bankNameErrVal %>" placeholder="Banque Misr" required>
              </div>
              <div class="row g-3 mb-3">
                <div class="col-6">
                  <label class="form-label"><fmt:message key="add.card.exp"/></label>
                  <div class="d-flex gap-2">
                    <select class="form-select<%= !expirDateErr.isEmpty()? " is-invalid":"" %>" id="m-exp-m" name="expMonth" data-exp-m required>
                      <option value="<%= expMonthErrVal %>" selected disabled><fmt:message key="add.card.mm"/></option>
                    </select>
                    <select class="form-select<%= !expirDateErr.isEmpty()? " is-invalid":"" %>" id="m-exp-y" name="expYear" data-exp-y required>
                      <option value="<%= expYearErrVal %>" selected disabled><fmt:message key="add.card.yy"/></option>
                    </select>
                  </div>
                  <% 
	          		if(!expirDateErr.isEmpty()){
	          		%>
                  <div class="form-error show"><fmt:message key="<%= expirDateErr %>"/></div>
                   <% 
		          	}
		         %>
                </div>
                <div class="col-6">
                  <label class="form-label" for="m-cvv"><fmt:message key="add.card.cvv"/></label>
                  <div class="input-group">
                    <input type="password" class="form-control<%= !cvvErr.isEmpty()? " is-invalid":"" %>" id="m-cvv" name="cvv" 
                    value="<%= cvvErrVal %>" placeholder="•••" data-cvv maxlength="3" inputmode="numeric" required>
                    <button class="input-group-text" type="button" data-toggle-pin="m-cvv" tabindex="-1"><i class="bi bi-eye"></i></button>
                  </div>
                  <% 
	          		if(!cvvErr.isEmpty()){
	          		%>
                  <div class="form-error show"><fmt:message key="<%= cvvErr %>"/></div>
                   <% 
		          	}
		         %>
                </div>
              </div>
              <div class="modal-footer" style="border-top:1px solid var(--border)">
            	<button type="button" class="btn btn-outline-line" data-bs-dismiss="modal"><fmt:message key="common.cancel"/></button>
            	<button type="submit"  class="btn btn-primary" data-add-card><fmt:message key="common.save"/></button>
          	  </div>
            </form>
          </div>
          
        </div>
      </div>
    </div>

  <%-- Delete-card modal: shows a summary of the card before the user confirms deletion. --%>
  <div class="modal fade" id="deleteCardModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" style="border-radius:18px;border:0;box-shadow:var(--shadow)">
          <div class="modal-header" style="border-bottom:1px solid var(--border)">
            <h5 class="modal-title fw-bold"><fmt:message key="cards.delete"/></h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <%-- Confirmation posts the cardId to cardController?action=deleteCard. --%>
            <form id="card-delete-form" action="/E-Wallet/cardController?action=deleteCard" method="post">
              <input type="hidden" name="cardId" id="delCardId">
              <div class="receipt mb-3">
                <div class="receipt-row">
                  <span><fmt:message key="cards.label"/></span>
                  <strong id="delCardLabel">—</strong>
                </div>
                <div class="receipt-row">
                  <span><fmt:message key="cards.cardId"/></span>
                  <strong id="delCardNumber" dir="ltr" style="font-family:monospace;letter-spacing:1px">—</strong>
                </div>
                <div class="receipt-row">
                  <span><fmt:message key="cards.holder"/></span>
                  <strong id="delCardHolder">—</strong>
                </div>
                <div class="receipt-row">
                  <span><fmt:message key="cards.bankName"/></span>
                  <strong id="delCardBank">—</strong>
                </div>
                <div class="receipt-row">
                  <span><fmt:message key="cards.expires"/></span>
                  <strong id="delCardExpire" dir="ltr">—</strong>
                </div>
              </div>
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

<%-- Re-open the add-card modal when a previous submission contained validation errors. --%>

<%
    
    if (err!=null && !err.isEmpty()) {
%>
<script>
    new bootstrap.Modal(document.getElementById('addCardModal')).show();
</script>
<%
    }
%>
