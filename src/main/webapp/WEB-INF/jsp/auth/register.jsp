<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html><head><title>회원가입</title></head>
<body>
<h2>회원가입</h2>

<form method="post" action="<c:url value='/register'/>">
  <input type="text" name="username" placeholder="아이디" required />
  <input type="password" name="password" placeholder="비밀번호" required />
  <input type="text" name="name" placeholder="이름" />
  <input type="text" name="phone" placeholder="전화번호" />
  <input type="email" name="email" placeholder="이메일" />
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <button type="submit">회원가입</button>
</form>
</body></html>
