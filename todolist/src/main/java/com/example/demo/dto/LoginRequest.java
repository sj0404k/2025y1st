package com.example.demo.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

public class LoginRequest {

    @Getter
    @Setter
    public static class JoinMember {
        private String email;
        private String password;
        private  String name;
    }

    @Getter
    @Setter
    public static class Login {
        private String email;
        private String password;
    }

    @Getter
    @Setter
    public static class RePwd {
        private String email;
        private String newPassword;
    }

}
