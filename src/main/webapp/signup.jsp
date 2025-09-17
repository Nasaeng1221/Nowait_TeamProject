<%@ page contentType="text/html; charset=UTF-8" %>
<jsp:include page="/WEB-INF/jsp/fragments/header.jsp"/>
<div style="padding:24px;max-width:640px;margin:0 auto;">
  <h2>회원가입</h2>
  <form id="f">
    <div><input name="username" placeholder="아이디" required></div>
    <div><input name="password" type="password" placeholder="비밀번호" required></div>
    <div><input name="name" placeholder="이름" required></div>
    <div><input name="phone" placeholder="연락처"></div>
    <div><input name="email" type="email" placeholder="이메일"></div>
    <div style="margin:8px 0;">
      <label><input type="radio" name="role" value="USER" checked> 일반회원</label>
      <label style="margin-left:12px;"><input type="radio" name="role" value="OWNER"> 점주</label>
    </div>
    <button type="submit">회원가입</button>
    <div id="err" style="color:#b00;margin-top:8px;"></div>
  </form>
</div>
<script>
(function(){
  var ctx='${pageContext.request.contextPath}';
  document.getElementById('f').addEventListener('submit', async function(e){
    e.preventDefault();
    var fd=new FormData(this), obj={};
    fd.forEach((v,k)=>obj[k]=v);
    try{
      const r=await fetch(ctx+'/api/signup',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(obj)});
      const j=await r.json().catch(()=>null);
      if(r.ok && j && j.success) location.href = ctx + '/login.jsp?signup=ok';
      else document.getElementById('err').textContent = (j&&j.message)||'가입 실패';
    }catch(_){ document.getElementById('err').textContent='네트워크 오류'; }
  });
})();
</script>
