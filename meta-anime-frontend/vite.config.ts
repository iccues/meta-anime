import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig(({ mode }) => ({
  plugins: [vue()],
  server: {
    proxy: mode === 'development' ? {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    } : undefined
  }
}))
