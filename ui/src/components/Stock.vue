<template>
  <div id="stock">
    <h1>{{ msg }}</h1>
    <div id="app">
      <el-row style="padding: 20px;">
        <el-col :span="6">
          <el-input v-model="productInput" placeholder="输入产品名"/>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click='searchStock'>搜索</el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click='onAddStock'>添加库存</el-button>
        </el-col>
      </el-row>
      <el-table ref="multipleTable" stripe :data="StockData" style="width: 100%;">
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
        <el-table-column fixed="right" label="操作" align="center" width="180">
          <template #default="opt">
            <el-button type="primary" @click="onUpdateStock(opt.row)">修改</el-button>
            <!-- <el-button type="danger" @click="onDeleteStock(opt.row.id)">删除</el-button> -->
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
      <el-dialog title="初始化库存" v-model="addStockVisible" width="80%">
        <el-form ref="addStockForm" :rules="addStockFormRules" :model="addStockForm" label-width="120px">
          <el-form-item label="产品:" prop="product">
            <el-input v-model="addStockForm.product"></el-input>
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
      <el-dialog title="修改库存信息" v-model="updateStockVisible" width="80%">
        <el-form ref="updateStockForm" :rules="updateStockFormRules" :model="updateStockForm"
                 label-width="120px">
          <el-form-item label="产品:" prop="product">
            <el-input v-model="updateStockForm.product"></el-input>
          </el-form-item>
          <el-form-item label="初始库存数量:" prop="unitstock">
            <el-input v-model="updateStockForm.unitstock"></el-input>
          </el-form-item>
          <el-form-item label="单价:" prop="unitprice">
            <el-input v-model="updateStockForm.unitprice"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onUpdateStockCommit('updateStockForm')">确定</el-button>
            <el-button @click="onUpdateStockCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </div>
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
    sleep(ms) { //sleep延迟方法2
      const time_ms = new Date().getTime();
      while (new Date().getTime() < time_ms + ms) {
      }
    },
    setStockStatus(row) {
      switch (row.stockstatus) {
        case "0":
          return "未初始化";
        case "1":
          return "已初始化";
        default:
          return "未定义";
      }
    },
    handleCurrentChange(currentPage) {
      this.page.index = currentPage;
      this.getAllShipment()
    },
    onAddStockCancel() {
      this.addStockVisible = false
    },
    onUpdateStockCancel() {
      this.updateStockVisible = false
    },
    onAddStock() {
      this.addStockVisible = true
    },
    onUpdateStock(stock) {
      this.updateStockForm = stock
      this.updateStockVisible = true
    },
    onAddStockCommit(addStockForm) {
      const that = this
      this.$refs[addStockForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append('product', this.addStockForm.product)
          param.append('unitstock', this.addStockForm.unitstock)
          param.append('unitprice', this.addStockForm.unitprice)
          this.$axios.post('http://124.223.70.175:8088/stock/addStock', param).then(function (response) {
            if (response.data.code === 1) {
              that.addStockVisible = false
              that.getAllStock()
            } else {
              that.$message.error(response.data.msg);
            }
          }).catch(function (error) {
          })
        } else {
          return false
        }
      })
    },
    onUpdateStockCommit(updateStockForm) {
      const that = this
      this.$refs[updateStockForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append('id', this.updateStockForm.id)
          param.append('product', this.updateStockForm.product)
          param.append('unitstock', this.updateStockForm.unitstock)
          param.append('unitprice', this.updateStockForm.unitprice)
          this.$axios.post('http://124.223.70.175:8088/stock/updateStock', param).then(function (response) {
            if (response.data.code === 1) {
              that.updateStockVisible = false
              that.getAllStock()
            } else {
              that.$message.error(response.data.msg);
            }
          }).catch(function (error) {
          })
        } else {
          return false
        }
      })
    },
    onDeleteStock(id) {
      const that = this;
      this.$axios.get('http://124.223.70.175:8088/stock/deleteStockById?id=' + id)
        .then(function (response) {
          if (response.data.code === 1) {
            that.getAllStock()
          } else {
            that.$message.error(response.data.msg);
          }
        }).catch(function (error) {
      })
    },
    getAllStock() {
      const that = this;
      this.$axios.get('http://124.223.70.175:8088/stock/findAllStock' +
        '?pageNum=' + that.page.index + '&pageSize=' + that.page.size +
        '&productName=' + that.productInput)
        .then(function (response) {
          that.StockData = response.data.data
        }).catch(function (error) {
      })
    },
    searchStock() {
      const that = this
      this.$axios.get('http://124.223.70.175:8088/stock/findAllStock' +
        '?pageNum=' + that.page.index + '&pageSize=' + that.page.size +
        '&productName=' + that.productInput)
        .then(function (response) {
          that.StockData = response.data.data
        }).catch(function (error) {
      })
    },
  },
  data() {
    return {
      page: {
        //当前页码
        index: 1,
        //每页展示数据
        size: 20,
        // 总条数
        total: 0
      },
      StockData: [],
      stockstatus: '0',
      productInput: '',
      addStockVisible: false,
      updateStockVisible: false,
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
      }
    }
  }
}
</script>

<style>
#stock {
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
</style>

