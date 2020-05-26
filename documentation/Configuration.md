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

| Profile | Description |
| ------- | ----------- |
| `example` | Populates the database with example data. WARNING: This will also wipe any existing data from the database. Intended for use with demo installations only. |

### Configuration Properties

### Data source

| Property | Default | Description |
| -------- | ------- | ----------- |
| `db.username` | `studytracker` | Database username |
| `db.password` | `studytracker` | Databas password |
| `db.host` | `localhost` | Host name for your database |
| `db.name` | `study-tracker` | Database or schema name |
| `db.port` | `27107` | Database host connection port |
| `db.connectionString` | `mongodb://${db.host}:${db.port}` | URL used to connect to the database |

### Web Security

| Property | Default | Description |
| -------- | ------- | ----------- |
| `security.mode` | `demo` | Sets the user authentication mechanism. Defaults to the `demo` mode, which creates a single user with the credentials: l/p: `demo`/`password`. Options: `demo`, `ldap` |

### Document writing

| Property | Default | Description |
| -------- | ------- | ----------- |
| `documents.slideshow.template` | n/a | Classpath location of the PowerPoint template to be used for writing study summary slideshows. This file should be placed within the web moduel's `src/main/resources` folder. |

### Electronic Laboratory Notebook (ELN)

| Property | Default | Description |
| -------- | ------- | ----------- |
| `notebook.mode` | `none` | Sets the notebook software to be used, if needed. Options are `none` and `idbs`. |

### Study File Storage

| Property | Default | Description |
| -------- | ------- | ----------- |
| `storage.mode` | `local` | Sets the file system or cloud storage service to be used for storing study files. Options are `local` and `egnyte`.  |
| `storage.use-existing` | `false` | Program, assay, or study folders which already exist will be re-used if a new record with the same name is created. If set to `false`, an exception will be thrown when trying to create a folder that already exists. Should only be set to `true` for development or staging applications. |
| `storage.temp-dir` | `/tmp` | Directory used by the application for storing temp files. |
| `storage.local-dir` | `/tmp` | When `storage.mode` is set to `local`, this property will specify the root directory on the local filesystem to be used for storing study files. |
| `egnyte.root-url` | n/a | Root URL for your organization's Egnyte instance. For example: `https://myorg.egnyte.com`. |
| `egnyte.api-token` | n/a | API token used to authenticate request |
| `egnyte.root-path` | n/a | Root directory within the Egnyte filesystem that will be used for storing study files. Eg. `Shared/General/StudyTracker` |
| `egnyte.qps` | 1 | Maximum number of queries-per-second that are allowed by Egnyte to their REST API. Setting this value will throttle requests so that they do not exceed the allowed maximum rate. |

### Study Metadata

| Property | Default | Description |
| -------- | ------- | ----------- |
| `study.default-code-prefix` | `ST` | Default prefix for generating study codes, if one is not provided by the associated program. |
| `study.default-external-code-prefix` | `EX` | Default prefix for generating study codes, if one is not provided by the associated external collaborator. |

### LDAP

| Property | Default | Description |
| -------- | ------- | ----------- |
| `ldap.url` | n/a | URL used to connect to the LDAP server. |
| `ldap.domain` | n/a | LDAP organization domain |
| `ldap.base` | n/a | LDAP directory base |
| `ldap.searchBase` | n/a | LDAP directory search base |
| `ldap.filter` | n/a | LDAP search filter |
| `ldap.admin.username` | n/a | LDAP admin account username |
| `ldap.admin.password` | n/a | LDAP admin account password |

### Microsoft Teams

| Property | Default | Description |
| -------- | ------- | ----------- |
| `teams.enabled` | `false` | When true, Teams will be used for messaging. |
| `teams.username` | n/a | Teams username for account to post messages |
| `teams.password` | n/a | Teams user password |
| `teams.client-id` | n/a | Teams instance client ID |
| `teams.secret` | n/a | Teams instance secret ID |
| `teams.default-team` | n/a | Name of the team that Study Tracker will make posts to, by default. |
| `teams.default-channel` | n/a | Channel within the above team that Study Tracker will post to, by default. |