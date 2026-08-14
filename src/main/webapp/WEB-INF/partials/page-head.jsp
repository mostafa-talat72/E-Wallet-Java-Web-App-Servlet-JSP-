<%--
  PAGE-HEAD PARTIAL
  Renders the standard page header used by inner pages: the localized
  ${pageTitle} and ${pageSubtitle} on the left, and the caller-supplied
  ${pageActions} snippet on the right. Skipped entirely when pageTitle is
  empty (auth pages use the auth-body layout instead).
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${not empty pageTitle}">
  <div class="page-head">
    <div>
      <h1 class="page-title">${pageTitle}</h1>
      <p class="page-subtitle">${pageSubtitle}</p>
    </div>
    <div class="page-actions">
      ${pageActions}
    </div>
  </div>
</c:if>
