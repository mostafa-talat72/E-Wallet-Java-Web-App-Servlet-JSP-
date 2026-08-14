<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="auth.registerTitle"/></c:set>
<c:set var="bodyClass" value="auth-body"/>
<%@ include file="WEB-INF/partials/head.jsp" %>

<%--
  REGISTER PAGE (public)
  Purpose: create a new wallet account with full name, phone number,
  national ID, PIN and PIN confirmation.
  Access: public — no session required.
  Controller: posts to /E-Wallet/walletController?action=signup.
  Displays: brand/feature panel, registration form, per-field validation
  errors, PIN strength meter and language toggle (?lang=ar|en).
--%>

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
        <!-- Language toggle: reloads the current page with ?lang=ar|en. -->
        <div class="lang-switch">
          <a href="register.jsp?lang=ar" class="${lang == 'ar' ? 'active' : ''}">عربي</a>
          <a href="register.jsp?lang=en" class="${lang == 'en' ? 'active' : ''}">EN</a>
        </div>
      </div>
      <p class="auth-subtitle"><fmt:message key="auth.registerSubtitle"/></p>
	<%-- Collect server-side validation errors and map them to per-field variables that prefill the form below. --%>
	<%
		Map<String, String> err = (Map<String, String>) request.getAttribute("errors");
		String fullNameErr = "";
		String phoneNumberErr = "";
		String nationalIdErr = "";
		String pinErr = "";
		String pinConfirmErr = "";
		String siginUpErr = "";
		
		String fullNameErrVal = request.getAttribute("fullNameErrVal") == null? "" : (String) request.getAttribute("fullNameErrVal");
		String phoneNumberErrVal = request.getAttribute("phoneNumberErrVal") == null? "" :(String) request.getAttribute("phoneNumberErrVal");
		String nationalIdErrVal = request.getAttribute("nationalIdErrVal") == null? "" :(String) request.getAttribute("nationalIdErrVal");
		String pinErrVal = request.getAttribute("pinErrVal") == null? "" :(String) request.getAttribute("pinErrVal");
		String pinConfirmErrVal = request.getAttribute("pinConfirmErrVal") == null? "" :(String) request.getAttribute("pinConfirmErrVal");
		
	
		
		if(err != null) {
		    for(Map.Entry<String, String> entry : err.entrySet()) {
		        String key = entry.getKey();
		        String value = entry.getValue();
		        
		        if("fullName".equals(key)) {
		            fullNameErr = value;
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
      <%-- Registration form: full name, phone, national ID, PIN + confirmation, terms checkbox. --%>
      <form action="/E-Wallet/walletController?action=signup" method="post" novalidate>
        <% 
          	if(!siginUpErr.isEmpty()){
         %>
	       <div class="form-alert" style="text-align:start" role="alert">
	       <i class="bi bi-exclamation-circle-fill"></i>
	       <span><fmt:message key="<%= siginUpErr %>"/></span>
	       </div>
        <% 
          	}
         %>
        <div class="mb-3">
          <label class="form-label" for="r-name"><fmt:message key="auth.fullName"/></label>
          <input type="text" class="form-control form-control-lg<%= !fullNameErr.isEmpty()? " is-invalid":"" %>" id="r-name" name="fullName"
                 value="<%= fullNameErrVal %>" placeholder="Ahmed Mohamed" maxlength="100" required>
          <% 
          	if(!fullNameErr.isEmpty()){
          %>
          	
          		<div class="form-error show"><fmt:message key="<%= fullNameErr %>"/></div>
           <% 
          	}
          %>
        </div>
        <div class="mb-3">
          <label class="form-label" for="r-phone"><fmt:message key="common.phone"/></label>
          <input type="tel" class="form-control form-control-lg<%= !phoneNumberErr.isEmpty()? " is-invalid":"" %>" id="r-phone" name="phone"
                 value="<%= phoneNumberErrVal %>" placeholder="<fmt:message key="auth.phonePh"/>" data-phone required>
          <% 
          	if(!phoneNumberErr.isEmpty()){
          %>
          	
          		<div class="form-error show"><fmt:message key="<%= phoneNumberErr %>"/></div>
           <% 
          	}
          %>
       
        </div>
        <div class="mb-3">
          <label class="form-label" for="national-id"><fmt:message key="auth.nationalId"/></label>
          <input type="text" class="form-control form-control-lg<%= !nationalIdErr.isEmpty()? " is-invalid":"" %>" id="national-id" name="nationalId"
                 value="<%= nationalIdErrVal %>" placeholder="29901010123456" data-phone data-max="14" required>
		  <% 
          	if(!nationalIdErr.isEmpty()){
          %>
          	
          		<div class="form-error show"><fmt:message key="<%= nationalIdErr %>"/></div>
           <% 
          	}
          %>       
        </div>
        <div class="row g-3 mb-3">
          <div class="col-md-6">
            <label class="form-label" for="r-pin"><fmt:message key="common.pin"/></label>
            <div class="input-group">
              <input type="password" class="form-control<%= !pinErr.isEmpty()? " is-invalid":"" %>" id="r-pin" name="pin"
                   value="<%= pinErrVal %>"  placeholder="••••••" data-pin-input data-pin-meter inputmode="numeric" dir="ltr" required>
              <button class="input-group-text" type="button" data-toggle-pin="r-pin" tabindex="-1">
                <i class="bi bi-eye"></i>
              </button>
            </div>
            <div class="pin-strong" data-pin-meter-bars>
              <span></span><span></span><span></span>
            </div>
            <small class="strength-label" data-pin-meter-label></small>
           <% 
          	if(!pinErr.isEmpty()){
          %>
          <div class="form-error" style="display:block"><fmt:message key="<%= pinErr %>"/></div>
           <% 
          	}
          %>  
          </div>
          <div class="col-md-6">
            <label class="form-label" for="r-pin2"><fmt:message key="auth.confirmPin"/></label>
            <div class="input-group">
              <input type="password" class="form-control<%= !pinConfirmErr.isEmpty()? " is-invalid":"" %>" id="r-pin2" name="pinConfirm"
                      value="<%= pinConfirmErrVal %>" placeholder="••••••" data-pin-input inputmode="numeric" dir="ltr" required>
              <button class="input-group-text" type="button" data-toggle-pin="r-pin2" tabindex="-1">
                <i class="bi bi-eye"></i>
              </button>
            </div>
            <% 
          	if(!pinConfirmErr.isEmpty()){
          %>
          <div class="form-error" style="display:block"><fmt:message key="<%= pinConfirmErr %>"/></div>
           <% 
          	}
          %>
          </div>
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
