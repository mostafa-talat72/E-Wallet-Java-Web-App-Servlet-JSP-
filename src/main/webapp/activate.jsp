<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="activate.title"/></c:set>
<c:set var="bodyClass" value="auth-body"/>
<%@ include file="WEB-INF/partials/head.jsp" %>

<%--
  ACTIVATION PAGE (public)
  Purpose: let a newly registered (or login-blocked) user prove the phone
  number by entering the 6-digit code received on WhatsApp, unlocking the
  wallet (status 0 -> 1). Also offers a resend button that issues a fresh code.
  Access: public — requires only the "pendingActivationWalletId" session value.
  Controller: posts to /E-Wallet/walletController?action=activate (resend via
  action=resendActivation). Language toggle via ?lang=ar|en.
--%>

<div class="auth-wrap">
  <div class="auth-brand">
    <div class="auth-brand-top">
      <span class="brand-logo"><i class="bi bi-wallet2"></i></span>
      <span>E-<span>Wallet</span></span>
    </div>
    <div class="auth-features">
      <div class="auth-feature">
        <div class="auth-feature-icon"><i class="bi bi-shield-lock-fill"></i></div>
        <div>
          <h5><fmt:message key="auth.feature2.title"/></h5>
          <p><fmt:message key="auth.feature2.desc"/></p>
        </div>
      </div>
      <div class="auth-feature">
        <div class="auth-feature-icon"><i class="bi bi-whatsapp"></i></div>
        <div>
          <h5><fmt:message key="activate.whatsapp.title"/></h5>
          <p><fmt:message key="activate.whatsapp.desc"/></p>
        </div>
      </div>
    </div>
    <div class="auth-brand-foot">
      <i class="bi bi-shield-check"></i> E-Wallet © 2026
    </div>
  </div>

  <div class="auth-form">
    <div class="auth-form-inner">
      <div class="auth-logo-mobile">
        <span class="brand-logo"><i class="bi bi-wallet2"></i></span>
        <span>E-<span class="brand-text-gradient">Wallet</span></span>
      </div>
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="auth-title"><fmt:message key="activate.title"/></h1>
        <!-- Language toggle: reloads the current page with ?lang=ar|en. -->
        <div class="lang-switch">
          <a href="activate.jsp?lang=ar" class="${lang == 'ar' ? 'active' : ''}">عربي</a>
          <a href="activate.jsp?lang=en" class="${lang == 'en' ? 'active' : ''}">EN</a>
        </div>
      </div>
      <p class="auth-subtitle"><fmt:message key="activate.subtitle"/></p>

      <%-- Validation errors (wrong/expired/locked code) and server messages. --%>
      <%
        Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
        if (errors != null) for (Map.Entry<String, String> entry : errors.entrySet()) {
      %>
        <div class="form-alert" role="alert">
          <i class="bi bi-exclamation-circle-fill"></i>
          <span><fmt:message key="<%= entry.getValue() %>"/></span>
        </div>
      <% } %>

      <c:if test="${not empty sessionScope.activationFallbackCode}">
        <div class="form-alert" role="alert">
          <i class="bi bi-info-circle-fill"></i>
          <span><fmt:message key="activate.fallback"/> <strong dir="ltr">${sessionScope.activationFallbackCode}</strong></span>
        </div>
      </c:if>

      <c:if test="${resent}">
        <div class="form-alert" role="alert">
          <i class="bi bi-check-circle-fill"></i>
          <span><fmt:message key="activate.sent"/></span>
        </div>
      </c:if>

      <%-- Activation form: posts the 6-digit code to WalletController (action=activate). --%>
      <form action="/E-Wallet/walletController?action=activate" method="post" novalidate>
        <div class="mb-3">
          <label class="form-label" for="code"><fmt:message key="activate.code"/></label>
          <input type="tel" class="form-control form-control-lg text-center" id="code" name="code"
                 placeholder="••••••" maxlength="6" pattern="[0-9]{6}"
                 inputmode="numeric" dir="ltr" required autofocus>
        </div>
        <button type="submit" class="btn btn-primary btn-lg w-100">
          <fmt:message key="activate.submit"/> <i class="bi bi-unlock"></i>
        </button>
      </form>

      <div class="text-center mt-3">
        <a href="/E-Wallet/walletController?action=resendActivation" class="small fw-bold">
          <i class="bi bi-arrow-repeat"></i> <fmt:message key="activate.resend"/>
        </a>
      </div>

      <p class="auth-foot">
        <fmt:message key="activate.backToLogin"/> <a href="login.jsp${qLang}"><fmt:message key="auth.loginTitle"/></a>
      </p>
    </div>
  </div>
</div>

<%@ include file="WEB-INF/partials/footer.jsp" %>