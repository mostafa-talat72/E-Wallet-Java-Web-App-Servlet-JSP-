<%@page import="java.util.Map"%>
<%@page import="com.ewallet.model.Wallet"%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="profile.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="profile.subtitle"/></c:set>
<c:set var="activeMenu" value="profile"/>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<main class="main-content">
  <div class="content-wrap" style="max-width:960px">

    <%@ include file="WEB-INF/partials/page-head.jsp" %>

    <div class="profile-card mb-4">
      <div class="d-flex align-items-center gap-4 flex-wrap">
        <span class="profile-avatar">${sessionScope.wallet.fullName.charAt(0)}</span>
        <div class="flex-grow-1">
          <h4 class="fw-bold mb-1">${sessionScope.wallet.fullName}</h4>
          <div class="d-flex align-items-center gap-2 flex-wrap">
            <span class="badge badge-success"><i class="bi bi-patch-check-fill"></i> <fmt:message key="common.active"/></span>
            <span class="small text-muted"><fmt:message key="profile.memberSince"/>: <b style="direction:ltr">${sessionScope.wallet.createdAt}</b></span>
          </div>
        </div>
        <a href="${appURL}cards.jsp${qLang}" class="btn btn-outline-line"><i class="bi bi-credit-card-2-front"></i> <fmt:message key="cards.title"/></a>
      </div>
    </div>
	<%
		Map<String, String> err = (Map<String, String>) request.getAttribute("errors");
		String fullNameErr = "";
		String updateInfoErr = "";
		
		String curPinErr ="";
		String newPinErr = "";
		String newPinConfirmErr = "";
		String updatePinErr = "";
		
		String deletedError = "";
		
		String fullNameErrVal = request.getAttribute("fullNameErrVal") == null? 
				((Wallet)request.getSession().getAttribute("wallet") == null? "" : 
					((Wallet)request.getSession().getAttribute("wallet")).getFullName()) : (String) request.getAttribute("fullNameErrVal");
		
		String curPinVal = request.getAttribute("curPinVal") == null? "" :(String) request.getAttribute("curPinVal");
		String newPinErrVal = request.getAttribute("newPinErrVal") == null? "" :(String) request.getAttribute("newPinErrVal");
		String newPinConfirmErrVal = request.getAttribute("newPinConfirmErrVal") == null? "" :(String) request.getAttribute("newPinConfirmErrVal");
		
		String delPhoneNumberErrVal = request.getAttribute("delPhoneNumberErrVall") == null? "" :(String) request.getAttribute("delPhoneNumberErrVall");
		String delPinErrVal = request.getAttribute("delPinErrVal") == null? "" :(String) request.getAttribute("delPinErrVal");

		if(err != null) {
		    for(Map.Entry<String, String> entry : err.entrySet()) {
		        String key = entry.getKey();
		        String value = entry.getValue();
		        
		        if("fullName".equals(key)) {
		            fullNameErr = value;
		        }else if("updateInfoErr".equals(key)) {
		        	updateInfoErr = value;
		        }else if("curPinErr".equals(key)) {
		        	curPinErr = value;
		        }else if("pin".equals(key)) {
		            newPinErr = value;
		        }else if("pinConfirm".equals(key)) {
		            newPinConfirmErr = value;
		        }else if("updatePinErr".equals(key)) {
		        	updatePinErr = value;
		        }else if("deletedError".equals(key)) {
		        	deletedError = value;
		        }
		    }
		}
		
	%>
    <div class="row g-4">
      <div class="col-12 col-lg-6">
        <div class="panel">
          <div class="panel-head">
            <h5 class="panel-title"><i class="bi bi-person"></i> <fmt:message key="profile.personal"/></h5>
          </div>
          <div class="panel-body">
            <form action="/E-Wallet/walletController?action=updateUserWallet" method="post" class="validates" id="profile-form" novalidate>
             <% 
          	if(!updateInfoErr.isEmpty()){
	         %>
		       <div class="form-alert" style="text-align:start" role="alert">
		       <i class="bi bi-exclamation-circle-fill"></i>
		       <span><fmt:message key="<%= updateInfoErr %>"/></span>
		       </div>
	        <% 
	          	}
	         %>
              <div class="mb-3">
                <label class="form-label" for="p-name"><fmt:message key="cards.name"/></label>
                <input type="text" class="form-control<%= !fullNameErr.isEmpty()? " is-invalid":"" %>" id="p-name" name="fullName"
                       value="<%= fullNameErrVal %>" required>
               <% 
	          	if(!fullNameErr.isEmpty()){
	          %>
	          	
	          		<div class="form-error show"><fmt:message key="<%= fullNameErr %>"/></div>
	           <% 
	          	}
	          %>
              </div>
              <div class="mb-3">
                <label class="form-label" for="p-phone"><fmt:message key="profile.phone"/></label>
                <input type="tel" class="form-control" id="p-phone" name="phone" value="${sessionScope.wallet.phoneNumber}" data-phone 
                style="background:var(--surface-2);cursor:not-allowed" readonly>
              </div>
              <div class="mb-3">
                <label class="form-label" for="p-national"><fmt:message key="profile.national"/></label>
                <input type="text" class="form-control" id="p-national" name="nationalId" value="${sessionScope.wallet.nationalId}" data-phone data-max="14" readonly
                       style="background:var(--surface-2);cursor:not-allowed">
              </div>
              <button type="submit" class="btn btn-primary" data-save-form="profile-form">
                <i class="bi bi-check2-circle"></i> <fmt:message key="common.save"/>
              </button>
            </form>
          </div>
        </div>
      </div>

      <div class="col-12 col-lg-6">
        <div class="panel mb-4">
          <div class="panel-head">
            <h5 class="panel-title"><i class="bi bi-key"></i> <fmt:message key="profile.changePin"/></h5>
          </div>
          <div class="panel-body">
		<form  action="/E-Wallet/walletController?action=updateUserWalletPin" method="post" class="validates" id="pin-form" novalidate>
              <% 
          	if(!updatePinErr.isEmpty()){
	         %>
		       <div class="form-alert" style="text-align:start" role="alert">
		       <i class="bi bi-exclamation-circle-fill"></i>
		       <span><fmt:message key="<%= updatePinErr %>"/></span>
		       </div>
	        <% 
	          	}
	         %>
              <div class="mb-3">
                <label class="form-label" for="cur-pin"><fmt:message key="profile.curPin"/></label>
                <div class="input-group">
                  <input type="password" class="form-control<%= !curPinErr.isEmpty()? " is-invalid":"" %>" id="cur-pin"
                   value="<%= curPinVal %>" name="curPin" placeholder="••••••" data-pin-input inputmode="numeric" dir="ltr" required>
                  <button class="input-group-text" type="button" data-toggle-pin="cur-pin" tabindex="-1"><i class="bi bi-eye"></i></button>
                </div>
			 <% 
	          	if(!curPinErr.isEmpty()){
	          %>
	          	
	          		<div class="form-error show"><fmt:message key="<%= curPinErr %>"/></div>
	           <% 
	          	}
	          %>              
              </div>
              <div class="mb-3">
                <label class="form-label" for="new-pin"><fmt:message key="profile.newPin"/></label>
                <div class="input-group">
                  <input type="password" class="form-control<%= !newPinErr.isEmpty()? " is-invalid":"" %>" id="new-pin"
                  value="<%= newPinErrVal %>" name="newPin" placeholder="••••••" data-pin-input data-pin-meter inputmode="numeric" dir="ltr" required>
                  <button class="input-group-text" type="button" data-toggle-pin="new-pin" tabindex="-1"><i class="bi bi-eye"></i></button>
                </div>
                <div class="pin-strong" data-pin-meter-bars>
                  <span></span><span></span><span></span>
                </div>
                <small class="strength-label" data-pin-meter-label></small>
                <small class="text-muted d-block mt-1"><fmt:message key="profile.pinHint"/></small>
                <% 
	          	if(!newPinErr.isEmpty()){
	          %>
	          	
	          		<div class="form-error show"><fmt:message key="<%= newPinErr %>"/></div>
	           <% 
	          	}
	          %>  
              </div>
              <div class="mb-4">
                <label class="form-label" for="new-pin2"><fmt:message key="profile.confirmNewPin"/></label>
                <div class="input-group">
                  <input type="password" class="form-control<%= !newPinConfirmErr.isEmpty()? " is-invalid":"" %>" id="new-pin2" 
                  value="<%= newPinConfirmErrVal %>" name="newPin2" placeholder="••••••" data-pin-input inputmode="numeric" dir="ltr" required>
                  <button class="input-group-text" type="button" data-toggle-pin="new-pin2" tabindex="-1"><i class="bi bi-eye"></i></button>
                </div>
                <% 
	          	if(!newPinConfirmErr.isEmpty()){
	          %>
	          	
	          		<div class="form-error show"><fmt:message key="<%= newPinConfirmErr %>"/></div>
	           <% 
	          	}
	          %>  
              </div>
              <button type="submit" class="btn btn-primary" data-save-form="pin-form">
                <i class="bi bi-shield-lock"></i> <fmt:message key="profile.changePin"/>
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <div class="panel" style="border:1px solid rgba(var(--danger-rgb,220,53,69),.3)">
      <div class="panel-head">
        <h5 class="panel-title" style="color:var(--danger)"><i class="bi bi-trash"></i> <fmt:message key="profile.deleteZone"/></h5>
      </div>
      <div class="panel-body d-flex align-items-center justify-content-between flex-wrap gap-3">
        <span class="small text-muted flex-grow-1" style="max-width:520px"><fmt:message key="profile.deleteDesc"/></span>
        <button type="button" class="btn btn-danger-soft" data-bs-toggle="modal" data-bs-target="#deleteProfileModal">
          <i class="bi bi-trash"></i> <fmt:message key="profile.deleteAccount"/>
        </button>
      </div>
    </div>

    <div class="modal fade" id="deleteProfileModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered" style="max-width:360px">
        <div class="modal-content" style="border-radius:18px;border:0;box-shadow:var(--shadow)">
          <div class="modal-header" style="border-bottom:1px solid var(--border)">
            <h5 class="modal-title fw-bold" style="color:var(--danger)"><i class="bi bi-trash"></i> <fmt:message key="profile.deleteZone"/></h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body text-center">
            <div class="success-icon" style="width:56px;height:56px;font-size:1.5rem;margin:0 auto .75rem">
              <i class="bi bi-exclamation-triangle" style="color:var(--danger)"></i>
            </div>
            <p class="text-muted small mb-3"><fmt:message key="profile.deleteConfirm"/></p>
		<% 
          if(!deletedError.isEmpty()){
         %>
	       <div class="form-alert" style="text-align:start" role="alert">
	       <i class="bi bi-exclamation-circle-fill"></i>
	       <span><fmt:message key="<%= deletedError %>"/></span>
	       </div>
        	<% 
          	}
         	%>
         	<form id="delete-form" action="/E-Wallet/walletController?action=deleteUserWallet" method="post" autocomplete="off">
              <label class="form-label d-block"><fmt:message key="profile.phone"/></label>
              <input type="tel" class="form-control text-center mb-3<%= !deletedError.isEmpty()? " is-invalid":"" %>" id="delPhone" name="phone"
               value="<%= delPhoneNumberErrVal %>" placeholder="01XXXXXXXXX" data-phone inputmode="numeric" dir="ltr" required>
              <label class="form-label d-block"><fmt:message key="profile.deletePinLabel"/></label>
              <div class="input-group mx-auto" style="max-width:190px">
                <input type="password" class="form-control text-center<%= !deletedError.isEmpty()? " is-invalid":"" %>" id="delPin" name="pin"
                value="<%= delPinErrVal %>" placeholder="••••••" data-pin-input inputmode="numeric" dir="ltr" required>
                <button class="input-group-text" type="button" data-toggle-pin="delPin" tabindex="-1"><i class="bi bi-eye"></i></button>
              </div>
            </form>
          </div>
          <div class="modal-footer" style="border-top:1px solid var(--border)">
            <button type="button" class="btn btn-outline-line" data-bs-dismiss="modal"><fmt:message key="common.cancel"/></button>
            <button type="submit" form="delete-form" class="btn btn-danger-soft" id="confirmDelete" style="color:#fff;background:var(--danger)">
              <i class="bi bi-trash"></i> <fmt:message key="profile.deleteAccount"/>
            </button>
          </div>
        </div>
      </div>
    </div>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>

<%
    
    if (!deletedError.isEmpty()) {
%>
<script>
    new bootstrap.Modal(document.getElementById('deleteProfileModal')).show();
</script>
<%
    }
%>