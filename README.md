# Raven Play – Movie Platform API

API REST para la plataforma de películas **Raven Play**, hecha con Spring Boot y Google Gemini (vía LangChain4j).

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java (OpenJDK) | 27 |
| Spring Boot | 4.1.2 |
| Gradle | 9.8.2 (wrapper) |
| LangChain4j | 1.23.0-beta33 |
| Modelo de IA | Gemini 2.5 Flash |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/hola` | Genera con IA un saludo de bienvenida (estilo Netflix, menos de 120 caracteres) |

## Estructura

```
src/main/java/com/raven/raven/
├── RavenApplication.java     # Clase principal
├── HolaController.java       # Controlador REST (/hola)
└── RavenPlayService.java     # Interfaz @AiService con el prompt
```

## Configuración

### 1. Clave de Gemini
Obtén una clave gratis en https://aistudio.google.com/apikey.

**Opción A (recomendada):** crea `src/main/resources/application-local.properties` (ignorado por Git):

```properties
langchain4j.google-ai-gemini.chat-model.api-key=TU_CLAVE
```

**Opción B:** define la variable de entorno `GEMINI_API_KEY`.

> Nunca subas tu clave a Git.

### 2. JDK
Necesitas un JDK 27. Si ejecutas desde la terminal, define `JAVA_HOME`:

```powershell
$env:JAVA_HOME = "C:\ruta\al\jdk-27"
```

## Ejecutar

```powershell
.\gradlew.bat bootRun
```

Luego abre http://localhost:8080/hola.

También puedes ejecutarlo desde IntelliJ con el triángulo verde junto a `main` en `RavenApplication.java`.

## Problemas encontrados y soluciones

| Problema | Causa | Solución |
|---|---|---|
| `JAVA_HOME is not set` en la terminal | La terminal no usa el JDK de IntelliJ, sino la variable de Windows | Definir `$env:JAVA_HOME` con la ruta del JDK (IntelliJ lo descarga en `C:\Users\<usuario>\.jdks\`) |
| `Cannot find a Java installation matching languageVersion=21` | `build.gradle` pedía Java 21 y solo había Java 27 | Cambiar a `JavaLanguageVersion.of(27)` en `build.gradle` |
| No aparece *Java Class* / *Interface* en el menú **New** de IntelliJ | Gradle no estaba sincronizado y IntelliJ no reconocía la carpeta como código Java | **Reload All Gradle Projects** (pestaña del elefante) |
| `package com;` en el controlador | El paquete no coincidía con la carpeta ni con el de `RavenApplication` | Usar `package com.raven.raven;` |
| Campo `final` sin inicializar en el controlador | Faltaba el constructor para inyectar el servicio | Agregar constructor `HolaController(RavenPlayService aiService)` |
| Error de nombre de método | `generateGreating` vs `generateGreeting` | Unificar a `generateGreeting` |
| `@RestController` no responde | Faltaba `@GetMapping` | Agregar `@GetMapping("/hola")` |
| No se tenía clave de OpenAI (de pago) | La API de OpenAI no es gratuita | Cambiar a Gemini (starter `langchain4j-google-ai-gemini-spring-boot-starter`) |
| Clave de API escrita en el código | Riesgo de subirla a GitHub | Moverla a `application-local.properties` e ignorarlo en `.gitignore` |
| `detected dubious ownership` en Git | El disco `E:` no registra propietario | `git config --global --add safe.directory E:/Sprint/raven` |

## Historial de cambios
Actualiza esta sección y la tabla anterior cada vez que cambies el stack o resuelvas un problema nuevo.
