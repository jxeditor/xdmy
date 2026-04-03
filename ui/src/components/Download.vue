<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">数据下载</h2>
      <div class="page-actions" style="display:flex;gap:8px;"></div>
    </div>

    <!-- 出货对账单 -->
    <div class="card" style="margin-bottom:16px;">
      <div class="card-header">
        <h3 class="card-title">出货对账单</h3>
      </div>
      <div class="filter-bar" style="border:none;padding:16px 0 0;">
        <el-row type="flex" justify="space-between" align="center" :gutter="20" style="width:100%;">
          <el-col :span="12">
            <el-date-picker v-model="shipmentBillDateInput"
                            type="daterange"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            value-format="YYYY-MM-DD"
                            :locale="zhCn"
                            style="width: 100%;"
            />
          </el-col>
          <el-col :span="6">
            <el-input v-model="customerInput" placeholder="输入客户名" style="width: 100%;"/>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" @click="downloadShipment" style="width: 100%;">下载出货对账单</el-button>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 入货对账单 -->
    <div class="card" style="margin-bottom:16px;">
      <div class="card-header">
        <h3 class="card-title">入货对账单</h3>
      </div>
      <div class="filter-bar" style="border:none;padding:16px 0 0;">
        <el-row type="flex" justify="space-between" align="center" :gutter="20" style="width:100%;">
          <el-col :span="12">
            <el-date-picker v-model="incomingBillDateInput"
                            type="daterange"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            value-format="YYYY-MM-DD"
                            :locale="zhCn"
                            style="width: 100%;"
            />
          </el-col>
          <el-col :span="6">
            <el-input v-model="producerInput" placeholder="输入供应商" style="width: 100%;"/>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" @click="downloadIncoming" style="width: 100%;">下载入货对账单</el-button>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import { zhCn } from "element-plus/dist/locale/zh-cn.mjs";

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
        // 401错误由响应拦截器处理，不显示错误信息
        if (error.response && error.response.status !== 401) {
          that.$message.error(error);
        }
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
        // 401错误由响应拦截器处理，不显示错误信息
        if (error.response && error.response.status !== 401) {
          that.$message.error(error);
        }
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
.page { padding: 24px; }

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-title { font-size: 1.25rem; font-weight: 700; color: var(--text-primary); }

.card {
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border);
  margin-bottom: 16px;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
}
.card-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
}

.filter-bar {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: flex-end;
  margin-bottom: 16px;
}
</style>
