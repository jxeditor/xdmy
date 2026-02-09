<template>
  <div class="screen-container">
    <h1>数据大屏</h1>
    
    <div class="content-wrapper">
      <!-- 第一个图表 -->
      <div class="chart-card">
        <div class="chart-header">
          <el-row type="flex" justify="start" align="middle" class="search-row">
            <el-col :span="12">
              <el-date-picker v-model="billDateInput1"
                              type="daterange"
                              range-separator="至"
                              start-placeholder="开始日期"
                              end-placeholder="结束日期"
                              value-format="YYYY-MM-DD"
                              style="width: 100%;"
              />
            </el-col>
            <el-col :span="6">
              <el-input v-model="customerInput1" placeholder="输入客户名" style="width: 100%;"/>
            </el-col>
            <el-col :span="4">
              <el-button type="primary" @click="getShipment1ChartData" style="width: 100%;">搜索</el-button>
            </el-col>
          </el-row>
        </div>
        <div id="shipment1" class="chart-content">
        </div>
      </div>
      
      <!-- 第二个图表 -->
      <div class="chart-card">
        <div id="shipment2" class="chart-content">
        </div>
      </div>
      
      <!-- 可以添加更多图表 -->
      <!-- <div class="chart-card">
        <div id="shipment3" class="chart-content">
        </div>
      </div>
      <div class="chart-card">
        <div id="shipment4" class="chart-content">
        </div>
      </div> -->
    </div>
  </div>
</template>

<script>
import * as echarts from "echarts";

let biShipment1Chart;
let biShipment2Chart;
export default {
  name: "Screen",
  mounted() {
    this.drawShipment1(`shipment1`)
    this.drawShipment2(`shipment2`)
  },
  methods: {
    drawShipment1(id) {
      const chartDom = document.getElementById(id);
      document.getElementById(id).removeAttribute(`_echarts_instance_`);
      biShipment1Chart = echarts.init(chartDom);
      this.getShipment1ChartData()
    },
    drawShipment2(id) {
      const chartDom = document.getElementById(id);
      document.getElementById(id).removeAttribute(`_echarts_instance_`);
      biShipment2Chart = echarts.init(chartDom);
      this.getShipment2ChartData()
    },
    getShipment1ChartData() {
      const that = this
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/screen/getShipment1ChartData` +
        `?customerName=` + that.customerInput1 +
        `&bizStartDate=` + that.billDateInput1[0] + `&bizEndDate=` + that.billDateInput1[1])
        .then(function (response) {
          let option;
          option = {
            title: {
              text: `出货按天统计`,
              left: `center`
            },
            tooltip: {
              trigger: `axis`,
              axisPointer: {
                type: `shadow`
              }
            },
            legend: {
              data: [`出货总金额`, `出货已收总金额`, `出货总利润`, `出货已收总利润`],
              left: `center`,
              bottom: 10
            },
            toolbox: {
              show: true,
              orient: `vertical`,
              left: `right`,
              top: `center`,
              feature: {
                mark: {show: true},
                dataView: {show: true, readOnly: false},
                magicType: {show: true, type: [`line`, `bar`, `stack`]},
                restore: {show: true},
                saveAsImage: {show: true}
              }
            },
            xAxis: {
              type: `category`,
              axisTick: { show: false },
              data: response.data.billdate
            },
            yAxis: {
              type: `value`,
              // scale: true
            },
            series: [
              {
                name: `出货总金额`,
                type: `bar`,
                barGap: 0,
                emphasis: {
                  focus: `series`
                },
                label: {
                  show: true,
                  position: `top`
                },
                data: response.data.money
              },
              {
                name: `出货已收总金额`,
                type: `bar`,
                barGap: 0,
                emphasis: {
                  focus: `series`
                },
                label: {
                  show: true,
                  position: `top`
                },
                data: response.data.paymoney
              },
              // {
              //   name: `出货总成本`,
              //   type: `bar`,
              //   barGap: 0,
              //   emphasis: {
              //     focus: `series`
              //   },
              //   label: {
              //     show: true,
              //     position: `top`
              //   },
              //   data: response.data.costmoney
              // },
              {
                name: `出货总利润`,
                type: `bar`,
                barGap: 0,
                emphasis: {
                  focus: `series`
                },
                label: {
                  show: true,
                  position: `top`
                },
                data: response.data.profit
              },
              {
                name: `出货已收总利润`,
                type: `bar`,
                barGap: 0,
                emphasis: {
                  focus: `series`
                },
                label: {
                  show: true,
                  position: `top`
                },
                data: response.data.payprofit
              }
            ]
          };
          option && biShipment1Chart.setOption(option);
        }).catch(function (error) {
        that.$message.error(error);
      })
    },
    getShipment2ChartData() {
      const that = this
      this.$axios.get(`${process.env.VUE_APP_API_BASE_URL}/screen/getShipment2ChartData`)
        .then(function (response) {
          let option;
          option = {
            title: {
              text: `出货按月统计`,
              left: `center`
            },
            tooltip: {
              trigger: `axis`,
              axisPointer: {
                type: `shadow`
              }
            },
            legend: {
              data: [`出货总金额`, `出货已收总金额`, `出货总利润`, `出货已收总利润`],
              left: `center`,
              bottom: 10
            },
            toolbox: {
              show: true,
              orient: `vertical`,
              left: `right`,
              top: `center`,
              feature: {
                mark: {show: true},
                dataView: {show: true, readOnly: false},
                magicType: {show: true, type: [`line`, `bar`, `stack`]},
                restore: {show: true},
                saveAsImage: {show: true}
              }
            },
            xAxis: [
              {
                type: `category`,
                axisTick: {show: false},
                data: response.data.billdate
              }
            ],
            yAxis: [
              {
                type: `value`
              }
            ],
            series: [
              {
                name: `出货总金额`,
                type: `bar`,
                barGap: 0,
                emphasis: {
                  focus: `series`
                },
                label: {
                  show: true,
                  position: `top`
                },
                data: response.data.money
              },
              {
                name: `出货已收总金额`,
                type: `bar`,
                emphasis: {
                  focus: `series`
                },
                label: {
                  show: true,
                  position: `top`
                },
                data: response.data.paymoney
              },
              // {
              //   name: `出货总成本`,
              //   type: `bar`,
              //   emphasis: {
              //     focus: `series`
              //   },
              //   label: {
              //     show: true,
              //     position: `top`
              //   },
              //   data: response.data.costmoney
              // },
              {
                name: `出货总利润`,
                type: `bar`,
                emphasis: {
                  focus: `series`
                },
                label: {
                  show: true,
                  position: `top`
                },
                data: response.data.profit
              },
              {
                name: `出货已收总利润`,
                type: `bar`,
                emphasis: {
                  focus: `series`
                },
                label: {
                  show: true,
                  position: `top`
                },
                data: response.data.payprofit
              }
            ]
          };
          option && biShipment2Chart.setOption(option);
        }).catch(function (error) {
        that.$message.error(error);
      })
    }
  },
  data() {
    return {
      customerInput1: ``,
      billDateInput1: ``,
    }
  }
}
</script>

<style scoped>
/* 页面容器 */
.screen-container {
  min-height: 100vh;
  padding: 20px;
  background-color: #f5f7fa;
}

/* 页面标题 */
.screen-container h1 {
  text-align: center;
  color: #303133;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
  padding-bottom: 15px;
  border-bottom: 3px solid #667eea;
  display: inline-block;
  position: relative;
  left: 50%;
  transform: translateX(-50%);
  animation: fadeInDown 0.5s ease-out;
}

/* 内容容器 */
.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease-out;
}

/* 图表卡片 */
.chart-card {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
  padding: 20px;
  animation: fadeInUp 0.5s ease-out 0.1s both;
}

/* 图表头部 */
.chart-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

/* 搜索行 */
.search-row {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

/* 图表内容 */
.chart-content {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
}

/* 按钮样式 */
.el-button {
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  padding: 10px 18px !important;
  font-weight: 500 !important;
}

.el-button:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
}

/* 日期选择器样式 */
.el-date-picker {
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

/* 输入框样式 */
.el-input {
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

.el-input:focus-within {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) !important;
  border-color: #667eea !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .screen-container {
    padding: 10px;
  }
  
  .screen-container h1 {
    font-size: 22px;
    margin-bottom: 20px;
  }
  
  .chart-card {
    padding: 15px;
  }
  
  .chart-content {
    height: 300px;
  }
  
  .search-row {
    flex-wrap: wrap;
  }
  
  .el-col {
    margin-bottom: 10px;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
