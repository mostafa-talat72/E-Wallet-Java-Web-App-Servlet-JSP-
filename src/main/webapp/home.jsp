<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="nav.dashboard"/></c:set>
<c:set var="activeMenu" value="home"/>
<c:set var="needChart" value="true"/>
<%@ include file="WEB-INF/partials/demo-data.jsp" %>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<main class="main-content">
  <div class="content-wrap">

    <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
      <div>
        <h1 class="page-title">
          <fmt:message key="dash.greeting"/>, <span class="brand-text-gradient">Ahmed</span>
        </h1>
        <p class="page-subtitle"><fmt:message key="dash.subtitle"/></p>
      </div>
      <div class="d-flex gap-2">
        <a href="${appURL}send-money.jsp${qLang}" class="btn btn-soft">
          <i class="bi bi-send"></i> <fmt:message key="dash.quick.send"/>
        </a>
        <a href="${appURL}add-money.jsp${qLang}" class="btn btn-primary">
          <i class="bi bi-plus-lg"></i> <fmt:message key="dash.quick.add"/>
        </a>
      </div>
    </div>

    <div class="row g-4">
      <div class="col-12 col-xl-7">
        <div class="balance-hero">
          <div class="hero-body">
            <div class="hero-label">
              <i class="bi bi-wallet2"></i> <fmt:message key="common.totalBalance"/>
            </div>
            <div class="balance-amount">
              <span>
                <span data-balance-value data-full="<fmt:formatNumber value='${wallet.available + wallet.held}' pattern='#,##0.00'/>">
                  <fmt:formatNumber value="${wallet.available + wallet.held}" pattern="#,##0.00"/>
                </span>
                <small class="fs-6 fw-bold opacity-75"> <fmt:message key="common.currency"/></small>
              </span>
              <button type="button" class="hide-btn" data-balance-toggle aria-label="toggle">
                <i class="bi bi-eye-slash"></i>
              </button>
            </div>
            <div class="hero-cols">
              <div class="hero-stat">
                <small><fmt:message key="common.available"/></small>
                <strong><fmt:formatNumber value="${wallet.available}" pattern="#,##0.00"/> <fmt:message key="common.currency"/></strong>
              </div>
              <div class="hero-stat">
                <small><fmt:message key="common.held"/></small>
                <strong><fmt:formatNumber value="${wallet.held}" pattern="#,##0.00"/> <fmt:message key="common.currency"/></strong>
              </div>
              <div class="hero-stat">
                <small><fmt:message key="common.phone"/></small>
                <strong style="direction:ltr;font-family:monospace;letter-spacing:1px">${wallet.phone}</strong>
              </div>
            </div>
            <div class="hero-actions">
              <a href="${appURL}send-money.jsp${qLang}" class="btn btn-light-soft">
                <i class="bi bi-send"></i> <fmt:message key="dash.quick.send"/>
              </a>
              <a href="${appURL}add-money.jsp${qLang}" class="btn btn-light-soft">
                <i class="bi bi-plus-lg"></i> <fmt:message key="dash.quick.add"/>
              </a>
              <a href="${appURL}atmotp.jsp${qLang}" class="btn btn-light-soft">
                <i class="bi bi-cash-stack"></i> <fmt:message key="dash.quick.withdraw"/>
              </a>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12 col-xl-5">
        <div class="row g-4 h-100">
          <div class="col-6">
            <div class="stat-card h-100">
              <div class="stat-icon income"><i class="bi bi-arrow-down-left"></i></div>
              <div class="stat-info">
                <div class="stat-label"><fmt:message key="dash.income"/></div>
                <div class="stat-value">4,600.00</div>
                <span class="stat-trend trend-up"><i class="bi bi-graph-up-arrow"></i> +12%</span>
              </div>
            </div>
          </div>
          <div class="col-6">
            <div class="stat-card h-100">
              <div class="stat-icon expense"><i class="bi bi-arrow-up-right"></i></div>
              <div class="stat-info">
                <div class="stat-label"><fmt:message key="dash.expense"/></div>
                <div class="stat-value">1,295.50</div>
                <span class="stat-trend trend-down"><i class="bi bi-graph-down-arrow"></i> -3%</span>
              </div>
            </div>
          </div>
          <div class="col-12">
            <div class="panel h-100">
              <div class="panel-head">
                <h5 class="panel-title"><i class="bi bi-graph-up-arrow"></i> <fmt:message key="dash.chart"/></h5>
              </div>
              <div class="panel-body">
                <div class="chart-wrap">
                  <canvas id="cashflowChart"
                          data-income="<fmt:message key="dash.income"/>"
                          data-expense="<fmt:message key="dash.expense"/>"
                          data-d1="07/28" data-d2="07/29" data-d3="07/30" data-d4="07/31"
                          data-d5="08/01" data-d6="08/02" data-d7="08/03"></canvas>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12">
        <div class="panel">
          <div class="panel-head">
            <h5 class="panel-title"><i class="bi bi-lightning-charge-fill"></i> <fmt:message key="dash.quick"/></h5>
          </div>
          <div class="panel-body">
            <div class="quick-grid">
              <a href="${appURL}send-money.jsp${qLang}" class="quick-item">
                <span class="quick-icon tone-blue"><i class="bi bi-send"></i></span>
                <fmt:message key="dash.quick.send"/>
              </a>
              <a href="${appURL}add-money.jsp${qLang}" class="quick-item">
                <span class="quick-icon tone-emerald"><i class="bi bi-plus-circle"></i></span>
                <fmt:message key="dash.quick.add"/>
              </a>
              <a href="${appURL}atmotp.jsp${qLang}" class="quick-item">
                <span class="quick-icon tone-amber"><i class="bi bi-cash-stack"></i></span>
                <fmt:message key="dash.quick.withdraw"/>
              </a>
              <a href="${appURL}cards.jsp${qLang}" class="quick-item">
                <span class="quick-icon tone-violet"><i class="bi bi-credit-card-2-front"></i></span>
                <fmt:message key="dash.quick.cards"/>
              </a>
              <a href="${appURL}transactions.jsp${qLang}" class="quick-item">
                <span class="quick-icon tone-cyan"><i class="bi bi-arrow-left-right"></i></span>
                <fmt:message key="dash.quick.transactions"/>
              </a>
              <a href="${appURL}notifications.jsp${qLang}" class="quick-item">
                <span class="quick-icon tone-rose"><i class="bi bi-bell"></i></span>
                <fmt:message key="dash.quick.notifications"/>
              </a>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12 col-xl-7">
        <div class="panel h-100">
          <div class="panel-head">
            <h5 class="panel-title"><i class="bi bi-clock-history"></i> <fmt:message key="dash.lastTx"/></h5>
            <a href="${appURL}transactions.jsp${qLang}" class="btn btn-outline-line btn-sm">
              <fmt:message key="common.viewAll"/>
            </a>
          </div>
          <div class="panel-body p-0">
            <c:forEach var="tx" items="${transactions}" varStatus="st" begin="0" end="4">
              <div class="tx-item">
                <span class="tx-icon ${tx.amount > 0 ? 'in' : tx.status == 'failed' ? 'failed' : tx.status == 'pending' ? 'pending' : 'out'}">
                  <i class="bi ${tx.amount > 0 ? 'bi-arrow-down-left' : tx.type == 'transfer' ? 'bi-send' : tx.type == 'withdraw' ? 'bi-cash-stack' : 'bi-arrow-up-right'}"></i>
                </span>
                <div class="tx-body">
                  <div class="tx-title">
                    <fmt:message key="tx.type.${tx.type}"/>
                    <c:choose>
                      <c:when test="${tx.status == 'success'}"><span class="badge badge-success"><fmt:message key="common.success"/></span></c:when>
                      <c:when test="${tx.status == 'pending'}"><span class="badge badge-warning"><fmt:message key="common.pending"/></span></c:when>
                      <c:otherwise><span class="badge badge-danger"><fmt:message key="common.failed"/></span></c:otherwise>
                    </c:choose>
                  </div>
                  <span class="tx-sub">${tx.other}</span>
                </div>
                <div class="text-end">
                  <div class="tx-amount ${tx.amount > 0 ? 'in' : 'out'}">
                    ${tx.amount > 0 ? '+' : '−'} <fmt:formatNumber value="${tx.amount > 0 ? tx.amount : -tx.amount}" pattern="#,##0.00"/>
                  </div>
                  <span class="tx-date">${tx.date}</span>
                </div>
              </div>
            </c:forEach>
          </div>
        </div>
      </div>

      <div class="col-12 col-xl-5">
        <div class="panel h-100">
          <div class="panel-head">
            <h5 class="panel-title"><i class="bi bi-bell"></i> <fmt:message key="dash.lastNotif"/></h5>
            <a href="${appURL}notifications.jsp${qLang}" class="btn btn-outline-line btn-sm">
              <fmt:message key="common.viewAll"/>
            </a>
          </div>
          <div class="panel-body p-0">
            <c:forEach var="n" items="${notifications}" begin="0" end="2">
              <div class="notif-item ${n.read ? '' : 'unread'}">
                <span class="notif-icon ${n.tone}"><i class="bi ${n.icon}"></i></span>
                <div class="notif-body">
                  <div class="notif-title"><fmt:message key="${n.titleKey}"/></div>
                  <p class="notif-msg"><fmt:message key="${n.bodyKey}"/></p>
                  <span class="notif-time"><fmt:message key="${n.timeKey}"/></span>
                </div>
              </div>
            </c:forEach>
          </div>
        </div>
      </div>

    </div>
  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>
