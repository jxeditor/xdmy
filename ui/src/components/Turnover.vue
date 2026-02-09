<template>
  <div id="turnover">
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
          <el-input v-model="payerInput" placeholder="输入付款人"/>
        </el-col>
        <el-col :span="2"></el-col>
        <el-col :span="2">
          <el-input v-model="payeeInput" placeholder="输入收款人"/>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="searchTurnover">搜索</el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="onAddTurnover">记录流水</el-button>
        </el-col>
      </el-row>
      <el-table ref="multipleTable" stripe :data="TurnoverData" style="width: 100%;">
        <el-table-column prop="id" label="UID" align="center">
        </el-table-column>
        <el-table-column prop="payer" label="付款人" width="180px" align="center">
        </el-table-column>
        <el-table-column prop="payee" label="收款人" width="180px" align="center">
        </el-table-column>
        <el-table-column prop="billdate" label="日期" width="120px" align="center">
        </el-table-column>
        <el-table-column prop="money" label="金额" width="100" align="center">
        </el-table-column>
        <el-table-column prop="tax" label="税点金额" width="100" align="center">
        </el-table-column>
        <el-table-column prop="paid" label="实际金额" width="100" align="center">
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="360" align="center" :show-overflow-tooltip="true">
        </el-table-column>
        <el-table-column fixed="right" label="操作" align="center" width="180">
          <template #default="opt">
            <el-button type="primary" @click="onUpdateTurnover(opt.row)">修改</el-button>
            <el-button type="danger" @click="onDeleteTurnover(opt.row.id)">删除</el-button>
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
      <el-dialog title="记录流水" v-model="addTurnoverVisible" width="80%">
        <el-form ref="addTurnoverForm" :rules="addTurnoverFormRules" :model="addTurnoverForm" label-width="100px">
          <el-form-item label="付款人:" prop="payer">
            <el-input v-model="addTurnoverForm.payer"></el-input>
          </el-form-item>
          <el-form-item label="收款人:" prop="payee">
            <el-input v-model="addTurnoverForm.payee"></el-input>
          </el-form-item>
          <el-form-item label="日期:" prop="billdate">
            <el-date-picker
              type="date"
              placeholder="选择日期"
              v-model="addTurnoverForm.billdate"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="金额:" prop="money">
            <el-input v-model="addTurnoverForm.money"></el-input>
          </el-form-item>
          <el-form-item label="税点金额:" prop="tax">
            <el-input v-model="addTurnoverForm.tax"></el-input>
          </el-form-item>
          <el-form-item label="备注:" prop="remark">
            <el-input v-model="addTurnoverForm.remark"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onAddTurnoverCommit(`addTurnoverForm`)">确定</el-button>
            <el-button @click="onAddTurnoverCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <el-dialog title="修改流水信息" v-model="updateTurnoverVisible" width="80%">
        <el-form ref="updateTurnoverForm" :rules="updateTurnoverFormRules" :model="updateTurnoverForm"
                 label-width="100px">
          <el-form-item label="付款人:" prop="payer">
            <el-input v-model="updateTurnoverForm.payer"></el-input>
          </el-form-item>
          <el-form-item label="收款人:" prop="payee">
            <el-input v-model="updateTurnoverForm.payee"></el-input>
          </el-form-item>
          <el-form-item label="日期:" prop="billdate">
            <el-date-picker
              type="date"
              placeholder="选择日期"
              v-model="updateTurnoverForm.billdate"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="金额:" prop="money">
            <el-input v-model="updateTurnoverForm.money"></el-input>
          </el-form-item>
          <el-form-item label="税点金额:" prop="tax">
            <el-input v-model="updateTurnoverForm.tax"></el-input>
          </el-form-item>
          <el-form-item label="备注:" prop="remark">
            <el-input v-model="updateTurnoverForm.remark"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onUpdateTurnoverCommit(`updateTurnoverForm`)">确定</el-button>
            <el-button @click="onUpdateTurnoverCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </div>
  </div>
</template>


<script>
export default {
  name: `TurnoverScript`,
  props: {
    msg: String
  },
  mounted() {
    this.getAllTurnover()
  },
  methods: {
    handleCurrentChange(currentPage) {
      this.page.index = currentPage;
      this.getAllTurnover()
    },
    onAddTurnoverCancel() {
      this.addTurnoverVisible = false
    },
    onUpdateTurnoverCancel() {
      this.updateTurnoverVisible = false
    },
    onAddTurnover() {
      this.addTurnoverVisible = true
    },
    onUpdateTurnover(Turnover) {
      this.updateTurnoverForm = Turnover
      this.updateTurnoverVisible = true
    },
    onAddTurnoverCommit(addTurnoverForm) {
      const that = this
      this.$refs[addTurnoverForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append(`payer`, this.addTurnoverForm.payer)
          param.append(`payee`, this.addTurnoverForm.payee)
          param.append(`billdate`, this.addTurnoverForm.billdate)
          param.append(`money`, this.addTurnoverForm.money)
          param.append(`tax`, this.addTurnoverForm.tax)
          param.append(`remark`, this.addTurnoverForm.remark)
          this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/turnover/addTurnover`, param).then(function (response) {
            if (response.data.code === 1) {
              that.addTurnoverVisible = false
              that.getAllTurnover()
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
    onUpdateTurnoverCommit(updateTurnoverForm) {
      const that = this
      this.$refs[updateTurnoverForm].validate((valid) => {
        if (valid) {
          let param = new URLSearchParams()
          param.append(`id`, this.updateTurnoverForm.id)
          param.append(`payer`, this.updateTurnoverForm.payer)
          param.append(`payee`, this.updateTurnoverForm.payee)
          param.append(`billdate`, this.updateTurnoverForm.billdate)
          param.append(`money`, this.updateTurnoverForm.money)
          param.append(`tax`, this.updateTurnoverForm.tax)
          param.append(`remark`, this.updateTurnoverForm.remark)
          this.$axios.post(`${process.env.VUE_APP_API_BASE_URL}/turnover/updateTurnover`, param).then(function (response) {
            if (response.data.code === 1) {
              that.updateTurnoverVisible = false
              that.getAllTurnover()
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
    onDeleteTurnover(id) {
      const that = this;
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/turnover/deleteTurnoverById?id=` + id)
        .then(function (response) {
          if (response.data.code === 1) {
            that.getAllTurnover()
          } else {
            that.$message.error(response.data.msg);
          }
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    getAllTurnover() {
      const that = this;
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/turnover/findAllTurnover` +
        `?pageNum=` + that.page.index + `&pageSize=` + that.page.size +
        `&payerName=` + that.payerInput +
        `&payeeName=` + that.payeeInput +
        `&bizStartDate=` + that.billDateInput[0] + `&bizEndDate=` + that.billDateInput[1])
        .then(function (response) {
          that.TurnoverData = response.data.data
          that.page.total = response.data.total
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    searchTurnover() {
      const that = this
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/turnover/findAllTurnover` +
        `?pageNum=` + that.page.index + `&pageSize=` + that.page.size +
        `&payerName=` + that.payerInput +
        `&payeeName=` + that.payeeInput +
        `&bizStartDate=` + that.billDateInput[0] + `&bizEndDate=` + that.billDateInput[1])
        .then(function (response) {
          that.TurnoverData = response.data.data
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
      TurnoverData: [],
      payerInput: ``,
      payeeInput: ``,
      billDateInput: ``,
      addTurnoverVisible: false,
      updateTurnoverVisible: false,
      addTurnoverForm: {
        payer: ``,
        payee: ``,
        billdate: ``,
        money: 0,
        tax: 0,
        remark: ``
      },
      updateTurnoverForm: {
        payer: ``,
        payee: ``,
        billdate: ``,
        money: 0,
        tax: 0,
        remark: ``
      },
      addTurnoverFormRules: {
        payer: [
          {required: true, message: `请输入付款人`, trigger: `blur`}
        ],
        payee: [
          {required: true, message: `请输入收款人`, trigger: `blur`},
        ],
        billdate: [
          {
            type: `date`, required: true, message: `请选择日期`, trigger: `change`
          }
        ],
        money: [
          {required: true, message: `请输入金额`, trigger: `blur`},
        ],
        tax: [
          {required: true, message: `请输入税点金额`, trigger: `blur`},
        ]
      },
      updateTurnoverFormRules: {
        payer: [
          {required: true, message: `请输入付款人`, trigger: `blur`}
        ],
        payee: [
          {required: true, message: `请输入收款人`, trigger: `blur`},
        ],
        billdate: [
          {
            type: `date`, required: true, message: `请选择日期`, trigger: `change`
          }
        ],
        money: [
          {required: true, message: `请输入金额`, trigger: `blur`},
        ],
        tax: [
          {required: true, message: `请输入税点金额`, trigger: `blur`},
        ]
      }
    }
  }
}
</script>

<style>
#turnover {
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
