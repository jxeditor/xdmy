<template>
  <div id="material-operation">
    <h1>{{ msg }}</h1>
    <div id="app">
      <!-- 筛选条件 -->
      <el-row type="flex" justify="space-between" align="center" style="width:100%;">
        <el-col :span="16">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-input
                v-model="materialName"
                placeholder="输入原材料名称"
                clearable
                prefix-icon="el-icon-search"
                style="width: 100%;"
              />
            </el-col>
            <el-col :span="6">
              <el-select v-model="operationType" placeholder="选择操作类型" clearable style="width: 100%;">
                <el-option label="出货" value="出货"></el-option>
                <el-option label="入货" value="入货"></el-option>
              </el-select>
            </el-col>
            <el-col :span="10">
              <el-date-picker v-model="dateRange"
                              type="daterange"
                              range-separator="至"
                              start-placeholder="开始日期"
                              end-placeholder="结束日期"
                              value-format="YYYY-MM-DD"
                              style="width: 100%;"
              />
            </el-col>
          </el-row>
        </el-col>
        <el-col :span="4" style="display: flex; justify-content: flex-end;">
          <el-button type="primary" @click="searchMaterialOperations">搜索</el-button>
        </el-col>
      </el-row>
      
      <!-- 操作记录表格 -->
      <el-table ref="multipleTable" stripe :data="materialOperationsData" style="width: 100%; table-layout: fixed;">
        <el-table-column prop="id" label="ID" min-width="60" align="center">
        </el-table-column>
        <el-table-column prop="operationType" label="操作类型" min-width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.operationType === '出货' ? 'danger' : 'success'">
              {{ scope.row.operationType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="billNo" label="业务单据号" min-width="180" align="center">
        </el-table-column>
        <el-table-column prop="productName" label="产品名称" min-width="220" align="center" show-overflow-tooltip>
        </el-table-column>
        <el-table-column prop="productQuantity" label="产品数量" min-width="100" align="center">
        </el-table-column>
        <el-table-column prop="materialName" label="原材料名称" min-width="220" align="center" show-overflow-tooltip>
        </el-table-column>
        <el-table-column prop="materialQuantity" label="原材料数量" min-width="120" align="center">
        </el-table-column>
        <el-table-column prop="operationDate" label="操作日期" min-width="180" align="center">
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="page.index"
        :page-size="page.size"
        layout="total,prev,pager,next"
        :total="page.total"
        @current-change="handleCurrentChange">
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: `MaterialOperationScript`,
  props: {
    msg: String
  },
  mounted() {
    this.getAllMaterialOperations()
  },
  methods: {
    sleep(ms) { //sleep延迟方法
      const time_ms = new Date().getTime();
      while (new Date().getTime() < time_ms + ms) {
      }
    },
    handleCurrentChange(currentPage) {
      this.page.index = currentPage;
      this.getAllMaterialOperations()
    },
    getAllMaterialOperations() {
      const that = this;
      const startDate = this.dateRange ? this.dateRange[0] : '';
      const endDate = this.dateRange ? this.dateRange[1] : '';
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/stock/findMaterialOperations` +
        `?pageNum=` + that.page.index + `&pageSize=` + that.page.size +
        `&materialName=` + that.materialName +
        `&operationType=` + that.operationType +
        `&startDate=` + startDate + `&endDate=` + endDate)
        .then(function (response) {
          console.log('获取原材料操作记录响应:', response.data)
          that.materialOperationsData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
        console.error('获取原材料操作记录失败:', error)
        that.$message.error('获取原材料操作记录失败');
      })
    },
    searchMaterialOperations() {
      this.page.index = 1;
      this.getAllMaterialOperations()
    }
  },
  data() {
    return {
      page: {
        //当前页码
        index: 1,
        //每页展示数据
        size: 10,
        // 总条数
        total: 0
      },
      materialOperationsData: [],
      materialName: '',
      operationType: '',
      dateRange: ''
    }
  }
}
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  background-color: #f5f7fa;
}

/* 页面容器 */
#material-operation {
  min-height: 100vh;
  padding: 20px;
}

#material-operation h1 {
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
#app {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 30px;
  width: 100%;
  animation: fadeInUp 0.5s ease-out;
}

/* 搜索容器 */
.stock-search-container {
  position: relative;
  width: 100%;
}

/* 联想结果下拉框 */
.stock-suggestions-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: #ffffff;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  max-height: 350px;
  overflow-y: auto;
  margin-top: 4px;
  animation: fadeIn 0.3s ease-out;
}

/* 联想结果项 */
.stock-suggestion-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
  overflow: hidden;
}

.stock-suggestion-item:hover {
  background-color: #f5f7fa;
  transform: translateX(8px);
}

.stock-suggestion-item:last-child {
  border-bottom: none;
}

/* 联想结果分页 */
.stock-suggestion-pagination {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
}

/* 表格样式 */
.el-table {
  width: 100% !important;
  margin-top: 24px;
  animation: fadeInUp 0.5s ease-out;
}

.el-table th {
  background-color: #f5f7fa !important;
  font-weight: 600 !important;
  color: #303133 !important;
}

.el-table tr {
  transition: all 0.3s ease;
}

.el-table tr:hover {
  background-color: #f5f7fa !important;
}

/* 分页样式 */
.el-pagination {
  margin-top: 30px;
  width: 100%;
  display: flex;
  justify-content: center;
  animation: fadeInUp 0.5s ease-out;
}

/* 动画效果 */
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

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translate(-50%, -20px);
  }
  to {
    opacity: 1;
    transform: translate(-50%, 0);
  }
}

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

/* 响应式设计 */
@media (max-width: 768px) {
  #app {
    padding: 20px;
  }
  
  .el-row {
    flex-direction: column;
    align-items: stretch !important;
  }
  
  .el-col {
    margin-bottom: 15px;
    width: 100% !important;
  }
  
  .el-col:last-child {
    margin-bottom: 0;
  }
}
</style>