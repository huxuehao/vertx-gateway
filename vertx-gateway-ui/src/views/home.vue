<script lang="ts" setup>
import { useUserStore } from "@/stores/user";
import { computed } from "vue";

const userStore = useUserStore();
let userInfo = computed(() => {
  return userStore.userInfo
})

const getTimePeriod = () => {
  const now = new Date();
  const hour = now.getHours();

  if (hour >= 5 && hour < 8) {
      return '早上';
  } else if (hour >= 8 && hour < 12) {
      return '上午';
  } else if (hour >= 12 && hour < 18) {
      return '下午';
  } else {
      return '晚上';
  }
}

</script>

<template>
  <div class="container">
    <div class="left-section">
      <h1>{{ getTimePeriod() }}好, {{ userInfo.nickname || userInfo.name || userInfo.account }}</h1>
      <h2>欢迎使用API网关框架-Vertx Gateway</h2>
      <div class="data-container">
        <div class="data-item">
            <span class="amount">1,200,000</span> 
            <span class="arrow">↑</span>
            <p class="label">访问总量</p>
        </div>
        <div class="data-item">
            <span class="percentage">23%</span>
            <span class="arrow">↑</span>
            <p class="label">较上周增长</p>
        </div>
      </div>
    </div>
    <div class="right-section">
      <img src="/src/assets/lf_icon2.png" alt="插图" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
body {
  font-family: Arial, sans-serif;
  background-color: #f0f4f8;
  color: #333;
}

.container {
  position: relative;
  display: flex;
  padding: 20px;
}

.left-section {
  flex: 0 0 30%;
  padding-left: 15%;
  padding-top: 10%;
}

.right-section {
  flex: 0 0 45%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.right-section img {
  max-width: 100%;
  height: auto;
}

h1, h2 {
  margin-bottom: 20px;
  color: #2c3e50;
}

.data-container {
  display: flex;
  gap: 40px;
}

.data-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.amount {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
}

.percentage {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
}

.arrow {
  font-size: 12px;
  color: #27ae60;
}

.label {
  font-size: 14px;
  color: #7f8c8d;
}
</style>
