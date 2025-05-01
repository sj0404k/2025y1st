package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 환경 변수나 properties 파일로부터 SECRET_KEY를 가져오는 것이 좋습니다.
    private final String secretKey = "236979CB6F1AD6B6A6184A31E6BE37DB3818CC36871E26235DD67DCFE4041492";
    private static final long VALIDITY_IN_MS = 1000 * 60 * 60;  // 1시간

    // JWT 생성
    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_IN_MS);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());  // secretKey를 바이트 배열로 변환하여 서명용 키 생성

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    // 토큰에서 유저 이메일 추출
    public String getUserEmailFromToken(String token) {
        String parsedToken = token.replace("Bearer ", "");
        try {
            Claims claims = Jwts.parserBuilder()  // deprecated된 parser() 대신 parserBuilder() 사용
                    .setSigningKey(secretKey.getBytes())  // secretKey를 바이트 배열로 변환하여 사용
                    .build()
                    .parseClaimsJws(parsedToken)
                    .getBody();
            return claims.getSubject();  // 이메일
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT 토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            throw new RuntimeException("JWT 토큰 파싱 오류");
        }
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            // 토큰이 만료되었는지, 서명이 유효한지 확인
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())  // secretKey를 바이트 배열로 변환하여 사용
                    .build()
                    .parseClaimsJws(token);  // 유효하지 않으면 예외 발생
            return true;
        } catch (ExpiredJwtException e) {
            // 만료된 토큰
            return false;
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰
            return false;
        } catch (Exception e) {
            // 기타 예외
            return false;
        }
    }
}
