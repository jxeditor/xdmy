<template>
  <div id="shipment">
    <h1>{{ msg }}</h1>
    <div id="app">
      <!-- 第一行：时间筛选 -->
      <el-row type="flex" justify="space-between" align="center" style="width:100%;padding: 10px 20px;">
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
        <el-col :span="8" style="display: flex; justify-content: flex-end; gap: 10px;">
          <el-button type="primary" @click="searchShipment">搜索</el-button>
          <el-button type="primary" @click="onAddShipment">开单</el-button>
        </el-col>
      </el-row>
      
      <!-- 第二行：客户和产品筛选 -->
      <el-row style="width:100%;padding: 0 20px 20px;">
        <el-col :span="8">
          <div class="shipment-search-container">
            <el-input
              v-model="customerInput"
              placeholder="输入客户名"
              clearable
              prefix-icon="el-icon-search"
              style="width: 100%;"
            />
            <!-- 客户联想结果下拉框 -->
            <div v-if="showCustomerSuggestions && customerSuggestions.length > 0" class="shipment-suggestions-dropdown">
              <div 
                v-for="(item, index) in customerSuggestions" 
                :key="index"
                class="shipment-suggestion-item"
                @click="selectCustomerSuggestion(item)"
              >
                {{ item }}
              </div>
              <!-- 客户联想分页 -->
              <div v-if="customerTotal > customerPageSize" class="shipment-suggestion-pagination">
                <el-pagination
                  small
                  layout="prev, pager, next, ->, total"
                  :total="customerTotal"
                  :page-size="customerPageSize"
                  :current-page="customerCurrentPage"
                  @current-change="handleCustomerPageChange"
                  style="margin-top: 10px;"
                />
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="2"></el-col>
        <el-col :span="8">
          <div class="shipment-search-container">
            <el-input
              v-model="productInput"
              placeholder="输入产品名"
              clearable
              prefix-icon="el-icon-search"
              style="width: 100%;"
            />
            <!-- 产品联想结果下拉框 -->
            <div v-if="showProductSuggestions && productSuggestions.length > 0" class="shipment-suggestions-dropdown">
              <div 
                v-for="(item, index) in productSuggestions" 
                :key="index"
                class="shipment-suggestion-item"
                @click="selectProductSuggestion(item)"
              >
                {{ item }}
              </div>
              <!-- 产品联想分页 -->
              <div v-if="productTotal > productPageSize" class="shipment-suggestion-pagination">
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
      <el-table ref="multipleTable" stripe :data="ShipmentData" style="width: 100%;"
                @selection-change="handleSelectionChange">
        <el-table-column type="selection">
        </el-table-column>
        <el-table-column prop="id" label="UID" width="80px" align="center">
        </el-table-column>
        <el-table-column prop="odd" label="单号" width="120px" align="center">
        </el-table-column>
        <el-table-column prop="customer" label="客户" width="150px" align="center">
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
        <el-table-column prop="boardcost" label="夹板成本" width="100px" align="center">
        </el-table-column>
        <el-table-column prop="fireproofboardcost" label="防火板成本" width="120px" align="center">
        </el-table-column>
        <el-table-column prop="costmoney" label="成本" width="100px" align="center">
        </el-table-column>
        <el-table-column prop="profit" label="利润" width="100px" align="center">
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="100px" align="center" show-overflow-tooltip>
        </el-table-column>
        <el-table-column fixed="right" label="操作" align="center" width="180">
          <template #default="opt">
            <el-button type="primary" @click="onUpdateShipment(opt.row)">修改</el-button>
            <el-button type="danger" @click="onDeleteShipment(opt.row.id)">删除</el-button>
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
        <el-button @click="onBatchUpdateShipment()">置为已付款</el-button>
        <el-button @click="onBatchDeleteShipment()">删除</el-button>
        <el-button @click="onClearSelection()">取消选择</el-button>
      </div>
      <el-dialog title="开单" v-model="addShipmentVisible" width="80%">
        <el-form ref="addShipmentForm" :rules="addShipmentFormRules" :model="addShipmentForm" label-width="100px">
          <el-form-item label="单号:" prop="odd">
            <el-input v-model="addShipmentForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="客户:" prop="customer">
            <div class="search-container">
              <el-input v-model="addShipmentForm.customer" placeholder="请输入客户名称"></el-input>
              <!-- 客户联想结果下拉框 -->
              <div v-if="showAddCustomerSuggestions && addCustomerSuggestions.length > 0" class="suggestions-dropdown">
                <div 
                  v-for="(item, index) in addCustomerSuggestions" 
                  :key="index"
                  class="suggestion-item"
                  @click="selectAddCustomerSuggestion(item)"
                >
                  {{ item }}
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <div class="search-container">
              <el-input v-model="addShipmentForm.product" placeholder="请输入产品名称"></el-input>
              <!-- 产品联想结果下拉框 -->
              <div v-if="showAddProductSuggestions && addProductSuggestions.length > 0" class="suggestions-dropdown">
                <div 
                  v-for="(item, index) in addProductSuggestions" 
                  :key="index"
                  class="suggestion-item"
                  @click="selectAddProductSuggestion(item)"
                >
                  {{ item }}
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="日期:" prop="billdate">
            <el-date-picker
              type="date"
              placeholder="选择日期"
              v-model="addShipmentForm.billdate"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="数量:" prop="amount">
            <el-input v-model="addShipmentForm.amount"></el-input>
          </el-form-item>
          <el-form-item label="单价:" prop="unitprice">
            <el-input v-model="addShipmentForm.unitprice"></el-input>
          </el-form-item>
          <el-form-item label="付款状态:" prop="paystatus">
            <el-select v-model="addShipmentForm.paystatus" placeholder="请选择付款状态">
              <el-option label="未付款" value="0"></el-option>
              <el-option label="已付款" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="夹板成本:" prop="boardcost">
            <el-input v-model="addShipmentForm.boardcost"></el-input>
          </el-form-item>
          <el-form-item label="防火板成本:" prop="fireproofboardcost">
            <el-input v-model="addShipmentForm.fireproofboardcost"></el-input>
          </el-form-item>
          <el-form-item label="备注:" prop="remark">
            <el-input v-model="addShipmentForm.remark"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onAddShipmentCommit(`addShipmentForm`)">确定</el-button>
            <el-button @click="onAddShipmentCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <el-dialog title="修改单据信息" v-model="updateShipmentVisible" width="80%">
        <el-form ref="updateShipmentForm" :rules="updateShipmentFormRules" :model="updateShipmentForm"
                 label-width="100px">
          <el-form-item label="单号:" prop="odd">
            <el-input v-model="updateShipmentForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="客户:" prop="customer">
            <div class="search-container">
              <el-input v-model="updateShipmentForm.customer" placeholder="请输入客户名称"></el-input>
              <!-- 客户联想结果下拉框 -->
              <div v-if="showUpdateCustomerSuggestions && updateCustomerSuggestions.length > 0" class="suggestions-dropdown">
                <div 
                  v-for="(item, index) in updateCustomerSuggestions" 
                  :key="index"
                  class="suggestion-item"
                  @click="selectUpdateCustomerSuggestion(item)"
                >
                  {{ item }}
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <div class="search-container">
              <el-input v-model="updateShipmentForm.product" placeholder="请输入产品名称"></el-input>
              <!-- 产品联想结果下拉框 -->
              <div v-if="showUpdateProductSuggestions && updateProductSuggestions.length > 0" class="suggestions-dropdown">
                <div 
                  v-for="(item, index) in updateProductSuggestions" 
                  :key="index"
                  class="suggestion-item"
                  @click="selectUpdateProductSuggestion(item)"
                >
                  {{ item }}
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="日期:" prop="billdate">
            <el-date-picker
              type="date"
              placeholder="选择日期"
              v-model="updateShipmentForm.billdate"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="数量:" prop="amount">
            <el-input v-model="updateShipmentForm.amount"></el-input>
          </el-form-item>
          <el-form-item label="单价:" prop="unitprice">
            <el-input v-model="updateShipmentForm.unitprice"></el-input>
          </el-form-item>
          <el-form-item label="付款状态:" prop="paystatus">
            <el-select v-model="updateShipmentForm.paystatus" placeholder="请选择付款状态">
              <el-option label="未付款" value="0"></el-option>
              <el-option label="已付款" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="夹板成本:" prop="boardcost">
            <el-input v-model="updateShipmentForm.boardcost"></el-input>
          </el-form-item>
          <el-form-item label="防火板成本:" prop="fireproofboardcost">
            <el-input v-model="updateShipmentForm.fireproofboardcost"></el-input>
          </el-form-item>
          <el-form-item label="备注:" prop="remark">
            <el-input v-model="updateShipmentForm.remark"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onUpdateShipmentCommit(`updateShipmentForm`)">确定</el-button>
            <el-button @click="onUpdateShipmentCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </div>
  </div>
</template>

<script>
export default {
  name: `ShipmentScript`,
  props: {
    msg: String
  },
  mounted() {
    this.getAllShipment()
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
          return "已付款";
        default:
          return "未定义";
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleCurrentChange(currentPage) {
      this.page.index = currentPage;
      this.getAllShipment()
    },
    onBatchUpdateShipment() {
      if (this.multipleSelection.length !== 0) {
        const that = this;
        this.multipleSelection.forEach((data) => {
          this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/shipment/updatePaystatusShipmentById?id=` + data.id)
            .catch(function (error) {
              that.$message.error(error);
            })
        })
        this.sleep(500)
        that.getAllShipment()
      }
    },
    onBatchDeleteShipment() {
      if (this.multipleSelection.length !== 0) {
        const that = this;
        this.multipleSelection.forEach((data) => {
          this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/shipment/deleteShipmentById?id=` + data.id)
            .catch(function (error) {
              that.$message.error(error);
            })
        })
        this.sleep(500)
        that.getAllShipment()
      }
    },
    onClearSelection() {
      this.$refs.multipleTable.clearSelection()
    },
    onAddShipmentCancel() {
      this.addShipmentVisible = false
    },
    onUpdateShipmentCancel() {
      this.updateShipmentVisible = false
    },
    onAddShipment() {
      this.addShipmentVisible = true
    },
    onUpdateShipment(Shipment) {
      this.updateShipmentForm = Shipment
      this.updateShipmentVisible = true
    },
    onAddShipmentCommit(addShipmentForm) {
      const that = this
      this.$refs[addShipmentForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append(`odd`, this.addShipmentForm.odd)
          param.append(`customer`, this.addShipmentForm.customer)
          param.append(`product`, this.addShipmentForm.product)
          param.append(`billdate`, this.addShipmentForm.billdate)
          param.append(`amount`, this.addShipmentForm.amount)
          param.append(`unitprice`, this.addShipmentForm.unitprice)
          param.append(`paystatus`, this.addShipmentForm.paystatus)
          param.append(`boardcost`, this.addShipmentForm.boardcost)
          param.append(`fireproofboardcost`, this.addShipmentForm.fireproofboardcost)
          param.append(`remark`, this.addShipmentForm.remark)
          this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/shipment/addShipment`, param).then(function (response) {
            if (response.data.code === 1) {
              that.addShipmentVisible = false
              that.getAllShipment()
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
    onUpdateShipmentCommit(updateShipmentForm) {
      const that = this
      this.$refs[updateShipmentForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append(`id`, this.updateShipmentForm.id)
          param.append(`odd`, this.updateShipmentForm.odd)
          param.append(`customer`, this.updateShipmentForm.customer)
          param.append(`product`, this.updateShipmentForm.product)
          param.append(`billdate`, this.updateShipmentForm.billdate)
          param.append(`amount`, this.updateShipmentForm.amount)
          param.append(`unitprice`, this.updateShipmentForm.unitprice)
          param.append(`paystatus`, this.updateShipmentForm.paystatus)
          param.append(`boardcost`, this.updateShipmentForm.boardcost)
          param.append(`fireproofboardcost`, this.updateShipmentForm.fireproofboardcost)
          param.append(`remark`, this.updateShipmentForm.remark)
          this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/shipment/updateShipment`, param).then(function (response) {
            if (response.data.code === 1) {
              that.updateShipmentVisible = false
              that.getAllShipment()
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
    onDeleteShipment(id) {
      const that = this;
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/shipment/deleteShipmentById?id=` + id)
        .then(function (response) {
          if (response.data.code === 1) {
            that.getAllShipment()
          } else {
            that.$message.error(response.data.msg);
          }
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    getAllShipment() {
      const that = this;
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/shipment/findAllShipment` +
        `?pageNum=` + that.page.index + `&pageSize=` + that.page.size +
        `&customerName=` + that.customerInput +
        `&productName=` + that.productInput +
        `&bizStartDate=` + (that.billDateInput ? that.billDateInput[0] : ``) + `&bizEndDate=` + (that.billDateInput ? that.billDateInput[1] : ``))
        .then(function (response) {
          that.ShipmentData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    searchShipment() {
      const that = this
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/shipment/findAllShipment` +
        `?pageNum=` + that.page.index + `&pageSize=` + that.page.size +
        `&customerName=` + that.customerInput +
        `&productName=` + that.productInput +
        `&bizStartDate=` + (that.billDateInput ? that.billDateInput[0] : ``) + `&bizEndDate=` + (that.billDateInput ? that.billDateInput[1] : ``))
        .then(function (response) {
          that.ShipmentData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    // 获取客户联想建议
    getCustomerSuggestions() {
      const that = this
      if (that.customerInput.length < 1) {
        that.customerSuggestions = []
        that.showCustomerSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/shipment/findCustomerNamesByPrefix`, {
        prefix: that.customerInput,
        pageNum: that.customerCurrentPage,
        pageSize: that.customerPageSize
      })
        .then(function (response) {
          that.customerSuggestions = response.data.data
          that.customerTotal = response.data.total
          that.showCustomerSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.customerSuggestions = []
        that.showCustomerSuggestions = false
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
    // 选择客户联想建议
    selectCustomerSuggestion(item) {
      this.customerInput = item
      this.showCustomerSuggestions = false
    },
    // 选择产品联想建议
    selectProductSuggestion(item) {
      this.productInput = item
      this.showProductSuggestions = false
    },
    // 处理客户分页
    handleCustomerPageChange(pageNum) {
      this.customerCurrentPage = pageNum
      this.getCustomerSuggestions()
    },
    // 处理产品分页
    handleProductPageChange(pageNum) {
      this.productCurrentPage = pageNum
      this.getProductSuggestions()
    },
    // 获取添加客户联想建议
    getAddCustomerSuggestions() {
      const that = this
      if (that.addShipmentForm.customer.length < 1) {
        that.addCustomerSuggestions = []
        that.showAddCustomerSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/shipment/findCustomerNamesByPrefix`, {
        prefix: that.addShipmentForm.customer,
        pageNum: 1,
        pageSize: 10
      })
        .then(function (response) {
          that.addCustomerSuggestions = response.data.data
          that.showAddCustomerSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.addCustomerSuggestions = []
        that.showAddCustomerSuggestions = false
      })
    },
    // 获取添加产品联想建议
    getAddProductSuggestions() {
      const that = this
      if (that.addShipmentForm.product.length < 1) {
        that.addProductSuggestions = []
        that.showAddProductSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/findProductNamesByPrefix`, {
        prefix: that.addShipmentForm.product,
        pageNum: 1,
        pageSize: 10
      })
        .then(function (response) {
          that.addProductSuggestions = response.data.data
          that.showAddProductSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.addProductSuggestions = []
        that.showAddProductSuggestions = false
      })
    },
    // 选择添加客户联想建议
    selectAddCustomerSuggestion(item) {
      this.addShipmentForm.customer = item
      this.showAddCustomerSuggestions = false
    },
    // 选择添加产品联想建议
    selectAddProductSuggestion(item) {
      this.addShipmentForm.product = item
      this.showAddProductSuggestions = false
    },
    // 获取修改客户联想建议
    getUpdateCustomerSuggestions() {
      const that = this
      if (that.updateShipmentForm.customer.length < 1) {
        that.updateCustomerSuggestions = []
        that.showUpdateCustomerSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/shipment/findCustomerNamesByPrefix`, {
        prefix: that.updateShipmentForm.customer,
        pageNum: 1,
        pageSize: 10
      })
        .then(function (response) {
          that.updateCustomerSuggestions = response.data.data
          that.showUpdateCustomerSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.updateCustomerSuggestions = []
        that.showUpdateCustomerSuggestions = false
      })
    },
    // 获取修改产品联想建议
    getUpdateProductSuggestions() {
      const that = this
      if (that.updateShipmentForm.product.length < 1) {
        that.updateProductSuggestions = []
        that.showUpdateProductSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/findProductNamesByPrefix`, {
        prefix: that.updateShipmentForm.product,
        pageNum: 1,
        pageSize: 10
      })
        .then(function (response) {
          that.updateProductSuggestions = response.data.data
          that.showUpdateProductSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.updateProductSuggestions = []
        that.showUpdateProductSuggestions = false
      })
    },
    // 选择修改客户联想建议
    selectUpdateCustomerSuggestion(item) {
      this.updateShipmentForm.customer = item
      this.showUpdateCustomerSuggestions = false
    },
    // 选择修改产品联想建议
    selectUpdateProductSuggestion(item) {
      this.updateShipmentForm.product = item
      this.showUpdateProductSuggestions = false
    }
  },
  watch: {
    // 监听客户输入变化，获取联想建议
    customerInput: {
      handler(val) {
        this.customerCurrentPage = 1
        this.getCustomerSuggestions()
      },
      debounce: 300
    },
    // 监听产品输入变化，获取联想建议
    productInput: {
      handler(val) {
        this.productCurrentPage = 1
        this.getProductSuggestions()
      },
      debounce: 300
    },
    // 监听添加客户输入变化，获取联想建议
    "addShipmentForm.customer": {
      handler(val) {
        this.getAddCustomerSuggestions()
      },
      debounce: 300
    },
    // 监听添加产品输入变化，获取联想建议
    "addShipmentForm.product": {
      handler(val) {
        this.getAddProductSuggestions()
      },
      debounce: 300
    },
    // 监听修改客户输入变化，获取联想建议
    "updateShipmentForm.customer": {
      handler(val) {
        this.getUpdateCustomerSuggestions()
      },
      debounce: 300
    },
    // 监听修改产品输入变化，获取联想建议
    "updateShipmentForm.product": {
      handler(val) {
        this.getUpdateProductSuggestions()
      },
      debounce: 300
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
      ShipmentData: [],
      customerInput: ``,
      productInput: ``,
      billDateInput: ``,
      addShipmentVisible: false,
      updateShipmentVisible: false,
      addShipmentForm: {
        odd: ``,
        customer: ``,
        product: ``,
        billdate: ``,
        amount: 0,
        unitprice: 0,
        paystatus: `0`,
        boardcost: 0,
        fireproofboardcost: 0,
        remark: `无`
      },
      updateShipmentForm: {
        odd: ``,
        customer: ``,
        product: ``,
        billdate: ``,
        amount: 0,
        unitprice: 0,
        paystatus: `0`,
        boardcost: 0,
        fireproofboardcost: 0,
        remark: `无`
      },
      addShipmentFormRules: {
        odd: [
          {required: true, message: `请输入单号`, trigger: `blur`}
        ],
        customer: [
          {required: true, message: `请输入客户名`, trigger: `blur`},
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
        ],
        boardcost: [
          {required: true, message: `请输入夹板成本`, trigger: `blur`},
        ],
        fireproofboardcost: [
          {required: true, message: `请输入防火板成本`, trigger: `blur`},
        ]
      },
      updateShipmentFormRules: {
        odd: [
          {required: true, message: `请输入单号`, trigger: `blur`}
        ],
        customer: [
          {required: true, message: `请输入客户名`, trigger: `blur`},
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
        ],
        boardcost: [
          {required: true, message: `请输入夹板成本`, trigger: `blur`},
        ],
        fireproofboardcost: [
          {required: true, message: `请输入防火板成本`, trigger: `blur`},
        ]
      },
      // 客户联想功能参数
      customerSuggestions: [],
      customerTotal: 0,
      customerCurrentPage: 1,
      customerPageSize: 10,
      showCustomerSuggestions: false,
      // 产品联想功能参数
      productSuggestions: [],
      productTotal: 0,
      productCurrentPage: 1,
      productPageSize: 10,
      showProductSuggestions: false,
      // 添加客户联想功能参数
      addCustomerSuggestions: [],
      showAddCustomerSuggestions: false,
      // 添加产品联想功能参数
      addProductSuggestions: [],
      showAddProductSuggestions: false,
      // 修改客户联想功能参数
      updateCustomerSuggestions: [],
      showUpdateCustomerSuggestions: false,
      // 修改产品联想功能参数
      updateProductSuggestions: [],
      showUpdateProductSuggestions: false
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
#shipment {
  min-height: 100vh;
  padding: 20px;
}

#shipment h1 {
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
.shipment-search-container {
  position: relative;
  width: 100%;
}

/* 联想结果下拉框 */
.shipment-suggestions-dropdown {
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
.shipment-suggestion-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
  overflow: hidden;
}

.shipment-suggestion-item:hover {
  background-color: #f5f7fa;
  color: #667eea;
  transform: translateX(5px);
}

.shipment-suggestion-item:last-child {
  border-bottom: none;
}

/* 联想分页 */
.shipment-suggestion-pagination {
  padding: 12px;
  border-top: 1px solid #e4e7ed;
  background-color: #f9f9f9;
  border-radius: 0 0 8px 8px;
}

.shipment-suggestion-pagination .el-pagination {
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

.el-date-picker {
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

.el-select {
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

.el-select:focus-within {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) !important;
  border-color: #667eea !important;
}

/* 批量操作按钮容器 */
.batch-actions {
  margin-top: 20px;
  display: flex;
  gap: 12px;
  justify-content: center;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  animation: fadeInUp 0.5s ease-out 0.4s both;
}

/* 响应式设计 */
@media (max-width: 768px) {
  #shipment {
    padding: 10px;
  }
  
  #app {
    padding: 15px;
  }
  
  #shipment h1 {
    font-size: 22px;
    margin-bottom: 20px;
  }
  
  .el-dialog__body {
    padding: 20px !important;
  }
  
  .batch-actions {
    flex-direction: column;
    align-items: center;
    gap: 8px;
  }
  
  .el-table {
    font-size: 12px;
  }
  
  .el-table th,
  .el-table td {
    padding: 10px 8px !important;
  }
  
  .el-form {
    max-width: 100%;
  }
  
  .suggestions-dropdown {
    max-height: 250px;
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