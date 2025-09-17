package com.myboot.restaurant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantSettingsController {

    private final DataSource dataSource;

    public RestaurantSettingsController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/{id}/settings")
    public Map<String, Object> getSettings(@PathVariable("id") long id) throws SQLException {
        try (Connection con = dataSource.getConnection()) {
            Settings s = fetch(con, "restaurant", id);
            if (s == null) s = fetch(con, "restaurants", id); // 테이블 두종류 대비

            if (s == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found");
            }

            Map<String, Object> out = new LinkedHashMap<>();
            out.put("bookingStartTime", s.start != null ? s.start : "09:00");
            out.put("bookingEndTime",   s.end   != null ? s.end   : "22:00");
            out.put("slotMinutes",      s.slot  != null ? s.slot  : 60);
            out.put("minParty",         s.minParty    != null ? s.minParty    : 2);
            out.put("maxParty",         s.maxParty    != null ? s.maxParty    : 20);
            out.put("changeCutoffMinutes", s.changeCutoff != null ? s.changeCutoff : 120);
            out.put("noShowMinutes",       s.noShow       != null ? s.noShow       : 30);
            return out;
        }
    }

    private Settings fetch(Connection con, String table, long id) throws SQLException {
        // 컬럼이 없을 수도 있으므로 SELECT 에서 존재 안하면 예외 → null 처리
        String sql = "SELECT " +
                colOrNull("booking_start_time") + " AS booking_start_time, " +
                colOrNull("booking_end_time")   + " AS booking_end_time, " +
                colOrNull("slot_minutes")       + " AS slot_minutes, " +
                colOrNull("min_party")          + " AS min_party, " +
                colOrNull("max_party")          + " AS max_party, " +
                colOrNull("change_cutoff_minutes") + " AS change_cutoff_minutes, " +
                colOrNull("no_show_minutes")       + " AS no_show_minutes " +
                "FROM " + table + " WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Settings s = new Settings();
                s.start = getStringSafe(rs, "booking_start_time");
                s.end   = getStringSafe(rs, "booking_end_time");
                s.slot  = getIntSafe(rs, "slot_minutes");
                s.minParty = getIntSafe(rs, "min_party");
                s.maxParty = getIntSafe(rs, "max_party");
                s.changeCutoff = getIntSafe(rs, "change_cutoff_minutes");
                s.noShow = getIntSafe(rs, "no_show_minutes");
                return s;
            }
        } catch (SQLSyntaxErrorException e) {
            // 테이블에 해당 컬럼이 없으면 여기로 들어올 수 있음 → null 반환해서 다른 테이블(or 기본값)로 처리
            return null;
        }
    }

    private static String getStringSafe(ResultSet rs, String col) {
        try { return rs.getString(col); } catch (Exception e) { return null; }
    }
    private static Integer getIntSafe(ResultSet rs, String col) {
        try {
            Object o = rs.getObject(col);
            if (o == null) return null;
            if (o instanceof Number) return ((Number) o).intValue();
            return Integer.valueOf(String.valueOf(o));
        } catch (Exception e) { return null; }
    }

    // 컬럼이 없을 때도 컴파일되게 하기 위한 트릭: 존재하지 않으면 NULL을 선택
    private static String colOrNull(String col) {
        // MariaDB는 존재하지 않는 컬럼을 직접 SELECT하면 에러 → TRY_CATCH가 안되므로 별도 처리 불가.
        // 여기서는 에러를 캐치해서 null 반환하는 전략을 사용 (위 catch SQLSyntaxErrorException).
        return col;
    }

    private static class Settings {
        String start, end;
        Integer slot, minParty, maxParty, changeCutoff, noShow;
    }
}
