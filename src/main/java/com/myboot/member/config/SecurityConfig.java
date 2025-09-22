package com.myboot.member.config;

import com.myboot.security.WebSessionAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSessionAuthFilter webSessionAuthFilter() {
        return new WebSessionAuthFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            // 세션→Security 브릿지 필터를 익명필터보다 먼저
            .addFilterBefore(webSessionAuthFilter(), AnonymousAuthenticationFilter.class)

            .authorizeHttpRequests(auth -> auth
                // ====== 완전 개방(헬스/핑/에코/디버그) - Ant 패턴만 사용 ======
                .requestMatchers(new AntPathRequestMatcher("/api/owner/restaurants/_health"))
                    .permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/owner/restaurants/__ping"))
                    .permitAll()
                // echo: /api/owner/restaurants/4/update/_echo 같은 형태를 모두 허용
                .requestMatchers(new AntPathRequestMatcher("/api/owner/restaurants/**/_echo"))
                    .permitAll()
                // (혹시 있을 수 있는) 내부 진단 엔드포인트
                .requestMatchers(new AntPathRequestMatcher("/api/owner/restaurants/_diag"))
                    .permitAll()
                // 디버그 핑(네가 테스트했던 경로 방어적으로 허용)
                .requestMatchers(new AntPathRequestMatcher("/api/**/DEBUG_PING_*"))
                    .permitAll()

                // ====== 정적/업로드/로그인 등 공개 ======
                .requestMatchers(new AntPathRequestMatcher("/login"))
                    .permitAll()
                .requestMatchers(new AntPathRequestMatcher("/member/login"))
                    .permitAll()
                .requestMatchers(new AntPathRequestMatcher("/signup"))
                    .permitAll()
                .requestMatchers(new AntPathRequestMatcher("/css/**"))
                    .permitAll()
                .requestMatchers(new AntPathRequestMatcher("/js/**"))
                    .permitAll()
                .requestMatchers(new AntPathRequestMatcher("/img/**"))
                    .permitAll()
                .requestMatchers(new AntPathRequestMatcher("/uploads/**"))
                    .permitAll()

                // ====== 점주/관리자 전용 API ======
                .requestMatchers(new AntPathRequestMatcher("/api/owner/**"))
                    .hasAnyRole("OWNER","ADMIN")

                // ====== 나머지는 허용(필요하면 tighten 가능) ======
                .anyRequest().permitAll()
            )

            .formLogin(form -> form.disable());

        return http.build();
    }
}

