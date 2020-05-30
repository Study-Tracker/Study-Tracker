# Study Tracker 

Study management software for research organizations. 

## Requirements

- JDK 11+
- Maven 3+
- MongoDB 3+
- NPM and Node.JS

## Documentation

Details documentation (work-in-progress) can be found in the `documentation` folder of the repository.

## Supported Integrations

### Electronic Laboratory Notebook

- IDBS

### File storage

- Egnyte
- Sharepoint

### Messaging

- Microsoft Teams

### User Authentication

- Active Directory LDAP

## Building and running

To build and run the project locally: 

1. Make sure you have all requirements installed.
2. Create a new MongoDB database and user account.
3. If you plan on doing local development or running tests, perform the following steps: 
    3a. Create a test database and user account.
    3b. Add a `test.properties` file in the `src/test/resources` folder of each module of the project. Use the provided `test.properties.example` file as a template for filling in the required information.
4. Create a new file, `src/main/resources/application.properties`. Use the `application.properties.example` file as a template for filling out the required parameters for running the application.
5. Build the application in the following order:
   
    ```bash
    npm install
    npm run build
    mvn clean package
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

Copyright 2020 Decibel Therapeutics

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.