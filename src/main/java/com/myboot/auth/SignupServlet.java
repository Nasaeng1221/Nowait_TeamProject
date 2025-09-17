package com.myboot.auth;

import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

@WebServlet("/api/signup")
public class SignupServlet extends HttpServlet {
  private final Gson gson=new Gson();
  private final MemberDAO dao=new MemberDAO();

  static class Req { String username; String password; String name; String phone; String email; String role; }
  static class Res { boolean success; String message; String redirect; }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/json;charset=UTF-8");
    Req r=null; String ct=req.getContentType();
    try{
      if(ct!=null && ct.toLowerCase().contains("application/json")) r=gson.fromJson(req.getReader(),Req.class);
      else { r=new Req();
        r.username=req.getParameter("username"); r.password=req.getParameter("password");
        r.name=req.getParameter("name"); r.phone=req.getParameter("phone"); r.email=req.getParameter("email");
        r.role=req.getParameter("role");
      }
    }catch(Exception ignore){}

    if(r==null || r.username==null || r.password==null || r.name==null){
      resp.setStatus(400); resp.getWriter().write("{\"success\":false,\"message\":\"입력값 확인\"}"); return;
    }
    String role = (r.role==null||r.role.isBlank())? "USER" : r.role.toUpperCase();
    if(!role.equals("USER") && !role.equals("OWNER") && !role.equals("ADMIN")) role="USER";

    try{
      String hashed = BCrypt.hashpw(r.password, BCrypt.gensalt(10));
      boolean ok = dao.createMember(r.username, hashed, r.name, r.phone, r.email, role);
      if(ok){ resp.getWriter().write("{\"success\":true,\"redirect\":\"/login.jsp\"}"); }
      else { resp.setStatus(409); resp.getWriter().write("{\"success\":false,\"message\":\"이미 사용중인 아이디\"}"); }
    }catch(Exception e){ resp.setStatus(500); resp.getWriter().write("{\"success\":false,\"message\":\"서버 오류\"}"); }
  }
}
