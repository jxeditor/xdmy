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
        <el-table-column prop="operate_material" label="是否操作原材料" width="150px" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.operate_material === 1 ? 'success' : 'info'">
              {{ scope.row.operate_material === 1 ? '是' : '否' }}
            </el-tag>
          </template>
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
      <el-dialog title="开单" v-model="addShipmentVisible" width="80%" class="shipment-dialog">
        <el-form ref="addShipmentForm" :rules="addShipmentFormRules" :model="addShipmentForm" label-width="180px">
          <el-form-item label="单号:" prop="odd">
            <el-input v-model="addShipmentForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="客户:" prop="customer">
            <div class="shipment-search-container">
              <el-input v-model="addShipmentForm.customer" placeholder="请输入客户名称" @input="handleAddCustomerInput"></el-input>
              <!-- 客户联想结果下拉框 -->
              <div v-if="showAddCustomerSuggestions && addCustomerSuggestions.length > 0" class="shipment-suggestions-dropdown">
                <div 
                  v-for="(item, index) in addCustomerSuggestions" 
                  :key="index"
                  class="shipment-suggestion-item"
                  @click="selectAddCustomerSuggestion(item)"
                >
                  {{ item }}
                </div>
                <!-- 客户联想分页 -->
                <div v-if="addCustomerTotal > addCustomerPageSize" class="shipment-suggestion-pagination">
                  <el-pagination
                    small
                    layout="prev, pager, next, ->, total"
                    :total="addCustomerTotal"
                    :page-size="addCustomerPageSize"
                    :current-page="addCustomerCurrentPage"
                    @current-change="handleAddCustomerPageChange"
                  />
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <div class="shipment-search-container">
              <el-input v-model="addShipmentForm.product" placeholder="请输入产品名称" @input="handleAddProductInput"></el-input>
              <!-- 产品联想结果下拉框 -->
              <div v-if="showAddProductSuggestions && addProductSuggestions.length > 0" class="shipment-suggestions-dropdown">
                <div 
                  v-for="(item, index) in addProductSuggestions" 
                  :key="index"
                  class="shipment-suggestion-item"
                  @click="selectAddProductSuggestion(item)"
                >
                  {{ item }}
                </div>
                <!-- 产品联想分页 -->
                <div v-if="addProductTotal > addProductPageSize" class="shipment-suggestion-pagination">
                  <el-pagination
                    small
                    layout="prev, pager, next, ->, total"
                    :total="addProductTotal"
                    :page-size="addProductPageSize"
                    :current-page="addProductCurrentPage"
                    @current-change="handleAddProductPageChange"
                  />
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
          <el-form-item label="是否操作原材料:" prop="operate_material">
            <el-switch v-model="addShipmentForm.operate_material" :active-value="1" :inactive-value="0" @change="handleAddOperateMaterialChange"></el-switch>
          </el-form-item>
          <!-- 原材料关系展示 -->
          <el-form-item v-if="addShipmentForm.operate_material === 1" label="原材料关系:">
            <el-table :data="addMaterialRelations" style="width: 100%">
              <el-table-column prop="material_name" label="原材料名称"></el-table-column>
              <el-table-column prop="quantity" label="数量" width="150">
                <template #default="scope">
                  <el-input-number v-model="scope.row.quantity" :min="1" :step="1" style="width: 100%" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="scope">
                  <el-button type="danger" size="small" @click="removeAddMaterialRelation(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <!-- 添加原材料表单 -->
            <div v-if="addShipmentForm.operate_material === 1" style="margin-top: 15px; padding: 15px; border: 1px solid #e4e7ed; border-radius: 4px;">
              <h4 style="margin-bottom: 10px;">添加原材料</h4>
              <el-row :gutter="20">
                <el-col :span="10">
                  <el-input v-model="newAddMaterial.material_name" placeholder="原材料名称" style="width: 100%" />
                </el-col>
                <el-col :span="8">
                  <el-input-number v-model="newAddMaterial.quantity" :min="1" :step="1" placeholder="数量" style="width: 100%" />
                </el-col>
                <el-col :span="6">
                  <el-button type="primary" @click="addNewAddMaterial">添加</el-button>
                </el-col>
              </el-row>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onAddShipmentCommit(`addShipmentForm`)">确定</el-button>
            <el-button @click="onAddShipmentCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <el-dialog title="修改单据信息" v-model="updateShipmentVisible" width="80%" class="shipment-dialog">
        <el-form ref="updateShipmentForm" :rules="updateShipmentFormRules" :model="updateShipmentForm"
                 label-width="180px">
          <el-form-item label="单号:" prop="odd">
            <el-input v-model="updateShipmentForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="客户:" prop="customer">
            <div class="shipment-search-container">
              <el-input v-model="updateShipmentForm.customer" placeholder="请输入客户名称" @input="handleUpdateCustomerInput"></el-input>
              <!-- 客户联想结果下拉框 -->
              <div v-if="showUpdateCustomerSuggestions && updateCustomerSuggestions.length > 0" class="shipment-suggestions-dropdown">
                <div 
                  v-for="(item, index) in updateCustomerSuggestions" 
                  :key="index"
                  class="shipment-suggestion-item"
                  @click="selectUpdateCustomerSuggestion(item)"
                >
                  {{ item }}
                </div>
                <!-- 客户联想分页 -->
                <div v-if="updateCustomerTotal > updateCustomerPageSize" class="shipment-suggestion-pagination">
                  <el-pagination
                    small
                    layout="prev, pager, next, ->, total"
                    :total="updateCustomerTotal"
                    :page-size="updateCustomerPageSize"
                    :current-page="updateCustomerCurrentPage"
                    @current-change="handleUpdateCustomerPageChange"
                  />
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <div class="shipment-search-container">
              <el-input v-model="updateShipmentForm.product" placeholder="请输入产品名称" @input="handleUpdateProductInput"></el-input>
              <!-- 产品联想结果下拉框 -->
              <div v-if="showUpdateProductSuggestions && updateProductSuggestions.length > 0" class="shipment-suggestions-dropdown">
                <div 
                  v-for="(item, index) in updateProductSuggestions" 
                  :key="index"
                  class="shipment-suggestion-item"
                  @click="selectUpdateProductSuggestion(item)"
                >
                  {{ item }}
                </div>
                <!-- 产品联想分页 -->
                <div v-if="updateProductTotal > updateProductPageSize" class="shipment-suggestion-pagination">
                  <el-pagination
                    small
                    layout="prev, pager, next, ->, total"
                    :total="updateProductTotal"
                    :page-size="updateProductPageSize"
                    :current-page="updateProductCurrentPage"
                    @current-change="handleUpdateProductPageChange"
                  />
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
          <el-form-item label="是否操作原材料:" prop="operate_material">
            <el-switch v-model="updateShipmentForm.operate_material" :active-value="1" :inactive-value="0" @change="handleUpdateOperateMaterialChange"></el-switch>
          </el-form-item>
          <!-- 原材料关系展示 -->
          <el-form-item v-if="updateShipmentForm.operate_material === 1" label="原材料关系:">
            <el-table :data="updateMaterialRelations" style="width: 100%">
              <el-table-column prop="material_name" label="原材料名称"></el-table-column>
              <el-table-column prop="quantity" label="数量" width="150">
                <template #default="scope">
                  <el-input-number v-model="scope.row.quantity" :min="1" :step="1" style="width: 100%" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="scope">
                  <el-button type="danger" size="small" @click="removeUpdateMaterialRelation(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <!-- 添加原材料表单 -->
            <div v-if="updateShipmentForm.operate_material === 1" style="margin-top: 15px; padding: 15px; border: 1px solid #e4e7ed; border-radius: 4px;">
              <h4 style="margin-bottom: 10px;">添加原材料</h4>
              <el-row :gutter="20">
                <el-col :span="10">
                  <el-input v-model="newUpdateMaterial.material_name" placeholder="原材料名称" style="width: 100%" />
                </el-col>
                <el-col :span="8">
                  <el-input-number v-model="newUpdateMaterial.quantity" :min="1" :step="1" placeholder="数量" style="width: 100%" />
                </el-col>
                <el-col :span="6">
                  <el-button type="primary" @click="addNewUpdateMaterial">添加</el-button>
                </el-col>
              </el-row>
            </div>
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
        // 添加批量删除确认框
        this.$confirm(`确定要删除选中的 ${this.multipleSelection.length} 条出货记录吗？此操作不可撤销。`, '批量删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            // 用户确认删除
            this.multipleSelection.forEach((data) => {
              this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/shipment/deleteShipmentById?id=` + data.id)
                .catch(function (error) {
                  that.$message.error(error);
                })
            })
            this.sleep(500)
            that.getAllShipment()
            that.$message.success('批量删除成功');
          })
          .catch(() => {
            // 用户取消删除
            this.$message.info('已取消批量删除');
          });
      }
    },
    onClearSelection() {
      this.$refs.multipleTable.clearSelection()
    },
    onAddShipmentCancel() {
      this.addShipmentVisible = false
      // 重置操作原材料状态
      this.addShipmentForm.operate_material = 0
      // 重置原材料关系数据
      this.addMaterialRelations = []
      this.newAddMaterial = { materialName: '', quantity: 1 }
    },
    onUpdateShipmentCancel() {
      this.updateShipmentVisible = false
      // 重置操作原材料状态为关闭
      this.updateShipmentForm.operate_material = 0
      // 重置原材料关系数据
      this.updateMaterialRelations = []
      this.newUpdateMaterial = { materialName: '', quantity: 1 }
    },
    onAddShipment() {
      // 设置初始化标志位为true
      this.isInitializing = true
      // 重置添加表单
      this.addShipmentForm = {
        odd: ``,
        customer: ``,
        product: ``,
        billdate: ``,
        amount: 0,
        unitprice: 0,
        paystatus: `0`,
        boardcost: 0,
        fireproofboardcost: 0,
        remark: `无`,
        operate_material: 0
      }
      // 重置下拉框显示状态
      this.showAddCustomerSuggestions = false
      this.showAddProductSuggestions = false
      // 重置原材料关系数据
      this.addMaterialRelations = []
      this.newAddMaterial = { material_name: '', quantity: 1 }
      // 显示添加对话框
      this.addShipmentVisible = true
      // 延迟设置初始化标志位为false，确保所有watch监听器都执行完毕
      setTimeout(() => {
        this.isInitializing = false
      }, 500)
    },
    onUpdateShipment(Shipment) {
      console.log('修改出货单数据:', Shipment)
      // 设置初始化标志位为true
      this.isInitializing = true
      // 深拷贝Shipment对象，避免直接引用影响原始数据
      this.updateShipmentForm = JSON.parse(JSON.stringify(Shipment))
      // 存储初始的operate_material状态
      this.initialOperateMaterial = Shipment.operate_material
      // 重置下拉框显示状态
      this.showUpdateCustomerSuggestions = false
      this.showUpdateProductSuggestions = false
      // 如果操作原材料开关是打开的，获取历史操作记录
      console.log('operate_material值:', Shipment.operate_material, '类型:', typeof Shipment.operate_material)
      if (Shipment.operate_material == 1) {
        console.log('操作原材料开关是打开的，获取历史操作记录:', Shipment.id)
        this.getShipmentMaterialOperations(Shipment.id)
      } else {
        console.log('操作原材料开关是关闭的，清空原材料关系')
        this.updateMaterialRelations = []
      }
      // 重置新添加的原材料信息
      this.newUpdateMaterial = { material_name: '', quantity: 1 }
      // 显示修改对话框
      this.updateShipmentVisible = true
      // 延迟设置初始化标志位为false，确保所有watch监听器都执行完毕
      setTimeout(() => {
        this.isInitializing = false
      }, 500)
    },
    onAddShipmentCommit(addShipmentForm) {
      const that = this
      this.$refs[addShipmentForm].validate((valid) => {
        if (valid) {
          // 如果开启了操作原材料，校验库存
          if (that.addShipmentForm.operate_material === 1) {
            // 检查是否添加了原材料关系
            if (!that.addMaterialRelations || that.addMaterialRelations.length === 0) {
              that.$message({
                message: '开启操作原材料后，必须添加至少一条原材料关系',
                type: 'error',
                center: true
              })
              return false
            }
            that.validateMaterialStock(that.addMaterialRelations, that.addShipmentForm.amount)
              .then(() => {
                // 库存校验通过，继续开单
                that.doAddShipment()
              })
              .catch(errorMessage => {
                that.$message.error(errorMessage)
              })
          } else {
            // 未开启操作原材料，直接开单
            that.doAddShipment()
          }
        } else {
          return false
        }
      })
    },
    // 执行开单操作
    doAddShipment() {
      const that = this
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
      param.append(`operate_material`, this.addShipmentForm.operate_material)
      // 添加原材料关系数据
      if (this.addShipmentForm.operate_material === 1) {
        param.append(`materialRelations`, JSON.stringify(this.addMaterialRelations))
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/shipment/addShipment`, param).then(function (response) {
        if (response.data.code === 1) {
          // 开单成功，操作原材料库存
            if (that.addShipmentForm.operate_material === 1) {
                that.operateMaterialStock(that.addMaterialRelations, that.addShipmentForm.amount, 'increase')
                  .then(() => {
                    console.log('原材料库存操作成功')
                  })
                  .catch(errorMessage => {
                    console.error('原材料库存操作失败:', errorMessage)
                    // 库存操作失败不影响开单结果，只记录错误
                  })
            }
          that.addShipmentVisible = false
          that.getAllShipment()
        } else {
          that.$message.error(response.data.msg);
        }
      }).catch(function (error) {
        that.$message.error(error);
      })
    },
    onUpdateShipmentCommit(updateShipmentForm) {
      const that = this
      this.$refs[updateShipmentForm].validate((valid) => {
        if (valid) {
          // 如果开启了操作原材料，需要处理库存
          if (that.updateShipmentForm.operate_material === 1) {
            // 检查是否添加了原材料关系
            if (!that.updateMaterialRelations || that.updateMaterialRelations.length === 0) {
              that.$message({
                message: '开启操作原材料后，必须添加至少一条原材料关系',
                type: 'error',
                center: true
              })
              return false
            }
            // 1. 获取原有出货单信息，用于恢复库存
            that.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/shipment/findShipmentById?id=${that.updateShipmentForm.id}`)
              .then(oldShipmentResponse => {
                if (oldShipmentResponse.data.code === 1) {
                  const oldShipment = oldShipmentResponse.data.data
                  // 2. 校验新的原材料库存
                  return that.validateMaterialStock(that.updateMaterialRelations, that.updateShipmentForm.amount)
                    .then(() => {
                      return oldShipment
                    })
                } else {
                  throw new Error('获取原有出货单信息失败')
                }
              })
              .then(oldShipment => {
                // 3. 库存校验通过，执行修改操作
                return that.doUpdateShipment(oldShipment)
              })
              .catch(errorMessage => {
                that.$message.error(errorMessage.toString())
              })
          } else {
            // 未开启操作原材料，直接修改
            that.doUpdateShipment(null)
          }
        } else {
          return false
        }
      })
    },
    // 执行修改操作
    doUpdateShipment(oldShipment) {
      const that = this
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
      param.append(`operate_material`, this.updateShipmentForm.operate_material)
      // 添加原材料关系数据
      if (this.updateShipmentForm.operate_material === 1) {
        param.append(`materialRelations`, JSON.stringify(this.updateMaterialRelations))
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/shipment/updateShipment`, param).then(function (response) {
        if (response.data.code === 1) {
          // 修改成功，后端会自动处理原材料库存
          console.log('修改出货单成功')
          that.updateShipmentVisible = false
          that.getAllShipment()
        } else {
          that.$message.error(response.data.msg);
        }
      }).catch(function (error) {
        that.$message.error(error);
      })
    },
    onDeleteShipment(id) {
      const that = this;
      // 添加删除确认框
      this.$confirm('确定要删除这条出货记录吗？此操作不可撤销。', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // 用户确认删除
          this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/shipment/deleteShipmentById?id=` + id)
            .then(function (response) {
              if (response.data.code === 1) {
                that.getAllShipment()
                that.$message.success('删除成功');
              } else {
                that.$message.error(response.data.msg);
              }
            }).catch(function (error) {
            that.$message.error(error);
          })
        })
        .catch(() => {
          // 用户取消删除
          this.$message.info('已取消删除');
        });
    },
    getAllShipment() {
      try {
        const that = this;
        const url = `${process.env.VUE_APP_API_BASE_URL}/shipment/findAllShipment?pageNum=${that.page.index}&pageSize=${that.page.size}&customerName=${that.customerInput}&productName=${that.productInput}&bizStartDate=${that.billDateInput ? that.billDateInput[0] : ''}&bizEndDate=${that.billDateInput ? that.billDateInput[1] : ''}`;
        console.log('请求URL:', url);
        this.$axios.get(url)
          .then(function (response) {
            console.log('响应数据:', response.data);
            that.ShipmentData = response.data.data
            that.page.total = response.data.total
          }).catch(function (error) {
          console.error('请求错误:', error);
          that.$message.error('连接失败，请检查网络或服务状态');
        })
      } catch (error) {
        console.error('getAllShipment方法执行错误:', error);
      }
    },
    searchShipment() {
      try {
        const that = this
        const url = `${process.env.VUE_APP_API_BASE_URL}/shipment/findAllShipment?pageNum=${that.page.index}&pageSize=${that.page.size}&customerName=${that.customerInput}&productName=${that.productInput}&bizStartDate=${that.billDateInput ? that.billDateInput[0] : ''}&bizEndDate=${that.billDateInput ? that.billDateInput[1] : ''}`;
        console.log('搜索请求URL:', url);
        this.$axios.get(url)
          .then(function (response) {
            console.log('搜索响应数据:', response.data);
            that.ShipmentData = response.data.data
            that.page.total = response.data.total
          }).catch(function (error) {
          console.error('搜索请求错误:', error);
          that.$message.error('连接失败，请检查网络或服务状态');
        })
      } catch (error) {
        console.error('searchShipment方法执行错误:', error);
      }
    },
    // 获取客户联想建议
    getCustomerSuggestions() {
      console.log('获取客户联想建议开始')
      console.log('Customer input:', this.customerInput)
      console.log('Customer input length:', this.customerInput.length)
      
      const that = this
      if (that.customerInput.length < 1) {
        console.log('Customer input too short, returning')
        that.customerSuggestions = []
        that.showCustomerSuggestions = false
        return
      }
      
      console.log('Company name from localStorage:', localStorage.getItem('companyName'))
      
      const url = `${process.env.VUE_APP_API_BASE_URL}/shipment/findCustomerNamesByPrefix`
      console.log('Request URL:', url)
      
      const params = {
        prefix: that.customerInput,
        pageNum: that.customerCurrentPage,
        pageSize: that.customerPageSize
      }
      console.log('Request params:', params)
      
      try {
        console.log('Sending POST request for customer suggestions')
        this.$axios.post(url, params)
          .then(function (response) {
            console.log('Customer suggestions response:', response)
            console.log('Customer suggestions data:', response.data)
            that.customerSuggestions = response.data.data
            that.customerTotal = response.data.total
            that.showCustomerSuggestions = true
            console.log('Customer suggestions updated:', that.customerSuggestions)
          })
          .catch(function (error) {
            console.error('Customer suggestions error:', error)
            that.customerSuggestions = []
            that.showCustomerSuggestions = false
          })
      } catch (error) {
        console.error('Error in getCustomerSuggestions:', error)
      }
    },
    // 获取产品联想建议
    getProductSuggestions() {
      console.log('获取产品联想建议开始')
      console.log('Product input:', this.productInput)
      console.log('Product input length:', this.productInput.length)
      
      const that = this
      if (that.productInput.length < 1) {
        console.log('Product input too short, returning')
        that.productSuggestions = []
        that.showProductSuggestions = false
        return
      }
      
      console.log('Company name from localStorage:', localStorage.getItem('companyName'))
      
      const url = `${process.env.VUE_APP_API_BASE_URL}/product/findProductNamesByPrefix`
      console.log('Request URL:', url)
      
      const params = {
        prefix: that.productInput,
        pageNum: that.productCurrentPage,
        pageSize: that.productPageSize
      }
      console.log('Request params:', params)
      
      try {
        console.log('Sending POST request for product suggestions')
        this.$axios.post(url, params)
          .then(function (response) {
            console.log('Product suggestions response:', response)
            console.log('Product suggestions data:', response.data)
            that.productSuggestions = response.data.data
            that.productTotal = response.data.total
            that.showProductSuggestions = true
            console.log('Product suggestions updated:', that.productSuggestions)
          })
          .catch(function (error) {
            console.error('Product suggestions error:', error)
            that.productSuggestions = []
            that.showProductSuggestions = false
          })
      } catch (error) {
        console.error('Error in getProductSuggestions:', error)
      }
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
    // 处理修改对话框客户输入变化
    handleUpdateCustomerInput() {
      this.updateCustomerCurrentPage = 1
      this.getUpdateCustomerSuggestions()
    },
    // 处理修改对话框客户分页
    handleUpdateCustomerPageChange(pageNum) {
      this.updateCustomerCurrentPage = pageNum
      this.getUpdateCustomerSuggestions()
    },
    // 处理修改对话框产品输入变化
    handleUpdateProductInput() {
      this.updateProductCurrentPage = 1
      this.getUpdateProductSuggestions()
    },
    // 处理修改对话框产品分页
    handleUpdateProductPageChange(pageNum) {
      this.updateProductCurrentPage = pageNum
      this.getUpdateProductSuggestions()
    },
    // 处理添加对话框客户输入变化
    handleAddCustomerInput() {
      this.addCustomerCurrentPage = 1
      this.getAddCustomerSuggestions()
    },
    // 处理添加对话框客户分页
    handleAddCustomerPageChange(pageNum) {
      this.addCustomerCurrentPage = pageNum
      this.getAddCustomerSuggestions()
    },
    // 处理添加对话框产品输入变化
    handleAddProductInput() {
      this.addProductCurrentPage = 1
      this.getAddProductSuggestions()
    },
    // 处理添加对话框产品分页
    handleAddProductPageChange(pageNum) {
      this.addProductCurrentPage = pageNum
      this.getAddProductSuggestions()
    },
    // 获取添加客户联想建议
    getAddCustomerSuggestions() {
      // 如果正在初始化，不触发下拉框
      if (this.isInitializing) {
        return
      }
      const that = this
      if (that.addShipmentForm.customer.length < 1) {
        that.addCustomerSuggestions = []
        that.showAddCustomerSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/shipment/findCustomerNamesByPrefix`, {
        prefix: that.addShipmentForm.customer,
        pageNum: that.addCustomerCurrentPage,
        pageSize: that.addCustomerPageSize
      })
        .then(function (response) {
          that.addCustomerSuggestions = response.data.data
          that.addCustomerTotal = response.data.total
          that.showAddCustomerSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.addCustomerSuggestions = []
        that.addCustomerTotal = 0
        that.showAddCustomerSuggestions = false
      })
    },
    // 获取添加产品联想建议
    getAddProductSuggestions() {
      // 如果正在初始化，不触发下拉框
      if (this.isInitializing) {
        return
      }
      const that = this
      if (that.addShipmentForm.product.length < 1) {
        that.addProductSuggestions = []
        that.showAddProductSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/product/findProductNamesByPrefix`, {
        prefix: that.addShipmentForm.product,
        pageNum: that.addProductCurrentPage,
        pageSize: that.addProductPageSize
      })
        .then(function (response) {
          that.addProductSuggestions = response.data.data
          that.addProductTotal = response.data.total
          that.showAddProductSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.addProductSuggestions = []
        that.addProductTotal = 0
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
      // 如果正在初始化，不触发下拉框
      if (this.isInitializing) {
        return
      }
      const that = this
      if (that.updateShipmentForm.customer.length < 1) {
        that.updateCustomerSuggestions = []
        that.showUpdateCustomerSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/shipment/findCustomerNamesByPrefix`, {
        prefix: that.updateShipmentForm.customer,
        pageNum: that.updateCustomerCurrentPage,
        pageSize: that.updateCustomerPageSize
      })
        .then(function (response) {
          that.updateCustomerSuggestions = response.data.data
          that.updateCustomerTotal = response.data.total
          that.showUpdateCustomerSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.updateCustomerSuggestions = []
        that.updateCustomerTotal = 0
        that.showUpdateCustomerSuggestions = false
      })
    },
    // 获取修改产品联想建议
    getUpdateProductSuggestions() {
      // 如果正在初始化，不触发下拉框
      if (this.isInitializing) {
        return
      }
      const that = this
      if (that.updateShipmentForm.product.length < 1) {
        that.updateProductSuggestions = []
        that.showUpdateProductSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/product/findProductNamesByPrefix`, {
        prefix: that.updateShipmentForm.product,
        pageNum: that.updateProductCurrentPage,
        pageSize: that.updateProductPageSize
      })
        .then(function (response) {
          that.updateProductSuggestions = response.data.data
          that.updateProductTotal = response.data.total
          that.showUpdateProductSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.updateProductSuggestions = []
        that.updateProductTotal = 0
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
    },
    // 处理点击页面其他地方的事件
    handleClickOutside(event) {
      // 隐藏所有客户联想下拉框
      this.showCustomerSuggestions = false
      this.showAddCustomerSuggestions = false
      this.showUpdateCustomerSuggestions = false
      // 隐藏所有产品联想下拉框
      this.showProductSuggestions = false
      this.showAddProductSuggestions = false
      this.showUpdateProductSuggestions = false
    },
    // 获取产品的原材料关系
    getMaterialRelations(productName, type) {
      console.log('获取原材料关系:', { productName, type })
      if (!productName) {
        console.log('产品名称为空，清空原材料关系')
        if (type === 'add') {
          this.addMaterialRelations = []
        } else {
          this.updateMaterialRelations = []
        }
        return
      }
      const that = this
      const url = `${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/findRelationsByProductName?productName=${encodeURIComponent(productName)}`
      console.log('请求原材料关系URL:', url)
      this.$axios.get(url)
        .then(function (response) {
          console.log('获取原材料关系响应:', response.data)
          // 调整数据处理逻辑，适配后端返回的数据格式
          let relations = []
          if (response.data.data && Array.isArray(response.data.data)) {
            // 如果data直接是数组
            relations = response.data.data
          } else if (response.data.data && response.data.data.data && Array.isArray(response.data.data.data)) {
            // 如果data.data是数组
            relations = response.data.data.data
          }
          console.log('处理后的原材料关系:', relations)
          // 转换字段名，确保表格能正确显示
          const formattedRelations = relations.map(item => ({
            material_name: item.materialName || item.material_name,
            quantity: item.quantity
          }))
          console.log('格式化后的原材料关系:', formattedRelations)
          if (type === 'add') {
            that.addMaterialRelations = formattedRelations
            console.log('添加表单原材料关系:', that.addMaterialRelations)
          } else {
            that.updateMaterialRelations = formattedRelations
            console.log('修改表单原材料关系:', that.updateMaterialRelations)
          }
        }).catch(function (error) {
        console.error('获取原材料关系失败:', error)
        if (type === 'add') {
          that.addMaterialRelations = []
        } else {
          that.updateMaterialRelations = []
        }
      })
    },
    // 获取出货单的原材料操作记录
    getShipmentMaterialOperations(shipmentId) {
      const that = this
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/shipment/findMaterialOperationsByShipmentId?shipmentId=${shipmentId}`)
        .then(function (response) {
          console.log('获取原材料操作记录响应:', response.data)
          // 调整数据处理逻辑，适配后端返回的数据格式
          let operations = []
          if (response.data.data && Array.isArray(response.data.data)) {
            // 如果data直接是数组
            operations = response.data.data
          } else if (response.data.data && response.data.data.data && Array.isArray(response.data.data.data)) {
            // 如果data.data是数组
            operations = response.data.data.data
          }
          console.log('处理后的原材料操作记录:', operations)
          // 转换字段名，确保表格能正确显示
          const formattedOperations = operations.map(item => ({
            material_name: item.material_name || item.materialName,
            quantity: item.quantity
          }))
          console.log('格式化后的原材料操作记录:', formattedOperations)
          that.updateMaterialRelations = formattedOperations
        }).catch(function (error) {
        console.error('获取原材料操作记录失败:', error)
        that.updateMaterialRelations = []
      })
    },
    // 处理添加表单操作原材料开关变化
    handleAddOperateMaterialChange(val) {
      console.log('添加表单操作原材料开关变化:', val)
      if (val === 1) {
        console.log('添加表单开关开启，获取原材料关系:', this.addShipmentForm.product)
        this.getMaterialRelations(this.addShipmentForm.product, 'add')
      } else {
        console.log('添加表单开关关闭，清空原材料关系')
        this.addMaterialRelations = []
      }
    },
    // 处理修改表单操作原材料开关变化
    handleUpdateOperateMaterialChange(val) {
      console.log('修改表单操作原材料开关变化:', val)
      console.log('初始operate_material状态:', this.initialOperateMaterial)
      if (val === 1) {
        console.log('修改表单开关开启')
        // 第一种情况：如果本身未开启操作原材料，此时开启操作原材料
        if (this.initialOperateMaterial != 1) {
          console.log('从"未开启"状态切换到"开启"状态，从原材料关系中根据产品名称检索数据:', this.updateShipmentForm.product)
          this.getMaterialRelations(this.updateShipmentForm.product, 'update')
        } else {
          // 第二种情况：如果本身已经开启操作原材料，此时从原材料操作记录中根据id检索数据
          console.log('从"开启"状态切换到"开启"状态，从原材料操作记录中根据id检索数据:', this.updateShipmentForm.id)
          this.getShipmentMaterialOperations(this.updateShipmentForm.id)
        }
      } else {
        console.log('修改表单开关关闭，清空原材料关系')
        this.updateMaterialRelations = []
      }
    },
    // 处理添加表单产品名称变化
    handleAddProductChange() {
      console.log('添加表单产品名称变化:', this.addShipmentForm.product)
      // 获取产品联想建议
      this.getAddProductSuggestions()
      // 如果开启了操作原材料，重新获取原材料关系
      if (this.addShipmentForm.operate_material === 1) {
        console.log('添加表单操作原材料开关开启，重新获取原材料关系')
        this.getMaterialRelations(this.addShipmentForm.product, 'add')
      }
    },
    // 处理修改表单产品名称变化
    handleUpdateProductChange() {
      console.log('修改表单产品名称变化:', this.updateShipmentForm.product)
      // 获取产品联想建议
      this.getUpdateProductSuggestions()
      // 如果开启了操作原材料，重新获取原材料关系
      if (this.updateShipmentForm.operate_material === 1) {
        console.log('修改表单操作原材料开关开启，重新获取原材料关系')
        this.getMaterialRelations(this.updateShipmentForm.product, 'update')
      }
    },
    // 删除添加表单中的原材料关系
    removeAddMaterialRelation(index) {
      this.addMaterialRelations.splice(index, 1)
    },
    // 在添加表单中添加新的原材料
    addNewAddMaterial() {
      if (!this.newAddMaterial.material_name || this.newAddMaterial.quantity < 1) {
        this.$message.error('请填写原材料名称和数量')
        return
      }
      this.addMaterialRelations.push({
        material_name: this.newAddMaterial.material_name,
        quantity: this.newAddMaterial.quantity
      })
      // 清空表单
      this.newAddMaterial = {
        material_name: '',
        quantity: 1
      }
    },
    // 删除修改表单中的原材料关系
    removeUpdateMaterialRelation(index) {
      this.updateMaterialRelations.splice(index, 1)
    },
    // 在修改表单中添加新的原材料
    addNewUpdateMaterial() {
      if (!this.newUpdateMaterial.material_name || this.newUpdateMaterial.quantity < 1) {
        this.$message.error('请填写原材料名称和数量')
        return
      }
      this.updateMaterialRelations.push({
        material_name: this.newUpdateMaterial.material_name,
        quantity: this.newUpdateMaterial.quantity
      })
      // 清空表单
      this.newUpdateMaterial = {
        material_name: '',
        quantity: 1
      }
    },
    // 校验原材料库存
    validateMaterialStock(materialRelations, productAmount) {
      return new Promise((resolve, reject) => {
        if (!materialRelations || materialRelations.length === 0) {
          resolve(true)
          return
        }
        
        // 遍历所有原材料，检查是否存在于库存中
        const checkPromises = materialRelations.map(relation => {
          return new Promise((checkResolve) => {
            this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/stock/checkMaterialExist?materialName=${encodeURIComponent(relation.material_name)}`)
              .then(response => {
                if (response.data.code !== 1) {
                  checkResolve({ success: false, message: response.data.msg })
                } else {
                  checkResolve({ success: true })
                }
              })
              .catch(error => {
                console.error('检查原材料存在失败:', error)
                checkResolve({ success: false, message: `未找到原材料"${relation.material_name}"的库存信息` })
              })
          })
        })
        
        Promise.all(checkPromises)
          .then(results => {
            const failedResult = results.find(result => !result.success)
            if (failedResult) {
              reject(failedResult.message)
            } else {
              resolve(true)
            }
          })
      })
    },
    // 操作原材料库存
    operateMaterialStock(materialRelations, productAmount, operationType) {
      return new Promise((resolve, reject) => {
        if (!materialRelations || materialRelations.length === 0) {
          resolve(true)
          return
        }
        
        // 遍历所有原材料，操作库存
        const operatePromises = materialRelations.map(relation => {
          return new Promise((operateResolve) => {
            const stockChange = relation.quantity * productAmount
            this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/operateMaterialStock?materialName=${encodeURIComponent(relation.material_name)}&stockChange=${operationType === 'decrease' ? -stockChange : stockChange}`)
              .then(response => {
                if (response.data.code !== 1) {
                  operateResolve({ success: false, message: response.data.msg })
                } else {
                  operateResolve({ success: true })
                }
              })
              .catch(error => {
                console.error('操作原材料库存失败:', error)
                operateResolve({ success: false, message: `操作原材料"${relation.material_name}"库存失败` })
              })
          })
        })
        
        Promise.all(operatePromises)
          .then(results => {
            const failedResult = results.find(result => !result.success)
            if (failedResult) {
              reject(failedResult.message)
            } else {
              resolve(true)
            }
          })
      })
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
        remark: `无`,
        operate_material: 0
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
        remark: `无`,
        operate_material: 0
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
      addCustomerCurrentPage: 1,
      addCustomerPageSize: 10,
      addCustomerTotal: 0,
      // 添加产品联想功能参数
      addProductSuggestions: [],
      showAddProductSuggestions: false,
      addProductCurrentPage: 1,
      addProductPageSize: 10,
      addProductTotal: 0,
      // 修改对话框客户联想相关
      updateCustomerSuggestions: [],
      showUpdateCustomerSuggestions: false,
      updateCustomerCurrentPage: 1,
      updateCustomerPageSize: 10,
      updateCustomerTotal: 0,
      // 修改对话框产品联想相关
      updateProductSuggestions: [],
      showUpdateProductSuggestions: false,
      updateProductCurrentPage: 1,
      updateProductPageSize: 10,
      updateProductTotal: 0,
      // 原材料关系相关
      addMaterialRelations: [],
      updateMaterialRelations: [],
      // 新添加的原材料信息
      newAddMaterial: {
        material_name: '',
        quantity: 1
      },
      newUpdateMaterial: {
        material_name: '',
        quantity: 1
      },
      // 标志位，用于区分是用户手动输入还是初始化时的赋值
      isInitializing: false
    }
  },
  mounted() {
    // 添加点击事件监听器，点击页面其他地方时隐藏下拉框
    document.addEventListener('click', this.handleClickOutside)
    // 页面首次加载时获取数据
    console.log('Shipment页面mounted方法被调用');
    this.getAllShipment()
  },
  beforeDestroy() {
    if (this.timer) {
      clearTimeout(this.timer)
    }
    // 移除点击事件监听器
    document.removeEventListener('click', this.handleClickOutside)
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

/* 出货单对话框样式 */
.shipment-dialog {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2) !important;
  border: none !important;
}

.shipment-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border-bottom: none !important;
  padding: 20px 24px !important;
  margin: -20px -24px 0 !important;
  width: calc(100% + 48px) !important;
  box-sizing: border-box !important;
}

.shipment-dialog .el-dialog__title {
  font-size: 18px !important;
  font-weight: 600 !important;
  color: white !important;
  margin: 0 !important;
}

.shipment-dialog .el-dialog__headerbtn {
  top: 20px !important;
  right: 24px !important;
  width: 24px !important;
  height: 24px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  padding: 0 !important;
  margin: 0 !important;
}

.shipment-dialog .el-dialog__headerbtn .el-icon {
  width: 24px !important;
  height: 24px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  line-height: 24px !important;
  font-size: 16px !important;
}

.shipment-dialog .el-dialog__headerbtn .el-icon .el-dialog__close {
  color: white !important;
  width: 100% !important;
  height: 100% !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.shipment-dialog .el-dialog__headerbtn .el-icon .el-dialog__close svg {
  width: 16px !important;
  height: 16px !important;
  vertical-align: middle !important;
  margin: 0 !important;
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