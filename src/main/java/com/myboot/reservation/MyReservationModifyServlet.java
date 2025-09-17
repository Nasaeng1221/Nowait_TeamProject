package com.myboot.reservation;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/api/my/reservations/*")
public class MyReservationModifyServlet extends HttpServlet {
  private final Gson gson = new Gson();
  private final ReservationDAO dao = new ReservationDAO();

  static class Req { String reservedDate; String reservedTime; String customerName; String customerPhone; }
  static class Res { boolean success; String message; String redirect; }

  // /api/my/reservations/{id} (POST: 변경), /api/my/reservations/{id}/cancel (POST: 취소)
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getPathInfo(); // like "/123" or "/123/cancel"
    Object midObj = req.getSession().getAttribute("memberId");
    resp.setContentType("application/json;charset=UTF-8");
    if (midObj == null) { resp.setStatus(401); resp.getWriter().write("{\"success\":false,\"message\":\"로그인이 필요합니다\"}"); return; }

    if (path == null || path.length() <= 1) { resp.setStatus(404); resp.getWriter().write("{\"success\":false}"); return; }
    String[] parts = path.split("/");
    long id;
    try { id = Long.parseLong(parts[1]); } catch (Exception e) { resp.setStatus(400); resp.getWriter().write("{\"success\":false}"); return; }
    long memberId = ((Number) midObj).longValue();

    boolean isCancel = (parts.length >= 3 && "cancel".equalsIgnoreCase(parts[2]));

    try {
      if (isCancel) {
        int n = dao.cancel(id, memberId);
        if (n > 0) { resp.getWriter().write("{\"success\":true,\"redirect\":\"/reservations.jsp\"}"); }
        else { resp.setStatus(404); resp.getWriter().write("{\"success\":false,\"message\":\"취소할 수 없습니다\"}"); }
        return;
      }

      // 변경
      Req r = null;
      String ct = req.getContentType();
      if (ct != null && ct.toLowerCase().contains("application/json")) {
        r = gson.fromJson(req.getReader(), Req.class);
      } else {
        r = new Req();
        r.reservedDate  = req.getParameter("reservedDate"); if (r.reservedDate==null) r.reservedDate=req.getParameter("date");
        r.reservedTime  = req.getParameter("reservedTime"); if (r.reservedTime==null) r.reservedTime=req.getParameter("time");
        r.customerName  = req.getParameter("customerName");
        r.customerPhone = req.getParameter("customerPhone");
      }
      if (r == null || r.reservedDate == null || r.reservedTime == null) {
        resp.setStatus(400); resp.getWriter().write("{\"success\":false,\"message\":\"입력값을 확인하세요\"}"); return;
      }
      int n = dao.updateWhenOwner(id, memberId, Date.valueOf(r.reservedDate), r.reservedTime, r.customerName, r.customerPhone);
      if (n == -1) { resp.setStatus(409); resp.getWriter().write("{\"success\":false,\"message\":\"이미 예약된 시간입니다\"}"); return; }
      if (n > 0) { resp.getWriter().write("{\"success\":true,\"redirect\":\"/reservations.jsp\"}"); }
      else { resp.setStatus(404); resp.getWriter().write("{\"success\":false,\"message\":\"변경할 수 없습니다\"}"); }
    } catch (Exception e) {
      resp.setStatus(500); resp.getWriter().write("{\"success\":false,\"message\":\"서버 오류\"}");
    }
  }
}
