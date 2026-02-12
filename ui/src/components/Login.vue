<template>
  <el-form
    ref="loginForm"
    status-icon
    :model="loginForm"
    :rules="loginFormRules"
    label-width="80px"
    class="login-form">
    <el-form-item label="用户名" prop="username">
      <el-input 
        v-model="loginForm.username" 
        autocomplete="off"
        placeholder="请输入用户名"
        class="login-input"
      >
        <template #prefix>
          <el-icon class="el-input__icon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6">
              <path fill-rule="evenodd" d="M7.5 6a4.5 4.5 0 119 0 4.5 4.5 0 01-9 0zM3.751 20.105a8.25 8.25 0 0116.498 0 .75.75 0 01-.437.695A18.683 18.683 0 0112 22.5c-2.786 0-5.433-.608-7.812-1.7a.75.75 0 01-.437-.695z" clip-rule="evenodd" />
            </svg>
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item label="密码" prop="password">
      <el-input 
        type="password" 
        v-model="loginForm.password" 
        placeholder="请输入密码"
        class="login-input"
        show-password
      >
        <template #prefix>
          <el-icon class="el-input__icon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6">
              <path fill-rule="evenodd" d="M12 1.5a5.25 5.25 0 00-5.25 5.25v3a3 3 0 00-3 3v6.75a3 3 0 003 3h10.5a3 3 0 003-3v-6.75a3 3 0 00-3-3v-3c0-2.9-2.35-5.25-5.25-5.25zm3.75 8.25v3a1.5 1.5 0 01-1.5 1.5h-7.5a1.5 1.5 0 01-1.5-1.5v-3a3.75 3.75 0 117.5 0z" clip-rule="evenodd" />
            </svg>
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item class="login-button-container">
      <el-button 
        type="primary" 
        @click="handleLogin"
        class="login-button"
        :loading="loading"
      >
        {{ loading ? '登录中...' : '登录' }}
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import router from "@/router"
import axios from "axios"

export default {
  name: "Login",
  methods: {
    handleLogin() {
      console.log('Login button clicked');
      console.log('Login form:', this.loginForm);
      
      // 直接验证表单，不使用Element Plus的validate方法
      if (!this.loginForm.username || !this.loginForm.password) {
        console.log('Form validation failed: username or password is empty');
        this.$message.error('用户名和密码不能为空');
        return;
      }
      
      console.log('Form validation passed');
      this.loading = true;
      console.log('Loading set to true');
      
      const that = this;
      
      try {
        // 对密码进行Base64编码，增加传输安全性
        console.log('Encoding password:', this.loginForm.password);
        let encodedPassword = btoa(this.loginForm.password);
        console.log('Encoded password:', encodedPassword);
        
        let param = {
          username: this.loginForm.username,
          password: encodedPassword
        };
        console.log('Login params:', param);
        
        // 使用环境变量中的API Base URL
        console.log('API Base URL:', process.env.VUE_APP_API_BASE_URL);
        const loginUrl = `${process.env.VUE_APP_API_BASE_URL}/admin/verifyLogin`;
        console.log('Login URL:', loginUrl);
        
        // 创建新的axios实例，避免拦截器的影响
        console.log('Creating axios instance');
        const instance = axios.create({
          headers: {
            'Content-Type': 'application/json'
          }
        });
        console.log('Axios instance created:', instance);
        
        // 发送登录请求
        console.log('Sending login request');
        instance.post(loginUrl, param)
          .then(function (response) {
            console.log('Login response received:', response);
            that.loading = false;
            console.log('Loading set to false');
            
            if (response.data.code === 1) {
              console.log('Login successful:', response.data.data[0]);
              localStorage.setItem('role', response.data.data[0].role);
              localStorage.setItem('companyName', response.data.data[0].companyName || '');
              localStorage.setItem('loginTime', new Date().getTime());
              // 存储Token
              localStorage.setItem('token', response.data.data[0].token);
              console.log('Token stored:', response.data.data[0].token);
              console.log('Redirecting to shipment page');
              router.push("shipment");
            } else {
              console.log('Login failed:', response.data.msg);
              that.$message.error(response.data.msg);
            }
          })
          .catch(function (error) {
            console.log('Login error:', error);
            that.loading = false;
            console.log('Loading set to false');
            that.$message.error('登录失败，请检查网络连接');
          });
      } catch (error) {
        console.log('Error in handleLogin:', error);
        this.loading = false;
        console.log('Loading set to false');
        this.$message.error('登录失败，请检查网络连接');
      }
    }
  },
  data() {
    return {
      loading: false,
      loginForm: {
        username: '',
        password: ''
      },
      loginFormRules: {
        username: [
          {
            required: true,
            trigger: 'blur',
            message: '用户名不能为空'
          }
        ],
        password: [
          {
            required: true,
            trigger: 'blur',
            message: '密码不能为空'
          }
        ],
      },
    }
  }
}
</script>

<style scoped>
.login-form {
  width: 100%;
}

.login-input {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.login-input:focus {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) !important;
  border-color: #667eea !important;
}

.login-button-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.login-button {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.login-button:active {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.6;
  transform: none;
  box-shadow: none;
}

/* 输入框图标样式 */
.el-input__icon {
  color: #999;
  font-size: 16px;
}

/* 表单标签样式 */
.el-form-item__label {
  font-weight: 500;
  color: #333;
}

/* 错误提示样式 */
.el-form-item__error {
  font-size: 12px;
  color: #f56c6c;
}
</style>
