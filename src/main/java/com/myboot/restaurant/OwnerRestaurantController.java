package com.myboot.restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/owner/restaurants")
public class OwnerRestaurantController {

    private static final Logger log = LoggerFactory.getLogger(OwnerRestaurantController.class);

    @Value("${app.upload.root:/mnt/nowait-uploads}")
    String uploadRoot;

    private final DataSource dataSource;
    public OwnerRestaurantController(DataSource dataSource){ this.dataSource = dataSource; }

    /* ========================== 공통 유틸 ========================== */

    private long getSessionMemberId(HttpServletRequest req){
        Object mid = (req.getSession(false)==null)? null : req.getSession(false).getAttribute("memberId");
        try {
            return (mid instanceof Number) ? ((Number) mid).longValue() :
                    (mid!=null ? Long.parseLong(String.valueOf(mid)) : 0L);
        } catch (Exception ignore){ return 0L; }
    }
    private static String emptyToNull(String s){ return (s==null || s.isBlank())? null : s; }
    private static String nz(String s){ return s==null? "" : s; }

    private boolean isOwnerOf(long restaurantId, long memberId) throws Exception {
        String sql = "SELECT COUNT(*) FROM restaurant WHERE id=? AND owner_member_id=?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, restaurantId);
            ps.setLong(2, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /* ========================== 조회 ========================== */

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String,Object>> list(HttpServletRequest req) throws Exception {
        long ownerId = getSessionMemberId(req);
        String sql = """
            SELECT id,name,phone,address,main_menu AS mainMenu,
                   open_hours AS openHours,image
              FROM restaurant
             WHERE owner_member_id=?
             ORDER BY id DESC
        """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String,Object>> out = new ArrayList<>();
                while (rs.next()){
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("name", nz(rs.getString("name")));
                    m.put("phone", nz(rs.getString("phone")));
                    m.put("address", nz(rs.getString("address")));
                    m.put("mainMenu", nz(rs.getString("mainMenu")));
                    m.put("openHours", nz(rs.getString("openHours")));
                    m.put("image", nz(rs.getString("image")));
                    out.add(m);
                }
                return out;
            }
        }
    }

    /* ========================== 신규 등록 (multipart/선택) ========================== */
    @PostMapping(path="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Object>> createRestaurantMultipart(
            HttpServletRequest req,
            @RequestParam String name,
            @RequestParam(required=false) String phone,
            @RequestParam(required=false) String address,
            @RequestParam(required=false) String mainMenu,
            @RequestParam(required=false) String openHours,
            @RequestParam(name="imageFile", required=false) MultipartFile imageFile
    ) {
        try {
            long ownerId = getSessionMemberId(req);
            long restaurantId = insertRestaurant(ownerId, name, phone, address, mainMenu, openHours);
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageKey = saveImage(ownerId, restaurantId, imageFile);
                updateImageColumn(restaurantId, imageKey);
            }
            return ResponseEntity.ok(Map.of("success", true, "id", restaurantId));
        } catch (Exception e) {
            log.error("createRestaurant error", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "error", msg(e)));
        }
    }

    /* ========================== 수정 (multipart 한정) ========================== */
    @PostMapping(path="/{restaurantId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Object>> updateRestaurant(
            HttpServletRequest req,
            @PathVariable long restaurantId,
            @RequestParam(required=false) String name,
            @RequestParam(required=false) String phone,
            @RequestParam(required=false) String address,
            @RequestParam(required=false) String mainMenu,
            @RequestParam(required=false) String openHours,
            @RequestParam(name="imageFile", required=false) MultipartFile imageFile
    ) {
        final String marker = "updateRestaurant v2";
        long sessionMemberId = getSessionMemberId(req);
        boolean hasImage = (imageFile != null && !imageFile.isEmpty());
        log.info("{}: ENTER id={}, sessionMemberId={}, name={}, openHours={}, hasImage={}",
                marker, restaurantId, sessionMemberId, name, openHours, hasImage);

        try {
            if (sessionMemberId <= 0) {
                log.warn("{}: UNAUTHORIZED (no session)", marker);
                return ResponseEntity.status(401).body(Map.of("success", false, "error", "UNAUTHORIZED"));
            }
            if (!isOwnerOf(restaurantId, sessionMemberId)) {
                log.warn("{}: NOT_FOUND_OR_FORBIDDEN (id={}, sessionMemberId={})", marker, restaurantId, sessionMemberId);
                return ResponseEntity.status(404).body(Map.of("success", false, "error", "NOT_FOUND_OR_FORBIDDEN"));
            }

            // 1) 텍스트 필드 업데이트
            updateRestaurantFields(restaurantId, name, phone, address, mainMenu, openHours);

            // 2) 이미지 있으면 저장 + 컬럼 반영
            if (hasImage) {
                String imageKey = saveImage(sessionMemberId, restaurantId, imageFile);
                updateImageColumn(restaurantId, imageKey);
            }

            log.info("{}: DONE id={}", marker, restaurantId);
            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            log.error("{}: updateRestaurant error", marker, e);
            return ResponseEntity.status(500).body(Map.of("success", false, "error", msg(e)));
        }
    }

    /* ========================== 진단용 엔드포인트 ========================== */

    @GetMapping(path="/_boom", produces = MediaType.APPLICATION_JSON_VALUE)
public Map<String,Object> boom() {
    throw new RuntimeException("BOOM test from /api/owner/restaurants/_boom");
}

    // 현재 세션과 대상 id, 소유자 일치 여부를 반환 (로그를 강제로 남김)
    @PostMapping(path="/{restaurantId}/update/_echo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> echoUpdate(
            HttpServletRequest req,
            @PathVariable long restaurantId,
            @RequestParam Map<String,String> allParams,
            @RequestParam(name="imageFile", required=false) MultipartFile imageFile
    ){
        String marker = "updateRestaurant v2 _echo";
        long sessionMemberId = getSessionMemberId(req);
        boolean hasImage = (imageFile != null && !imageFile.isEmpty());
        boolean owner;
        try { owner = isOwnerOf(restaurantId, sessionMemberId); }
        catch(Exception e){ owner = false; }

        log.info("{}: id={}, sessionMemberId={}, owner={}, params={}, hasImage={}",
                marker, restaurantId, sessionMemberId, owner, allParams, hasImage);

        Map<String,Object> out = new LinkedHashMap<>();
        out.put("success", true);
        out.put("id", restaurantId);
        out.put("sessionMemberId", sessionMemberId);
        out.put("isOwner", owner);
        out.put("params", allParams);
        out.put("hasImage", hasImage);
        return out;
    }

    // 세션/소유권 진단
    @GetMapping(path="/_diag", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> diag(HttpServletRequest req) throws Exception {
        long sessionMemberId = getSessionMemberId(req);
        Map<String,Object> out = new LinkedHashMap<>();
        out.put("sessionMemberId", sessionMemberId);
        return out;
    }

    /* ========================== DB 작업 ========================== */

    private long insertRestaurant(long ownerId, String name, String phone, String address, String mainMenu, String openHours) throws Exception {
        String sql = """
            INSERT INTO restaurant(owner_member_id,name,phone,address,main_menu,open_hours)
            VALUES(?,?,?,?,?,?)
        """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, ownerId);
            ps.setString(2, emptyToNull(name));
            ps.setString(3, emptyToNull(phone));
            ps.setString(4, emptyToNull(address));
            ps.setString(5, emptyToNull(mainMenu));
            ps.setString(6, emptyToNull(openHours));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new IllegalStateException("Insert failed");
    }

    private void updateRestaurantFields(long id, String name, String phone, String address, String mainMenu, String openHours) throws Exception {
        Map<String, String> fields = new LinkedHashMap<>();
        if (name != null)     fields.put("name", name);
        if (phone != null)    fields.put("phone", phone);
        if (address != null)  fields.put("address", address);
        if (mainMenu != null) fields.put("main_menu", mainMenu);
        if (openHours != null)fields.put("open_hours", openHours);
        if (fields.isEmpty()) return;

        StringBuilder sb = new StringBuilder("UPDATE restaurant SET ");
        int i = 0;
        for (String col : fields.keySet()) {
            if (i++>0) sb.append(", ");
            sb.append(col).append("=?");
        }
        sb.append(" WHERE id=?");

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sb.toString())) {
            int idx=1;
            for (String val : fields.values()) ps.setString(idx++, emptyToNull(val));
            ps.setLong(idx, id);
            int rows = ps.executeUpdate();
            log.info("updateRestaurant v2: rowsUpdated={} (id={}, ownerId=?)", rows, id); // ownerId는 별도 쿼리 필요
        }
    }

    private void updateImageColumn(long id, String imageKey) throws Exception {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE restaurant SET image=? WHERE id=?")) {
            ps.setString(1, imageKey);
            ps.setLong(2, id);
            int rows = ps.executeUpdate();
            log.info("updateRestaurant v2: rowsImageUpdated={} (id={}, ownerId=?)", rows, id);
        }
    }

    private String saveImage(long ownerId, long restaurantId, MultipartFile file) throws Exception {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')).toLowerCase() : "";
        if (!(ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") || ext.equals(".webp"))) {
            throw new IllegalArgumentException("unsupported file type");
        }
        String safeName = "r"+restaurantId+"_"+System.currentTimeMillis()+ext;
        Path dir = Paths.get(uploadRoot, String.valueOf(ownerId), String.valueOf(restaurantId));
        Files.createDirectories(dir);
        Path dest = dir.resolve(safeName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            try {
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-r--r--");
                Files.setPosixFilePermissions(dest, perms);
            } catch (UnsupportedOperationException | SecurityException ignored) {}
        }
        return ownerId + "/" + restaurantId + "/" + safeName;
    }

    private static String msg(Throwable t){
        if (t==null) return "unknown";
        String m = t.getMessage();
        if (m!=null && !m.isBlank()) return m;
        return t.getClass().getSimpleName();
    }
}

