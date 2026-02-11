<template>
  <div id="incoming">
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
          <el-button type="primary" @click="searchIncoming">搜索</el-button>
          <el-button type="primary" @click="onAddIncoming">入货</el-button>
        </el-col>
      </el-row>
      
      <!-- 第二行：供应商和产品筛选 -->
      <el-row style="width:100%;padding: 0 20px 20px;">
        <el-col :span="8">
          <div class="incoming-search-container">
            <el-input
              v-model="producerInput"
              placeholder="输入供应商"
              clearable
              prefix-icon="el-icon-search"
              style="width: 100%;"
            />
            <!-- 供应商联想结果下拉框 -->
            <div v-if="showProducerSuggestions && producerSuggestions.length > 0" class="incoming-suggestions-dropdown">
              <div 
                v-for="(item, index) in producerSuggestions" 
                :key="index"
                class="incoming-suggestion-item"
                @click="selectProducerSuggestion(item)"
              >
                {{ item }}
              </div>
              <!-- 供应商联想分页 -->
              <div v-if="producerTotal > producerPageSize" class="incoming-suggestion-pagination">
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
          <div class="incoming-search-container">
            <el-input
              v-model="productInput"
              placeholder="输入产品名"
              clearable
              prefix-icon="el-icon-search"
              style="width: 100%;"
            />
            <!-- 产品联想结果下拉框 -->
            <div v-if="showProductSuggestions && productSuggestions.length > 0" class="incoming-suggestions-dropdown">
              <div 
                v-for="(item, index) in productSuggestions" 
                :key="index"
                class="incoming-suggestion-item"
                @click="selectProductSuggestion(item)"
              >
                {{ item }}
              </div>
              <!-- 产品联想分页 -->
              <div v-if="productTotal > productPageSize" class="incoming-suggestion-pagination">
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
        <el-table-column prop="operate_material" label="是否操作原材料" width="150px" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.operate_material === 1 ? 'success' : 'info'">
              {{ scope.row.operate_material === 1 ? '是' : '否' }}
            </el-tag>
          </template>
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
      <el-dialog title="入货" v-model="addIncomingVisible" width="80%" class="incoming-dialog">
        <el-form ref="addIncomingForm" :rules="addIncomingFormRules" :model="addIncomingForm" label-width="100px">
          <el-form-item label="单号:" prop="odd">
            <el-input v-model="addIncomingForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="供应商:" prop="producer">
            <div class="incoming-search-container">
              <el-input v-model="addIncomingForm.producer" placeholder="请输入供应商名称" @input="handleAddProducerInput"></el-input>
              <!-- 供应商联想结果下拉框 -->
              <div v-if="showAddProducerSuggestions && addProducerSuggestions.length > 0" class="incoming-suggestions-dropdown">
                <div 
                  v-for="(item, index) in addProducerSuggestions" 
                  :key="index"
                  class="incoming-suggestion-item"
                  @click="selectAddProducerSuggestion(item)"
                >
                  {{ item }}
                </div>
                <!-- 供应商联想分页 -->
                <div v-if="addProducerTotal > addProducerPageSize" class="incoming-suggestion-pagination">
                  <el-pagination
                    small
                    layout="prev, pager, next, ->, total"
                    :total="addProducerTotal"
                    :page-size="addProducerPageSize"
                    :current-page="addProducerCurrentPage"
                    @current-change="handleAddProducerPageChange"
                  />
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <div class="incoming-search-container">
              <el-input v-model="addIncomingForm.product" placeholder="请输入产品名称" @input="handleAddProductInput"></el-input>
              <!-- 产品联想结果下拉框 -->
              <div v-if="showAddProductSuggestions && addProductSuggestions.length > 0" class="incoming-suggestions-dropdown">
                <div 
                  v-for="(item, index) in addProductSuggestions" 
                  :key="index"
                  class="incoming-suggestion-item"
                  @click="selectAddProductSuggestion(item)"
                >
                  {{ item }}
                </div>
                <!-- 产品联想分页 -->
                <div v-if="addProductTotal > addProductPageSize" class="incoming-suggestion-pagination">
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
          <el-form-item label="是否操作原材料:" prop="operate_material" label-width="120px">
            <el-switch v-model="addIncomingForm.operate_material" :active-value="1" :inactive-value="0" @change="handleAddOperateMaterialChange"></el-switch>
          </el-form-item>
          <!-- 原材料关系展示 -->
          <el-form-item v-if="addIncomingForm.operate_material === 1" label="原材料关系:">
            <el-table :data="addMaterialRelations" style="width: 100%">
              <el-table-column prop="materialName" label="原材料名称"></el-table-column>
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
            <div v-if="addIncomingForm.operate_material === 1" style="margin-top: 15px; padding: 15px; border: 1px solid #e4e7ed; border-radius: 4px;">
              <h4 style="margin-bottom: 10px;">添加原材料</h4>
              <el-row :gutter="20">
                <el-col :span="10">
                  <el-input v-model="newAddMaterial.materialName" placeholder="原材料名称" style="width: 100%" />
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
            <el-button type="primary" @click="onAddIncomingCommit(`addIncomingForm`)">确定</el-button>
            <el-button @click="onAddIncomingCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <el-dialog title="修改单据信息" v-model="updateIncomingVisible" width="80%" class="incoming-dialog">
        <el-form ref="updateIncomingForm" :rules="updateIncomingFormRules" :model="updateIncomingForm"
                 label-width="100px">
          <el-form-item label="单号:" prop="odd">
            <el-input v-model="updateIncomingForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="供应商:" prop="producer">
            <div class="incoming-search-container">
              <el-input v-model="updateIncomingForm.producer" placeholder="请输入供应商名称" @input="handleUpdateProducerInput"></el-input>
              <!-- 供应商联想结果下拉框 -->
              <div v-if="showUpdateProducerSuggestions && updateProducerSuggestions.length > 0" class="incoming-suggestions-dropdown">
                <div 
                  v-for="(item, index) in updateProducerSuggestions" 
                  :key="index"
                  class="incoming-suggestion-item"
                  @click="selectUpdateProducerSuggestion(item)"
                >
                  {{ item }}
                </div>
                <!-- 供应商联想分页 -->
                <div v-if="updateProducerTotal > updateProducerPageSize" class="incoming-suggestion-pagination">
                  <el-pagination
                    small
                    layout="prev, pager, next, ->, total"
                    :total="updateProducerTotal"
                    :page-size="updateProducerPageSize"
                    :current-page="updateProducerCurrentPage"
                    @current-change="handleUpdateProducerPageChange"
                  />
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <div class="incoming-search-container">
              <el-input v-model="updateIncomingForm.product" placeholder="请输入产品名称" @input="handleUpdateProductInput"></el-input>
              <!-- 产品联想结果下拉框 -->
              <div v-if="showUpdateProductSuggestions && updateProductSuggestions.length > 0" class="incoming-suggestions-dropdown">
                <div 
                  v-for="(item, index) in updateProductSuggestions" 
                  :key="index"
                  class="incoming-suggestion-item"
                  @click="selectUpdateProductSuggestion(item)"
                >
                  {{ item }}
                </div>
                <!-- 产品联想分页 -->
                <div v-if="updateProductTotal > updateProductPageSize" class="incoming-suggestion-pagination">
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
          <el-form-item label="是否操作原材料:" prop="operate_material" label-width="120px">
            <el-switch v-model="updateIncomingForm.operate_material" :active-value="1" :inactive-value="0" @change="handleUpdateOperateMaterialChange"></el-switch>
          </el-form-item>
          <!-- 原材料关系展示 -->
          <el-form-item v-if="updateIncomingForm.operate_material === 1" label="原材料关系:">
            <el-table :data="updateMaterialRelations" style="width: 100%">
              <el-table-column prop="materialName" label="原材料名称"></el-table-column>
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
            <div v-if="updateIncomingForm.operate_material === 1" style="margin-top: 15px; padding: 15px; border: 1px solid #e4e7ed; border-radius: 4px;">
              <h4 style="margin-bottom: 10px;">添加原材料</h4>
              <el-row :gutter="20">
                <el-col :span="10">
                  <el-input v-model="newUpdateMaterial.materialName" placeholder="原材料名称" style="width: 100%" />
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
      // 重置操作原材料状态
      this.addIncomingForm.operate_material = 0
      // 重置原材料关系数据
      this.addMaterialRelations = []
      this.newAddMaterial = { materialName: '', quantity: 1 }
      // 重置产品联想相关数据
      this.addProductSuggestions = []
      this.showAddProductSuggestions = false
      // 重置供应商联想相关数据
      this.addProducerSuggestions = []
      this.showAddProducerSuggestions = false
    },
    onUpdateIncomingCancel() {
      this.updateIncomingVisible = false
      // 重置操作原材料状态为关闭
      this.updateIncomingForm.operate_material = 0
      // 重置原材料关系数据
      this.updateMaterialRelations = []
      this.newUpdateMaterial = { materialName: '', quantity: 1 }
      // 重置产品联想相关数据
      this.updateProductSuggestions = []
      this.showUpdateProductSuggestions = false
      // 重置供应商联想相关数据
      this.updateProducerSuggestions = []
      this.showUpdateProducerSuggestions = false
    },
    onAddIncoming() {
      this.addIncomingVisible = true
      // 重置原材料关系数据
      this.addMaterialRelations = []
      this.newAddMaterial = { material_name: '', quantity: 1 }
    },
    onUpdateIncoming(incoming) {
      console.log('修改入货单数据:', incoming)
      // 深拷贝incoming对象，避免直接引用影响原始数据
      this.updateIncomingForm = JSON.parse(JSON.stringify(incoming))
      // 存储初始的operate_material状态
      this.initialOperateMaterial = incoming.operate_material
      this.updateIncomingVisible = true
      // 重置原材料关系数据
      this.updateMaterialRelations = []
      this.newUpdateMaterial = { materialName: '', quantity: 1 }
      // 如果操作原材料开关是打开的，获取历史操作记录
      console.log('operate_material值:', incoming.operate_material, '类型:', typeof incoming.operate_material)
      if (incoming.operate_material == 1) {
        console.log('操作原材料开关是打开的，获取历史操作记录:', incoming.id)
        this.getIncomingMaterialOperations(incoming.id)
      } else {
        console.log('操作原材料开关是关闭的，清空原材料关系')
        this.updateMaterialRelations = []
      }
    },
    // 处理添加入货单时操作原材料开关变化
    handleAddOperateMaterialChange(val) {
      console.log('添加入货单操作原材料开关变化:', val)
      if (val === 1) {
        console.log('添加入货单开关开启，获取原材料关系:', this.addIncomingForm.product)
        this.getMaterialRelations(this.addIncomingForm.product, 'add')
      } else {
        console.log('添加入货单开关关闭，清空原材料关系')
        this.addMaterialRelations = []
      }
    },
    // 处理修改入货单时操作原材料开关变化
    handleUpdateOperateMaterialChange(val) {
      console.log('修改入货单操作原材料开关变化:', val)
      if (val === 1) {
        console.log('修改入货单开关开启，根据当前产品名获取原材料关系:', this.updateIncomingForm.product)
        this.getMaterialRelations(this.updateIncomingForm.product, 'update')
      } else {
        console.log('修改入货单开关关闭，清空原材料关系')
        this.updateMaterialRelations = []
      }
    },
    // 处理产品名变化
    handleProductChange(type) {
      console.log('产品名变化，类型:', type)
      if (type === 'add') {
        console.log('添加入货单产品名变化:', this.addIncomingForm.product)
        // 获取产品联想建议
        this.getAddProductSuggestions()
        // 如果开启了操作原材料，重新获取原材料关系
        if (this.addIncomingForm.operate_material === 1) {
          console.log('添加入货单操作原材料开关开启，重新获取原材料关系')
          this.getMaterialRelations(this.addIncomingForm.product, 'add')
        }
      } else if (type === 'update') {
        console.log('修改入货单产品名变化:', this.updateIncomingForm.product)
        // 获取产品联想建议
        this.getUpdateProductSuggestions()
        // 如果开启了操作原材料，重新获取原材料关系
        if (this.updateIncomingForm.operate_material === 1) {
          console.log('修改入货单操作原材料开关开启，重新获取原材料关系')
          this.getMaterialRelations(this.updateIncomingForm.product, 'update')
        }
      }
    },
    // 添加入货单时添加新的原材料
    addNewAddMaterial() {
      if (this.newAddMaterial.materialName && this.newAddMaterial.quantity > 0) {
        this.addMaterialRelations.push({
          materialName: this.newAddMaterial.materialName,
          quantity: this.newAddMaterial.quantity
        })
        this.newAddMaterial = { materialName: '', quantity: 1 }
      } else {
        this.$message.error('请输入原材料名称和数量')
      }
    },
    // 添加入货单时删除原材料
    removeAddMaterialRelation(index) {
      this.addMaterialRelations.splice(index, 1)
    },
    // 修改入货单时添加新的原材料
    addNewUpdateMaterial() {
      if (this.newUpdateMaterial.materialName && this.newUpdateMaterial.quantity > 0) {
        this.updateMaterialRelations.push({
          materialName: this.newUpdateMaterial.materialName,
          quantity: this.newUpdateMaterial.quantity
        })
        this.newUpdateMaterial = { materialName: '', quantity: 1 }
      } else {
        this.$message.error('请输入原材料名称和数量')
      }
    },
    // 修改入货单时删除原材料
    removeUpdateMaterialRelation(index) {
      this.updateMaterialRelations.splice(index, 1)
    },
    // 根据产品名称获取原材料关系
    getMaterialRelations(productName, type) {
      if (!productName) {
        return
      }
      const that = this
      // 使用环境变量中的API基础URL
      console.log('获取原材料关系API URL:', `${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/findRelationsByProductName?productName=${productName}`)
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/findRelationsByProductName?productName=${productName}`)
        .then(function (response) {
          console.log('获取原材料关系响应:', response)
          if (response.data.code === 1) {
            const relations = response.data.data
            console.log('获取到的原材料关系:', relations)
            if (type === 'add') {
              that.addMaterialRelations = relations
              console.log('添加入货单原材料关系更新为:', that.addMaterialRelations)
            } else if (type === 'update') {
              that.updateMaterialRelations = relations
              console.log('修改入货单原材料关系更新为:', that.updateMaterialRelations)
            }
          } else {
            console.log('获取原材料关系失败，响应码:', response.data.code)
            // 检索不到原材料关系时，保持列表为空，不弹出错误提示
            if (type === 'add') {
              that.addMaterialRelations = []
            } else if (type === 'update') {
              that.updateMaterialRelations = []
            }
          }
        }).catch(function (error) {
        console.error('获取原材料关系失败:', error)
        // 网络错误时也不弹出错误提示，保持列表为空
        if (type === 'add') {
          that.addMaterialRelations = []
        } else if (type === 'update') {
          that.updateMaterialRelations = []
        }
      })
    },
    // 根据入货单ID获取原材料操作记录
    getIncomingMaterialOperations(incomingId) {
      const that = this
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/incoming/getIncomingMaterialOperations?id=${incomingId}`)
        .then(function (response) {
          if (response.data.code === 1) {
            that.updateMaterialRelations = response.data.data
          } else {
            that.$message.error(response.data.msg)
          }
        }).catch(function (error) {
        console.error('获取原材料操作记录失败:', error)
        that.$message.error('获取原材料操作记录失败')
      })
    },
    // 校验原材料库存
    validateMaterialStock(materialRelations, amount) {
      return new Promise((resolve, reject) => {
        if (!materialRelations || materialRelations.length === 0) {
          resolve()
          return
        }
        
        const validationPromises = materialRelations.map(relation => {
          return new Promise((innerResolve, innerReject) => {
            // 检查materialName是否存在
            if (!relation.materialName || relation.materialName.trim() === '') {
              innerReject('原材料名称不能为空')
              return
            }
            this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/stock/checkMaterialExist?materialName=${relation.materialName}`)
              .then(response => {
                if (response.data.code === 1) {
                  innerResolve()
                } else {
                  innerReject(`原材料 ${relation.materialName} 不存在`)
                }
              })
              .catch(error => {
                innerReject(`校验原材料 ${relation.materialName} 库存失败: ${error}`)
              })
          })
        })
        
        Promise.all(validationPromises)
          .then(() => {
            resolve()
          })
          .catch(errorMessage => {
            reject(errorMessage)
          })
      })
    },
    // 操作原材料库存
    operateMaterialStock(materialRelations, amount, operationType) {
      return new Promise((resolve, reject) => {
        if (!materialRelations || materialRelations.length === 0) {
          resolve()
          return
        }
        
        const operationPromises = materialRelations.map(relation => {
          return new Promise((innerResolve, innerReject) => {
            // 检查materialName是否存在
            if (!relation.materialName || relation.materialName.trim() === '') {
              innerReject('原材料名称不能为空')
              return
            }
            const totalQuantity = relation.quantity * amount
            const stockChange = operationType === 'increase' ? totalQuantity : -totalQuantity
            
            this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/operateMaterialStock`, {
              materialName: relation.materialName,
              stockChange: stockChange
            })
              .then(response => {
                if (response.data.code === 1) {
                  innerResolve()
                } else {
                  innerReject(`操作原材料 ${relation.materialName} 库存失败: ${response.data.msg}`)
                }
              })
              .catch(error => {
                innerReject(`操作原材料 ${relation.materialName} 库存失败: ${error}`)
              })
          })
        })
        
        Promise.all(operationPromises)
          .then(() => {
            resolve()
          })
          .catch(errorMessage => {
            reject(errorMessage)
          })
      })
    },
    onAddIncomingCommit(addIncomingForm) {
      const that = this
      this.$refs[addIncomingForm].validate((valid) => {
        if (valid) {
          // 如果开启了操作原材料，校验库存
          if (that.addIncomingForm.operate_material === 1) {
            // 检查是否添加了原材料关系
            if (!that.addMaterialRelations || that.addMaterialRelations.length === 0) {
              that.$message({
                message: '开启操作原材料后，必须添加至少一条原材料关系',
                type: 'error',
                center: true
              })
              return false
            }
            that.validateMaterialStock(that.addMaterialRelations, that.addIncomingForm.amount)
              .then(() => {
                // 库存校验通过，继续入货
                that.doAddIncoming()
              })
              .catch(errorMessage => {
                that.$message.error(errorMessage)
              })
          } else {
            // 未开启操作原材料，直接入货
            that.doAddIncoming()
          }
        } else {
          return false
        }
      })
    },
    // 执行添加入货单操作
    doAddIncoming() {
      const that = this
      let param = new URLSearchParams()
      param.append(`odd`, this.addIncomingForm.odd)
      param.append(`producer`, this.addIncomingForm.producer)
      param.append(`product`, this.addIncomingForm.product)
      param.append(`billdate`, this.addIncomingForm.billdate)
      param.append(`amount`, this.addIncomingForm.amount)
      param.append(`unitprice`, this.addIncomingForm.unitprice)
      param.append(`paystatus`, this.addIncomingForm.paystatus)
      param.append(`remark`, this.addIncomingForm.remark)
      param.append(`operate_material`, this.addIncomingForm.operate_material)
      // 添加原材料关系数据
      if (this.addIncomingForm.operate_material === 1) {
        param.append(`materialRelations`, JSON.stringify(this.addMaterialRelations))
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/incoming/addIncoming`, param).then(function (response) {
        if (response.data.code === 1) {
          // 入货成功，操作原材料库存
            if (that.addIncomingForm.operate_material === 1) {
                that.operateMaterialStock(that.addMaterialRelations, that.addIncomingForm.amount, 'increase')
                  .then(() => {
                    console.log('原材料库存操作成功')
                  })
                  .catch(errorMessage => {
                    console.error('原材料库存操作失败:', errorMessage)
                    // 库存操作失败不影响入货结果，只记录错误
                  })
            }
          that.addIncomingVisible = false
          that.getAllIncoming()
        } else {
          that.$message.error(response.data.msg);
        }
      }).catch(function (error) {
        that.$message.error(error);
      })
    },
    onUpdateIncomingCommit(updateIncomingForm) {
      const that = this
      this.$refs[updateIncomingForm].validate((valid) => {
        if (valid) {
          // 如果开启了操作原材料，需要处理库存
          if (that.updateIncomingForm.operate_material === 1) {
            // 检查是否添加了原材料关系
            if (!that.updateMaterialRelations || that.updateMaterialRelations.length === 0) {
              that.$message({
                message: '开启操作原材料后，必须添加至少一条原材料关系',
                type: 'error',
                center: true
              })
              return false
            }
            // 1. 获取原有入货单信息，用于恢复库存
            that.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/incoming/findIncomingById?id=${that.updateIncomingForm.id}`)
              .then(oldIncomingResponse => {
                if (oldIncomingResponse.data.code === 1) {
                  const oldIncoming = oldIncomingResponse.data.data
                  // 2. 校验新的原材料库存
                  return that.validateMaterialStock(that.updateMaterialRelations, that.updateIncomingForm.amount)
                    .then(() => {
                      return oldIncoming
                    })
                } else {
                  throw new Error('获取原有入货单信息失败')
                }
              })
              .then(oldIncoming => {
                // 3. 库存校验通过，执行修改操作
                return that.doUpdateIncoming(oldIncoming)
              })
              .catch(errorMessage => {
                that.$message.error(errorMessage.toString())
              })
          } else {
            // 未开启操作原材料，直接修改
            that.doUpdateIncoming(null)
          }
        } else {
          return false
        }
      })
    },
    // 执行修改入货单操作
    doUpdateIncoming(oldIncoming) {
      const that = this
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
      param.append(`operate_material`, this.updateIncomingForm.operate_material)
      // 添加原材料关系数据
      if (this.updateIncomingForm.operate_material === 1) {
        param.append(`materialRelations`, JSON.stringify(this.updateMaterialRelations))
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/incoming/updateIncoming`, param).then(function (response) {
        if (response.data.code === 1) {
          // 修改成功，后端会自动处理原材料库存
          console.log('修改入货单成功')
          that.updateIncomingVisible = false
          that.getAllIncoming()
        } else {
          that.$message.error(response.data.msg);
        }
      }).catch(function (error) {
        that.$message.error(error);
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
      const url = `${process.env.VUE_APP_API_BASE_URL}/incoming/findAllIncoming` +
        `?pageNum=` + that.page.index + `&pageSize=` + that.page.size +
        `&producerName=` + that.producerInput +
        `&productName=` + that.productInput +
        `&bizStartDate=` + (that.billDateInput ? that.billDateInput[0] : ``) + `&bizEndDate=` + (that.billDateInput ? that.billDateInput[1] : ``);
      console.log('API URL:', url);
      this.$axios.get(url)
        .then(function (response) {
          console.log('API Response:', response);
          that.IncomingData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
        console.error('API Error:', error);
        that.$message.error('连接失败，请检查网络或服务状态');
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
    // 处理供应商名称变化
    handleProducerChange(type) {
      console.log('供应商名称变化，类型:', type)
      if (type === 'add') {
        console.log('添加入货单供应商名称变化:', this.addIncomingForm.producer)
        // 获取供应商联想建议
        this.getAddProducerSuggestions()
      } else if (type === 'update') {
        console.log('修改入货单供应商名称变化:', this.updateIncomingForm.producer)
        // 获取供应商联想建议
        this.getUpdateProducerSuggestions()
      }
    },
    // 获取添加入货单供应商联想建议
    getAddProducerSuggestions() {
      const that = this
      if (that.addIncomingForm.producer.length < 1) {
        that.addProducerSuggestions = []
        that.showAddProducerSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/incoming/findProducerNamesByPrefix`, {
        prefix: that.addIncomingForm.producer,
        pageNum: that.addProducerCurrentPage,
        pageSize: that.addProducerPageSize
      })
        .then(function (response) {
          that.addProducerSuggestions = response.data.data
          that.addProducerTotal = response.data.total
          that.showAddProducerSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.addProducerSuggestions = []
        that.addProducerTotal = 0
        that.showAddProducerSuggestions = false
      })
    },
    // 获取修改入货单供应商联想建议
    getUpdateProducerSuggestions() {
      const that = this
      if (that.updateIncomingForm.producer.length < 1) {
        that.updateProducerSuggestions = []
        that.showUpdateProducerSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/incoming/findProducerNamesByPrefix`, {
        prefix: that.updateIncomingForm.producer,
        pageNum: that.updateProducerCurrentPage,
        pageSize: that.updateProducerPageSize
      })
        .then(function (response) {
          that.updateProducerSuggestions = response.data.data
          that.updateProducerTotal = response.data.total
          that.showUpdateProducerSuggestions = true
        }).catch(function (error) {
        console.error(error)
        that.updateProducerSuggestions = []
        that.updateProducerTotal = 0
        that.showUpdateProducerSuggestions = false
      })
    },
    // 选择添加入货单供应商联想建议
    selectAddProducerSuggestion(item) {
      this.addIncomingForm.producer = item
      this.showAddProducerSuggestions = false
    },
    // 选择修改入货单供应商联想建议
    selectUpdateProducerSuggestion(item) {
      this.updateIncomingForm.producer = item
      this.showUpdateProducerSuggestions = false
    },
    // 处理点击页面其他地方的事件
    handleClickOutside(event) {
      // 隐藏所有供应商联想下拉框
      this.showProducerSuggestions = false
      this.showAddProducerSuggestions = false
      this.showUpdateProducerSuggestions = false
      // 隐藏所有产品联想下拉框
      this.showProductSuggestions = false
      this.showAddProductSuggestions = false
      this.showUpdateProductSuggestions = false
    },
    // 处理添加对话框供应商输入变化
    handleAddProducerInput() {
      this.addProducerCurrentPage = 1
      this.getAddProducerSuggestions()
    },
    // 处理添加对话框供应商分页
    handleAddProducerPageChange(pageNum) {
      this.addProducerCurrentPage = pageNum
      this.getAddProducerSuggestions()
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
    // 处理修改对话框供应商输入变化
    handleUpdateProducerInput() {
      this.updateProducerCurrentPage = 1
      this.getUpdateProducerSuggestions()
    },
    // 处理修改对话框供应商分页
    handleUpdateProducerPageChange(pageNum) {
      this.updateProducerCurrentPage = pageNum
      this.getUpdateProducerSuggestions()
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
    // 获取添加入货单产品联想建议
    getAddProductSuggestions() {
      const that = this
      if (that.addIncomingForm.product.length < 1) {
        that.addProductSuggestions = []
        that.showAddProductSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/findProductNamesByPrefix`, {
        prefix: that.addIncomingForm.product,
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
    // 获取修改入货单产品联想建议
    getUpdateProductSuggestions() {
      const that = this
      if (that.updateIncomingForm.product.length < 1) {
        that.updateProductSuggestions = []
        that.showUpdateProductSuggestions = false
        return
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/findProductNamesByPrefix`, {
        prefix: that.updateIncomingForm.product,
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
    // 选择添加入货单产品联想建议
    selectAddProductSuggestion(item) {
      this.addIncomingForm.product = item
      this.showAddProductSuggestions = false
    },
    // 选择修改入货单产品联想建议
    selectUpdateProductSuggestion(item) {
      this.updateIncomingForm.product = item
      this.showUpdateProductSuggestions = false
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
      // 添加入货单供应商联想相关数据
      addProducerSuggestions: [],
      showAddProducerSuggestions: false,
      addProducerCurrentPage: 1,
      addProducerPageSize: 10,
      addProducerTotal: 0,
      // 修改入货单供应商联想相关数据
      updateProducerSuggestions: [],
      showUpdateProducerSuggestions: false,
      updateProducerCurrentPage: 1,
      updateProducerPageSize: 10,
      updateProducerTotal: 0,
      // 产品联想相关数据
      productSuggestions: [],
      showProductSuggestions: false,
      productCurrentPage: 1,
      productPageSize: 10,
      productTotal: 0,
      // 添加表单产品联想相关数据
      addProductSuggestions: [],
      showAddProductSuggestions: false,
      addProductCurrentPage: 1,
      addProductPageSize: 10,
      addProductTotal: 0,
      // 修改表单产品联想相关数据
      updateProductSuggestions: [],
      showUpdateProductSuggestions: false,
      updateProductCurrentPage: 1,
      updateProductPageSize: 10,
      updateProductTotal: 0,
      addIncomingForm: {
        odd: ``,
        producer: ``,
        product: ``,
        billdate: ``,
        amount: 0,
        unitprice: 0,
        paystatus: `0`,
        remark: `无`,
        operate_material: 0
      },
      updateIncomingForm: {
        odd: ``,
        producer: ``,
        product: ``,
        billdate: ``,
        amount: 0,
        unitprice: 0,
        paystatus: `0`,
        remark: `无`,
        operate_material: 0
      },
      // 原材料关系相关数据
      addMaterialRelations: [],
      newAddMaterial: {
        material_name: '',
        quantity: 1
      },
      updateMaterialRelations: [],
      newUpdateMaterial: {
        material_name: '',
        quantity: 1
      },
      initialOperateMaterial: 0,
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
  },
  mounted() {
    // 添加点击事件监听器，点击页面其他地方时隐藏下拉框
    document.addEventListener('click', this.handleClickOutside)
    // 页面首次加载时获取数据
    console.log('Incoming页面mounted方法被调用');
    this.getAllIncoming()
  },
  beforeDestroy() {
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
#incoming {
  min-height: 100vh;
  padding: 20px;
}

#incoming h1 {
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
.incoming-search-container {
  position: relative;
  width: 100%;
}

/* 联想结果下拉框 */
.incoming-suggestions-dropdown {
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
.incoming-suggestion-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
  overflow: hidden;
  text-align: left;
}

.incoming-suggestion-item:hover {
  background-color: #f5f7fa;
  color: #667eea;
  transform: translateX(5px);
}

.incoming-suggestion-item:last-child {
  border-bottom: none;
}

/* 联想分页 */
.incoming-suggestion-pagination {
  padding: 12px;
  border-top: 1px solid #e4e7ed;
  background-color: #f9f9f9;
  border-radius: 0 0 8px 8px;
  text-align: center;
}

.incoming-suggestion-pagination .el-pagination {
  margin-top: 0;
}

/* 搜索容器样式 */
.incoming-search-container {
  position: relative;
  width: 100%;
}

/* 联想结果下拉框 */
.incoming-suggestions-dropdown {
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
.incoming-suggestion-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
  overflow: hidden;
  text-align: left;
}

.incoming-suggestion-item:hover {
  background-color: #f5f7fa;
  color: #667eea;
  transform: translateX(5px);
}

.incoming-suggestion-item:last-child {
  border-bottom: none;
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

/* 入货单对话框样式 */
.incoming-dialog {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2) !important;
  border: none !important;
}

.incoming-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border-bottom: none !important;
  padding: 20px 24px !important;
  margin: -20px -24px 0 !important;
  width: calc(100% + 48px) !important;
  box-sizing: border-box !important;
}

.incoming-dialog .el-dialog__title {
  font-size: 18px !important;
  font-weight: 600 !important;
  color: white !important;
  margin: 0 !important;
}

.incoming-dialog .el-dialog__headerbtn {
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

.incoming-dialog .el-dialog__headerbtn .el-icon {
  width: 24px !important;
  height: 24px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  line-height: 24px !important;
  font-size: 16px !important;
}

.incoming-dialog .el-dialog__headerbtn .el-icon .el-dialog__close {
  color: white !important;
  width: 100% !important;
  height: 100% !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.incoming-dialog .el-dialog__headerbtn .el-icon .el-dialog__close svg {
  width: 16px !important;
  height: 16px !important;
  vertical-align: middle !important;
  margin: 0 !important;
}

.incoming-dialog .el-dialog__body {
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
  #incoming {
    padding: 10px;
  }
  
  #app {
    padding: 15px;
  }
  
  #incoming h1 {
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

