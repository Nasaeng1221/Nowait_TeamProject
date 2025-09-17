package com.myboot.restaurant;

import java.sql.*;
import java.util.*;

public class OwnerRestaurantDAO {
  private static final String URL="jdbc:mariadb://192.168.72.135:3306/test";
  private static final String DB_USER="user"; private static final String DB_PASS="123";
  static { try{ Class.forName("org.mariadb.jdbc.Driver"); } catch(Exception e){ throw new RuntimeException(e); } }

  public static class Rest {
    public long id; public String name, phone, address, mainMenu, openHours, image;
  }

  public List<Rest> listByOwner(long ownerId) throws SQLException {
    String sql = "SELECT id,name,phone,address,main_menu,open_hours,image FROM restaurant WHERE owner_member_id=? ORDER BY id DESC";
    List<Rest> list=new ArrayList<>();
    try(Connection c=DriverManager.getConnection(URL,DB_USER,DB_PASS);
        PreparedStatement ps=c.prepareStatement(sql)){
      ps.setLong(1,ownerId);
      try(ResultSet rs=ps.executeQuery()){
        while(rs.next()){
          Rest r=new Rest();
          r.id=rs.getLong("id"); r.name=rs.getString("name"); r.phone=rs.getString("phone");
          r.address=rs.getString("address"); r.mainMenu=rs.getString("main_menu");
          r.openHours=rs.getString("open_hours"); r.image=rs.getString("image");
          list.add(r);
        }
      }
    } return list;
  }

  public long create(long ownerId, String name, String phone, String address, String mainMenu, String openHours, String image) throws SQLException {
    String sql="INSERT INTO restaurant (owner_member_id,name,phone,address,main_menu,open_hours,image) VALUES (?,?,?,?,?,?,?)";
    try(Connection c=DriverManager.getConnection(URL,DB_USER,DB_PASS);
        PreparedStatement ps=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
      ps.setLong(1, ownerId); ps.setString(2,name); ps.setString(3,phone);
      ps.setString(4,address); ps.setString(5,mainMenu); ps.setString(6,openHours); ps.setString(7,image);
      ps.executeUpdate(); try(ResultSet rs=ps.getGeneratedKeys()){ if(rs.next()) return rs.getLong(1); }
    } return 0;
  }

  public int update(long ownerId, long id, String name, String phone, String address, String mainMenu, String openHours, String image) throws SQLException {
    String sql="UPDATE restaurant SET name=?, phone=?, address=?, main_menu=?, open_hours=?, image=? WHERE id=? AND owner_member_id=?";
    try(Connection c=DriverManager.getConnection(URL,DB_USER,DB_PASS);
        PreparedStatement ps=c.prepareStatement(sql)){
      ps.setString(1,name); ps.setString(2,phone); ps.setString(3,address);
      ps.setString(4,mainMenu); ps.setString(5,openHours); ps.setString(6,image);
      ps.setLong(7,id); ps.setLong(8,ownerId);
      return ps.executeUpdate();
    }
  }
}
