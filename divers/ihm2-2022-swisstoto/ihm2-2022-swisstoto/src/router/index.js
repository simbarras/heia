import { createRouter, createWebHistory } from 'vue-router'
import SensorsView from '../views/SensorsManagement.vue'
import ProfileView from "@/views/ProfileView.vue";
import GraphicView from "@/views/GraphicView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'sensors',
      component: SensorsView
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView
    },
    {
      path: '/graphic',
        name: 'graphic',
        component: GraphicView}]
})

export default router
