# Study Tracker 

Study Tracker makes the job of managing your team's research easier by providing a user-friendly web
application that serves as a single source-of-truth for your organization, and saves time by
integrating other required platforms, such as electronic laboratory notebooks (ELNs) and data file
storage systems. Study Tracker also includes a command line interface (CLI), a REST API, and an
event dispatcher service for easy data access and integration with external systems.

## Requirements

- JDK 11+
- Maven 3+
- MongoDB 3+
- NPM and Node.JS

## Documentation

For instructions about how to configure and deploy Study
Tracker, [see the wiki](https://github.com/Decibel-Therapeutics/Study-Tracker/wiki).

## Supported Integrations

### Electronic Laboratory Notebook

- Benchling

### File storage

- Egnyte
- Local filesystem

### Event Management

- AWS EventBridge

## Building and running locally

1. Make sure you have all requirements installed.
2. Create a new MongoDB database and user account.
3. If you plan on doing local development or running tests, perform the following steps:
   3a. Create a test database and user account. 3b. Add a `test.properties` file in
   the `src/test/resources` folder of each module of the project. Use the
   provided `test.properties.example` file as a template for filling in the required information.
4. Create a new file, `src/main/resources/application.properties`. Use the
   `application.properties.example` file as a template for filling out the required parameters for
   running the application.
5. Build the application in the following order:

    ```bash
    npm install
    npm run build
    mvn clean install -DskipTests
    ```

6. You can run the application with Maven:

    ```bash
   mvn spring-boot:run 
   ```
   
   Or, you can execute the packaged WAR file directly:
   
   ```bash
   java -jar study-tracker.war
   ```

## Contact

For questions about Study Tracker, or if you would like to contribute, please contact:

- [Will Oemler, Decibel Therapeutics](mailto:woemler@decibeltx.com)

## License

Copyright 2021 Decibel Therapeutics

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