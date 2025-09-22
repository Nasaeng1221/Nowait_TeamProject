package com.myboot.restaurant;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.*;

@RestController
@RequestMapping("/api/owner/restaurants")
public class RestaurantImageController {

    @Value("${app.upload.root:/mnt/nowait-uploads}")
    String uploadRoot;

    private final DataSource dataSource;
    public RestaurantImageController(DataSource dataSource) { this.dataSource = dataSource; }

    @PostMapping("/{restaurantId}/image")
    public ResponseEntity<?> upload(
            @PathVariable long restaurantId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("ownerId") long ownerId
    ) throws Exception {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("{\"success\":false,\"message\":\"empty file\"}");

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')).toLowerCase() : "";
        if (!(ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") || ext.equals(".webp"))) {
            return ResponseEntity.badRequest().body("{\"success\":false,\"message\":\"unsupported file type\"}");
        }

        String safeName = "r"+restaurantId+"_"+System.currentTimeMillis()+ext;
        Path dir = Paths.get(uploadRoot, String.valueOf(ownerId), String.valueOf(restaurantId));
        Files.createDirectories(dir);
        Path dest = dir.resolve(safeName);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            Files.setPosixFilePermissions(dest, PosixFilePermissions.fromString("rw-r--r--"));
        } catch (UnsupportedOperationException ignore) {
            // Windows/NFS 권한세팅 불가시 무시
        }

        String imageKey = ownerId + "/" + restaurantId + "/" + safeName;

        // restaurant 또는 restaurants 테이블에 반영 (둘 중 하나 존재)
        int updated = 0;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE restaurant SET image=? WHERE id=?")) {
            ps.setString(1, imageKey);
            ps.setLong(2, restaurantId);
            updated = ps.executeUpdate();
        } catch (SQLSyntaxErrorException e) { /* 테이블 없으면 다음으로 */ }

        if (updated == 0) {
            try (Connection con = dataSource.getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE restaurants SET image=? WHERE id=?")) {
                ps.setString(1, imageKey);
                ps.setLong(2, restaurantId);
                ps.executeUpdate();
            }
        }

        return ResponseEntity.ok("{\"success\":true,\"imageKey\":\""+imageKey+"\"}");
    }
}
