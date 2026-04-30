<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../../stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const username = ref('');
const password = ref('');
const error = ref('');
const loading = ref(false);

const handleLogin = async () => {
  error.value = '';
  loading.value = true;
  try {
    await authStore.login(username.value, password.value);
    router.push({ name: 'home' });
  } catch (err) {
    error.value = 'Credenciales inválidas. Por favor, inténtalo de nuevo.';
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="card w-full max-w-sm shadow-2xl bg-base-100">
    <div class="card-body">
      <h2 class="card-title text-2xl font-bold mb-4 justify-center">Iniciar Sesión</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-control">
          <label class="label">
            <span class="label-text">Usuario</span>
          </label>
          <input v-model="username" type="text" placeholder="Tu usuario" class="input input-bordered" required />
        </div>
        <div class="form-control mt-4">
          <label class="label">
            <span class="label-text">Contraseña</span>
          </label>
          <input v-model="password" type="password" placeholder="Tu contraseña" class="input input-bordered" required />
        </div>
        <div v-if="error" class="alert alert-error mt-4 text-sm">
          <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
          <span>{{ error }}</span>
        </div>
        <div class="form-control mt-6">
          <button class="btn btn-primary" :disabled="loading">
            <span v-if="loading" class="loading loading-spinner"></span>
            Ingresar
          </button>
        </div>
      </form>
      <div class="text-center mt-4">
        <p class="text-sm">¿No tienes cuenta? <router-link to="/register" class="link link-primary">Regístrate</router-link></p>
      </div>
    </div>
  </div>
</template>
