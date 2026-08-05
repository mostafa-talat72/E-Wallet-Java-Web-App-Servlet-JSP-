<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="auth.registerTitle"/></c:set>
<c:set var="bodyClass" value="auth-body"/>
<%@ include file="WEB-INF/partials/head.jsp" %>

<div class="auth-wrap">
  <div class="auth-brand">
    <div class="auth-brand-top">
      <span class="brand-logo"><i class="bi bi-wallet2"></i></span>
      <span>E-<span>Wallet</span></span>
    </div>
    <div class="auth-features">
      <div class="auth-feature">
        <div class="auth-feature-icon"><i class="bi bi-lightning-charge-fill"></i></div>
        <div>
          <h5><fmt:message key="auth.feature1.title"/></h5>
          <p><fmt:message key="auth.feature1.desc"/></p>
        </div>
      </div>
      <div class="auth-feature">
        <div class="auth-feature-icon"><i class="bi bi-shield-lock-fill"></i></div>
        <div>
          <h5><fmt:message key="auth.feature2.title"/></h5>
          <p><fmt:message key="auth.feature2.desc"/></p>
        </div>
      </div>
      <div class="auth-feature">
        <div class="auth-feature-icon"><i class="bi bi-sliders2"></i></div>
        <div>
          <h5><fmt:message key="auth.feature3.title"/></h5>
          <p><fmt:message key="auth.feature3.desc"/></p>
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
        <h1 class="auth-title"><fmt:message key="auth.registerTitle"/></h1>
        <div class="lang-switch">
          <a href="register.jsp?lang=ar" class="${lang == 'ar' ? 'active' : ''}">عربي</a>
          <a href="register.jsp?lang=en" class="${lang == 'en' ? 'active' : ''}">EN</a>
        </div>
      </div>
      <p class="auth-subtitle"><fmt:message key="auth.registerSubtitle"/></p>

      <form action="login.jsp" method="get" novalidate>
        <div class="mb-3">
          <label class="form-label" for="r-name"><fmt:message key="auth.fullName"/></label>
          <input type="text" class="form-control form-control-lg" id="r-name" name="fullName"
                 placeholder="Ahmed Mohamed" maxlength="100" required>
        </div>
        <div class="mb-3">
          <label class="form-label" for="r-phone"><fmt:message key="common.phone"/></label>
          <input type="tel" class="form-control form-control-lg" id="r-phone" name="phone"
                 placeholder="<fmt:message key="auth.phonePh"/>" data-phone required>
        </div>
        <div class="mb-3">
          <label class="form-label" for="national-id"><fmt:message key="auth.nationalId"/></label>
          <input type="text" class="form-control form-control-lg" id="national-id" name="nationalId"
                 placeholder="29901010123456" data-phone data-max="14" required>
        </div>
        <div class="row g-3 mb-3">
          <div class="col-md-6">
            <label class="form-label" for="r-pin"><fmt:message key="common.pin"/></label>
            <div class="input-group">
              <input type="password" class="form-control" id="r-pin" name="pin"
                     placeholder="••••••" data-pin-input data-pin-meter inputmode="numeric" dir="ltr" required>
              <button class="input-group-text" type="button" data-toggle-pin="r-pin" tabindex="-1">
                <i class="bi bi-eye"></i>
              </button>
            </div>
            <div class="pin-strong" data-pin-meter-bars>
              <span></span><span></span><span></span>
            </div>
            <small class="strength-label" data-pin-meter-label></small>
          </div>
          <div class="col-md-6">
            <label class="form-label" for="r-pin2"><fmt:message key="auth.confirmPin"/></label>
            <div class="input-group">
              <input type="password" class="form-control" id="r-pin2" name="pinConfirm"
                     placeholder="••••••" data-pin-input inputmode="numeric" dir="ltr" required>
              <button class="input-group-text" type="button" data-toggle-pin="r-pin2" tabindex="-1">
                <i class="bi bi-eye"></i>
              </button>
            </div>
          </div>
        </div>
        <div class="form-check mb-4">
          <input class="form-check-input" type="checkbox" id="terms" required>
          <label class="form-check-label small fw-semibold text-muted" for="terms">
            <fmt:message key="auth.agree"/> <a href="#" class="fw-bold"><fmt:message key="auth.terms"/></a>
          </label>
        </div>
        <button type="submit" class="btn btn-primary btn-lg w-100">
          <fmt:message key="auth.registerBtn"/> <i class="bi bi-person-plus"></i>
        </button>
      </form>

      <p class="auth-foot">
        <fmt:message key="auth.haveAccount"/> <a href="login.jsp${qLang}"><fmt:message key="auth.loginLink"/></a>
      </p>
    </div>
  </div>
</div>

<%@ include file="WEB-INF/partials/footer.jsp" %>
