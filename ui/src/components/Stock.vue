<template>
  <div id="stock">
    <h1>{{ msg }}</h1>
    <div id="app">
      <!-- 搜索行 -->
      <el-row type="flex" justify="space-between" align="center" style="width:100%;padding: 10px 20px; margin-bottom: 24px;">
        <el-col :span="8">
          <div class="stock-search-container">
            <el-input
              v-model="productInput"
              placeholder="输入产品名"
              clearable
              prefix-icon="el-icon-search"
              style="width: 100%;"
            />
            <!-- 联想结果下拉框 -->
            <div v-if="showSuggestions && suggestOptions.length > 0" class="stock-suggestions-dropdown">
              <div 
                v-for="(item, index) in suggestOptions" 
                :key="index"
                class="stock-suggestion-item"
                @click="selectSuggestion(item)"
              >
                {{ item }}
              </div>
              <!-- 分页控件 -->
              <div v-if="suggestTotal > suggestPageSize" class="stock-suggestion-pagination">
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
        <el-col :span="8" style="display: flex; justify-content: flex-end; gap: 10px;">
          <el-button type="primary" @click="searchStock">搜索</el-button>
          <el-button type="primary" @click='onAddStock'>添加库存</el-button>
          <el-button type="warning" @click="toggleHideZeroStock">{{ hideZeroStock ? '取消隐藏' : '隐藏' }}</el-button>
          <el-button type="danger" @click="flattenStock">抹平</el-button>
        </el-col>
      </el-row>
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
            <el-button type="primary" @click="onAddStockCommit('addStockForm')">确定</el-button>
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
            <el-button type="primary" @click="onUpdateStockCommit('updateStockForm')">确定</el-button>
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
          param.append('product', that.addStockForm.product)
          param.append('unitstock', that.addStockForm.unitstock)
          param.append('unitprice', that.addStockForm.unitprice)
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
          param.append('id', that.updateStockForm.id)
          param.append('product', that.updateStockForm.product)
          param.append('unitstock', that.updateStockForm.unitstock)
          param.append('unitprice', that.updateStockForm.unitprice)
          param.append('stockstatus', that.updateStockForm.stockstatus)
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
      console.log('Get all stock with hideZeroStock:', that.hideZeroStock)
      console.log('API URL:', `${process.env.VUE_APP_API_BASE_URL}/stock/findAllStock` +
        '?pageNum=' + that.page.index + '&pageSize=' + that.page.size +
        '&productName=' + that.productInput +
        '&hideZeroStock=' + that.hideZeroStock)
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/stock/findAllStock` +
        '?pageNum=' + that.page.index + '&pageSize=' + that.page.size +
        '&productName=' + that.productInput +
        '&hideZeroStock=' + that.hideZeroStock)
        .then(function (response) {
          console.log('API response:', response.data)
          // 使用深拷贝确保Vue能够检测到数据变化
          that.StockData = JSON.parse(JSON.stringify(response.data.data))
          that.page.total = response.data.total
          console.log('Updated StockData:', that.StockData)
          console.log('Updated page.total:', that.page.total)
          // 强制Vue重新渲染组件，确保表格能够正确响应数据变化
          that.$forceUpdate()
          console.log('Forced update completed')
        }).catch(function (error) {
        console.error('API error:', error)
        that.$message.error(error);
      })
    },
    // 搜索库存
    searchStock() {
      const that = this
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/stock/findAllStock` +
        '?pageNum=' + that.page.index + '&pageSize=' + that.page.size +
        '&productName=' + that.productInput +
        '&hideZeroStock=' + that.hideZeroStock)
        .then(function (response) {
          console.log('Search API response:', response.data)
          // 使用深拷贝确保Vue能够检测到数据变化
          that.StockData = JSON.parse(JSON.stringify(response.data.data))
          that.page.total = response.data.total
          console.log('Updated StockData after search:', that.StockData)
          console.log('Updated page.total after search:', that.page.total)
        }).catch(function (error) {
        console.error('Search API error:', error)
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
    },
    // 切换隐藏零库存状态
    toggleHideZeroStock() {
      this.hideZeroStock = !this.hideZeroStock
      console.log('Toggle hideZeroStock:', this.hideZeroStock)
      // 先将StockData设置为空数组，强制表格组件重新渲染
      this.StockData = []
      // 然后调用getAllStock方法获取新数据
      this.getAllStock()
    },
    // 抹平库存
    flattenStock() {
      const that = this
      
      // 先获取需要抹平的库存数据数量
      that.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/stock/getFlattenStockCount`)
        .then(function (countResponse) {
          if (countResponse.data.code === 1) {
            const flattenCount = countResponse.data.data
            
            // 检查需要抹除的数据条数是否为0
            if (flattenCount === 0) {
              // 显示黄色的提示信息，说明具体原因
              that.$message({
                message: '无需执行库存抹平操作，当前没有需要初始化的UID为0数据和负库存数据。',
                type: 'warning'
              });
              return;
            }
            
            // 显示确认弹窗，包含操作数量
            that.$confirm(`确定要执行库存抹平操作吗？这将初始化UID为0的数据并调整负库存为0。\n本次操作将处理 ${flattenCount} 条数据。`, '警告', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              // 显示进度条
              const loadingInstance = that.$loading({
                lock: true,
                text: '库存抹平中，请稍候...',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
              });
              
              // 执行库存抹平操作
              that.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/flattenStock`)
                .then(function (response) {
                  // 关闭进度条
                  loadingInstance.close();
                  
                  if (response.data.code === 1) {
                    that.$message.success('库存抹平成功');
                    that.getAllStock()
                  } else {
                    // 只有当需要抹除的数据条数不为0但抹平失败时才显示红色提示信息
                    that.$message.error('库存抹平失败');
                  }
                }).catch(function (error) {
                  // 关闭进度条
                  loadingInstance.close();
                  that.$message.error('库存抹平失败：' + error);
                })
            }).catch(() => {
              // 取消操作
            })
          } else {
            that.$message.error('获取需要抹平的库存数据数量失败');
          }
        }).catch(function (error) {
          that.$message.error('获取需要抹平的库存数据数量失败：' + error);
        })
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
      productInput: '',
      // 对话框状态
      addStockVisible: false,
      updateStockVisible: false,
      // 表单数据
      addStockForm: {
        product: '',
        unitstock: 0,
        unitprice: 0
      },
      updateStockForm: {
        product: '',
        unitstock: 0,
        unitprice: 0
      },
      // 表单验证规则
      addStockFormRules: {
        product: [
          {required: true, message: '请输入产品名', trigger: 'blur'},
        ],
        unitstock: [
          {required: true, message: '请输入数量', trigger: 'blur'},
        ],
        unitprice: [
          {required: true, message: '请输入单价', trigger: 'blur'},
        ]
      },
      updateStockFormRules: {
        product: [
          {required: true, message: '请输入产品名', trigger: 'blur'},
        ],
        unitstock: [
          {required: true, message: '请输入数量', trigger: 'blur'},
        ],
        unitprice: [
          {required: true, message: '请输入单价', trigger: 'blur'},
        ]
      },
      // 联想功能参数
      suggestOptions: [],
      suggestTotal: 0,
      suggestCurrentPage: 1,
      suggestPageSize: 10,
      showSuggestions: false,
      // 隐藏零库存状态
      hideZeroStock: false
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
  margin: 0 auto;
  max-width: 1400px;
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
  text-align: left;
}

.stock-suggestion-item:hover {
  background-color: #f5f7fa;
  color: #667eea;
  transform: translateX(5px);
}

.stock-suggestion-item:last-child {
  border-bottom: none;
}

/* 联想分页 */
.stock-suggestion-pagination {
  padding: 12px;
  border-top: 1px solid #e4e7ed;
  background-color: #f9f9f9;
  border-radius: 0 0 8px 8px;
  text-align: center;
}

.stock-suggestion-pagination .el-pagination {
  margin-top: 0;
}

/* 表格样式 */
.el-table {
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
  animation: fadeInUp 0.5s ease-out 0.2s both;
}

.el-table th {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  font-weight: 600;
  color: #303133;
  padding: 14px 12px;
}

.el-table tr:hover {
  background-color: #f5f7fa;
  transition: all 0.3s ease;
}

.el-table--striped .el-table__row--striped {
  background-color: #fafbfc;
}

/* 分页样式 */
.el-pagination {
  margin-top: 30px;
  text-align: center;
  animation: fadeInUp 0.5s ease-out 0.3s both;
}

.el-pagination__item:hover {
  border-color: #667eea !important;
  color: #667eea !important;
}

.el-pagination__item.active {
  background-color: #667eea !important;
  border-color: #667eea !important;
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

.el-button--danger {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%) !important;
  border: none !important;
}

.el-button--success {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%) !important;
  border: none !important;
}

/* 对话框样式 */
.el-dialog {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2) !important;
}

.el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border-bottom: none !important;
  padding: 20px 24px !important;
}

.el-dialog__title {
  font-size: 18px !important;
  font-weight: 600 !important;
  color: white !important;
}

.el-dialog__close {
  color: white !important;
}

.el-dialog__body {
  padding: 30px !important;
  background-color: #ffffff !important;
}

/* 表单样式 */
.el-form {
  max-width: 600px;
  margin: 0 auto;
}

.el-form-item {
  margin-bottom: 24px;
}

.el-form-item__label {
  font-weight: 500;
  color: #606266;
  font-size: 14px;
}

.el-input {
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

.el-input:focus-within {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) !important;
  border-color: #667eea !important;
}

.el-select {
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

.el-select:focus-within {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) !important;
  border-color: #667eea !important;
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
    font-size: 22px;
    margin-bottom: 20px;
  }
  
  .search-row {
    padding: 15px;
  }
  
  .el-dialog__body {
    padding: 20px !important;
  }
  
  .el-form {
    max-width: 100%;
  }
  
  .suggestions-dropdown {
    max-height: 250px;
  }
  
  .el-table {
    font-size: 12px;
  }
  
  .el-table th,
  .el-table td {
    padding: 10px 8px !important;
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