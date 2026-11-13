# Raven Play – Movie Platform API

API REST para la plataforma de películas **Raven Play**, hecha con Spring Boot, PostgreSQL y Google Gemini (vía LangChain4j).

## Tecnologías

| Tecnología | Versión / detalle |
|---|---|
| Java (OpenJDK) | 27 |
| Spring Boot | 4.1.2 |
| Gradle | 9.8.2 (wrapper) |
| Spring Data JPA + Hibernate | Persistencia |
| PostgreSQL | Base de datos `raven_play_db` |
| LangChain4j | 1.23.0-beta33 |
| Modelo de IA | Gemini 2.5 Flash |
| Herramientas | IntelliJ IDEA, DBeaver |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/hola` | Genera con IA un saludo de bienvenida (estilo Netflix, menos de 120 caracteres) |

## Estructura

```
src/main/java/com/raven/raven/
├── RavenApplication.java                 # Clase principal
├── web/controller/HolaController.java    # Controlador REST (/hola)
├── domain/service/RavenPlayService.java  # Interfaz @AiService con el prompt
└── persistence/entity/MovieEntity.java   # Entidad JPA -> tabla raven_play_peliculas
```

### Tabla `raven_play_peliculas`
Se crea automáticamente al arrancar la app (`spring.jpa.hibernate.ddl-auto=update`).

| Columna | Tipo | Notas |
|---|---|---|
| `id` | Long | PK, autoincremental |
| `titulo` | String(150) | Obligatorio, único |
| `duracion` | Integer | Obligatorio |
| `genero` | String(40) | Obligatorio |
| `fecha_estreno` | LocalDate | Opcional |
| `clasificacion` | BigDecimal(3,2) | Opcional |
| `estado` | String(1) | Obligatorio |

## Configuración

### Perfiles de Spring

| Archivo | Contenido | ¿Se sube a Git? |
|---|---|---|
| `application.properties` | Valores comunes y perfiles activos (`dev, local`) | Sí |
| `application-dev.properties` | Configuración de desarrollo (BD, logs) | Sí |
| `application-local.properties` | Secretos (API key) | **No** (en `.gitignore`) |

Spring carga `application` → `dev` → `local`. Si una propiedad se repite, gana la última.

### 1. Clave de Gemini
Obtén una clave gratis en https://aistudio.google.com/apikey.

**Opción A (recomendada):** crea `src/main/resources/application-local.properties`:

```properties
langchain4j.google-ai-gemini.chat-model.api-key=TU_CLAVE
```

**Opción B:** define la variable de entorno `GEMINI_API_KEY`.

> Nunca subas claves ni contraseñas a Git.

### 2. Base de datos PostgreSQL
1. Crea la base de datos (Hibernate crea las tablas, pero **no** la base):
   ```sql
   CREATE DATABASE raven_play_db;
   ```
2. Configura la conexión (`spring.datasource.url`, `username`, `password`). Lo ideal es poner usuario y contraseña en `application-local.properties`.

### 3. JDK
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
| No aparece *Java Class* / *Interface* en el menú **New** de IntelliJ | Gradle no estaba sincronizado | **Reload All Gradle Projects** (pestaña del elefante) |
| `package com;` en el controlador | El paquete no coincidía con la carpeta ni con `RavenApplication` | Usar el paquete real, por ejemplo `com.raven.raven.web.controller` |
| Campo `final` sin inicializar en el controlador | Faltaba el constructor para inyectar el servicio | Agregar constructor con el servicio como parámetro |
| Error de nombre de método | `generateGreating` vs `generateGreeting` | Unificar el nombre en servicio y controlador |
| `@RestController` no responde | Faltaba `@GetMapping` | Agregar `@GetMapping("/hola")` |
| No se tenía clave de OpenAI (de pago) | La API de OpenAI no es gratuita | Cambiar a Gemini (`langchain4j-google-ai-gemini-spring-boot-starter`) |
| Clave de API escrita en el código | Riesgo de subirla a GitHub | Moverla a `application-local.properties` e ignorarlo en `.gitignore` |
| `detected dubious ownership` en Git | El disco `E:` no registra propietario | `git config --global --add safe.directory E:/Sprint/raven` |
| `/hola` da error 500 al quitar el perfil `local` | Sin el perfil, la clave de Gemini queda vacía | Mantener `local` activo o definir `GEMINI_API_KEY` |
| No salen logs de las peticiones a Gemini | `log-requests` y `log-responses` no existen en el starter de Gemini | Usar `langchain4j.google-ai-gemini.chat-model.log-requests-and-responses=true` |
| `spring.profiles.active` dentro de `application-dev.properties` | Spring Boot no permite esa propiedad en archivos de perfil | Ponerla solo en `application.properties` |
| Typo `sprint.datasource...` | Error de escritura | Corregir a `spring.datasource...` |
| No se ve la tabla en DBeaver | DBeaver mostraba la base `postgres`, la app usa `raven_play_db` | Activar *Mostrar todas las bases de datos* en la conexión y refrescar con `F5` |

## Lo aprendido

- **Terminal vs IntelliJ:** la terminal usa `JAVA_HOME` de Windows; IntelliJ usa el SDK del proyecto.
- **Gradle:** al cambiar `build.gradle` hay que recargar el proyecto en IntelliJ.
- **Paquetes:** Spring Boot solo escanea el paquete de la clase `@SpringBootApplication` y sus subpaquetes.
- **Inyección de dependencias:** un campo `final` se inicializa con un constructor y Spring inyecta el bean.
- **Perfiles de Spring:** separar configuración común (`application`), de entorno (`dev`) y secretos (`local`).
- **Secretos:** nunca van en archivos que se suben a Git. `.gitignore` solo evita archivos nuevos, no los ya subidos.
- **Propiedades de librerías:** cada starter tiene sus propios nombres. Spring ignora en silencio las propiedades desconocidas, así que conviene verificarlos.
- **LangChain4j:** `@AiService` sobre una interfaz con `@UserMessage` genera la implementación automáticamente.
- **JPA/Hibernate:** `ddl-auto=update` crea las tablas desde las entidades, pero la base de datos debe existir antes.
- **DBeaver:** una conexión puede mostrar solo una base de datos; hay que revisar que sea la correcta.

## Pendiente

- [ ] Confirmar que `raven_play_db` existe y que la app crea la tabla `raven_play_peliculas`.
- [ ] Mover usuario y contraseña de la BD a `application-local.properties`.
- [ ] Crear repositorio, servicio y endpoints para películas.

## Historial de cambios
Actualiza esta sección y las tablas cada vez que cambies el stack o resuelvas un problema nuevo.
