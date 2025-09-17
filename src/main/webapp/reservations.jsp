<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>내 예약</title>
  <style>
    .container{padding:24px; max-width:1100px; margin:0 auto;}
    table{border-collapse:collapse; width:100%}
    th,td{border:1px solid #ddd; padding:8px; text-align:left}
    thead{background:#fafafa;}
    .err{color:#b00;margin:12px 0;display:none}
    .empty{color:#666;margin:12px 0;display:none}
    .col-actions button{margin-right:6px}
  </style>
</head>
<body>
  <jsp:include page="/WEB-INF/jsp/fragments/header.jsp"/>

  <div class="container">
    <h2>내 예약</h2>
    <div id="errorBox" class="err"></div>
    <div id="emptyBox" class="empty">아직 예약이 없습니다.</div>

    <table id="resvTable" style="display:none">
      <thead>
        <tr>
          <th>식당</th><th>예약일</th><th>시간</th><th>이름/연락처</th><th>상태</th><th style="width:220px">관리</th>
        </tr>
      </thead>
      <tbody></tbody>
    </table>
  </div>

  <script>
  (function(){
    var ctx = '<%= request.getContextPath() %>';
    var tb  = document.querySelector('#resvTable tbody');
    var tbl = document.getElementById('resvTable');
    var empty = document.getElementById('emptyBox');
    var err = document.getElementById('errorBox');

    function pad(n){ n = parseInt(n,10)||0; return (n<10?'0':'')+n; }
    function toISO(s){
      try{
        var d = new Date(s);
        return d.getFullYear() + '-' + pad(d.getMonth()+1) + '-' + pad(d.getDate());
      }catch(e){ return s||''; }
    }

    function renderRow(r){
      var tr = document.createElement('tr'); tr.dataset.id = r.id;
      var isoDate = r.reservedDate ? toISO(r.reservedDate) : '';

      tr.innerHTML =
        '<td>' + (r.restaurantName || r.restaurantId || '') + '</td>' +
        '<td class="col-date">' + (r.reservedDate || '') + '</td>' +
        '<td class="col-time">' + (r.reservedTime || '') + '</td>' +
        '<td class="col-name">' + [(r.customerName||''),(r.customerPhone||'')].filter(Boolean).join(' / ') + '</td>' +
        '<td class="col-status">' + (r.status || '') + '</td>' +
        '<td class="col-actions">' +
          ((r.status && r.status.toUpperCase().indexOf('CANCEL')===0)
            ? '<em>취소됨</em>'
            : '<button class="btn-edit">변경</button><button class="btn-cancel">취소</button>') +
        '</td>';

      // 변경
      var btnEdit = tr.querySelector('.btn-edit');
      if(btnEdit){
        btnEdit.addEventListener('click', function(){
          tr.classList.add('editing');
          tr.querySelector('.col-date').innerHTML = '<input type="date" class="edit-date" value="' + isoDate + '">';
          tr.querySelector('.col-time').innerHTML = '<input type="time" class="edit-time" value="' + (r.reservedTime||'') + '">';
          var nm = (r.customerName||''), ph = (r.customerPhone||'');
          tr.querySelector('.col-name').innerHTML =
            '<input type="text" class="edit-name"  style="width:120px" placeholder="이름" value="' + nm + '">' +
            '<input type="text" class="edit-phone" style="width:140px;margin-left:6px" placeholder="연락처" value="' + ph + '">';
          tr.querySelector('.col-actions').innerHTML =
            '<button class="btn-save">저장</button><button class="btn-cancel-edit">취소</button>';

          tr.querySelector('.btn-save').addEventListener('click', function(){
            var id = tr.dataset.id;
            var date = tr.querySelector('.edit-date') ? tr.querySelector('.edit-date').value : '';
            var time = tr.querySelector('.edit-time') ? tr.querySelector('.edit-time').value : '';
            var name = tr.querySelector('.edit-name') ? tr.querySelector('.edit-name').value : '';
            var phone= tr.querySelector('.edit-phone') ? tr.querySelector('.edit-phone').value : '';
            if(!date || !time){ alert('날짜/시간을 입력하세요.'); return; }
            fetch(ctx + '/api/my/reservations/' + id, {
              method:'POST',
              headers:{'Content-Type':'application/json'},
              credentials:'same-origin',
              body: JSON.stringify({ reservedDate:date, reservedTime:time, customerName:name, customerPhone:phone })
            }).then(function(rs){ return rs.json().catch(function(){return {success:false};}); })
              .then(function(j){ if(j && j.success){ location.reload(); } else { alert((j&&j.message)||'변경 실패'); } })
              .catch(function(){ alert('네트워크 오류'); });
          });

          tr.querySelector('.btn-cancel-edit').addEventListener('click', function(){ location.reload(); });
        });
      }

      // 취소
      var btnCancel = tr.querySelector('.btn-cancel');
      if(btnCancel){
        btnCancel.addEventListener('click', function(){
          if(!confirm('예약을 취소할까요?')) return;
          fetch(ctx + '/api/my/reservations/' + r.id + '/cancel', {method:'POST', credentials:'same-origin'})
            .then(function(rs){ return rs.json().catch(function(){return {success:false};}); })
            .then(function(j){ if(j && j.success){ location.reload(); } else { alert((j&&j.message)||'취소 실패'); } })
            .catch(function(){ alert('네트워크 오류'); });
        });
      }
      return tr;
    }

    // 초기 로딩
    fetch(ctx + '/api/my/reservations', {credentials:'same-origin'})
      .then(function(r){ if(!r.ok) throw new Error('HTTP '+r.status); return r.json(); })
      .then(function(list){
        if(!list || list.length===0){ empty.style.display='block'; return; }
        tbl.style.display='table';
        list.forEach(function(r){ tb.appendChild(renderRow(r)); });
      })
      .catch(function(e){ err.textContent='목록을 불러오지 못했습니다: ' + e.message; err.style.display='block'; });
  })();
  </script>
</body>
</html>
