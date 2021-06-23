package com.akvelon.planningsystem.repo;

import com.akvelon.planningsystem.entity.Task;
import com.akvelon.planningsystem.exception.DaoOperationException;

import java.util.List;

public interface TaskRepository {


    void save(Task task);

    /**
     * Retrieves and returns all tasks from the database
     * @return list of all tasks
     */
    List<Task> findAll();

    /**
     * Returns a task object by its id
     * @param id task identifier (primary key)
     * @return one task by its id
     */
    Task findOne(Long id);


    void update(Task task);

    /**
     * Removes an existing task from the database
     * @param task stored product
     */
    void remove(Task task);

    /**
     * Retrieves and returns all tasks from the database sorted by priority
     * @return sorted list of all tasks
     */
    List<Task> sortBy(String pref);



}
