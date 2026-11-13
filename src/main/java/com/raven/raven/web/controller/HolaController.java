package com.raven.raven.web.controller;

import com.raven.raven.domain.service.RavenPlayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaController {
    private final RavenPlayService aiService;

    public HolaController(RavenPlayService aiService){
        this.aiService=aiService;
    }
    @GetMapping("/hola")
    public String hola() {
        return this.aiService.generateGreeting();
    }
}