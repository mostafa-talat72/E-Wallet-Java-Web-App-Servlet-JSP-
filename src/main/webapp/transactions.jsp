<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="tx.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="tx.subtitle"/></c:set>
<c:set var="activeMenu" value="transactions"/>
<%@ include file="WEB-INF/partials/demo-data.jsp" %>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<main class="main-content">
  <div class="content-wrap">

    <c:set var="pageActions">
      <div class="topbar-search d-md-none w-100">
        <i class="bi bi-search"></i>
        <input type="search" class="form-control" placeholder="<fmt:message key="tx.search"/>" data-tx-search>
      </div>
      <a href="${appURL}send-money.jsp${qLang}" class="btn btn-primary"><i class="bi bi-send"></i> <fmt:message key="send.title"/></a>
    </c:set>
    <%@ include file="WEB-INF/partials/page-head.jsp" %>

    <div class="panel">
      <div class="panel-head flex-wrap gap-3">
        <div class="filter-pills" id="txFilterPills">
          <button type="button" class="filter-pill active" data-filter-pill="all"><fmt:message key="tx.filter.all"/></button>
          <button type="button" class="filter-pill" data-filter-pill="income"><fmt:message key="tx.filter.income"/></button>
          <button type="button" class="filter-pill" data-filter-pill="expense"><fmt:message key="tx.filter.expense"/></button>
          <button type="button" class="filter-pill" data-filter-pill="transfer"><fmt:message key="tx.filter.transfer"/></button>
        </div>
        <div class="topbar-search d-none d-md-flex" style="min-width:300px">
          <i class="bi bi-search"></i>
          <input type="search" class="form-control" placeholder="<fmt:message key="tx.search"/>" data-tx-search>
        </div>
      </div>
      <div class="table-responsive">
        <table class="table align-middle" data-tx-list>
          <thead>
            <tr>
              <th><fmt:message key="common.type"/></th>
              <th><fmt:message key="send.preview.to"/></th>
              <th><fmt:message key="common.ref"/></th>
              <th><fmt:message key="common.date"/></th>
              <th class="text-end"><fmt:message key="common.amount"/></th>
              <th class="text-center"><fmt:message key="common.status"/></th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="tx" items="${transactions}">
              <tr data-tx-row data-type="${tx.type}">
                <td>
                  <div class="tx-cell">
                    <span class="tx-icon ${tx.amount > 0 ? 'in' : tx.status == 'failed' ? 'failed' : tx.status == 'pending' ? 'pending' : 'out'}">
                      <i class="bi ${tx.amount > 0 ? 'bi-arrow-down-left' : tx.type == 'transfer' ? 'bi-send' : tx.type == 'withdraw' ? 'bi-cash-stack' : 'bi-arrow-up-right'}"></i>
                    </span>
                    <strong class="small"><fmt:message key="tx.type.${tx.type}"/></strong>
                  </div>
                </td>
                <td><span class="small" style="direction:ltr;display:inline-block">${tx.other}</span></td>
                <td><span class="ref-code">${tx.ref}</span></td>
                <td><span class="small" style="direction:ltr;display:inline-block">${tx.date}</span></td>
                <td class="text-end">
                  <span class="fw-bold ${tx.amount > 0 ? 'text-success' : 'text-danger'}">
                    ${tx.amount > 0 ? '+' : '−'}<fmt:formatNumber value="${tx.amount > 0 ? tx.amount : -tx.amount}" pattern="#,##0.00"/>
                  </span>
                </td>
                <td class="text-center">
                  <c:choose>
                    <c:when test="${tx.status == 'success'}"><span class="badge badge-success"><fmt:message key="common.success"/></span></c:when>
                    <c:when test="${tx.status == 'pending'}"><span class="badge badge-warning"><fmt:message key="common.pending"/></span></c:when>
                    <c:otherwise><span class="badge badge-danger"><fmt:message key="common.failed"/></span></c:otherwise>
                  </c:choose>
                </td>
              </tr>
            </c:forEach>
            <tr class="empty-no-tx" style="display:none">
              <td colspan="6">
                <div class="empty-state">
                  <i class="bi bi-inbox"></i>
                  <fmt:message key="tx.noResult"/>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="panel-foot d-flex justify-content-between align-items-center flex-wrap gap-2">
        <span class="small text-muted">
          <fmt:message key="tx.showing"/> <strong>1</strong>–<strong>8</strong> ${transactions.size()} | <strong>2026-08-04</strong>
        </span>
        <div class="d-flex gap-2">
          <button type="button" class="btn btn-outline-line btn-sm disabled"><fmt:message key="common.back"/></button>
          <button type="button" class="btn btn-outline-line btn-sm disabled">1</button>
          <button type="button" class="btn btn-outline-line btn-sm"><fmt:message key="common.next"/></button>
        </div>
      </div>
    </div>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>