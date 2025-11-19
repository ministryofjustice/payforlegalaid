#!/bin/bash

# Build and Dockerize Pay For Legal Aid Application
# This script builds the application, runs acceptance tests to create H2 database, and builds Docker image

set -e  # Exit on any error

echo "🏗️  Building Pay For Legal Aid Application with Docker"
echo "=========================================="

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_step() {
    echo -e "${BLUE}➡️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed or not in PATH"
    exit 1
fi

if [ ! -f "pom.xml" ]; then
    print_error "pom.xml not found. Please run this script from the project root directory."
    exit 1
fi

print_step "Step 1: Clean and build the application"
mvn clean install -DskipTests || {
    print_error "Maven build failed"
    exit 1
}
print_success "Application built successfully"

print_step "Step 2: Initialize H2 database for Docker"
print_warning "Initializing H2 database with required schema and data..."
# Create a temporary directory for database initialization
mkdir -p target/docker-db-init
# Run database initialization
./init-database.sh
print_success "Database initialization completed"

print_step "Step 3: Check if JAR file exists"
if [ ! -f "target/pay-for-legal-aid-0.0.1-SNAPSHOT-exec.jar" ]; then
    print_error "JAR file not found. Build may have failed."
    exit 1
fi
print_success "JAR file found"

print_step "Step 4: Build Docker image"
docker build -t payforlegalaid:latest . || {
    print_error "Docker build failed"
    exit 1
}
print_success "Docker image built successfully"

print_step "Step 5: Verify Docker image"
docker images | grep payforlegalaid || {
    print_error "Docker image not found after build"
    exit 1
}

print_success "🎉 Build completed successfully!"
echo
echo -e "${BLUE}📋 Next steps:${NC}"
echo "  • Run with Docker: docker run -p 8080:8080 payforlegalaid:latest"
echo "  • Run with docker-compose: docker-compose up"
echo "  • Access application: http://localhost:8080"
echo "  • Health check: http://localhost:8080/actuator/health"
echo
echo -e "${YELLOW}💡 Tips:${NC}"
echo "  • The H2 database data will be persisted in Docker volumes"
echo "  • Add template files to ./templates/ directory if needed"
echo "  • Use 'docker-compose logs' to view application logs"