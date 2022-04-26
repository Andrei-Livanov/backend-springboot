package ru.webapp.springboot.business.service;

import org.springframework.stereotype.Service;
import ru.webapp.springboot.business.entity.Priority;
import ru.webapp.springboot.business.repository.PriorityRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PriorityService {

    private final PriorityRepository priorityRepository;

    public PriorityService(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    public Priority add(Priority priority) {
        return priorityRepository.save(priority);
    }

    public void update(Priority priority) {
        priorityRepository.save(priority);
    }

    public void delete(Long id) {
        priorityRepository.deleteById(id);
    }

    public List<Priority> findAll(String email) {
        return priorityRepository.findByUserEmailOrderByIdAsc(email);
    }

    public List<Priority> find(String title, String email) {
        return priorityRepository.find(title, email);
    }

    public Priority findById(Long id) {
        return priorityRepository.findById(id).get();
    }

}
