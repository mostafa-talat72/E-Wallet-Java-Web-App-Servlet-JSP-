<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="notif.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="notif.subtitle"/></c:set>
<c:set var="activeMenu" value="notifications"/>
<%@ include file="WEB-INF/partials/demo-data.jsp" %>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<main class="main-content">
  <div class="content-wrap" style="max-width:880px">

    <c:set var="pageActions">
      <button type="button" class="btn btn-soft" data-mark-all>
        <i class="bi bi-check2-all"></i> <fmt:message key="notif.markAll"/>
      </button>
    </c:set>
    <%@ include file="WEB-INF/partials/page-head.jsp" %>

    <div class="panel">
      <div data-notif-list>
        <c:forEach var="n" items="${notifications}">
          <div class="notif-item ${n.read ? '' : 'unread'}">
            <span class="notif-icon ${n.tone}"><i class="bi ${n.icon}"></i></span>
            <div class="notif-body">
              <div class="notif-title">
                <fmt:message key="${n.titleKey}"/>
                <c:if test="${not n.read}"><span class="badge badge-info"><fmt:message key="common.new"/></span></c:if>
              </div>
              <p class="notif-msg"><fmt:message key="${n.bodyKey}"/></p>
              <span class="notif-time"><fmt:message key="${n.timeKey}"/></span>
            </div>
            <button type="button" class="btn btn-danger-soft btn-icon-sm align-self-center" data-del-notif>
              <i class="bi bi-trash"></i>
            </button>
          </div>
        </c:forEach>
      </div>
      <div class="panel-foot text-center">
        <span class="small text-muted">
          <i class="bi bi-bell"></i> ${notifications.size()} <fmt:message key="notif.title"/>
        </span>
      </div>
    </div>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>