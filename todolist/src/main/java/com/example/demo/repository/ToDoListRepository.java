package com.example.demo.repository;

import com.example.demo.domain.ToDoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ToDoListRepository extends JpaRepository<ToDoList,Long> {
List<ToDoList> findAllByDoDate (LocalDate Dodate);
}
