<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.ewallet.model.ATM" %>
<%@ page import="com.ewallet.util.DateUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="../WEB-INF/partials/lang.jsp" %>
<!DOCTYPE html>
<html lang="${lang}" dir="${dir}">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="E-Wallet ATM Map">
  <title><fmt:message key="atm.map.title"/></title>
  <link rel="icon" href="../assets/img/favicon.svg" type="image/svg+xml">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Cairo:wght@400;600;700;800;900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="atm.css">
  <style>
    body { display:block; padding:32px 16px; }
    .wrap { max-width:1000px; margin:0 auto; }
    .map-head { display:flex; align-items:center; justify-content:space-between; gap:12px; margin-bottom:18px; flex-wrap:wrap; }
    .map-title { font-size:1.4rem; font-weight:800; display:flex; align-items:center; gap:10px; }
    .map-title .dot { width:10px; height:10px; border-radius:50%; background:var(--phos); box-shadow:0 0 10px var(--phos); animation:pulse 2s infinite; }
    .lang-toggle {
      background:var(--machine-2); color:var(--ink); border:1px solid var(--machine-edge);
      border-radius:999px; padding:8px 18px; font-weight:700; font-family:inherit; cursor:pointer; font-size:.85rem;
      text-decoration:none;
    }
    .lang-toggle:hover { border-color:var(--phos-dim); }

    .map-canvas {
      position:relative; height:480px; border-radius:20px; overflow:hidden;
      background:linear-gradient(180deg,#16223a,#0d1524 60%,#0a101c);
      border:1px solid var(--machine-edge);
      box-shadow:0 20px 50px -20px rgba(0,0,0,.7), inset 0 1px 0 rgba(255,255,255,.05);
    }

    .atm-marker {
      position:absolute; transform:translate(-50%,-50%); background:none; border:0; padding:0;
      display:flex; flex-direction:column; align-items:center; gap:3px; cursor:pointer; z-index:3; text-decoration:none;
    }
    .marker-pin {
      width:38px; height:38px; border-radius:50% 50% 50% 4px; transform:rotate(-45deg);
      background:linear-gradient(135deg,#38bdf8,#2563eb);
      display:flex; align-items:center; justify-content:center; box-shadow:0 6px 16px rgba(37,99,235,.55);
      border:2px solid #fff; transition:transform .15s ease;
    }
    .marker-pin .ico { transform:rotate(45deg); color:#fff; font-size:1.05rem; font-weight:900; }
    .atm-marker:hover .marker-pin { transform:rotate(-45deg) scale(1.14); }
    .marker-label {
      background:#fff; color:#0f172a; border-radius:999px; padding:2px 10px;
      font-size:.72rem; font-weight:800; white-space:nowrap; max-width:170px;
      overflow:hidden; text-overflow:ellipsis; box-shadow:0 2px 8px rgba(0,0,0,.4);
    }
    .atm-marker::after {
      content:""; position:absolute; left:50%; top:9px; width:32px; height:32px; border-radius:50%;
      transform:translateX(-50%); background:rgba(56,189,248,.3); z-index:-1;
      animation:markerPulse 1.8s ease-out infinite;
    }
    @keyframes markerPulse { 0% { transform:translateX(-50%) scale(.5); opacity:1; } 100% { transform:translateX(-50%) scale(1.7); opacity:0; } }
    @media (prefers-reduced-motion: reduce) { .atm-marker::after { animation:none; } }

    .map-legend {
      position:absolute; left:12px; bottom:12px; background:rgba(10,16,28,.85); border:1px solid var(--machine-edge);
      border-radius:12px; padding:8px 14px; font-size:.75rem; display:flex; gap:16px; align-items:center; z-index:2;
    }
    .map-legend .lg { display:flex; align-items:center; gap:6px; color:var(--mute); }
    .map-legend .dot { width:9px; height:9px; border-radius:50%; background:var(--phos); box-shadow:0 0 6px var(--phos); }

    .atm-list { display:grid; grid-template-columns:repeat(auto-fill,minmax(260px,1fr)); gap:12px; margin-top:18px; }
    .atm-card {
      background:linear-gradient(160deg,var(--machine-1),var(--machine-2));
      border:1px solid var(--machine-edge); border-radius:16px; padding:14px;
      display:flex; align-items:center; gap:12px; cursor:pointer; transition:border-color .15s ease, transform .15s ease;
      text-align:start; font-family:inherit; color:var(--ink); text-decoration:none;
    }
    .atm-card:hover { border-color:var(--phos-dim); transform:translateY(-2px); }
    .atm-card .aico {
      flex-shrink:0; width:40px; height:40px; border-radius:12px;
      background:rgba(56,189,248,.12); border:1px solid rgba(56,189,248,.25);
      display:flex; align-items:center; justify-content:center; font-size:1.1rem; font-weight:900; color:#38bdf8;
    }
    .atm-card .t { min-width:0; }
    .atm-card .t strong { display:block; font-size:.9rem; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
    .atm-card .t small { color:var(--mute); font-size:.75rem; display:block; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
    .atm-card .st { margin-inline-start:auto; flex-shrink:0; width:9px; height:9px; border-radius:50%; background:var(--phos); box-shadow:0 0 8px var(--phos); }

    .status-line { text-align:center; color:var(--mute); font-size:.85rem; margin-top:16px; }
    .hint { text-align:center; color:var(--mute); font-size:.85rem; margin:14px 0 0; }
  </style>
</head>
<body>
<%
	List<ATM> atms = (ArrayList<ATM>) request.getAttribute("atms");
	
	if (atms == null) {
		response.sendRedirect("/E-Wallet/atmController?action=getAllATMs");
		return;
	}
%>
  <div class="wrap">
    <div class="map-head">
      <div class="map-title"><span class="dot"></span><fmt:message key="atm.map.title"/></div>
      <a class="lang-toggle" href="${appURL}atm/atm-map.jsp?lang=${lang == 'en' ? 'ar' : 'en'}">${lang == 'ar' ? 'English' : 'العربية'}</a>
    </div>

    <c:choose>
      <c:when test="${empty atms}">
        <p class="hint"><fmt:message key="atm.map.empty"/></p>
      </c:when>
      <c:otherwise>

        <div class="map-canvas">
          <c:forEach var="atm" items="${atms}">
            <a class="atm-marker"
               href="${appURL}atmController${qLang}&action=getATMById&atmId=${atm.atmId}"
               target="_blank"
               title="${fn:escapeXml(atm.atmName)}"
               style="left:${atm.mapX}%;top:${atm.mapY}%">
              <span class="marker-pin"><span class="ico">&#9673;</span></span>
              <span class="marker-label">${fn:escapeXml(atm.atmName)}</span>
            </a>
          </c:forEach>
          <div class="map-legend">
            <span class="lg"><span class="dot"></span><fmt:message key="atm.map.online"/></span>
          </div>
        </div>

        <p class="hint"><fmt:message key="atm.map.hint"/></p>

        <div class="atm-list">
          <c:forEach var="atm" items="${atms}">
            <a class="atm-card"
               href="${appURL}atmController?action=getATMById&atmId=${atm.atmId}${qLang}"
               target="_blank">
              <span class="aico">&#9673;</span>
              <span class="t">
                <strong>${fn:escapeXml(atm.atmName)}</strong>
                <small>${fn:escapeXml(atm.atmLocation)}</small>
              </span>
              <span class="st"></span>
            </a>
          </c:forEach>
        </div>

        <p class="status-line"><strong>${fn:length(atms)}</strong> <fmt:message key="atm.map.machines"/></p>

      </c:otherwise>
    </c:choose>
  </div>
</body>
</html>
