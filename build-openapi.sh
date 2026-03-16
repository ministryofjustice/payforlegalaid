#!/bin/bash
set -e

echo "===================================="
echo "Building OpenAPI Dependency"
echo "===================================="

# Get OpenAPI version from pom.xml
echo "Extracting OpenAPI version from pom.xml..."
OPENAPI_VERSION=$(mvn help:evaluate -Dexpression=payforlegalaid-openapi.version -q -DforceStdout)

if [ -z "$OPENAPI_VERSION" ]; then
    echo "ERROR: Could not extract OpenAPI version from pom.xml"
    exit 1
fi

echo "OpenAPI version: $OPENAPI_VERSION"

# Check if openapi directory already exists and has the correct version
if [ -d "openapi" ]; then
    echo "Cleaning up existing openapi directory..."
    rm -rf openapi
fi

# Clone the OpenAPI repository at the specific version tag
echo "Cloning payforlegalaid-openapi repository (tag: v$OPENAPI_VERSION)..."
git clone --depth 1 --branch "v$OPENAPI_VERSION" \
    https://github.com/ministryofjustice/payforlegalaid-openapi.git openapi

# Build the OpenAPI dependency
echo "Building OpenAPI dependency..."
cd openapi
mvn -B clean install

echo "===================================="
echo "OpenAPI dependency built successfully"
echo "===================================="
