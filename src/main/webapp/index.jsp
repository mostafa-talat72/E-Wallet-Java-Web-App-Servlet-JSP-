<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  INDEX PAGE (public entry point)
  Purpose: root URL entry — contains no UI of its own; immediately redirects
  the visitor to the login page.
--%>
<c:redirect url="login.jsp"/>
