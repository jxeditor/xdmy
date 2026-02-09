<template>
  <div class="login-container">
    <div class="login-background">
      <div class="login-shape"></div>
      <div class="login-shape"></div>
      <div class="login-shape"></div>
      <div class="login-shape"></div>
    </div>
    <div class="login-card">
      <div class="login-header">
        <h1>{{ msg }}</h1>
        <p>欢迎回来，请登录您的账号</p>
      </div>
      <Login/>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import Login from '@/components/Login.vue'

export default {
  name: 'LoginView',
  components: {
    Login
  },
  data() {
    return {
      msg: '雄达木业'
    }
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  z-index: 0;
}

.login-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 8s ease-in-out infinite;
}

.login-shape:nth-child(1) {
  width: 100px;
  height: 100px;
  top: 10%;
  left: 20%;
  animation-delay: 0s;
}

.login-shape:nth-child(2) {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 10%;
  animation-delay: 2s;
}

.login-shape:nth-child(3) {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 30%;
  animation-delay: 4s;
}

.login-shape:nth-child(4) {
  width: 120px;
  height: 120px;
  top: 30%;
  right: 30%;
  animation-delay: 6s;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  animation: fadeInUp 1s ease-out;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.login-header p {
  font-size: 16px;
  color: #666;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .login-card {
    width: 90%;
    padding: 30px;
  }
}
</style>
