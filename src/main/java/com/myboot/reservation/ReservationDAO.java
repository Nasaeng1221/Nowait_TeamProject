package com.myboot.reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;            // java.sql.Date만 사용
import java.util.List;
import java.util.ArrayList;

public class ReservationDAO {
  private static final String URL = "jdbc:mariadb://192.168.72.135:3306/test";
  private static final String DB_USER = "user";
  private static final String DB_PASS = "123";
  static { try { Class.forName("org.mariadb.jdbc.Driver"); } catch (Exception e) { throw new RuntimeException(e); } }

  // ---------- 생성 ----------
  public long create(long restaurantId, long memberId, String name, String phone, Date date, String time) throws SQLException {
    try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS)) {
      // 같은 식당/날짜/시간 중복(취소 제외) 방지
      try (PreparedStatement ck = c.prepareStatement(
        "SELECT id FROM reservation WHERE restaurant_id=? AND reserved_date=? AND reserved_time=? " +
        "AND (status IS NULL OR UPPER(status) NOT IN ('CANCELED','CANCELLED')) LIMIT 1")) {
        ck.setLong(1, restaurantId); ck.setDate(2, date); ck.setString(3, time);
        try (ResultSet rs = ck.executeQuery()) { if (rs.next()) return -1; }
      }
      try (PreparedStatement ps = c.prepareStatement(
        "INSERT INTO reservation (restaurant_id, member_id, customer_name, customer_phone, reserved_date, reserved_time, status) " +
        "VALUES (?,?,?,?,?,?, 'CONFIRMED')",
        Statement.RETURN_GENERATED_KEYS)) {
        ps.setLong(1, restaurantId); ps.setLong(2, memberId);
        ps.setString(3, name); ps.setString(4, phone);
        ps.setDate(5, date); ps.setString(6, time);
        ps.executeUpdate();
        try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getLong(1); }
      }
    }
    return 0;
  }

  // ---------- 단건 조회(본인 것만) ----------
  public Reservation findByIdForMember(long id, long memberId) throws SQLException {
    String sql = "SELECT * FROM reservation WHERE id=? AND member_id=?";
    try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, id); ps.setLong(2, memberId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Reservation r = new Reservation();
          r.id = rs.getLong("id"); r.restaurantId = rs.getLong("restaurant_id"); r.memberId = rs.getLong("member_id");
          r.customerName = rs.getString("customer_name"); r.customerPhone = rs.getString("customer_phone");
          r.reservedDate = rs.getDate("reserved_date"); r.reservedTime = rs.getString("reserved_time");
          r.createdAt = rs.getTimestamp("created_at"); r.status = rs.getString("status");
          return r;
        }
      }
    }
    return null;
  }

  // ---------- 일자/시간/연락처 변경 ----------
  public int updateWhenOwner(long id, long memberId, Date date, String time, String name, String phone) throws SQLException {
    try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS)) {
      // 본인 예약/식당 확인
      Reservation mine = findByIdForMember(id, memberId);
      if (mine == null) return 0;

      // 같은 식당/날짜/시간 중복 체크(본인 예약 제외)
      try (PreparedStatement ck = c.prepareStatement(
        "SELECT id FROM reservation WHERE restaurant_id=? AND reserved_date=? AND reserved_time=? AND id<>? " +
        "AND (status IS NULL OR UPPER(status) NOT IN ('CANCELED','CANCELLED')) LIMIT 1")) {
        ck.setLong(1, mine.restaurantId); ck.setDate(2, date); ck.setString(3, time); ck.setLong(4, id);
        try (ResultSet rs = ck.executeQuery()) { if (rs.next()) return -1; }
      }

      try (PreparedStatement ps = c.prepareStatement(
        "UPDATE reservation SET reserved_date=?, reserved_time=?, customer_name=?, customer_phone=?, status='CONFIRMED' " +
        "WHERE id=? AND member_id=?")) {
        ps.setDate(1, date); ps.setString(2, time);
        ps.setString(3, (name!=null && !name.isEmpty()) ? name : mine.customerName);
        ps.setString(4, (phone!=null && !phone.isEmpty()) ? phone : mine.customerPhone);
        ps.setLong(5, id); ps.setLong(6, memberId);
        return ps.executeUpdate();
      }
    }
  }

  // ---------- 취소 ----------
  public int cancel(long id, long memberId) throws SQLException {
    try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
         PreparedStatement ps = c.prepareStatement(
           "UPDATE reservation SET status='CANCELED' " +
           "WHERE id=? AND member_id=? AND (status IS NULL OR UPPER(status) NOT IN ('CANCELED','CANCELLED'))")) {
      ps.setLong(1, id); ps.setLong(2, memberId);
      return ps.executeUpdate();
    }
  }

  // ---------- 목록 ----------
  public List<Reservation> listByMember(long memberId) throws SQLException {
    String sql = """
      SELECT r.*,
             COALESCE(rt.name, rts.name) AS restaurant_name
        FROM reservation r
        LEFT JOIN restaurant  rt  ON r.restaurant_id = rt.id
        LEFT JOIN restaurants rts ON r.restaurant_id = rts.id
       WHERE r.member_id = ?
       ORDER BY r.created_at DESC
    """;
    List<Reservation> list = new ArrayList<>();
    try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, memberId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Reservation r = new Reservation();
          r.id = rs.getLong("id"); r.restaurantId = rs.getLong("restaurant_id"); r.memberId = rs.getLong("member_id");
          r.customerName = rs.getString("customer_name"); r.customerPhone = rs.getString("customer_phone");
          r.reservedDate = rs.getDate("reserved_date"); r.reservedTime = rs.getString("reserved_time");
          r.createdAt = rs.getTimestamp("created_at"); r.status = rs.getString("status");
          r.restaurantName = rs.getString("restaurant_name");
          list.add(r);
        }
      }
    }
    return list;
  }

  // 직렬화용 간단 모델
  public static class Reservation {
    public long id, restaurantId, memberId;
    public String customerName, customerPhone, reservedTime, status, restaurantName;
    public Date reservedDate;
    public Timestamp createdAt;
  }
}
