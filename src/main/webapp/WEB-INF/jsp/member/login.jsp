<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

    <main class="container">
        <h2>로그인</h2>
        <form action="${pageContext.request.contextPath}/member/login" method="post">
            <label>아이디:</label>
            <input type="text" name="username" required>

            <label>비밀번호:</label>
            <input type="password" name="password" required>

            <button type="submit">로그인</button>
        </form>

        <c:if test="${not empty error}">
            <p class="error">${error}</p>
        </c:if>

        <!-- 회원가입 안내 -->
        <div class="form-links">
            <p>아직 회원이 아니신가요? 
               <a href="${pageContext.request.contextPath}/member/signup">회원가입하기</a>
            </p>
        </div>
    </main>

    <%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>

</body>
</html>
