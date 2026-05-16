import { defineStore } from 'pinia';
import api from '../services/api';

export const useTaskStore = defineStore('tasks', {
  state: () => ({
    tasks: [],
    categories: [],
    tags: [],
    loading: false,
    error: null,
  }),
  actions: {
    async fetchTasks(filters = {}) {
      this.loading = true;
      this.error = null;
      try {
        // Construimos los parámetros de búsqueda (filtros)
        const params = new URLSearchParams();
        let hasFilters = false;

        if (filters.title) {
          params.append('title', filters.title);
          hasFilters = true;
        }
        if (filters.description) {
          params.append('description', filters.description);
          hasFilters = true;
        }
        if (filters.categoryId) {
          params.append('category', filters.categoryId);
          hasFilters = true;
        }
        if (filters.completed !== undefined && filters.completed !== null && filters.completed !== '') {
           params.append('completed', filters.completed);
           hasFilters = true;
        }
        if (filters.priority) {
          params.append('priority', filters.priority);
          hasFilters = true;
        }
        if (filters.tag) {
          params.append('tag', filters.tag);
          hasFilters = true;
        }

        // Si tiene filtros, usamos /task/search. Si no, usamos el listado simple /task
        const url = hasFilters ? `/task/search?${params.toString()}` : '/task';

        const response = await api.get(url);
        this.tasks = response.data;
      } catch (error) {
        if (error.response && error.response.status === 404) {
          this.tasks = []; // Si no hay tareas, vaciamos la lista
        } else {
          this.error = 'Error al cargar las tareas';
          console.error(error);
        }
      } finally {
        this.loading = false;
      }
    },

    async fetchCategories() {
      try {
        const response = await api.get('/categories');
        this.categories = response.data;
      } catch (error) {
        console.error('Error al cargar categorías', error);
      }
    },

    async addCategory(categoryData) {
      try {
        const response = await api.post('/category', categoryData);
        this.categories.push(response.data);
        return response.data;
      } catch (error) {
        console.error('Error al añadir categoría', error);
        throw error;
      }
    },

    async updateCategory(id, categoryData) {
      try {
        const response = await api.put(`/category/${id}`, categoryData);
        const index = this.categories.findIndex(c => c.id === id);
        if (index !== -1) {
          this.categories[index] = response.data;
        }
        return response.data;
      } catch (error) {
        console.error('Error al actualizar categoría', error);
        throw error;
      }
    },

    async deleteCategory(id) {
      try {
        await api.delete(`/category/${id}`);
        this.categories = this.categories.filter(c => c.id !== id);
      } catch (error) {
        console.error('Error al borrar categoría', error);
        throw error;
      }
    },

    async fetchTags() {
      try {
        const response = await api.get('/tag');
        this.tags = response.data;
      } catch (error) {
        console.error('Error al cargar etiquetas', error);
      }
    },

    async addTag(tagName) {
      try {
        const response = await api.post('/tag', { name: tagName });
        this.tags.push(response.data);
        return response.data;
      } catch (error) {
        console.error('Error al añadir etiqueta', error);
        throw error;
      }
    },

    async updateTag(id, tagName) {
      try {
        const response = await api.put(`/tag/${id}`, { name: tagName });
        const index = this.tags.findIndex(t => t.id === id);
        if (index !== -1) {
          this.tags[index] = response.data;
        }
        return response.data;
      } catch (error) {
        console.error('Error al actualizar etiqueta', error);
        throw error;
      }
    },

    async deleteTag(tagId) {
      try {
        await api.delete(`/tag/${tagId}`);
        this.tags = this.tags.filter(t => t.id !== tagId);
      } catch (error) {
        console.error('Error al borrar etiqueta', error);
        throw error;
      }
    },

    async addTask(taskData) {
      try {
        const response = await api.post('/task', taskData);
        this.tasks.push(response.data);
      } catch (error) {
        console.error('Error al añadir tarea', error);
        throw error;
      }
    },

    async updateTask(id, taskData) {
      try {
        const response = await api.put(`/task/${id}`, taskData);
        const index = this.tasks.findIndex(t => t.id === id);
        if (index !== -1) {
          this.tasks[index] = response.data;
        }
      } catch (error) {
        console.error('Error al actualizar tarea', error);
        throw error;
      }
    },

    async toggleTaskCompletion(task) {
      try {
        // Cambiamos el estado de completado de la tarea
        const updatedTask = { ...task, completed: !task.completed };
        
        // El backend espera un formato específico para las fechas
        const payload = {
          title: updatedTask.title,
          description: updatedTask.description,
          deadline: updatedTask.deadline ? updatedTask.deadline.replace('Z', '').replace('T', ' ').substring(0, 19) : null,
          completed: updatedTask.completed,
          priority: updatedTask.priority,
          categoryId: task.categoryId, 
        };
        
        // Nota: El componente llamará a updateTask con el payload correcto
      } catch (error) {
        console.error('Error al cambiar estado de la tarea', error);
      }
    },

    async deleteTask(id) {
      try {
        await api.delete(`/task/${id}`);
        this.tasks = this.tasks.filter(t => t.id !== id);
      } catch (error) {
        console.error('Error al borrar tarea', error);
        throw error;
      }
    }
  }
});
