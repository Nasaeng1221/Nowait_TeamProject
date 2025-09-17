package com.myboot.auth;
import java.sql.*;

public class MemberDAO {
  private static final String URL="jdbc:mariadb://192.168.72.135:3306/test";
  private static final String DB_USER="user"; private static final String DB_PASS="123";
  static { try{ Class.forName("org.mariadb.jdbc.Driver"); } catch(Exception e){ throw new RuntimeException(e); } }

  private Member map(ResultSet rs) throws SQLException {
    Member m=new Member();
    m.setId(rs.getLong("id"));
    m.setUsername(rs.getString("username"));
    m.setPassword(rs.getString("password"));
    m.setName(rs.getString("name"));
    m.setPhone(rs.getString("phone"));
    m.setEmail(rs.getString("email"));
    try { m.setRole(rs.getString("role")); } catch (SQLException ignore) {}
    return m;
  }

  public Member findByUsername(String username) throws SQLException {
    String sql="SELECT id,username,password,name,phone,email,role FROM member WHERE username=?";
    try(Connection c=DriverManager.getConnection(URL,DB_USER,DB_PASS);
        PreparedStatement ps=c.prepareStatement(sql)){
      ps.setString(1,username);
      try(ResultSet rs=ps.executeQuery()){ if(rs.next()) return map(rs); }
    } return null;
  }

  public Member findById(long id) throws SQLException {
    String sql="SELECT id,username,password,name,phone,email,role FROM member WHERE id=?";
    try(Connection c=DriverManager.getConnection(URL,DB_USER,DB_PASS);
        PreparedStatement ps=c.prepareStatement(sql)){
      ps.setLong(1,id);
      try(ResultSet rs=ps.executeQuery()){ if(rs.next()) return map(rs); }
    } return null;
  }

  public boolean createMember(String username,String hashedPassword,String name,String phone,String email,String role) throws SQLException {
    String sql="INSERT INTO member (username,password,name,phone,email,role) VALUES (?,?,?,?,?,?)";
    try(Connection c=DriverManager.getConnection(URL,DB_USER,DB_PASS);
        PreparedStatement ps=c.prepareStatement(sql)){
      ps.setString(1,username); ps.setString(2,hashedPassword);
      ps.setString(3,name); ps.setString(4,phone); ps.setString(5,email);
      ps.setString(6, (role==null||role.isBlank()) ? "USER" : role.toUpperCase());
      ps.executeUpdate(); return true;
    } catch(SQLIntegrityConstraintViolationException ex){ return false; }
  }
}
