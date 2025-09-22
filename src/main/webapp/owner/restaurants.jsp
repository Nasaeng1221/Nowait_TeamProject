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
  <!-- ✅ 신규등록은 멀티파트로 서버에 그대로 제출 -->
  <form id="newForm" method="post" action="${pageContext.request.contextPath}/api/owner/restaurants/create"
        enctype="multipart/form-data" style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:12px;">
    <input name="name" placeholder="가게명" required>
    <input name="phone" placeholder="연락처">
    <input name="address" placeholder="주소" style="min-width:320px;">
    <input name="mainMenu" placeholder="주메뉴">
    <input name="openHours" placeholder="운영시간">
    <input type="file" name="imageFile" accept="image/*">
    <button type="submit">등록</button>
  </form>

  <div id="msg" style="color:#b00;margin:6px 0;"></div>

  <h3>내 식당 목록</h3>
  <table id="tbl" border="1" cellpadding="8" cellspacing="0" style="border-collapse:collapse;width:100%;">
    <thead>
      <tr>
        <th>미리보기</th>
        <th>이름</th>
        <th>연락처</th>
        <th>주소</th>
        <th>주메뉴</th>
        <th>운영시간</th>
        <th>이미지(경로)</th>
        <th style="width:200px;">관리</th>
      </tr>
    </thead>
    <tbody></tbody>
  </table>
</div>

<script>
(function(){
  var ctx='${pageContext.request.contextPath}';
  var tb=document.querySelector('#tbl tbody');
  var msg=document.getElementById('msg');

  function esc(s){ return (s==null?'':String(s)); }

  function load(){
    fetch(ctx+'/api/owner/restaurants',{credentials:'same-origin'})
      .then(function(r){ return r.json(); })
      .then(function(list){
        tb.innerHTML='';
        (list||[]).forEach(function(r){
          var tr=document.createElement('tr'); tr.dataset.id=r.id;

          var imgHtml = (r.image && r.image.length)
            ? '<img src="' + ctx + '/uploads/' + r.image + '" style="height:36px;">'
            : '';

          // ✅ 헤더와 맞는 8개 컬럼 생성 (미리보기 td 포함)
          tr.innerHTML =
            '<td class="c-prev">'+ imgHtml +'</td>' +
            '<td class="c-name">'+esc(r.name)+'</td>' +
            '<td class="c-phone">'+esc(r.phone)+'</td>' +
            '<td class="c-address">'+esc(r.address)+'</td>' +
            '<td class="c-main">'+esc(r.mainMenu)+'</td>' +
            '<td class="c-open">'+esc(r.openHours)+'</td>' +
            '<td class="c-image-path">'+esc(r.image||"")+'</td>' +
            '<td class="c-act"><button class="edit">수정</button></td>';

          tr.querySelector('.edit').addEventListener('click', function(){
            if(tr.classList.contains('editing')){ location.reload(); return; }
            tr.classList.add('editing');

            // 입력필드로 전환
            tr.querySelector('.c-name').innerHTML    = '<input class="e-name" value="'+esc(r.name)+'">';
            tr.querySelector('.c-phone').innerHTML   = '<input class="e-phone" value="'+esc(r.phone)+'">';
            tr.querySelector('.c-address').innerHTML = '<input class="e-address" style="min-width:260px" value="'+esc(r.address)+'">';
            tr.querySelector('.c-main').innerHTML    = '<input class="e-main" value="'+esc(r.mainMenu)+'">';
            tr.querySelector('.c-open').innerHTML    = '<input class="e-open" value="'+esc(r.openHours)+'">';

            // 미리보기 유지, 이미지 경로 칸에 파일 선택 추가
            tr.querySelector('.c-prev').innerHTML =
              (r.image ? '<img src="'+ctx+'/uploads/'+r.image+'" style="height:36px;vertical-align:middle;">' : '');
            tr.querySelector('.c-image-path').innerHTML =
              (r.image ? '<div style="font-size:12px;color:#555;margin-bottom:6px;">'+esc(r.image)+'</div>' : '') +
              '<input class="e-image-file" type="file" accept=".jpg,.jpeg,.png,.webp">';

            tr.querySelector('.c-act').innerHTML =
              '<button class="save">저장</button> <button class="cancel">취소</button>';

            tr.querySelector('.save').addEventListener('click', function(){
              var form=new FormData();
              // 서버는 세션의 memberId를 사용하므로 ownerId는 필수 아님
              form.append('name',     tr.querySelector('.e-name').value);
              form.append('phone',    tr.querySelector('.e-phone').value);
              form.append('address',  tr.querySelector('.e-address').value);
              form.append('mainMenu', tr.querySelector('.e-main').value);
              form.append('openHours',tr.querySelector('.e-open').value);

              var f=tr.querySelector('.e-image-file');
              if(f && f.files && f.files[0]) form.append('imageFile', f.files[0]);

              fetch(ctx+'/api/owner/restaurants/'+r.id+'/update', {
                method:'POST',
                credentials:'same-origin',
                body: form
              })
              .then(function(rs){ return rs.json().catch(function(){ return {success:false, error:'invalid json'}; }); })
              .then(function(j){
                if (!j || j.success === false) {
                  alert('수정 실패: ' + (j && j.error ? j.error : '서버 오류'));
                } else {
                  load();
                }
              })
              .catch(function(){ alert('수정 실패: 네트워크 오류'); });
            });

            tr.querySelector('.cancel').addEventListener('click', function(){ location.reload(); });
          });

          tb.appendChild(tr);
        });
      });
  }

  // ✅ 신규 등록: form 자체 submit 가로채서 multipart로 API(/create) 호출
  document.getElementById('newForm').addEventListener('submit', function(e){
    e.preventDefault(); msg.textContent='';

    var formData = new FormData(this);
    fetch(this.action, {
      method: 'POST',
      credentials: 'same-origin',
      body: formData
    })
    .then(function(rs){ return rs.json().catch(function(){ return {success:false}; }); })
    .then(function(j){
      if(j && j.success){
        msg.style.color = '#090';
        msg.textContent = '등록 완료';
        (document.getElementById('newForm')).reset();
        load();
      }else{
        msg.style.color = '#b00';
        msg.textContent = '등록 실패' + (j && j.error ? (' - ' + j.error) : '');
      }
    })
    .catch(function(){
      msg.style.color = '#b00';
      msg.textContent = '등록 실패 - 네트워크 오류';
    });
  });

  load();
})();
</script>

