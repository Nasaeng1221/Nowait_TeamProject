document.addEventListener("DOMContentLoaded", () => {
  console.log("intro.jsp 페이지 로드 완료!");

  // ============ 검색/페이지네이션 ============
  const searchBtn = document.getElementById("searchBtn");
  const searchInput = document.getElementById("searchInput");
  const restaurantResults = document.getElementById("restaurant-results");
  const pagination = document.getElementById("pagination");

  // 페이지네이션 생성
  function renderPagination(totalPages, currentPage, region) {
    const blockSize = 10;
    const currentBlock = Math.floor(currentPage / blockSize);
    const startPage = currentBlock * blockSize + 1;
    let endPage = startPage + blockSize - 1;
    if (endPage > totalPages) endPage = totalPages;

    let html = "";
    if (startPage > 1) {
      html += `<button class="page-btn" data-page="${startPage - 2}">&lt;</button>`;
    }
    for (let i = startPage; i <= endPage; i++) {
      html += `<button class="page-btn ${i - 1 === currentPage ? "active" : ""}" data-page="${i - 1}">${i}</button>`;
    }
    if (endPage < totalPages) {
      html += `<button class="page-btn" data-page="${endPage}">&gt;</button>`;
    }

    pagination.innerHTML = html;

    pagination.querySelectorAll(".page-btn").forEach(btn => {
      btn.addEventListener("click", () => {
        const page = parseInt(btn.getAttribute("data-page"), 10);
        loadRestaurants(region, page);
      });
    });
  }

  // 검색 결과 불러오기
  function loadRestaurants(region, page = 0) {
    fetch(`/intro/search?region=${encodeURIComponent(region)}&page=${page}`)
      .then(res => res.json())
      .then(data => {
        const todayTitle = document.getElementById("today-title");

        if (todayTitle) todayTitle.style.display = "none";

        let html = "";

        if (!data.content || data.content.length === 0) {
          alert("검색 결과가 없습니다.");
          restaurantResults.innerHTML = "";
          pagination.innerHTML = "";
          return;
        }

        // ✅ 검색 결과 카드 렌더링
        // ✅ 검색 결과 카드 렌더링
        data.content.forEach(r => {
          html += `
            <div class="card" onclick="location.href='/restaurants/${r.id}'">
              <div class="card-img">
                <img src="/img/intro/${r.image}" alt="${r.name}" />
              </div>
              <div class="card-info">
                <p class="card-title">${r.dong} ${r.name}</p>
              </div>
            </div>
          `;
        });

        restaurantResults.innerHTML = html;

        renderPagination(data.totalPages, data.number, region);
      })
      .catch(err => {
        console.error(err);
        alert("검색 중 오류가 발생했습니다.");
      });
  }

  // 검색 버튼 이벤트
  searchBtn.addEventListener("click", () => {
    const region = searchInput.value.trim();
    if (!region) return alert("지역명을 입력하세요!");
    loadRestaurants(region, 0);
  });

  // ✅ Enter 키로도 검색 실행
  searchInput.addEventListener("keyup", (event) => {
    if (event.key === "Enter") {
      searchBtn.click();
    }
  });

  // ============ 슬라이더 ============
  const slides = document.querySelectorAll(".hero-slider .slide");
  const dots = document.querySelectorAll(".hero-slider .dot");

  if (slides.length > 0) {
    let currentIndex = 0;
    let timerId = null;
    let autoDir = 1;

    function cleanSlideClasses(slide) {
      slide.classList.remove("exit-left", "exit-right", "from-left");
    }

    function goToSlide(newIndex, direction) {
      if (newIndex === currentIndex) return;

      const current = slides[currentIndex];
      const next = slides[newIndex];

      cleanSlideClasses(next);
      if (direction === "left") next.classList.add("from-left");
      next.getBoundingClientRect();

      current.classList.remove("active");
      current.classList.add(direction === "right" ? "exit-left" : "exit-right");

      next.classList.add("active");
      next.classList.remove("from-left");

      dots[currentIndex]?.classList.remove("active");
      dots[newIndex]?.classList.add("active");

      const tidy = (e) => {
        if (e.propertyName === "transform") {
          current.removeEventListener("transitionend", tidy);
          cleanSlideClasses(current);
        }
      };
      current.addEventListener("transitionend", tidy);

      currentIndex = newIndex;
    }

    function autoNext() {
      const direction = autoDir === 1 ? "right" : "left";
      const nextIndex =
        autoDir === 1
          ? (currentIndex + 1) % slides.length
          : (currentIndex - 1 + slides.length) % slides.length;

      goToSlide(nextIndex, direction);
      autoDir *= -1;
    }

    function start() {
      stop();
      timerId = setInterval(autoNext, 4000);
    }
    function stop() {
      if (timerId) clearInterval(timerId);
      timerId = null;
    }

    dots.forEach((dot, i) => {
      dot.addEventListener("click", () => {
        stop();
        goToSlide(i, i > currentIndex ? "right" : "left");
        start();
      });
    });

    slides.forEach((s, i) => {
      if (i === 0) s.classList.add("active");
      else cleanSlideClasses(s);
    });
    dots[0]?.classList.add("active");
    start();
  }
});
