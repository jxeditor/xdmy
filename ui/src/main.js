import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import axios from 'axios'

console.log('Environment Variables:', process.env);
console.log('API Base URL from main.js:', process.env.VUE_APP_API_BASE_URL);

// 添加Axios请求拦截器，在所有请求中自动添加公司名称参数和Token
axios.interceptors.request.use(config => {
  // 从localStorage中获取公司名称
  const companyName = localStorage.getItem('companyName') || '';
  // 从localStorage中获取Token
  const token = localStorage.getItem('token') || '';
  
  try {
    // 在请求头中添加公司名称（使用encodeURIComponent编码，确保只包含ISO-8859-1兼容的字符）
    config.headers['X-Company-Name'] = encodeURIComponent(companyName);
    
    // 在请求头中添加Token
    if (token) {
      config.headers['X-Token'] = token;
    }
    
    // 如果是POST请求且有请求体，也在请求体中添加公司名称
    if (config.method === 'post' && config.data) {
      // 如果是FormData格式
      if (config.data instanceof FormData) {
        config.data.append('companyName', companyName);
      } 
      // 如果是JSON格式
      else if (typeof config.data === 'object') {
        config.data.companyName = companyName;
      }
    }
  } catch (error) {
    console.error('Error setting request headers:', error);
  }
  
  return config;
}, error => {
  return Promise.reject(error);
});

const app = createApp(App)
app.config.globalProperties.$axios = axios
app.use(store).use(router).use(ElementPlus, { locale: zhCn }).mount('#app')

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
  localStorage.removeItem('token');
  router.push('/login');
}

// 监听用户操作事件
const userEvents = ['click', 'mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'touchmove'];
userEvents.forEach(event => {
  window.addEventListener(event, resetInactivityTimer, true);
});

// 初始化定时器
resetInactivityTimer();


