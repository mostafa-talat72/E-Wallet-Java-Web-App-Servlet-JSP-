<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="withdraw.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="withdraw.subtitle"/></c:set>
<c:set var="activeMenu" value="withdraw"/>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>
<%
	boolean isDone = request.getAttribute("done") != null && !request.getAttribute("done").toString().isEmpty();
	boolean hasError = request.getAttribute("error") != null && !request.getAttribute("error").toString().isEmpty();
%>

<main class="main-content">
  <div class="content-wrap" style="max-width:860px">

    <c:set var="pageActions">
      <span class="badge badge-warning"><i class="bi bi-bank2"></i> OTP</span>
    </c:set>
    <%@ include file="WEB-INF/partials/page-head.jsp" %>

    <% if (!isDone && !hasError) { %>
    <div class="d-flex align-items-start gap-3 p-3 rounded-3 mb-4" style="background:var(--bg-soft)">
      <span class="flex-shrink-0 d-flex align-items-center justify-content-center rounded-circle" style="width:44px;height:44px;background:var(--primary-light);color:var(--primary)"><i class="bi bi-info-circle fs-5"></i></span>
      <div>
        <h6 class="fw-bold mb-1"><fmt:message key="withdraw.code.title"/></h6>
        <p class="text-muted small mb-0"><fmt:message key="withdraw.code.desc"/></p>
      </div>
    </div>
    <% } %>

    <% if (hasError) { %>
    <div class="panel shadow-sm mb-4">
      <div class="panel-body">
        <div class="d-flex align-items-center gap-3 p-3" style="background:rgba(239,68,68,.08);border-radius:12px">
          <span class="flex-shrink-0 d-flex align-items-center justify-content-center rounded-circle" style="width:44px;height:44px;background:rgba(239,68,68,.15);color:var(--danger)"><i class="bi bi-exclamation-circle fs-5"></i></span>
          <div>
            <h6 class="fw-bold mb-1 text-danger"><fmt:message key="withdraw.code.title"/></h6>
            <p class="text-muted small mb-0"><fmt:message key="${error}"/></p>
          </div>
        </div>
        <div class="text-center mt-3">
          <a href="${appURL}atmotp.jsp${qLang}" class="btn btn-primary"><i class="bi bi-arrow-repeat me-1"></i><fmt:message key="withdraw.generate"/></a>
        </div>
      </div>
    </div>
    <% } %>

    <% if (!isDone) { %>
    <div class="panel shadow-sm">
      <div class="panel-body">
        <form action="${appURL}transactionCodeController?action=generateCode" method="post" novalidate class="validates">

          <label class="form-label" for="otp-amount"><fmt:message key="withdraw.amount"/> (<fmt:message key="common.currency"/>)</label>
          <input type="number" class="form-control form-control-lg mb-3" id="otp-amount" name="amount"
                 value="${fn:escapeXml(param.amount)}"
                 min="1" step="0.01" placeholder="0.00" required>
          <div class="d-flex gap-2 flex-wrap mb-4" data-amount-chips="otp-amount">
            <button type="button" class="btn btn-outline-line btn-sm chip" data-value="100">100</button>
            <button type="button" class="btn btn-outline-line btn-sm chip" data-value="200">200</button>
            <button type="button" class="btn btn-outline-line btn-sm chip" data-value="500">500</button>
            <button type="button" class="btn btn-outline-line btn-sm chip" data-value="1000">1,000</button>
            <button type="button" class="btn btn-outline-line btn-sm chip" data-value="5000">5,000</button>
          </div>

          <div class="mb-4 text-center">
            <label class="form-label" for="atm-pin"><fmt:message key="withdraw.pinConfirm"/></label>
            <div class="input-group input-group-lg mx-auto" style="max-width:280px">
              <input type="password" class="form-control text-center" id="atm-pin" name="pin"
                     placeholder="••••••" value="${fn:escapeXml(param.pin)}"
                     data-pin-input minlength="6" maxlength="6" inputmode="numeric" dir="ltr" autocomplete="off" required>
              <button class="input-group-text" type="button" data-toggle-pin="atm-pin" tabindex="-1" aria-label="Show PIN">
                <i class="bi bi-eye"></i>
              </button>
            </div>
          </div>

          <div class="d-flex justify-content-end mt-4">
            <button type="submit" class="btn btn-primary btn-lg">
              <i class="bi bi-qr-code"></i> <fmt:message key="withdraw.generate"/>
            </button>
          </div>
        </form>
      </div>
    </div>
    <% } %>

    <% if (isDone) { %>
    <%
    	String otpCodeVal = request.getAttribute("transactionCodeVal") != null ? request.getAttribute("transactionCodeVal").toString() : "";
    	String encodedCode = java.net.URLEncoder.encode(otpCodeVal, "UTF-8");
    	String createdDisplay = "";
    	Object createdRaw = request.getAttribute("created_at");
    	if (createdRaw instanceof java.sql.Timestamp) {
    		java.util.Date d = new java.util.Date(((java.sql.Timestamp) createdRaw).getTime() + 3600L * 1000L);
    		createdDisplay = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
    	}
    %>
    <div class="panel shadow-sm">
      <div class="panel-body">
        <div class="success-wrap">
          <div class="success-icon" style="background:linear-gradient(135deg,#fbbf24,#d97706)"><i class="bi bi-qr-code"></i></div>
          <h3 class="fw-bold mb-2"><fmt:message key="withdraw.code.title"/></h3>
          <p class="text-muted"><fmt:message key="withdraw.code.desc"/></p>
          <div class="code-box" style="max-width:280px" id="otpCode">
            <%= request.getAttribute("transactionCodeVal") != null ? request.getAttribute("transactionCodeVal") : "" %>
          </div>
          <div class="d-flex justify-content-center gap-2 mb-3">
            <button type="button" class="btn btn-soft btn-sm" onclick="navigator.clipboard.writeText(document.getElementById('otpCode').textContent.trim()).then(function(){var b=event.target.closest('button');b.innerHTML='<i class=\'bi bi-check\'></i> Copied';setTimeout(function(){b.innerHTML='<i class=\'bi bi-clipboard\'></i> Copy';},1500)})">
              <i class="bi bi-clipboard"></i> <fmt:message key="common.copy"/>
            </button>
            <span class="timer-chip" data-countdown="600" data-countdown-url="${appURL}transactionCodeController?action=updateCodeStatus&lang=${lang}&code=<%= encodedCode %>"><i class="bi bi-hourglass-split"></i> <fmt:message key="withdraw.code.expires"/> <span data-countdown-time></span></span>
          </div>
          <p class="text-muted small"><fmt:message key="common.amount"/>: <strong><%= request.getAttribute("amountVal") != null ? request.getAttribute("amountVal") : "—" %></strong></p>
          <p class="text-muted small"><fmt:message key="common.date"/>: <span style="direction:ltr"><%= createdDisplay %></span></p>
          <div class="d-flex justify-content-center gap-2 flex-wrap mt-2">
            <a href="${appURL}atmotp.jsp${qLang}" class="btn btn-primary btn-lg">
              <i class="bi bi-qr-code"></i> <fmt:message key="withdraw.generate"/>
            </a>
            <a href="${appURL}home.jsp${qLang}" class="btn btn-outline-line btn-lg">
              <fmt:message key="withdraw.done"/>
            </a>
          </div>
        </div>
      </div>
    </div>
    <% } %>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>
