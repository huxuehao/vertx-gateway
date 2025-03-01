<template>
    <div class="card" @mouseenter="hover = true" @mouseleave="hover = false" :style="cardStyle">
      <h3>{{ title }}</h3>
      <p>{{ description }}</p>
    </div>
  </template>
  
  <script>
  export default {
    name: 'FeatureCard',
    props: {
      title: String,
      description: String,
    },
    data() {
      return {
        hover: false,
      };
    },
    computed: {
      cardStyle() {
        return {
          transform: this.hover ? 'translateY(-10px)' : 'translateY(0)',
          boxShadow: this.hover
            ? '0 8px 16px rgba(0, 0, 0, 0.2)'
            : '0 4px 8px rgba(0, 0, 0, 0.1)',
        };
      },
    },
  };
  </script>
  
  <style scoped>
  .card {
    background-color: #fff;
    border-radius: 10px;
    padding: 20px;
    width: 300px;
    text-align: left;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }
  
  .card h3 {
    font-size: 1.5rem;
    color: #2c3e50;
    margin-bottom: 15px;
  }
  
  .card p {
    font-size: 1rem;
    color: #666;
    line-height: 1.6;
  }
  </style>