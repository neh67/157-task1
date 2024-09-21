package com.example.Task2.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Task2.Repository.TaskRepository;
import com.example.Task2.model.Task;
import com.example.Task2.model.Task.TaskStatus;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTaskStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public List<Task> getTasksDueToday() {
        return taskRepository.findByDueDate(LocalDate.now());
    }
}
