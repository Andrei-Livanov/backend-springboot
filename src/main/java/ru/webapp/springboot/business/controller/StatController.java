package ru.webapp.springboot.business.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.webapp.springboot.business.entity.Stat;
import ru.webapp.springboot.business.service.StatService;
import ru.webapp.springboot.business.util.MyLogger;

@RestController
public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/stat")
    public ResponseEntity<Stat> findByEmail(@RequestBody String email) {

        MyLogger.debugMethodName("StatController: findByEmail(email) ---------------------------------- ");

        return ResponseEntity.ok(statService.findByEmail(email));
    }

}
