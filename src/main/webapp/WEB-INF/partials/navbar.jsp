<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageName" value="${fn:replace(fn:substringAfter(pageContext.request.servletPath, '/'), '.jsp', '')}"/>
<c:if test="${empty pageName}"><c:set var="pageName" value="home"/></c:if>
<aside class="sidebar" id="sidebar">
  <div class="sidebar-brand">
    <a href="${appURL}home.jsp${qLang}" class="brand-link">
      <span class="brand-logo"><i class="bi bi-wallet2"></i></span>
      <span class="brand-name">E-<span class="brand-accent">Wallet</span></span>
    </a>
  </div>
  <nav class="sidebar-nav">
    <div class="nav-section-label"><fmt:message key="nav.main"/></div>
    <a href="${appURL}home.jsp${qLang}" class="nav-link ${activeMenu == 'home' ? 'active' : ''}">
      <i class="bi bi-grid-1x2-fill"></i><span><fmt:message key="nav.dashboard"/></span>
    </a>
    <a href="${appURL}send-money.jsp${qLang}" class="nav-link ${activeMenu == 'send' ? 'active' : ''}">
      <i class="bi bi-send-fill"></i><span><fmt:message key="nav.send"/></span>
    </a>
    <a href="/E-Wallet/cardController?action=getAllCards&redirect=add-money" class="nav-link ${activeMenu == 'add' ? 'active' : ''}">
      <i class="bi bi-plus-circle-fill"></i><span><fmt:message key="nav.add"/></span>
    </a>
    <a href="${appURL}atmotp.jsp${qLang}" class="nav-link ${activeMenu == 'withdraw' ? 'active' : ''}">
      <i class="bi bi-cash-stack"></i><span><fmt:message key="nav.withdraw"/></span>
    </a>
    <div class="nav-section-label"><fmt:message key="nav.account"/></div>
    <a href="/E-Wallet/cardController?action=getAllCards&redirect=cards" class="nav-link ${activeMenu == 'cards' ? 'active' : ''}">
      <i class="bi bi-credit-card-2-front-fill"></i><span><fmt:message key="nav.cards"/></span>
    </a>
    <a href="${appURL}transactions.jsp${qLang}" class="nav-link ${activeMenu == 'transactions' ? 'active' : ''}">
      <i class="bi bi-arrow-left-right"></i><span><fmt:message key="nav.transactions"/></span>
    </a>
    <a href="${appURL}notifications.jsp${qLang}" class="nav-link ${activeMenu == 'notifications' ? 'active' : ''}">
      <i class="bi bi-bell-fill"></i><span><fmt:message key="nav.notifications"/></span>
      <span class="nav-badge">3</span>
    </a>
  </nav>
  <div class="sidebar-footer">
    <a href="${appURL}profile.jsp${qLang}" class="nav-link ${activeMenu == 'profile' ? 'active' : ''}">
      <i class="bi bi-person-fill"></i><span><fmt:message key="nav.profile"/></span>
    </a>
    <a href="${appURL}login.jsp" class="nav-link nav-logout">
      <i class="bi bi-box-arrow-right"></i><span><fmt:message key="nav.logout"/></span>
    </a>
  </div>
</aside>
<div class="sidebar-overlay" id="sidebarOverlay"></div>

<header class="topbar">
  <div class="topbar-start">
    <button type="button" class="btn-icon topbar-toggle" id="sidebarToggle" aria-label="Toggle menu">
      <i class="bi bi-list"></i>
    </button>
    <div class="topbar-search d-none d-md-flex">
      <i class="bi bi-search"></i>
      <input type="search" class="form-control" placeholder="<fmt:message key="common.search"/>">
    </div>
  </div>
  <div class="topbar-end">
    <div class="lang-switch">
      <a href="${appURL}${pageName}.jsp?lang=ar" class="${lang == 'ar' ? 'active' : ''}">عربي</a>
      <a href="${appURL}${pageName}.jsp?lang=en" class="${lang == 'en' ? 'active' : ''}">EN</a>
    </div>
    <a href="${appURL}notifications.jsp${qLang}" class="btn-icon topbar-notif">
      <i class="bi bi-bell"></i>
      <span class="notif-dot"></span>
    </a>
    <div class="dropdown">
      <button class="user-chip dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
        <span class="user-avatar">${sessionScope.wallet.fullName.charAt(0)}</span>
        <span class="user-meta d-none d-md-block">
          <strong>${sessionScope.wallet.fullName }</strong>
          <small>${sessionScope.wallet.phoneNumber}</small>
        </span>
      </button>
      <ul class="dropdown-menu dropdown-menu-end">
        <li><a class="dropdown-item" href="${appURL}profile.jsp${qLang}"><i class="bi bi-person me-2"></i><fmt:message key="nav.profile"/></a></li>
        <li><hr class="dropdown-divider"></li>
        <li><a class="dropdown-item text-danger" href="${appURL}login.jsp"><i class="bi bi-box-arrow-right me-2"></i><fmt:message key="nav.logout"/></a></li>
      </ul>
    </div>
  </div>
</header>
