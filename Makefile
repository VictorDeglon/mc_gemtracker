# Makefile for GemTracker Bukkit plugin
# Provides convenience targets for building, testing and cleaning the project.

# Default target builds the plugin JAR
.PHONY: all
all: build

# Build the plugin using Gradle
.PHONY: build
build:
gradle build

# Run unit tests (none currently but kept for future additions)
.PHONY: test
test:
gradle test

# Clean build artifacts
.PHONY: clean
clean:
gradle clean
