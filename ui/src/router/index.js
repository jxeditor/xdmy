import {createRouter, createWebHashHistory} from 'vue-router'
import LoginView from '../views/LoginView.vue'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    component: LoginView
  },
  {
    path: '/shipment',
    name: 'shipment',
    component: () => import('../views/ShipmentView.vue'),
    meta: {
      requireAuth: true,
      role: "ADMIN"
    }
  },
  {
    path: '/incoming',
    name: 'incoming',
    component: () => import('../views/IncomingView.vue'),
    meta: {
      requireAuth: true,
      role: "ADMIN"
    }
  },
  {
    path: '/stock',
    name: 'stock',
    component: () => import('../views/StockView.vue'),
    meta: {
      requireAuth: true,
      role: "ADMIN"
    }
  },
  {
    path: '/product',
    name: 'product',
    component: () => import('../views/ProductView.vue'),
    meta: {
      requireAuth: true,
      role: "ADMIN"
    }
  },
  {
    path: '/materialStock',
    name: 'materialStock',
    component: () => import('../views/MaterialStockView.vue'),
    meta: {
      requireAuth: true,
      role: "ADMIN"
    }
  },
  {
    path: '/productMaterialRelation',
    name: 'productMaterialRelation',
    component: () => import('../views/ProductMaterialRelationView.vue'),
    meta: {
      requireAuth: true,
      role: "ADMIN"
    }
  },
  {
    path: '/turnover',
    name: 'turnover',
    component: () => import('../views/TurnoverView.vue'),
    meta: {
      requireAuth: true,
      role: "ADMIN"
    }
  },
  {
    path: '/screen',
    name: 'screen',
    component: () => import('../views/ScreenView.vue'),
    meta: {
      requireAuth: true,
      role: "ADMIN"
    }
  },
  {
    path: '/download',
    name: 'download',
    component: () => import('../views/DownloadView.vue'),
    meta: {
      requireAuth: true,
      role: "ADMIN"
    }
  },
  {
    path: '/materialOperation',
    name: 'materialOperation',
    component: () => import('../views/MaterialOperationView.vue'),
    meta: {
      requireAuth: true,
      role: "ADMIN"
    }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.afterEach((to, from) => {
  document.title = '雄达木业'
})

router.beforeEach((to, from, next) => {
  const role = localStorage.getItem('role');	//获取当前用户权限
  const loginTime = localStorage.getItem('loginTime');	//获取登录时间
  
  // 检查会话是否过期（2小时 = 7200000毫秒）
  if (loginTime) {
    const currentTime = new Date().getTime();
    if (currentTime - parseInt(loginTime) > 7200000) {
      // 会话过期，清除登录信息
      localStorage.removeItem('role');
      localStorage.removeItem('loginTime');
      next({path: '/login'});
      return;
    }
  }
  
  if (to.meta.role === 'ADMIN') {		//判断即将跳转到的页面要检查的权限是否为admin
    if (role === 'ADMIN') {
      next();		//若当前用户权限为admin则放行
    } else {
      next({path: '/login'})
    }
  } else {
    next();
  }
})

export default router
