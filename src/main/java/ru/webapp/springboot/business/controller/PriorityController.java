package ru.webapp.springboot.business.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.webapp.springboot.business.entity.Priority;
import ru.webapp.springboot.business.search.PrioritySearchValues;
import ru.webapp.springboot.business.service.PriorityService;
import ru.webapp.springboot.business.util.MyLogger;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/priority")
public class PriorityController {

    private final PriorityService priorityService;

    public PriorityController(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @PutMapping("/add")
    public ResponseEntity<?> add(@RequestBody Priority priority) {

        MyLogger.debugMethodName("PriorityController: add(priority) ---------------------------------- ");

        if (priority.getId() != null && priority.getId() != 0) {
            return new ResponseEntity<>("redundant param: id MUST be null", HttpStatus.NOT_ACCEPTABLE);
        }

        if (priority.getTitle() == null || priority.getTitle().trim().length() == 0) {
            return new ResponseEntity<>("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        if (priority.getColor() == null || priority.getColor().trim().length() == 0) {
            return new ResponseEntity<>("missed param: color", HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(priorityService.add(priority));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody Priority priority) {

        MyLogger.debugMethodName("PriorityController: update(priority) ---------------------------------- ");

        if (priority.getId() == null || priority.getId() == 0) {
            return new ResponseEntity<>("Missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        if (priority.getTitle() == null || priority.getTitle().trim().length() == 0) {
            return new ResponseEntity<>("Missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        if (priority.getColor() == null || priority.getColor().trim().length() == 0) {
            return new ResponseEntity<>("Missed param: color", HttpStatus.NOT_ACCEPTABLE);
        }

        priorityService.update(priority);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {

        MyLogger.debugMethodName("PriorityController: delete(id) ---------------------------------- ");

        try {
            priorityService.delete(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity<>("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/all")
    public ResponseEntity<List<Priority>> findAll(@RequestBody String email) {

        MyLogger.debugMethodName("PriorityController: findAll(email) ---------------------------------- ");

        return ResponseEntity.ok(priorityService.findAll(email));
    }

    @PostMapping("/search")
    public ResponseEntity<List<Priority>> search(@RequestBody PrioritySearchValues prioritySearchValues) {

        MyLogger.debugMethodName("PriorityController: search() ---------------------------------- ");

        List<Priority> list = priorityService.find(prioritySearchValues.getTitle(), prioritySearchValues.getEmail());

        return ResponseEntity.ok(list);
    }

    @PostMapping("/id")
    public ResponseEntity<?> findById(@RequestBody Long id) {

        MyLogger.debugMethodName("PriorityController: findById(id) ---------------------------------- ");

        Priority priority;

        try {
            priority = priorityService.findById(id);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity<>("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(priority);
    }

}
