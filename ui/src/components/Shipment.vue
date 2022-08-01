<template>
  <div id="shipment">
    <h1>{{ msg }}</h1>
    <div id="app">
      <el-row style="width:100%;padding: 20px;">
        <el-col :span="10">
          <el-date-picker v-model="billDateInput"
                          type="daterange"
                          range-separator="至"
                          start-placeholder="开始日期"
                          end-placeholder="结束日期"
                          value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="2">
          <el-input v-model="customerInput" placeholder="输入客户名"/>
        </el-col>
        <el-col :span="2"></el-col>
        <el-col :span="2">
          <el-input v-model="productInput" placeholder="输入产品名"/>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click='searchShipment'>搜索</el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click='onAddShipment'>开单</el-button>
        </el-col>
      </el-row>
      <el-table ref="multipleTable" stripe :data="ShipmentData" style="width: 100%;"
                @selection-change="handleSelectionChange">
        <el-table-column type="selection">
        </el-table-column>
        <el-table-column prop="id" label="UID" align="center">
        </el-table-column>
        <el-table-column prop="odd" label="单号" align="center">
        </el-table-column>
        <el-table-column prop="customer" label="客户" width="120px" align="center">
        </el-table-column>
        <el-table-column prop="product" label="产品" width="240px" align="center">
        </el-table-column>
        <el-table-column prop="billdate" label="日期" width="120px" align="center">
        </el-table-column>
        <el-table-column prop="amount" label="数量" width="80" align="center">
        </el-table-column>
        <el-table-column prop="unitprice" label="单价" width="80" align="center">
        </el-table-column>
        <el-table-column prop="money" label="金额" width="80" align="center">
        </el-table-column>
        <el-table-column prop="paystatus" :formatter="setPayStatus" label="付款状态" width="100" align="center">
        </el-table-column>
        <el-table-column prop="boardcost" label="夹板成本" width="100" align="center">
        </el-table-column>
        <el-table-column prop="fireproofboardcost" label="防火板成本" width="100" align="center">
        </el-table-column>
        <el-table-column prop="costmoney" label="成本" width="100" align="center">
        </el-table-column>
        <el-table-column prop="profit" label="利润" width="100" align="center">
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
            <el-input v-model="addShipmentForm.customer"></el-input>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <el-input v-model="addShipmentForm.product"></el-input>
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
          <el-form-item>
            <el-button type="primary" @click="onAddShipmentCommit('addShipmentForm')">确定</el-button>
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
            <el-input v-model="updateShipmentForm.customer"></el-input>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <el-input v-model="updateShipmentForm.product"></el-input>
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
          <el-form-item>
            <el-button type="primary" @click="onUpdateShipmentCommit('updateShipmentForm')">确定</el-button>
            <el-button @click="onUpdateShipmentCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </div>
  </div>
</template>


<script>
export default {
  name: 'ShipmentScript',
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
          this.$axios.get('http://124.223.70.175:8088/shipment/updatePaystatusShipmentById?id=' + data.id)
            .catch(function (error) {
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
          this.$axios.get('http://124.223.70.175:8088/shipment/deleteShipmentById?id=' + data.id)
            .catch(function (error) {
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
          param.append('odd', this.addShipmentForm.odd)
          param.append('customer', this.addShipmentForm.customer)
          param.append('product', this.addShipmentForm.product)
          param.append('billdate', this.addShipmentForm.billdate)
          param.append('amount', this.addShipmentForm.amount)
          param.append('unitprice', this.addShipmentForm.unitprice)
          param.append('paystatus', this.addShipmentForm.paystatus)
          param.append('boardcost', this.addShipmentForm.boardcost)
          param.append('fireproofboardcost', this.addShipmentForm.fireproofboardcost)
          this.$axios.post('http://124.223.70.175:8088/shipment/addShipment', param).then(function (response) {
            if (response.data.code === 1) {
              that.addShipmentVisible = false
              that.getAllShipment()
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
    onUpdateShipmentCommit(updateShipmentForm) {
      const that = this
      this.$refs[updateShipmentForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append('id', this.updateShipmentForm.id)
          param.append('odd', this.updateShipmentForm.odd)
          param.append('customer', this.updateShipmentForm.customer)
          param.append('product', this.updateShipmentForm.product)
          param.append('billdate', this.updateShipmentForm.billdate)
          param.append('amount', this.updateShipmentForm.amount)
          param.append('unitprice', this.updateShipmentForm.unitprice)
          param.append('paystatus', this.updateShipmentForm.paystatus)
          param.append('boardcost', this.updateShipmentForm.boardcost)
          param.append('fireproofboardcost', this.updateShipmentForm.fireproofboardcost)
          this.$axios.post('http://124.223.70.175:8088/shipment/updateShipment', param).then(function (response) {
            if (response.data.code === 1) {
              that.updateShipmentVisible = false
              that.getAllShipment()
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
    onDeleteShipment(id) {
      const that = this;
      this.$axios.get('http://124.223.70.175:8088/shipment/deleteShipmentById?id=' + id)
        .then(function (response) {
          if (response.data.code === 1) {
            that.getAllShipment()
          } else {
            that.$message.error(response.data.msg);
          }
        }).catch(function (error) {
      })
    },
    getAllShipment() {
      const that = this;
      this.$axios.get('http://124.223.70.175:8088/shipment/findAllShipment' +
        '?pageNum=' + that.page.index + '&pageSize=' + that.page.size +
        '&customerName=' + that.customerInput +
        '&productName=' + that.productInput +
        '&bizStartDate=' + that.billDateInput[0] + '&bizEndDate=' + that.billDateInput[1])
        .then(function (response) {
          that.ShipmentData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
      })
    },
    searchShipment() {
      const that = this
      this.$axios.get('http://124.223.70.175:8088/shipment/findAllShipment' +
        '?pageNum=' + that.page.index + '&pageSize=' + that.page.size +
        '&customerName=' + that.customerInput +
        '&productName=' + that.productInput +
        '&bizStartDate=' + that.billDateInput[0] + '&bizEndDate=' + that.billDateInput[1])
        .then(function (response) {
          that.ShipmentData = response.data.data
          that.page.total = response.data.total
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
        size: 10,
        // 总条数
        total: 0
      },
      multipleSelection: [],
      ShipmentData: [],
      customerInput: '',
      productInput: '',
      billDateInput: '',
      addShipmentVisible: false,
      updateShipmentVisible: false,
      addShipmentForm: {
        odd: '',
        customer: '',
        product: '',
        billdate: '',
        amount: 0,
        unitprice: 0,
        paystatus: '0',
        boardcost: 0,
        fireproofboardcost: 0
      },
      updateShipmentForm: {
        odd: '',
        customer: '',
        product: '',
        billdate: '',
        amount: 0,
        unitprice: 0,
        paystatus: '0',
        boardcost: 0,
        fireproofboardcost: 0
      },
      addShipmentFormRules: {
        odd: [
          {required: true, message: '请输入单号', trigger: 'blur'}
        ],
        customer: [
          {required: true, message: '请输入客户名', trigger: 'blur'},
        ],
        product: [
          {required: true, message: '请输入产品名', trigger: 'blur'},
        ],
        billdate: [
          {
            type: 'date', required: true, message: '请选择日期', trigger: 'change'
          }
        ],
        amount: [
          {required: true, message: '请输入数量', trigger: 'blur'},
        ],
        unitprice: [
          {required: true, message: '请输入单价', trigger: 'blur'},
        ],
        paystatus: [
          {required: true, message: '请选择付款状态', trigger: 'blur'},
        ],
        boardcost: [
          {required: true, message: '请输入夹板成本', trigger: 'blur'},
        ],
        fireproofboardcost: [
          {required: true, message: '请输入防火板成本', trigger: 'blur'},
        ]
      },
      updateShipmentFormRules: {
        odd: [
          {required: true, message: '请输入单号', trigger: 'blur'}
        ],
        customer: [
          {required: true, message: '请输入客户名', trigger: 'blur'},
        ],
        product: [
          {required: true, message: '请输入产品名', trigger: 'blur'},
        ],
        billdate: [
          {
            type: 'date', required: true, message: '请选择日期', trigger: 'change'
          }
        ],
        amount: [
          {required: true, message: '请输入数量', trigger: 'blur'},
        ],
        unitprice: [
          {required: true, message: '请输入单价', trigger: 'blur'},
        ],
        paystatus: [
          {required: true, message: '请选择付款状态', trigger: 'blur'},
        ],
        boardcost: [
          {required: true, message: '请输入夹板成本', trigger: 'blur'},
        ],
        fireproofboardcost: [
          {required: true, message: '请输入防火板成本', trigger: 'blur'},
        ]
      }
    }
  }
}
</script>

<style>
#shipment {
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
