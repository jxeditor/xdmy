<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">数据大屏</h2>
      <div class="page-actions" style="display:flex;gap:8px;"></div>
    </div>

    <div class="charts-grid">
      <!-- 第一个图表 -->
      <div class="chart-box">
        <div class="filter-bar" style="margin-bottom:12px;">
          <el-row type="flex" justify="start" align="middle" :gutter="20" style="width:100%;">
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
        <div id="shipment1" style="width:100%;height:260px;">
        </div>
      </div>

      <!-- 第二个图表 -->
      <div class="chart-box">
        <div id="shipment2" style="width:100%;height:340px;">
        </div>
      </div>

      <!-- 可以添加更多图表 -->
      <!-- <div class="chart-box">
        <div id="shipment3" style="width:100%;height:340px;">
        </div>
      </div>
      <div class="chart-box">
        <div id="shipment4" style="width:100%;height:340px;">
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
        // 401错误由响应拦截器处理，不显示错误信息
        if (error.response && error.response.status !== 401) {
          that.$message.error(error);
        }
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
        // 401错误由响应拦截器处理，不显示错误信息
        if (error.response && error.response.status !== 401) {
          that.$message.error(error);
        }
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

.charts-grid {
  display: grid;
  gap: 16px;
}
.chart-box {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 20px;
  height: 380px;
}
</style>
