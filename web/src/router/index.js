import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    component: () => import( '../views/login.vue')
  },
  {
    path: '/',
    component: () => import( '../views/main.vue'),
    children: [{
      path: 'welcome',
      component: () => import( '../views/main/welcome.vue'),
    },
    {
      path: 'passenger',
      component: () => import( '../views/main/passenger.vue'),
    },
    {
      path: 'ticket',
      component: () => import( '../views/main/ticket.vue'),
    }]
  },
  {
    path: '',
    redirect: '/welcome'
  }

]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
