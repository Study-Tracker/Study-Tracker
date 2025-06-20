# Study Tracker

![example branch parameter](https://github.com/Study-Tracker/Study-Tracker/actions/workflows/build-and-test.yml/badge.svg?branch=main)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/ff2b66794fc540d5a74bda46d5913d37)](https://www.codacy.com/gh/Study-Tracker/Study-Tracker/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Study-Tracker/Study-Tracker&amp;utm_campaign=Badge_Grade)
[![Documentation](https://img.shields.io/badge/GitBook-Documentation-lightblue?logo=gitbook)](https://study-tracker.gitbook.io/documentation/)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FStudy-Tracker%2FStudy-Tracker.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FStudy-Tracker%2FStudy-Tracker?ref=badge_shield)

Study Tracker makes the job of managing your team's research easier by providing a user-friendly web
application that serves as a single source-of-truth for your organization. Save time by
connecting with other required platforms, such as electronic laboratory notebooks (ELNs), data file
storage systems, and team messaging services. Integrate Study Tracker with other platforms via an
integrated REST API and event dispatcher service for sharing of data.

![Study Tracker front page](media/front-page.png)

Looking for a fully-managed Study Tracker? Take a look at [Lab Atlas](https://labatlas.com). 

## Requirements

- JDK 17+
- Maven 3+
- PostgreSQL 12+
- OpenSearch 2+ (optional)

NodeJS and NPM are *not* required for building and running the project, as the
`frontend-maven-plugin` installs them at build time, in order to properly compile the front-end of
the application.

### Upgrading from Study Tracker v0.9 or earlier

Starting in v1.0, Study Tracker now requires Java 17 and now supports OpenSearch in place of 
ElasticSearch. Some older initialization scripts have also been removed, so if you are coming 
from earlier than v0.9.0, you will first want to upgrade to v0.9.11 and then to v1.0+.

## Documentation

For instructions about how to configure deploy Study
Tracker, [see Gitbook](https://study-tracker.gitbook.io/documentation/).

## Supported Integrations

### Electronic Laboratory Notebook

- Benchling

### File storage

- Microsoft SharePoint & OneDrive
- Egnyte
- Amazon S3
- Local filesystem

### Event Management

- AWS EventBridge

### Single Sign-on

- Okta
- Microsoft Entra ID (Azure AD)

### Source Code Management

- GitLab

## Quick Start

For a rapid build and deployment in development mode, follow the steps below. For production deployment, see the wiki.

1. Make sure you have all requirements installed.
2. Create a new PostgreSQL database.
3. Create a new file, `web/src/main/resources/application.properties`. Use the
   `application.properties.example` file as a template for filling out the required parameters for
   running the application.
4. Create a new file, `web/flyway.conf`, using the `flyway.conf.example` file as a template. Fill in
   your database's username, password, and connection URL.
5. Build the application with the included Maven wrapper:

    ```bash
    ./mvnw clean package -DskipTests
    ```

6. Run the Flyway plugin to import the Study Tracker database schema and default data:

   ```bash
   ./mvnw -Dflyway.configFiles=web/flyway.conf flyway:clean flyway:migrate
   ```

7. You can run the application with Maven from the `web` directory:

   ```bash
   ./mvnw spring-boot:run 
   ```

   Or, you can execute the packaged WAR file directly:

   ```bash
   java -jar web/target/study-tracker.war
   ```

## Docker Support

An official Study Tracker Docker image is available from the [GitHub Container Registry](https://github.com/Study-Tracker/Study-Tracker/pkgs/container/study-tracker). 
You can build the image locally from source by running the following command from the root of the project:

```bash
docker buildx build --platform linux/amd64 -t study-tracker .
```

An example `docker-compose.yml` file is available in the root of the project, which you can use 
for quickly spinning up a local instance of Study Tracker with PostgreSQL. 
1. Create a folder name`.docker`.
2. Create two folders inside `.docker` named `config` and `data`.
3. Create a file named `application.properties` inside the `.docker/config` folder, and copy the 
   following contents into it:
   ```properties
   server.port=8080
   application.host-name=localhost
   application.secret=studytrackerstudytracker
   admin.email=your-email@domain.com
   admin.password=password
   storage.temp-dir=/tmp
   storage.local-dir=/data
   ```
4. Run the following command from the root of the project to start the application:

   ```bash
   docker compose -f docker-compose.yml up
   ```
5. You can now access Study Tracker at `http://localhost:8080` and log in with the email and 
   password defined in the `application.properties` file.

## Contact

For questions about Study Tracker, or if you would like to contribute, please contact:

- [Will Oemler, Dotwise Software](mailto:will@dotwisesoftware.com)

For support or feature requests:

- [support@dotwisesoftware.com](mailto:support@dotwisesoftware.com)

## License

Copyright 2019-2025 the original authors

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing permissions and limitations under the
License.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing permissions and limitations under the
License.
