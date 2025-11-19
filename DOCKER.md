# Docker Deployment Guide

This guide explains how to dockerize and run the Pay For Legal Aid (GPFD) application.

## Quick Start

### Option 1: Using the build script (Recommended)
```bash
./build-docker.sh
```

### Option 2: Using the run script
```bash
./run.sh build    # Build and run with Docker
./run.sh compose  # Run with docker-compose
./run.sh local    # Run locally with Maven
```

### Option 3: Manual steps
```bash
mvn clean install

# Run tests to create H2 database
mvn test -Dspring.profiles.active=test

docker build -t payforlegalaid:latest .

docker run -p 8080:8080 payforlegalaid:latest
```

## Application Access

Once running, the application will be available at:
- **Main Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **H2 Console**: http://localhost:8080/h2-console (if enabled)

## Database Setup

The application uses an H2 database for local development. The database is automatically initialized during the Docker build process using Liquibase migrations.

### Database Initialization
The database is initialized with:
1. **Schema creation**: All required tables (REPORTS, REPORT_QUERIES, FIELD_ATTRIBUTES, etc.)
2. **Test data**: Sample reports and configuration data
3. **Liquibase changesets**: Applied in the correct order during build

### Database Configuration
- **URL**: `jdbc:h2:file:/app/data/gpfd`
- **Driver**: `org.h2.Driver`
- **Mode**: Oracle compatibility mode
- **Data location**: Persisted in Docker volume `h2_data`

### Manual Database Initialization
If you need to reinitialize the database:
```bash
./init-database.sh
```

## Environment Variables

You can customize the application using environment variables in `docker-compose.yml`:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=local
  - SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/gpfd;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE
  - SPRING_H2_CONSOLE_ENABLED=true
```

## Volume Mounts

The docker-compose setup includes these volumes:
- `h2_data:/app/data` - Database persistence
- `app_logs:/app/logs` - Application logs
- `./templates:/app/templates:ro` - Template files (read-only)

## Template Files

If you need to add Excel template files for reports:
1. Create a `templates` directory in the project root
2. Add your `.xlsx` template files
3. They will be automatically mounted into the container

## Troubleshooting

### Build Issues
```bash
# Check if all dependencies are available
mvn dependency:resolve

# Clean everything and rebuild
mvn clean
rm -rf target/
./build-docker.sh
```

### Runtime Issues
```bash
# Check container logs
docker logs payforlegalaid-container

# Or with docker-compose
docker-compose logs -f

# Check health
curl http://localhost:8080/actuator/health
```

### Database Issues
```bash
# If you get "Failed to fetch reports from database" error:
# 1. Check if database files exist
docker exec payforlegalaid-app ls -la /app/data/

# 2. Recreate database with fresh initialization
docker-compose down -v
./init-database.sh
docker build -t payforlegalaid:with-db .
docker-compose up -d

# 3. Check database connection
docker exec payforlegalaid-app ls -la /app/data/

# Other database troubleshooting:
```bash
# Remove database volume to start fresh
docker-compose down -v
docker volume rm payforlegalaid_h2_data

# Run tests to recreate database
mvn test -Dspring.profiles.active=test
```

### Container Resources
You can limit container resources in docker-compose.yml:
```yaml
deploy:
  resources:
    limits:
      memory: 3G
      cpus: '2'
```

## Development Workflow

1. **Local Development**:
   ```bash
   ./run.sh local
   ```

2. **Docker Development**:
   ```bash
   ./run.sh build
   ```

3. **Testing Changes**:
   ```bash
   mvn clean install
   ./build-docker.sh
   ./run.sh docker
   ```

4. **Clean Up**:
   ```bash
   ./run.sh clean
   ```

### build-docker.sh
- Builds Maven project
- Runs acceptance tests
- Creates Docker image
- Validates build

### run.sh options
- `local` - Run with Maven locally
- `docker` - Run with Docker
- `compose` - Run with docker-compose
- `build` - Build and run Docker
- `stop` - Stop containers
- `logs` - Show logs
- `clean` - Clean up resources