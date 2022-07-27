<template>
  <el-form
    ref="loginForm"
    status-icon
    :model="loginForm"
    :rules="loginFormRules"
    label-width="120px">
    <el-form-item label="用户名：" prop="username">
      <el-input v-model="loginForm.username" autocomplete="off">
        <template>
          <el-icon class="el-input__icon">
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item label="密码：" prop="password">
      <el-input type="password" v-model="loginForm.password" placeholder="请输入密码">
        <template>
          <el-icon class="el-input__icon">
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleLogin">登录</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import router from '@/router'

export default {
  name: "Login",
  methods: {
    handleLogin() {
      const that = this
      let param = new URLSearchParams()
      param.append('username', this.loginForm.username)
      param.append('password', this.loginForm.password)
      this.$axios.post('http://124.223.70.175:8088/admin/verifyLogin', param).then(function (response) {
        if (response.data.code === 1) {
          localStorage.setItem('role', response.data.data[0].role);
          // window.location.href = '/#/shipment';
          router.push("shipment")
        } else {
          that.$message.error(response.data.msg);
        }
      }).catch(function (error) {
      })
    }
  },
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginFormRules: {
        username: [
          {
            required: true,
            trigger: 'blur',
            message: '用户名长度在2-6之间'
          }
        ],
        password: [
          {
            required: true,
            trigger: 'blur',
            message: '密码不能为空'
          }
        ]
      },
    }
  }
}
</script>

<style scoped>

</style>
