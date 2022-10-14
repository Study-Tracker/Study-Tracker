# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
- Working on a new feature to allow creation of permanent API tokens for use by integrations. This will allow admin users to create a token that can be used to authenticate to the API without having to use their username and password. This will be useful for integrations that need to access the API, but do not have a way to store a username and password.
- Study Tracker will eventually deprecate support of Elasticsearch 7.10 in favor of newer versions. 

## [0.7.1] - 2022-XX-XX

### Added
- Added File Manager tool to the Study Tracker UI. This tool allows the browsing of storage locations registered within Study Tracker. Users can navigate & create folders, and upload & download files.
- Added support for Amazon Web Services S3 storage locations. This allows users to store files in S3 buckets.
- Added support for creating API users, which can be used exclusively for API access. This allows for easier management of integrations and removes the need to use a user's credentials for API access.

### Changed
- Changed the signature of the JWT created and returned to authenticated users to include creation and expiration times.

### Fixed
- Fixed validation of Quill input fields to prevent empty inputs.
- Fixed validation of required custom assay type fields.
- Fixed validation of key-value attribute fields.
- Fixed a number of small UI bugs.

## [0.7.0] - 2022-09-26

Study Tracker version 0.7.0 is the first of several transitional updates that will modernize the front and back ends of the application. These updates are primarily designed to utilize newer versions of dependencies, remove deprecated dependencies, and mitigate security vulnerabilities. A significant number of new features will be added, as well, in order to take the application in a direction that will broaden its utility.

### Added
- Added a new, versioned public API intended for use by external applications and users. The existing Swagger API documentation now covers this API. This API supports JWT-based authentication. The existing API has been 'hidden' and is now intended only for use by the front-end web application, but otherwise functions the same.
- Assays are now indexed by Elasticsearch, allowing for a more effective power search tool.

### Changed
- Study Tracker now uses the `frontend-maven-plugin` to download NPM & NodeJS and build the front-end web application. This allows for a more streamlined build process and removes the requirement for NodeJS & NPM installation from the user.
- Updated the front-end to use React 17.0.2. Rewrote a lot of the front-end code to use modern techniques, such as React Hooks and Formik, which will make future development easier.
- The old API has been hidden from the Swagger documentation. It is still available, but is not intended for use by external applications and users.
- Benchling notebook entry templates no longer require registration in the front-end to allow their usage when creating studies and assays. Users can now select from any existing template in their tenant when creating studies and assays.
- Improved parameterization of Elasticsearch in `.properties` files.

### Removed
- Removed the admin dashboard functionality for registering notebook entry templates.

### Deprecated
- The `NotebookEntryTemplate` model and associated classes have been deprecated and are no longer in active use. They will be removed in a future release, which will include a database migration script.

### Fixed
- Fixed a number of minor UI bugs.


## [0.6.9] - 2022-06-14

### Added
- Added 'Features' API endpoint. This endpoint returns a list of features that are enabled for the current tenant. This is intended to be used by the front-end to determine which features are available for the current user.
- Added support for externalized SAML keystore files.

### Changed
- Study and Assay Details pages' Files tabs will now not load all remote content by default. The user must now click a button to load the content. This is intended to improve performance for users with large numbers of files, and also prevent exhausting Egnyte's API rate limit.

### Fixed
- Fixed sign-out redirects for SAML users.
- Fixed folder repair functionality.


## [0.6.8] - 2022-05-31

### Added
- Added Swagger UI link to the admin dashboard.
- Added the ability to enable/disable users from the admin dashboard.
- Added the ability to reset user password in the admin dashboard.
- Added CSRF protection to the API.

### Changed
- Refactored the base package structure, updated Java dependencies.

### Fixed
- Fixed assay edit page login redirect bug.