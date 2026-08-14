<%--
  FOOTER PARTIAL
  Ends every main page: optionally loads Chart.js when ${needChart} is set,
  then the Bootstrap JS bundle and the app's main.js (initializes steppers,
  toasts, PIN meters, card pickers, etc.), then closes <body> and <html>.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${needChart}">
  <!-- Chart.js: only included on pages that set ${needChart}. -->
  <script src="${appURL}assets/vendor/chartjs/chart.umd.min.js"></script>
</c:if>
<script src="${appURL}assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${appURL}assets/js/main.js?v=20260812c"></script>
</body>
</html>
