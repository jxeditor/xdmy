<template>
  <div id="download">
    <div id="shipment">
      <el-row style="width:100%;padding: 20px;">
        <el-col :span="10">
          <el-date-picker v-model="shipmentBillDateInput"
                          type="daterange"
                          range-separator="至"
                          start-placeholder="开始日期"
                          end-placeholder="结束日期"
                          value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="2">
          <el-input v-model="customerInput" placeholder="输入客户名"/>
        </el-col>
        <el-col :span="2"></el-col>
        <el-col :span="4">
          <el-button type="primary" @click="downloadShipment">下载出货对账单</el-button>
        </el-col>
      </el-row>
    </div>

    <div id="incoming">
      <el-row style="width:100%;padding: 20px;">
        <el-col :span="10">
          <el-date-picker v-model="incomingBillDateInput"
                          type="daterange"
                          range-separator="至"
                          start-placeholder="开始日期"
                          end-placeholder="结束日期"
                          value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="2">
          <el-input v-model="producerInput" placeholder="输入供应商"/>
        </el-col>
        <el-col :span="2"></el-col>
        <el-col :span="4">
          <el-button type="primary" @click="downloadIncoming">下载入货对账单</el-button>
        </el-col>
      </el-row>
    </div>
  </div>

</template>

<script>
export default {
  name: "Download",
  methods: {
    downloadShipment() {
      const that = this;
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/download/downloadShipmentStatement` +
        `?customerName=` + that.customerInput +
        `&bizStartDate=` + that.shipmentBillDateInput[0] + `&bizEndDate=` + that.shipmentBillDateInput[1], {responseType: `blob`})
        .then(function (res) {
          let data = res.data
          let filename = "用户明细表.xlsx"
          console.log(res)
          if (res.headers.filename) {
            filename = decodeURI(res.headers.filename)
          }
          let url = window.URL.createObjectURL(new Blob([data]))
          let link = document.createElement(`a`)
          link.style.display = `none`
          link.href = url
          link.setAttribute(`download`, filename)

          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link) // 下载完成移除元素
          window.URL.revokeObjectURL(url) // 释放掉blob对象
        }).catch((error) => {
        that.$message.error(error);
      })
    },
    downloadIncoming() {
      const that = this;
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/download/downloadIncomingStatement` +
        `?producerName=` + that.producerInput +
        `&bizStartDate=` + that.incomingBillDateInput[0] + `&bizEndDate=` + that.incomingBillDateInput[1], {responseType: `blob`})
        .then(function (res) {
          let data = res.data
          let filename = "用户明细表.xlsx"
          console.log(res)
          if (res.headers.filename) {
            filename = decodeURI(res.headers.filename)
          }
          let url = window.URL.createObjectURL(new Blob([data]))
          let link = document.createElement(`a`)
          link.style.display = `none`
          link.href = url
          link.setAttribute(`download`, filename)

          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link) // 下载完成移除元素
          window.URL.revokeObjectURL(url) // 释放掉blob对象
        }).catch((error) => {
        that.$message.error(error);
      })
    }
  },
  data() {
    return {
      customerInput: ``,
      producerInput: ``,
      shipmentBillDateInput: ``,
      incomingBillDateInput: ``,
    }
  }
}
</script>

<style scoped>

</style>
