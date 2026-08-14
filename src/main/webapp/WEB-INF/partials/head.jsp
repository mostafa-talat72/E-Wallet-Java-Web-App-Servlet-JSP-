<%--
  HEAD PARTIAL
  Renders the document <head> for all main pages:
  - <title> from ${pageTitle} + app name, plus favicon
  - Bootstrap RTL/LTR stylesheet chosen by ${dir}
  - Bootstrap Icons and the app's custom style.css
  - window.APPMSG: localized message keys consumed by main.js
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="${lang}" dir="${dir}">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${pageTitle} | <fmt:message key="app.name"/></title>
<link rel="icon" type="image/svg+xml" href="${appURL}assets/img/favicon.svg">
<c:choose>
  <c:when test="${dir == 'rtl'}">
    <link rel="stylesheet" href="${appURL}assets/vendor/bootstrap/css/bootstrap.rtl.min.css">
  </c:when>
  <c:otherwise>
    <link rel="stylesheet" href="${appURL}assets/vendor/bootstrap/css/bootstrap.min.css">
  </c:otherwise>
</c:choose>
<link rel="stylesheet" href="${appURL}assets/vendor/icons/bootstrap-icons.min.css">
<link rel="stylesheet" href="${appURL}assets/css/style.css">
<%-- Localized client-side messages: main.js reads these keys for toasts, confirmations and badges. --%>
<script>
window.APPMSG = {
  marked: '<fmt:message key="js.marked"/>',
  removed: '<fmt:message key="js.removed"/>',
  saved: '<fmt:message key="js.saved"/>',
  pinChanged: '<fmt:message key="js.pinChanged"/>',
  copied: '<fmt:message key="js.copied"/>',
  invalid: '<fmt:message key="js.invalid"/>',
  cardAdded: '<fmt:message key="js.cardAdded"/>',
  cardRemoved: '<fmt:message key="js.cardRemoved"/>',
  wrongPin: '<fmt:message key="js.wrongPin"/>',
  weak: '<fmt:message key="profile.weak"/>',
  medium: '<fmt:message key="profile.medium"/>',
  strong: '<fmt:message key="profile.strong"/>',
  active: '<fmt:message key="common.active"/>',
  inactive: '<fmt:message key="common.inactive"/>',
  cardsHolder: '<fmt:message key="cards.holder"/>',
  cardsExpires: '<fmt:message key="cards.expires"/>',
  cardDeleteConfirm: '<fmt:message key="cards.deleteConfirm"/>',
  selectCard: '<fmt:message key="js.selectCard"/>'
};
</script>
</head>
<body class="${empty bodyClass ? '' : bodyClass}">
