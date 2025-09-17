package com.myboot.member.controller;

import com.myboot.domain.Member;
import com.myboot.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /** 회원가입 처리 */
    @PostMapping("/signup")
    public String signup(Member member) {
        memberService.register(member);
        return "redirect:/member/login";
    }

    @GetMapping("/member/login")
    public String loginForm() {
        return "member/login"; // ✅ 이렇게 수정
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "member/signup"; // ✅ 이렇게 수정
    }

    /** 로그인 처리 */
    @PostMapping("/member/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session,
            Model model) {
        Member loginMember = memberService.login(username, password);
        if (loginMember != null) {
            session.setAttribute("loginMember", loginMember);
            return "redirect:/";
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "member/login";
        }
    }

    /** 로그아웃 */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
