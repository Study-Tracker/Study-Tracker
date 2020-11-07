# Study Tracker Configuration

## Configuring your Study Tracker instance

### Users

TODO: Populating the user database

### Metadata and search indexes

TODO: Configuring search/metadata indexes

### Programs

TODO: Adding programs

## Reference

### Profiles

There are a few included application profiles that can be used to provide additional features or configuraions. These can be invoked in several ways:

- Specifying active profiles in the `application.properties` file:

  ```
  spring.profiles.active=example,local
  ```
  
- Specifying the desired profiles at runtime:

  ```bash
  // With maven
  mvn spring-boot:run -Dspring-boot.run.profiles=example,local
  
  // Executable JAR
  java -jar study-tracker.war --spring.profiles.active=example,local
  ```

| Profile | Description |
| ------- | ----------- |
| `example` | Populates the database with example data. WARNING: This will also wipe any existing data from the database. Intended for use with demo installations only. |

### Configuration Properties

### Data source

Study Tracker requires a connection to a MongoDB-compatible database. You must provide host information and user credentials for connecting to the server.

| Property | Default | Description |
| -------- | ------- | ----------- |
| `db.username` | `studytracker` | Database username |
| `db.password` | `studytracker` | Database password |
| `db.host` | `localhost` | Host name for your database |
| `db.name` | `study-tracker` | Database or schema name |
| `db.port` | `27107` | Database host connection port |
| `db.connectionString` | `mongodb://${db.host}:${db.port}` | URL used to connect to the database |

### Web Security

| Property | Default | Description |
| -------- | ------- | ----------- |
| `security.mode` | `demo` | Sets the user authentication mechanism. Defaults to the `demo` mode, which creates a single user with the credentials: l/p: `demo`/`password`. Options: `demo`, `ldap` |

### Electronic Laboratory Notebook (ELN)

| Property | Default | Description |
| -------- | ------- | ----------- |
| `notebook.mode` | `none` | Sets the notebook software to be used, if needed. Options are `none` and `idbs`. |

### Study File Storage

##### Common

| Property | Default | Description |
| -------- | ------- | ----------- |
| `storage.mode` | `local` | Sets the file system or cloud storage service to be used for storing study files. Options are `local` and `egnyte`.  |
| `storage.use-existing` | `false` | Program, assay, or study folders which already exist will be re-used if a new record with the same name is created. If set to `false`, an exception will be thrown when trying to create a folder that already exists. Should only be set to `true` for development or staging applications. |
| `storage.temp-dir` | `/tmp` | Directory used by the application for storing temp files. |

##### Local File System

| Property | Default | Description |
| -------- | ------- | ----------- |
| `storage.local-dir` | `/tmp` | When `storage.mode` is set to `local`, this property will specify the root directory on the local filesystem to be used for storing study files. |

##### Egnyte

| Property | Default | Description |
| -------- | ------- | ----------- |
| `egnyte.root-url` | n/a | Root URL for your organization's Egnyte instance. For example: `https://myorg.egnyte.com`. |
| `egnyte.api-token` | n/a | API token used to authenticate request |
| `egnyte.root-path` | n/a | Root directory within the Egnyte filesystem that will be used for storing study files. Eg. `Shared/General/StudyTracker` |
| `egnyte.qps` | 1 | Maximum number of queries-per-second that are allowed by Egnyte to their REST API. Setting this value will throttle requests so that they do not exceed the allowed maximum rate. |

### Study Metadata

| Property | Default | Description |
| -------- | ------- | ----------- |
| `study.default-code-prefix` | `ST` | Default prefix for generating study codes, if one is not provided by the associated program. |
| `study.default-external-code-prefix` | `EX` | Default prefix for generating study codes, if one is not provided by the associated external collaborator. |

### Events

Study Tracker keeps track of various study lifecycle events, which are triggered by users interacting with the system. This activity is stored within the main Study Tracker database, but is also sent externally using event dispatchers. Event listeners can be configured to trigger secondary actions and events in a way that allow customization or integrations that the core application does not support. By default, Study Tracker will dispatch events within the application environment, but it can also be configured to dispatch event externally to services such as WS EventBridge.   

##### Common

| Property | Default | Description |
| -------- | ------- | ----------- |
| `events.mode` | `local` | Sets the event dispatcher/handler mode. Options are `local` and `eventbridge`. |

##### AWS EventBridge

| Property | Default | Description |
| -------- | ------- | ----------- |
| `aws.eventbridge.bus-name` | n/a | Nave of the EventBridge bus you would like to recieve Study Tracker events. |

### Amazon Web Services (AWS)

Study Tracker can integrate directly with AWS services, including EventBridge, using the official AWS Java SDK. In order to connect to your services, you will have to provide a developer key and secret token.

| Property | Default | Description |
| -------- | ------- | ----------- |
| `aws.access-key-id` | n/a | Access key of the account you would like to connect to AWS with. |
| `aws.secret-access-key` | n/a | Secret key to authenticate the user account. |
| `aws.region` | n/a | AWS region you would like to connect to. | 

### Benchling

| Property | Default | Description |
| -------- | ------- | ----------- |
| `benchling.eln.api.token` | n/a | HTTP Basic-digested token used to authenticate the user. Can be used in place of provided `username` and `password` | 
| `benchling.eln.api.username` | n/a | User who all posts made to Benchling will be attributes to. |
| `benchling.eln.api.password` | n/a | Password for the provided user. |
| `benchling.eln.api.root-url` | n/a | Root URL of your Benchling tenant. |
| `benchling.eln.api.root-entity` | n/a | Root entity ID. |
| `benchling.eln.api.root-folder-url` | n/a | ID of the root folder in the project hierarchy. |