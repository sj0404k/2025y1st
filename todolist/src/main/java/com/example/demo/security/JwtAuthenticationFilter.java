package com.example.demo.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);  // 토큰 추출 메서드 호출

        // 토큰이 존재하고 유효한지 확인
        if (token != null && jwtTokenProvider.validateToken(token)) {
            try {
                String userEmail = jwtTokenProvider.getUserEmailFromToken(token);  // 토큰에서 유저 이메일 추출
                // 유저 정보 설정 (이메일로 사용자 인증)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userEmail, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);  // 인증 정보 설정
            } catch (Exception e) {
                // 예외 처리: 토큰이 잘못된 경우 로그를 남기고 Unauthorized 상태 코드 응답
                logger.error("JWT 토큰 처리 오류: {}");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;  // 필터 체인의 나머지 처리 안 함
            }
        } else {
            // 토큰이 없거나 유효하지 않으면 필터 체인의 나머지 처리를 계속 진행
            logger.warn("JWT 토큰이 없거나 유효하지 않음");
        }

        filterChain.doFilter(request, response);  // 필터 체인 진행
    }

    // Authorization 헤더에서 "Bearer" 토큰을 추출하는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 제외한 토큰 반환
        }
        return null;  // 토큰이 없으면 null 반환
    }
}
