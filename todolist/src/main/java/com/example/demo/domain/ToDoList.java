package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToDoList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String contents;

    @Column
    private Boolean isCheck;

    @Column
    private LocalDate doDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // ToDoList 테이블에 user_id 컬럼 생성
    private User user; // 작성한 User

}
