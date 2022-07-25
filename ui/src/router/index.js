import { createRouter, createWebHashHistory } from 'vue-router'
import ShipmentView from '../views/ShipmentView.vue'

const routes = [
  {
    path: '/',
    name: 'shipment',
    component: ShipmentView
  },
  {
    path: '/incoming',
    name: 'incoming',
    component: () => import('../views/IncomingView.vue')
  },
  {
    path: '/stock',
    name: 'stock',
    component: () => import('../views/StockView.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
