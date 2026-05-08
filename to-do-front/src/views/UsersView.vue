<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const users = ref([]);
const loading = ref(false);

const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await api.get('/users');
    users.value = response.data;
  } catch (error) {
    alert('Error al cargar usuarios');
  } finally {
    loading.value = false;
  }
};

onMounted(fetchUsers);

const promoteUser = async (id) => {
  try {
    await api.put(`/users/${id}/promote`);
    await fetchUsers();
  } catch (error) {
    alert('Error al promocionar al usuario');
  }
};

const demoteUser = async (id) => {
  try {
    await api.put(`/users/${id}/demote`);
    await fetchUsers();
  } catch (error) {
    alert('Error al degradar al usuario');
  }
};

const deleteUser = async (id) => {
  if (confirm('¿Estás seguro de eliminar este usuario?')) {
    try {
      await api.delete(`/users/${id}`);
      await fetchUsers();
    } catch (error) {
      alert('Error al eliminar usuario');
    }
  }
};
</script>

<template>
  <div class="mt-8 space-y-8 max-w-5xl mx-auto">
    <!-- Encabezado de Gestión de Usuarios -->
    <div class="bg-base-200/50 backdrop-blur-sm p-6 rounded-3xl border border-white/5 shadow-xl">
      <div class="flex flex-col justify-between items-start gap-2">
        <h1 class="text-4xl font-black bg-gradient-to-r from-accent to-primary bg-clip-text text-transparent">
          Gestión de Usuarios
        </h1>
        <p class="text-base-content/50 font-medium">Administra los roles y accesos</p>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center my-12">
      <span class="loading loading-spinner loading-lg text-accent"></span>
    </div>

    <!-- Tabla de Usuarios -->
    <div v-else class="bg-base-200/30 rounded-3xl border border-white/5 shadow-xl overflow-hidden">
      <div class="overflow-x-auto">
        <table class="table w-full">
          <thead>
            <tr class="bg-base-300/50">
              <th class="px-6 py-4">Usuario</th>
              <th class="px-6 py-4">Email</th>
              <th class="px-6 py-4 text-center">Rol</th>
              <th class="px-6 py-4 text-right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id" class="hover:bg-base-200/50 transition-colors border-b border-base-content/5">
              <td class="px-6 py-4 font-bold">{{ user.username }}</td>
              <td class="px-6 py-4 text-sm opacity-70">{{ user.email }}</td>
              <td class="px-6 py-4 text-center">
                <span :class="['badge badge-sm font-bold', 
                  user.role === 'ADMIN' ? 'badge-error' : 
                  (user.role === 'MANAGER' ? 'badge-warning' : 'badge-info')]">
                  {{ user.role }}
                </span>
              </td>
              <td class="px-6 py-4 text-right space-x-2">
                <button v-if="user.role === 'USER'" @click="promoteUser(user.id)" class="btn btn-xs btn-outline btn-warning">
                  Hacer Gestor
                </button>
                <button v-if="user.role === 'MANAGER'" @click="demoteUser(user.id)" class="btn btn-xs btn-outline btn-info">
                  Degradar a Usuario
                </button>
                <button @click="deleteUser(user.id)" class="btn btn-xs btn-error text-white shadow-sm" :disabled="user.role === 'ADMIN'">
                  Eliminar
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
