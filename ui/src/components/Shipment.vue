<template>
  <div id="shipment">
    <h1>{{ msg }}</h1>
    <div id="app">
      <el-row style="padding: 20px;">
        <el-col :span="10">
          <el-date-picker v-model="billDateInput"
                          type="daterange"
                          range-separator="至"
                          start-placeholder="开始日期"
                          end-placeholder="结束日期"
                          value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="6">
          <el-input v-model="customerInput" placeholder="输入客户名"/>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click='searchOrder'>搜索</el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click='onAddOrder'>开单</el-button>
        </el-col>
      </el-row>
      <el-table ref="multipleTable" stripe border :data="orderData" style="width: 100%;"
                @selection-change="handleSelectionChange">
        <el-table-column type="selection">
        </el-table-column>
        <el-table-column prop="id" label="UID" width="80" align="center">
        </el-table-column>
        <el-table-column prop="odd" label="单号" width="100" align="center">
        </el-table-column>
        <el-table-column prop="customer" label="客户" width="120" align="center">
        </el-table-column>
        <el-table-column prop="product" label="产品" width="180" align="center">
        </el-table-column>
        <el-table-column prop="billdate" label="日期" width="100" align="center">
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
        <el-table-column fixed="right" label="操作" align="center" width="180">
          <template #default="opt">
            <el-button type="primary" @click="onUpdateOrder(opt.row)">修改</el-button>
            <el-button type="danger" @click="onDeleteOrder(opt.row.id)">删除</el-button>
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
        <el-button @click="onBatchUpdateOrder()">置为已付款</el-button>
        <el-button @click="onBatchDeleteOrder()">删除</el-button>
        <el-button @click="onClearSelection()">取消选择</el-button>
      </div>
      <el-dialog title="开单" v-model="addOrderVisible" width="80%">
        <el-form ref="addOrderForm" :rules="addOrderFormRules" :model="addOrderForm" label-width="100px">
          <el-form-item label="单号:" prop="odd">
            <el-input v-model="addOrderForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="客户:" prop="customer">
            <el-input v-model="addOrderForm.customer"></el-input>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <el-input v-model="addOrderForm.product"></el-input>
          </el-form-item>
          <el-form-item label="日期:" prop="billdate">
            <el-date-picker
              type="date"
              placeholder="选择日期"
              v-model="addOrderForm.billdate"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="数量:" prop="amount">
            <el-input v-model="addOrderForm.amount"></el-input>
          </el-form-item>
          <el-form-item label="单价:" prop="unitprice">
            <el-input v-model="addOrderForm.unitprice"></el-input>
          </el-form-item>
          <el-form-item label="付款状态:" prop="paystatus">
            <el-select v-model="addOrderForm.paystatus" placeholder="请选择付款状态">
              <el-option label="未付款" value="0"></el-option>
              <el-option label="已付款" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="夹板成本:" prop="boardcost">
            <el-input v-model="addOrderForm.boardcost"></el-input>
          </el-form-item>
          <el-form-item label="防火板成本:" prop="fireproofboardcost">
            <el-input v-model="addOrderForm.fireproofboardcost"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onAddOrderCommit('addOrderForm')">确定</el-button>
            <el-button @click="onAddOrderCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <el-dialog title="修改单据信息" v-model="updateOrderVisible" width="80%">
        <el-form :model="updateOrderForm" label-width="80px">
          <el-form-item label="单号:">
            <el-input v-model="updateOrderForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="客户:">
            <el-input v-model="updateOrderForm.customer"></el-input>
          </el-form-item>
          <el-form-item label="产品:">
            <el-input v-model="updateOrderForm.product"></el-input>
          </el-form-item>
          <el-form-item label="日期:">
            <el-input v-model="updateOrderForm.billdate"></el-input>
          </el-form-item>
          <el-form-item label="数量:">
            <el-input v-model="updateOrderForm.amount"></el-input>
          </el-form-item>
          <el-form-item label="单价:">
            <el-input v-model="updateOrderForm.unitprice"></el-input>
          </el-form-item>
          <el-form-item label="付款状态:">
            <el-select v-model="updateOrderForm.paystatus" placeholder="请选择付款状态">
              <el-option label="未付款" value="0"></el-option>
              <el-option label="已付款" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="夹板成本:">
            <el-input v-model="updateOrderForm.boardcost"></el-input>
          </el-form-item>
          <el-form-item label="防火板成本:">
            <el-input v-model="updateOrderForm.fireproofboardcost"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onUpdateOrderCommit">确定</el-button>
            <el-button @click="onUpdateOrderCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </div>
  </div>
</template>


<script>
export default {
  name: 'GlobalScript',
  props: {
    msg: String
  },
  mounted() {
    this.getAllOrder()
  },
  methods: {
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
    onBatchUpdateOrder() {
      if (this.multipleSelection.length !== 0) {
        this.multipleSelection.forEach((data) => {
          console.log(data.id)
        })
      }
    },
    onBatchDeleteOrder() {
      if (this.multipleSelection.length !== 0) {
        this.multipleSelection.forEach((data) => {
          console.log(data.id)
        })
      }
    },
    onClearSelection() {
      this.$refs.multipleTable.clearSelection()
    },
    handleCurrentChange(currentPage) {
      // 改变当前页码
      console.log(currentPage)
      this.page.index = currentPage;
      console.log(this.page.index)
      // 重新获取数据
      this.getAllOrder()
    },
    onAddOrderCancel() {
      this.addOrderVisible = false
    },
    onUpdateOrderCancel() {
      this.updateOrderVisible = false
    },
    onAddOrder() {
      this.addOrderVisible = true
    },
    onUpdateOrder(order) {
      console.log(order)
      this.updateOrderForm = order
      this.updateOrderVisible = true
    },
    onAddOrderCommit(addOrderForm) {
      const that = this
      this.$refs[addOrderForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append('odd', this.addOrderForm.odd)
          param.append('customer', this.addOrderForm.customer)
          param.append('product', this.addOrderForm.product)
          param.append('billdate', this.addOrderForm.billdate)
          param.append('amount', this.addOrderForm.amount)
          param.append('unitprice', this.addOrderForm.unitprice)
          param.append('paystatus', this.addOrderForm.paystatus)
          param.append('boardcost', this.addOrderForm.boardcost)
          param.append('fireproofboardcost', this.addOrderForm.fireproofboardcost)
          this.$axios.post('http://localhost:8088/shipment/addShipment', param).then(function (response) {
            if (response.data.code === 1) {
              that.addOrderVisible = false
              that.getAllOrder()

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
    onUpdateOrderCommit() {
      console.log("确定更新")
    },
    onDeleteOrder(id) {
      console.log("确定删除")
    },
    getAllOrder() {
      const that = this;
      this.$axios.get('http://localhost:8088/shipment/findAllShipment' +
        '?pageNum=' + that.page.index + '&pageSize=' + that.page.size +
        '&customerName=' + that.customerInput +
        '&bizStartDate=' + that.billDateInput[0] + '&bizEndDate=' + that.billDateInput[1])
        .then(function (response) {
          that.orderData = response.data.data
          that.page.total = response.data.total
          console.log(response.data.data)
        }).catch(function (error) {
      })
    },
    searchOrder() {
      const that = this
      console.log(this.billDateInput)
      this.$axios.get('http://localhost:8088/shipment/findAllShipment' +
        '?pageNum=' + that.page.index + '&pageSize=' + that.page.size +
        '&customerName=' + that.customerInput +
        '&bizStartDate=' + that.billDateInput[0] + '&bizEndDate=' + that.billDateInput[1])
        .then(function (response) {
          that.orderData = response.data.data
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
        total: 20
      },
      multipleSelection: [],
      orderData: [],
      customerInput: '',
      billDateInput: '',
      addOrderVisible: false,
      updateOrderVisible: false,
      addOrderForm: {
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
      updateOrderForm: {},
      addOrderFormRules: {
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
