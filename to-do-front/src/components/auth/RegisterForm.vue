<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../../stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const username = ref('');
const fullname = ref('');
const password = ref('');
const email = ref('');
const error = ref('');
const loading = ref(false);

const handleRegister = async () => {
  error.value = '';
  loading.value = true;

  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d).{8,}$/;
  if (!passwordRegex.test(password.value)) {
    error.value = 'La contraseña debe tener al menos 8 caracteres, una letra y un número.';
    loading.value = false;
    return;
  }

  try {
    await authStore.register({ username: username.value, fullname: fullname.value, password: password.value, email: email.value });
    // Intentar iniciar sesión automáticamente tras el registro
    await authStore.login(username.value, password.value);
    router.push({ name: 'home' });
  } catch (err) {
    error.value = 'Fallo en el registro. Quizás el usuario ya existe.';
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="card w-full max-w-sm shadow-2xl bg-base-100">
    <div class="card-body">
      <h2 class="card-title text-2xl font-bold mb-4 justify-center">Crear Cuenta</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-control">
          <label class="label">
            <span class="label-text">Nombre Completo</span>
          </label>
          <input v-model="fullname" type="text" placeholder="Tu nombre completo" class="input input-bordered" required />
        </div>
        <div class="form-control mt-4">
          <label class="label">
            <span class="label-text">Usuario</span>
          </label>
          <input v-model="username" type="text" placeholder="Tu usuario" class="input input-bordered" required />
        </div>
        <div class="form-control mt-4">
          <label class="label">
            <span class="label-text">Email</span>
          </label>
          <input v-model="email" type="email" placeholder="Tu correo electrónico" class="input input-bordered" required />
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
            Registrarse
          </button>
        </div>
      </form>
      <div class="text-center mt-4">
        <p class="text-sm">¿Ya tienes cuenta? <router-link to="/login" class="link link-primary">Inicia Sesión</router-link></p>
      </div>
    </div>
  </div>
</template>
