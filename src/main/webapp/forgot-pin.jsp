<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="forgot.title"/></c:set>
<c:set var="bodyClass" value="auth-body"/>
<%@ include file="WEB-INF/partials/head.jsp" %>

<%--
  FORGOT PIN PAGE (public) — step 1
  Purpose: let a user who forgot their PIN prove the phone number. The user
  enters the registered phone number and a 6-digit reset code is sent on
  WhatsApp (with an on-screen fallback when the sidecar is down).
  Access: public — no session required.
  Controller: posts to /E-Wallet/walletController?action=forgotPin (step 2 is
  forgot-pin-code.jsp, action=resetPin). Language toggle via ?lang=ar|en.
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
        <h1 class="auth-title"><fmt:message key="forgot.title"/></h1>
        <!-- Language toggle: reloads the current page with ?lang=ar|en. -->
        <div class="lang-switch">
          <a href="forgot-pin.jsp?lang=ar" class="${lang == 'ar' ? 'active' : ''}">عربي</a>
          <a href="forgot-pin.jsp?lang=en" class="${lang == 'en' ? 'active' : ''}">EN</a>
        </div>
      </div>
      <p class="auth-subtitle"><fmt:message key="forgot.subtitle"/></p>

      <%-- Server-side validation errors. --%>
      <%
        Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
        String phoneNumberErr = "";
        if (errors != null) for (Map.Entry<String, String> entry : errors.entrySet()) {
          String errorMessage = entry.getValue();
      %>
        <div class="form-alert" role="alert">
          <i class="bi bi-exclamation-circle-fill"></i>
          <span><fmt:message key="<%= errorMessage %>"/></span>
        </div>
      <%
          if ("phoneNumber".equals(entry.getKey())) {
            phoneNumberErr = (String) request.getAttribute("phoneNumberErr");
          }
        }
      %>

      <%-- Request form: posts the phone number to WalletController (action=forgotPin). --%>
      <form action="/E-Wallet/walletController?action=forgotPin" method="post" novalidate>
        <div class="mb-3">
          <label class="form-label" for="phone"><fmt:message key="common.phone"/></label>
          <input type="tel" class="form-control form-control-lg<%= !phoneNumberErr.isEmpty() ? " is-invalid" : "" %>"
                 id="phone" name="phone" value="<%= phoneNumberErr %>"
                 placeholder="<fmt:message key="forgot.phonePh"/>" data-phone required autofocus>
        </div>
        <p class="text-muted small mb-4"><i class="bi bi-info-circle"></i> <fmt:message key="forgot.hint"/></p>
        <button type="submit" class="btn btn-primary btn-lg w-100">
          <fmt:message key="forgot.submit"/> <i class="bi bi-send"></i>
        </button>
      </form>

      <p class="auth-foot">
        <fmt:message key="forgot.backToLogin"/> <a href="login.jsp${qLang}"><fmt:message key="forgot.loginLink"/></a>
      </p>
    </div>
  </div>
</div>

<%@ include file="WEB-INF/partials/footer.jsp" %>
