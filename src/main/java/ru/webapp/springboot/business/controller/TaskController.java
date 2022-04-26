package ru.webapp.springboot.business.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.webapp.springboot.business.entity.Task;
import ru.webapp.springboot.business.search.TaskSearchValues;
import ru.webapp.springboot.business.service.TaskService;
import ru.webapp.springboot.business.util.MyLogger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/task")
public class TaskController {

    public static final String ID_COLUMN = "id";

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PutMapping("/add")
    public ResponseEntity<?> add(@RequestBody Task task) {

        MyLogger.debugMethodName("task: add(task) ---------------------------------- ");

        if (task.getId() != null && task.getId() != 0) {
            return new ResponseEntity<>("redundant param: Id must be null", HttpStatus.NOT_ACCEPTABLE);
        }

        if (task.getTitle() == null || task.getTitle().trim().length() == 0) {
            return new ResponseEntity<>("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(taskService.add(task));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody Task task) {

        MyLogger.debugMethodName("task: update(task) ---------------------------------- ");

        if (task.getId() == null || task.getId() == 0) {
            return new ResponseEntity<>("missed param: Id", HttpStatus.NOT_ACCEPTABLE);
        }

        if (task.getTitle() == null || task.getTitle().trim().length() == 0) {
            return new ResponseEntity<>("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        taskService.update(task);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {

        MyLogger.debugMethodName("task: delete(id) ---------------------------------- ");

        try {
            taskService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity<>("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/all")
    public ResponseEntity<List<Task>> findAll(@RequestBody String email) {

        MyLogger.debugMethodName("task: findAll(email) ---------------------------------- ");

        return ResponseEntity.ok(taskService.findAll(email));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<Task>> search(@RequestBody TaskSearchValues taskSearchValues) {

        MyLogger.debugMethodName("task: search() ---------------------------------- ");

        String title = taskSearchValues.getTitle() != null ? taskSearchValues.getTitle() : null;
        Integer completed = taskSearchValues.getCompleted() != null ? taskSearchValues.getCompleted() : null;
        Long priorityId = taskSearchValues.getPriorityId() != null ? taskSearchValues.getPriorityId() : null;
        Long categoryId = taskSearchValues.getCategoryId() != null ? taskSearchValues.getCategoryId() : null;
        String sortColumn = taskSearchValues.getSortColumn() != null ? taskSearchValues.getSortColumn() : null;
        String sortDirection = taskSearchValues.getSortDirection() != null ? taskSearchValues.getSortDirection() : null;
        int pageNumber = taskSearchValues.getPageNumber() != null ? taskSearchValues.getPageNumber() : 0;
        int pageSize = taskSearchValues.getPageSize() != null ? taskSearchValues.getPageSize() : 10;
        String email = taskSearchValues.getEmail() != null ? taskSearchValues.getEmail() : null;

        Date dateFrom = null;
        Date dateTo = null;

        if (taskSearchValues.getDateFrom() != null) {

            Calendar calendarFrom = Calendar.getInstance();
            calendarFrom.setTime(taskSearchValues.getDateFrom());
            calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
            calendarFrom.set(Calendar.MINUTE, 0);
            calendarFrom.set(Calendar.SECOND, 0);
            calendarFrom.set(Calendar.MILLISECOND, 0);

            dateFrom = calendarFrom.getTime();

        }

        if (taskSearchValues.getDateTo() != null) {

            Calendar calendarTo = Calendar.getInstance();
            calendarTo.setTime(taskSearchValues.getDateTo());
            calendarTo.set(Calendar.HOUR_OF_DAY, 23);
            calendarTo.set(Calendar.MINUTE, 59);
            calendarTo.set(Calendar.SECOND, 59);
            calendarTo.set(Calendar.MILLISECOND, 999);

            dateTo = calendarTo.getTime();

        }

        Sort.Direction direction = sortDirection == null
                || sortDirection.trim().length() == 0
                || sortDirection.trim().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        if (sortColumn == null) {
            sortColumn = ID_COLUMN;
        }

        Sort sort = Sort.by(direction, sortColumn, ID_COLUMN);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        Page<Task> result =
                taskService.find(title, completed, priorityId, categoryId, email, dateFrom, dateTo, pageRequest);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/id")
    public ResponseEntity<?> findById(@RequestBody Long id) {

        MyLogger.debugMethodName("task: findById(id) ---------------------------------- ");

        Task task;

        try {
            task = taskService.findById(id);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity<>("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(task);
    }

}
