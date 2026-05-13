<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();

const email = ref('');
const fullname = ref('');
const loading = ref(false);
const successMessage = ref('');
const errorMessage = ref('');

onMounted(() => {
  if (authStore.user) {
    email.value = authStore.user.email || '';
    fullname.value = authStore.user.fullname || '';
  }
});

const handleUpdate = async () => {
  loading.value = true;
  successMessage.value = '';
  errorMessage.value = '';

  if (!fullname.value.trim()) {
    errorMessage.value = 'El nombre completo es obligatorio.';
    loading.value = false;
    return;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email.value)) {
    errorMessage.value = 'Por favor, introduce un correo electrónico válido.';
    loading.value = false;
    return;
  }
  
  try {
    await authStore.updateProfile({
      email: email.value,
      fullname: fullname.value
    });
    successMessage.value = 'Perfil actualizado correctamente';
  } catch (error) {
    errorMessage.value = 'Error al actualizar el perfil';
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="mt-8 space-y-8 max-w-2xl mx-auto">
    <!-- Encabezado -->
    <div class="bg-base-200/50 backdrop-blur-sm p-6 rounded-3xl border border-white/5 shadow-xl">
      <div class="flex flex-col justify-between items-start gap-2">
        <h1 class="text-4xl font-black bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent">
          Mi Perfil
        </h1>
        <p class="text-base-content/50 font-medium">Actualiza tu información personal</p>
      </div>
    </div>

    <!-- Formulario de edición -->
    <div class="bg-base-200/30 p-5 rounded-3xl border border-white/5 shadow-xl">
      <div v-if="successMessage" class="alert alert-success shadow-lg mb-6 rounded-2xl">
        <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
        <span>{{ successMessage }}</span>
      </div>

      <div v-if="errorMessage" class="alert alert-error shadow-lg mb-6 rounded-2xl">
        <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
        <span>{{ errorMessage }}</span>
      </div>

      <form @submit.prevent="handleUpdate" class="space-y-5" novalidate>
        <div class="form-control w-full">
          <label class="label">
            <span class="label-text font-bold opacity-70">Nombre de Usuario</span>
          </label>
          <input 
            type="text" 
            :value="authStore.user?.username" 
            disabled 
            class="input input-bordered w-full rounded-2xl bg-base-300 opacity-70 cursor-not-allowed font-medium"
            title="El nombre de usuario no se puede cambiar"
          />
        </div>

        <div class="form-control w-full">
          <label class="label">
            <span class="label-text font-bold opacity-70">Nombre Completo</span>
          </label>
          <input 
            v-model="fullname" 
            type="text" 
            placeholder="Introduce tu nombre completo" 
            class="input input-bordered w-full rounded-2xl bg-base-100 focus:input-primary transition-all font-medium"
            required
          />
        </div>

        <div class="form-control w-full">
          <label class="label">
            <span class="label-text font-bold opacity-70">Correo Electrónico</span>
          </label>
          <input 
            v-model="email" 
            type="email" 
            placeholder="ejemplo@correo.com" 
            class="input input-bordered w-full rounded-2xl bg-base-100 focus:input-primary transition-all font-medium"
            required
          />
        </div>

        <div class="form-control w-full">
          <label class="label">
            <span class="label-text font-bold opacity-70">Rol</span>
          </label>
          <div class="badge badge-primary badge-lg py-3 px-3 font-bold shadow-md shadow-primary/20">
            {{ authStore.user?.role }}
          </div>
        </div>

        <div class="divider opacity-10"></div>

        <div class="flex justify-end gap-2 mt-6">
          <button 
            type="submit" 
            class="btn btn-primary rounded-2xl px-8 shadow-lg shadow-primary/30" 
            :disabled="loading"
          >
            <span v-if="loading" class="loading loading-spinner"></span>
            {{ loading ? 'Guardando...' : 'Guardar Cambios' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
