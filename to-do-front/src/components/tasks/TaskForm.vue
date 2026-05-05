<script setup>
import { ref, watch, onMounted } from 'vue';
import { useTaskStore } from '../../stores/tasks';

const props = defineProps({
  taskToEdit: {
    type: Object,
    default: null
  },
  show: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['save', 'close']);
const taskStore = useTaskStore();

const title = ref('');
const description = ref('');
const deadline = ref('');
const completed = ref(false);
const priority = ref('LOW');
const categoryId = ref('');
const selectedTags = ref([]);

onMounted(() => {
  taskStore.fetchCategories();
  taskStore.fetchTags();
});

// Para convertir fechas al formato esperado en el input datetime-local o desde el servidor
const formatDatetimeLocal = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const pad = (n) => String(n).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
};

// Actualizamos el formulario interno cada vez que se muestre el modal (propiedad show) o la tarea a editar cambie
watch(() => props.show, (newVal) => {
  if (newVal) {
    if (props.taskToEdit) {
      title.value = props.taskToEdit.title;
      description.value = props.taskToEdit.description;
      deadline.value = formatDatetimeLocal(props.taskToEdit.deadline);
      completed.value = props.taskToEdit.completed;
      priority.value = props.taskToEdit.priority || 'LOW';
      categoryId.value = props.taskToEdit.categoryId || '';
      selectedTags.value = props.taskToEdit.tags ? [...props.taskToEdit.tags] : [];
    } else {
      title.value = '';
      description.value = '';
      deadline.value = '';
      completed.value = false;
      priority.value = 'LOW';
      categoryId.value = '';
      selectedTags.value = [];
    }
  }
});

const toggleTag = (tagName) => {
  const index = selectedTags.value.indexOf(tagName);
  if (index === -1) {
    selectedTags.value.push(tagName);
  } else {
    selectedTags.value.splice(index, 1);
  }
};

const handleSubmit = () => {
  let formattedDeadline = null;
  if (deadline.value) {
    formattedDeadline = deadline.value.replace('T', ' ') + ':00';
  }

  const payload = {
    title: title.value,
    description: description.value,
    deadline: formattedDeadline,
    completed: completed.value,
    priority: priority.value,
    categoryId: categoryId.value || null,
    tags: selectedTags.value
  };
  emit('save', payload);
};
</script>

<template>
  <dialog :class="['modal', { 'modal-open': show }]">
    <div class="modal-box bg-base-100/80 backdrop-blur-md border border-white/10 shadow-2xl max-w-2xl">
      <div class="flex justify-between items-center mb-6">
        <h3 class="font-bold text-2xl bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
          {{ taskToEdit ? 'Editar Tarea' : 'Nueva Tarea' }}
        </h3>
        <button class="btn btn-sm btn-circle btn-ghost" @click="emit('close')">✕</button>
      </div>
      
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="form-control md:col-span-2">
            <label class="label"><span class="label-text font-semibold">Título</span></label>
            <input v-model="title" type="text" class="input input-bordered focus:input-primary transition-all duration-300" placeholder="¿Qué hay que hacer?" required />
          </div>
          
          <div class="form-control md:col-span-2">
            <label class="label"><span class="label-text font-semibold">Descripción</span></label>
            <textarea v-model="description" class="textarea textarea-bordered h-24 focus:textarea-primary transition-all duration-300" placeholder="Añade algunos detalles..." required></textarea>
          </div>
          
          <div class="form-control">
            <label class="label"><span class="label-text font-semibold">Fecha límite</span></label>
            <input v-model="deadline" type="datetime-local" class="input input-bordered focus:input-primary transition-all duration-300" />
          </div>

          <div class="form-control">
            <label class="label"><span class="label-text font-semibold">Prioridad</span></label>
            <select v-model="priority" class="select select-bordered focus:select-primary transition-all duration-300">
              <option value="LOW">Baja</option>
              <option value="MEDIUM">Media</option>
              <option value="HIGH">Alta</option>
            </select>
          </div>

          <div class="form-control">
            <label class="label"><span class="label-text font-semibold">Categoría</span></label>
            <select v-model="categoryId" class="select select-bordered focus:select-primary transition-all duration-300">
              <option value="">Sin categoría</option>
              <option v-for="cat in taskStore.categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>

          <div class="form-control md:col-span-2">
            <label class="label"><span class="label-text font-semibold">Etiquetas</span></label>
            <div class="flex flex-wrap gap-2 p-3 bg-base-200/50 rounded-xl min-h-[50px]">
              <div v-if="taskStore.tags.length === 0" class="text-xs opacity-50 italic">
                No hay etiquetas creadas. Puedes gestionarlas desde la lista principal.
              </div>
              <button 
                v-for="tag in taskStore.tags" 
                :key="tag.id"
                type="button"
                @click="toggleTag(tag.name)"
                :class="['badge badge-md cursor-pointer transition-all duration-300 py-3 px-4 font-semibold', 
                         selectedTags.includes(tag.name) ? 'badge-primary shadow-md shadow-primary/20' : 'badge-outline opacity-60 hover:opacity-100']"
              >
                #{{ tag.name }}
              </button>
            </div>
          </div>

          <div v-if="taskToEdit" class="form-control flex-row items-center gap-4 mt-2">
            <label class="label cursor-pointer gap-2">
              <input v-model="completed" type="checkbox" class="checkbox checkbox-primary" />
              <span class="label-text font-semibold">Completada</span>
            </label>
          </div>
        </div>
        
        <div class="modal-action mt-8">
          <button type="button" class="btn btn-ghost hover:bg-base-300" @click="emit('close')">Cancelar</button>
          <button type="submit" class="btn btn-primary px-8 shadow-lg shadow-primary/20">
            {{ taskToEdit ? 'Actualizar' : 'Crear Tarea' }}
          </button>
        </div>
      </form>
    </div>
    <form method="dialog" class="modal-backdrop" @submit.prevent="emit('close')">
      <button>close</button>
    </form>
  </dialog>
</template>
