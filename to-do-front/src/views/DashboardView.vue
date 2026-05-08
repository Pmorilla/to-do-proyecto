<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const dashboardData = ref(null);
const loading = ref(false);
const error = ref('');

onMounted(async () => {
  loading.value = true;
  try {
    const response = await api.get('/task/dashboard');
    dashboardData.value = response.data;
  } catch (err) {
    error.value = 'Error al cargar el panel de control';
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="mt-8 space-y-8 max-w-6xl mx-auto">
    <!-- Encabezado del Dashboard -->
    <div class="bg-base-200/50 backdrop-blur-sm p-6 rounded-3xl border border-white/5 shadow-xl">
      <div class="flex flex-col justify-between items-start gap-2">
        <h1 class="text-4xl font-black bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent">
          Dashboard
        </h1>
        <p class="text-base-content/50 font-medium">Estadísticas de tus tareas</p>
      </div>
    </div>

    <!-- Estado de Carga -->
    <div v-if="loading" class="flex justify-center my-24">
      <span class="loading loading-infinity loading-lg text-primary"></span>
    </div>

    <div v-else-if="error" class="alert alert-error">
      {{ error }}
    </div>

    <!-- Cuadrícula de Estadísticas -->
    <div v-else-if="dashboardData" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="stat bg-base-200/30 rounded-3xl border border-white/5 shadow-xl">
        <div class="stat-figure text-primary">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="inline-block w-8 h-8 stroke-current"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
        </div>
        <div class="stat-title">Total Tareas</div>
        <div class="stat-value text-primary">{{ dashboardData.totalTasks }}</div>
      </div>
      
      <div class="stat bg-base-200/30 rounded-3xl border border-white/5 shadow-xl">
        <div class="stat-figure text-success">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="inline-block w-8 h-8 stroke-current"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
        </div>
        <div class="stat-title">Completadas</div>
        <div class="stat-value text-success">{{ dashboardData.completedTasks }}</div>
      </div>
      
      <div class="stat bg-base-200/30 rounded-3xl border border-white/5 shadow-xl">
        <div class="stat-figure text-warning">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="inline-block w-8 h-8 stroke-current"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
        </div>
        <div class="stat-title">Pendientes</div>
        <div class="stat-value text-warning">{{ dashboardData.pendingTasks }}</div>
      </div>
      
      <div class="stat bg-base-200/30 rounded-3xl border border-white/5 shadow-xl">
        <div class="stat-figure text-error">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="inline-block w-8 h-8 stroke-current"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>
        </div>
        <div class="stat-title">Vencidas</div>
        <div class="stat-value text-error">{{ dashboardData.overdueTasks }}</div>
      </div>

      <div class="md:col-span-2 bg-base-200/30 p-6 rounded-3xl border border-white/5 shadow-xl">
        <h3 class="font-bold text-xl mb-4 text-accent">Por Categoría</h3>
        <div v-if="Object.keys(dashboardData.tasksByCategory).length === 0" class="opacity-50">No hay datos</div>
        <ul v-else class="space-y-2">
          <li v-for="(count, category) in dashboardData.tasksByCategory" :key="category" class="flex justify-between items-center p-3 bg-base-100 rounded-xl">
            <span class="font-semibold">{{ category }}</span>
            <span class="badge badge-accent">{{ count }}</span>
          </li>
        </ul>
      </div>

      <div class="md:col-span-2 bg-base-200/30 p-6 rounded-3xl border border-white/5 shadow-xl">
        <h3 class="font-bold text-xl mb-4 text-secondary">Por Etiqueta</h3>
        <div v-if="Object.keys(dashboardData.tasksByTag).length === 0" class="opacity-50">No hay datos</div>
        <ul v-else class="space-y-2 flex flex-wrap gap-2">
          <li v-for="(count, tag) in dashboardData.tasksByTag" :key="tag" class="flex items-center gap-2 p-2 bg-base-100 rounded-xl">
            <span class="font-semibold text-sm">#{{ tag }}</span>
            <span class="badge badge-sm badge-secondary">{{ count }}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>
