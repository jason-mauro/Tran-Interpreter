import path from "path"
import react from "@vitejs/plugin-react"
import { defineConfig } from "vite"

export default defineConfig({
  server: {
    host: "0.0.0.0",
    port: 5173,
    cors: true,
    hmr: {
      host: "localhost",
    },
  },
   preview: {
    host: "0.0.0.0",
    port: 4173, 
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            // Split vendor modules into their own chunk
            return 'vendor';
          }
        },
      },
    },
    chunkSizeWarningLimit: 1000, // Adjust size limit (optional)
  },
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
})
