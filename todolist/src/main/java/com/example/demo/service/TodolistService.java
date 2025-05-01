package com.example.demo.service;

import com.example.demo.repository.ToDoListRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TodolistService {
    private final ToDoListRepository toDoListRepository;

    public void deleteToDo(Long userId, Long id) {
        toDoListRepository.deleteByUserIdAndId(userId, id);
    }
}
