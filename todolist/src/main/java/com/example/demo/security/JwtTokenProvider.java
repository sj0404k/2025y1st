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
    // 여기부터 수정
    // 토큰에서 유저 이메일 추출
    public String getUserEmailFromToken(String token) {
        String parsedToken = token.replace("Bearer ", "");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(parsedToken)
                    .getBody();
            return claims.getSubject();  // 이메일
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            // ✅ 더 이상 예외를 던지지 않고 null 반환
            return null;
        }
    }


    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
