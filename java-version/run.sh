#!/bin/bash

# Azure PostgreSQL AAD Demo Runner Script
# This script builds and runs the Azure PostgreSQL AAD authentication demo

set -e  # Exit on any error

echo "=== Azure PostgreSQL AAD Demo Runner ==="
echo

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed or not in PATH"
    echo "Please install Java 11 or higher"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "?(1\.)?\K\d+' | head -1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "Java 11 or higher is required (found Java $JAVA_VERSION)"
    exit 1
fi

echo "Java version: $(java -version 2>&1 | head -1)"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed or not in PATH"
    echo "Please install Apache Maven 3.6+"
    exit 1
fi

echo "Maven version: $(mvn -version | head -1)"
echo

# Build the project
echo "Building the project..."
mvn clean compile -q

if [ $? -eq 0 ]; then
    echo "Build successful"
else
    echo "Build failed"
    exit 1
fi

echo

# Run the application
echo "Running Azure PostgreSQL AAD Demo..."
echo

# Execute the main class
mvn exec:java -Dexec.mainClass="com.example.demo.AzurePostgreSqlAadDemo" -q

echo
echo "=== Demo execution completed ==="