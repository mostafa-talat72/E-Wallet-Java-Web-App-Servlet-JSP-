<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="error.title"/></c:set>
<c:set var="bodyClass" value="auth-body"/>
<%@ include file="WEB-INF/partials/head.jsp" %>

<div class="w-100 d-flex align-items-center justify-content-center" style="min-height:100vh">
  <div class="text-center" style="max-width:440px">
    <div class="mb-4">
      <i class="bi bi-exclamation-triangle-fill" style="font-size:4.5rem;color:#f59e0b"></i>
    </div>
    <h1 class="fw-bold mb-2" style="font-size:2.2rem"><fmt:message key="error.title"/></h1>
    <p class="text-muted mb-4"><fmt:message key="error.desc"/></p>
    <a href="home.jsp${qLang}" class="btn btn-primary btn-lg px-4">
      <i class="bi bi-house-door"></i> <fmt:message key="error.home"/>
    </a>
  </div>
</div>

<%@ include file="WEB-INF/partials/footer.jsp" %>
