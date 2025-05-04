# Migration from Webpack to Vite

This document outlines the changes made to migrate the client module from Webpack (via Create React App) to Vite.

## Changes Made

1. **Updated package.json**:
   - Replaced webpack-related dependencies (react-scripts, react-app-rewired) with Vite
   - Added ESLint plugins for React
   - Updated build scripts to use Vite
   - Updated ESLint configuration to be compatible with Vite

2. **Created Vite Configuration**:
   - Added vite.config.js with React plugin
   - Configured build output to match the expected structure
   - Set up path aliases and other Vite-specific settings

3. **Updated HTML Entry Point**:
   - Moved index.html from public/ to the root directory
   - Added script tag to load the JavaScript entry point

4. **Environment Variables**:
   - Created .env file for Vite environment variables
   - Added .gitignore to exclude sensitive files

5. **Removed Webpack Configuration**:
   - Removed config-overrides.js as it's no longer needed

## Testing the Migration

To test the migration, follow these steps:

1. Install dependencies:
   ```
   npm install
   ```

2. Start the development server:
   ```
   npm run dev
   ```

3. Build the project:
   ```
   npm run build
   ```

4. Preview the production build:
   ```
   npm run preview
   ```

5. Run tests:
   ```
   npm test
   ```

## Maven Integration

The Maven build process should work as before. The frontend-maven-plugin will:
1. Install Node.js and npm
2. Run npm install
3. Run npm build

The build output will be in the `build` directory, which is compatible with the existing Maven configuration.

## Troubleshooting

If you encounter any issues:

1. Check the Vite documentation: https://vitejs.dev/guide/
2. Ensure all dependencies are installed correctly
3. Check for any environment-specific configurations that might need adjustment
4. Verify that the build output structure matches what the application expects