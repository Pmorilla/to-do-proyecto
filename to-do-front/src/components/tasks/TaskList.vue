<script setup>
import { onMounted, ref, watch } from 'vue';
import { useTaskStore } from '../../stores/tasks';
import TaskItem from './TaskItem.vue';
import TaskForm from './TaskForm.vue';
import TagManager from './TagManager.vue';

const taskStore = useTaskStore();

const showModal = ref(false);
const showTagManager = ref(false);
const selectedTask = ref(null);

// Filtros para las tareas
const filters = ref({
  categoryId: '',
  completed: '',
  priority: '',
  tag: ''
});

onMounted(async () => {
  await taskStore.fetchTasks();
  await taskStore.fetchCategories();
  await taskStore.fetchTags();
});

// Recargar las tareas desde la API automáticamente si cambia algún filtro
watch(filters, () => {
  taskStore.fetchTasks(filters.value);
}, { deep: true });

const openCreateModal = () => {
  selectedTask.value = null;
  showModal.value = true;
};

const openEditModal = (task) => {
  selectedTask.value = task;
  showModal.value = true;
};

const handleDelete = async (id) => {
  if (confirm('¿Estás seguro de que quieres eliminar esta tarea?')) {
    await taskStore.deleteTask(id);
  }
};

const handleSave = async (taskData) => {
  if (selectedTask.value) {
    await taskStore.updateTask(selectedTask.value.id, taskData);
  } else {
    await taskStore.addTask(taskData);
  }
  showModal.value = false;
};

const clearFilters = () => {
  filters.value = {
    categoryId: '',
    completed: '',
    priority: '',
    tag: ''
  };
};
</script>

<template>
  <div class="mt-8 space-y-8">
    <!-- Encabezado y Filtros -->
    <div class="bg-base-200/50 backdrop-blur-sm p-6 rounded-3xl border border-white/5 shadow-xl relative z-30">
      <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6">
        <div>
          <h1 class="text-4xl font-black bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent">
            Mis Tareas
          </h1>
          <p class="text-base-content/50 mt-1 font-medium">Gestiona tus objetivos con precisión</p>
        </div>
        <div class="flex flex-wrap gap-2 w-full lg:w-auto">
          <button class="btn btn-outline btn-accent btn-sm sm:btn-md shadow-lg shadow-accent/10 px-4 sm:px-6 rounded-2xl group flex-1 sm:flex-none" @click="showTagManager = true">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5 mr-1 sm:mr-2 group-hover:scale-110 transition-transform">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9.568 3H5.25A2.25 2.25 0 003 5.25v4.318c0 .597.237 1.17.659 1.591l9.581 9.581a2.25 2.25 0 003.182 0l4.318-4.318a2.25 2.25 0 000-3.182L11.159 3.659A2.25 2.25 0 009.568 3z" />
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 6h.008v.008H6V6z" />
            </svg>
            <span class="text-xs sm:text-sm">Etiquetas</span>
          </button>
          <button class="btn btn-primary btn-sm sm:btn-md shadow-lg shadow-primary/30 px-4 sm:px-6 rounded-2xl group flex-1 sm:flex-none" @click="openCreateModal">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-5 h-5 mr-1 sm:mr-2 group-hover:rotate-90 transition-transform duration-300">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>
            <span class="text-xs sm:text-sm">Nueva Tarea</span>
          </button>
        </div>
      </div>

      <div class="divider opacity-10"></div>

      <!-- Barra de Filtros -->
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4 items-end">
        <!-- Desplegable de Categoría -->
        <div class="dropdown">
          <label class="label py-1"><span class="label-text text-xs font-bold uppercase opacity-50">Categoría</span></label>
          <div tabindex="0" role="button" class="btn btn-sm btn-outline rounded-xl w-full justify-between bg-base-100 border-base-content/20">
            {{ taskStore.categories.find(c => c.id === filters.categoryId)?.name || 'Todas' }}
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4 opacity-50">
              <path stroke-linecap="round" stroke-linejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5" />
            </svg>
          </div>
          <ul tabindex="0" class="dropdown-content z-[999] menu p-2 shadow-2xl bg-base-200 rounded-box w-52 border border-white/5 mt-1">
            <li><a @click="filters.categoryId = ''" :class="{ 'active': filters.categoryId === '' }">Todas</a></li>
            <li v-for="cat in taskStore.categories" :key="cat.id">
              <a @click="filters.categoryId = cat.id" :class="{ 'active': filters.categoryId === cat.id }">{{ cat.name }}</a>
            </li>
          </ul>
        </div>

        <!-- Desplegable de Estado -->
        <div class="dropdown">
          <label class="label py-1"><span class="label-text text-xs font-bold uppercase opacity-50">Estado</span></label>
          <div tabindex="0" role="button" class="btn btn-sm btn-outline rounded-xl w-full justify-between bg-base-100 border-base-content/20">
            {{ filters.completed === true ? 'Completadas' : (filters.completed === false ? 'Pendientes' : 'Todos') }}
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4 opacity-50">
              <path stroke-linecap="round" stroke-linejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5" />
            </svg>
          </div>
          <ul tabindex="0" class="dropdown-content z-[9999] menu p-2 shadow-2xl bg-base-200 rounded-box w-52 border border-white/5 mt-1">
            <li><a @click="filters.completed = ''" :class="{ 'active': filters.completed === '' }">Todos</a></li>
            <li><a @click="filters.completed = true" :class="{ 'active': filters.completed === true }">Completadas</a></li>
            <li><a @click="filters.completed = false" :class="{ 'active': filters.completed === false }">Pendientes</a></li>
          </ul>
        </div>

        <!-- Desplegable de Prioridad -->
        <div class="dropdown">
          <label class="label py-1"><span class="label-text text-xs font-bold uppercase opacity-50">Prioridad</span></label>
          <div tabindex="0" role="button" class="btn btn-sm btn-outline rounded-xl w-full justify-between bg-base-100 border-base-content/20">
            {{ filters.priority === 'LOW' ? 'Baja' : (filters.priority === 'MEDIUM' ? 'Media' : (filters.priority === 'HIGH' ? 'Alta' : 'Todas')) }}
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4 opacity-50">
              <path stroke-linecap="round" stroke-linejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5" />
            </svg>
          </div>
          <ul tabindex="0" class="dropdown-content z-[9999] menu p-2 shadow-2xl bg-base-200 rounded-box w-52 border border-white/5 mt-1">
            <li><a @click="filters.priority = ''" :class="{ 'active': filters.priority === '' }">Todas</a></li>
            <li><a @click="filters.priority = 'LOW'" :class="{ 'active': filters.priority === 'LOW' }">Baja</a></li>
            <li><a @click="filters.priority = 'MEDIUM'" :class="{ 'active': filters.priority === 'MEDIUM' }">Media</a></li>
            <li><a @click="filters.priority = 'HIGH'" :class="{ 'active': filters.priority === 'HIGH' }">Alta</a></li>
          </ul>
        </div>

        <div class="form-control">
          <label class="label py-1"><span class="label-text text-xs font-bold uppercase opacity-50">Etiqueta</span></label>
          <input v-model="filters.tag" type="text" placeholder="Buscar etiqueta..." class="input input-bordered input-sm rounded-xl bg-base-100" />
        </div>

        <button @click="clearFilters" class="btn btn-ghost btn-sm text-xs font-bold uppercase opacity-50 hover:opacity-100 mb-0.5">
          Limpiar filtros
        </button>
      </div>
    </div>

    <!-- Lista de Tareas -->
    <div v-if="taskStore.loading" class="flex flex-col items-center justify-center my-24 gap-4">
      <span class="loading loading-infinity loading-lg text-primary"></span>
      <p class="text-sm font-bold uppercase tracking-widest opacity-30">Sincronizando tareas...</p>
    </div>

    <div v-else-if="taskStore.error" class="alert alert-error shadow-lg rounded-2xl">
      <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
      <span class="font-bold">{{ taskStore.error }}</span>
    </div>

    <div v-else-if="taskStore.tasks.length === 0" class="text-center py-24 bg-base-200/30 rounded-3xl border border-dashed border-base-content/10">
      <div class="text-6xl mb-4 opacity-20">🎯</div>
      <h3 class="text-2xl font-bold text-base-content/40">No se encontraron tareas</h3>
      <p class="mt-2 text-base-content/30 max-w-xs mx-auto">Ajusta tus filtros o crea una nueva tarea para empezar a ser productivo.</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6 pb-12">
      <TaskItem 
        v-for="task in taskStore.tasks" 
        :key="task.id" 
        :task="task" 
        @edit="openEditModal" 
        @delete="handleDelete" 
      />
    </div>

    <TaskForm 
      :show="showModal" 
      :taskToEdit="selectedTask" 
      @save="handleSave" 
      @close="showModal = false" 
    />

    <TagManager 
      :show="showTagManager" 
      @close="showTagManager = false" 
    />
  </div>
</template>
