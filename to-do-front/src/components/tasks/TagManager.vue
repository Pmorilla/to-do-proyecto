<script setup>
import { ref } from 'vue';
import { useTaskStore } from '../../stores/tasks';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close']);
const taskStore = useTaskStore();
const newTagName = ref('');
const loading = ref(false);

const handleAddTag = async () => {
  if (!newTagName.value.trim()) return;
  
  loading.ref = true;
  try {
    await taskStore.addTag(newTagName.value.trim());
    newTagName.value = '';
  } catch (error) {
    alert('Error al crear la etiqueta');
  } finally {
    loading.ref = false;
  }
};

const handleDeleteTag = async (id) => {
  if (confirm('¿Estás seguro de que quieres eliminar esta etiqueta? Las tareas que la usen dejarán de tenerla.')) {
    try {
      await taskStore.deleteTag(id);
    } catch (error) {
      alert('Error al eliminar la etiqueta');
    }
  }
};
</script>

<template>
  <dialog :class="['modal', { 'modal-open': show }]">
    <div class="modal-box bg-base-100/90 backdrop-blur-xl border border-white/10 shadow-2xl">
      <div class="flex justify-between items-center mb-6">
        <h3 class="font-bold text-2xl bg-gradient-to-r from-accent to-primary bg-clip-text text-transparent">
          Gestionar Etiquetas
        </h3>
        <button class="btn btn-sm btn-circle btn-ghost" @click="emit('close')">✕</button>
      </div>

      <!-- Formulario para Añadir Etiqueta -->
      <div class="flex gap-2 mb-8">
        <input 
          v-model="newTagName" 
          type="text" 
          placeholder="Nueva etiqueta..." 
          class="input input-bordered flex-1 focus:input-accent transition-all duration-300"
          @keyup.enter="handleAddTag"
        />
        <button 
          @click="handleAddTag" 
          class="btn btn-accent shadow-lg shadow-accent/20"
          :disabled="!newTagName.trim() || loading"
        >
          Añadir
        </button>
      </div>

      <!-- Lista de Etiquetas -->
      <div class="space-y-3 max-h-64 overflow-y-auto pr-2 custom-scrollbar">
        <div v-if="taskStore.tags.length === 0" class="text-center py-8 opacity-40 italic">
          No hay etiquetas creadas
        </div>
        
        <div 
          v-for="tag in taskStore.tags" 
          :key="tag.id" 
          class="flex justify-between items-center p-3 rounded-xl bg-base-200/50 border border-white/5 group hover:bg-base-200 transition-colors"
        >
          <span class="font-semibold text-base-content/80">#{{ tag.name }}</span>
          <button 
            @click="handleDeleteTag(tag.id)" 
            class="btn btn-circle btn-xs btn-ghost text-error opacity-0 group-hover:opacity-100 transition-opacity"
            title="Eliminar etiqueta"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <div class="modal-action">
        <button class="btn btn-ghost" @click="emit('close')">Cerrar</button>
      </div>
    </div>
    <form method="dialog" class="modal-backdrop" @submit.prevent="emit('close')">
      <button>close</button>
    </form>
  </dialog>
</template>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}
</style>
