import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'

const app = createApp(App)
app.config.globalProperties.$axios = axios
app.use(store).use(router).use(ElementPlus).mount('#app')

// 用户无操作检测，2小时后自动退出登录
let inactivityTimer;
const INACTIVITY_TIMEOUT = 7200000; // 2小时，单位毫秒

// 重置无操作定时器
function resetInactivityTimer() {
  clearTimeout(inactivityTimer);
  inactivityTimer = setTimeout(logoutDueToInactivity, INACTIVITY_TIMEOUT);
  // 更新登录时间，确保会话不过期
  localStorage.setItem('loginTime', new Date().getTime());
}

// 由于无操作而退出登录
function logoutDueToInactivity() {
  localStorage.removeItem('role');
  localStorage.removeItem('loginTime');
  router.push('/login');
}

// 监听用户操作事件
const userEvents = ['click', 'mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'touchmove'];
userEvents.forEach(event => {
  window.addEventListener(event, resetInactivityTimer, true);
});

// 初始化定时器
resetInactivityTimer();


