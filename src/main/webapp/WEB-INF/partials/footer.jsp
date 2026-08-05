<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${needChart}">
  <script src="${appURL}assets/vendor/chartjs/chart.umd.min.js"></script>
</c:if>
<script src="${appURL}assets/js/main.js"></script>
</body>
</html>
