<script setup>
import { useAuthStore } from '../../stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const logout = () => {
  authStore.logout();
  router.push({ name: 'login' });
};

const closeDropdown = () => {
  document.activeElement.blur();
};
</script>

<template>
  <div class="navbar bg-base-100 shadow-lg mb-8 rounded-box relative z-[100]">
    <div class="flex-1">
      <router-link to="/" class="btn btn-ghost normal-case text-xl text-primary font-bold">ToDo App</router-link>
    </div>
    <div class="flex-none gap-2">
      <div class="dropdown dropdown-end">
        <div tabindex="0" role="button" class="btn m-1">Theme</div>
        <ul tabindex="0" class="dropdown-content z-[50] menu p-2 shadow bg-base-100 rounded-box w-52">
          <li><input type="radio" name="theme-dropdown" @click="closeDropdown" class="theme-controller btn btn-sm btn-block btn-ghost justify-start" aria-label="Dark" value="dark" /></li>
          <li><input type="radio" name="theme-dropdown" @click="closeDropdown" class="theme-controller btn btn-sm btn-block btn-ghost justify-start" aria-label="Dracula" value="dracula" /></li>
          <li><input type="radio" name="theme-dropdown" @click="closeDropdown" class="theme-controller btn btn-sm btn-block btn-ghost justify-start" aria-label="Light" value="light" /></li>
        </ul>
      </div>

      <div v-if="authStore.isAdmin" class="flex-none">
        <router-link :to="{ name: 'categories' }" class="btn btn-ghost btn-sm normal-case">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 11h.01M7 15h.01M11 7h8M11 11h8M11 15h8" />
          </svg>
          Categorías
        </router-link>
      </div>

      <div v-if="authStore.isAuthenticated" class="dropdown dropdown-end">
        <label tabindex="0" class="btn btn-ghost btn-circle avatar">
          <div class="w-10 flex justify-center items-center rounded-full bg-primary text-primary-content">
            <span class="text-xl">{{ authStore.user?.username?.charAt(0).toUpperCase() || 'U' }}</span>
          </div>
        </label>
        <ul tabindex="0" class="mt-3 z-[1] p-2 shadow menu menu-sm dropdown-content bg-base-100 rounded-box w-52">
          <li><a @click="logout" class="text-error">Logout</a></li>
        </ul>
      </div>
    </div>
  </div>
</template>
