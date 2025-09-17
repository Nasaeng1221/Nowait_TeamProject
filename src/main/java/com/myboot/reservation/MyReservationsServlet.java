package com.myboot.reservation;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/my/reservations")
public class MyReservationsServlet extends HttpServlet {
  private final Gson gson = new Gson();
  private final ReservationDAO dao = new ReservationDAO();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/json;charset=UTF-8");
    Object mid = req.getSession().getAttribute("memberId");
    if (mid == null) { resp.setStatus(401); resp.getWriter().write("[]"); return; }
    try {
      long memberId = ((Number) mid).longValue();
      List<ReservationDAO.Reservation> list = dao.listByMember(memberId);
      resp.getWriter().write(gson.toJson(list));
    } catch (Exception e) {
      resp.setStatus(500);
      resp.getWriter().write("[]");
    }
  }
}
