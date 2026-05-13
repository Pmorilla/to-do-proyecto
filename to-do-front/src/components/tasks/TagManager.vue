<script setup>
import { ref } from 'vue';
import { useTaskStore } from '../../stores/tasks';
import ConfirmationModal from '../common/ConfirmationModal.vue';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close']);
const taskStore = useTaskStore();
const newTagName = ref('');
const editingTagId = ref(null);
const editTagName = ref('');
const loading = ref(false);
const errorMsg = ref('');

const confirmModalOpen = ref(false);
const tagToDelete = ref(null);

const handleAddTag = async () => {
  if (!newTagName.value.trim()) return;
  
  errorMsg.value = '';
  loading.ref = true;
  try {
    await taskStore.addTag(newTagName.value.trim());
    newTagName.value = '';
  } catch (error) {
    errorMsg.value = 'Error al crear la etiqueta';
  } finally {
    loading.ref = false;
  }
};

const handleDeleteTag = async (id) => {
  tagToDelete.value = id;
  confirmModalOpen.value = true;
};

const confirmDeleteTag = async () => {
  if (tagToDelete.value) {
    try {
      await taskStore.deleteTag(tagToDelete.value);
    } catch (error) {
      errorMsg.value = 'Error al eliminar la etiqueta';
    }
    tagToDelete.value = null;
  }
  confirmModalOpen.value = false;
};

const startEdit = (tag) => {
  editingTagId.value = tag.id;
  editTagName.value = tag.name;
};

const cancelEdit = () => {
  editingTagId.value = null;
  editTagName.value = '';
};

const handleUpdateTag = async (id) => {
  if (!editTagName.value.trim()) return;
  
  errorMsg.value = '';
  loading.value = true;
  try {
    await taskStore.updateTag(id, editTagName.value.trim());
    cancelEdit();
  } catch (error) {
    errorMsg.value = 'Error al actualizar la etiqueta';
  } finally {
    loading.value = false;
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

      <div v-if="errorMsg" class="alert alert-error mb-6 text-sm py-2">
        <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-5 w-5" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
        <span>{{ errorMsg }}</span>
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
          <div v-if="editingTagId === tag.id" class="flex-1 flex gap-2">
            <input 
              v-model="editTagName" 
              type="text" 
              class="input input-bordered input-sm flex-1 focus:input-accent"
              @keyup.enter="handleUpdateTag(tag.id)"
              @keyup.esc="cancelEdit"
            />
            <button @click="handleUpdateTag(tag.id)" class="btn btn-sm btn-accent" :disabled="!editTagName.trim() || loading">Guardar</button>
            <button @click="cancelEdit" class="btn btn-sm btn-ghost">Cancelar</button>
          </div>
          <template v-else>
            <span class="font-semibold text-base-content/80">#{{ tag.name }}</span>
            <div class="opacity-0 group-hover:opacity-100 transition-opacity flex gap-1">
              <button 
                @click="startEdit(tag)" 
                class="btn btn-circle btn-xs btn-ghost text-info"
                title="Editar etiqueta"
              >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10" />
                </svg>
              </button>
              <button 
                @click="handleDeleteTag(tag.id)" 
                class="btn btn-circle btn-xs btn-ghost text-error"
                title="Eliminar etiqueta"
              >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </template>
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
  <ConfirmationModal
    :isOpen="confirmModalOpen"
    title="Eliminar etiqueta"
    message="¿Estás seguro de que quieres eliminar esta etiqueta? Las tareas que la usen dejarán de tenerla."
    @confirm="confirmDeleteTag"
    @cancel="confirmModalOpen = false"
  />
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
