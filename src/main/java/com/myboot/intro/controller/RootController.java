package com.myboot.intro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    @GetMapping("/")
    public String home() {
        return "redirect:/intro"; // 루트 들어오면 /intro로 보냄
    }
}
