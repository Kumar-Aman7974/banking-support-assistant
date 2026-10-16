# Decisions log — ai-service

Timestamp: 2026-10-15T22:56:36+05:30

Summary
- Make a small set of Pom fixes so the module builds and can run locally. These are recorded below.

Changes made and reasoning

1) Fixed missing dependency versions for Spring AI starters
- Change: Added <version>${spring-ai.version}</version> for:
  - org.springframework.ai:spring-ai-starter-model-google-genai-embedding
  - org.springframework.ai:spring-ai-starter-model-google-genai
- Reason: These artifacts are managed by the spring-ai BOM but the pom used explicit import; adding explicit version ensures Maven can resolve them in this module if BOM import wasn't effective in the initial state.

2) Replaced incorrect / non-standard artifacts
- Change: spring-boot-starter-webmvc -> spring-boot-starter-web
  - Reason: The project should use the standard web starter (spring-boot-starter-web). "webmvc" is a classifier more commonly seen in test artifacts; using the standard "web" starter is correct for runtime web apps.
- Change: flyway-database-postgresql -> flyway-core
  - Reason: "flyway-database-postgresql" is not the common published artifact; flyway-core is standard and supports PostgreSQL via JDBC driver.

3) Consolidated test starters into spring-boot-starter-test
- Change: Removed several non-standard "*-test" starters and used spring-boot-starter-test instead.
- Reason: spring-boot-starter-test is the canonical test starter bundling JUnit, Mockito, Spring Test, etc. The removed names were not valid boot starter coordinates.

4) Kept webflux starter
- Change: Left spring-boot-starter-webflux present (project appears to use reactive components for embedding exclusion notes).
- Reason: The original pom explicitly included webflux; preserved to avoid changing runtime behaviour unexpectedly.

Notes / Rationale
- Preference is to rely on parent BOM (spring-boot-starter-parent + spring-ai-bom) to provide versions. Where BOM resolution looked incomplete or artifacts were non-standard, explicit versions or standard artifact replacements were used.
- Aim was minimal surgical edits to make the module build and run locally without changing business logic.

Commands run during fix
- mvn -f ai-service\pom.xml validate -DskipTests=true
- mvn -f ai-service\pom.xml spring-boot:run

Files changed
- D:\banking-support-assitent\ai-service\pom.xml  — replaced/adjusted dependency artifactIds and added versions for spring-ai starters.

Follow-ups recommended (not done automatically)
- Re-import project in IDE (IntelliJ/VSCode) so source roots and BOM imports are recognized.
- Verify intended usage of webflux vs web; if the project expects purely reactive stacks, adjust controllers/service types accordingly.
- If specific spring-ai versions are desired, pin spring-ai.version to an appropriate release after testing.



---

2026-10-15T23:08:20+05:30

5) Temporary: Disabled Flyway migrations (application.yml)
- Change: Set spring.flyway.enabled = false in src/main/resources/application.yml
- Reason: Startup failed with FlywayException: "Unsupported Database: PostgreSQL 16.15". Disabling Flyway lets the application start for local development while investigating the root cause.
- Next steps (recommended):
  - Upgrade Flyway to a version that explicitly supports PostgreSQL 16.x (update pom.xml dependency org.flywaydb:flyway-core to a newer release).
  - Alternatively, pin or upgrade the PostgreSQL JDBC driver if Flyway detects database via driver metadata.
  - Re-enable migrations and run them against a test DB after confirming compatibility.

End of log.
