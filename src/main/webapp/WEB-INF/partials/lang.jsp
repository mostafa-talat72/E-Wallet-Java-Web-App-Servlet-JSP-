<%--
  I18N PARTIAL — must be included FIRST on every page.
  - Applies no-cache response headers so logged-out content is never served stale.
  - Resolves the locale: ?lang=en|ar request param, persisted to the session,
    defaulting to Arabic (ar).
  - Sets shared variables used across all pages:
      lang   -> current locale code
      dir    -> 'ltr' | 'rtl' (drives the <html dir> attribute)
      appURL -> context path with trailing slash
      qLang  -> '?lang=<lang>' query suffix for same-page links
  - Configures the JSTL <fmt> locale and the ewallet.i18n.messages bundle.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Date" %>
<%
  response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
  response.setHeader("Pragma", "no-cache");
  response.setDateHeader("Expires", 0);
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- Resolve the language: an explicit ?lang param wins; otherwise reuse the session value (default: Arabic). --%>
<c:choose>
  <c:when test="${not empty param.lang}">
    <c:set var="lang" value="${param.lang == 'en' ? 'en' : 'ar'}" scope="session"/>
  </c:when>
  <c:otherwise>
    <c:if test="${empty sessionScope.lang}">
      <c:set var="lang" value="ar" scope="session"/>
    </c:if>
  </c:otherwise>
</c:choose>
<c:set var="lang" value="${sessionScope.lang}"/>
<c:set var="dir" value="${lang == 'en' ? 'ltr' : 'rtl'}"/>
<c:set var="appURL" value="${pageContext.request.contextPath}/"/>
<c:set var="qLang" value="?lang=${lang}"/>
<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="ewallet.i18n.messages"/>
