<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty wallet}">
  <c:set var="wallet" value="${ {'phone':'01012345678','nationalId':'29901010123456','available':12500.75,'held':450.00} }"/>
</c:if>

<c:if test="${empty transactions}">
  <c:set var="transactions" value="${ [
      {'type':'deposit','status':'success','amount':1500.00,'from':'Card •••• 0366','to':'Wallet','date':'2026-08-03 14:32','ref':'TX-882134'},
      {'type':'transfer','status':'success','amount':-250.00,'from':'Wallet','to':'01123456789','date':'2026-08-03 11:07','ref':'TX-882133'},
      {'type':'withdraw','status':'success','amount':-800.00,'from':'Wallet','to':'ATM — Nasr City','date':'2026-08-02 19:45','ref':'TX-882132'},
      {'type':'transfer','status':'pending','amount':-120.00,'from':'Wallet','to':'01098765432','date':'2026-08-02 16:20','ref':'TX-882131'},
      {'type':'payment','status':'success','amount':-45.50,'from':'Wallet','to':'Internet bill','date':'2026-08-01 09:12','ref':'TX-882130'},
      {'type':'deposit','status':'success','amount':3000.00,'from':'Salary','to':'Wallet','date':'2026-07-31 08:00','ref':'TX-882129'},
      {'type':'withdraw','status':'failed','amount':-500.00,'from':'Wallet','to':'ATM — Zamalek','date':'2026-07-30 22:18','ref':'TX-882128'},
      {'type':'transfer','status':'success','amount':-75.00,'from':'Wallet','to':'01234567890','date':'2026-07-30 13:05','ref':'TX-882127'},
      {'type':'payment','status':'success','amount':-199.99,'from':'Wallet','to':'Electricity bill','date':'2026-07-29 20:31','ref':'TX-882126'},
      {'type':'deposit','status':'success','amount':1000.00,'from':'ATM — Maadi','to':'Wallet','date':'2026-07-28 17:44','ref':'TX-882125'}
  ] }"/>
</c:if>

<c:if test="${empty cards}">
  <c:set var="cards" value="${ [
      {'number':'4532015112830366','name':'Ahmed Mohamed','bank':'Banque Misr','label':'Salary card','expire':'08/28','cvv':'123','status':1,'tone':'blue'},
      {'number':'5241150057894478','name':'Ahmed Mohamed','bank':'CIB','label':'Online purchases','expire':'03/27','cvv':'456','status':1,'tone':'violet'},
      {'number':'3778855512349102','name':'Ahmed Mohamed','bank':'QNB AlAhli','label':'','expire':'11/26','cvv':'789','status':0,'tone':'emerald'}
  ] }"/>
</c:if>
