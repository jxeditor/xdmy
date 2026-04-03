<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">{{ msg }}</h2>
      <div class="page-actions" style="display:flex;gap:8px;"></div>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-bar">
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
    </div>

    <!-- 操作记录表格 -->
    <div class="card" style="padding:0;overflow:hidden;">
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
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page.index"
          :page-size="page.size"
          layout="total,prev,pager,next"
          :total="page.total"
          @current-change="handleCurrentChange">
        </el-pagination>
      </div>
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
        // 401错误由响应拦截器处理，不显示错误信息
        if (error.response && error.response.status !== 401) {
          that.$message.error('获取原材料操作记录失败');
        }
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

<style scoped>
.page { padding: 24px; }

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-title { font-size: 1.25rem; font-weight: 700; color: var(--text-primary); }

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

.card {
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border);
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  border-top: 1px solid var(--border);
}
</style>