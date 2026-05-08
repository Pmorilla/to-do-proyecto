<script setup>
import { computed } from 'vue';
import { useTaskStore } from '../../stores/tasks';

const props = defineProps({
  task: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['edit', 'delete']);
const taskStore = useTaskStore();

const isOverdue = computed(() => {
  if (!props.task.deadline || props.task.completed) return false;
  return new Date(props.task.deadline) < new Date();
});

const formattedDate = computed(() => {
  if (!props.task.deadline) return 'Sin fecha límite';
  const date = new Date(props.task.deadline);
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
});

const priorityClass = computed(() => {
  switch (props.task.priority) {
    case 'HIGH': return 'badge-error text-white';
    case 'MEDIUM': return 'badge-warning text-warning-content';
    case 'LOW': return 'badge-info text-white';
    default: return 'badge-ghost';
  }
});

const priorityLabel = computed(() => {
  switch (props.task.priority) {
    case 'HIGH': return 'Alta';
    case 'MEDIUM': return 'Media';
    case 'LOW': return 'Baja';
    default: return 'Normal';
  }
});

const handleToggle = async () => {
  const payload = {
    title: props.task.title,
    description: props.task.description,
    deadline: props.task.deadline ? props.task.deadline.replace('Z', '').replace('T', ' ').substring(0, 19) : null,
    completed: !props.task.completed,
    priority: props.task.priority,
    categoryId: props.task.categoryId,
    tags: props.task.tags
  };
  await taskStore.updateTask(props.task.id, payload);
};
</script>

<template>
  <div :class="['card shadow-xl hover:shadow-2xl transition-all duration-500 border border-white/10 group', 
                task.completed ? 'bg-base-300/40 opacity-80' : 'bg-base-100/60 backdrop-blur-sm']">
    <div class="card-body p-6">
      <div class="flex flex-col sm:flex-row justify-between items-start gap-2">
        <div class="flex items-start gap-3  w-full sm:w-auto">
          <input 
            type="checkbox" 
            :checked="task.completed" 
            @change="handleToggle"
            class="checkbox checkbox-primary mt-1" 
          />
          <div class="min-w-0 flex-1">
            <h2 :class="['card-title text-xl font-bold transition-all duration-300 truncate sm:whitespace-normal', task.completed ? 'line-through opacity-50' : '']" :title="task.title">
              {{ task.title }}
            </h2>
            <div v-if="task.category" class="text-xs font-semibold text-primary/80 uppercase tracking-wider mt-0.5">
              {{ task.category }}
            </div>
          </div>
        </div>
        <div :class="['badge badge-sm font-bold uppercase py-3 shrink-0 self-end sm:self-start', priorityClass]">
          {{ priorityLabel }}
        </div>
      </div>

      <p :class="['text-sm mt-3 transition-all duration-300', task.completed ? 'opacity-40' : 'text-base-content/70']">
        {{ task.description }}
      </p>
      
      <div v-if="task.tags && task.tags.length > 0" class="flex flex-wrap gap-1 mt-4">
        <span v-for="tag in task.tags" :key="tag" class="badge badge-outline badge-sm text-primary/70 border-primary/30">
          #{{ tag }}
        </span>
      </div>

      <div class="divider my-4 opacity-10"></div>
      
      <div class="flex items-center justify-between">
        <div :class="['flex items-center gap-1.5 text-xs font-medium', isOverdue ? 'text-error animate-pulse' : 'text-base-content/50']">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          {{ formattedDate }}
          <span v-if="isOverdue" class="ml-1 font-bold">(Atrasada)</span>
        </div>

        <div class="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
          <button class="btn btn-circle btn-xs btn-ghost text-primary hover:bg-primary/10" @click="emit('edit', task)" title="Editar">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4">
              <path stroke-linecap="round" stroke-linejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10" />
            </svg>
          </button>
          <button class="btn btn-circle btn-xs btn-ghost text-error hover:bg-error/10" @click="emit('delete', task.id)" title="Eliminar">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4">
              <path stroke-linecap="round" stroke-linejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
