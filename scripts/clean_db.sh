#!/bin/bash

# Test if run directory is correct
CONF_FILE=$(pwd)/flyway.conf
if [ ! -f "$(pwd)/pom.xml" ]; then
    echo "Please run this script from the root directory of the project."
    exit 1
fi
if [ ! -f "${CONF_FILE}" ]; then
    echo "flyway.conf file does not exist."
    exit 1
fi

read -p "Are you sure you want to clean the database? All data will be lost and migrations will be reapplied. (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo "Cleaning database..."
    mvn flyway:clean flyway:migrate -Dflyway.configFiles="${CONF_FILE}"
    echo "Done."
fi
