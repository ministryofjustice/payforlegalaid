# Docker Setup for Local Development

This document describes how to run the Pay For Legal Aid application using Docker for local development.

## Prerequisites

- Docker (version 20.10 or later)
- Docker Compose (version 2.0 or later)

## Quick Start

### Using Docker Compose (Recommended)

1. Build and start the application:
   ```bash
   docker-compose up --build
   ```

2. The application will be available at:
   - API: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console (if enabled)

3. To stop the application:
   ```bash
   docker-compose down
   ```

4. To stop and remove volumes (including the H2 database):
   ```bash
   docker-compose down -v
   ```

### Using Docker Only

1. Build the Docker image:
   ```bash
   docker build -t payforlegalaid:local .
   ```

2. Run the container:
   ```bash
   docker run -d \
     -p 8080:8080 \
     -v payforlegalaid-h2:/app/data \
     --name payforlegalaid-local \
     payforlegalaid:local
   ```

3. View logs:
   ```bash
   docker logs -f payforlegalaid-local
   ```

4. Stop and remove the container:
   ```bash
   docker stop payforlegalaid-local
   docker rm payforlegalaid-local
   ```

## Architecture

The Dockerfile uses a multi-stage build process:

1. **Builder Stage**: 
   - Installs git for cloning dependencies
   - Runs `build-openapi.sh` to checkout and build the OpenAPI spec dependency from the [payforlegalaid-openapi](https://github.com/ministryofjustice/payforlegalaid-openapi) repository
   - Compiles the application using Maven
2. **Tester Stage**: Runs tests to initialize the H2 database schema
3. **Runtime Stage**: Runs the application with the local profile

### OpenAPI Dependency

The `build-openapi.sh` script mirrors the GitHub Actions workflow logic:
- Extracts the OpenAPI version from `pom.xml`
- Clones the `payforlegalaid-openapi` repository at the specified version tag
- Builds and installs the OpenAPI dependency to the local Maven repository

This ensures that the Docker build has access to the same OpenAPI spec used in CI/CD.

## Database

The application uses an H2 file-based database for local development. The database is:
- Stored in a Docker volume named `h2-data`
- Persisted between container restarts
- Configured in Oracle mode to match production behavior
- Initialized by running the test suite during the Docker build

### Accessing the H2 Console

If the H2 console is enabled in your local configuration, you can access it at:
http://localhost:8080/h2-console

Connection settings:
- JDBC URL: `jdbc:h2:file:/app/data/localGpfdDb`
- Username: `sa`
- Password: (leave empty)

## Configuration

The application runs with the `local` Spring profile by default. You can override this by setting the `SPRING_PROFILES_ACTIVE` environment variable:

```bash
docker-compose up -e SPRING_PROFILES_ACTIVE=dev
```

## Troubleshooting

### Container fails to start

Check the logs:
```bash
docker-compose logs -f payforlegalaid
```

### OpenAPI dependency build fails

The build requires access to the public GitHub repository `ministryofjustice/payforlegalaid-openapi`. If you encounter errors:

1. Ensure you have internet connectivity
2. Verify the version tag exists in the [OpenAPI repository](https://github.com/ministryofjustice/payforlegalaid-openapi/tags)
3. Check the `payforlegalaid-openapi.version` property in `pom.xml`

You can also run the build script locally to test:
```bash
./build-openapi.sh
```

### Database issues

Remove the volume and rebuild:
```bash
docker-compose down -v
docker-compose up --build
```

### Build fails

Clear Docker cache and rebuild:
```bash
docker-compose build --no-cache
docker-compose up
```

## Development Workflow

For active development, you may want to:

1. Keep the container running:
   ```bash
   docker-compose up
   ```

2. In another terminal, rebuild and restart when you make changes:
   ```bash
   docker-compose up --build -d
   ```

3. View live logs:
   ```bash
   docker-compose logs -f
   ```

## Resource Requirements

The local Docker setup uses reduced memory settings compared to production:
- Initial heap: 512MB
- Maximum heap: 1GB

For production deployment, refer to the production Dockerfile.
