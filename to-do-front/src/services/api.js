import axios from 'axios';
import { useAuthStore } from '../stores/auth';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

api.interceptors.request.use((config) => {
  const authStore = useAuthStore();
  
  // Do not add Authorization header if we are registering or if it's already set
  const isPublicEndpoint = config.url.includes('/auth/register');
  
  if (!isPublicEndpoint && authStore.isAuthenticated && authStore.token && !config.headers.Authorization) {
    config.headers.Authorization = `Basic ${authStore.token}`;
  }
  
  console.log(`Sending request to ${config.url}`, config.headers);
  return config;
});

export default api;
