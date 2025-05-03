# React 18 Migration

This document provides instructions for completing the migration to React 18 in the Study Tracker client module.

## Changes Made

1. Updated React dependencies to version 18 in package.json
2. Created new versions of the following files with .jsx extensions:
   - client/src/common/DataTable.jsx
   - client/src/pages/adminDashboard/apiUserSettings/ApiUserTable.jsx
   - client/src/pages/adminDashboard/assayTypes/AssayTypeTable.jsx

3. Updated the column definitions in these files to use Tanstack Table format instead of BootstrapTable format
4. Created scripts to help with the migration:
   - client/rename_js_to_jsx.sh - Renames all React component files from .js to .jsx
   - client/update_imports.sh - Updates import statements to reference .jsx extensions

5. Created a migration guide: client/bootstrap_to_datatable_template.md

## Steps to Complete the Migration

1. Run the script to rename all React component files from .js to .jsx:
   ```bash
   cd client
   chmod +x rename_js_to_jsx.sh
   ./rename_js_to_jsx.sh
   ```

2. Update the remaining files that use BootstrapTable components to use DataTable instead. The following files need to be updated:
   - client/src/common/ProgramListTable.js
   - client/src/common/fileManager/FileManagerTable.js
   - client/src/pages/adminDashboard/program/ProgramsTable.js
   - client/src/pages/adminDashboard/userSettings/UserSettingsTable.js
   - client/src/pages/assayList/AssayList.js
   - client/src/pages/programDetails/ProgramStudiesTab.js
   - client/src/pages/studyCollectionList/CollectionList.js
   - client/src/pages/studyCollectionsDetails/StudyCollectionDetails.js
   - client/src/pages/studyList/StudyList.js
   - client/src/pages/userList/UserList.js

   Follow the instructions in client/bootstrap_to_datatable_template.md to update these files.

3. Run the script to update import statements in all files:
   ```bash
   cd client
   chmod +x update_imports.sh
   ./update_imports.sh
   ```

4. Build the client module to verify the changes:
   ```bash
   cd client
   npm run build
   ```

5. Test the application to ensure that all tables are working correctly.

## Notes

- The DataTable component uses Tanstack Table (react-table v8) instead of react-bootstrap-table
- The column definitions have been updated to use the Tanstack Table format
- All React component files have been renamed from .js to .jsx
- Import statements have been updated to reference .jsx extensions