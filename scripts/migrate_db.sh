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

echo "Migrating database..."
mvn flyway:migrate -Dflyway.configFiles=${CONF_FILE}
echo "Done."