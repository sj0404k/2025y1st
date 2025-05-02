package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class ToDoListRequest {
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
        private Long id;
        private Boolean isCheck;
    }

}
