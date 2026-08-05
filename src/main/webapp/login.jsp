<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="auth.loginTitle"/></c:set>
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
        <h1 class="auth-title"><fmt:message key="auth.loginTitle"/></h1>
        <div class="lang-switch">
          <a href="login.jsp?lang=ar" class="${lang == 'ar' ? 'active' : ''}">عربي</a>
          <a href="login.jsp?lang=en" class="${lang == 'en' ? 'active' : ''}">EN</a>
        </div>
      </div>
      <p class="auth-subtitle"><fmt:message key="auth.loginSubtitle"/></p>

      <form action="home.jsp" method="get" novalidate>
        <div class="mb-3">
          <label class="form-label" for="phone"><fmt:message key="common.phone"/></label>
          <input type="tel" class="form-control form-control-lg" id="phone" name="phone"
                 placeholder="<fmt:message key="auth.phonePh"/>" data-phone required>
        </div>
        <div class="mb-3">
          <label class="form-label" for="pin"><fmt:message key="common.pin"/></label>
          <div class="input-group input-group-lg">
            <input type="password" class="form-control" id="pin" name="pin"
                   placeholder="••••" data-pin-input inputmode="numeric" required>
            <button class="input-group-text" type="button" data-toggle-pin="pin" tabindex="-1">
              <i class="bi bi-eye"></i>
            </button>
          </div>
        </div>
        <div class="d-flex justify-content-between align-items-center mb-4">
          <div class="form-check">
            <input class="form-check-input" type="checkbox" id="remember" checked>
            <label class="form-check-label small fw-semibold text-muted" for="remember">
              <fmt:message key="auth.remember"/>
            </label>
          </div>
          <a href="#" class="small fw-bold"><fmt:message key="auth.forgotPin"/></a>
        </div>
        <button type="submit" class="btn btn-primary btn-lg w-100">
          <fmt:message key="auth.loginBtn"/> <i class="bi bi-box-arrow-in-end"></i>
        </button>
      </form>

      <p class="auth-foot">
        <fmt:message key="auth.noAccount"/> <a href="register.jsp${qLang}"><fmt:message key="auth.registerLink"/></a>
      </p>
      <div class="auth-demo">
        <i class="bi bi-info-circle-fill"></i>
        <span><fmt:message key="auth.demoHint"/></span>
      </div>
    </div>
  </div>
</div>

<%@ include file="WEB-INF/partials/footer.jsp" %>
