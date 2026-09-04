# Copilot Instructions for Pay For Legal Aid (GPFD)

Get Payments and Finance Data (GPFD) is a Spring Boot API that provides financial reports data and integrates with the Data Claims Reporting Service (DCRS).

**For detailed setup, running, and troubleshooting instructions, see [README.md](../README.md).**

## Quick Command Reference

```bash
# Build
mvn clean package
mvn clean package -DskipTests

# Test - unit and integration only
mvn clean test -Dtest='!*RunCucumberTest'
mvn test -Dtest=ClassName#methodName

# Test - acceptance (BDD)
mvn clean test -Dtest='*RunCucumberTest' -Dcucumber.filter.tags="not @performance"

# Test - performance (Gatling)
export JSESSIONID=<from-browser>
mvn gatling:test -Dgatling.simulationClass=uk.gov.laa.gpfd.simulations.ClassName -Dmaven.antrun.skip=true -Dmaven.resources.skip=true

# Analysis
mvn spotbugs:check
snyk test --policy-path=.snyk

# Docker local development
docker compose build
docker compose up
docker compose down
```

## Architecture & Design

### System Overview
GPFD is a dual-interface application:
- **REST API**: Serves JSON for programmatic report access
- **Web UI**: Thymeleaf-based interface for users to view and download reports

Data sources:
- **MOJFIN database** (read-only via views): Live financial report data
- **Data Claims Reporting Service (DCRS)**: Pre-generated CSV reports
- **RDS tracking database**: Report metadata and access logs

Authentication: Microsoft Entra ID via SILAS (Sign Into Legal Aid Services)

### Layered Architecture
```
Controller (HTTP endpoints)
  ↓
Service (business logic)
  ↓
DAO (database access) / S3Client (AWS)
  ↓
Database (RDS, MOJFIN) / AWS S3
```

Key modules:
- `controller/`: HTTP endpoints for reports, health, API operations
- `services/`: ReportManagementService (core), StreamingService (file download), s3/ (AWS integration)
- `dao/`: Database query layer with read-only MOJFIN access
- `config/`: Spring Security (SILAS), S3, AppConfig, SecurityConfig
- `security/`: Custom auth/authz components
- `model/`: DTOs (auto-generated from OpenAPI spec), entities
- `mapper/`: Object mapping utilities

### Database Strategy
- **Flyway** (`src/main/resources/flyway/migration/schema`): Manages RDS metadata schema in deployed environments
- **Liquibase** (`src/test/resources`): Defines H2 test database schema
- **MOJFIN access**: Read-only credentials via environment variables; queries use parameterised statements

## Code Patterns & Conventions

### Language
- **Use British English spelling and terminology** in all code comments, documentation, commit messages, and variable names (e.g., `colour` not `color`, `localise` not `localize`, `organise` not `organize`)

### Code Style
- **Controllers**: REST endpoints only; delegate logic to services
- **Services**: Implement business rules; avoid mixing concerns (keep report logic separate from S3, database, etc.)
- **DAOs**: Query abstraction layer; enforce parameterised queries (prevent SQL injection)
- **Models**: Use immutables (Immutables) or Lombok for concise data classes
- **Security**: Custom components (SecurityConfig, ContextBasedAuthorizationManager, TimeBasedAccessInterceptor) handle auth flow

### Testing Structure
- **Unit tests** (`src/test/java`, `*Test.java`): Mock services, H2 database, isolated component testing
- **Integration tests** (`src/it/java`, `*IT.java`): Mock external services (S3, MOJFIN), H2 database, component interactions
- **Acceptance tests** (`*RunCucumberTest`): BDD scenarios with Gherkin syntax, include accessibility (Axe) checks
- **Performance tests** (`src/test/java/uk/gov/laa/gpfd/simulations`): Gatling simulations for load testing
- **Coverage targets**: 80% line, 68% branch (JaCoCo enforced; build fails if below thresholds)

### Configuration
- **Spring profiles**: `local` (console logs, file-based S3), `dev`, `uat`, `prod` (ECS JSON logging)
- **Environment variables**: Override properties; required for sensitive values (Entra, S3, database credentials)
- Select profile: `--spring.profiles.active=profile` or via Docker environment

### Database Migrations
- Use descriptive names: `V1__create_report_table.sql`, `R__01_seed_metadata.sql`
- Flyway version-based (V*.sql) for deployment; repeatable (R__*.sql) for seed data
- Deployed environments: RDS only (no Liquibase); MOJFIN accessed read-only

### Logging
- **Production (dev/uat/prod)**: ECS structured JSON with trace/span IDs for OpenSearch/Kibana
- **Local**: Human-readable console output with trace/span IDs
- Includes: timestamp, level, service name, version, environment, pod name, correlation IDs

### AWS S3 Integration
- Conditional configuration: `S3Config` (deployed), `S3ConfigLocal` (local testing with file storage)
- Patterns: `FileDownloadFromS3Service`, `S3ClientWrapper`, `ReportFileNameResolver`
- File streaming for large reports to avoid memory issues
