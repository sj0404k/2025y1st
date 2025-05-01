package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ToDoListResponse {
    private Long id;
    private String contents;
    private boolean isCheck;
    private LocalDate doDate;
    private Long userId;
}