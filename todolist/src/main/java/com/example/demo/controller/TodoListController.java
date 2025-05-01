package com.example.demo.controller;

import com.example.demo.domain.ToDoList;
import com.example.demo.domain.User;
import com.example.demo.dto.TodolistRequest;
import com.example.demo.repository.ToDoListRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.TodolistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/todolist")
public class TodoListController {

    private final TodolistService todolistService;
    private final ToDoListRepository toDoListRepository;
    private final UserRepository userRepository;  // UserRepository 추가
    private final JwtTokenProvider jwtTokenProvider;

    // 할일 추가
    @PostMapping
    public ToDoList create(@RequestHeader("Authorization") String token, @RequestBody TodolistRequest.Creatlist request) {
        // Bearer 제거
        String parsedToken = token.replace("Bearer ", "");

        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getUserEmailFromToken(parsedToken);

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        // 할 일 목록 생성
        ToDoList toDoList = ToDoList.builder()
                .contents(request.getContents())
                .isCheck(false)
                .doDate(request.getDate())
                .user(user)
                .build();

        return toDoListRepository.save(toDoList);
    }

    // 체크박스
    @PostMapping(value = "/checkBox")
    public ToDoList checkbox(@RequestBody TodolistRequest.CheckBox request){
        Optional<ToDoList> optional = Optional.ofNullable(toDoListRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("no id")));
        ToDoList todo = optional.get();
        todo.setIsCheck(request.getIsCheck());
        return toDoListRepository.save(todo);
    }
    //수정
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody TodolistRequest.Updatelist request) {
        try {
            ToDoList todo = toDoListRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("해당 ID의 할 일이 없습니다."));
            todo.setContents(request.getContents());
            todo.setDoDate(request.getDate());
            return ResponseEntity.ok(toDoListRepository.save(todo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("에러 발생: " + e.getMessage());
        }
    }

    //전체 조회
    @GetMapping
    public List<ToDoList> getAll() {
        return toDoListRepository.findAll();
    }
    //날짜 조회
    @GetMapping("/date/{date}")
    public List<ToDoList> getByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date); // YYYY-MM-DD 날짜 별로
        return toDoListRepository.findAllByDoDate(localDate);
    }

    //삭제   삭제시 유저 확인 필요

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        toDoListRepository.deleteById(id);
    }


}
