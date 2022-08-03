<template>
  <nav>
    <router-link to="/shipment">出货</router-link>
    |
    <router-link to="/incoming">入货</router-link>
    |
    <router-link to="/stock">库存</router-link>
    |
    <router-link to="/turnover">流水</router-link>
    |
    <router-link to="/screen">大屏</router-link>
    |
    <a href="" style="color:red" @click="handleLogout">注销</a>
  </nav>
  <router-view/>
</template>

<script>
export default {
  name: 'AppView',
  methods: {
    handleLogout() {
      localStorage.setItem('role', "GUEST");
      this.$router.push("/login");
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>
