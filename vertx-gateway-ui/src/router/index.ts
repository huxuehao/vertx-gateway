import { createRouter, createWebHashHistory } from "vue-router";
import Layout from "@/layout/Index.vue"
import setting from '@/config/setting'

const router = createRouter({
  history: createWebHashHistory('/'+setting.baseUrl+"/"),
  routes: [
    {
      path: "/login",
      name: "登录",
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
