# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
- Study Tracker will eventually deprecate support of Elasticsearch 7.10 in favor of newer versions.
- Working on a new module for capturing data sets and their metadata. This module will allow the registration of various data sources (such as AWS S3), and the creation of data set records that will include one-or-more resources from these storage locations, along with metadata describing them. Users will then be allowed to associate one-or-more data sets with a study. Studies will likely get data set records created for them, which will include their storage folder and notebook by default.
- Will add a notifications feature that will capture and display notifications for users.

## [0.9.7] - 2024-04-24

### Added
- Added link to documentation in the navbar user menu.

### Fixed
- Fixed an issue where active Benchling integration would not be properly be recognized when 
  creating new programs.

## [0.9.6] - 2024-03-10

### Added
- The default storage folder for studies and assays can now be changed in the Files tab of the Study and Assay Details pages. It is also now possible to remove non-default folders from studies and assays.
- Studies can now be moved from one program to another. This will result in a new study code and new storage/ELN folders for the study. Old folders will still be associated with the study.
- Assays can also be moved from one study to another. This will result in a new assay code and new storage/ELN folders for the assay. Old folders will still be associated with the assay.

### Changed
- Simplified the Keywords data model, combining the category and keyword fields into a single table. This will make it simpler to search for keywords and to register keywords on-the-fly.
- Leading and trailing whitespace for program, study, and assay names will now trigger a form validation error. This is to prevent issues with folder creation and other downstream processes. (#159)
- When adding a folder to a study or assay in the File Manager, it will now have read-write permissions by default, with the option to make it read-only. (#162)
- Updated the admin dashboard settings for Benchling to allow creating/updating integration details within the UI. Integrations can still be initialized with existing `application.properties` config parameters.
- Programs, studies, and assays now support multiple notebook folders (just like storage folders).

### Fixed
- Fixed a bug that caused incorrect values to show in the Storage Folders settings in the Admin Dashboard. (#157)
- Fixed a bug that was causing errors when loading the study form page.
- Fixed study list not filtering by keywords when using the search bar (#129).

## [0.9.5] - 2023-12-14

### Added
- Added search bar to admin dashboard assay type settings table (#134).
- Added search bar to program list page to allow for easier filtering of large lists (#128).
- The MS Azure AD integration now supports the SharePoint `Sites.Selected` permission, which grants the app access to only sites that the user specifies. SharePoint sites and their drives can now be added by requesting them by directly by ID. This is required when using `Sites.Selected`, since this permission set will deny access to the endpoint that lists all available Sharepoint sites. (#151) 

### Changed
- Public and private study collection are now differentiated in the 'Add Study to Collection' dialog (#127).
- GitLab initializer will not run if integration is already registered.
- Replaced cards with a table in the Program Details page Studies tab.

### Fixed
- Fixed download button in the File Manager (#121).
- Fixed logout link (#131).
- Fixed ordering of study collections in the 'Add Study to Collection' dialog (#125).
- Fixed refresh of study collection table after adding a study to a collection (#126).
- Fixed obstructed CRO dropdown in the study form (#123).
- User table correctly refreshes on status change (#140).
- Fixed a bug preventing private collections from being edited (#124 & #139).
- Fixed bug preventing assay tasks from being assigned to users after the assay had been created (#130).
- Fixed navigation buttons for file and notebook widgets (#136).
- Fixed a bug that could cause two file-select dialogs to appear when attempting to upload files in the File Manager (#132).

## [0.9.4] - 2023-10-20

### Changed
- GitLab repositories now get more specific paths created for them, including both the project codes and names. GitLab repositories also now get a better truncated description based on the study/assay description.

### Fixed
- Fixed a bug with repairing/creating notebook folders for programs/studies/assays without them.
- Removed some unnecessary logging.
- Fixed an issue with S3 path resolution that was throwing errors on file upload.
- Fixed a bug that could prevent the creation of new folders in S3 drives (#118).

## [0.9.3] - 2023-07-04

### Added
- You can now create S3 folders for assays in the same way you can for studies.

### Fixed
- Fixed a bug that was preventing copying correct S3 file & folder paths in the File Manager.
- Fixed bug preventing adding folders to assays. 
- Fixed file manager traversal via controls & breadcrumbs.

### Changed
- Updated and simplified the data model for storage drives and folders to make them easier to work with in the long-term.

## [0.9.2] - 2023-06-22

### Fixed
- Fixed a bug causing multiple requests to be sent to GitLab servers when creating a new study.
- Fixed a circular bean dependencies in back-end.
- Disabled reactive ElasticSearch autoconfiguration, which could cause issue on application startup.
- Removed new-folder and file-upload controls from File Manager for read-only folders.

## [0.9.1] - 2023-06-20

### Added
- You can now add folders you are browsing in the File Manager to studies or assays. This allows you to associate existing storage locations and data with your studies and assays.

### Fixed
- Fixed an issue that would cause git repository creation to fail if study or assays descriptions were too long.
- Fixed a bug that would prevent file manager page from loading correctly when changing to a new root folder.
- The option to add S3 buckets and git repositories to studies should now show up correctly on the study form page.
- Fixed an issue where failure to publish events to EventBridge would cause the triggering request to fail.

## [0.9.0] - 2023-06-06

### Added
- Added support for Microsoft 365 cloud services: SharePoint and OneDrive. OneDrive drives can be used for file storage, and SharePoint sites can be used for registering drives.
- Added details page widgets for displaying and repairing storage folders and ELN notebooks.
- Added the ability to associate programs with Git project groups and add git repositories to studies after they have been initially created.
- Added the ability to restore studies and assays that have been removed. Removed studies & assays can be viewed on the respective list pages with a new filter and then  restored with a new quick-action on their details page. 

### Changed
- Begun overhaul of how Study Tracker manages external service integrations. This will allow for easier configuration of integrations, which can now be managed from the front-end of the application, without the need to restart the app.
- Also overhauled how storage folder records are managed on the back-end, allowing for more flexible folder management across multiple file storage services.
- Consolidated assay and study controls into a single dropdown in the Actions widget.
- Study and assay names can now be changed by editing them. This name change will *not* cascade to storage or notebook folders created with the original names.
- Updated the styling of the program list and details pages.

### Fixed
- Fixed several issues with File Manager breadcrumbs and controls, which prevented them from acting as expected.
- Fixed an issue where large ELN folder requests could timeout or crash study & assay creation requests.
- Fixed a bug where removed assays would still appear in the study details page.
- Fixed the ordering of assay custom input fields.

## [0.8.2] - 2023-03-01

### Fixed
- Studies can no longer be added to collections they already belong to.
- Fixed a bug with study collection activity not showing correctly.

### Changed
- You can now more easily edit collection studies from the collection details page. Study collections can now also be deleted from the collection details page.

## [0.8.1] - 2023-02-10

### Fixed

- Fixed an issue where some study details inputs would not disable on submission, allowing duplicate records to be created if the user clicked the submit button multiple times.
- Fixed 'My Active Studies' link on the main page.
- Fixed an issue where null values in optional inputs of the user form would throw validation errors.

## [0.8.0] - 2023-01-26

### Added

- Added additional functionality to Assay Tasks. You can now assign tasks to users, set due dates, and require additional information to be filled out in custom forms when tasks are completed.
- Added bean property bindings to configuration properties for easier reference and validation. Any misconfigured properties will throw a more accessible error message at startup.
- Added dropdown-select and file-upload inputs to the Assay Type custom field definitions. You can now also set default values for Assay Type custom fields.
- Added host information to event payload. This will allow consumers to know which host and what Study Tracker version the event originated from. 

### Changed

- Updated the Study and Assay details pages with a new design. This includes an updated summary tab, the replacement of the old Files tab with a new File Manager tab, and more.
- Updated configuration properties for email integration. The old properties were spring-specific, but will still work.

### Fixed

- Fixed a bug with study and assay list display, due to missing fields.
- Fixed a bug where failed study notebook loading would crash the web app.
- Fixed an issue with missing database records for study and assay folders.
- Fixed a bug preventing assays to be updated, due to failed end date validation.
- A number of small UI bugs.

## [0.7.1] - 2022-11-15

### Added
- Added File Manager tool to the Study Tracker UI. This tool allows the browsing of storage locations registered within Study Tracker. Users can navigate & create folders, and upload & download files.
- Added support for Amazon Web Services S3 storage locations. This allows users to store files in S3 buckets.
- Added support for creating API users, which can be used exclusively for API access. This allows for easier management of integrations and removes the need to use a user's credentials for API access.
- Admin users can now select existing Benchling projects to map new programs to.
- Added generic Git service integration with GitLab implementation. Users can now opt to create Git repositories for new studies and assays.

### Changed
- Changed the signature of the JWT created and returned to authenticated users to include creation and expiration times.
- Updated program, study, and assay forms to make some features optional. Users can choose whether they want notebook and git resources created for them, rather than assuming they are required.
- Updated the database schema to capture integration configuration parameters for external services that Study Tracker connects with. This will allow for admin users to connect services from within the application, without the need for back-end modifications and redeployment.

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