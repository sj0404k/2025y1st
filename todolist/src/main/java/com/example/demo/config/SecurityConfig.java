package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;  // JWT 인증 필터
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable  // CSRF 방어 비활성화 (API 서버에서는 일반적으로 끕니다)
//                )
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/main/login", "/main/signup", "/main/reset-password")  // 로그인, 회원가입, 비밀번호 재설정은 인증 없이 허용
//                        .permitAll()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")  // Swagger 관련 URL도 예외 처리
//                        .permitAll()
//                        .anyRequest()  // 나머지 API는 인증 필요
//                        .authenticated()
//                )
//                .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);  // JWT 인증 필터 추가
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();  // 패스워드 암호화 방식
//    }
//}
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth  // 변경된 부분
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());  // 변경된 부분 (람다식 사용)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}