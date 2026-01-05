<script setup lang="ts">
import { ref } from 'vue'
import { useMainStore } from '@/store'

const store = useMainStore()
const count = ref(0)

const increment = () => {
  count.value++
  store.increment()
}
</script>

<template>
  <div class="home">
    <h1>高职人工智能学院实训耗材管理系统</h1>
    <p>欢迎使用耗材管理系统</p>

    <div class="counter">
      <button @click="increment">count is {{ count }}</button>
      <p>Store count: {{ store.counter }}</p>
      <p>Double count: {{ store.doubleCount }}</p>
    </div>
  </div>
</template>

<style scoped>
.home {
  text-align: center;
  padding: 2rem;
}

.counter {
  margin-top: 2rem;
}

button {
  padding: 0.5rem 1rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
}

button:hover {
  background: #f0f0f0;
}
</style>