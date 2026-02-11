<template>
  <div class="app-container">
    <div class="scale-container">
      <!-- 登录页面不显示头部导航 -->
      <header v-if="$route.path !== '/login'" class="app-header">
        <div class="header-left">
          <h1 class="app-title">雄达木业</h1>
        </div>
        <nav class="main-nav">
          <router-link to="/shipment" class="nav-link">出货</router-link>
          <router-link to="/incoming" class="nav-link">入货</router-link>
          <div class="dropdown">
            <button class="dropdown-toggle">产品管理</button>
            <div class="dropdown-menu">
              <router-link to="/product" class="dropdown-item">产品</router-link>
              <router-link to="/stock" class="dropdown-item">产品库存</router-link>
              <router-link to="/materialStock" class="dropdown-item">原材料库存</router-link>
              <router-link to="/productMaterialRelation" class="dropdown-item">原材料关系</router-link>
              <router-link to="/materialOperation" class="dropdown-item">原材料操作记录</router-link>
            </div>
          </div>
          <router-link to="/turnover" class="nav-link">流水</router-link>
          <router-link to="/screen" class="nav-link">大屏</router-link>
          <router-link to="/download" class="nav-link">下载</router-link>
          <button class="logout-button" @click="handleLogout">注销</button>
        </nav>
      </header>
      <!-- 登录页面使用特殊的内容容器 -->
      <div v-if="$route.path === '/login'" class="login-content">
        <router-view/>
      </div>
      <!-- 其他页面使用普通内容容器 -->
      <main v-else class="app-content">
        <router-view/>
      </main>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AppView',
  mounted() {
    // 初始化缩放
    this.updateScale();
    // 监听窗口大小变化
    window.addEventListener('resize', this.updateScale);
  },
  beforeUnmount() {
    // 移除事件监听
    window.removeEventListener('resize', this.updateScale);
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('role');
      localStorage.removeItem('loginTime');
      this.$router.push("/login");
    },
    updateScale() {
      const scaleContainer = document.querySelector('.scale-container');
      if (!scaleContainer) return;
      
      // 设计稿宽度（根据实际情况调整）
      const designWidth = 1440;
      // 设计稿高度（根据实际情况调整）
      const designHeight = 1000; // 包含头部高度
      
      // 获取窗口尺寸
      const windowWidth = window.innerWidth;
      const windowHeight = window.innerHeight;
      
      // 计算缩放比例
      const scaleX = windowWidth / designWidth;
      const scaleY = windowHeight / designHeight;
      const scale = Math.min(scaleX, scaleY);
      
      // 应用缩放
      scaleContainer.style.transform = `scale(${scale})`;
      scaleContainer.style.transformOrigin = 'top left';
      
      // 调整容器大小以适应缩放
      scaleContainer.style.width = `${designWidth}px`;
      scaleContainer.style.height = `${designHeight}px`;
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 40px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
}

.app-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-link {
  color: white;
  text-decoration: none;
  padding: 10px 16px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.nav-link.router-link-exact-active {
  background: rgba(255, 255, 255, 0.2);
  font-weight: bold;
}

.nav-link.router-link-exact-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: white;
  border-radius: 3px;
}

.logout-button {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-left: 20px;
}

.logout-button:hover {
  background: rgba(255, 0, 0, 0.3);
  transform: translateY(-2px);
}

/* 下拉菜单样式 */
.dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-toggle {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.dropdown-toggle:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 160px;
  z-index: 1000;
  margin-top: 8px;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.dropdown:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 10px 16px;
  text-align: left;
  border: none;
  background: none;
  cursor: pointer;
  color: #2c3e50;
  text-decoration: none;
  transition: all 0.3s ease;
  border-radius: 4px;
  margin: 2px 0;
}

.dropdown-item:hover {
  background: #f5f7fa;
  color: #667eea;
  transform: translateX(4px);
}

.dropdown-item.router-link-exact-active {
  background: #f5f7fa;
  color: #667eea;
  font-weight: bold;
}

.app-content {
  width: 100%;
  height: 100%;
  padding: 20px;
  background: #f5f7fa;
}

/* 登录页面内容容器 */
.login-content {
  width: 100%;
  height: 1000px; /* 与设计稿高度一致 */
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 缩放容器 */
.scale-container {
  flex: 1;
  overflow: auto;
  position: relative;
  min-width: 0;
  min-height: 0;
}

@media (max-width: 768px) {
  .app-header {
    padding: 0 20px;
    height: auto;
    flex-direction: column;
    padding: 20px;
    gap: 15px;
  }
  
  .main-nav {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .nav-link {
    padding: 8px 12px;
    font-size: 14px;
  }
  
  .logout-button {
    margin-left: 0;
  }
  
  .app-content {
    padding: 10px;
  }
}
</style>
