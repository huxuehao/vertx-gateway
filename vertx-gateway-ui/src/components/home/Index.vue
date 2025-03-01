<template>
    <div class="container">
      <!-- 标题 -->
      <h1>欢迎使用 Vertx-Gateway</h1>
      <h2>高效、可靠、易用的API网关平台</h2>
  
      <!-- 欢迎语句 -->
      <!-- <div class="welcome-text">
        <p>在当今数字化时代，构建高效、安全且易于管理的 API 架构至关重要。[Vertx-Gateway] API 网关平台应运而生，致力于为您提供一站式的 API 管理解决方案。。</p>
        <p>立即开始，体验高效的任务管理！</p>
      </div> -->
  
      <!-- 功能卡片 -->
      <div class="card-container">
        <FeatureCard
          v-for="(feature, index) in features"
          :key="index"
          :title="feature.title"
          :description="feature.description"
        />
      </div>
    </div>
  </template>
  
  <script>
  import FeatureCard from './FeatureCard.vue';
  
  export default {
    components: {
      FeatureCard,
    },
    data() {
      return {
        features: [
          {
            title: '分布式部署能力',
            description: '轻松应对大规模业务场景，支持分布式部署，确保系统的高可用性和可扩展性。无论您的业务规模如何增长，我们的平台都能稳定运行，为您的 API 服务保驾护航。',
          },
          {
            title: '全异步非阻塞架构',
            description: '平台核心功能基于 vertx 框架构建，采用全异步非阻塞模式。这意味着在处理大量并发请求时，系统无需为每个请求分配额外线程，大大减少了线程上下文切换开销，显著提升了处理效率。在高并发场景下，能够快速响应 API 请求，确保您的服务始终保持高效运行，为用户提供流畅的使用体验。',
          },
          {
            title: '接口级权限配置',
            description: '针对每个 API 接口，您可以进行细致的权限设置，精确控制不同客户端对接口的访问权限，确保数据的安全性和保密性。',
          },
        ],
      };
    },
    methods: {
      startUsing() {
        alert('开始使用系统！');
      },
    },
  };
  </script>
  
  <style scoped>
  /* 全局样式 */
  body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    margin: 0;
    padding: 0;
    background-color: #f4f4f9;
    color: #333;
  }
  
  /* 容器样式 */
  .container {
    max-width: 100%;
    margin: 0 auto;
    padding: 20px;
    text-align: center;
  }
  
  /* 标题样式 */
  h1 {
    font-size: 2.5rem;
    color: #2c3e50;
    margin-bottom: 20px;
  }
  
  /* 副标题样式 */
  h2 {
    font-size: 1.8rem;
    color: #34495e;
    margin-bottom: 30px;
  }
  
  /* 欢迎语句样式 */
  .welcome-text {
    font-size: 1.2rem;
    line-height: 1.8;
    color: #555;
    margin-bottom: 40px;
  }
  
  /* 卡片容器样式 */
  .card-container {
    display: flex;
    justify-content: space-around;
    flex-wrap: wrap;
    gap: 20px;
  }
  
  /* 按钮样式 */
  .btn {
    display: inline-block;
    padding: 10px 20px;
    font-size: 1rem;
    color: #fff;
    background-color: #3498db;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .btn:hover {
    background-color: #2980b9;
  }
  
  /* 页脚样式 */
  footer {
    margin-top: 50px;
    padding: 20px;
    background-color: #2c3e50;
    color: #fff;
    text-align: center;
  }
  
  footer a {
    color: #3498db;
    text-decoration: none;
  }
  
  footer a:hover {
    text-decoration: underline;
  }
  </style>