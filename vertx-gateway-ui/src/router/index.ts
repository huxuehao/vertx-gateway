import { createRouter, createWebHistory } from "vue-router";
import Layout from "@/layout/Index.vue"

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name: "login",
      component: () => import("@/views/login.vue"),
    },
    {
      path: "/404",
      name: "404",
      component: () => import("@/views/404.vue"),
    },
    {
      path: "/",
      redirect: "/home"
    },
    {
      path: "/home",
      component: Layout,
      children: [
        {
          path: "",
          name: "首页",
          component: () => import("@/views/home.vue"),
        }
      ]
    },
    {
      path: '/:catchAll(.*)',
      redirect: '/404'
    }
  ],
});

export default router;
