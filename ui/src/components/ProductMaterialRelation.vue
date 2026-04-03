<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">{{ msg }}</h2>
      <div class="page-actions" style="display:flex;gap:8px;align-items:center;">
        <el-button type="primary" @click="onAddRelation">添加关系</el-button>
      </div>
    </div>
    <div class="filter-bar">
      <div class="relation-search-container">
        <el-input
          v-model="productInput"
          placeholder="输入产品名"
          clearable
          style="width: 100%;"
          @input="handleProductInput"
          @focus="showProductSuggestions = true"
          @blur="handleProductBlur"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <!-- 产品联想结果下拉框 -->
        <div v-if="showProductSuggestions && productSuggestions.length > 0" class="relation-suggestions-dropdown">
          <div
            v-for="(item, index) in productSuggestions"
            :key="index"
            class="relation-suggestion-item"
            @click="selectProductSuggestion(item)"
          >
            {{ item }}
          </div>
        </div>
      </div>
      <el-button type="primary" @click="searchRelation">搜索</el-button>
    </div>
    <div class="card" style="padding:0;overflow:hidden;">
      <el-table ref="multipleTable" stripe :data="RelationData" style="width: 100%; table-layout: fixed;"
                @selection-change="handleSelectionChange">
        <el-table-column type="selection">
        </el-table-column>
        <el-table-column prop="id" label="UID" min-width="60" align="center">
        </el-table-column>
        <el-table-column prop="productName" label="产品名称" min-width="220" align="center" show-overflow-tooltip>
        </el-table-column>
        <el-table-column prop="materialName" label="原材料名称" min-width="220" align="center" show-overflow-tooltip>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" min-width="100" align="center">
        </el-table-column>
        <el-table-column prop="isDefault" label="是否默认" min-width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isDefault === 1 ? 'success' : 'info'">
              {{ scope.row.isDefault === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" min-width="180">
          <template #default="opt">
            <el-button type="primary" @click="onUpdateRelation(opt.row)">修改</el-button>
            <el-button type="danger" @click="onDeleteRelation(opt.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page.index"
          :page-size="page.size"
          layout="total,prev,pager,next"
          :total="page.total"
          @current-change="handleCurrentChange">
        </el-pagination>
      </div>
    </div>
    <div style="margin-top: 20px">
      <el-button type="danger" @click="batchDeleteRelation" :disabled="selectedRelations.length === 0">批量删除</el-button>
      <el-button @click="onClearSelection">取消选择</el-button>
    </div>
      
      <!-- 添加关系对话框 -->
      <el-dialog title="添加产品与原材料关系" v-model="addRelationVisible" width="80%" append-to-body>
        <el-form ref="addRelationForm" :rules="addRelationFormRules" :model="addRelationForm" label-width="120px">
          <el-form-item label="产品:" prop="productName">
            <div class="relation-search-container">
              <el-input v-model="addRelationForm.productName" placeholder="请输入产品名称" @input="getAddProductSuggestions"></el-input>
              <!-- 产品联想结果下拉框 -->
              <div v-if="showAddProductSuggestions && addProductSuggestions.length > 0" class="relation-suggestions-dropdown">
                <div 
                  v-for="(item, index) in addProductSuggestions" 
                  :key="index"
                  class="relation-suggestion-item"
                  @click="selectAddProductSuggestion(item)"
                >
                  {{ item }}
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="原材料:" prop="materialName">
            <div class="relation-search-container">
              <el-input v-model="addRelationForm.materialName" placeholder="请输入原材料名称" @input="getAddMaterialSuggestions"></el-input>
              <!-- 原材料联想结果下拉框 -->
              <div v-if="showAddMaterialSuggestions && addMaterialSuggestions.length > 0" class="relation-suggestions-dropdown">
                <div 
                  v-for="(item, index) in addMaterialSuggestions" 
                  :key="index"
                  class="relation-suggestion-item"
                  @click="selectAddMaterialSuggestion(item)"
                >
                  {{ item }}
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="数量:" prop="quantity">
            <el-input-number v-model="addRelationForm.quantity" :min="1" style="width: 100%;"></el-input-number>
          </el-form-item>
          <el-form-item label="是否默认:" prop="isDefault">
            <el-switch v-model="addRelationForm.isDefault" :active-value="1" :inactive-value="0"></el-switch>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onAddRelationCommit(`addRelationForm`)">确定</el-button>
            <el-button @click="onAddRelationCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      
      <!-- 修改关系对话框 -->
      <el-dialog title="修改产品与原材料关系" v-model="updateRelationVisible" width="80%" append-to-body>
        <el-form ref="updateRelationForm" :rules="updateRelationFormRules" :model="updateRelationForm" label-width="120px">
          <el-form-item label="产品:" prop="productName">
            <div class="search-container">
              <el-input v-model="updateRelationForm.productName" placeholder="请输入产品名称" @input="getUpdateProductSuggestions"></el-input>
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
          <el-form-item label="原材料:" prop="materialName">
            <div class="search-container">
              <el-input v-model="updateRelationForm.materialName" placeholder="请输入原材料名称" @input="getUpdateMaterialSuggestions"></el-input>
              <!-- 原材料联想结果下拉框 -->
              <div v-if="showUpdateMaterialSuggestions && updateMaterialSuggestions.length > 0" class="suggestions-dropdown">
                <div 
                  v-for="(item, index) in updateMaterialSuggestions" 
                  :key="index"
                  class="suggestion-item"
                  @click="selectUpdateMaterialSuggestion(item)"
                >
                  {{ item }}
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="数量:" prop="quantity">
            <el-input-number v-model="updateRelationForm.quantity" :min="1" style="width: 100%;"></el-input-number>
          </el-form-item>
          <el-form-item label="是否默认:" prop="isDefault">
            <el-switch v-model="updateRelationForm.isDefault" :active-value="1" :inactive-value="0"></el-switch>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onUpdateRelationCommit(`updateRelationForm`)">确定</el-button>
            <el-button @click="onUpdateRelationCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
  </div>
</template>

<script>
import { Search } from '@element-plus/icons-vue';

export default {
  name: "ProductMaterialRelation",
  components: {
    Search
  },
  data() {
    return {
      msg: "产品与原材料关系维护",
      page: {
        index: 1,
        size: 10,
        total: 0
      },
      RelationData: [],
      productInput: "",
      addRelationVisible: false,
      updateRelationVisible: false,
      addRelationForm: {
        productName: "",
        materialName: "",
        quantity: 1,
        isDefault: 1
      },
      updateRelationForm: {
        id: "",
        productName: "",
        materialName: "",
        quantity: 1,
        isDefault: 0
      },
      addRelationFormRules: {
        productName: [
          {required: true, message: "请输入产品名称", trigger: "blur"}
        ],
        materialName: [
          {required: true, message: "请输入原材料名称", trigger: "blur"}
        ],
        quantity: [
          {required: true, message: "请输入数量", trigger: "blur"}
        ]
      },
      updateRelationFormRules: {
        productName: [
          {required: true, message: "请输入产品名称", trigger: "blur"}
        ],
        materialName: [
          {required: true, message: "请输入原材料名称", trigger: "blur"}
        ],
        quantity: [
          {required: true, message: "请输入数量", trigger: "blur"}
        ]
      },
      // 添加产品联想功能参数
      addProductSuggestions: [],
      showAddProductSuggestions: false,
      // 添加原材料联想功能参数
      addMaterialSuggestions: [],
      showAddMaterialSuggestions: false,
      // 修改产品联想功能参数
      updateProductSuggestions: [],
      showUpdateProductSuggestions: false,
      // 修改原材料联想功能参数
      updateMaterialSuggestions: [],
      showUpdateMaterialSuggestions: false,
      // 产品筛选联想功能参数
      productSuggestions: [],
      showProductSuggestions: false,
      // 选中的关系数据
      selectedRelations: []
    };
  },
  mounted() {
    this.getAllRelation();
    // 添加点击事件监听器，点击页面其他地方时隐藏下拉框
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeDestroy() {
    // 移除点击事件监听器
    document.removeEventListener('click', this.handleClickOutside)
  },
  methods: {
    handleCurrentChange(currentPage) {
      this.page.index = currentPage;
      this.getAllRelation();
    },
    // 处理表格选择变化
    handleSelectionChange(val) {
      this.selectedRelations = val;
    },
    // 批量删除关系
    batchDeleteRelation() {
      const that = this;
      if (that.selectedRelations.length === 0) {
        that.$message.warning('请先选择要删除的数据');
        return;
      }
      
      this.$confirm(`确定要删除选中的 ${that.selectedRelations.length} 条关系数据吗？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 提取选中数据的id列表
        const ids = that.selectedRelations.map(item => item.id).join(',');
        
        that.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/batchDeleteRelation`, {
          params: {
            ids: ids
          }
        })
          .then(function (response) {
            if (response.data.code === 1) {
              that.$message.success('批量删除成功');
              that.getAllRelation();
              that.selectedRelations = [];
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
      this.selectedRelations = [];
    },

    searchRelation() {
      this.page.index = 1;
      this.getAllRelation();
    },
    onAddRelationCancel() {
      this.addRelationVisible = false;
    },
    onUpdateRelationCancel() {
      this.updateRelationVisible = false;
    },
    onAddRelation() {
      this.addRelationVisible = true;
    },
    onUpdateRelation(relation) {
      this.updateRelationForm = relation;
      this.updateRelationVisible = true;
    },
    onAddRelationCommit(addRelationForm) {
      const that = this;
      this.$refs[addRelationForm].validate((valid) => {
        if (valid) {
          // 检查产品名称+原材料名称是否唯一
          this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/checkRelationUnique`, {
            params: {
              productName: this.addRelationForm.productName,
              materialName: this.addRelationForm.materialName
            }
          })
          .then(function (checkResponse) {
            if (checkResponse.data.code === 1) {
              // 唯一，继续添加
              let param = new URLSearchParams();
              param.append(`productName`, that.addRelationForm.productName);
              param.append(`materialName`, that.addRelationForm.materialName);
              param.append(`quantity`, that.addRelationForm.quantity);
              param.append(`isDefault`, that.addRelationForm.isDefault);
              that.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/addRelation`, param)
                .then(function (response) {
                  if (response.data.code === 1) {
                    that.addRelationVisible = false;
                    that.getAllRelation();
                  } else {
                    that.$message.error(response.data.msg);
                  }
                })
                .catch(function (error) {
                  // 401错误由响应拦截器处理，不显示错误信息
                  if (error.response && error.response.status !== 401) {
                    that.$message.error(error);
                  }
                });
            } else {
              // 不唯一，提示错误
              that.$message.error(checkResponse.data.msg);
            }
          })
          .catch(function (error) {
            // 401错误由响应拦截器处理，不显示错误信息
            if (error.response && error.response.status !== 401) {
              that.$message.error('检查关系唯一性失败');
            }
          });
        } else {
          return false;
        }
      });
    },
    onUpdateRelationCommit(updateRelationForm) {
      const that = this;
      this.$refs[updateRelationForm].validate((valid) => {
        if (valid) {
          // 检查产品名称+原材料名称是否唯一（排除当前记录）
          this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/checkRelationUnique`, {
            params: {
              productName: this.updateRelationForm.productName,
              materialName: this.updateRelationForm.materialName,
              id: this.updateRelationForm.id
            }
          })
          .then(function (checkResponse) {
            if (checkResponse.data.code === 1) {
              // 唯一，继续更新
              let param = new URLSearchParams();
              param.append(`id`, that.updateRelationForm.id);
              param.append(`productName`, that.updateRelationForm.productName);
              param.append(`materialName`, that.updateRelationForm.materialName);
              param.append(`quantity`, that.updateRelationForm.quantity);
              param.append(`isDefault`, that.updateRelationForm.isDefault);
              that.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/updateRelation`, param)
                .then(function (response) {
                  if (response.data.code === 1) {
                    that.updateRelationVisible = false;
                    that.getAllRelation();
                  } else {
                    that.$message.error(response.data.msg);
                  }
                })
                .catch(function (error) {
                  // 401错误由响应拦截器处理，不显示错误信息
                  if (error.response && error.response.status !== 401) {
                    that.$message.error(error);
                  }
                });
            } else {
              // 不唯一，提示错误
              that.$message.error(checkResponse.data.msg);
            }
          })
          .catch(function (error) {
            // 401错误由响应拦截器处理，不显示错误信息
            if (error.response && error.response.status !== 401) {
              that.$message.error('检查关系唯一性失败');
            }
          });
        } else {
          return false;
        }
      });
    },
    onDeleteRelation(id) {
      const that = this;
      this.$confirm("此操作将永久删除该关系, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(function () {
          that.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/deleteRelationById?id=` + id)
            .then(function (response) {
              if (response.data.code === 1) {
                that.$message({
                  type: "success",
                  message: "删除成功!"
                });
                that.getAllRelation();
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
            message: "已取消删除"
          });
        });
    },
    getAllRelation() {
      const that = this;
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/findAllRelation` +
        `?pageNum=` + that.page.index + `&pageSize=` + that.page.size +
        `&productName=` + that.productInput)
        .then(function (response) {
          that.RelationData = response.data.data;
          that.page.total = response.data.total;
        })
        .catch(function (error) {
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            that.$message.error(error);
          }
        });
    },
    handleProductInput() {
      this.page.index = 1;
      this.getAllRelation();
      this.getProductSuggestions();
    },
    // 获取产品联想建议（从原材料关系表中获取）
    getProductSuggestions() {
      const that = this;
      if (that.productInput.length < 1) {
        that.productSuggestions = [];
        that.showProductSuggestions = false;
        return;
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/productMaterialRelation/findProductNamesByPrefix`, {
        prefix: that.productInput,
        pageNum: 1,
        pageSize: 10
      })
        .then(function (response) {
          that.productSuggestions = response.data.data;
          that.showProductSuggestions = true;
        })
        .catch(function (error) {
          console.error(error);
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            that.productSuggestions = [];
            that.showProductSuggestions = false;
          }
        });
    },
    // 选择产品联想建议
    selectProductSuggestion(item) {
      this.productInput = item;
      this.showProductSuggestions = false;
      this.page.index = 1;
      this.getAllRelation();
    },
    // 处理产品筛选输入框失焦事件
    handleProductBlur() {
      setTimeout(() => {
        this.showProductSuggestions = false;
      }, 200);
    },
    // 获取添加产品联想建议
    getAddProductSuggestions() {
      const that = this;
      if (that.addRelationForm.productName.length < 1) {
        that.addProductSuggestions = [];
        that.showAddProductSuggestions = false;
        return;
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/findProductNamesByPrefix`, {
        prefix: that.addRelationForm.productName,
        pageNum: 1,
        pageSize: 10
      })
        .then(function (response) {
          that.addProductSuggestions = response.data.data;
          that.showAddProductSuggestions = true;
        })
        .catch(function (error) {
          console.error(error);
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            that.addProductSuggestions = [];
            that.showAddProductSuggestions = false;
          }
        });
    },
    // 获取添加原材料联想建议
    getAddMaterialSuggestions() {
      const that = this;
      if (that.addRelationForm.materialName.length < 1) {
        that.addMaterialSuggestions = [];
        that.showAddMaterialSuggestions = false;
        return;
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/materialStock/findMaterialNamesByPrefix`, {
        prefix: that.addRelationForm.materialName,
        pageNum: 1,
        pageSize: 10
      })
        .then(function (response) {
          that.addMaterialSuggestions = response.data.data;
          that.showAddMaterialSuggestions = true;
        })
        .catch(function (error) {
          console.error(error);
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            that.addMaterialSuggestions = [];
            that.showAddMaterialSuggestions = false;
          }
        });
    },
    // 选择添加产品联想建议
    selectAddProductSuggestion(item) {
      this.addRelationForm.productName = item;
      this.showAddProductSuggestions = false;
    },
    // 选择添加原材料联想建议
    selectAddMaterialSuggestion(item) {
      this.addRelationForm.materialName = item;
      this.showAddMaterialSuggestions = false;
    },
    // 获取修改产品联想建议
    getUpdateProductSuggestions() {
      const that = this;
      if (that.updateRelationForm.productName.length < 1) {
        that.updateProductSuggestions = [];
        that.showUpdateProductSuggestions = false;
        return;
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/stock/findProductNamesByPrefix`, {
        prefix: that.updateRelationForm.productName,
        pageNum: 1,
        pageSize: 10
      })
        .then(function (response) {
          that.updateProductSuggestions = response.data.data;
          that.showUpdateProductSuggestions = true;
        })
        .catch(function (error) {
          console.error(error);
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            that.updateProductSuggestions = [];
            that.showUpdateProductSuggestions = false;
          }
        });
    },
    // 获取修改原材料联想建议
    getUpdateMaterialSuggestions() {
      const that = this;
      if (that.updateRelationForm.materialName.length < 1) {
        that.updateMaterialSuggestions = [];
        that.showUpdateMaterialSuggestions = false;
        return;
      }
      this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/materialStock/findMaterialNamesByPrefix`, {
        prefix: that.updateRelationForm.materialName,
        pageNum: 1,
        pageSize: 10
      })
        .then(function (response) {
          that.updateMaterialSuggestions = response.data.data;
          that.showUpdateMaterialSuggestions = true;
        })
        .catch(function (error) {
          console.error(error);
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            that.updateMaterialSuggestions = [];
            that.showUpdateMaterialSuggestions = false;
          }
        });
    },
    // 选择修改产品联想建议
    selectUpdateProductSuggestion(item) {
      this.updateRelationForm.productName = item;
      this.showUpdateProductSuggestions = false;
    },
    // 选择修改原材料联想建议
    selectUpdateMaterialSuggestion(item) {
      this.updateRelationForm.materialName = item;
      this.showUpdateMaterialSuggestions = false;
    },
    // 点击外部区域关闭下拉框
    handleClickOutside(event) {
      // 检查点击是否发生在任何搜索框或下拉框外部
      const searchContainers = document.querySelectorAll('.relation-search-container');
      let isClickInside = false;
      
      searchContainers.forEach(container => {
        if (container.contains(event.target)) {
          isClickInside = true;
        }
      });
      
      if (!isClickInside) {
        this.showAddProductSuggestions = false;
        this.showAddMaterialSuggestions = false;
        this.showUpdateProductSuggestions = false;
        this.showUpdateMaterialSuggestions = false;
        this.showProductSuggestions = false;
      }
    }
  }
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

/* 联想下拉（若有）*/
.relation-search-container {
  position: relative;
  width: 300px;
}
.relation-suggestions-dropdown {
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
.relation-suggestion-item {
  padding: 9px 14px;
  font-size: .85rem;
  color: var(--text-primary);
  cursor: pointer;
}
.relation-suggestion-item:hover { background: #f1f5f9; }
</style>
