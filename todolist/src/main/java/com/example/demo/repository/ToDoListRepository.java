package com.example.demo.repository;

import com.example.demo.domain.ToDoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ToDoListRepository extends JpaRepository<ToDoList,Long> {
    List<ToDoList> findAllByDoDate (LocalDate Dodate);
    Optional<ToDoList> findAllByUserIdAndId(Long userId, Long Id);
    List<ToDoList> findAllByUserIdAndDoDate(Long userId, LocalDate Dodate);

    List<ToDoList> findAllByUserId(Long id);
    void deleteByUserIdAndId(Long userId, Long Id);
}
