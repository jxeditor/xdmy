<template>
  <div class="product-view">
    <Product msg="产品"/>
  </div>
</template>

<script>
import Product from '../components/Product.vue';

export default {
  name: 'ProductView',
  components: {
    Product
  }
}
</script>

<style scoped>
.product-view {
  width: 100%;
  height: 100%;
}
</style>
