<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">{{ msg }}</h2>
      <div class="page-actions" style="display:flex;gap:8px;align-items:center;">
        <el-button type="primary" @click='onAddStock'>添加库存</el-button>
        <el-button type="warning" @click="toggleHideZeroStock">{{ hideZeroStock ? '取消隐藏' : '隐藏' }}</el-button>
        <el-button type="danger" @click="flattenStock">抹平</el-button>
      </div>
    </div>
    <div class="filter-bar">
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
      <el-button type="primary" @click="searchStock">搜索</el-button>
    </div>
    <div class="card" style="padding:0;overflow:hidden;">
      <el-table ref="multipleTable" stripe :data="StockData" style="width: 100%;" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center">
        </el-table-column>
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
        <el-table-column fixed="right" label="操作" align="center" width="220">
          <template #default="opt">
            <el-button type="primary" @click="onUpdateStock(opt.row)">修改</el-button>
            <el-button type="danger" @click="onDeleteStock(opt.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page.index"
          :page-size="page.size"
          layout="total,prev,pager,next"
          :total="page.total"
          @current-change="handleCurrentChange"
        >
        </el-pagination>
      </div>
    </div>
    <div style="margin-top: 20px">
      <el-button type="danger" @click="batchDeleteStock" :disabled="selectedStock.length === 0">批量删除</el-button>
      <el-button @click="onClearSelection">取消选择</el-button>
    </div>
      <el-dialog title="初始化库存" v-model="addStockVisible" width="80%" class="stock-dialog"><br>
        <el-form ref="addStockForm" :rules="addStockFormRules" :model="addStockForm" label-width="120px">
          <el-form-item label="产品:" prop="product">
            <div class="stock-search-container">
              <el-input
                v-model="addStockForm.product"
                placeholder="请输入产品名称"
                style="width: 300px"
                @input="handleAddStockInput"
                @focus="handleAddStockFocus"
                @blur="handleAddStockBlur"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <!-- 添加库存产品联想结果下拉框 -->
              <div
                v-if="showAddStockDropdown && addStockSuggestions.length > 0"
                class="stock-suggestions-dropdown"
              >
                <div class="stock-dropdown-body">
                  <div
                    v-for="(item, index) in addStockSuggestions"
                    :key="index"
                    class="stock-dropdown-item"
                    @mousedown="selectAddStockSuggestion(item)"
                  >
                    {{ item }}
                  </div>
                </div>
                <div
                  v-if="addStockTotal > addStockPageSize"
                  class="stock-dropdown-footer"
                >
                  <el-pagination
                    small
                    layout="prev, pager, next"
                    :total="addStockTotal"
                    :page-size="addStockPageSize"
                    :current-page="addStockCurrentPage"
                    @current-change="handleAddStockPageChange"
                    style="margin: 0; white-space: nowrap;"
                  />
                </div>
              </div>
            </div>
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
      <el-dialog title="修改库存信息" v-model="updateStockVisible" width="80%" class="stock-dialog"><br>
        <el-form ref="updateStockForm" :rules="updateStockFormRules" :model="updateStockForm"
                 label-width="120px">
          <el-form-item label="产品:" prop="product">
            <div class="stock-search-container">
              <el-input
                v-model="updateStockForm.product"
                placeholder="请输入产品名称"
                style="width: 300px"
                @input="handleUpdateStockInput"
                @focus="handleUpdateStockFocus"
                @blur="handleUpdateStockBlur"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <!-- 修改库存产品联想结果下拉框 -->
              <div
                v-if="showUpdateStockDropdown && updateStockSuggestions.length > 0"
                class="stock-suggestions-dropdown"
              >
                <div class="stock-dropdown-body">
                  <div
                    v-for="(item, index) in updateStockSuggestions"
                    :key="index"
                    class="stock-dropdown-item"
                    @mousedown="selectUpdateStockSuggestion(item)"
                  >
                    {{ item }}
                  </div>
                </div>
                <div
                  v-if="updateStockTotal > updateStockPageSize"
                  class="stock-dropdown-footer"
                >
                  <el-pagination
                    small
                    layout="prev, pager, next"
                    :total="updateStockTotal"
                    :page-size="updateStockPageSize"
                    :current-page="updateStockCurrentPage"
                    @current-change="handleUpdateStockPageChange"
                    style="margin: 0; white-space: nowrap;"
                  />
                </div>
              </div>
            </div>
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
            // 401错误由响应拦截器处理，不显示错误信息
            if (error.response && error.response.status !== 401) {
              that.$message.error(error);
            }
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
            // 401错误由响应拦截器处理，不显示错误信息
            if (error.response && error.response.status !== 401) {
              that.$message.error(error);
            }
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
          // 按剩余库存降序排序
          that.StockData.sort(function(a, b) {
            return b.stock - a.stock;
          });
          that.page.total = response.data.total
          console.log('Updated StockData:', that.StockData)
          console.log('Updated page.total:', that.page.total)
          // 强制Vue重新渲染组件，确保表格能够正确响应数据变化
          that.$forceUpdate()
          console.log('Forced update completed')
        }).catch(function (error) {
        console.error('API error:', error)
        // 401错误由响应拦截器处理，不显示错误信息
        if (error.response && error.response.status !== 401) {
          that.$message.error(error);
        }
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
          // 按剩余库存降序排序
          that.StockData.sort(function(a, b) {
            return b.stock - a.stock;
          });
          that.page.total = response.data.total
          console.log('Updated StockData after search:', that.StockData)
          console.log('Updated page.total after search:', that.page.total)
        }).catch(function (error) {
        console.error('Search API error:', error)
        // 401错误由响应拦截器处理，不显示错误信息
        if (error.response && error.response.status !== 401) {
          that.$message.error(error);
        }
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
        // 401错误由响应拦截器处理，不显示错误信息
        if (error.response && error.response.status !== 401) {
          that.suggestOptions = []
          that.showSuggestions = false
        }
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
    // 添加库存联想相关方法（从product表获取数据）
    handleAddStockInput() {
      this.addStockCurrentPage = 1;
      this.fetchAddStockSuggestions();
    },
    handleAddStockFocus() {
      if (this.addStockForm.product) {
        this.fetchAddStockSuggestions();
      }
    },
    handleAddStockBlur() {
      setTimeout(() => {
        this.showAddStockDropdown = false;
      }, 200);
    },
    fetchAddStockSuggestions() {
      const that = this;
      if (that.addStockForm.product.length < 1) {
        that.addStockSuggestions = [];
        that.showAddStockDropdown = false;
        return;
      }
      this.$axios
        .post(
          `${process.env.VUE_APP_API_BASE_URL}/product/findProductNamesByPrefix`,
          {
            prefix: that.addStockForm.product,
            pageNum: that.addStockCurrentPage,
            pageSize: that.addStockPageSize,
          }
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.addStockSuggestions = response.data.data;
            that.addStockTotal = response.data.total;
            that.showAddStockDropdown = true;
          }
        })
        .catch(function (error) {
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            console.error('获取产品名称失败:', error);
          }
        });
    },
    selectAddStockSuggestion(item) {
      this.addStockForm.product = item;
      this.showAddStockDropdown = false;
    },
    handleAddStockPageChange(pageNum) {
      if (this.addStockCurrentPage !== pageNum) {
        this.addStockCurrentPage = pageNum;
        this.fetchAddStockSuggestions();
      }
    },
    // 修改库存联想相关方法（从stock表获取数据）
    handleUpdateStockInput() {
      this.updateStockCurrentPage = 1;
      this.fetchUpdateStockSuggestions();
    },
    handleUpdateStockFocus() {
      if (this.updateStockForm.product) {
        this.fetchUpdateStockSuggestions();
      }
    },
    handleUpdateStockBlur() {
      setTimeout(() => {
        this.showUpdateStockDropdown = false;
      }, 200);
    },
    fetchUpdateStockSuggestions() {
      const that = this;
      if (that.updateStockForm.product.length < 1) {
        that.updateStockSuggestions = [];
        that.showUpdateStockDropdown = false;
        return;
      }
      this.$axios
        .post(
          `${process.env.VUE_APP_API_BASE_URL}/stock/findProductNamesByPrefix`,
          {
            prefix: that.updateStockForm.product,
            pageNum: that.updateStockCurrentPage,
            pageSize: that.updateStockPageSize,
          }
        )
        .then(function (response) {
          if (response.data.code === 1) {
            that.updateStockSuggestions = response.data.data;
            that.updateStockTotal = response.data.total;
            that.showUpdateStockDropdown = true;
          }
        })
        .catch(function (error) {
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            console.error('获取产品名称失败:', error);
          }
        });
    },
    selectUpdateStockSuggestion(item) {
      this.updateStockForm.product = item;
      this.showUpdateStockDropdown = false;
    },
    handleUpdateStockPageChange(pageNum) {
      if (this.updateStockCurrentPage !== pageNum) {
        this.updateStockCurrentPage = pageNum;
        this.fetchUpdateStockSuggestions();
      }
    },
    // 切换隐藏零库存状态
    toggleHideZeroStock() {
      this.hideZeroStock = !this.hideZeroStock
      console.log('Toggle hideZeroStock:', this.hideZeroStock)
      // 先将StockData设置为空数组，强制表格组件重新渲染
      this.StockData = []
      // 然后调用getAllStock方法获取新数据（已包含排序逻辑）
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
                  // 401错误由响应拦截器处理，不显示错误信息
                  if (error.response && error.response.status !== 401) {
                    that.$message.error('库存抹平失败：' + error);
                  }
                })
            }).catch(() => {
              // 取消操作
            })
          } else {
            that.$message.error('获取需要抹平的库存数据数量失败');
          }
        }).catch(function (error) {
          // 401错误由响应拦截器处理，不显示错误信息
          if (error.response && error.response.status !== 401) {
            that.$message.error('获取需要抹平的库存数据数量失败：' + error);
          }
        })
    },
    // 处理表格选择变化
    handleSelectionChange(val) {
      this.selectedStock = val;
    },
    // 单条删除库存
    onDeleteStock(id) {
      const that = this;
      this.$confirm('确定要删除这条库存数据吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        that.$axios.delete(`${process.env.VUE_APP_API_BASE_URL}/stock/deleteStockById?id=${id}`)
          .then(function (response) {
            if (response.data.code === 1) {
              that.$message.success('删除成功');
              that.getAllStock();
            } else {
              that.$message.error(response.data.msg);
            }
          }).catch(function (error) {
            // 401错误由响应拦截器处理，不显示错误信息
            if (error.response && error.response.status !== 401) {
              that.$message.error('删除失败：' + error);
            }
          });
      }).catch(() => {
        // 取消操作
      });
    },
    // 批量删除库存
    batchDeleteStock() {
      const that = this;
      if (that.selectedStock.length === 0) {
        that.$message.warning('请先选择要删除的数据');
        return;
      }
      
      this.$confirm(`确定要删除选中的 ${that.selectedStock.length} 条库存数据吗？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 提取选中数据的id列表
        const ids = that.selectedStock.map(item => item.id).join(',');
        
        that.$axios.delete(`${process.env.VUE_APP_API_BASE_URL}/stock/batchDeleteStock?ids=${ids}`)
          .then(function (response) {
            if (response.data.code === 1) {
              that.$message.success('批量删除成功');
              that.getAllStock();
              that.selectedStock = [];
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
      this.selectedStock = [];
    },
    // 点击外部区域关闭下拉框
    handleClickOutside(event) {
      // 检查点击是否发生在搜索框或下拉框外部
      const searchContainer = document.querySelector('.stock-search-container')
      if (searchContainer && !searchContainer.contains(event.target)) {
        this.showSuggestions = false
      }
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
  mounted() {
    // 添加点击事件监听器，点击页面其他地方时隐藏下拉框
    document.addEventListener('click', this.handleClickOutside)
    // 组件挂载后自动获取库存数据
    this.getAllStock()
  },
  beforeDestroy() {
    // 移除点击事件监听器
    document.removeEventListener('click', this.handleClickOutside)
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
      // 添加库存联想相关数据（从product表获取）
      addStockSuggestions: [],
      showAddStockDropdown: false,
      addStockCurrentPage: 1,
      addStockPageSize: 10,
      addStockTotal: 0,
      // 修改库存联想相关数据（从stock表获取）
      updateStockSuggestions: [],
      showUpdateStockDropdown: false,
      updateStockCurrentPage: 1,
      updateStockPageSize: 10,
      updateStockTotal: 0,
      // 隐藏零库存状态
      hideZeroStock: false,
      // 选中的库存数据
      selectedStock: []
    }
  }
}
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
.stock-search-container {
  position: relative;
}
.stock-suggestions-dropdown {
  position: absolute;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0,0,0,.1);
  z-index: 9999;
  max-height: 240px;
  overflow-y: auto;
}
.stock-suggestion-item {
  padding: 9px 14px;
  font-size: .85rem;
  color: var(--text-primary);
  cursor: pointer;
}
.stock-suggestion-item:hover { background: #f1f5f9; }
</style>

<style>
/* 对话框样式：不使用 scoped，因为 el-dialog 渲染在 body 下 */
.stock-dialog {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2) !important;
  border: none !important;
}

.stock-dialog .el-dialog__header {
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

.stock-dialog .el-dialog__title {
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

.stock-dialog .el-dialog__headerbtn {
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

.stock-dialog .el-dialog__headerbtn:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.stock-dialog .el-dialog__headerbtn .el-icon {
  color: white !important;
  font-size: 16px !important;
  line-height: 1 !important;
}

.stock-dialog .el-dialog__headerbtn .el-icon svg {
  fill: white !important;
  width: 16px !important;
  height: 16px !important;
  vertical-align: middle !important;
  margin: 0 !important;
}

.stock-dialog .el-dialog__body {
  padding: 30px !important;
  min-height: 300px !important;
  box-sizing: border-box !important;
  background-color: #ffffff !important;
}
</style>