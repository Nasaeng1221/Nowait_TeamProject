<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
  String role = (String)session.getAttribute("memberRole");
  if(role==null || (!"OWNER".equals(role) && !"ADMIN".equals(role))){
    response.sendRedirect(request.getContextPath()+"/login.jsp?next="+java.net.URLEncoder.encode("/owner/restaurants.jsp","UTF-8"));
    return;
  }
%>
<jsp:include page="/WEB-INF/jsp/fragments/header.jsp"/>

<div style="padding:24px; max-width:1100px; margin:0 auto;">
  <h2>내 식당 관리</h2>

  <h3 style="margin-top:16px;">신규 등록</h3>
  <form id="newForm" style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:12px;">
    <input name="name" placeholder="가게명" required>
    <input name="phone" placeholder="연락처">
    <input name="address" placeholder="주소" style="min-width:320px;">
    <input name="mainMenu" placeholder="주메뉴">
    <input name="openHours" placeholder="운영시간">
    <input name="image" placeholder="이미지 파일명(선택)">
    <button type="submit">등록</button>
  </form>
  <div id="msg" style="color:#b00;margin:6px 0;"></div>

  <h3>내 식당 목록</h3>
  <table id="tbl" border="1" cellpadding="8" cellspacing="0" style="border-collapse:collapse;width:100%;">
    <thead><tr><th>이름</th><th>연락처</th><th>주소</th><th>주메뉴</th><th>운영시간</th><th>이미지</th><th style="width:160px;">관리</th></tr></thead>
    <tbody></tbody>
  </table>
</div>

<script>
(function(){
  var ctx='${pageContext.request.contextPath}';
  var tb=document.querySelector('#tbl tbody');
  var msg=document.getElementById('msg');

  function load(){
    fetch(ctx+'/api/owner/restaurants',{credentials:'same-origin'})
      .then(r=>r.json()).then(list=>{
        tb.innerHTML='';
        (list||[]).forEach(r=>{
          var tr=document.createElement('tr'); tr.dataset.id=r.id;
          tr.innerHTML =
            '<td class="c-name">'+(r.name||'')+'</td>'+
            '<td class="c-phone">'+(r.phone||'')+'</td>'+
            '<td class="c-address">'+(r.address||'')+'</td>'+
            '<td class="c-main">'+(r.mainMenu||'')+'</td>'+
            '<td class="c-open">'+(r.openHours||'')+'</td>'+
            '<td class="c-image">'+(r.image||'')+'</td>'+
            '<td class="c-act"><button class="edit">수정</button></td>';
          tr.querySelector('.edit').addEventListener('click', function(){
            if(tr.classList.contains('editing')){ location.reload(); return; }
            tr.classList.add('editing');
            tr.querySelector('.c-name').innerHTML   = '<input class="e-name" value="'+(r.name||'')+'">';
            tr.querySelector('.c-phone').innerHTML  = '<input class="e-phone" value="'+(r.phone||'')+'">';
            tr.querySelector('.c-address').innerHTML= '<input class="e-address" style="min-width:260px" value="'+(r.address||'')+'">';
            tr.querySelector('.c-main').innerHTML   = '<input class="e-main" value="'+(r.mainMenu||'')+'">';
            tr.querySelector('.c-open').innerHTML   = '<input class="e-open" value="'+(r.openHours||'')+'">';
            tr.querySelector('.c-image').innerHTML  = '<input class="e-image" value="'+(r.image||'')+'">';
            tr.querySelector('.c-act').innerHTML =
              '<button class="save">저장</button> <button class="cancel">취소</button>';
            tr.querySelector('.save').addEventListener('click', function(){
              var body = {
                name:   tr.querySelector('.e-name').value,
                phone:  tr.querySelector('.e-phone').value,
                address:tr.querySelector('.e-address').value,
                mainMenu: tr.querySelector('.e-main').value,
                openHours:tr.querySelector('.e-open').value,
                image:  tr.querySelector('.e-image').value
              };
              fetch(ctx+'/api/owner/restaurants/'+r.id,{
                method:'POST', headers:{'Content-Type':'application/json'},
                credentials:'same-origin', body: JSON.stringify(body)
              }).then(rs=>rs.json().catch(()=>({success:false})))
                .then(j=>{ if(j&&j.success){ load(); } else { alert('수정 실패'); }});
            });
            tr.querySelector('.cancel').addEventListener('click', function(){ location.reload(); });
          });
          tb.appendChild(tr);
        });
      });
  }

  document.getElementById('newForm').addEventListener('submit', function(e){
    e.preventDefault(); msg.textContent='';
    var fd=new FormData(this), obj={}; fd.forEach((v,k)=>obj[k]=v);
    fetch(ctx+'/api/owner/restaurants', {
      method:'POST', headers:{'Content-Type':'application/json'},
      credentials:'same-origin', body: JSON.stringify(obj)
    }).then(rs=>rs.json().catch(()=>({success:false})))
      .then(j=>{ if(j&&j.success){ this.reset(); load(); } else { msg.textContent='등록 실패'; } });
  });

  load();
})();
</script>
