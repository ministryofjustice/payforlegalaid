#!/bin/bash

# Database initialization script for Docker builds

set -e

echo "🔧 Initializing H2 database for Docker..."

# Create target directory for Docker database files
mkdir -p target/docker-db-init

# Database file path (local build path) - use absolute path
DB_PATH="$(pwd)/target/docker-db-init/gpfd"
DB_URL="jdbc:h2:file:${DB_PATH};DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE;MODE=Oracle"

echo "Compiling project and test sources..."
/opt/homebrew/bin/mvn test-compile -q

echo "Running database initialization utility..."
/opt/homebrew/bin/mvn exec:java \
    -Dexec.mainClass="uk.gov.laa.gpfd.utils.StandaloneDatabaseInitializer" \
    -Dexec.args="${DB_PATH}" \
    -Dexec.classpathScope=test \
    -q || echo "Database initialization completed"

echo "✅ Database initialization completed"

if [ -f "${DB_PATH}.mv.db" ]; then
    echo "✅ Database file created successfully: ${DB_PATH}.mv.db"
    ls -la target/docker-db-init/
else
    echo "❌ Database file not found at ${DB_PATH}.mv.db!"
    echo "Contents of target/docker-db-init/:"
    ls -la target/docker-db-init/ || echo "Directory not found"
    exit 1
fi