package com.example.demo.controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.domain.User;
import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/main")
public class LoginController {
    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;
    private final JwtTokenProvider jwtTokenProvider;

    //회원가입
    @PostMapping(value = "/signup")
    public ResponseEntity<String> signup(@RequestBody LoginRequest.JoinMember request) { // 회원 중복 확인 필요 v 테스트 완 // 서비스 분리 필요
        // 1. 아이디 중복 체크
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(400).body("이미 사용 중인 아이디입니다.");
        }
        // 2. 비밀번호 암호화
        request.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
        User user = User.builder()
                .email(request.getEmail())
                .pwd(request.getPassword())
                .name(request.getName())
                .build();
        userRepository.save(user);
        return ResponseEntity.ok("회원가입 완료");
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest.Login request) {
        // 이메일로 사용자 찾기
        User foundUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 확인
        if (!securityConfig.passwordEncoder().matches(request.getPassword(), foundUser.getPwd())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(foundUser.getEmail());

        // JWT 토큰을 포함한 응답 반환
        return ResponseEntity.ok(new JwtResponse(token));
    }


    //비밀번호 재설정
        @PostMapping(value = "/reset-password")
        public ResponseEntity<String> resetPassword(@RequestBody LoginRequest.RePwd request) {
            // 이메일로 사용자 찾기
            User foundUser = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 새로운 비밀번호 암호화 후 업데이트
            String encodedPassword = securityConfig.passwordEncoder().encode(request.getNewPassword());
            foundUser.setPwd(encodedPassword);
            userRepository.save(foundUser);

            return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
        }


}
