<template>
  <div class="login-page">

    <!-- ── 左侧品牌面板 ── -->
    <div class="brand-panel">
      <!-- 装饰光晕 -->
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>

      <div class="brand-inner">
        <div class="brand-logo">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" width="32" height="32">
            <path d="M20 7H4C2.9 7 2 7.9 2 9v10c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V9c0-1.1-.9-2-2-2z"
                  stroke="white" stroke-width="1.8" fill="none"/>
            <path d="M16 7V5c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v2" stroke="white" stroke-width="1.8"/>
            <line x1="12" y1="12" x2="12" y2="16" stroke="white" stroke-width="1.8" stroke-linecap="round"/>
            <line x1="10" y1="14" x2="14" y2="14" stroke="white" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
        </div>
        <h1 class="brand-title">{{ companyName }}</h1>
        <p class="brand-subtitle">高效管理，让业务更清晰</p>

        <!-- 特性列表 -->
        <ul class="feature-list">
          <li>
            <svg viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
            </svg>
            <span>出入货管理，一站式处理</span>
          </li>
          <li>
            <svg viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
            </svg>
            <span>库存实时同步，精确到原材料</span>
          </li>
          <li>
            <svg viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
            </svg>
            <span>数据可视化大屏，掌握全局</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- ── 右侧表单面板 ── -->
    <div class="form-panel">
      <div class="form-inner">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>请输入账号和密码登录</p>
        </div>

        <Login />

        <p class="form-footer">内部系统 · 仅限授权人员访问</p>
      </div>
    </div>

  </div>
</template>

<script>
import Login from '@/components/Login.vue'

export default {
  name: 'LoginView',
  components: { Login },
  computed: {
    companyName() {
      return localStorage.getItem('companyName') || '管理系统'
    }
  }
}
</script>

<style scoped>
/* ── 整页布局 ── */
.login-page {
  display: flex;
  min-height: 100vh;
  width: 100%;
}

/* ── 左侧品牌面板 ── */
.brand-panel {
  flex: 1.1;
  background: linear-gradient(145deg, #0f172a 0%, #1e3a5f 60%, #0f1f3d 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 60px;
}

/* 装饰光晕 */
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: .35;
  pointer-events: none;
}
.orb-1 {
  width: 400px; height: 400px;
  background: #3b82f6;
  top: -100px; left: -100px;
}
.orb-2 {
  width: 300px; height: 300px;
  background: #6366f1;
  bottom: -80px; right: -60px;
}
.orb-3 {
  width: 200px; height: 200px;
  background: #0ea5e9;
  top: 50%; left: 55%;
  transform: translate(-50%, -50%);
}

.brand-inner {
  position: relative;
  z-index: 1;
  max-width: 420px;
}

.brand-logo {
  width: 72px; height: 72px;
  background: rgba(255,255,255,.1);
  border: 1px solid rgba(255,255,255,.15);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28px;
  backdrop-filter: blur(10px);
}

.brand-title {
  font-size: 2.2rem;
  font-weight: 800;
  color: #f1f5f9;
  letter-spacing: -.02em;
  margin-bottom: 10px;
  line-height: 1.2;
}

.brand-subtitle {
  font-size: 1rem;
  color: #94a3b8;
  margin-bottom: 48px;
  line-height: 1.6;
}

.feature-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.feature-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #cbd5e1;
  font-size: .9rem;
}
.feature-list li svg {
  color: #60a5fa;
  flex-shrink: 0;
}

/* ── 右侧表单面板 ── */
.form-panel {
  width: 440px;
  flex-shrink: 0;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 48px;
}

.form-inner {
  width: 100%;
  max-width: 340px;
}

.form-header {
  margin-bottom: 36px;
}
.form-header h2 {
  font-size: 1.75rem;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 6px;
  letter-spacing: -.02em;
}
.form-header p {
  font-size: .9rem;
  color: #64748b;
}

.form-footer {
  text-align: center;
  font-size: .75rem;
  color: #94a3b8;
  margin-top: 32px;
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .brand-panel { display: none; }
  .form-panel {
    width: 100%;
    padding: 48px 32px;
  }
}
</style>
