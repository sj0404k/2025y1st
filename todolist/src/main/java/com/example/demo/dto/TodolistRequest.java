package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class TodolistRequest {
    @Getter
    @Setter
    public static class Creatlist{
//        private String em;
        private String contents;
        private LocalDate date;
    }

    @Setter
    @Getter
    public static class Updatelist{
        private Long id;
        private String contents;
        private LocalDate date;
    }

    @Setter
    @Getter
    public static class DateList{
        private LocalDate date;
    }

    @Setter
    @Getter
    public static class CheckBox{
        private Long Id;
        private Boolean isCheck;
    }

}
