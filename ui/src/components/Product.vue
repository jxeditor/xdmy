<template>
  <el-row>
    <el-col :span="24">
      <el-card shadow="never" class="search-card" style="overflow: visible;">
        <el-row :gutter="20">
          <el-col :span="16">
            <div style="display: flex; align-items: center; gap: 10px; position: relative;">
              <el-input
                v-model="searchQuery"
                placeholder="请输入产品名称"
                style="width: 300px"
                @input="handleInput"
                @focus="handleFocus"
                @blur="handleBlur"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <div v-if="showDropdown" class="dropdown-container product-dropdown">
                <div class="dropdown-header">
                  <span>共 {{ dropdownItems.length }} 条</span>
                </div>
                <div class="dropdown-body">
                  <div
                    v-for="(item, index) in dropdownItems"
                    :key="index"
                    class="dropdown-item"
                    @mousedown="selectItem(item)"
                  >
                    {{ item }}
                  </div>
                </div>
                <div class="dropdown-footer" v-if="total > pageSize">
                  <span>第 {{ currentPage }} / {{ totalPages }} 页</span>
                  <div>
                    <el-button
                      type="text"
                      size="small"
                      @click="prevPage"
                      :disabled="currentPage === 1"
                    >
                      上一页
                    </el-button>
                    <el-button
                      type="text"
                      size="small"
                      @click="nextPage"
                      :disabled="currentPage === totalPages"
                    >
                      下一页
                    </el-button>
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
      </el-card>

      <el-card shadow="never" class="data-card">
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
      </el-card>

      <!-- 添加产品对话框 -->
      <el-dialog v-model="dialogVisible" title="添加产品" width="500px">
        <el-form :model="addProductForm" label-width="120px">
          <el-form-item label="产品名称:" prop="productName">
            <el-input v-model="addProductForm.productName" placeholder="请输入产品名称" />
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
      <el-dialog v-model="editDialogVisible" title="编辑产品" width="500px">
        <el-form :model="editProductForm" label-width="120px">
          <el-form-item label="产品名称:" prop="productName">
            <el-input v-model="editProductForm.productName" placeholder="请输入产品名称" />
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
                      <div v-if="showMaterialDropdown" class="dropdown-container material-dropdown">
                        <div class="dropdown-header">
                          <span>共 {{ materialSuggestions.length }} 条</span>
                        </div>
                        <div class="dropdown-body">
                          <div
                            v-for="(item, index) in materialSuggestions"
                            :key="index"
                            class="dropdown-item"
                            @mousedown="selectMaterialSuggestion(item)"
                          >
                            {{ item }}
                          </div>
                        </div>
                        <div class="dropdown-footer" v-if="materialTotal > materialPageSize">
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
    </el-col>
  </el-row>
</template>

<script>
import { Search } from "@element-plus/icons-vue";

export default {
  name: "Product",
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
      total: 0,
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
      // 选中的产品数据
      selectedProducts: [],
    };
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
            that.total = response.data.total;
            that.showDropdown = true;
          }
        });
    },
    selectItem(item) {
      this.searchQuery = item;
      this.showDropdown = false;
      this.searchProduct();
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.fetchProductNames();
      }
    },
    nextPage() {
      if (this.currentPage < Math.ceil(this.total / this.pageSize)) {
        this.currentPage++;
        this.fetchProductNames();
      }
    },
    get totalPages() {
      return Math.ceil(this.total / this.pageSize);
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
    get materialTotalPages() {
      return Math.ceil(this.materialTotal / this.materialPageSize);
    },
  },
};
</script>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.data-card {
  margin-bottom: 20px;
}

.dropdown-container {
  position: absolute;
  top: 100%;
  left: 0;
  width: 300px;
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  margin-top: 4px;
}

.dropdown-header {
  padding: 8px 12px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 12px;
  color: #999;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dropdown-body {
  padding: 4px 0;
}

.dropdown-item {
  padding: 8px 12px;
  cursor: pointer;
  font-size: 14px;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
}

.dropdown-footer {
  padding: 8px 12px;
  border-top: 1px solid #f0f0f0;
  font-size: 12px;
  color: #999;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-dropdown {
  z-index: 9999 !important;
  position: absolute !important;
  top: 100% !important;
  left: 0 !important;
  margin-top: 4px !important;
  width: 300px !important;
  max-height: 300px !important;
  overflow-y: auto !important;
  border: 1px solid #d9d9d9 !important;
  border-radius: 4px !important;
  background: #fff !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15) !important;
}

.material-dropdown {
  z-index: 9999 !important;
  position: absolute !important;
  top: 100% !important;
  left: 0 !important;
  margin-top: 4px !important;
  width: 300px !important;
  max-height: 300px !important;
  overflow-y: auto !important;
  border: 1px solid #d9d9d9 !important;
  border-radius: 4px !important;
  background: #fff !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15) !important;
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
</style>
