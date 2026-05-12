import axios from 'axios';
import { useAuthStore } from '../stores/auth';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
});

// Intercepta las peticiones salientes para añadir el token de autenticación si el usuario ha iniciado sesión
api.interceptors.request.use((config) => {
  const authStore = useAuthStore();

  const isPublicEndpoint = config.url.includes('/auth/register');

  if (!isPublicEndpoint && authStore.isAuthenticated && authStore.token && !config.headers.Authorization) {
    config.headers.Authorization = `Basic ${authStore.token}`;
  }

  return config;
});

export default api;
