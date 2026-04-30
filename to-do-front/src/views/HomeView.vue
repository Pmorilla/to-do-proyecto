<script setup>
import TaskList from '../components/tasks/TaskList.vue';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
</script>

<template>
  <div>
    <TaskList />
  </div>
</template>
