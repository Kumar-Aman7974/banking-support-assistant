# Code flow — ai-service

Generated: 2026-10-15T22:56:36+05:30

Overview
- Application entry point: com.banking.support.ai_service.AiServiceApplication (src/main/java/com/banking/support/ai_service/AiServiceApplication.java)
- Framework: Spring Boot — SpringApplication.run(...) starts the application context and triggers auto-configuration and component scanning under the base package.

Execution order (high level)
1) JVM starts AiServiceApplication.main(String[])
2) SpringApplication.run(AiServiceApplication.class, args)
   - Create SpringApplication, start Spring context
   - Auto-configuration classes run (data source, JPA, Flyway, web server, Spring AI autoconfig if present)
   - Component scan picks up @Component, @Service, @Repository, @Controller, @RestController in the application package and subpackages
3) Beans are created in dependency order (singletons by default). Repositories (JPA) and DataSource initialize before any data-access beans.
4) Flyway (flyway-core) executes database migrations during DataSource initialization (if configured)
5) Web server (Tomcat/Netty depending on web vs webflux starter) starts and exposes controllers/routes
6) Incoming HTTP requests are dispatched to controller handler methods which call service classes, repositories, and helper utilities (PDF parsing, embedding, vector store).

Which function calls which
- AiServiceApplication.main -> SpringApplication.run
- Spring Boot auto-config -> creates DataSource, JPA EntityManagerFactory
- Flyway (if enabled) -> runs migrations on DataSource before JPA usage
- Controllers (classes annotated @RestController / @Controller) -> call Service classes (annotated @Service)
- Service classes -> call Repositories (interfaces extending JpaRepository) and helper classes (PDF parsing with PDFBox, Spring AI clients)
- Spring AI starters -> provide beans for embedding/model clients; application code should inject these via @Autowired or constructors (e.g., EmbeddingsClient or ModelClient abstractions)

Notes about reactive vs servlet stack
- If both spring-boot-starter-web and spring-boot-starter-webflux are present, the classpath determines which server is active. Usually spring-web (servlet) will start an embedded Tomcat; webflux starts Netty. Ensure intended runtime is present and controllers match (annotated handlers for WebFlux differ in some reactive types).

How to trace actual call graph for this project
- Open AiServiceApplication.java to find the @SpringBootApplication base package
- Search for @RestController, @Controller, @Service, @Repository annotations under src/main/java to list concrete classes and trace which services repositories they call



Operational note (2026-10-15T23:08:20+05:30): Flyway disabled to allow startup
- During startup Flyway threw: "Unsupported Database: PostgreSQL 16.15" and prevented Spring context from initializing.
- Change: spring.flyway.enabled set to false in src/main/resources/application.yml
- Effect on flow: Step 4 (Flyway migrations) is skipped. DataSource and JPA initialize without applying migrations; ensure schema exists or set hibernate.ddl-auto appropriately for development.

End of flow document.
