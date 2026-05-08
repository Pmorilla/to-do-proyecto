<script setup>
import { useAuthStore } from '../../stores/auth';
import { useRouter } from 'vue-router';
import { ref, onMounted } from 'vue';

const authStore = useAuthStore();
const router = useRouter();

// Gestión de tema sincronizada
const currentTheme = ref(localStorage.getItem('theme') || 'dark');

const setTheme = (theme) => {
  currentTheme.value = theme;
  document.documentElement.setAttribute('data-theme', theme);
  localStorage.setItem('theme', theme);
  closeDropdown();
};

const logout = () => {
  authStore.logout();
  router.push({ name: 'login' });
};

const closeDropdown = () => {
  document.activeElement.blur();
};

onMounted(() => {
  document.documentElement.setAttribute('data-theme', currentTheme.value);
});
</script>

<template>
  <div class="navbar bg-base-100 shadow-lg mb-8 rounded-box relative z-[100]">
    <!-- Inicio de Navbar: Marca y Menú Móvil -->
    <div class="navbar-start">
      <!-- Desplegable Móvil -->
      <div class="dropdown sm:hidden">
        <label tabindex="0" class="btn btn-ghost btn-circle text-primary">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </label>
        <ul tabindex="0" class="menu menu-md dropdown-content mt-3 z-[101] p-3 shadow-2xl bg-base-100 rounded-box w-64 border border-white/5">
          <li v-if="authStore.isAuthenticated">
            <router-link :to="{ name: 'dashboard' }" @click="closeDropdown" class="text-lg font-bold py-3">Dashboard</router-link>
          </li>
          <li v-if="authStore.canManageCategories">
            <router-link :to="{ name: 'categories' }" @click="closeDropdown" class="text-lg font-bold py-3">Categorías</router-link>
          </li>
          <li v-if="authStore.isAdmin">
            <router-link :to="{ name: 'users' }" @click="closeDropdown" class="text-lg font-bold py-3 text-accent">Usuarios</router-link>
          </li>
          
          <div class="divider my-2"></div>
          <li class="menu-title text-sm font-black uppercase tracking-widest opacity-50 px-4 py-2">Tema</li>
          <li class="flex flex-col gap-1">
            <input type="radio" name="theme-selection" 
              :checked="currentTheme === 'dark'"
              @change="setTheme('dark')"
              class="btn btn-md btn-block btn-ghost justify-start text-base" 
              aria-label="Oscuro" value="dark" />
            <input type="radio" name="theme-selection" 
              :checked="currentTheme === 'dracula'"
              @change="setTheme('dracula')"
              class="btn btn-md btn-block btn-ghost justify-start text-base" 
              aria-label="Drácula" value="dracula" />
            <input type="radio" name="theme-selection" 
              :checked="currentTheme === 'light'"
              @change="setTheme('light')"
              class="btn btn-md btn-block btn-ghost justify-start text-base" 
              aria-label="Claro" value="light" />
          </li>
        </ul>
      </div>
      <router-link to="/" class="btn btn-ghost normal-case text-xl text-primary font-bold">ToDo App</router-link>
    </div>

    <!-- Centro de Navbar: Enlaces de Escritorio -->
    <div class="navbar-center hidden sm:flex">
      <ul class="menu menu-horizontal px-1 gap-1">
        <li v-if="authStore.isAuthenticated">
          <router-link :to="{ name: 'dashboard' }" class="btn btn-ghost btn-sm normal-case">Dashboard</router-link>
        </li>
        <li v-if="authStore.canManageCategories">
          <router-link :to="{ name: 'categories' }" class="btn btn-ghost btn-sm normal-case">Categorías</router-link>
        </li>
        <li v-if="authStore.isAdmin">
          <router-link :to="{ name: 'users' }" class="btn btn-ghost btn-sm normal-case text-accent font-semibold">Usuarios</router-link>
        </li>
      </ul>
    </div>

    <!-- Fin de Navbar: Tema y Perfil -->
    <div class="navbar-end gap-2">
      <!-- Desplegable de Tema para Escritorio -->
      <div class="dropdown dropdown-end hidden sm:block">
        <div tabindex="0" role="button" class="btn btn-ghost btn-sm">Tema</div>
        <ul tabindex="0" class="dropdown-content z-[50] menu p-2 shadow bg-base-100 rounded-box w-52">
          <li>
            <input type="radio" name="theme-selection" 
              :checked="currentTheme === 'dark'"
              @change="setTheme('dark')"
              class="btn btn-sm btn-block btn-ghost justify-start" 
              aria-label="Oscuro" value="dark" />
          </li>
          <li>
            <input type="radio" name="theme-selection" 
              :checked="currentTheme === 'dracula'"
              @change="setTheme('dracula')"
              class="btn btn-sm btn-block btn-ghost justify-start" 
              aria-label="Drácula" value="dracula" />
          </li>
          <li>
            <input type="radio" name="theme-selection" 
              :checked="currentTheme === 'light'"
              @change="setTheme('light')"
              class="btn btn-sm btn-block btn-ghost justify-start" 
              aria-label="Claro" value="light" />
          </li>
        </ul>
      </div>

      <!-- Desplegable de Perfil -->
      <div v-if="authStore.isAuthenticated" class="dropdown dropdown-end">
        <label tabindex="0" class="avatar placeholder cursor-pointer hover:opacity-80 transition-opacity">
          <div class="bg-primary text-primary-content rounded-full w-10 h-10">
            <span class="text-xl font-bold uppercase">{{ authStore.user?.username?.charAt(0) || 'U' }}</span>
          </div>
        </label>
        <ul tabindex="0" class="mt-3 z-[1] p-2 shadow menu menu-sm dropdown-content bg-base-100 rounded-box w-52">
          <li><router-link :to="{ name: 'profile' }" @click="closeDropdown">Mi Perfil</router-link></li>
          <li><a @click="logout" class="text-error">Logout</a></li>
        </ul>
      </div>
    </div>
  </div>
</template>
