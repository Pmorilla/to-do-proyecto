<script setup>
defineProps({
  isOpen: Boolean,
  title: String,
  message: String,
  confirmText: {
    type: String,
    default: 'Eliminar'
  },
  cancelText: {
    type: String,
    default: 'Cancelar'
  }
});

defineEmits(['confirm', 'cancel']);
</script>

<template>
  <div class="modal" :class="{ 'modal-open': isOpen }">
    <div class="modal-box border border-white/10 bg-base-100/90 backdrop-blur-md">
      <h3 class="font-bold text-lg text-error flex items-center gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
        {{ title }}
      </h3>
      <p class="py-4">{{ message }}</p>
      <div class="modal-action">
        <button class="btn btn-ghost" @click="$emit('cancel')">{{ cancelText }}</button>
        <button class="btn btn-error text-white" @click="$emit('confirm')">{{ confirmText }}</button>
      </div>
    </div>
    <div class="modal-backdrop bg-base-300/40 backdrop-blur-sm" @click="$emit('cancel')">
      <button class="cursor-default">close</button>
    </div>
  </div>
</template>
