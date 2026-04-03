<template>
  <el-form
    ref="loginForm"
    :model="loginForm"
    :rules="loginFormRules"
    class="login-form"
    @keyup.enter="handleLogin"
  >
    <el-form-item prop="username">
      <el-input
        v-model="loginForm.username"
        placeholder="用户名"
        size="large"
        autocomplete="off"
      >
        <template #prefix>
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"
               width="16" height="16" style="color:#94a3b8">
            <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"
                  stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="1.8"/>
          </svg>
        </template>
      </el-input>
    </el-form-item>

    <el-form-item prop="password">
      <el-input
        type="password"
        v-model="loginForm.password"
        placeholder="密码"
        size="large"
        show-password
      >
        <template #prefix>
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"
               width="16" height="16" style="color:#94a3b8">
            <rect x="3" y="11" width="18" height="11" rx="2"
                  stroke="currentColor" stroke-width="1.8"/>
            <path d="M7 11V7a5 5 0 0110 0v4"
                  stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
        </template>
      </el-input>
    </el-form-item>

    <el-button
      type="primary"
      size="large"
      class="login-btn"
      :loading="loading"
      @click="handleLogin"
    >
      {{ loading ? '登录中…' : '登 录' }}
    </el-button>
  </el-form>
</template>

<script>
import router from "@/router"
import axios from "axios"

export default {
  name: "Login",
  methods: {
    handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        this.$message.error('用户名和密码不能为空');
        return;
      }
      this.loading = true;
      const that = this;
      try {
        let encodedPassword = btoa(this.loginForm.password);
        let deviceInfo = {
          userAgent: navigator.userAgent,
          platform: navigator.platform,
          language: navigator.language,
          screen: `${window.screen.width}x${window.screen.height}`,
          timestamp: new Date().getTime()
        };
        let param = {
          username: this.loginForm.username,
          password: encodedPassword,
          deviceInfo: JSON.stringify(deviceInfo)
        };
        const loginUrl = `${process.env.VUE_APP_API_BASE_URL}/admin/verifyLogin`;
        const instance = axios.create({ headers: { 'Content-Type': 'application/json' } });
        instance.post(loginUrl, param)
          .then(function (response) {
            that.loading = false;
            if (response.data.code === 1) {
              localStorage.setItem('role', response.data.data[0].role);
              localStorage.setItem('companyName', response.data.data[0].companyName || '');
              localStorage.setItem('loginTime', new Date().getTime());
              localStorage.setItem('token', response.data.data[0].token);
              router.push("shipment");
            } else {
              that.$message.error(response.data.msg);
            }
          })
          .catch(function () {
            that.loading = false;
            that.$message.error('登录失败，请检查网络连接');
          });
      } catch {
        this.loading = false;
        this.$message.error('登录失败，请检查网络连接');
      }
    }
  },
  data() {
    return {
      loading: false,
      loginForm: { username: '', password: '' },
      loginFormRules: {
        username: [{ required: true, trigger: 'blur', message: '用户名不能为空' }],
        password: [{ required: true, trigger: 'blur', message: '密码不能为空' }],
      },
    }
  }
}
</script>

<style scoped>
.login-form {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

/* 输入框样式 */
.login-form :deep(.el-input__wrapper) {
  border-radius: 10px !important;
  border: 1.5px solid #e2e8f0 !important;
  box-shadow: none !important;
  padding: 0 14px !important;
  background: #f8fafc !important;
  transition: border-color .2s, box-shadow .2s !important;
}
.login-form :deep(.el-input__wrapper:hover) {
  border-color: #93c5fd !important;
}
.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #3b82f6 !important;
  background: #fff !important;
  box-shadow: 0 0 0 3px rgba(59,130,246,.12) !important;
}
.login-form :deep(.el-input__inner) {
  color: #0f172a !important;
  font-size: .9rem !important;
}
.login-form :deep(.el-input__inner::placeholder) {
  color: #94a3b8 !important;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 46px;
  border-radius: 10px !important;
  font-size: .95rem !important;
  font-weight: 600 !important;
  letter-spacing: .04em;
  margin-top: 8px;
  background: #1e40af !important;
  border-color: #1e40af !important;
  transition: background .2s, transform .15s, box-shadow .2s !important;
}
.login-btn:hover {
  background: #1d4ed8 !important;
  border-color: #1d4ed8 !important;
  box-shadow: 0 4px 16px rgba(30,64,175,.35) !important;
  transform: translateY(-1px);
}
.login-btn:active {
  transform: translateY(0) !important;
}
</style>
