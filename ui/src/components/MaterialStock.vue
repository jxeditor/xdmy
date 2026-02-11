<template>
  <el-row>
    <el-col :span="24">
      <el-card shadow="never" class="search-card">
        <el-row :gutter="20">
          <el-col :span="16">
            <div style="display: flex; align-items: center; gap: 10px;">
              <el-input
                v-model="searchQuery"
                placeholder="请输入材料名称"
                style="width: 300px"
                @input="handleInput"
                @focus="handleFocus"
                @blur="handleBlur"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <div v-if="showDropdown" class="dropdown-container material-dropdown">
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
            <el-button type="primary" @click="searchStock">搜索</el-button>
            <el-button type="primary" @click='onAddMaterialStock'>添加材料</el-button>
            <el-button type="warning" @click="toggleHideZeroStock">{{ hideZeroStock ? '取消隐藏' : '隐藏' }}</el-button>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" class="data-card">
        <el-table ref="multipleTable" :data="MaterialStockData" style="width: 100%; table-layout: fixed;" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="materialName" label="材料名称" min-width="220" show-overflow-tooltip align="center" />
          <el-table-column label="数量" min-width="120" align="center">
            <template #default="scope">
              <span :style="getQuantityStyle(scope.row.unitstock)">
                {{ scope.row.unitstock }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="200" align="center">
            <template #default="scope">
              <div style="display: flex; gap: 10px; justify-content: center;">
                <el-button size="small" @click="onEditMaterialStock(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="onDeleteMaterialStock(scope.row.id)">
                  删除
                </el-button>
              </div>
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
          <el-button type="danger" @click="batchDeleteMaterialStock" :disabled="selectedMaterials.length === 0">批量删除</el-button>
          <el-button @click="onClearSelection">取消选择</el-button>
        </div>
      </el-card>

      <!-- 添加库存对话框 -->
      <el-dialog v-model="dialogVisible" title="添加材料库存" width="500px">
        <el-form :model="form" label-width="120px">
          <el-form-item label="材料名称">
            <el-input v-model="form.materialName" placeholder="请输入材料名称" />
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
      <el-dialog v-model="editDialogVisible" title="编辑材料库存" width="500px">
        <el-form :model="editForm" label-width="120px">
          <el-form-item label="材料名称">
            <el-input v-model="editForm.materialName" placeholder="请输入材料名称" />
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
    </el-col>
  </el-row>
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
            that.dropdownItems = response.data.data.data;
            that.total = response.data.data.total;
            that.showDropdown = true;
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
            that.$message.error('批量删除失败：' + error);
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

.material-dropdown {
  z-index: 1000;
}
</style>
