<template>
  <div id="product">
    <h1>{{ msg }}</h1>
    <div id="app">
      <!-- 搜索行 -->
      <el-row type="flex" justify="space-between" align="center" style="width:100%;padding: 10px 20px; margin-bottom: 24px;">
        <el-col :span="16">
          <div class="shipment-search-container product-search-container" style="width: 400px;">
            <el-input
              v-model="searchQuery"
              placeholder="请输入产品名称"
              style="width: 400px"
              @input="handleInput"
              @focus="handleFocus"
              @blur="handleBlur"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <!-- 产品联想结果下拉框 -->
            <div
              v-if="showDropdown && dropdownItems.length > 0"
              class="shipment-suggestions-dropdown product-suggestions-dropdown"
            >
              <div class="product-dropdown-body">
                <div
                  v-for="(item, index) in dropdownItems"
                  :key="index"
                  class="shipment-suggestion-item product-dropdown-item"
                  @mousedown="selectItem(item)"
                >
                  {{ item }}
                </div>
              </div>
              <div
                v-if="dropdownTotal > pageSize"
                class="shipment-suggestion-pagination product-dropdown-footer"
              >
                <div class="product-dropdown-pagination-info">
                  <span>共 {{ dropdownTotal }} 条</span>
                  <span>第 {{ currentPage }} / {{ totalPages }} 页</span>
                </div>
                <el-pagination
                  small
                  layout="prev, pager, next"
                  :total="dropdownTotal"
                  :page-size="pageSize"
                  :current-page="currentPage"
                  @current-change="handleDropdownPageChange"
                  style="margin: 0; white-space: nowrap;"
                />
              </div>
              <div
                v-else
                class="shipment-suggestion-pagination product-dropdown-footer"
              >
                <div class="product-dropdown-pagination-info">
                  <span>共 {{ dropdownTotal }} 条</span>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="8" style="display: flex; justify-content: flex-end; gap: 10px;">
          <el-button type="primary" @click="searchProduct">搜索</el-button>
          <el-button type="primary" @click="onAddProduct">添加产品</el-button>
        </el-col>
      </el-row>

      <el-table :data="productData" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="productName" label="产品名称" width="200" align="center" />
        <el-table-column prop="suggestedPrice" label="建议售价" width="120" align="center">
          <template #default="scope">
            ¥{{ scope.row.suggestedPrice.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="costPrice" label="成本价" width="120" align="center">
          <template #default="scope">
            ¥{{ scope.row.costPrice.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
        <el-table-column prop="maintainMaterial" label="是否维护原材料" width="150" align="center">
          <template #default="scope">
            <span :class="scope.row.maintainMaterial === 1 ? 'maintain-yes' : 'maintain-no'">
              {{ scope.row.maintainMaterial === 1 ? '是' : '否' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="scope">
            <el-button size="small" @click="onEditProduct(scope.row)" style="margin-right: 5px">编辑</el-button>
            <el-button size="small" type="warning" @click="maintainMaterials(scope.row)" style="margin-right: 5px">维护原材料</el-button>
            <el-button size="small" type="danger" @click="onDeleteProduct(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        >
        </el-pagination>
      </div>
      <div style="margin-top: 20px">
        <el-button type="danger" @click="batchDeleteProduct" :disabled="selectedProducts.length === 0">批量删除</el-button>
        <el-button @click="onClearSelection">取消选择</el-button>
      </div>

      <!-- 添加产品对话框 -->
      <el-dialog v-model="dialogVisible" title="添加产品" width="500px" class="product-dialog">
        <el-form :model="addProductForm" label-width="120px">
          <el-form-item label="产品名称:" prop="productName">
            <div class="product-search-container">
              <el-input
                v-model="addProductForm.productName"
                placeholder="请输入产品名称"
                style="width: 300px"
                @input="handleAddProductInput"
                @focus="handleAddProductFocus"
                @blur="handleAddProductBlur"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <!-- 添加产品联想结果下拉框 -->
              <div
                v-if="showAddProductDropdown && addProductSuggestions.length > 0"
                class="product-suggestions-dropdown"
              >
                <div class="product-dropdown-body">
                  <div
                    v-for="(item, index) in addProductSuggestions"
                    :key="index"
                    class="product-dropdown-item"
                    @mousedown="selectAddProductSuggestion(item)"
                  >
                    {{ item }}
                  </div>
                </div>
                <div
                  v-if="addProductTotal > addProductPageSize"
                  class="product-dropdown-footer"
                >
                  <el-pagination
                    small
                    layout="prev, pager, next"
                    :total="addProductTotal"
                    :page-size="addProductPageSize"
                    :current-page="addProductCurrentPage"
                    @current-change="handleAddProductPageChange"
                    style="margin: 0; white-space: nowrap;"
                  />
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="建议售价:" prop="suggestedPrice">
            <el-input-number v-model="addProductForm.suggestedPrice" :min="0" :step="0.01" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="成本价:" prop="costPrice">
            <el-input-number v-model="addProductForm.costPrice" :min="0" :step="0.01" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="是否维护原材料:" prop="maintainMaterial">
            <el-radio-group v-model="addProductForm.maintainMaterial">
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">确定</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 编辑产品对话框 -->
      <el-dialog v-model="editDialogVisible" title="编辑产品" width="500px" class="product-dialog">
        <el-form :model="editProductForm" label-width="120px">
          <el-form-item label="产品名称:" prop="productName">
            <div class="product-search-container">
              <el-input
                v-model="editProductForm.productName"
                placeholder="请输入产品名称"
                style="width: 300px"
                @input="handleEditProductInput"
                @focus="handleEditProductFocus"
                @blur="handleEditProductBlur"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <!-- 编辑产品联想结果下拉框 -->
              <div
                v-if="showEditProductDropdown && editProductSuggestions.length > 0"
                class="product-suggestions-dropdown"
              >
                <div class="product-dropdown-body">
                  <div
                    v-for="(item, index) in editProductSuggestions"
                    :key="index"
                    class="product-dropdown-item"
                    @mousedown="selectEditProductSuggestion(item)"
                  >
                    {{ item }}
                  </div>
                </div>
                <div
                  v-if="editProductTotal > editProductPageSize"
                  class="product-dropdown-footer"
                >
                  <el-pagination
                    small
                    layout="prev, pager, next"
                    :total="editProductTotal"
                    :page-size="editProductPageSize"
                    :current-page="editProductCurrentPage"
                    @current-change="handleEditProductPageChange"
                    style="margin: 0; white-space: nowrap;"
                  />
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="建议售价:" prop="suggestedPrice">
            <el-input-number v-model="editProductForm.suggestedPrice" :min="0" :step="0.01" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="成本价:" prop="costPrice">
            <el-input-number v-model="editProductForm.costPrice" :min="0" :step="0.01" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="是否维护原材料:" prop="maintainMaterial">
            <el-radio-group v-model="editProductForm.maintainMaterial">
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="editDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitEditForm">确定</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 维护原材料对话框 -->
      <el-dialog v-model="materialDialogVisible" :title="'维护原材料 - ' + currentProductName" width="700px">
        <div class="material-maintenance">
          <!-- 原材料列表 -->
          <el-table :data="materialList" style="width: 100%" border>
            <el-table-column prop="materialName" label="原材料名称" />
            <el-table-column prop="quantity" label="数量" width="180">
              <template #default="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" :step="1" style="width: 150px" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" type="danger" @click="removeMaterial(scope.$index)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 添加原材料表单 -->
          <div class="add-material-form">
            <h4 style="margin-top: 20px; margin-bottom: 10px">添加原材料</h4>
            <el-form :model="addMaterialForm" label-width="100px" style="margin-top: 10px">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="原材料名称:">
                    <div style="position: relative;">
                      <el-input 
                        v-model="addMaterialForm.materialName" 
                        placeholder="请输入原材料名称"
                        @input="handleMaterialInput"
                        @focus="handleMaterialFocus"
                        @blur="handleMaterialBlur"
                      />
                      <div v-if="showMaterialDropdown" class="product-dropdown-container product-material-dropdown">
                        <div class="product-dropdown-header">
                          <span>共 {{ materialSuggestions.length }} 条</span>
                        </div>
                        <div class="product-dropdown-body">
                          <div
                            v-for="(item, index) in materialSuggestions"
                            :key="index"
                            class="product-dropdown-item"
                            @mousedown="selectMaterialSuggestion(item)"
                          >
                            {{ item }}
                          </div>
                        </div>
                        <div class="product-dropdown-footer" v-if="materialTotal > materialPageSize">
                          <span>第 {{ materialCurrentPage }} / {{ materialTotalPages }} 页</span>
                          <div>
                            <el-button
                              type="text"
                              size="small"
                              @click="prevMaterialPage"
                              :disabled="materialCurrentPage === 1"
                            >
                              上一页
                            </el-button>
                            <el-button
                              type="text"
                              size="small"
                              @click="nextMaterialPage"
                              :disabled="materialCurrentPage === materialTotalPages"
                            >
                              下一页
                            </el-button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="数量:">
                    <el-input-number v-model="addMaterialForm.quantity" :min="1" :step="1" style="width: 100%" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item>
                <el-button type="primary" @click="addMaterialToList">添加</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="cancelMaterialMaintenance">取消</el-button>
            <el-button type="primary" @click="confirmMaterialMaintenance">确认</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { Search } from "@element-plus/icons-vue";

export default {
  name: "Product",
  props: {
    msg: String
  },
  components: {
    Search,
  },
  data() {
    return {
      productData: [],
      searchQuery: "",
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      editDialogVisible: false,
      addProductForm: {
        productName: "",
        suggestedPrice: 0,
        costPrice: 0,
        maintainMaterial: 0,
      },
      editProductForm: {
        id: "",
        productName: "",
        suggestedPrice: 0,
        costPrice: 0,
        maintainMaterial: 0,
      },
      showDropdown: false,
      dropdownItems: [],
      currentPage: 1,
      pageSize: 10,
      dropdownTotal: 0,
      // 维护原材料相关数据
      materialDialogVisible: false,
      currentProductName: '',
      materialList: [],
      originalMaterialList: [], // 存储原始数据，用于取消操作
      addMaterialForm: {
        materialName: '',
        quantity: 1,
      },
      // 原材料联想相关数据
      showMaterialDropdown: false,
      materialSuggestions: [],
      materialCurrentPage: 1,
      materialPageSize: 10,
      materialTotal: 0,
      // 添加产品联想相关数据
      addProductSuggestions: [],
      showAddProductDropdown: false,
      addProductCurrentPage: 1,
      addProductPageSize: 10,
      addProductTotal: 0,
      // 编辑产品联想相关数据
      editProductSuggestions: [],
      showEditProductDropdown: false,
      editProductCurrentPage: 1,
      editProductPageSize: 10,
      editProductTotal: 0,
      // 选中的产品数据
      selectedProducts: [],
    };
  },
  computed: {
    totalPages() {
      if (this.dropdownTotal === 0) {
        return 1;
      }
      return Math.ceil(this.dropdownTotal / this.pageSize);
    },
    materialTotalPages() {
      if (this.materialTotal === 0) {
        return 1;
      }
      return Math.ceil(this.materialTotal / this.materialPageSize);
    },
  },
  mounted() {
    this.getAllProduct();
  },
  methods: {
    getAllProduct() {
      const that = this;
      this.$axios
        .get(
          `${process.env.VUE_APP_API_BASE_URL}/product/findAllProduct`,
          {
            params: {
              pageNum: this.pageNum,
              pageSize: this.pageSize,
              productName: this.searchQuery,
            },
          }
        )
        .then(function (response) {
          that.productData = response.data.data;
          that.total = response.data.total;
        });
    },
    searchProduct() {
      this.pageNum = 1;
      this.getAllProduct();
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getAllProduct();
    },
    handleCurrentChange(val) {
      this.pageNum = val;
      this.getAllProduct();
    },
    onAddProduct() {
      this.dialogVisible = true;
      this.addProductForm = {
        productName: "",
        suggestedPrice: 0,
        costPrice: 0,
        maintainMaterial: 0,
      };
    },
    onEditProduct(row) {
      this.editDialogVisible = true;
      this.editProductForm = {
        id: row.id,
        productName: row.productName,
        suggestedPrice: row.suggestedPrice,
        costPrice: row.costPrice,
        maintainMaterial: row.maintainMaterial,
      };
    },
    maintainMaterials(row) {
      // 打开维护原材料对话框
      this.currentProductName = row.productName;
      this.materialDialogVisible = true;
      // 加载该产品的原材料信息
      this.getMaterialList(row.productName);
    },
    // 获取产品的原材料列表
    getMaterialList(productName) {
      const that = this;
      this.$axios
        .get(
          `${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/findRelationsByProductName`,
          {
            params: {
              productName: productName,
            },
          }
        )
        .then(function (response) {
          console.log('获取原材料列表响应:', response);
          if (response.data.code === 1) {
            that.materialList = response.data.data;
            // 保存原始数据，用于取消操作
            that.originalMaterialList = JSON.parse(JSON.stringify(response.data.data));
          }
        })
        .catch(error => {
          console.error('获取原材料列表失败:', error);
        });
    },
    // 添加原材料到列表
    addMaterialToList() {
      if (!this.addMaterialForm.materialName) {
        this.$message.error("请输入原材料名称");
        return;
      }
      
      // 检查是否已存在相同名称的原材料
      const exists = this.materialList.some(item => item.materialName === this.addMaterialForm.materialName);
      if (exists) {
        this.$message.error("该原材料已存在");
        return;
      }
      
      // 添加到列表中
      this.materialList.push({
        productName: this.currentProductName,
        materialName: this.addMaterialForm.materialName,
        quantity: this.addMaterialForm.quantity,
        isDefault: 0
      });
      
      // 重置表单
      this.addMaterialForm = {
        materialName: '',
        quantity: 1,
      };
    },
    // 从列表中移除原材料
    removeMaterial(index) {
      this.materialList.splice(index, 1);
    },
    // 取消维护原材料
    cancelMaterialMaintenance() {
      // 恢复原始数据
      this.materialList = JSON.parse(JSON.stringify(this.originalMaterialList));
      this.materialDialogVisible = false;
    },
    // 确认维护原材料
    confirmMaterialMaintenance() {
      const that = this;
      
      // 显示加载中
      this.$loading({
        lock: true,
        text: '正在保存...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      
      // 首先删除所有原始原材料
      const deletePromises = this.originalMaterialList.map(item => {
        return that.$axios.get(
          `${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/deleteRelationById`,
          {
            params: {
              id: item.id,
            },
          }
        );
      });
      
      // 执行删除操作
      Promise.all(deletePromises)
        .then(() => {
          // 然后添加所有新的原材料
          const addPromises = that.materialList.map(item => {
            // 使用URLSearchParams格式发送参数，确保后端能正确获取
            const params = new URLSearchParams();
            params.append('productName', item.productName);
            params.append('materialName', item.materialName);
            params.append('quantity', item.quantity);
            params.append('isDefault', item.isDefault || 0);
            
            return that.$axios.post(
              `${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/addRelation`,
              params,
              {
                headers: {
                  'Content-Type': 'application/x-www-form-urlencoded'
                }
              }
            );
          });
          
          return Promise.all(addPromises);
        })
        .then(() => {
          // 关闭加载中
          that.$loading().close();
          
          // 显示成功消息
          that.$message({
            type: "success",
            message: "保存成功!",
          });
          
          // 关闭对话框
          that.materialDialogVisible = false;
          
          // 更新产品的maintainMaterial字段
          const product = that.productData.find(p => p.productName === that.currentProductName);
          if (product) {
            that.$axios
              .post(
                `${process.env.VUE_APP_API_BASE_URL}/product/updateProduct`,
                {
                  id: product.id,
                  productName: that.currentProductName,
                  suggestedPrice: product.suggestedPrice,
                  costPrice: product.costPrice,
                  maintainMaterial: that.materialList.length > 0 ? 1 : 0,
                }
              )
            .then(() => {
              // 重新加载产品列表
              that.getAllProduct();
            });
          }
        })
        .catch(error => {
          // 关闭加载中
          that.$loading().close();
          
          // 显示错误消息
          that.$message.error("保存失败，请重试");
          console.error('保存原材料失败:', error);
          // 打印具体的错误信息
          if (error.response) {
            console.error('响应数据:', error.response.data);
            console.error('响应状态:', error.response.status);
          } else if (error.request) {
            console.error('请求数据:', error.request);
          } else {
            console.error('错误信息:', error.message);
          }
        });
    },
    onDeleteProduct(id) {
      const that = this;
      this.$confirm("此操作将永久删除该产品, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(function () {
          that.$axios
            .get(
              `${process.env.VUE_APP_API_BASE_URL}/product/deleteProductById`,
              {
                params: {
                  id: id,
                },
              }
            )
            .then(function (response) {
              if (response.data.code === 1) {
                that.$message({
                  type: "success",
                  message: "删除成功!",
                });
                that.getAllProduct();
              } else {
                that.$message.error("删除失败");
              }
            });
        })
        .catch(function () {
          that.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },
    // 处理表格选择变化
    handleSelectionChange(val) {
      this.selectedProducts = val;
    },
    // 批量删除产品
    batchDeleteProduct() {
      const that = this;
      if (that.selectedProducts.length === 0) {
        that.$message.warning('请先选择要删除的数据');
        return;
      }
      
      this.$confirm(`确定要删除选中的 ${that.selectedProducts.length} 个产品吗？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 提取选中数据的id列表
        const ids = that.selectedProducts.map(item => item.id).join(',');
        
        that.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/product/batchDeleteProduct`, {
          params: {
            ids: ids
          }
        })
          .then(function (response) {
            if (response.data.code === 1) {
              that.$message({
                type: "success",
                message: "批量删除成功!",
              });
              that.getAllProduct();
              that.selectedProducts = [];
            } else {
              that.$message.error(response.data.msg);
            }
          }).catch(function (error) {
            that.$message.error('批量删除失败：' + error);
          });
      }).catch(() => {
        // 取消操作
      });
    },
    // 取消选择
    onClearSelection() {
      this.$refs.multipleTable.clearSelection();
      this.selectedProducts = [];
    },

    submitForm() {
      const that = this;
      this.$axios
        .post(
          `${process.env.VUE_APP_API_BASE_URL}/product/addProduct`,
          this.addProductForm
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.$message({
              type: "success",
              message: "添加成功!",
            });
            that.dialogVisible = false;
            that.getAllProduct();
          } else {
            that.$message.error("添加失败");
          }
        });
    },
    submitEditForm() {
      const that = this;
      this.$axios
        .post(
          `${process.env.VUE_APP_API_BASE_URL}/product/updateProduct`,
          this.editProductForm
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.$message({
              type: "success",
              message: "编辑成功!",
            });
            that.editDialogVisible = false;
            that.getAllProduct();
          } else {
            that.$message.error("编辑失败");
          }
        });
    },
    handleInput() {
      this.currentPage = 1;
      this.fetchProductNames();
    },
    handleFocus() {
      if (this.searchQuery) {
        this.fetchProductNames();
      }
    },
    handleBlur() {
      setTimeout(() => {
        this.showDropdown = false;
      }, 200);
    },
    fetchProductNames() {
      const that = this;
      this.$axios
        .post(
          `${process.env.VUE_APP_API_BASE_URL}/product/findProductNamesByPrefix`,
          {
            prefix: this.searchQuery,
            pageNum: this.currentPage,
            pageSize: this.pageSize,
          }
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.dropdownItems = response.data.data;
            that.dropdownTotal = response.data.total;
            that.showDropdown = true;
          }
        });
    },
    selectItem(item) {
      this.searchQuery = item;
      this.showDropdown = false;
      this.searchProduct();
    },
    handleDropdownPageChange(pageNum) {
      if (this.currentPage !== pageNum) {
        this.currentPage = pageNum;
        this.fetchProductNames();
      }
    },
    // 原材料联想相关方法
    handleMaterialInput() {
      this.materialCurrentPage = 1;
      this.fetchMaterialSuggestions();
    },
    handleMaterialFocus() {
      if (this.addMaterialForm.materialName) {
        this.fetchMaterialSuggestions();
      }
    },
    handleMaterialBlur() {
      setTimeout(() => {
        this.showMaterialDropdown = false;
      }, 200);
    },
    fetchMaterialSuggestions() {
      const that = this;
      if (that.addMaterialForm.materialName.length < 1) {
        that.materialSuggestions = [];
        that.showMaterialDropdown = false;
        return;
      }
      this.$axios
        .post(
          `${process.env.VUE_APP_API_BASE_URL}/materialStock/findMaterialNamesByPrefix`,
          {
            prefix: that.addMaterialForm.materialName,
            pageNum: that.materialCurrentPage,
            pageSize: that.materialPageSize
          }
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.materialSuggestions = response.data.data;
            that.materialTotal = response.data.total;
            that.showMaterialDropdown = true;
          }
        });
    },
    selectMaterialSuggestion(item) {
      this.addMaterialForm.materialName = item;
      this.showMaterialDropdown = false;
    },
    prevMaterialPage() {
      if (this.materialCurrentPage > 1) {
        this.materialCurrentPage--;
        this.fetchMaterialSuggestions();
      }
    },
    nextMaterialPage() {
      if (this.materialCurrentPage < this.materialTotalPages) {
        this.materialCurrentPage++;
        this.fetchMaterialSuggestions();
      }
    },
    // 添加产品联想相关方法
    handleAddProductInput() {
      this.addProductCurrentPage = 1;
      this.fetchAddProductSuggestions();
    },
    handleAddProductFocus() {
      if (this.addProductForm.productName) {
        this.fetchAddProductSuggestions();
      }
    },
    handleAddProductBlur() {
      setTimeout(() => {
        this.showAddProductDropdown = false;
      }, 200);
    },
    fetchAddProductSuggestions() {
      const that = this;
      if (that.addProductForm.productName.length < 1) {
        that.addProductSuggestions = [];
        that.showAddProductDropdown = false;
        return;
      }
      this.$axios
        .post(
          `${process.env.VUE_APP_API_BASE_URL}/product/findProductNamesByPrefix`,
          {
            prefix: that.addProductForm.productName,
            pageNum: that.addProductCurrentPage,
            pageSize: that.addProductPageSize,
          }
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.addProductSuggestions = response.data.data;
            that.addProductTotal = response.data.total;
            that.showAddProductDropdown = true;
          }
        });
    },
    selectAddProductSuggestion(item) {
      this.addProductForm.productName = item;
      this.showAddProductDropdown = false;
    },
    handleAddProductPageChange(pageNum) {
      if (this.addProductCurrentPage !== pageNum) {
        this.addProductCurrentPage = pageNum;
        this.fetchAddProductSuggestions();
      }
    },
    // 编辑产品联想相关方法
    handleEditProductInput() {
      this.editProductCurrentPage = 1;
      this.fetchEditProductSuggestions();
    },
    handleEditProductFocus() {
      if (this.editProductForm.productName) {
        this.fetchEditProductSuggestions();
      }
    },
    handleEditProductBlur() {
      setTimeout(() => {
        this.showEditProductDropdown = false;
      }, 200);
    },
    fetchEditProductSuggestions() {
      const that = this;
      if (that.editProductForm.productName.length < 1) {
        that.editProductSuggestions = [];
        that.showEditProductDropdown = false;
        return;
      }
      this.$axios
        .post(
          `${process.env.VUE_APP_API_BASE_URL}/product/findProductNamesByPrefix`,
          {
            prefix: that.editProductForm.productName,
            pageNum: that.editProductCurrentPage,
            pageSize: that.editProductPageSize,
          }
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.editProductSuggestions = response.data.data;
            that.editProductTotal = response.data.total;
            that.showEditProductDropdown = true;
          }
        });
    },
    selectEditProductSuggestion(item) {
      this.editProductForm.productName = item;
      this.showEditProductDropdown = false;
    },
    handleEditProductPageChange(pageNum) {
      if (this.editProductCurrentPage !== pageNum) {
        this.editProductCurrentPage = pageNum;
        this.fetchEditProductSuggestions();
      }
    },
  },
};
</script>

<style scoped>
/* 动画效果 */
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

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 页面容器 */
#product {
  min-height: 100vh;
  padding: 20px;
}

#product h1 {
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

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.search-card {
  margin-bottom: 20px;
  position: relative;
  z-index: 10;
}

.data-card {
  margin-bottom: 20px;
}

.product-search-container {
  position: relative;
  display: inline-block;
  z-index: 2000;
  width: 400px;
}

.product-suggestions-dropdown {
  position: fixed;
  width: 400px;
  max-height: 350px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 999999;
  animation: fadeIn 0.3s ease-out;
}

.product-dropdown-header {
  padding: 8px 12px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 12px;
  color: #999;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-dropdown-body {
  padding: 4px 0;
}

.product-dropdown-item {
  padding: 8px 12px;
  cursor: pointer;
  font-size: 14px;
}

.product-dropdown-item:hover {
  background-color: #f5f5f5;
}

.product-dropdown-footer {
  padding: 8px 12px;
  border-top: 1px solid #f0f0f0;
  font-size: 12px;
  color: #999;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: nowrap;
}

.product-dropdown-pagination-info {
  display: flex;
  gap: 15px;
  margin-right: 10px;
  color: #999;
  font-size: 12px;
  white-space: nowrap;
}

/* 是否维护原材料样式 */
.maintain-yes {
  color: green;
  font-weight: bold;
}

.maintain-no {
  color: red;
  font-weight: bold;
}


.product-dialog {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2) !important;
  border: none !important;
}

.product-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border-bottom: none !important;
  padding: 20px 24px !important;
  margin: -20px -24px 0 !important;
  width: calc(100% + 48px) !important;
  box-sizing: border-box !important;
}


</style>

<style>
/* 对话框样式：不使用 scoped，因为 el-dialog 渲染在 body 下 */
.product-dialog {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2) !important;
  border: none !important;
}

.product-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border-bottom: none !important;
  padding: 20px 24px !important;
  margin: -20px -24px 0 !important;
  width: calc(100% + 48px) !important;
  height: 60px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: space-between !important;
  box-sizing: border-box !important;
  overflow: hidden !important;
  position: relative !important;
}

.product-dialog .el-dialog__title {
  color: white !important;
  font-size: 18px !important;
  font-weight: 600 !important;
  margin: 0 !important;
  padding: 0 24px !important;
  height: 100% !important;
  display: flex !important;
  align-items: center !important;
  box-sizing: border-box !important;
  flex: 1 !important;
}

.product-dialog .el-dialog__headerbtn {
  position: relative !important;
  top: 0 !important;
  right: 0 !important;
  margin-top: 0 !important;
  padding: 0 !important;
  width: 60px !important;
  height: 60px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-sizing: border-box !important;
  flex-shrink: 0 !important;
}

.product-dialog .el-dialog__headerbtn:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.product-dialog .el-dialog__headerbtn .el-icon {
  color: white !important;
  font-size: 16px !important;
  line-height: 1 !important;
}

.product-dialog .el-dialog__headerbtn .el-icon svg {
  fill: white !important;
  width: 16px !important;
  height: 16px !important;
  vertical-align: middle !important;
  margin: 0 !important;
}

.product-dialog .el-dialog__body {
  padding: 30px !important;
  min-height: 300px !important;
  box-sizing: border-box !important;
  background-color: #ffffff !important;
}
</style>
