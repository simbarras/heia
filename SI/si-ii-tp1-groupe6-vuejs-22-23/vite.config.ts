import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import { SPRING_API_URL, DOTNET_API_URL } from "./constant";

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    proxy: {
      "/api/spring": {
        target: `${SPRING_API_URL}`,
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api\/spring/, ""),
      },
      "/api/dotnet": {
        target: `${DOTNET_API_URL}`,
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api\/dotnet/, ""),
      },
    },
  },
  plugins: [vue()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
});
