<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">原材料库存</h2>
      <div class="page-actions" style="display:flex;gap:8px;align-items:center;">
        <el-button type="primary" @click='onAddMaterialStock'>添加材料</el-button>
        <el-button type="warning" @click="toggleHideZeroStock">{{ hideZeroStock ? '取消隐藏' : '隐藏' }}</el-button>
      </div>
    </div>
    <div class="filter-bar">
      <div class="material-search-container">
        <el-input
          v-model="searchQuery"
          placeholder="请输入材料名称"
          clearable
          style="width: 100%; text-align: left;"
          @input="handleInput"
          @focus="handleFocus"
          @blur="handleBlur"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <!-- 联想结果下拉框 -->
        <div v-if="showDropdown" class="material-dropdown-container material-dropdown">
          <div class="material-dropdown-body">
            <div
              v-for="(item, index) in dropdownItems"
              :key="index"
              class="material-dropdown-item"
              @mousedown="selectItem(item)"
            >
              {{ item }}
            </div>
          </div>
          <div class="material-dropdown-footer">
            <span>共 {{ total }} 条</span>
            <div v-if="total > pageSize">
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
      </div>
      <el-button type="primary" @click="searchStock">搜索</el-button>
    </div>
    <div class="card" style="padding:0;overflow:hidden;">
      <el-table ref="multipleTable" stripe :data="MaterialStockData" style="width: 100%;" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center">
        </el-table-column>
        <el-table-column prop="materialName" label="材料名称" min-width="220" show-overflow-tooltip align="center">
        </el-table-column>
        <el-table-column label="数量" min-width="120" align="center">
          <template #default="scope">
            <span :style="getQuantityStyle(scope.row.unitstock)">
              {{ scope.row.unitstock }}
            </span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" align="center" width="220">
          <template #default="opt">
            <el-button type="primary" @click="onEditMaterialStock(opt.row)">编辑</el-button>
            <el-button type="danger" @click="onDeleteMaterialStock(opt.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
        </el-pagination>
      </div>
    </div>
    <div style="margin-top: 20px">
      <el-button type="danger" @click="batchDeleteMaterialStock" :disabled="selectedMaterials.length === 0">批量删除</el-button>
      <el-button @click="onClearSelection">取消选择</el-button>
    </div>

      <!-- 添加库存对话框 -->
      <el-dialog v-model="dialogVisible" title="添加材料库存" width="500px" class="material-dialog">
        <el-form :model="form" label-width="120px">
          <el-form-item label="材料名称">
            <div class="material-search-container">
              <el-input
                v-model="form.materialName"
                placeholder="请输入材料名称"
                style="width: 300px"
                @input="handleAddMaterialInput"
                @focus="handleAddMaterialFocus"
                @blur="handleAddMaterialBlur"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <!-- 添加材料联想结果下拉框 -->
              <div
                v-if="showAddMaterialDropdown && addMaterialSuggestions.length > 0"
                class="material-dropdown-container material-dropdown"
              >
                <div class="material-dropdown-body">
                  <div
                    v-for="(item, index) in addMaterialSuggestions"
                    :key="index"
                    class="material-dropdown-item"
                    @mousedown="selectAddMaterialSuggestion(item)"
                  >
                    {{ item }}
                  </div>
                </div>
                <div
                  v-if="addMaterialTotal > addMaterialPageSize"
                  class="material-dropdown-footer"
                >
                  <el-pagination
                    small
                    layout="prev, pager, next"
                    :total="addMaterialTotal"
                    :page-size="addMaterialPageSize"
                    :current-page="addMaterialCurrentPage"
                    @current-change="handleAddMaterialPageChange"
                    style="margin: 0; white-space: nowrap;"
                  />
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="数量">
            <el-input-number v-model="form.unitstock" :min="0" style="width: 100%" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">确定</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 编辑库存对话框 -->
      <el-dialog v-model="editDialogVisible" title="编辑材料库存" width="500px" class="material-dialog">
        <el-form :model="editForm" label-width="120px">
          <el-form-item label="材料名称">
            <div class="material-search-container">
              <el-input
                v-model="editForm.materialName"
                placeholder="请输入材料名称"
                style="width: 300px"
                @input="handleEditMaterialInput"
                @focus="handleEditMaterialFocus"
                @blur="handleEditMaterialBlur"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <!-- 编辑材料联想结果下拉框 -->
              <div
                v-if="showEditMaterialDropdown && editMaterialSuggestions.length > 0"
                class="material-dropdown-container material-dropdown"
              >
                <div class="material-dropdown-body">
                  <div
                    v-for="(item, index) in editMaterialSuggestions"
                    :key="index"
                    class="material-dropdown-item"
                    @mousedown="selectEditMaterialSuggestion(item)"
                  >
                    {{ item }}
                  </div>
                </div>
                <div
                  v-if="editMaterialTotal > editMaterialPageSize"
                  class="material-dropdown-footer"
                >
                  <el-pagination
                    small
                    layout="prev, pager, next"
                    :total="editMaterialTotal"
                    :page-size="editMaterialPageSize"
                    :current-page="editMaterialCurrentPage"
                    @current-change="handleEditMaterialPageChange"
                    style="margin: 0; white-space: nowrap;"
                  />
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="数量">
            <el-input-number v-model="editForm.unitstock" :min="0" style="width: 100%" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="editDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitEditForm">确定</el-button>
          </span>
        </template>
      </el-dialog>
  </div>
</template>

<script>
import { Search } from "@element-plus/icons-vue";

export default {
  name: "MaterialStock",
  data() {
    return {
      MaterialStockData: [],
      searchQuery: "",
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      editDialogVisible: false,
      form: {
        materialName: "",
        unitstock: 0,
      },
      editForm: {
        id: "",
        materialName: "",
        unitstock: 0,
      },
      showDropdown: false,
      dropdownItems: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      hideZeroStock: false,
      // 添加材料联想相关数据（从原材料表获取）
      addMaterialSuggestions: [],
      showAddMaterialDropdown: false,
      addMaterialCurrentPage: 1,
      addMaterialPageSize: 10,
      addMaterialTotal: 0,
      // 编辑材料联想相关数据（从原材料表获取）
      editMaterialSuggestions: [],
      showEditMaterialDropdown: false,
      editMaterialCurrentPage: 1,
      editMaterialPageSize: 10,
      editMaterialTotal: 0,
      // 选中的原材料数据
      selectedMaterials: [],
    };
  },
  mounted() {
    this.getAllMaterialStock();
  },
  methods: {
    getAllMaterialStock() {
      const that = this;
      this.$axios
        .get(
          `${process.env.VUE_APP_API_BASE_URL}/materialStock/findAllMaterialStock`,
          {
            params: {
              pageNum: this.pageNum,
              pageSize: this.pageSize,
              materialName: this.searchQuery,
              hideZeroStock: this.hideZeroStock,
            },
          }
        )
        .then(function (response) {
          console.log('原材料库存数据:', response.data);
          if (response.data.code === 1) {
            that.MaterialStockData = response.data.data;
            that.total = response.data.total;
          }
        })
        .catch(function (error) {
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            that.$message.error('连接失败，请检查网络或服务状态');
          }
        });
    },
    searchStock() {
      this.pageNum = 1;
      this.getAllMaterialStock();
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getAllMaterialStock();
    },
    handleCurrentChange(val) {
      this.pageNum = val;
      this.getAllMaterialStock();
    },
    onAddMaterialStock() {
      this.dialogVisible = true;
      this.form = {
        materialName: "",
        unitstock: 0,
      };
    },
    onEditMaterialStock(row) {
      this.editDialogVisible = true;
      this.editForm = {
        id: row.id,
        materialName: row.materialName,
        unitstock: row.unitstock,
      };
    },
    onDeleteMaterialStock(id) {
      const that = this;
      this.$confirm("此操作将永久删除该材料库存, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(function () {
          that.$axios
            .get(
              `${process.env.VUE_APP_API_BASE_URL}/materialStock/deleteMaterialStockById`,
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
                that.getAllMaterialStock();
              } else {
                that.$message.error("删除失败");
              }
            })
            .catch(function (error) {
              // 401错误由响应拦截器处理，不显示错误信息
              if (error.response && error.response.status !== 401) {
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
    submitForm() {
      const that = this;
      this.$axios
        .get(
          `${process.env.VUE_APP_API_BASE_URL}/materialStock/addMaterialStock`,
          {
            params: {
              materialName: this.form.materialName,
              unitstock: this.form.unitstock,
            },
          }
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.$message({
              type: "success",
              message: "添加成功!",
            });
            that.dialogVisible = false;
            that.getAllMaterialStock();
          } else {
            that.$message.error("添加失败");
          }
        })
        .catch(function (error) {
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            that.$message.error("添加失败");
          }
        });
    },
    submitEditForm() {
      const that = this;
      this.$axios
        .get(
          `${process.env.VUE_APP_API_BASE_URL}/materialStock/updateMaterialStock`,
          {
            params: {
              id: this.editForm.id,
              materialName: this.editForm.materialName,
              unitstock: this.editForm.unitstock,
            },
          }
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.$message({
              type: "success",
              message: "编辑成功!",
            });
            that.editDialogVisible = false;
            that.getAllMaterialStock();
          } else {
            that.$message.error("编辑失败");
          }
        })
        .catch(function (error) {
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            that.$message.error("编辑失败");
          }
        });
    },
    handleInput() {
      this.currentPage = 1;
      this.fetchMaterialNames();
    },
    handleFocus() {
      if (this.searchQuery) {
        this.fetchMaterialNames();
      }
    },
    handleBlur() {
      setTimeout(() => {
        this.showDropdown = false;
      }, 200);
    },
    fetchMaterialNames() {
      const that = this;
      this.$axios
        .post(
          `${process.env.VUE_APP_API_BASE_URL}/materialStock/findMaterialNamesByPrefix`,
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
        })
        .catch(function (error) {
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            console.error('获取材料名称失败:', error);
          }
        });
    },
    selectItem(item) {
      this.searchQuery = item;
      this.showDropdown = false;
    },
    // 根据数量返回不同的颜色样式
    getQuantityStyle(quantity) {
      if (quantity > 0) {
        return { color: '#67C23A' }; // 绿色
      } else if (quantity === 0) {
        return { color: '#E6A23C' }; // 黄色
      } else {
        return { color: '#F56C6C' }; // 红色
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.fetchMaterialNames();
      }
    },
    nextPage() {
      if (this.currentPage < Math.ceil(this.total / this.pageSize)) {
        this.currentPage++;
        this.fetchMaterialNames();
      }
    },
    get totalPages() {
      return Math.ceil(this.total / this.pageSize);
    },
    // 添加材料联想相关方法（从原材料表获取数据）
    handleAddMaterialInput() {
      this.addMaterialCurrentPage = 1;
      this.fetchAddMaterialSuggestions();
    },
    handleAddMaterialFocus() {
      if (this.form.materialName) {
        this.fetchAddMaterialSuggestions();
      }
    },
    handleAddMaterialBlur() {
      setTimeout(() => {
        this.showAddMaterialDropdown = false;
      }, 200);
    },
    fetchAddMaterialSuggestions() {
      const that = this;
      if (that.form.materialName.length < 1) {
        that.addMaterialSuggestions = [];
        that.showAddMaterialDropdown = false;
        return;
      }
      this.$axios
        .post(
          `${process.env.VUE_APP_API_BASE_URL}/materialStock/findMaterialNamesByPrefix`,
          {
            prefix: that.form.materialName,
            pageNum: that.addMaterialCurrentPage,
            pageSize: that.addMaterialPageSize,
          }
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.addMaterialSuggestions = response.data.data;
            that.addMaterialTotal = response.data.total;
            that.showAddMaterialDropdown = true;
          }
        })
        .catch(function (error) {
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            console.error('获取材料名称失败:', error);
          }
        });
    },
    selectAddMaterialSuggestion(item) {
      this.form.materialName = item;
      this.showAddMaterialDropdown = false;
    },
    handleAddMaterialPageChange(pageNum) {
      if (this.addMaterialCurrentPage !== pageNum) {
        this.addMaterialCurrentPage = pageNum;
        this.fetchAddMaterialSuggestions();
      }
    },
    // 编辑材料联想相关方法（从原材料表获取数据）
    handleEditMaterialInput() {
      this.editMaterialCurrentPage = 1;
      this.fetchEditMaterialSuggestions();
    },
    handleEditMaterialFocus() {
      if (this.editForm.materialName) {
        this.fetchEditMaterialSuggestions();
      }
    },
    handleEditMaterialBlur() {
      setTimeout(() => {
        this.showEditMaterialDropdown = false;
      }, 200);
    },
    fetchEditMaterialSuggestions() {
      const that = this;
      if (that.editForm.materialName.length < 1) {
        that.editMaterialSuggestions = [];
        that.showEditMaterialDropdown = false;
        return;
      }
      this.$axios
        .post(
          `${process.env.VUE_APP_API_BASE_URL}/materialStock/findMaterialNamesByPrefix`,
          {
            prefix: that.editForm.materialName,
            pageNum: that.editMaterialCurrentPage,
            pageSize: that.editMaterialPageSize,
          }
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.editMaterialSuggestions = response.data.data;
            that.editMaterialTotal = response.data.total;
            that.showEditMaterialDropdown = true;
          }
        })
        .catch(function (error) {
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            console.error('获取材料名称失败:', error);
          }
        });
    },
    selectEditMaterialSuggestion(item) {
      this.editForm.materialName = item;
      this.showEditMaterialDropdown = false;
    },
    handleEditMaterialPageChange(pageNum) {
      if (this.editMaterialCurrentPage !== pageNum) {
        this.editMaterialCurrentPage = pageNum;
        this.fetchEditMaterialSuggestions();
      }
    },
    toggleHideZeroStock() {
      this.hideZeroStock = !this.hideZeroStock;
      // 先将MaterialStockData设置为空数组，强制表格组件重新渲染
      this.MaterialStockData = [];
      // 然后调用getAllMaterialStock方法获取新数据
      this.getAllMaterialStock();
    },
    // 处理表格选择变化
    handleSelectionChange(val) {
      this.selectedMaterials = val;
    },
    // 批量删除原材料库存
    batchDeleteMaterialStock() {
      const that = this;
      if (that.selectedMaterials.length === 0) {
        that.$message.warning('请先选择要删除的数据');
        return;
      }
      
      this.$confirm(`确定要删除选中的 ${that.selectedMaterials.length} 条原材料库存数据吗？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 提取选中数据的id列表
        const ids = that.selectedMaterials.map(item => item.id).join(',');
        
        that.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/materialStock/batchDeleteMaterialStock`, {
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
              that.getAllMaterialStock();
              that.selectedMaterials = [];
            } else {
              that.$message.error(response.data.msg);
            }
          }).catch(function (error) {
            // 401错误由响应拦截器处理，不显示错误信息
            if (error.response && error.response.status !== 401) {
              that.$message.error('批量删除失败：' + error);
            }
          });
      }).catch(() => {
        // 取消操作
      });
    },
    // 取消选择
    onClearSelection() {
      this.$refs.multipleTable.clearSelection();
      this.selectedMaterials = [];
    },

  },
};
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
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  border-top: 1px solid var(--border);
}

.material-search-container {
  position: relative;
  width: 300px;
  z-index: 1000;
}

.material-dropdown-container {
  position: absolute;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0,0,0,.1);
  z-index: 9999;
  max-height: 240px;
  overflow-y: auto;
  width: 100%;
}

.material-dropdown-item {
  padding: 9px 14px;
  font-size: .85rem;
  color: var(--text-primary);
  cursor: pointer;
}
.material-dropdown-item:hover { background: #f1f5f9; }

.material-dropdown-footer {
  padding: 8px 12px;
  border-top: 1px solid var(--border);
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

<style>
/* 对话框样式：不使用 scoped，因为 el-dialog 渲染在 body 下 */
.material-dialog {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2) !important;
  border: none !important;
}

.material-dialog .el-dialog__header {
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

.material-dialog .el-dialog__title {
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

.material-dialog .el-dialog__headerbtn {
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

.material-dialog .el-dialog__headerbtn:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.material-dialog .el-dialog__headerbtn .el-icon {
  color: white !important;
  font-size: 16px !important;
  line-height: 1 !important;
}

.material-dialog .el-dialog__headerbtn .el-icon svg {
  fill: white !important;
  width: 16px !important;
  height: 16px !important;
  vertical-align: middle !important;
  margin: 0 !important;
}

.material-dialog .el-dialog__body {
  padding: 30px !important;
  min-height: 300px !important;
  box-sizing: border-box !important;
  background-color: #ffffff !important;
}
</style>
