<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
