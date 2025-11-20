#!/bin/bash

# Build PayForLegalAid OpenAPI dependency
# This script mirrors the GitHub Action logic to checkout and build the OpenAPI spec dependency

set -e  # Exit on any error

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

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    print_error "pom.xml not found. Please run this script from the project root directory."
    exit 1
fi

print_step "Getting OpenAPI version from pom.xml"
VERSION=$(mvn help:evaluate -Dexpression="payforlegalaid-openapi.version" -q -DforceStdout 2>/dev/null || echo "")

if [ -z "$VERSION" ]; then
    print_error "Could not extract payforlegalaid-openapi.version from pom.xml"
    exit 1
fi

print_success "Found OpenAPI version: $VERSION"

# Check if the dependency is already available in local repository
print_step "Checking if OpenAPI dependency is already available locally"
if mvn dependency:get -Dartifact=uk.gov.laa:payforlegalaid-openapi:$VERSION -q 2>/dev/null; then
    print_success "OpenAPI dependency v$VERSION is already available in local Maven repository"
    exit 0
fi

print_warning "OpenAPI dependency not found locally, will checkout and build from source"

# Clean up any existing openapi directory
if [ -d "openapi" ]; then
    print_step "Cleaning up existing openapi directory"
    rm -rf openapi
fi

print_step "Checking out payforlegalaid-openapi repository (tag v$VERSION)"
if ! git clone --branch "v$VERSION" --depth 1 https://github.com/ministryofjustice/payforlegalaid-openapi.git openapi; then
    print_error "Failed to checkout payforlegalaid-openapi repository at tag v$VERSION"
    print_error "Please check that tag v$VERSION exists in the repository"
    exit 1
fi

print_step "Building OpenAPI spec dependency"
cd openapi
if ! mvn -B clean install -q; then
    print_error "Failed to build payforlegalaid-openapi dependency"
    exit 1
fi
cd ..

print_success "OpenAPI dependency built and installed to local Maven repository"

# Clean up the checked out repository
print_step "Cleaning up temporary checkout"
rm -rf openapi

print_success "🎉 OpenAPI dependency build completed successfully!"