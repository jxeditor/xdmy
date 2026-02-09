<template>
  <div id="incoming">
    <h1>{{ msg }}</h1>
    <div id="app">
      <!-- 第一行：时间筛选 -->
      <el-row style="width:100%;padding: 10px 20px;">
        <el-col :span="12">
          <el-date-picker v-model="billDateInput"
                          type="daterange"
                          range-separator="至"
                          start-placeholder="开始日期"
                          end-placeholder="结束日期"
                          value-format="YYYY-MM-DD"
                          style="width: 100%;"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="searchIncoming">搜索</el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="onAddIncoming">入货</el-button>
        </el-col>
      </el-row>
      
      <!-- 第二行：供应商和产品筛选 -->
      <el-row style="width:100%;padding: 0 20px 20px;">
        <el-col :span="8">
          <div class="search-container">
            <el-input
              v-model="producerInput"
              placeholder="输入供应商"
              clearable
              prefix-icon="el-icon-search"
              style="width: 100%;"
            />
            <!-- 供应商联想结果下拉框 -->
            <div v-if="showProducerSuggestions && producerSuggestions.length > 0" class="suggestions-dropdown">
              <div 
                v-for="(item, index) in producerSuggestions" 
                :key="index"
                class="suggestion-item"
                @click="selectProducerSuggestion(item)"
              >
                {{ item }}
              </div>
              <!-- 供应商联想分页 -->
              <div v-if="producerTotal > producerPageSize" class="suggestion-pagination">
                <el-pagination
                  small
                  layout="prev, pager, next, ->, total"
                  :total="producerTotal"
                  :page-size="producerPageSize"
                  :current-page="producerCurrentPage"
                  @current-change="handleProducerPageChange"
                  style="margin-top: 10px;"
                />
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="2"></el-col>
        <el-col :span="8">
          <div class="search-container">
            <el-input
              v-model="productInput"
              placeholder="输入产品名"
              clearable
              prefix-icon="el-icon-search"
              style="width: 100%;"
            />
            <!-- 产品联想结果下拉框 -->
            <div v-if="showProductSuggestions && productSuggestions.length > 0" class="suggestions-dropdown">
              <div 
                v-for="(item, index) in productSuggestions" 
                :key="index"
                class="suggestion-item"
                @click="selectProductSuggestion(item)"
              >
                {{ item }}
              </div>
              <!-- 产品联想分页 -->
              <div v-if="productTotal > productPageSize" class="suggestion-pagination">
                <el-pagination
                  small
                  layout="prev, pager, next, ->, total"
                  :total="productTotal"
                  :page-size="productPageSize"
                  :current-page="productCurrentPage"
                  @current-change="handleProductPageChange"
                  style="margin-top: 10px;"
                />
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-table ref="multipleTable" stripe :data="IncomingData" style="width: 100%;"
                @selection-change="handleSelectionChange">
        <el-table-column type="selection">
        </el-table-column>
        <el-table-column prop="id" label="UID" width="80px" align="center">
        </el-table-column>
        <el-table-column prop="odd" label="单号" width="120px" align="center">
        </el-table-column>
        <el-table-column prop="producer" label="供应商" width="150px" align="center">
        </el-table-column>
        <el-table-column prop="product" label="产品" width="200px" align="center" show-overflow-tooltip>
        </el-table-column>
        <el-table-column prop="billdate" label="日期" width="120px" align="center">
        </el-table-column>
        <el-table-column prop="amount" label="数量" width="80px" align="center">
        </el-table-column>
        <el-table-column prop="unitprice" label="单价" width="80px" align="center">
        </el-table-column>
        <el-table-column prop="money" label="金额" width="100px" align="center">
        </el-table-column>
        <el-table-column prop="paystatus" :formatter="setPayStatus" label="付款状态" width="100px" align="center">
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="100px" align="center" show-overflow-tooltip>
        </el-table-column>
        <el-table-column fixed="right" label="操作" align="center" width="180">
          <template #default="opt">
            <el-button type="primary" @click="onUpdateIncoming(opt.row)">修改</el-button>
            <el-button type="danger" @click="onDeleteIncoming(opt.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page.index"
        :page-size="page.size"
        layout="total,prev,pager,next"
        :total="page.total"
        @current-change="handleCurrentChange">
      </el-pagination>
      <div style="margin-top: 20px">
        <el-button @click="onBatchUpdateIncoming()">置为已付款</el-button>
        <el-button @click="onBatchDeleteIncoming()">删除</el-button>
        <el-button @click="onClearSelection()">取消选择</el-button>
      </div>
      <el-dialog title="入货" v-model="addIncomingVisible" width="80%">
        <el-form ref="addIncomingForm" :rules="addIncomingFormRules" :model="addIncomingForm" label-width="100px">
          <el-form-item label="单号:" prop="odd">
            <el-input v-model="addIncomingForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="供应商:" prop="producer">
            <el-input v-model="addIncomingForm.producer"></el-input>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <el-input v-model="addIncomingForm.product"></el-input>
          </el-form-item>
          <el-form-item label="日期:" prop="billdate">
            <el-date-picker
              type="date"
              placeholder="选择日期"
              v-model="addIncomingForm.billdate"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="数量:" prop="amount">
            <el-input v-model="addIncomingForm.amount"></el-input>
          </el-form-item>
          <el-form-item label="单价:" prop="unitprice">
            <el-input v-model="addIncomingForm.unitprice"></el-input>
          </el-form-item>
          <el-form-item label="付款状态:" prop="paystatus">
            <el-select v-model="addIncomingForm.paystatus" placeholder="请选择付款状态">
              <el-option label="未付款" value="0"></el-option>
              <el-option label="已付运费" value="1"></el-option>
              <el-option label="已付款" value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="备注:" prop="remark">
            <el-input v-model="addIncomingForm.remark"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onAddIncomingCommit(`addIncomingForm`)">确定</el-button>
            <el-button @click="onAddIncomingCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <el-dialog title="修改单据信息" v-model="updateIncomingVisible" width="80%">
        <el-form ref="updateIncomingForm" :rules="updateIncomingFormRules" :model="updateIncomingForm"
                 label-width="100px">
          <el-form-item label="单号:" prop="odd">
            <el-input v-model="updateIncomingForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="供应商:" prop="producer">
            <el-input v-model="updateIncomingForm.producer"></el-input>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <el-input v-model="updateIncomingForm.product"></el-input>
          </el-form-item>
          <el-form-item label="日期:" prop="billdate">
            <el-date-picker
              type="date"
              placeholder="选择日期"
              v-model="updateIncomingForm.billdate"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="数量:" prop="amount">
            <el-input v-model="updateIncomingForm.amount"></el-input>
          </el-form-item>
          <el-form-item label="单价:" prop="unitprice">
            <el-input v-model="updateIncomingForm.unitprice"></el-input>
          </el-form-item>
          <el-form-item label="付款状态:" prop="paystatus">
            <el-select v-model="updateIncomingForm.paystatus" placeholder="请选择付款状态">
              <el-option label="未付款" value="0"></el-option>
              <el-option label="已付运费" value="1"></el-option>
              <el-option label="已付款" value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="备注:" prop="remark">
            <el-input v-model="updateIncomingForm.remark"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onUpdateIncomingCommit(`updateIncomingForm`)">确定</el-button>
            <el-button @click="onUpdateIncomingCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </div>
  </div>
</template>


<script>
export default {
  name: `IncomingScript`,
  props: {
    msg: String
  },
  mounted() {
    this.getAllIncoming()
  },
  methods: {
    sleep(ms) { //sleep延迟方法2
      const time_ms = new Date().getTime();
      while (new Date().getTime() < time_ms + ms) {
      }
    },
    setPayStatus(row) {
      switch (row.paystatus) {
        case "0":
          return "未付款";
        case "1":
          return "已付运费";
        case "2":
          return "已付款"
        default:
          return "未定义";
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    onBatchUpdateIncoming() {
      if (this.multipleSelection.length !== 0) {
        const that = this;
        this.multipleSelection.forEach((data) => {
          this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/incoming/updatePaystatusIncomingById?id=` + data.id)
            .catch(function (error) {
              that.$message.error(error);
            })
        })
        this.sleep(500)
        that.getAllIncoming()
      }
    },
    onBatchDeleteIncoming() {
      if (this.multipleSelection.length !== 0) {
        const that = this;
        this.multipleSelection.forEach((data) => {
          this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/incoming/deleteIncomingById?id=` + data.id)
            .catch(function (error) {
              that.$message.error(error);
            })
        })
        this.sleep(500)
        that.getAllIncoming()
      }
    },
    onClearSelection() {
      this.$refs.multipleTable.clearSelection()
    },
    handleCurrentChange(currentPage) {
      this.page.index = currentPage;
      this.getAllIncoming()
    },
    onAddIncomingCancel() {
      this.addIncomingVisible = false
    },
    onUpdateIncomingCancel() {
      this.updateIncomingVisible = false
    },
    onAddIncoming() {
      this.addIncomingVisible = true
    },
    onUpdateIncoming(incoming) {
      this.updateIncomingForm = incoming
      this.updateIncomingVisible = true
    },
    onAddIncomingCommit(addIncomingForm) {
      const that = this
      this.$refs[addIncomingForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append(`odd`, this.addIncomingForm.odd)
          param.append(`producer`, this.addIncomingForm.producer)
          param.append(`product`, this.addIncomingForm.product)
          param.append(`billdate`, this.addIncomingForm.billdate)
          param.append(`amount`, this.addIncomingForm.amount)
          param.append(`unitprice`, this.addIncomingForm.unitprice)
          param.append(`paystatus`, this.addIncomingForm.paystatus)
          param.append(`remark`, this.addIncomingForm.remark)
          this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/incoming/addIncoming`, param).then(function (response) {
            if (response.data.code === 1) {
              that.addIncomingVisible = false
              that.getAllIncoming()
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
    onUpdateIncomingCommit(updateIncomingForm) {
      const that = this
      this.$refs[updateIncomingForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append(`id`, this.updateIncomingForm.id)
          param.append(`odd`, this.updateIncomingForm.odd)
          param.append(`producer`, this.updateIncomingForm.producer)
          param.append(`product`, this.updateIncomingForm.product)
          param.append(`billdate`, this.updateIncomingForm.billdate)
          param.append(`amount`, this.updateIncomingForm.amount)
          param.append(`unitprice`, this.updateIncomingForm.unitprice)
          param.append(`paystatus`, this.updateIncomingForm.paystatus)
          param.append(`remark`, this.updateIncomingForm.remark)
          this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/incoming/updateIncoming`, param).then(function (response) {
            if (response.data.code === 1) {
              that.updateIncomingVisible = false
              that.getAllIncoming()
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
    onDeleteIncoming(id) {
      const that = this;
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/incoming/deleteIncomingById?id=` + id)
        .then(function (response) {
          if (response.data.code === 1) {
            that.getAllIncoming()
          } else {
            that.$message.error(response.data.msg);
          }
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    getAllIncoming() {
      const that = this;
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/incoming/findAllIncoming` +
        `?pageNum=` + that.page.index + `&pageSize=` + that.page.size +
        `&producerName=` + that.producerInput +
        `&productName=` + that.productInput +
        `&bizStartDate=` + (that.billDateInput ? that.billDateInput[0] : ``) + `&bizEndDate=` + (that.billDateInput ? that.billDateInput[1] : ``))
        .then(function (response) {
          that.IncomingData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    searchIncoming() {
      const that = this
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/incoming/findAllIncoming` +
        `?pageNum=` + that.page.index + `&pageSize=` + that.page.size +
        `&producerName=` + that.producerInput +
        `&productName=` + that.productInput +
        `&bizStartDate=` + (that.billDateInput ? that.billDateInput[0] : ``) + `&bizEndDate=` + (that.billDateInput ? that.billDateInput[1] : ``))
        .then(function (response) {
          that.IncomingData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    // 获取供应商联想建议
    getProducerSuggestions() {
      const that = this
      if (that.producerInput.length < 1) {
        that.producerSuggestions = []
        that.showProducerSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/incoming/findProducerNamesByPrefix`, {
        prefix: that.producerInput,
        pageNum: that.producerCurrentPage,
        pageSize: that.producerPageSize
      })
        .then(function (response) {
          that.producerSuggestions = response.data.data
          that.producerTotal = response.data.total
          that.showProducerSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.producerSuggestions = []
        that.showProducerSuggestions = false
      })
    },
    // 获取产品联想建议
    getProductSuggestions() {
      const that = this
      if (that.productInput.length < 1) {
        that.productSuggestions = []
        that.showProductSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/findProductNamesByPrefix`, {
        prefix: that.productInput,
        pageNum: that.productCurrentPage,
        pageSize: that.productPageSize
      })
        .then(function (response) {
          that.productSuggestions = response.data.data
          that.productTotal = response.data.total
          that.showProductSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.productSuggestions = []
        that.showProductSuggestions = false
      })
    },
    // 选择供应商联想建议
    selectProducerSuggestion(item) {
      this.producerInput = item
      this.showProducerSuggestions = false
    },
    // 选择产品联想建议
    selectProductSuggestion(item) {
      this.productInput = item
      this.showProductSuggestions = false
    },
    // 处理供应商分页
    handleProducerPageChange(pageNum) {
      this.producerCurrentPage = pageNum
      this.getProducerSuggestions()
    },
    // 处理产品分页
    handleProductPageChange(pageNum) {
      this.productCurrentPage = pageNum
      this.getProductSuggestions()
    },
  },
  watch: {
    producerInput() {
      this.producerCurrentPage = 1
      this.getProducerSuggestions()
    },
    productInput() {
      this.productCurrentPage = 1
      this.getProductSuggestions()
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
      multipleSelection: [],
      IncomingData: [],
      producerInput: ``,
      productInput: ``,
      billDateInput: ``,
      addIncomingVisible: false,
      updateIncomingVisible: false,
      // 供应商联想相关数据
      producerSuggestions: [],
      showProducerSuggestions: false,
      producerCurrentPage: 1,
      producerPageSize: 10,
      producerTotal: 0,
      // 产品联想相关数据
      productSuggestions: [],
      showProductSuggestions: false,
      productCurrentPage: 1,
      productPageSize: 10,
      productTotal: 0,
      addIncomingForm: {
        odd: ``,
        producer: ``,
        product: ``,
        billdate: ``,
        amount: 0,
        unitprice: 0,
        paystatus: `0`,
        remark: `无`
      },
      updateIncomingForm: {
        odd: ``,
        producer: ``,
        product: ``,
        billdate: ``,
        amount: 0,
        unitprice: 0,
        paystatus: `0`,
        remark: `无`
      },
      addIncomingFormRules: {
        odd: [
          {required: true, message: `请输入单号`, trigger: `blur`}
        ],
        producer: [
          {required: true, message: `请输入供应商`, trigger: `blur`},
        ],
        product: [
          {required: true, message: `请输入产品名`, trigger: `blur`},
        ],
        billdate: [
          {
            type: `date`, required: true, message: `请选择日期`, trigger: `change`
          }
        ],
        amount: [
          {required: true, message: `请输入数量`, trigger: `blur`},
        ],
        unitprice: [
          {required: true, message: `请输入单价`, trigger: `blur`},
        ],
        paystatus: [
          {required: true, message: `请选择付款状态`, trigger: `blur`},
        ]
      },
      updateIncomingFormRules: {
        odd: [
          {required: true, message: `请输入单号`, trigger: `blur`}
        ],
        producer: [
          {required: true, message: `请输入供应商`, trigger: `blur`},
        ],
        product: [
          {required: true, message: `请输入产品名`, trigger: `blur`},
        ],
        billdate: [
          {
            type: `date`, required: true, message: `请选择日期`, trigger: `change`
          }
        ],
        amount: [
          {required: true, message: `请输入数量`, trigger: `blur`},
        ],
        unitprice: [
          {required: true, message: `请输入单价`, trigger: `blur`},
        ],
        paystatus: [
          {required: true, message: `请选择付款状态`, trigger: `blur`},
        ]
      }
    }
  }
}
</script>

<style>
#incoming {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

h3 {
  margin: 40px 0 0;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}

/* 搜索联想下拉框样式 */
.search-container {
  position: relative;
  width: 100%;
}

.suggestions-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 1000;
  max-height: 300px;
  overflow-y: auto;
  margin-top: 4px;
}

.suggestion-item {
  padding: 10px 15px;
  cursor: pointer;
  text-align: left;
  transition: background-color 0.3s;
}

.suggestion-item:hover {
  background-color: #f5f7fa;
}

.suggestion-pagination {
  padding: 10px;
  border-top: 1px solid #dcdfe6;
  text-align: center;
}

/* 表格样式优化 */
.el-table {
  margin: 0 20px;
  border-radius: 4px;
  overflow: hidden;
}

.el-pagination {
  margin: 20px;
  text-align: center;
}

/* 按钮样式优化 */
.el-button {
  margin: 0 5px;
}

/* 表单样式优化 */
.el-dialog {
  border-radius: 4px;
}

.el-form-item {
  margin-bottom: 15px;
}

/* 响应式布局调整 */
@media screen and (max-width: 1200px) {
  .el-row {
    flex-wrap: wrap;
  }
  
  .el-col {
    margin-bottom: 10px;
  }
}
</style>

