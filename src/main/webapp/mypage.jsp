<%@ page contentType="text/html; charset=UTF-8" %>
<%
  if (session.getAttribute("memberId")==null &&
      session.getAttribute("memberName")==null &&
      session.getAttribute("userName")==null) {
    response.sendRedirect(request.getContextPath()+"/login.jsp"); return;
  }
%>
<%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>
<div class="container" style="padding:24px;">
  <h2>마이페이지</h2>
  <p>프로필/비밀번호 변경 등 구성 예정(임시).</p>
</div>
