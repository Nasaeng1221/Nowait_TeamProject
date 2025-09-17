<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

    <main class="container">
        <h2>회원가입</h2>
        <form action="${pageContext.request.contextPath}/member/signup" method="post">
            <label>아이디:</label>
            <input type="text" name="username" required><br>

            <label>비밀번호:</label>
            <input type="password" name="password" required><br>

            <label>이름:</label>
            <input type="text" name="name" required><br>

            <label>전화번호:</label>
            <input type="text" name="phone" required><br>

            <label>이메일:</label>
            <input type="email" name="email" required><br>

            <button type="submit">회원가입</button>
        </form>
    </main>

    <%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>

</body>
</html>
