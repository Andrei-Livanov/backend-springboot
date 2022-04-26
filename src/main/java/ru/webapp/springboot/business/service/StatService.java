package ru.webapp.springboot.business.service;

import org.springframework.stereotype.Service;
import ru.webapp.springboot.business.entity.Stat;
import ru.webapp.springboot.business.repository.StatRepository;

import javax.transaction.Transactional;

@Service
@Transactional
public class StatService {

    private final StatRepository statRepository;

    public StatService(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    public Stat findByEmail(String email) {
        return statRepository.findByUserEmail(email);
    }

}
