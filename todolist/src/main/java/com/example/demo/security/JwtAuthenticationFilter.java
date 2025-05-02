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
            String userEmail = jwtTokenProvider.getUserEmailFromToken(token);

            if (userEmail == null) {
                // ✅ 토큰은 있지만 만료되었거나 유효하지 않음 → 401 응답
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 만료되었거나 유효하지 않습니다.");
                return;
            }

            // ✅ 인증 정보 설정
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userEmail, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // ❗ 유효하지 않거나 없는 경우 경고만 찍고 필터 통과 (필요에 따라 변경 가능)
            logger.warn("JWT 토큰이 없거나 유효하지 않음");
        }

        filterChain.doFilter(request, response);
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
