document.addEventListener("DOMContentLoaded", () => {
  // ===== 유틸 =====
  const pad2 = n => (n < 10 ? "0" + n : "" + n);
  const fmtDate = d => {
    const y = d.getFullYear();
    const m = pad2(d.getMonth() + 1);
    const day = pad2(d.getDate());
    return `${y}-${m}-${day}`;
  };

  function buildSlots(openHours) {
    const [startStr, endStr] = openHours.split("~");
    const [sh, sm] = startStr.split(":").map(Number);
    const [eh, em] = endStr.split(":").map(Number);
    let t = new Date(2000, 0, 1, sh, sm, 0);
    const end = new Date(2000, 0, 1, eh, em, 0);
    const slots = [];
    while (t < end) {
      slots.push(`${pad2(t.getHours())}:${pad2(t.getMinutes())}`);
      t = new Date(t.getTime() + 30 * 60 * 1000);
    }
    return slots;
  }

  const ALL_SLOTS = buildSlots(window.OPEN_HOURS);

  // ===== 전역 상태 =====
  let current = new Date();
  let selectedDate = null;
  let selectedTime = null;

  // ===== DOM =====
  const monthTitle = document.getElementById("monthTitle");
  const calendarEl = document.getElementById("calendar");
  const timeSlotsEl = document.getElementById("timeSlots");
  const prevBtn = document.getElementById("prevMonth");
  const nextBtn = document.getElementById("nextMonth");
  const reserveForm = document.getElementById("reserveForm");
  const nameInput = document.getElementById("nameInput");
  const phoneInput = document.getElementById("phoneInput");

  // ===== API =====
  async function fetchAvailability(dateStr) {
    const res = await fetch(`/restaurants/${window.REST_ID}/availability?date=${dateStr}`);
    if (!res.ok) throw new Error("availability load failed");
    return await res.json();
  }

  // ===== 달력 렌더링 =====
  async function renderCalendar() {
    const year = current.getFullYear();
    const month = current.getMonth();
    monthTitle.textContent = `${year}.${pad2(month + 1)}`;

    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const startDow = firstDay.getDay();
    const totalDays = lastDay.getDate();

    const headRow = `
      <div class="cal-grid">
        <div class="cal-head">일</div>
        <div class="cal-head">월</div>
        <div class="cal-head">화</div>
        <div class="cal-head">수</div>
        <div class="cal-head">목</div>
        <div class="cal-head">금</div>
        <div class="cal-head">토</div>
      </div>`;

    const grid = document.createElement("div");
    grid.className = "cal-grid";

    for (let i = 0; i < startDow; i++) {
      const empty = document.createElement("div");
      empty.className = "cal-cell empty";
      grid.appendChild(empty);
    }

    for (let d = 1; d <= totalDays; d++) {
      const cell = document.createElement("button");
      cell.type = "button";
      cell.className = "cal-cell";
      cell.textContent = d;
      const dateStr = `${year}-${pad2(month + 1)}-${pad2(d)}`;
      cell.dataset.date = dateStr;

      // ✅ 오늘 기준 1개월 이내만 선택 가능
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const targetDate = new Date(year, month, d);
      targetDate.setHours(0, 0, 0, 0);
      const oneMonthLater = new Date();
      oneMonthLater.setMonth(today.getMonth() + 1);

      if (targetDate < today || targetDate > oneMonthLater) {
        cell.classList.add("disabled");
      }

      cell.addEventListener("click", async () => {
        if (cell.classList.contains("disabled")) return;
        document.querySelectorAll(".cal-cell.selected").forEach(el => el.classList.remove("selected"));
        cell.classList.add("selected");
        selectedDate = dateStr;
        selectedTime = null;
        await renderTimesFor(dateStr);
      });

      grid.appendChild(cell);
    }

    calendarEl.innerHTML = headRow;
    calendarEl.appendChild(grid);

    const dateBtns = grid.querySelectorAll(".cal-cell:not(.empty)");
    dateBtns.forEach(async (btn) => {
      const date = btn.dataset.date;
      try {
        const data = await fetchAvailability(date);
        if (data.fullyBooked) btn.classList.add("disabled");
        const todayStr = fmtDate(new Date());
        if (!selectedDate && date === todayStr && !data.fullyBooked) btn.click();
      } catch (e) {
        console.error(e);
      }
    });
  }

  async function renderTimesFor(dateStr) {
    timeSlotsEl.innerHTML = "로딩 중...";
    try {
      const data = await fetchAvailability(dateStr);
      const set = new Set(data.availableTimes);
      timeSlotsEl.innerHTML = "";
      ALL_SLOTS.forEach(time => {
        const btn = document.createElement("button");
        btn.type = "button";
        btn.className = "time-btn";
        btn.textContent = time;
        if (!set.has(time)) btn.disabled = true;
        btn.addEventListener("click", () => {
          if (btn.disabled) return;
          document.querySelectorAll(".time-btn.active").forEach(el => el.classList.remove("active"));
          btn.classList.add("active");
          selectedTime = time;
        });
        timeSlotsEl.appendChild(btn);
      });
    } catch (e) {
      console.error(e);
      timeSlotsEl.innerHTML = "<div class='login-warning'>시간 정보를 불러오지 못했습니다.</div>";
    }
  }

  prevBtn.addEventListener("click", () => {
    current = new Date(current.getFullYear(), current.getMonth() - 1, 1);
    selectedDate = null;
    selectedTime = null;
    renderCalendar();
  });

  nextBtn.addEventListener("click", () => {
    current = new Date(current.getFullYear(), current.getMonth() + 1, 1);
    selectedDate = null;
    selectedTime = null;
    renderCalendar();
  });

  if (reserveForm) {
    reserveForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      // ✅ 로그인 여부 확인
      if (!window.IS_LOGGED_IN) {
        alert("로그인 후 예약 가능합니다.");
        return;
      }

      if (!selectedDate) {
        alert("날짜를 먼저 선택해 주세요.");
        return;
      }
      if (!selectedTime) {
        alert("시간을 선택해 주세요.");
        return;
      }
      if (!nameInput || !phoneInput) {
        alert("예약자 입력 요소가 없습니다.");
        return;
      }
      const customerName = nameInput.value.trim();
      const customerPhone = phoneInput.value.trim();
      if (!customerName || !customerPhone) {
        alert("성함과 연락처를 입력해 주세요.");
        return;
      }

      try {
        const res = await fetch(`/restaurants/${window.REST_ID}/reservations`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ date: selectedDate, time: selectedTime, customerName, customerPhone })
        });

        if (!res.ok) {
          const msg = await res.text().catch(() => "");
          throw new Error(msg || "예약 실패");
        }

        alert("예약이 완료되었습니다.");
        await renderTimesFor(selectedDate);
        renderCalendar();
      } catch (err) {
        alert(err.message || "예약 중 오류가 발생했습니다.");
      }
    });
  }

  renderCalendar();
});
