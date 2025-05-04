import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],

  // Set the base path for the application
  base: '/',

  // Configure the build output
  build: {
    outDir: 'build',
    assetsDir: 'static',
    emptyOutDir: true,
    sourcemap: true,
  },

  // Configure the development server
  server: {
    port: 3000,
    open: true,
  },

  // Configure static asset handling
  publicDir: 'public',

  // Configure path aliases if needed
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },

  // Configure test with vitest
  test: {
    globals: true,
    environment: 'jsdom',
  },
});
