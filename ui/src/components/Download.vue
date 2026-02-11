<template>
  <div class="download-container">
    <h1>数据下载</h1>
    
    <div class="content-wrapper">
      <!-- 出货对账单下载 -->
      <div class="download-card">
        <h2>出货对账单</h2>
        <el-row type="flex" justify="space-between" align="center" class="search-row" :gutter="20">
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
      
      <!-- 入货对账单下载 -->
      <div class="download-card">
        <h2>入货对账单</h2>
        <el-row type="flex" justify="space-between" align="center" class="search-row" :gutter="20">
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
/* 页面容器 */
.download-container {
  min-height: 100vh;
  padding: 20px;
  background-color: #f5f7fa;
}

/* 页面标题 */
.download-container h1 {
  text-align: center;
  color: #303133;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
  padding-bottom: 15px;
  border-bottom: 3px solid #667eea;
  display: inline-block;
  position: relative;
  left: 50%;
  transform: translateX(-50%);
  animation: fadeInDown 0.5s ease-out;
}

/* 内容容器 */
.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease-out;
}

/* 下载卡片 */
.download-card {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
  padding: 30px;
  animation: fadeInUp 0.5s ease-out 0.1s both;
}

/* 卡片标题 */
.download-card h2 {
  color: #303133;
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
  animation: fadeInUp 0.5s ease-out 0.2s both;
}

/* 搜索行 */
.search-row {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 12px;
  animation: fadeInUp 0.5s ease-out 0.3s both;
}

/* 按钮样式 */
.el-button {
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  padding: 10px 18px !important;
  font-weight: 500 !important;
}

.el-button:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
}

/* 日期选择器样式 */
.el-date-picker {
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

/* 输入框样式 */
.el-input {
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

.el-input:focus-within {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) !important;
  border-color: #667eea !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .download-container {
    padding: 10px;
  }
  
  .download-container h1 {
    font-size: 22px;
    margin-bottom: 20px;
  }
  
  .download-card {
    padding: 15px;
  }
  
  .download-card h2 {
    font-size: 18px;
  }
  
  .search-row {
    flex-wrap: wrap;
    padding: 15px;
  }
  
  .el-col {
    margin-bottom: 10px;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
