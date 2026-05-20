# Get Legal Aid Data (GLAD) / Get Payments and Finance Data (GPFD)

(Formerly known as 'Pay For Legal Aid' (PFLA))

[![Ministry of Justice Repository Compliance Badge](https://github-community.service.justice.gov.uk/repository-standards/api/payforlegalaid/badge)](https://github-community.service.justice.gov.uk/repository-standards/payforlegalaid)

[![.github/workflows/codeql-analysis.yml](https://github.com/ministryofjustice/payforlegalaid/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/ministryofjustice/payforlegalaid/actions/workflows/codeql-analysis.yml)

## About this repository

This is an API that gets financial reports data from the MOJFIN database in the modernisation platform,
and returns it to the user in the form of a CSV or Excel data stream. The current user flow is:

1. A user clicks on a URL hyperlink inside a Sharepoint site
2. This will send a request to the GPFD API with a request parameter
   The five endpoints are:
   /reports/ (returns a list of the available reports and some information about them).
   /reports/{id} (returns metadata about the requested report)
   /reports/{id}/csv (returns the CSV data stream for the requested report),
   /reports/{id}/excel (returns the Excel data stream for the requested report),
   /reports/{id}/file (returns a pre-generated CSV, for specific reports only)
3. The API will authenticate the user against MOJ Microsoft Entra, and if the user has an account
   within the MOJ 'tenant' they will be authenticated and allowed to interact with the API. 
    - The service will redirect the user's browser to Microsoft Entra/Active Directory for them to enter their
      Microsoft account details
    - This authentication is handled by the `spring-cloud-azure-starter-active-directory` library in the POM
    - Some endpoints require further role-based checks.
4. The service makes calls to various tables in the MOJFIN DEV database, to obtain metadata about the financial
   reports and gather the actual reports data on generated reports.
5. A response is sent back to the user, and either displayed in their browser as JSON (metadata endpoints) or downloaded (report data endpoints)

## Database

GPFD currently uses two sets of MOJFIN database user credentials, defined in the application properties:

1. 'read-only', which is used to access reports data through db views created in MOJFIN
2. 'write' which is used to read and write to the custom GPFD tables created in MOJFIN

There are multiple GPFD tables - see the [Database Design page](https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/5481922635/Database+Design)

The GPFD table definitions and data are stored in the [data repository](https://github.com/ministryofjustice/payforlegalaid-data)

There is also an H2 database which some unit tests run against, the schema of which is defined in `test/resources`, as Liquibase changelogs

## Technology Stack

- Java
- Maven
- Spring Boot
    - Spring Security
    - Spring Web
- Docker
- Kubernetes
- GitHub Actions
- Microsoft Entra
- Liquibase
- [OpenAPI](https://github.com/ministryofjustice/payforlegalaid-openapi)

## Architecture

Architectural designs can be found here:
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4492689485/Architecture+and+Design

Context around the AWS cloud/kubernetes setup at LAA and the GPFD app in particular can be found here:  
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4428398672/Cloud+Platform+Setup

## Environments
More information can be found in the following confluence page:
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4736516940/GPFD+Environments

## Running The App
### Locally
Locally you can install the app and run it
- `mvn clean install`
- `java -jar -Dspring.profiles.active=local target/pay-for-legal-aid-0.0.1-SNAPSHOT-exec.jar`

This makes use of a local h2 database. You can create one via running the acceptance tests.

You will need to populate the `src/main/resources` folder with any template files you need. These are the `.xlsx` files that
hold a skeleton of the report in. E.g. if you want to test the Third Party Report you should place a copy of the Third Party Report template in the folder.
For more details on how to get this template visit the [Confluence](https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/5803409516/How+to+create+a+template#How-do-I-get-a-template-to-use-on-my-local-system)

### Locally (Docker)

The application can be run locally using Docker and Docker Compose, which handles building the app and spinning up all required services.
Uses the DB changelog files that were initially stored in the payforlegalaid-tests repo to build out the test database via Liquibase.

#### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (or Docker Engine 23+)
- Git
- .env created and populated with Oracle DB password (can be anything)

#### Building the Image

Using the .env.example file as a template, create your own .env file on your machine at the same level as the example file
and populate with your own password for the Oracle DB. This should help set up credentials for the DB itself and assist with
Liquibase migrations. Gitignore has been updated to include .env, if accidentally pushed to git, PR will complain about password
and automatically block.

Once set up, build the images:

```bash
docker compose build
```

On first build this will take a few minutes as Maven downloads dependencies and builds the OpenAPI library. Subsequent builds are significantly faster due to layer and dependency caching — as long as `pom.xml` hasn't changed, the dependency resolution step is skipped entirely.

To force a clean rebuild from scratch:
```bash
docker volume rm payforlegalaid_oracle-data   
docker compose build --no-cache
```

> **Note:** Avoid using `--no-cache` routinely as it discards the Maven dependency cache and will significantly slow down the build.

#### Running the Application
```bash
docker compose up
```

The application will be available at `http://localhost:8080` once healthy. You can check the status with:
```bash
docker compose ps
```

To run in detached mode:
```bash
docker compose up -d
```

To stop the application:
```bash
docker compose down
```

Connecting to the Oracle container
```bash
docker exec -it oracle-local bash
sqlplus / as sysdba
ALTER SESSION SET CONTAINER = FREEPDB1;
```

Example SQL query once connected
```sql
SELECT table_name FROM all_tables WHERE owner = 'GPFD';
```

#### Troubleshooting

**Build fails with `invalid target release` error**
Ensure you are using the correct base image in the Dockerfile (`maven:3.9.14-eclipse-temurin-25`). The OpenAPI dependency requires Java 25 to compile.

**Application exits immediately with `UnsupportedClassVersionError`**
The runtime image must match the Java version used to compile the app. Ensure the runtime stage uses `eclipse-temurin:25-jre-jammy` or equivalent.

**Application fails to start with `Could not resolve placeholder` error**
A required property is missing from the active Spring profile's configuration. Check `src/main/resources/application-local.yml` and ensure all required properties are defined. Azure AD properties must be present even when `enabled: false`.

**`dependency:go-offline` is slow**
This is expected on a cold cache (e.g. first build or after `--no-cache`). Once the Maven cache is warm it will be near-instant. If it is slow on every build, verify BuildKit is enabled by adding the following to your shell profile:
```bash
export DOCKER_BUILDKIT=1
export COMPOSE_DOCKER_CLI_BUILD=1
```

Then reload your shell with `source ~/.zshrc` (or `~/.bashrc`).

### Scanning Snyk tools
- `snyk test --policy-path=.snyk`

### Access on development
- There is a shared password-manager group account for the GPFD team, ask the team for details on how to request access. 
This group has a number of test users which can be used to access the Dev environment - these accounts have been registered with the DEV environment's active directory, and so will pass the SSO authentication.

## Logging

This service uses ECS (Elastic Common Schema) structured logging for all deployed environments, providing machine-readable JSON logs that integrate with observability tools like OpenSearch and Kibana. For local development, human-readable console logs are used instead.

### ECS Structured Logging

In non-local profiles (dev, uat, prod), logs are output in ECS JSON format including:
- `@timestamp` - ISO 8601 timestamp
- `log.level` - Log level (INFO, WARN, ERROR, etc.)
- `message` - Log message
- `service.name` - Application name (`Pay For Legal Aid`)
- `service.version` - Build version
- `service.environment` - Active Spring profile
- `service.node.name` - Hostname/pod name
- `trace.id` and `span.id` - Distributed tracing correlation IDs

Example ECS JSON log output:
```json
{
  "@timestamp": "2026-05-14T10:23:45.123Z",
  "log.level": "INFO",
  "message": "Downloading report for id xxx",
  "ecs.version": "8.11",
  "service.name": "Pay For Legal Aid",
  "service.version": "0.0.1-SNAPSHOT",
  "service.environment": "dev",
  "service.node.name": "laa-get-payments-finance-data-xxxx",
  "trace.id": "1234567890abcdef",
  "span.id": "fedcba0987654321",
  "log.logger": "uk.gov.laa.gpfd.controller.ReportController"
}
```

### Local Development Logging

When running locally or in Docker with `--spring.profiles.active=local`, logs are output in a human-readable format with trace and span IDs for correlation:

```
2026-05-14 10:23:45.123  INFO [1234567890abcdef,fedcba0987654321] --- [nio-8080-exec-1] u.g.l.gpfd.controller.ReportController   : Downloading report for id xxx
```

## Tests

There are unit tests which use mocked services and an H2 database. Config for the latter is located in the
test/resources folder.

Acceptance tests are located [here](https://github.com/ministryofjustice/payforlegalaid-tests).

### Performance Tests

Performance tests have been set up in payforlegalaid using Gatling to evaluate things like retrieving the list of reports, 
downloading the reports and also concurrency.

src/test/java/uk.gov.laa.gpfd/simulations

The base URL that the tests use is set within gatling.properties within /resources. Set to UAT env by default.
To run the tests, first log in manually into desired environment, then copy the session cookie, `JSESSIONID`, from
within the browser DevTools -> Application -> Cookies, then run:

```bash
export JSESSIONID=<insert session id>
mvn gatling:test -Dgatling.simulationClass=uk.gov.laa.gpfd.simulations.<test-name> -Dmaven.antrun.skip=true -Dmaven.resources.skip=true`
```

Replace <test-name> with the choice of test. Reports are generated automatically by Gatling and can be accessed at the end of a
test run within the terminal.

Alternatively, developers can trigger the manual GitHub Actions workflow at
`.github/workflows/run-gatling-performance-tests.yml` and provide:
- `simulation-class` as the fully qualified Gatling simulation class
- `jsessionid` as the authenticated browser session cookie

The workflow runs the same Maven Gatling command and uploads the generated report artifacts for later review.

## CI/CD

GitHub Actions is used for CI/CD.

# Releases

This project uses [release-please](https://github.com/googleapis/release-please)
to automate versioning, changelog generation, and GitHub releases.

### How It Works

When commits are merged into `main`, release-please:

- determines the next semantic version
- updates `pom.xml`
- updates `CHANGELOG.md`
- creates a release pull request
- creates a GitHub release after the PR is merged

---

When the Release PR is merged:
- a Git tag is created
- a GitHub Release is published
- the new version becomes available

---

### Commit Message Format

All commits should follow this format:

```text
<type>: <short summary>
```

Examples:

```text
feat: add webhook retry support
fix: prevent duplicate event processing
docs: update installation instructions
```

---

### Commit Types

| Type | Description | Version Impact |
|------|-------------|----------------|
| `feat` | Introduces a new feature | Minor |
| `fix` | Fixes a bug | Patch |
| `feat!` | Breaking feature change | Major |
| `docs` | Documentation updates only | None |
| `refactor` | Internal code restructuring | None |
| `test` | Adding or updating tests | None |
| `chore` | Maintenance tasks | None |
| `ci` | CI/CD pipeline changes | None |
| `build` | Build tooling or dependency changes | None |
| `perf` | Performance improvements | Patch |
| `revert` | Reverts a previous commit | Depends |

---
## Semantic Versioning Examples

### Patch Release

```text
fix: handle null response from API
```

Results in:

```text
1.4.0 → 1.4.1
```

---

### Minor Release

```text
feat: add OAuth authentication
```

Results in:

```text
1.4.0 → 1.5.0
```

---

### Major Release

```text
feat!: remove deprecated REST endpoints
```

or:

```text
feat: remove deprecated REST endpoints

BREAKING CHANGE: legacy REST API removed
```

Results in:

```text
1.4.0 → 2.0.0
```
---

## Azure Active Directory SSO

The code for authenticating via SSO is located in the pom library `spring-cloud-azure-starter-active-directory`,
and the config for this is located in the application config.

More information can be found in the following confluence page:
https://dsdmoj.atlassian.net/wiki/spaces/LPF/pages/4500652730/Azure+Active+Directory+SSO+-+Setup

And the official docs:
https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/spring-boot-starter-for-azure-active-directory-developer-guide?tabs=SpringCloudAzure4x

# Pre commit hooks

- Pre commit hooks have been set up on this repository to ensure no accidental commits of secrets, keys etc. Provided by DevSecOps https://github.com/ministryofjustice/devsecops-hooks
- Pre commit hooks have been set up to ensure commit messages follow the correct format for release-please, which automates our releases. See the [Releases](#releases) section for more details.

Install both hooks with the following command:
```text
pre-commit install
```
