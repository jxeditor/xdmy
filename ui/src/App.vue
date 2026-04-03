<template>
  <!-- 登录页：全屏，无侧边栏 -->
  <div v-if="isLogin" class="login-layout">
    <router-view />
  </div>

  <!-- 主应用布局：侧边栏 + 内容区 -->
  <div v-else class="app-layout">
    <!-- ── 侧边栏 ── -->
    <aside class="sidebar">
      <!-- 品牌区 -->
      <div class="sidebar-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" width="20" height="20">
            <path d="M20 7H4C2.9 7 2 7.9 2 9v10c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V9c0-1.1-.9-2-2-2z" stroke="currentColor" stroke-width="1.8" fill="none"/>
            <path d="M16 7V5c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v2" stroke="currentColor" stroke-width="1.8"/>
            <line x1="12" y1="12" x2="12" y2="16" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            <line x1="10" y1="14" x2="14" y2="14" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
        </div>
        <span class="brand-name">{{ companyName }}</span>
      </div>

      <!-- 导航菜单 -->
      <nav class="sidebar-nav">
        <!-- 出货 -->
        <router-link to="/shipment" class="nav-item">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M21 16V8a2 2 0 00-1-1.73l-7-4a2 2 0 00-2 0l-7 4A2 2 0 003 8v8a2 2 0 001 1.73l7 4a2 2 0 002 0l7-4A2 2 0 0021 16z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
            <path d="M3.27 6.96L12 12.01l8.73-5.05M12 22.08V12" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>出货</span>
        </router-link>

        <!-- 入货 -->
        <router-link to="/incoming" class="nav-item">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 3v12m0 0l-4-4m4 4l4-4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M20 17v1a2 2 0 01-2 2H6a2 2 0 01-2-2v-1" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
          <span>入货</span>
        </router-link>

        <!-- 产品管理 折叠组 -->
        <div class="nav-group">
          <button class="nav-group-toggle" :class="{ open: productMenuOpen }" @click="productMenuOpen = !productMenuOpen">
            <svg class="nav-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 19a2 2 0 01-2 2H4a2 2 0 01-2-2V5a2 2 0 012-2h5l2 3h9a2 2 0 012 2v11z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
            </svg>
            <span>产品管理</span>
            <svg class="chevron" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <div class="nav-sub" :class="{ open: productMenuOpen }">
            <router-link to="/product" class="nav-sub-item">产品</router-link>
            <router-link to="/stock" class="nav-sub-item">产品库存</router-link>
            <router-link to="/materialStock" class="nav-sub-item">原材料库存</router-link>
            <router-link to="/productMaterialRelation" class="nav-sub-item">原材料关系</router-link>
            <router-link to="/materialOperation" class="nav-sub-item">操作记录</router-link>
          </div>
        </div>

        <!-- 流水 -->
        <router-link to="/turnover" class="nav-item">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="2" y="5" width="20" height="14" rx="2" stroke="currentColor" stroke-width="1.8"/>
            <path d="M2 10h20" stroke="currentColor" stroke-width="1.8"/>
          </svg>
          <span>流水</span>
        </router-link>

        <!-- 大屏 -->
        <router-link to="/screen" class="nav-item">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 20V10M12 20V4M6 20v-6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>数据大屏</span>
        </router-link>

        <!-- 下载 -->
        <router-link to="/download" class="nav-item">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            <path d="M7 10l5 5 5-5M12 3v12" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>下载</span>
        </router-link>
      </nav>

      <!-- 注销 -->
      <div class="sidebar-footer">
        <button class="btn-logout" @click="handleLogout">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>注销</span>
        </button>
      </div>
    </aside>

    <!-- ── 主内容区 ── -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  name: 'AppView',
  data() {
    return {
      companyName: '',
      productMenuOpen: false,
    }
  },
  computed: {
    isLogin() {
      return this.$route.path === '/login'
    }
  },
  watch: {
    '$route': {
      handler(to) {
        this.updateCompanyName()
        // 产品管理子菜单：若当前路由在产品管理下则自动展开
        const productRoutes = ['/product', '/stock', '/materialStock', '/productMaterialRelation', '/materialOperation']
        if (productRoutes.includes(to.path)) {
          this.productMenuOpen = true
        }
      },
      immediate: true
    }
  },
  methods: {
    updateCompanyName() {
      this.companyName = localStorage.getItem('companyName') || '管理系统'
    },
    handleLogout() {
      localStorage.removeItem('role')
      localStorage.removeItem('loginTime')
      localStorage.removeItem('companyName')
      localStorage.removeItem('token')
      this.$router.push('/login')
    }
  }
}
</script>

<style>
/* ── CSS 变量 ── */
:root {
  --sidebar-w: 220px;
  --sidebar-bg: #0f172a;
  --sidebar-border: rgba(255,255,255,.06);
  --sidebar-text: #94a3b8;
  --sidebar-text-hover: #e2e8f0;
  --sidebar-active-bg: rgba(59,130,246,.15);
  --sidebar-active-text: #60a5fa;
  --content-bg: #f1f5f9;
  --card-bg: #ffffff;
  --border: #e2e8f0;
  --text-primary: #0f172a;
  --text-secondary: #64748b;
  --blue: #3b82f6;
  --green: #10b981;
  --red: #ef4444;
  --yellow: #f59e0b;

  /* 覆盖 Element Plus 主色 */
  --el-color-primary: #3b82f6;
  --el-color-primary-light-3: #93c5fd;
  --el-color-primary-light-5: #bfdbfe;
  --el-color-primary-light-7: #dbeafe;
  --el-color-primary-light-8: #eff6ff;
  --el-color-primary-light-9: #f0f9ff;
  --el-color-primary-dark-2: #2563eb;
}

*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

html, body, #app {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC',
               'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  color: var(--text-primary);
}

/* ── 登录布局（透传，LoginView 自己处理全屏样式）── */
.login-layout {
  min-height: 100vh;
  width: 100%;
}

/* ── 主布局 ── */
.app-layout {
  display: flex;
  min-height: 100vh;
}

/* ── 侧边栏 ── */
.sidebar {
  width: var(--sidebar-w);
  flex-shrink: 0;
  background: var(--sidebar-bg);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0; left: 0;
  height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
  z-index: 100;
  border-right: 1px solid var(--sidebar-border);
}

/* 品牌区 */
.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 16px 16px;
  border-bottom: 1px solid var(--sidebar-border);
}
.brand-icon {
  width: 36px; height: 36px;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  color: white;
  flex-shrink: 0;
}
.brand-name {
  font-size: .9rem;
  font-weight: 600;
  color: #e2e8f0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 导航 */
.sidebar-nav {
  flex: 1;
  padding: 12px 8px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 8px;
  color: var(--sidebar-text);
  text-decoration: none;
  font-size: .875rem;
  font-weight: 500;
  transition: background .15s, color .15s;
}
.nav-item:hover {
  background: rgba(255,255,255,.06);
  color: var(--sidebar-text-hover);
}
.nav-item.router-link-active,
.nav-item.router-link-exact-active {
  background: var(--sidebar-active-bg);
  color: var(--sidebar-active-text);
}
.nav-icon {
  width: 18px; height: 18px;
  flex-shrink: 0;
}

/* 折叠组 */
.nav-group { display: flex; flex-direction: column; }
.nav-group-toggle {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 8px;
  color: var(--sidebar-text);
  font-size: .875rem;
  font-weight: 500;
  background: none;
  border: none;
  cursor: pointer;
  transition: background .15s, color .15s;
  width: 100%;
  text-align: left;
}
.nav-group-toggle:hover {
  background: rgba(255,255,255,.06);
  color: var(--sidebar-text-hover);
}
.nav-group-toggle.open {
  color: var(--sidebar-text-hover);
}
.chevron {
  width: 14px; height: 14px;
  margin-left: auto;
  flex-shrink: 0;
  transition: transform .2s;
}
.nav-group-toggle.open .chevron { transform: rotate(90deg); }

.nav-sub {
  display: none;
  flex-direction: column;
  padding: 2px 0 4px 28px;
  gap: 2px;
}
.nav-sub.open { display: flex; }

.nav-sub-item {
  display: block;
  padding: 7px 10px;
  border-radius: 6px;
  color: var(--sidebar-text);
  text-decoration: none;
  font-size: .82rem;
  transition: background .15s, color .15s;
}
.nav-sub-item:hover {
  background: rgba(255,255,255,.06);
  color: var(--sidebar-text-hover);
}
.nav-sub-item.router-link-active,
.nav-sub-item.router-link-exact-active {
  background: var(--sidebar-active-bg);
  color: var(--sidebar-active-text);
}

/* 注销区 */
.sidebar-footer {
  padding: 12px 8px;
  border-top: 1px solid var(--sidebar-border);
}
.btn-logout {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 9px 10px;
  border-radius: 8px;
  background: none;
  border: none;
  color: var(--sidebar-text);
  font-size: .875rem;
  cursor: pointer;
  transition: background .15s, color .15s;
}
.btn-logout:hover {
  background: rgba(239,68,68,.15);
  color: #f87171;
}

/* ── 主内容区 ── */
.main-content {
  flex: 1;
  margin-left: var(--sidebar-w);
  min-height: 100vh;
  background: var(--content-bg);
  overflow-x: hidden;
}

/* ── 通用页面结构 ── */
.page {
  padding: 24px;
  min-height: 100vh;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
}
.page-subtitle {
  font-size: .85rem;
  color: var(--text-secondary);
  margin-top: 2px;
}
.page-body { display: flex; flex-direction: column; gap: 16px; }

/* ── 通用卡片 ── */
.card {
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border);
  padding: 20px;
}
.card-sm { padding: 16px; }

/* ── 过滤栏 ── */
.filter-bar {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: flex-end;
}

/* ── Element Plus 全局覆盖 ── */
.el-button--primary {
  background-color: var(--blue) !important;
  border-color: var(--blue) !important;
}
.el-button--primary:hover,
.el-button--primary:focus {
  background-color: #2563eb !important;
  border-color: #2563eb !important;
}
.el-pagination.is-background .el-pager li.is-active {
  background-color: var(--blue) !important;
}
.el-table th.el-table__cell {
  background: #f8fafc !important;
  color: var(--text-secondary) !important;
  font-weight: 600;
  font-size: .82rem;
}
.el-table {
  border-radius: 8px;
  overflow: hidden;
}
</style>
