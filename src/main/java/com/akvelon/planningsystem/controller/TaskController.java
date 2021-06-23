package com.akvelon.planningsystem.controller;

import com.akvelon.planningsystem.entity.Task;
import com.akvelon.planningsystem.repo.TaskRepository;
import com.akvelon.planningsystem.repo.TaskRepositoryImpl;
import com.akvelon.planningsystem.util.JdbcUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final DataSource dataSource
            = JdbcUtil.createDefaultPostgresDataSource();
    private static final TaskRepository taskRepository
            = new TaskRepositoryImpl(dataSource);


    @GetMapping("/{id}")
    public ResponseEntity<Task> fetchTask(@PathVariable Long id) {

        return ResponseEntity.ok()
                .body(taskRepository.findOne(id));
    }

    @GetMapping
    public ResponseEntity<List<Task>> fetchAllTasks() {

        return ResponseEntity.ok()
                .body(taskRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<String> createTask(@RequestBody Task task) {

        taskRepository.save(task);
        return ResponseEntity
                .ok()
                .body("Task has been created!");
    }

    @PutMapping
    public ResponseEntity<String> updateTask(@RequestBody Task task) {

        taskRepository.update(task);
        return ResponseEntity.ok()
                .body("Task has been created!");
    }

    @DeleteMapping()
    public ResponseEntity<Task> deleteTask(@RequestBody Task task) {

        taskRepository.remove(task);
        return ResponseEntity
                .ok()
                .body(task);
    }

    @GetMapping("/sort/{pref}")
    public ResponseEntity<List<Task>> sortByPriority(@PathVariable String pref) {

        return ResponseEntity.ok()
                .body(taskRepository.sortBy(pref));
    }
}


