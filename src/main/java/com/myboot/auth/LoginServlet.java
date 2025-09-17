package com.myboot.auth;

import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.util.*;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
  private final Gson gson = new Gson();
  private final MemberDAO dao = new MemberDAO();

  static class Req { String username; String password; }
  static class Resp { boolean success; String message; String redirect; }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/json;charset=UTF-8");

    Req r=null;
    try{
      String ct=req.getContentType();
      if(ct!=null && ct.toLowerCase().contains("application/json")) r=gson.fromJson(req.getReader(),Req.class);
      else { r=new Req(); r.username=req.getParameter("username"); r.password=req.getParameter("password"); }
    }catch(Exception ignore){}

    if(r==null || r.username==null || r.password==null){
      Resp out=new Resp(); out.success=false; out.message="Invalid input"; resp.setStatus(400); resp.getWriter().write(gson.toJson(out)); return;
    }

    try{
      Member m=dao.findByUsername(r.username);
      if(m==null){ Resp out=new Resp(); out.success=false; out.message="잘못된 인증정보"; resp.setStatus(401); resp.getWriter().write(gson.toJson(out)); return; }

      String stored=m.getPassword();
      boolean isBcrypt = stored!=null && (stored.startsWith("$2a$")||stored.startsWith("$2b$")||stored.startsWith("$2y$"));
      boolean ok = isBcrypt ? BCrypt.checkpw(r.password, stored) : stored!=null && stored.equals(r.password);
      if(!ok){ Resp out=new Resp(); out.success=false; out.message="잘못된 인증정보"; resp.setStatus(401); resp.getWriter().write(gson.toJson(out)); return; }

      HttpSession s=req.getSession(true);
      String role = (m.getRole()==null||m.getRole().isBlank()) ? "USER" : m.getRole().toUpperCase();
      s.setAttribute("memberId", m.getId());
      s.setAttribute("memberName", (m.getName()!=null && !m.getName().isBlank()) ? m.getName() : m.getUsername());
      s.setAttribute("memberRole", role);

      Map<String,Object> sm=new HashMap<>();
      sm.put("id", m.getId());
      sm.put("name", (m.getName()!=null && !m.getName().isBlank()) ? m.getName() : m.getUsername());
      sm.put("phone", m.getPhone());
      sm.put("email", m.getEmail());
      sm.put("role", role);
      s.setAttribute("member", sm);

      Resp out=new Resp(); out.success=true; out.redirect = req.getContextPath() + "/intro";
      resp.getWriter().write(gson.toJson(out));
    }catch(Exception e){ resp.setStatus(500); resp.getWriter().write("{\"success\":false,\"message\":\"서버 오류\"}"); }
  }
}
