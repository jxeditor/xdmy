<template>
  <div id="incoming">
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
        <el-col :span="2">
          <el-input v-model="producerInput" placeholder="输入供应商"/>
        </el-col>
        <el-col :span="2"></el-col>
        <el-col :span="2">
          <el-input v-model="productInput" placeholder="输入产品名"/>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click='searchIncoming'>搜索</el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click='onAddIncoming'>入货</el-button>
        </el-col>
      </el-row>
      <el-table ref="multipleTable" stripe :data="IncomingData" style="width: 100%;"
                @selection-change="handleSelectionChange">
        <el-table-column type="selection">
        </el-table-column>
        <el-table-column prop="id" label="UID" align="center">
        </el-table-column>
        <el-table-column prop="odd" label="单号" align="center">
        </el-table-column>
        <el-table-column prop="producer" label="供应商" width="120px" align="center">
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
        <el-table-column prop="remark" label="备注" width="80" align="center">
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
      <el-dialog title="入货" v-model="addIncomingVisible" width="80%">
        <el-form ref="addIncomingForm" :rules="addIncomingFormRules" :model="addIncomingForm" label-width="100px">
          <el-form-item label="单号:" prop="odd">
            <el-input v-model="addIncomingForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="供应商:" prop="producer">
            <el-input v-model="addIncomingForm.producer"></el-input>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <el-input v-model="addIncomingForm.product"></el-input>
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
          <el-form-item>
            <el-button type="primary" @click="onAddIncomingCommit('addIncomingForm')">确定</el-button>
            <el-button @click="onAddIncomingCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <el-dialog title="修改单据信息" v-model="updateIncomingVisible" width="80%">
        <el-form ref="updateIncomingForm" :rules="updateIncomingFormRules" :model="updateIncomingForm"
                 label-width="100px">
          <el-form-item label="单号:" prop="odd">
            <el-input v-model="updateIncomingForm.odd"></el-input>
          </el-form-item>
          <el-form-item label="供应商:" prop="producer">
            <el-input v-model="updateIncomingForm.producer"></el-input>
          </el-form-item>
          <el-form-item label="产品:" prop="product">
            <el-input v-model="updateIncomingForm.product"></el-input>
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
          <el-form-item>
            <el-button type="primary" @click="onUpdateIncomingCommit('updateIncomingForm')">确定</el-button>
            <el-button @click="onUpdateIncomingCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </div>
  </div>
</template>


<script>
export default {
  name: 'IncomingScript',
  props: {
    msg: String
  },
  mounted() {
    this.getAllIncoming()
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
          this.$axios.get('http://124.223.70.175:8088/incoming/updatePaystatusIncomingById?id=' + data.id)
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
          this.$axios.get('http://124.223.70.175:8088/incoming/deleteIncomingById?id=' + data.id)
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
    },
    onUpdateIncomingCancel() {
      this.updateIncomingVisible = false
    },
    onAddIncoming() {
      this.addIncomingVisible = true
    },
    onUpdateIncoming(incoming) {
      this.updateIncomingForm = incoming
      this.updateIncomingVisible = true
    },
    onAddIncomingCommit(addIncomingForm) {
      const that = this
      this.$refs[addIncomingForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append('odd', this.addIncomingForm.odd)
          param.append('producer', this.addIncomingForm.producer)
          param.append('product', this.addIncomingForm.product)
          param.append('billdate', this.addIncomingForm.billdate)
          param.append('amount', this.addIncomingForm.amount)
          param.append('unitprice', this.addIncomingForm.unitprice)
          param.append('paystatus', this.addIncomingForm.paystatus)
          param.append('remark', this.addIncomingForm.remark)
          this.$axios.post('http://124.223.70.175:8088/incoming/addIncoming', param).then(function (response) {
            if (response.data.code === 1) {
              that.addIncomingVisible = false
              that.getAllIncoming()
            } else {
              that.$message.error(response.data.msg);
            }
          }).catch(function (error) {
            that.$message.error(error);
          })
        } else {
          return false
        }
      })
    },
    onUpdateIncomingCommit(updateIncomingForm) {
      const that = this
      this.$refs[updateIncomingForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append('id', this.updateIncomingForm.id)
          param.append('odd', this.updateIncomingForm.odd)
          param.append('producer', this.updateIncomingForm.producer)
          param.append('product', this.updateIncomingForm.product)
          param.append('billdate', this.updateIncomingForm.billdate)
          param.append('amount', this.updateIncomingForm.amount)
          param.append('unitprice', this.updateIncomingForm.unitprice)
          param.append('paystatus', this.updateIncomingForm.paystatus)
          param.append('remark', this.updateIncomingForm.remark)
          this.$axios.post('http://124.223.70.175:8088/incoming/updateIncoming', param).then(function (response) {
            if (response.data.code === 1) {
              that.updateIncomingVisible = false
              that.getAllIncoming()
            } else {
              that.$message.error(response.data.msg);
            }
          }).catch(function (error) {
            that.$message.error(error);
          })
        } else {
          return false
        }
      })
    },
    onDeleteIncoming(id) {
      const that = this;
      this.$axios.get('http://124.223.70.175:8088/incoming/deleteIncomingById?id=' + id)
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
      this.$axios.get('http://124.223.70.175:8088/incoming/findAllIncoming' +
        '?pageNum=' + that.page.index + '&pageSize=' + that.page.size +
        '&producerName=' + that.producerInput +
        '&productName=' + that.productInput +
        '&bizStartDate=' + that.billDateInput[0] + '&bizEndDate=' + that.billDateInput[1])
        .then(function (response) {
          that.IncomingData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    searchIncoming() {
      const that = this
      this.$axios.get('http://124.223.70.175:8088/incoming/findAllIncoming' +
        '?pageNum=' + that.page.index + '&pageSize=' + that.page.size +
        '&producerName=' + that.producerInput +
        '&productName=' + that.productInput +
        '&bizStartDate=' + that.billDateInput[0] + '&bizEndDate=' + that.billDateInput[1])
        .then(function (response) {
          that.IncomingData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
        that.$message.error(error);
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
      IncomingData: [],
      producerInput: '',
      productInput: '',
      billDateInput: '',
      addIncomingVisible: false,
      updateIncomingVisible: false,
      addIncomingForm: {
        odd: '',
        producer: '',
        product: '',
        billdate: '',
        amount: 0,
        unitprice: 0,
        paystatus: '0',
        remark: '无'
      },
      updateIncomingForm: {
        odd: '',
        producer: '',
        product: '',
        billdate: '',
        amount: 0,
        unitprice: 0,
        paystatus: '0',
        remark: '无'
      },
      addIncomingFormRules: {
        odd: [
          {required: true, message: '请输入单号', trigger: 'blur'}
        ],
        producer: [
          {required: true, message: '请输入供应商', trigger: 'blur'},
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
        ]
      },
      updateIncomingFormRules: {
        odd: [
          {required: true, message: '请输入单号', trigger: 'blur'}
        ],
        producer: [
          {required: true, message: '请输入供应商', trigger: 'blur'},
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
        ]
      }
    }
  }
}
</script>

<style>
#incoming {
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

