package com.example.demo.controller;

import com.example.demo.domain.ToDoList;
import com.example.demo.domain.User;
import com.example.demo.dto.ToDoListRequest;
import com.example.demo.dto.ToDoListResponse;
import com.example.demo.repository.ToDoListRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.TodolistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.List;

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
    public ResponseEntity<String> create(@RequestHeader("Authorization") String token, @RequestBody ToDoListRequest.Creatlist request) {

        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getUserEmailFromToken(token);

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자를 찾을 수 없습니다."));

        // 할 일 목록 생성
        ToDoList toDoList = ToDoList.builder()
                .contents(request.getContents())
                .isCheck(false)
                .doDate(request.getDate())
                .user(user)
                .build();
        toDoListRepository.save(toDoList);
        return ResponseEntity.ok("글 작성 성공");
    }

    // 체크박스
    @PostMapping(value = "/checkBox")
    public ResponseEntity<String> checkbox(@RequestHeader("Authorization") String token,
                                           @RequestBody ToDoListRequest.CheckBox request) {

        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getUserEmailFromToken(token);

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자를 찾을 수 없습니다."));

        // 사용자 ID와 요청 ID로 할 일 항목 조회

        if(toDoListRepository.findAllByUserIdAndId(user.getId(), request.getId()).isPresent()) {
            ToDoList todo = toDoListRepository.findAllByUserIdAndId(user.getId(), request.getId())
                    .orElseThrow();
            todo.setIsCheck(request.getIsCheck());
            // 저장
            toDoListRepository.save(todo);
            return ResponseEntity.ok("체크 작성 성공");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("글을 찾을 수 없습니다.");
        }
        // 체크 여부 설정

    }

    //수정
    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestHeader("Authorization") String token, @RequestBody ToDoListRequest.Updatelist request) {
        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getUserEmailFromToken(token);

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자를 찾을 수 없습니다."));
        try {
            ToDoList todo = toDoListRepository.findAllByUserIdAndId(user.getId(), request.getId())
                    .orElseThrow(() -> new RuntimeException("해당 ID의 할 일이 없습니다."));
            todo.setContents(request.getContents());
            todo.setDoDate(request.getDate());
            toDoListRepository.save(todo);
            return ResponseEntity.ok("글 수정 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("에러 발생: " + e.getMessage());
        }
    }

    //전체 조회
    @GetMapping
    public List<ToDoListResponse> getAll(@RequestHeader("Authorization") String token) {
        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getUserEmailFromToken(token);

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자를 찾을 수 없습니다."));

        List<ToDoList> todoLists = toDoListRepository.findAllByUserId(user.getId());

        return todoLists.stream()
                .map(todo -> new ToDoListResponse(
                        todo.getId(),
                        todo.getContents(),
                        todo.getIsCheck(),
                        todo.getDoDate(),
                        todo.getUser().getId()))
                .toList();
    }
    //날짜 조회
    @GetMapping("/date/{date}")
    public List<ToDoListResponse> getByDate(@RequestHeader("Authorization") String token, @PathVariable String date) {
        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getUserEmailFromToken(token);

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자를 찾을 수 없습니다."));

        LocalDate localDate = LocalDate.parse(date); // YYYY-MM-DD 날짜 별로
        List<ToDoList> todoLists = toDoListRepository.findAllByUserIdAndDoDate(user.getId(),localDate);

        return todoLists.stream()
                .map(todo -> new ToDoListResponse(
                        todo.getId(),
                        todo.getContents(),
                        todo.getIsCheck(),
                        todo.getDoDate(),
                        todo.getUser().getId()))
                .toList();
    }

    //삭제

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String email = jwtTokenProvider.getUserEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 사용자를 찾을 수 없습니다."));

        if(toDoListRepository.findAllByUserIdAndId(user.getId(), id).isPresent()) {
            todolistService.deleteToDo(user.getId(), id);
            return ResponseEntity.ok("글 삭제 성공");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("글을 찾을 수 없습니다.");
        }
    }
}
