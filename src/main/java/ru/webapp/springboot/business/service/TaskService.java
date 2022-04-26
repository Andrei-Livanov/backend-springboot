package ru.webapp.springboot.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.webapp.springboot.business.entity.Task;
import ru.webapp.springboot.business.repository.TaskRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task add(Task task) {
        return taskRepository.save(task);
    }

    public void update(Task task) {
        taskRepository.save(task);
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> findAll(String email) {
        return taskRepository.findByUserEmailOrderByTitleAsc(email);
    }

    public Page<Task> find(String title, Integer completed, Long priorityId,
                           Long categoryId, String email, Date dateFrom, Date dateTo, PageRequest paging) {
        return taskRepository.find(title, completed, priorityId, categoryId, email, dateFrom, dateTo, paging);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).get();
    }

}
