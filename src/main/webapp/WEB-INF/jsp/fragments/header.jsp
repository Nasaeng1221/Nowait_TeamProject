<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header/header.css">

<c:set var="m" value="${sessionScope.member}" />
<c:set var="role" value="${empty m ? '' : m['role']}" />

<header>
  <div class="logo">
    <a href="${pageContext.request.contextPath}/intro">
      <img src="${pageContext.request.contextPath}/img/intro/logo.jpg" alt="nowait 로고">
    </a>
  </div>

  <nav class="nav-menu">
    <c:choose>
      <c:when test="${not empty m}">
        <span>${m['name']} 님</span>
        <c:choose>
          <c:when test="${role == 'OWNER' || role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/owner/restaurants.jsp">내 식당 관리</a>
            <a href="${pageContext.request.contextPath}/mypage.jsp">마이페이지</a>
          </c:when>
          <c:otherwise>
            <a href="${pageContext.request.contextPath}/reservations.jsp">내 예약</a>
            <a href="${pageContext.request.contextPath}/mypage.jsp">마이페이지</a>
          </c:otherwise>
        </c:choose>
        <a href="${pageContext.request.contextPath}/auth/logout">로그아웃</a>
      </c:when>
      <c:otherwise>
        <a href="${pageContext.request.contextPath}/login.jsp"  class="login">로그인</a>
        <a href="${pageContext.request.contextPath}/signup.jsp" class="signup">회원가입</a>
      </c:otherwise>
    </c:choose>
  </nav>
</header>
<hr class="yellow-line" />
