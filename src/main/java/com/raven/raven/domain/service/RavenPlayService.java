package com.raven.raven.domain.service;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface RavenPlayService {
    @UserMessage("""
            Genera un saludo de bienvenida a la plataforma de gestion de peliculas Raven Play.
            Usa menos de 120 caracteres y hazlo con el estilo de netflix.
            """)
    String generateGreeting();
}
