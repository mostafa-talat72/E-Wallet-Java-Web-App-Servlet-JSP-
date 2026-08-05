<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="profile.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="profile.subtitle"/></c:set>
<c:set var="activeMenu" value="profile"/>
<%@ include file="WEB-INF/partials/demo-data.jsp" %>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<main class="main-content">
  <div class="content-wrap" style="max-width:960px">

    <%@ include file="WEB-INF/partials/page-head.jsp" %>

    <div class="profile-card mb-4">
      <div class="d-flex align-items-center gap-4 flex-wrap">
        <span class="profile-avatar">AM</span>
        <div class="flex-grow-1">
          <h4 class="fw-bold mb-1"><fmt:message key="demo.userName"/></h4>
          <div class="d-flex align-items-center gap-2 flex-wrap">
            <span class="badge badge-success"><i class="bi bi-patch-check-fill"></i> <fmt:message key="common.active"/></span>
            <span class="small text-muted"><fmt:message key="profile.memberSince"/>: <b style="direction:ltr">2024-03-15</b></span>
          </div>
        </div>
        <a href="${appURL}cards.jsp${qLang}" class="btn btn-outline-line"><i class="bi bi-credit-card-2-front"></i> <fmt:message key="cards.title"/></a>
      </div>
    </div>

    <div class="row g-4">
      <div class="col-12 col-lg-6">
        <div class="panel">
          <div class="panel-head">
            <h5 class="panel-title"><i class="bi bi-person"></i> <fmt:message key="profile.personal"/></h5>
          </div>
          <div class="panel-body">
            <form class="validates" id="profile-form" novalidate>
              <div class="mb-3">
                <label class="form-label" for="p-name"><fmt:message key="cards.name"/></label>
                <input type="text" class="form-control" id="p-name" name="name" value="<fmt:message key="demo.userName"/>" required>
              </div>
              <div class="mb-3">
                <label class="form-label" for="p-phone"><fmt:message key="profile.phone"/></label>
                <input type="tel" class="form-control" id="p-phone" name="phone" value="${wallet.phone}" data-phone required>
              </div>
              <div class="mb-3">
                <label class="form-label" for="p-national"><fmt:message key="profile.national"/></label>
                <input type="text" class="form-control" id="p-national" name="nationalId" value="${wallet.nationalId}" data-phone data-max="14" readonly
                       style="background:var(--surface-2);cursor:not-allowed">
              </div>
              <button type="button" class="btn btn-primary" data-save-form="profile-form">
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
            <form class="validates" id="pin-form" novalidate>
              <div class="mb-3">
                <label class="form-label" for="cur-pin"><fmt:message key="profile.curPin"/></label>
                <div class="input-group">
                  <input type="password" class="form-control" id="cur-pin" name="curPin" placeholder="••••" data-pin-input inputmode="numeric" required>
                  <button class="input-group-text" type="button" data-toggle-pin="cur-pin" tabindex="-1"><i class="bi bi-eye"></i></button>
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label" for="new-pin"><fmt:message key="profile.newPin"/></label>
                <div class="input-group">
                  <input type="password" class="form-control" id="new-pin" name="newPin" placeholder="••••" data-pin-input data-pin-meter inputmode="numeric" required>
                  <button class="input-group-text" type="button" data-toggle-pin="new-pin" tabindex="-1"><i class="bi bi-eye"></i></button>
                </div>
                <div class="pin-strong" data-pin-meter-bars>
                  <span></span><span></span><span></span>
                </div>
                <small class="strength-label" data-pin-meter-label></small>
                <small class="text-muted d-block mt-1"><fmt:message key="profile.pinHint"/></small>
              </div>
              <div class="mb-4">
                <label class="form-label" for="new-pin2"><fmt:message key="profile.confirmNewPin"/></label>
                <div class="input-group">
                  <input type="password" class="form-control" id="new-pin2" name="newPin2" placeholder="••••" data-pin-input inputmode="numeric" required>
                  <button class="input-group-text" type="button" data-toggle-pin="new-pin2" tabindex="-1"><i class="bi bi-eye"></i></button>
                </div>
              </div>
              <button type="button" class="btn btn-primary" data-save-form="pin-form">
                <i class="bi bi-shield-lock"></i> <fmt:message key="profile.changePin"/>
              </button>
            </form>
          </div>
        </div>

        <div class="panel">
          <div class="panel-head">
            <h5 class="panel-title"><i class="bi bi-shield-check"></i> <fmt:message key="profile.security"/></h5>
          </div>
          <div class="panel-body d-flex flex-column gap-3">
            <div class="setting-row">
              <span class="setting-icon" style="background:var(--success-bg);color:var(--success)"><i class="bi bi-shield-lock-fill"></i></span>
              <div class="flex-grow-1">
                <strong class="d-block small">OTP — <fmt:message key="profile.twoFactor"/></strong>
                <span class="small text-muted"><fmt:message key="profile.securityDesc"/></span>
              </div>
              <span class="badge badge-success"><i class="bi bi-check-circle"></i> <fmt:message key="common.active"/></span>
            </div>
            <div class="setting-row">
              <span class="setting-icon" style="background:var(--info-bg);color:var(--info)"><i class="bi bi-wifi"></i></span>
              <div class="flex-grow-1">
                <strong class="d-block small"><fmt:message key="common.phone"/></strong>
                <span class="small text-muted" style="direction:ltr;display:inline-block">${wallet.phone}</span>
              </div>
              <span class="badge badge-success"><i class="bi bi-check-circle"></i> <fmt:message key="common.verified"/></span>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>