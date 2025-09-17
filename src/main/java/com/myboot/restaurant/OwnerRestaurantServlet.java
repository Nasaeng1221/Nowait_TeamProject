package com.myboot.restaurant;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

@WebServlet(urlPatterns={"/api/owner/restaurants","/api/owner/restaurants/*"})
public class OwnerRestaurantServlet extends HttpServlet {
  private final OwnerRestaurantDAO dao=new OwnerRestaurantDAO();
  private final Gson gson=new Gson();

  private boolean isOwner(HttpSession s){
    Object role = s.getAttribute("memberRole");
    return role!=null && ("OWNER".equals(role.toString()) || "ADMIN".equals(role.toString()));
  }

  @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
    resp.setContentType("application/json;charset=UTF-8");
    HttpSession s=req.getSession(false);
    if(s==null || s.getAttribute("memberId")==null || !isOwner(s)){ resp.setStatus(403); resp.getWriter().write("[]"); return; }
    try{
      long ownerId=((Number)s.getAttribute("memberId")).longValue();
      var list=dao.listByOwner(ownerId);
      resp.getWriter().write(gson.toJson(list));
    }catch(Exception e){ resp.setStatus(500); resp.getWriter().write("[]"); }
  }

  @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
    resp.setContentType("application/json;charset=UTF-8");
    HttpSession s=req.getSession(false);
    if(s==null || s.getAttribute("memberId")==null || !isOwner(s)){ resp.setStatus(403); resp.getWriter().write("{\"success\":false}"); return; }
    long ownerId=((Number)s.getAttribute("memberId")).longValue();

    String path=req.getPathInfo(); // null or "/{id}"
    String ct=req.getContentType();
    Map<String,String> p=new HashMap<>();

    try{
      if(ct!=null && ct.toLowerCase().contains("application/json")){
        Map<?,?> m = gson.fromJson(req.getReader(), Map.class);
        if(m!=null){
          for(Map.Entry<?,?> e : m.entrySet()){
            p.put(String.valueOf(e.getKey()), e.getValue()==null ? null : String.valueOf(e.getValue()));
          }
        }
      }else{
        Map<String,String[]> pm=req.getParameterMap();
        for(Map.Entry<String,String[]> e : pm.entrySet()){
          String[] v=e.getValue();
          p.put(e.getKey(), (v!=null && v.length>0) ? v[0] : null);
        }
      }

      String name     = p.getOrDefault("name","");
      String phone    = p.getOrDefault("phone","");
      String address  = p.getOrDefault("address","");
      String mainMenu = p.getOrDefault("mainMenu","");
      String openHours= p.getOrDefault("openHours","");
      String image    = p.getOrDefault("image","");

      if(path==null || path.length()<=1){
        long id=dao.create(ownerId,name,phone,address,mainMenu,openHours,image);
        if(id>0){ resp.getWriter().write("{\"success\":true}"); }
        else { resp.setStatus(500); resp.getWriter().write("{\"success\":false}"); }
      }else{
        long rid=Long.parseLong(path.substring(1));
        int n=dao.update(ownerId,rid,name,phone,address,mainMenu,openHours,image);
        if(n>0){ resp.getWriter().write("{\"success\":true}"); }
        else { resp.setStatus(404); resp.getWriter().write("{\"success\":false}"); }
      }
    }catch(Exception e){
      resp.setStatus(500); resp.getWriter().write("{\"success\":false}");
    }
  }
}
