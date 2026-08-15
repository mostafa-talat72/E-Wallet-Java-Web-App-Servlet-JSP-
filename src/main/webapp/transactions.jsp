<%@page import="java.sql.Timestamp"%>
<%@page import="java.math.BigDecimal"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.ewallet.model.Transaction" %>
<%@ page import="com.ewallet.model.Wallet" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="WEB-INF/partials/lang.jsp" %>
<c:set var="pageTitle"><fmt:message key="tx.title"/></c:set>
<c:set var="pageSubtitle"><fmt:message key="tx.subtitle"/></c:set>
<c:set var="activeMenu" value="transactions"/>
<%@ include file="WEB-INF/partials/head.jsp" %>
<%@ include file="WEB-INF/partials/navbar.jsp" %>

<%--
  TRANSACTIONS PAGE (authenticated)
  Purpose: browse the wallet's transaction history.
  Access: logged-in users only.
  Controller: loads data from transactionController?action=allTtransaction
  (redirects to it when the request attribute is missing).
  Displays: filter pills (all/withdraw/deposit/transfer), searchable table
  with in/out amounts and status badges, and client-side pagination.
--%>
<%

	List<Transaction> transactions =(List<Transaction>) request.getAttribute("transactions");
	List<Map.Entry<String, String>> toOrFromNames = (List<Map.Entry<String, String>>) request.getAttribute("toOrFromNames");
	if (transactions == null) {
		response.sendRedirect("transactionController?action=allTtransaction");
		return;
	}
	Wallet wallet = (Wallet) request.getSession().getAttribute("wallet");
%>
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

    <%-- Transaction list panel: filter pills (all/withdraw/deposit/transfer) + search field. --%>
    <div class="panel">
      <div class="panel-head flex-wrap gap-3">
        <div class="filter-pills" id="txFilterPills">
          <button type="button" class="filter-pill active" data-filter-pill="all"><fmt:message key="tx.filter.all"/></button>
          <button type="button" class="filter-pill" data-filter-pill="withdraw"><fmt:message key="tx.filter.withdraw"/></button>
          <button type="button" class="filter-pill" data-filter-pill="deposit"><fmt:message key="tx.filter.deposit"/></button>
          <button type="button" class="filter-pill" data-filter-pill="transfer"><fmt:message key="tx.filter.transfer"/></button>
        </div>
        <div class="topbar-search d-none d-md-flex" style="min-width:300px">
          <i class="bi bi-search"></i>
          <input type="search" class="form-control" placeholder="<fmt:message key="tx.search"/>" data-tx-search>
        </div>
      </div>
      <div class="table-responsive">
        <%-- Transactions table: one row per transaction, rendered by the scriptlet below. --%>
        <table class="table align-middle" data-tx-list data-page-size="8">
          <thead>
            <tr>
              <th><fmt:message key="common.type"/></th>
              <th><fmt:message key="common.from"/></th>
              <th><fmt:message key="common.to"/></th>
              <th><fmt:message key="common.ref"/></th>
              <th><fmt:message key="common.date"/></th>
              <th class="text-end"><fmt:message key="common.amount"/></th>
              <th class="text-center"><fmt:message key="common.status"/></th>
            </tr>
          </thead>
          <%-- Rows: direction icon (in/out), from/to phones, ref, date, signed amount and status badge. --%>
          <tbody>
            <%
            	if(transactions!=null){
            		for(int i = 0; i < transactions.size(); i++){
            			long transactionStatusId = transactions.get(i).getTransactionTypeId();
            			String type = transactionStatusId == 1? "deposit" : transactionStatusId == 2? "withdraw": "transfer";
            		pageContext.setAttribute("txType", type);
            			String typeDesign = transactionStatusId == 1? "bi-arrow-down-left" : transactionStatusId == 2? "bi-cash-stack": " bi-send";

            			BigDecimal amount = transactions.get(i).getAmount().add(transactions.get(i).getFees());
            			String inOrOut = "in";
            			String from = wallet.getPhoneNumber(), to = wallet.getPhoneNumber();
            			if(toOrFromNames.get(i).getValue().equals("to")){
            				inOrOut = "out";
            				to = toOrFromNames.get(i).getKey();
            			}else{
            				from = toOrFromNames.get(i).getKey();
            			}
            			String txRef = transactions.get(i).getReferenceNumber();
            			Timestamp date = transactions.get(i).getCreatedAt();
            %>
		              <tr data-tx-row data-type="<%= type %>" data-amount="<%= amount %>">
		                <td>
		                  <div class="tx-cell">
		                    <span class="tx-icon <%= inOrOut %>">
		                      <i class="bi <%= typeDesign %>"></i>
		                    </span>
		                    <strong class="small"><fmt:message key="tx.type.${txType}"/></strong>
		                  </div>
		                </td>
		                <td><span class="small" style="direction:ltr;display:inline-block"><%= from %></span></td>
		                <td><span class="small" style="direction:ltr;display:inline-block"><%= to %></span></td>
		                <td><span class="ref-code"><%= txRef %></span></td>
		                <td><span class="small" style="direction:ltr;display:inline-block"><%= date %></span></td>
		                <td class="text-end">
		                  <span class="fw-bold <%= inOrOut.equals("in")? " text-success" : " text-danger" %>">
		                    <%= inOrOut.equals("in")? "+" : "-" %><fmt:formatNumber value="<%= amount %>" pattern="#,##0.00"/>
		                  </span>
		                </td>
		                <td class="text-center">
		                    <%
		                    	if(transactions.get(i).getTransactionStatusId() == 1){
		                    %>
		                    <span class="badge badge-warning"><fmt:message key="common.pending"/></span>
		                    <%
		                    }else if(transactions.get(i).getTransactionStatusId() == 2)	{	                    
		                    %>
		                    <span class="badge badge-success"><fmt:message key="common.success"/></span>
		                    <%
		                    }else 	 {                   
		                    %>
		                    <span class="badge badge-danger"><fmt:message key="common.failed"/></span>
							<%
							}
							%>
		                </td>
		              </tr>
			<%
            		}
            	}
			%>
            <%-- Empty-state row: shown by the client-side filter/search when nothing matches. --%>
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
      <%-- Client-side pagination: 8 rows per page (data-page-size), prev/next + page numbers. --%>
      <div class="panel-foot d-flex justify-content-between align-items-center flex-wrap gap-2" data-tx-pager-wrap>
        <span class="small text-muted">
          <fmt:message key="tx.showing"/> <strong data-tx-range>0</strong> <fmt:message key="tx.of"/> <strong data-tx-total><%= transactions != null ? transactions.size() : 0 %></strong>
        </span>
        <div class="d-flex gap-2 flex-wrap justify-content-center" data-tx-pager>
          <button type="button" class="btn btn-outline-line btn-sm" data-tx-page="prev" disabled><fmt:message key="common.back"/></button>
          <div class="d-flex gap-1 flex-wrap" data-tx-pages></div>
          <button type="button" class="btn btn-outline-line btn-sm" data-tx-page="next" disabled><fmt:message key="common.next"/></button>
        </div>
      </div>
    </div>

  </div>
</main>

<%@ include file="WEB-INF/partials/footer.jsp" %>