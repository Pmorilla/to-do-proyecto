<script setup>
import { ref, onMounted } from 'vue';
import { useTaskStore } from '../stores/tasks';

const taskStore = useTaskStore();
const showModal = ref(false);
const editingCategory = ref(null);
const categoryName = ref('');

onMounted(async () => {
  await taskStore.fetchCategories();
});

const openCreateModal = () => {
  editingCategory.value = null;
  categoryName.value = '';
  showModal.value = true;
};

const openEditModal = (category) => {
  editingCategory.value = category;
  categoryName.value = category.name;
  showModal.value = true;
};

const handleSave = async () => {
  if (!categoryName.value.trim()) return;

  try {
    if (editingCategory.value) {
      await taskStore.updateCategory(editingCategory.value.id, { name: categoryName.value });
    } else {
      await taskStore.addCategory({ name: categoryName.value });
    }
    showModal.value = false;
  } catch (error) {
    alert('Error al guardar la categoría');
  }
};

const handleDelete = async (id) => {
  if (confirm('¿Estás seguro de que quieres eliminar esta categoría?')) {
    try {
      await taskStore.deleteCategory(id);
    } catch (error) {
      alert('Error al eliminar la categoría. Asegúrate de que no tenga tareas asociadas.');
    }
  }
};
</script>

<template>
  <div class="mt-8 space-y-8 max-w-4xl mx-auto">
    <!-- Header -->
    <div class="bg-base-200/50 backdrop-blur-sm p-6 rounded-3xl border border-white/5 shadow-xl">
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-6">
        <div>
          <h1 class="text-4xl font-black bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent">
            Gestión de Categorías
          </h1>
          <p class="text-base-content/50 mt-1 font-medium">Administra las categorías del sistema</p>
        </div>
        <button class="btn btn-primary shadow-lg shadow-primary/30 px-6 rounded-2xl group" @click="openCreateModal">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-5 h-5 mr-2 group-hover:rotate-90 transition-transform duration-300">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
          </svg>
          Nueva Categoría
        </button>
      </div>
    </div>

    <!-- Categories Table/List -->
    <div class="bg-base-200/30 rounded-3xl border border-white/5 shadow-xl overflow-hidden">
      <div class="overflow-x-auto">
        <table class="table w-full">
          <thead>
            <tr class="bg-base-300/50">
              <th class="text-xs font-bold uppercase opacity-50 px-6 py-4">ID</th>
              <th class="text-xs font-bold uppercase opacity-50 px-6 py-4">Nombre</th>
              <th class="text-xs font-bold uppercase opacity-50 px-6 py-4 text-right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cat in taskStore.categories" :key="cat.id" class="hover:bg-base-200/50 transition-colors border-b border-base-content/5">
              <td class="px-6 py-4 font-mono text-xs opacity-50">{{ cat.id }}</td>
              <td class="px-6 py-4">
                <span class="font-bold text-lg">{{ cat.name }}</span>
              </td>
              <td class="px-6 py-4 text-right space-x-2">
                <button @click="openEditModal(cat)" class="btn btn-ghost btn-sm btn-circle text-info hover:bg-info/10">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
                    <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10" />
                  </svg>
                </button>
                <button @click="handleDelete(cat.id)" class="btn btn-ghost btn-sm btn-circle text-error hover:bg-error/10">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
                    <path stroke-linecap="round" stroke-linejoin="round" d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0" />
                  </svg>
                </button>
              </td>
            </tr>
            <tr v-if="taskStore.categories.length === 0">
              <td colspan="3" class="text-center py-12 opacity-30 italic">No hay categorías registradas</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal for Create/Edit -->
    <div v-if="showModal" class="modal modal-open">
      <div class="modal-box rounded-3xl border border-white/5 bg-base-100 shadow-2xl p-8">
        <h3 class="text-2xl font-black mb-6">{{ editingCategory ? 'Editar Categoría' : 'Nueva Categoría' }}</h3>
        
        <div class="form-control w-full">
          <label class="label">
            <span class="label-text font-bold opacity-50">Nombre de la categoría</span>
          </label>
          <input 
            v-model="categoryName" 
            type="text" 
            placeholder="Ej. Trabajo, Personal..." 
            class="input input-bordered w-full rounded-2xl bg-base-200/50 focus:input-primary transition-all text-lg font-medium"
            @keyup.enter="handleSave"
            ref="nameInput"
          />
        </div>

        <div class="modal-action mt-10">
          <button class="btn btn-ghost rounded-2xl px-6" @click="showModal = false">Cancelar</button>
          <button 
            class="btn btn-primary rounded-2xl px-8 shadow-lg shadow-primary/30" 
            @click="handleSave"
            :disabled="!categoryName.trim()"
          >
            {{ editingCategory ? 'Actualizar' : 'Guardar' }}
          </button>
        </div>
      </div>
      <div class="modal-backdrop" @click="showModal = false"></div>
    </div>
  </div>
</template>
