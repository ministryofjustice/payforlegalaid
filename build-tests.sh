#!/bin/bash
set -e

echo "===================================="
echo "Building Test Database Setup"
echo "===================================="

# Install the main application JAR to local Maven repo
echo "Installing main application to local Maven repository..."
cd /build
mvn -B install -DskipTests

# Check if tests directory already exists
if [ -d "payforlegalaid-tests" ]; then
    echo "Cleaning up existing payforlegalaid-tests directory..."
    rm -rf payforlegalaid-tests
fi

# Clone the tests repository
echo "Cloning payforlegalaid-tests repository..."
git clone --depth 1 https://github.com/ministryofjustice/payforlegalaid-tests.git

# Build and run tests to set up the database
echo "Building and running tests to initialize database..."
cd payforlegalaid-tests
mvn -B clean compile
mvn -B test || echo "Tests completed with failures, but database should be initialized. Continuing..."

echo "===================================="
echo "Test database setup completed successfully"
echo "===================================="
