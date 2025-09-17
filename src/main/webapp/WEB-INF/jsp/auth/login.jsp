<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html><head><title>로그인</title></head>
<body>
<h2>로그인</h2>
<c:if test="${param.error != null}">
  <div style="color:red">로그인 실패: 아이디/비밀번호를 확인하세요.</div>
</c:if>
<c:if test="${param.registered != null}">
  <div style="color:green">회원가입이 완료되었습니다. 로그인하세요.</div>
</c:if>

<form method="post" action="<c:url value='/login'/>">
  <input type="text" name="username" placeholder="아이디" required />
  <input type="password" name="password" placeholder="비밀번호" required />
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <button type="submit">로그인</button>
</form>
</body></html>
