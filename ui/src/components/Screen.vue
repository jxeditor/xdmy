<template>

  <div class="shipment1" style="border:1px solid #000">
    <el-row style="width:100%;padding: 20px;">
      <el-col :span="10">
        <el-date-picker v-model="billDateInput1"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="YYYY-MM-DD"
        />
      </el-col>
      <el-col :span="2">
        <el-input v-model="customerInput1" placeholder="输入客户名"/>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="getShipment1ChartData">搜索</el-button>
      </el-col>
    </el-row>
    <div id="shipment1" style="width: 100%;height: 400px">
    </div>
  </div>
  <div id="shipment2" style="border:1px solid #000;width: 100%;height: 400px;margin-top: 20px">
  </div>
  <div id="shipment3" style="width: 100%;height: 400px">
  </div>
  <div id="shipment4" style="width: 100%;height: 400px">
  </div>
</template>

<script>
import * as echarts from 'echarts';

let biShipment1Chart;
let biShipment2Chart;
export default {
  name: "Screen",
  mounted() {
    this.drawShipment1('shipment1')
    this.drawShipment2('shipment2')
  },
  methods: {
    drawShipment1(id) {
      const chartDom = document.getElementById(id);
      biShipment1Chart = echarts.init(chartDom);
      this.getShipment1ChartData()
    },
    drawShipment2(id) {
      const chartDom = document.getElementById(id);
      biShipment2Chart = echarts.init(chartDom);
      this.getShipment2ChartData()
    },
    getShipment1ChartData() {
      const that = this
      this.$axios.get('http://124.223.70.175:8088/screen/getShipment1ChartData' +
        '?customerName=' + that.customerInput1 +
        '&bizStartDate=' + that.billDateInput1[0] + '&bizEndDate=' + that.billDateInput1[1])
        .then(function (response) {
          let option;
          option = {
            title: {
              text: '出货按天统计'
            },
            tooltip: {
              trigger: 'axis'
            },
            legend: {
              data: ['出货总金额', '出货总成本', '出货总利润', '出货已收总金额', '出货已收总利润']
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            toolbox: {
              feature: {
                saveAsImage: {}
              }
            },
            xAxis: {
              type: 'category',
              boundaryGap: false,
              data: response.data.billdate
            },
            yAxis: {
              type: 'value',
              // scale: true
            },
            series: [
              {
                name: '出货总金额',
                type: 'line',
                data: response.data.money
              },
              {
                name: '出货总成本',
                type: 'line',
                data: response.data.costmoney
              },
              {
                name: '出货总利润',
                type: 'line',
                data: response.data.profit
              },
              {
                name: '出货已收总金额',
                type: 'line',
                data: response.data.paymoney
              },
              {
                name: '出货已收总利润',
                type: 'line',
                data: response.data.payprofit
              }
            ]
          };
          option && biShipment1Chart.setOption(option);
        }).catch(function (error) {
        console.log(error)
      })
    },
    getShipment2ChartData() {
      const that = this
      this.$axios.get('http://124.223.70.175:8088/screen/getShipment2ChartData')
        .then(function (response) {
          let option;
          option = {
            title: {
              text: '出货按月统计'
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'shadow'
              }
            },
            legend: {
              data: ['出货总金额', '出货总成本', '出货总利润', '出货已收总金额', '出货已收总利润']
            },
            toolbox: {
              show: true,
              orient: 'vertical',
              left: 'right',
              top: 'center',
              feature: {
                mark: { show: true },
                dataView: { show: true, readOnly: false },
                magicType: { show: true, type: ['line', 'bar', 'stack'] },
                restore: { show: true },
                saveAsImage: { show: true }
              }
            },
            xAxis: [
              {
                type: 'category',
                axisTick: { show: false },
                data: response.data.billdate
              }
            ],
            yAxis: [
              {
                type: 'value'
              }
            ],
            series: [
              {
                name: '出货总金额',
                type: 'bar',
                barGap: 0,
                emphasis: {
                  focus: 'series'
                },
                data: response.data.money
              },
              {
                name: '出货总成本',
                type: 'bar',
                emphasis: {
                  focus: 'series'
                },
                data: response.data.costmoney
              },
              {
                name: '出货总利润',
                type: 'bar',
                emphasis: {
                  focus: 'series'
                },
                data: response.data.profit
              },
              {
                name: '出货已收总金额',
                type: 'bar',
                emphasis: {
                  focus: 'series'
                },
                data: response.data.paymoney
              },
              {
                name: '出货已收总利润',
                type: 'bar',
                emphasis: {
                  focus: 'series'
                },
                data: response.data.payprofit
              }
            ]
          };
          option && biShipment2Chart.setOption(option);
        }).catch(function (error) {
        console.log(error)
      })
    }
  },
  data() {
    return {
      customerInput1: '',
      billDateInput1: '',
    }
  }
}
</script>

<style scoped>

</style>
