import { defineStore } from 'pinia';
import api from '../services/api';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isManager: (state) => state.user?.role === 'MANAGER',
    canManageCategories: (state) => state.user?.role === 'ADMIN' || state.user?.role === 'MANAGER',
  },
  actions: {
    async login(username, password) {
      try {
        // Generamos el token en base64 para Basic Auth
        const token = btoa(`${username}:${password}`);

        // Verificamos credenciales llamando al endpoint /me para obtener el perfil completo
        const response = await api.get('/me', {
          headers: {
            Authorization: `Basic ${token}`,
          },
        });

        // Si hay éxito, guardamos el token y los datos completos del usuario (incluyendo el role)
        this.token = token;
        this.user = response.data;
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(this.user));
        return true;
      } catch (error) {
        console.error('Error en el login', error);
        throw error;
      }
    },
    async register(userData) {
      try {
        this.logout(); // Limpiamos cualquier sesión previa
        const response = await api.post('/auth/register', userData);
        return response.data;
      } catch (error) {
        console.error('Error en el registro', error);
        throw error;
      }
    },

    async updateProfile(userData) {
      try {
        const response = await api.get('/profile'); // Asegurarnos de tener datos frescos
        const responseUpdate = await api.put('/profile', userData);
        this.user = responseUpdate.data;
        localStorage.setItem('user', JSON.stringify(this.user));
        return responseUpdate.data;
      } catch (error) {
        console.error('Error al actualizar el perfil', error);
        throw error;
      }
    },
    logout() {
      // Borramos los datos de sesión del estado y del almacenamiento local
      this.user = null;
      this.token = null;
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    },
  },
});
