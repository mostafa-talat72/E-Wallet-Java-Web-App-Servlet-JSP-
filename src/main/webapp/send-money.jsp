<%@page import="java.math.BigDecimal"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.math.BigDecimal" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="send.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="send.subtitle"/></c:set>
<c:set var="activeMenu" value="send"/>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<%--
  SEND MONEY PAGE (authenticated)
  Purpose: transfer money from the wallet to another phone number.
  Access: logged-in users only (available balance from sessionScope.walletBalance).
  Controller: posts to /E-Wallet/transactionController?action=transfer.
  Displays: 2-step wizard (recipient + amount, review + PIN), live fee
  calculation and a success/error receipt after submission.
--%>

<%-- Collect server-side validation errors (recipient, amount, PIN) into variables that prefill the form. --%>
<%
		Map<String, String> err = (Map<String, String>) request.getAttribute("errors");
		String recipientPhoneErr = "";
		String pinErr = "";
		String amountErr = "";

		
		String recipientPhoneVal = request.getAttribute("recipientPhoneVal") == null? "" :(String) request.getAttribute("recipientPhoneVal");
		String pinVal = request.getAttribute("pinVal") == null? "" :(String) request.getAttribute("pinVal");
		BigDecimal amountVal = request.getAttribute("amountVal") == null? new  BigDecimal(0) :(BigDecimal) request.getAttribute("amountVal");
		String noteVal = request.getAttribute("noteVal") == null? "" :(String) request.getAttribute("noteVal");

	
		
		if(err != null) {
		    for(Map.Entry<String, String> entry : err.entrySet()) {
		        String key = entry.getKey();
		        String value = entry.getValue();
		        
		        if("phoneNumber".equals(key)) {
		        	recipientPhoneErr = value;
		        } else if("pin".equals(key)) {
		            pinErr = value;
		        }else if("amount".equals(key)) {
		        	amountErr = value;
		        }
		    }
		}
	%>
<main class="main-content">
  <div class="content-wrap" style="max-width:860px">

    <c:set var="pageActions">
      <span class="badge badge-info"><i class="bi bi-lightning-charge-fill"></i> <fmt:message key="tx.type.transfer"/></span>
    </c:set>
    <%@ include file="WEB-INF/partials/page-head.jsp" %>

    <%-- Transfer form: wraps the 2-step wizard and posts to transactionController?action=transfer. --%>
    <form id="sendMoneyForm" class="validates" action="${appURL}transactionController?action=transfer" method="post" novalidate>
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
      </div>

      <%-- Step 1: recipient phone, amount (with quick chips) and optional note; shows the available balance. --%>
        <div class="panel step-panel">
        <div class="panel-body">
            <div class="mb-4">
              <label class="form-label" for="recipient"><fmt:message key="send.recipient"/></label>
              <input type="tel" class="form-control form-control-lg<%= !recipientPhoneErr.isEmpty()? " is-invalid":"" %>" id="recipient" name="recipient"
                     value="<%= recipientPhoneVal %>"
                     placeholder="<fmt:message key="send.recipientPh"/>" data-phone required>
                <% 
	          	if(!recipientPhoneErr.isEmpty()){
	          %>
              <div class="form-error show"><fmt:message key="<%= recipientPhoneErr %>"/></div>
              <% 
	          	}
	          %>
            </div>
            <div class="mb-4">
              <label class="form-label" for="amount"><fmt:message key="common.amount"/> (<fmt:message key="common.currency"/>)</label>
              <input type="number" class="form-control form-control-lg<%= !amountErr.isEmpty()? " is-invalid":"" %>" id="amount" name="amount"
                     value="<%= amountVal %>"
                     min="1" step="0.01" placeholder="0.00" required>
               <% 
	          	if(!amountErr.isEmpty()){
	          %>
             <div class="form-error show"><fmt:message key="<%= amountErr %>"/></div>
              <% 
	          	}
	          %>
              <div class="mt-2 d-flex gap-2 flex-wrap" data-amount-chips="amount">
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="50">50</button>
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="100">100</button>
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="500">500</button>
                <button type="button" class="btn btn-outline-line btn-sm chip" data-value="1000">1,000</button>
              </div>
            </div>
            <div class="mb-4">
              <label class="form-label" for="note"><fmt:message key="send.note"/></label>
              <input type="text" class="form-control" id="note" name="note"
                     value="<%= noteVal %>"
                     placeholder="<fmt:message key="send.notePh"/>" maxlength="200">
            </div>
            <div class="d-flex justify-content-between align-items-center">
              <span class="small text-muted fw-semibold">
                <fmt:message key="common.available"/>:
                <strong class="text-success"><fmt:formatNumber value="${sessionScope.walletBalance.availableBalance}" pattern="#,##0.00"/> <fmt:message key="common.currency"/></strong>
              </span>
              <button type="button" class="btn btn-primary btn-lg" data-next>
                <fmt:message key="common.continue"/> <i class="bi bi-arrow-left"></i>
              </button>
            </div>
        </div>
      </div>

      <%-- Step 2: receipt preview with live fee calculation, then PIN confirmation. --%>
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
              <!-- Fee: 0.1% of the amount, computed live by main.js; the total is written into #preview-total. -->
              <strong data-fee-calc data-fee-source="#amount" data-fee-rate="0.001" data-fee-cur="<fmt:message key="common.currency"/>" data-fee-target="#preview-total">0.00 <fmt:message key="common.currency"/></strong>
            </div>
            <div class="receipt-row">
              <span><fmt:message key="send.total"/></span>
              <strong class="text-success" id="preview-total">—</strong>
            </div>
            <div class="receipt-row">
              <span><fmt:message key="send.note"/></span>
              <strong id="preview-note" data-fill="#note">—</strong>
            </div>
          </div>
          <div class="mb-4 mt-4 text-center">
            <label class="form-label" for="pin"><fmt:message key="common.pin"/></label>
            <p class="text-muted mb-3" style="font-size:.9rem"><fmt:message key="send.otp.desc"/></p>
            <div class="input-group input-group-lg mx-auto" style="max-width:280px">
              <input type="password" class="form-control text-center" id="pin" name="pin"
                     placeholder="••••••" value="${fn:escapeXml(param.pin)}"
                     data-pin-input inputmode="numeric" dir="ltr" autocomplete="off" required>
              <button class="input-group-text" type="button" data-toggle-pin="pin" tabindex="-1">
                <i class="bi bi-eye"></i>
              </button>
            </div>
          </div>
          <div class="d-flex justify-content-between gap-3">
            <button type="button" class="btn btn-outline-line btn-lg" data-prev>
              <i class="bi bi-arrow-right"></i> <fmt:message key="common.back"/>
            </button>
            <button type="submit" class="btn btn-primary btn-lg" data-finish>
              <fmt:message key="common.confirm"/> <i class="bi bi-shield-check"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
    </form>

    <%-- Result panel (hidden by default): success or error summary with the full transfer receipt. --%>
    <div class="panel shadow-sm d-none" data-done>
      <div class="panel-body">
        <div class="success-wrap">
          <c:choose>
            <c:when test="${not empty error}">
              <div class="success-icon" style="background:var(--danger)"><i class="bi bi-x-lg"></i></div>
              <h2 class="fw-bold mb-2"><fmt:message key="send.fail.title"/></h2>
              <p class="text-muted"><fmt:message key="send.fail.desc"/></p>
              <div class="form-alert" style="max-width:440px;margin:0 auto 1rem;text-align:start" role="alert">
                <i class="bi bi-exclamation-circle-fill"></i>
                <span><fmt:message key="${error}"/></span>
              </div>
            </c:when>
            <c:otherwise>
              <div class="success-icon"><i class="bi bi-check-lg"></i></div>
              <h2 class="fw-bold mb-2"><fmt:message key="send.success.title"/></h2>
              <p class="text-muted"><fmt:message key="send.success.desc"/></p>
              <c:if test="${not empty success}">
                <div class="form-alert" style="max-width:440px;margin:0 auto 1rem;text-align:start" role="alert">
                  <i class="bi bi-check-circle-fill"></i>
                  <span><fmt:message key="send.success.done"/></span>
                </div>
              </c:if>
            </c:otherwise>
          </c:choose>
          <div class="receipt" style="max-width:none">
            <div class="receipt-row"><span><fmt:message key="send.preview.to"/></span><strong style="direction:ltr" data-fill="#recipient">—</strong></div>
            <div class="receipt-row"><span><fmt:message key="common.amount"/></span><strong data-fill="#amount"><%= request.getAttribute("amountVal") != null? (BigDecimal)request.getAttribute("amountVal") : new BigDecimal(0) %></strong></div>
            <div class="receipt-row"><span><fmt:message key="send.fees"/></span><strong><%= request.getAttribute("feesVal") != null? (BigDecimal)request.getAttribute("feesVal") : new BigDecimal(0) %> <fmt:message key="common.currency"/></strong></div>
            <div class="receipt-row"><span><fmt:message key="send.total"/></span><strong class="text-success"><%= request.getAttribute("feesVal") != null && request.getAttribute("amountVal") != null? ((BigDecimal)request.getAttribute("feesVal")).add((BigDecimal)request.getAttribute("amountVal")) : new BigDecimal(0) %> <fmt:message key="common.currency"/></strong></div>
            <div class="receipt-row"><span><fmt:message key="send.note"/></span><strong><%= request.getAttribute("noteVal") != null? request.getAttribute("noteVal") : "—" %></strong></div>
            <div class="receipt-row"><span><fmt:message key="common.ref"/></span><strong class="ref-code">
            <%= request.getAttribute("txRef") != null? request.getAttribute("txRef") : request.getAttribute("transactionReference") != null? request.getAttribute("transactionReference") : "—" %></strong></div>
            <div class="receipt-row"><span><fmt:message key="common.date"/></span><strong style="direction:ltr"><%= request.getAttribute("txDate") != null? request.getAttribute("txDate") : request.getAttribute("created_at") != null? request.getAttribute("created_at") : "—" %></strong></div>
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

<%
	String doneFlag = (String) request.getAttribute("done");
	String errFlag = (String) request.getAttribute("error");
	if ((doneFlag != null && !doneFlag.isEmpty()) || (errFlag != null && !errFlag.isEmpty())) {
%>
<script>
  document.getElementById("sendMoneyForm").classList.add("d-none");
  var donePanel = document.querySelector("[data-done]");
  if (donePanel) donePanel.classList.remove("d-none");
  window.scrollTo({ top: 0, behavior: "smooth" });
</script>
<%
	}
%>

<%@ include file="WEB-INF/partials/footer.jsp" %>

<%-- On done/error flags, hide the wizard and reveal the result panel on load. --%>