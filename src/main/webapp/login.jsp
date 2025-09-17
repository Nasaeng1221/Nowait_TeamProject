<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
  <h2>로그인</h2>

  <!-- 폼 전송도 가능하도록 action/method 지정 -->
  <form id="loginForm" action="<%= request.getContextPath()%>/api/login" method="POST">
    <label for="username">아이디</label>
    <input id="username" name="username" type="text" required />

    <label for="password">비밀번호</label>
    <input id="password" name="password" type="password" required />

    <button type="submit">로그인</button>
    <div id="loginError" style="display:none;color:#b00;margin-top:8px;"></div>
  </form>
  <p style="margin-top:8px;font-size:12px;color:#666">
    문제가 계속되면, 폼 전송(기본 방식)으로도 처리됩니다.
  </p>
</div>

<script>
(function(){
  const base = window.location.origin + '<%= request.getContextPath()%>';
  const form = document.getElementById('loginForm');
  const errEl = document.getElementById('loginError');

  form.addEventListener('submit', async function(e){
    // JS가 가능하면 fetch로 처리 (실패 시 폼 전송으로 폴백)
    e.preventDefault();
    errEl.style.display = 'none';

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    if(!username || !password){
      errEl.textContent = '아이디와 비밀번호를 입력하세요.';
      errEl.style.display = 'block';
      return;
    }

    try{
      const res = await fetch(base + '/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'same-origin',
        body: JSON.stringify({ username, password })
      });

      // fetch 자체가 성공(네트워크 OK)했을 때만 여기 옴
      const data = await res.json().catch(()=>null);

      if(!res.ok){
        errEl.textContent = (data && data.message) || '로그인 실패';
        errEl.style.display = 'block';
        return;
      }

      if(data && data.success){
        window.location.href = data.redirect || (base + '/');
      } else {
        errEl.textContent = (data && data.message) || '로그인 실패';
        errEl.style.display = 'block';
      }
    } catch(err){
      // 네트워크/스킴/포트/컨텍스트 문제 등으로 fetch 불가 → 폼 기본 전송으로 폴백
      console.error('fetch error -> fallback to form submit', err);
      form.submit();
    }
  });
})();
</script>
