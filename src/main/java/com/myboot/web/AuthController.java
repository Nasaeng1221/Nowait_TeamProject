package com.myboot.web;

import com.myboot.domain.Member;
import com.myboot.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(MemberRepository memberRepository, PasswordEncoder passwordEncoder){
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() { return "auth/login"; }

    @GetMapping("/register")
    public String registerForm(Model m){
        m.addAttribute("member", new Member());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute Member member, BindingResult br, Model m){
        if(member.getUsername() == null || member.getPassword() == null){
            br.reject("error.member", "아이디/비밀번호는 필수입니다.");
            return "auth/register";
        }
        if(memberRepository.existsByUsername(member.getUsername())){
            br.rejectValue("username", "error.member", "이미 사용중인 아이디입니다.");
            return "auth/register";
        }
        // BCrypt로 암호화하여 저장
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
        return "redirect:/login?registered";
    }
}
