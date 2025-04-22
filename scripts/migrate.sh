#!/bin/bash

# Get the directory of this script
RUN_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
MIGRATION_DIR="$RUN_DIR/../web/src/main/resources/db/migration"

# Function to check if a file exists
check_file_exists() {
    if [ ! -f "$1" ]; then
        echo "Error: Configuration file not found: $1"
        exit 1
    fi
}

# Function to run Flyway commands
run_migrate() {
    local config_file=$1
    echo "Running flyway migrate with config file $config_file..."
    flyway -locations="filesystem:$MIGRATION_DIR" -configFiles="$config_file" migrate
}

run_clean() {
    local config_file=$1
    echo "Running flyway clean with config file $config_file..."
    flyway -locations="filesystem:$MIGRATION_DIR" -configFiles="$config_file" -cleanDisabled=false clean
}

# Check if at least one argument is provided
if [ $# -lt 1 ]; then
    echo "Usage: $0 [-c] <config_file1> [<config_file2> ...]"
    exit 1
fi

# Check for the -c option
CLEAN=0
if [ "$1" == "-c" ]; then
    CLEAN=1
    shift # Shift arguments to the left
fi

# Iterate over all the remaining arguments
for CONFIG_FILE in "$@"; do
    check_file_exists "$CONFIG_FILE"

    # Run Flyway clean if the -c option is set
    if [ $CLEAN -eq 1 ]; then
        run_clean "$CONFIG_FILE"
    fi

    # Run Flyway migrate
    run_migrate "$CONFIG_FILE"
done