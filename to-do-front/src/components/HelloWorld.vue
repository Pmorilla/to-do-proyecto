<script setup>
import { useCounterStore } from '../stores/counter'

const counter = useCounterStore()
</script>

<template>
  <div class="min-h-screen bg-base-200 flex items-center justify-center p-4">
    <!-- Sección de Héroe -->
    <div class="hero max-w-4xl bg-base-100 shadow-2xl rounded-3xl overflow-hidden transition-all duration-500 hover:shadow-primary/20 hover:shadow-3xl">
      <div class="hero-content flex-col lg:flex-row gap-10 p-10 lg:p-16">
        
        <!-- Decoración / Logo -->
        <div class="w-full max-w-sm rounded-2xl bg-gradient-to-br from-primary to-secondary p-1 shadow-lg transform transition-transform hover:scale-105 duration-300">
           <div class="bg-base-100/90 backdrop-blur-sm rounded-xl h-64 flex items-center justify-center">
             <div class="text-center">
                <span class="text-6xl mb-4 block">🚀</span>
                <h2 class="text-2xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-primary to-secondary">IM Todo App</h2>
             </div>
           </div>
        </div>

        <!-- Contenido Principal -->
        <div class="text-center lg:text-left">
          <div class="badge badge-primary badge-outline mb-4">Entorno Configurado</div>
          <h1 class="text-5xl font-extrabold tracking-tight mb-4 text-base-content">
            Vue + Tailwind + DaisyUI
          </h1>
          <p class="py-4 text-base-content/80 text-lg">
            ¡Tu stack tecnológico está funcionando perfectamente! Pinia, Tailwind CSS y los componentes de DaisyUI están integrados y listos para desarrollar tu aplicación.  
          </p>
          
          <!-- Sección Interactiva del Store de Pinia -->
          <div class="mt-6 flex flex-col sm:flex-row items-center gap-4 justify-center lg:justify-start">
            <div class="stats bg-base-200 shadow">
              <div class="stat px-8 py-3">
                <div class="stat-title font-medium">Contador Pinia</div>
                <div class="stat-value text-primary">{{ counter.count }}</div>
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <button 
                @click="counter.increment()" 
                class="btn btn-primary hover:scale-105 transition-transform"
              >
                Sumar
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5 ml-1"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" /></svg>
              </button>
              
              <button 
                @click="counter.reset()" 
                class="btn btn-sm btn-ghost text-error hover:bg-error/10"
              >
                Reiniciar
              </button>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
