<template>
  <div id="stock">
    <h1>{{ msg }}</h1>
    <div id="app">
      <el-container style="max-width: 1400px; margin: 0 auto;">
        <el-header style="padding: 0; margin-bottom: 20px;">
          <el-row :gutter="10" type="flex" justify="start" align="middle" class="search-row">
            <el-col :xs="20" :sm="16" :md="12" :lg="8">
              <div class="search-container">
                <el-input
                  v-model="productInput"
                  placeholder="输入产品名"
                  clearable
                  prefix-icon="el-icon-search"
                  style="width: 100%;"
                >
                  <template #append>
                    <el-button @click="searchStock" type="primary">搜索</el-button>
                  </template>
                </el-input>
                <!-- 联想结果下拉框 -->
                <div v-if="showSuggestions && suggestOptions.length > 0" class="suggestions-dropdown">
                  <div 
                    v-for="(item, index) in suggestOptions" 
                    :key="index"
                    class="suggestion-item"
                    @click="selectSuggestion(item)"
                  >
                    {{ item }}
                  </div>
                  <!-- 分页控件 -->
                  <div v-if="suggestTotal > suggestPageSize" class="suggestion-pagination">
                    <el-pagination
                      small
                      layout="prev, pager, next, ->, total"
                      :total="suggestTotal"
                      :page-size="suggestPageSize"
                      :current-page="suggestCurrentPage"
                      @current-change="handleSuggestPageChange"
                      style="margin-top: 10px;"
                    />
                  </div>
                </div>
              </div>
            </el-col>
            <el-col :xs="12" :sm="4" :md="4" :lg="3">
              <el-button type="primary" @click="onAddStock" class="w-100">添加库存</el-button>
            </el-col>
          </el-row>
        </el-header>
        <el-main>
      <el-table ref="multipleTable" stripe :data="StockData" style="width: 100%;">
        <el-table-column prop="id" label="UID" align="center">
        </el-table-column>
        <el-table-column prop="product" label="产品" width="240px" align="center">
        </el-table-column>
        <el-table-column prop="stock" label="剩余库存" width="80" align="center">
        </el-table-column>
        <el-table-column prop="unitstock" label="初始库存数量" width="120" align="center">
        </el-table-column>
        <el-table-column prop="unitprice" label="单价" width="80" align="center">
        </el-table-column>
        <el-table-column prop="purchaseprice" label="采购单价" width="80" align="center">
        </el-table-column>
        <el-table-column prop="inamount" label="累计入货数量" width="120" align="center">
        </el-table-column>
        <el-table-column prop="outamount" label="累计出货数量" width="120" align="center">
        </el-table-column>
        <el-table-column prop="money" label="金额" width="80" align="center">
        </el-table-column>
        <el-table-column prop="lastindate" label="最后一次入货时间" width="140" align="center">
        </el-table-column>
        <el-table-column prop="lastoutdate" label="最后一次出货时间" width="140" align="center">
        </el-table-column>
        <el-table-column prop="stockstatus" :formatter="setStockStatus" label="是否初始化库存" width="140" align="center">
        </el-table-column>
        <el-table-column fixed="right" label="操作" align="center" width="180">
          <template #default="opt">
            <el-button type="primary" @click="onUpdateStock(opt.row)">修改</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page.index"
        :page-size="page.size"
        layout="total,prev,pager,next"
        :total="page.total"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; text-align: center;"
      >
      </el-pagination>
        </el-main>
      </el-container>
      <el-dialog title="初始化库存" v-model="addStockVisible" width="80%">
        <el-form ref="addStockForm" :rules="addStockFormRules" :model="addStockForm" label-width="120px">
          <el-form-item label="产品:" prop="product">
            <el-input v-model="addStockForm.product"></el-input>
          </el-form-item>
          <el-form-item label="初始库存数量:" prop="unitstock">
            <el-input v-model="addStockForm.unitstock"></el-input>
          </el-form-item>
          <el-form-item label="单价:" prop="unitprice">
            <el-input v-model="addStockForm.unitprice"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onAddStockCommit(`addStockForm`)">确定</el-button>
            <el-button @click="onAddStockCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <el-dialog title="修改库存信息" v-model="updateStockVisible" width="80%">
        <el-form ref="updateStockForm" :rules="updateStockFormRules" :model="updateStockForm"
                 label-width="120px">
          <el-form-item label="产品:" prop="product">
            <el-input v-model="updateStockForm.product"></el-input>
          </el-form-item>
          <el-form-item label="初始库存数量:" prop="unitstock">
            <el-input v-model="updateStockForm.unitstock"></el-input>
          </el-form-item>
          <el-form-item label="单价:" prop="unitprice">
            <el-input v-model="updateStockForm.unitprice"></el-input>
          </el-form-item>
          <el-form-item label="库存状态:" prop="stockstatus">
            <el-select v-model="updateStockForm.stockstatus" placeholder="请选择库存状态">
              <el-option label="已初始化" value="1"></el-option>
              <el-option label="不进行推送" value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onUpdateStockCommit(`updateStockForm`)">确定</el-button>
            <el-button @click="onUpdateStockCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </div>
  </div>
</template>

<script>
export default {
  name: "Stock",
  props: {
    msg: String
  },
  mounted() {
    this.getAllStock()
  },
  methods: {
    // 设置库存状态文本
    setStockStatus(row) {
      switch (row.stockstatus) {
        case "0":
          return "未初始化";
        case "1":
          return "已初始化";
        case "2":
          return "不进行推送";
        default:
          return "未定义";
      }
    },
    // 处理分页切换
    handleCurrentChange(currentPage) {
      this.page.index = currentPage;
      this.getAllStock()
    },
    // 取消添加库存
    onAddStockCancel() {
      this.addStockVisible = false
    },
    // 取消修改库存
    onUpdateStockCancel() {
      this.updateStockVisible = false
    },
    // 打开添加库存对话框
    onAddStock() {
      this.addStockVisible = true
    },
    // 打开修改库存对话框
    onUpdateStock(stock) {
      this.updateStockForm = stock
      this.updateStockVisible = true
    },
    // 提交添加库存
    onAddStockCommit(addStockForm) {
      const that = this
      this.$refs[addStockForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append(`product`, that.addStockForm.product)
          param.append(`unitstock`, that.addStockForm.unitstock)
          param.append(`unitprice`, that.addStockForm.unitprice)
          that.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/addStock`, param).then(function (response) {
            if (response.data.code === 1) {
              that.addStockVisible = false
              that.getAllStock()
            } else {
              that.$message.error(response.data.msg);
            }
          }).catch(function (error) {
            that.$message.error(error);
          })
        } else {
          return false
        }
      })
    },
    // 提交修改库存
    onUpdateStockCommit(updateStockForm) {
      const that = this
      this.$refs[updateStockForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append(`id`, that.updateStockForm.id)
          param.append(`product`, that.updateStockForm.product)
          param.append(`unitstock`, that.updateStockForm.unitstock)
          param.append(`unitprice`, that.updateStockForm.unitprice)
          param.append(`stockstatus`, that.updateStockForm.stockstatus)
          that.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/updateStock`, param).then(function (response) {
            if (response.data.code === 1) {
              that.updateStockVisible = false
              that.getAllStock()
            } else {
              that.$message.error(response.data.msg);
            }
          }).catch(function (error) {
            that.$message.error(error);
          })
        } else {
          return false
        }
      })
    },
    // 获取所有库存
    getAllStock() {
      const that = this;
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/stock/findAllStock` +
        `?pageNum=` + that.page.index + `&pageSize=` + that.page.size +
        `&productName=` + that.productInput)
        .then(function (response) {
          that.StockData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    // 搜索库存
    searchStock() {
      const that = this
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/stock/findAllStock` +
        `?pageNum=` + that.page.index + `&pageSize=` + that.page.size +
        `&productName=` + that.productInput)
        .then(function (response) {
          that.StockData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    // 获取联想建议
    getSuggestions() {
      const that = this
      if (that.productInput.length < 1) {
        that.suggestOptions = []
        that.showSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/findProductNamesByPrefix`, {
        prefix: that.productInput,
        pageNum: that.suggestCurrentPage,
        pageSize: that.suggestPageSize
      })
        .then(function (response) {
          that.suggestOptions = response.data.data
          that.suggestTotal = response.data.total
          that.showSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.suggestOptions = []
        that.showSuggestions = false
      })
    },
    // 选择联想建议
    selectSuggestion(item) {
      this.productInput = item
      this.showSuggestions = false
    },
    // 处理联想分页
    handleSuggestPageChange(pageNum) {
      this.suggestCurrentPage = pageNum
      this.getSuggestions()
    }
  },
  watch: {
    // 监听输入变化，获取联想建议
    productInput: {
      handler(val) {
        this.suggestCurrentPage = 1
        this.getSuggestions()
      },
      debounce: 300
    }
  },
  data() {
    return {
      // 分页参数
      page: {
        index: 1,
        size: 20,
        total: 0
      },
      // 库存数据
      StockData: [],
      // 搜索参数
      productInput: ``,
      // 对话框状态
      addStockVisible: false,
      updateStockVisible: false,
      // 表单数据
      addStockForm: {
        product: ``,
        unitstock: 0,
        unitprice: 0
      },
      updateStockForm: {
        product: ``,
        unitstock: 0,
        unitprice: 0
      },
      // 表单验证规则
      addStockFormRules: {
        product: [
          {required: true, message: `请输入产品名`, trigger: `blur`},
        ],
        unitstock: [
          {required: true, message: `请输入数量`, trigger: `blur`},
        ],
        unitprice: [
          {required: true, message: `请输入单价`, trigger: `blur`},
        ]
      },
      updateStockFormRules: {
        product: [
          {required: true, message: `请输入产品名`, trigger: `blur`},
        ],
        unitstock: [
          {required: true, message: `请输入数量`, trigger: `blur`},
        ],
        unitprice: [
          {required: true, message: `请输入单价`, trigger: `blur`},
        ]
      },
      // 联想功能参数
      suggestOptions: [],
      suggestTotal: 0,
      suggestCurrentPage: 1,
      suggestPageSize: 10,
      showSuggestions: false
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
#stock {
  min-height: 100vh;
  padding: 20px;
}

#stock h1 {
  text-align: center;
  color: #303133;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 600;
}

/* 内容容器 */
#app {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin: 0 auto;
  max-width: 1400px;
}

/* 搜索行 */
.search-row {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

/* 搜索容器 */
.search-container {
  position: relative;
  width: 100%;
}

/* 联想结果下拉框 */
.suggestions-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: #ffffff;
  border: 1px solid #dcdfe6;
  border-radius: 0 0 4px 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 1000;
  max-height: 300px;
  overflow-y: auto;
}

/* 联想结果项 */
.suggestion-item {
  padding: 10px 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.suggestion-item:hover {
  background-color: #ecf5ff;
  color: #409eff;
}

/* 联想分页 */
.suggestion-pagination {
  padding: 10px;
  border-top: 1px solid #e4e7ed;
  background-color: #f5f7fa;
}

.suggestion-pagination .el-pagination {
  margin-top: 0;
}

/* 表格样式 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.el-table th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #303133;
}

.el-table tr:hover {
  background-color: #f5f7fa;
}

/* 分页样式 */
.el-pagination {
  margin-top: 30px;
  text-align: center;
}

/* 按钮样式 */
.el-button {
  border-radius: 4px;
  transition: all 0.3s;
}

.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 对话框样式 */
.el-dialog {
  border-radius: 8px;
  overflow: hidden;
}

.el-dialog__header {
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.el-dialog__title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.el-dialog__body {
  padding: 30px;
  background-color: #ffffff;
}

/* 表单样式 */
.el-form {
  max-width: 600px;
  margin: 0 auto;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__label {
  font-weight: 500;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  #stock {
    padding: 10px;
  }
  
  #app {
    padding: 15px;
  }
  
  #stock h1 {
    font-size: 20px;
    margin-bottom: 20px;
  }
  
  .search-row {
    padding: 15px;
  }
  
  .el-dialog__body {
    padding: 20px;
  }
  
  .el-form {
    max-width: 100%;
  }
  
  .suggestions-dropdown {
    max-height: 200px;
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>