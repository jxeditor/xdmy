<template>
  <div class="stock">
    <Stock msg="雄达木业"/>
  </div>
</template>

<script>
// @ is an alias to /src
import Stock from '@/components/Stock.vue'

export default {
  name: 'StockView',
  components: {
    Stock
  }
}
</script>
