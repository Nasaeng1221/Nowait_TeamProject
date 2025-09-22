<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8" />
            <title>Intro Page</title>

            <!-- 공통 헤더 CSS -->
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header/header.css" />

            <!-- 인트로 전용 CSS -->
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/intro/intro.css" />

            <!-- 아이콘 -->
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />

            <script>
                // 컨텍스트패스 전역
                window.APP_CTX = '${pageContext.request.contextPath}';
            </script>
        </head>

        <body class="intro-page">
            <%@ include file="/WEB-INF/jsp/fragments/header.jsp" %>

                <!-- 2번째 줄: 메인 히어로 -->
                <section class="main-hero">
                    <!-- 왼쪽 -->
                    <div class="hero-left">
                        <div class="title-box">
                            <p class="title-text">최고의 맛집!</p>
                            <p class="title-text">미리 예약하세요</p>
                        </div>

                        <div class="search-box">
                            <input type="text" id="searchInput" placeholder="어떤 지역을 찾으세요?" />
                            <button id="searchBtn"><i class="fa fa-search"></i></button>
                        </div>
                    </div>

                    <!-- 오른쪽 슬라이드 -->
                    <div class="hero-right">
                        <div class="hero-slider">
                            <!-- 슬라이드 1 -->
                            <div class="slide active" style="background-color:#f7c600;">
                                <div class="slide-content">
                                    <div class="slide-icon">
                                        <!-- 쿠폰 -->
                                        <svg viewBox="0 0 160 100" width="88" height="58"
                                            xmlns="http://www.w3.org/2000/svg">
                                            <defs>
                                                <mask id="couponCut">
                                                    <rect x="0" y="0" width="160" height="100" fill="#fff" />
                                                    <circle cx="0" cy="50" r="14" fill="#000" />
                                                    <circle cx="160" cy="50" r="14" fill="#000" />
                                                </mask>
                                            </defs>
                                            <rect x="4" y="8" width="152" height="84" rx="16" ry="16" fill="#fff"
                                                mask="url(#couponCut)" />
                                            <path d="M80 12 L80 88" stroke="#f7c600" stroke-width="2"
                                                stroke-dasharray="6 6" />
                                            <circle cx="52" cy="40" r="8" fill="#f7c600" />
                                            <circle cx="52" cy="64" r="8" fill="#f7c600" />
                                            <path d="M60 72 L44 32" stroke="#f7c600" stroke-width="6"
                                                stroke-linecap="round" />
                                        </svg>
                                    </div>
                                    <h2>오늘 가입하면 1회 무료예약 쿠폰!</h2>
                                    <p>가입만 해도 즉시 사용 가능</p>
                                </div>
                            </div>

                            <!-- 슬라이드 2 -->
                            <div class="slide" style="background-color:#4f8ef7;">
                                <div class="slide-content">
                                    <div class="slide-icon">
                                        <!-- 카드 -->
                                        <svg viewBox="0 0 180 120" width="88" height="58"
                                            xmlns="http://www.w3.org/2000/svg">
                                            <rect x="6" y="10" width="168" height="100" rx="14" ry="14"
                                                fill="#e6f0ff" />
                                            <rect x="6" y="28" width="168" height="16" fill="rgba(79,142,247,.3)" />
                                            <rect x="28" y="58" width="34" height="24" rx="4" fill="#f7c600" />
                                            <path d="M32 70h26M45 62v20" stroke="#4f8ef7" stroke-width="2"
                                                stroke-linecap="round" />
                                            <rect x="72" y="62" width="70" height="6" rx="3" fill="#4f8ef7" />
                                            <rect x="72" y="76" width="54" height="6" rx="3"
                                                fill="rgba(79,142,247,.7)" />
                                        </svg>
                                    </div>
                                    <h2>노웨잇 카드 발급시 평생 수수료 할인!</h2>
                                    <p>온라인/오프라인 어디서나 혜택</p>
                                </div>
                            </div>

                            <!-- 인디케이터 -->
                            <div class="indicators">
                                <span class="dot active"></span>
                                <span class="dot"></span>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- 3번째 줄: 카테고리 -->
                <div class="food-menu">
                    <div class="menu-item">
                        <div class="icon">🍗</div>
                        <div class="label">치킨</div>
                    </div>
                    <div class="menu-item">
                        <div class="icon">🍲</div>
                        <div class="label">찜/탕</div>
                    </div>
                    <div class="menu-item">
                        <div class="icon">🍕</div>
                        <div class="label">피자</div>
                    </div>
                    <div class="menu-item">
                        <div class="icon">🌙</div>
                        <div class="label">야식</div>
                    </div>
                    <div class="menu-item">
                        <div class="icon">🍚</div>
                        <div class="label">한식</div>
                    </div>
                    <div class="menu-item">
                        <div class="icon">☕</div>
                        <div class="label">카페/디저트</div>
                    </div>
                    <div class="menu-item">
                        <div class="icon">🥩</div>
                        <div class="label">고기</div>
                    </div>
                    <div class="menu-item">
                        <div class="icon">🍔</div>
                        <div class="label">패스트푸드</div>
                    </div>
                    <div class="menu-item">
                        <div class="icon">🍣</div>
                        <div class="label">돈까스/회</div>
                    </div>
                    <div class="menu-item">
                        <div class="icon">🍺</div>
                        <div class="label">술집</div>
                    </div>
                </div>

<!-- 오늘의 선정맛집 / 검색 결과 영역 -->
<section id="today-section" class="today-restaurant">
    <div class="today-left" id="today-title">
        <h2>오늘의<br><span class="highlight">선정맛집!</span></h2>
    </div>

    <div class="today-right">
        <!-- ✅ DB에서 가져온 랜덤 맛집 3개 출력 -->
        <div id="restaurant-results" class="restaurant-list">
            <c:forEach var="r" items="${randomRestaurants}">
                <div class="card"
                     onclick="location.href='${pageContext.request.contextPath}/restaurants/${r.id}'">
                    <div class="card-img">
                        <c:choose>
                            <c:when test="${not empty r.image}">
                                <!-- 업로드된 이미지 사용, 깨지면 기본이미지로 대체 -->
                                <img
                                    src="${pageContext.request.contextPath}/uploads/${r.image}"
                                    alt="${r.name}"
                                    loading="lazy"
                                    style="width:100%;height:100%;object-fit:cover;"
                                    onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/img/no-image.png';"
                                />
                            </c:when>
                            <c:otherwise>
                                <!-- 이미지가 없으면 기본이미지 -->
                                <img
                                    src="${pageContext.request.contextPath}/img/no-image.png"
                                    alt="이미지 없음"
                                    loading="lazy"
                                    style="width:100%;height:100%;object-fit:cover;"
                                />
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <p>${r.dong} ${r.name}</p>
                </div>
            </c:forEach>
        </div>

        <!-- ✅ 페이지네이션 (검색 결과 시에만 활성화) -->
        <div id="pagination" class="pagination"></div>
    </div>
</section>

                <%@ include file="/WEB-INF/jsp/fragments/footer.jsp" %>
                    <script src="${pageContext.request.contextPath}/js/intro/intro.js"></script>
        </body>

        </html>
<div id="auth-buttons" style="padding:16px 0;">
  <a href="<%= request.getContextPath()%>/login.jsp" class="btn"
     style="display:inline-block;padding:10px 16px;border:1px solid #333;border-radius:8px;text-decoration:none;">로그인</a>
  <a href="<%= request.getContextPath()%>/signup.jsp" class="btn"
     style="display:inline-block;padding:10px 16px;border:1px solid #333;border-radius:8px;text-decoration:none;margin-left:8px;">회원가입</a>
</div>
