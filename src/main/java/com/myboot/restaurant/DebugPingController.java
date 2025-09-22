package com.myboot.restaurant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class DebugPingController {
    @GetMapping("/api/DEBUG_PING_42")
    public Map<String,Object> ping(){
        return Map.of(
            "ok", true,
            "marker", "PING_42_v1"
        );
    }
}

