#!/bin/bash

# Quick run script for Pay For Legal Aid Application

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_usage() {
    echo -e "${BLUE}Pay For Legal Aid - Run Script${NC}"
    echo "Usage: $0 [OPTION]"
    echo
    echo "Options:"
    echo "  local     Run locally with Maven (default)"
    echo "  docker    Run with Docker (builds image if needed)"
    echo "  compose   Run with docker-compose"
    echo "  build     Build and run with Docker"
    echo "  stop      Stop all running containers"
    echo "  logs      Show Docker container logs"
    echo "  clean     Clean up Docker resources"
    echo "  help      Show this help message"
}

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

run_local() {
    print_step "Running application locally with Maven"
    print_warning "Make sure you have run tests first to create the H2 database"
    echo "Press Ctrl+C to stop the application"
    echo
    mvn clean install -DskipTests=true
    java -jar -Dspring.profiles.active=local target/pay-for-legal-aid-0.0.1-SNAPSHOT-exec.jar
}

run_docker() {
    print_step "Running application with Docker"
    
    # Check if image exists
    if ! docker images | grep -q payforlegalaid; then
        print_warning "Docker image not found. Building first..."
        ./build-docker.sh
    fi
    
    print_step "Starting container..."
    docker run --rm -p 8443:8443 --name payforlegalaid-container payforlegalaid:latest
}

run_compose() {
    print_step "Running application with docker-compose"
    
    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml not found"
        exit 1
    fi
    
    docker-compose up --build
}

build_and_run() {
    print_step "Building and running with Docker"
    ./build-docker.sh
    run_docker
}

stop_containers() {
    print_step "Stopping all Pay For Legal Aid containers"
    docker stop payforlegalaid-container 2>/dev/null || true
    docker-compose down 2>/dev/null || true
    print_success "Containers stopped"
}

show_logs() {
    print_step "Showing container logs"
    if docker ps | grep -q payforlegalaid; then
        docker logs -f payforlegalaid-container
    elif docker-compose ps | grep -q payforlegalaid; then
        docker-compose logs -f
    else
        print_warning "No running containers found"
    fi
}

clean_docker() {
    print_step "Cleaning up Docker resources"
    docker stop payforlegalaid-container 2>/dev/null || true
    docker-compose down -v --remove-orphans 2>/dev/null || true
    docker rmi payforlegalaid:latest 2>/dev/null || true
    docker system prune -f
    print_success "Docker resources cleaned"
}

# Main script logic
case "${1:-local}" in
    local)
        run_local
        ;;
    docker)
        run_docker
        ;;
    compose)
        run_compose
        ;;
    build)
        build_and_run
        ;;
    stop)
        stop_containers
        ;;
    logs)
        show_logs
        ;;
    clean)
        clean_docker
        ;;
    help|--help|-h)
        print_usage
        ;;
    *)
        print_error "Unknown option: $1"
        print_usage
        exit 1
        ;;
esac