<template>
  <div class="data-screen">
    <!-- 顶部标题 -->
    <div class="screen-header">
      <div class="header-left">
        <dv-decoration-8 :style="{width: '100%', height: '40px'}" />
      </div>
      <div class="header-center">
        <div class="title-box">
          <div class="title-content">
            <div class="title-text">郑州市养老机构资金监管大屏</div>
            <dv-decoration-6 class="title-decoration" />
          </div>
          <div class="datetime">{{ currentDateTime }}</div>
        </div>
      </div>
      <div class="header-right">
        <dv-decoration-8 :reverse="true" :style="{width: '100%', height: '40px'}" />
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="screen-content">
      <!-- 左侧区域 -->
      <div class="left-panel">
        <!-- 总体概况 -->
        <div class="panel-item overview-panel">
          <div class="panel-header">
            <div class="header-title">总体概况</div>
          </div>
          <div class="panel-content">
            <div class="overview-grid">
              <div class="overview-item" v-for="(item, index) in overviewData" :key="index">
                <div class="item-box">
                  <div class="item-icon" :style="{color: item.color}">
                    <i :class="item.icon"></i>
                  </div>
                  <div class="item-value" :style="{color: item.color}">
                    {{ item.value }}
                  </div>
                  <div class="item-label">{{ item.label }}</div>
                  <div class="item-change" :class="item.changeClass">
                    <i :class="item.changeIcon"></i>
                    {{ item.change }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 各区县机构排名 -->
        <div class="panel-item flex-1">
          <div class="panel-header">
            <div class="header-title">各区县机构数量排名</div>
          </div>
          <div class="panel-content">
            <div id="districtRankChart" class="chart-box"></div>
          </div>
        </div>

        <!-- 机构类型分布 -->
        <div class="panel-item">
          <div class="panel-header">
            <div class="header-title">机构类型分布</div>
          </div>
          <div class="panel-content">
            <div id="institutionTypeChart" class="chart-box"></div>
          </div>
        </div>

        <!-- 各区县老人分布 -->
        <div class="panel-item">
          <div class="panel-header">
            <div class="header-title">各区县老人分布</div>
          </div>
          <div class="panel-content">
            <div id="elderDistrictChart" class="chart-box"></div>
          </div>
        </div>
      </div>

      <!-- 中间区域 -->
      <div class="center-panel">
        <!-- 地图区域 -->
        <div class="map-container">
          <div class="map-header">
            <div class="map-title">郑州市各区县分布图</div>
          </div>
          <div class="map-body">
            <div id="mapChart" class="map-chart" v-show="mapLoaded"></div>
            <div class="map-placeholder" v-show="!mapLoaded">
              <dv-loading>Loading...</dv-loading>
            </div>
            <div class="map-data-overlay">
              <div class="data-card" v-for="(item, index) in mapDataCards" :key="index">
                <div class="data-icon" v-if="item.icon">{{ item.icon }}</div>
                <div class="data-info" :class="{ 'no-icon': !item.icon }">
                  <div class="data-label">{{ item.label }}</div>
                  <div class="data-value">{{ item.value }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 实时预警列表 -->
        <div class="warning-container">
          <div class="warning-header">
            <div class="header-title">实时预警信息</div>
          </div>
          <div class="warning-body">
            <div class="warning-table">
              <div class="table-header">
                <div class="header-col">排名</div>
                <div class="header-col">机构名称</div>
                <div class="header-col">预警类型</div>
                <div class="header-col">次数</div>
              </div>
              <div class="table-body">
                <div class="table-row" v-for="(item, index) in warningList" :key="index">
                  <div class="row-col rank">
                    <span class="rank-number" :class="index < 3 ? 'top' : ''">{{ index + 1 }}</span>
                  </div>
                  <div class="row-col">{{ item.name }}</div>
                  <div class="row-col">
                    <span class="warning-tag" :class="getWarningClass(item.value)">{{ getWarningType(item.value) }}</span>
                  </div>
                  <div class="row-col count">{{ item.value }}次</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧区域 -->
      <div class="right-panel">
        <!-- 资金监管总览 -->
        <div class="panel-item fund-panel">
          <div class="panel-header">
            <div class="header-title">资金监管总览</div>
          </div>
          <div class="panel-content">
            <div class="fund-overview">
              <div class="fund-item" v-for="(item, index) in fundOverview" :key="index">
                <div class="fund-label">{{ item.label }}</div>
                <div class="fund-value" :style="{color: item.color}">{{ item.value }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 月度资金趋势 -->
        <div class="panel-item flex-1">
          <div class="panel-header">
            <div class="header-title">月度资金趋势</div>
          </div>
          <div class="panel-content">
            <div id="fundTrendChart" class="chart-box"></div>
          </div>
        </div>

        <!-- 资金流向分析 -->
        <div class="panel-item">
          <div class="panel-header">
            <div class="header-title">交易渠道占比</div>
          </div>
          <div class="panel-content">
            <div id="channelChart" class="chart-box"></div>
          </div>
        </div>

        <!-- 预警类型统计 -->
        <div class="panel-item">
          <div class="panel-header">
            <div class="header-title">预警类型统计</div>
          </div>
          <div class="panel-content">
            <div id="warningTypeChart" class="chart-box"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import axios from 'axios'

// 郑州市各区县
const ZHENGZHOU_DISTRICTS = [
  { name: '中原区', adcode: '410102' },
  { name: '二七区', adcode: '410103' },
  { name: '管城回族区', adcode: '410104' },
  { name: '金水区', adcode: '410105' },
  { name: '上街区', adcode: '410106' },
  { name: '惠济区', adcode: '410108' },
  { name: '中牟县', adcode: '410122' },
  { name: '巩义市', adcode: '410181' },
  { name: '荥阳市', adcode: '410182' },
  { name: '新密市', adcode: '410183' },
  { name: '新郑市', adcode: '410184' },
  { name: '登封市', adcode: '410185' }
]

export default {
  name: 'DataScreen',
  data() {
    return {
      currentDateTime: '',
      timer: null,
      mapLoaded: false,
      charts: {},
      // 总体概况数据
      overviewData: [
        {
          label: '机构总数',
          value: '326',
          change: '+12',
          changeClass: 'up',
          changeIcon: 'el-icon-top',
          color: '#00ff88',
          icon: 'el-icon-office-building'
        },
        {
          label: '老人总数',
          value: '15,680',
          change: '+156',
          changeClass: 'up',
          changeIcon: 'el-icon-top',
          color: '#00d4ff',
          icon: 'el-icon-user'
        },
        {
          label: '资金总量(万元)',
          value: '89,234',
          change: '+1,234',
          changeClass: 'up',
          changeIcon: 'el-icon-top',
          color: '#ffa502',
          icon: 'el-icon-wallet'
        },
        {
          label: '今日变动(万元)',
          value: '523',
          change: '+523',
          changeClass: 'up',
          changeIcon: 'el-icon-top',
          color: '#ff6b6b',
          icon: 'el-icon-data-line'
        }
      ],
      // 资金监管总览
      fundOverview: [
        { label: '总余额', value: '8.92亿', color: '#00ff88' },
        { label: '今日收入', value: '123万', color: '#00d4ff' },
        { label: '今日支出', value: '23万', color: '#ff6b6b' },
        { label: '待审批', value: '56万', color: '#ffa502' },
        { label: '预警金额', value: '12万', color: '#ff4757' }
      ],
      // 地图数据卡片
      mapDataCards: [
        { icon: '', label: '机构总数', value: '326家' },
        { icon: '', label: '老人总数', value: '15,680人' },
        { icon: '', label: '资金总额', value: '8.92亿元' }
      ],
      // 预警列表
      warningList: []
    }
  },
  mounted() {
    this.init()
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
    Object.values(this.charts).forEach(chart => {
      if (chart) chart.dispose()
    })
  },
  methods: {
    async init() {
      this.updateDateTime()
      this.timer = setInterval(this.updateDateTime, 1000)

      // 加载地图
      await this.loadMap()

      // 初始化图表
      this.initAllCharts()

      // 刷新数据
      setInterval(() => {
        this.updateDateTime()
      }, 30000)
    },

    updateDateTime() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const weekdays = ['日', '一', '二', '三', '四', '五', '六']
      const weekday = weekdays[now.getDay()]
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      this.currentDateTime = `${year}-${month}-${day} 星期${weekday} ${hours}:${minutes}:${seconds}`
    },

    async loadMap() {
      try {
        const res = await axios.get('/map/zhengzhou.json', {
          headers: { 'Cache-Control': 'no-cache' }
        })
        echarts.registerMap('zhengzhou', res.data)
        this.mapLoaded = true
        this.$nextTick(() => {
          setTimeout(() => {
            this.initMapChart()
          }, 100)
        })
      } catch (error) {
        console.error('地图加载失败:', error)
        this.mapLoaded = false
        this.initFallbackMap()
      }
    },

    // 地图加载失败时的备用方案
    initFallbackMap() {
      const chartDom = document.getElementById('mapChart')
      if (!chartDom) return

      const chart = echarts.init(chartDom)
      this.charts.mapChart = chart

      const data = ZHENGZHOU_DISTRICTS.map(d => ({
        name: d.name,
        value: Math.floor(Math.random() * 50 + 10)
      }))

      const option = {
        backgroundColor: 'transparent',
        grid: {
          left: '15%',
          right: '10%',
          top: '15%',
          bottom: '15%',
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: '{b}<br/>机构数：{c}家',
          backgroundColor: 'rgba(0, 20, 60, 0.9)',
          borderColor: '#00d4ff',
          textStyle: { color: '#fff' }
        },
        xAxis: {
          type: 'value',
          axisLabel: { color: '#00d4ff' },
          axisLine: { lineStyle: { color: '#1a5c89' } },
          splitLine: { lineStyle: { color: '#1a5c89', type: 'dashed' } }
        },
        yAxis: {
          type: 'category',
          data: data.map(d => d.name),
          axisLabel: { color: '#fff', fontSize: 11 },
          axisLine: { lineStyle: { color: '#1a5c89' } }
        },
        series: [{
          type: 'bar',
          data: data.map(d => d.value),
          barWidth: '60%',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
              { offset: 0, color: '#00d4ff' },
              { offset: 1, color: '#00ff88' }
            ]),
            borderRadius: [0, 4, 4, 0]
          },
          label: {
            show: true,
            position: 'right',
            color: '#00ff88'
          }
        }]
      }

      chart.setOption(option)
      console.log('使用备用方案：区县柱状图')
    },

    initAllCharts() {
      this.initDistrictRankChart()
      this.initInstitutionTypeChart()
      this.initElderDistrictChart()
      this.initFundTrendChart()
      this.initChannelChart()
      this.initWarningTypeChart()
      this.initWarningData()
    },

    // 获取预警类型名称
    getWarningType(value) {
      const types = ['大额动账预警', '最低余额预警', '退款过高预警', '异常支出预警', '资金流水异常']
      return types[value % types.length]
    },

    // 获取预警类型样式
    getWarningClass(value) {
      const type = this.getWarningType(value)
      if (type === '大额动账预警' || type === '退款过高预警') return 'high'
      if (type === '最低余额预警' || type === '异常支出预警') return 'medium'
      return 'low'
    },

    // 初始化地图
    initMapChart() {
      const chartDom = document.getElementById('mapChart')
      if (!chartDom) return

      if (chartDom.offsetWidth === 0 || chartDom.offsetHeight === 0) {
        setTimeout(() => {
          this.initMapChart()
        }, 200)
        return
      }

      const chart = echarts.init(chartDom)
      this.charts.mapChart = chart

      const mapRegistered = echarts.getMap('zhengzhou')
      if (!mapRegistered) {
        this.initFallbackMap()
        return
      }

      const data = ZHENGZHOU_DISTRICTS.map(d => ({
        name: d.name,
        value: Math.floor(Math.random() * 50 + 10)
      }))

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          formatter: '{b}<br/>机构数：{c}家',
          backgroundColor: 'rgba(0, 20, 60, 0.9)',
          borderColor: '#00d4ff',
          textStyle: { color: '#fff' }
        },
        visualMap: {
          min: 0,
          max: 60,
          right: '15px',
          bottom: '15px',
          text: ['高', '低'],
          calculable: true,
          inRange: {
            color: ['#1a5c89', '#2386ad', '#00d4ff', '#00ff88']
          },
          textStyle: { color: '#fff', fontSize: 10 }
        },
        geo: {
          map: 'zhengzhou',
          roam: true,
          layoutCenter: ['50%', '50%'],
          layoutSize: '125%',
          scaleLimit: { min: 0.8, max: 2 },
          label: {
            show: true,
            color: '#fff',
            fontSize: 11
          },
          itemStyle: {
            areaColor: '#1a5c89',
            borderColor: '#00d4ff',
            borderWidth: 1.5
          },
          emphasis: {
            itemStyle: {
              areaColor: '#00ff88',
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              color: '#fff',
              fontSize: 12,
              fontWeight: 'bold'
            }
          }
        },
        series: [{
          name: '机构数量',
          type: 'map',
          geoIndex: 0,
          data: data
        }]
      }

      chart.setOption(option)
    },

    // 各区县机构排名
    initDistrictRankChart() {
      const chartDom = document.getElementById('districtRankChart')
      if (!chartDom) return

      const chart = echarts.init(chartDom)
      this.charts.districtRankChart = chart

      const data = ZHENGZHOU_DISTRICTS.map(d => ({
        name: d.name,
        value: Math.floor(Math.random() * 50 + 10)
      })).sort((a, b) => b.value - a.value)

      const option = {
        grid: {
          left: '5%',
          right: '8%',
          top: '5%',
          bottom: '5%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          axisLabel: { color: '#00d4ff' },
          axisLine: { lineStyle: { color: '#1a5c89' } },
          splitLine: { lineStyle: { color: '#1a5c89', type: 'dashed' } }
        },
        yAxis: {
          type: 'category',
          data: data.map(d => d.name),
          axisLabel: { color: '#fff', fontSize: 11 },
          axisLine: { lineStyle: { color: '#1a5c89' } }
        },
        series: [{
          type: 'bar',
          data: data.map(d => d.value),
          barWidth: '60%',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
              { offset: 0, color: '#00d4ff' },
              { offset: 1, color: '#00ff88' }
            ]),
            borderRadius: [0, 4, 4, 0]
          },
          label: {
            show: true,
            position: 'right',
            color: '#00ff88'
          }
        }]
      }

      chart.setOption(option)
    },

    // 机构类型分布
    initInstitutionTypeChart() {
      const chartDom = document.getElementById('institutionTypeChart')
      if (!chartDom) return

      const chart = echarts.init(chartDom)
      this.charts.institutionTypeChart = chart

      const data = [
        { name: '公办养老院', value: 45, itemStyle: { color: '#00d4ff' } },
        { name: '民办养老院', value: 128, itemStyle: { color: '#00ff88' } },
        { name: '社区养老中心', value: 89, itemStyle: { color: '#ffa502' } },
        { name: '乡镇敬老院', value: 64, itemStyle: { color: '#ff6b6b' } }
      ]

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}家 ({d}%)',
          backgroundColor: 'rgba(0, 20, 60, 0.9)',
          borderColor: '#00d4ff',
          textStyle: { color: '#fff' }
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center',
          textStyle: { color: '#fff', fontSize: 12 }
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['30%', '50%'],
          data: data,
          itemStyle: {
            borderRadius: 8,
            borderColor: '#0b1120',
            borderWidth: 2
          },
          label: {
            color: '#fff',
            fontSize: 12,
            formatter: '{b}\n{c}家'
          },
          labelLine: {
            lineStyle: { color: '#fff' }
          }
        }]
      }

      chart.setOption(option)
    },

    // 各区县老人分布
    initElderDistrictChart() {
      const chartDom = document.getElementById('elderDistrictChart')
      if (!chartDom) return

      const chart = echarts.init(chartDom)
      this.charts.elderDistrictChart = chart

      const data = ZHENGZHOU_DISTRICTS.map(d => ({
        name: d.name,
        value: Math.floor(Math.random() * 2000 + 500)
      })).sort((a, b) => b.value - a.value).slice(0, 8)

      const option = {
        grid: {
          left: '5%',
          right: '8%',
          top: '5%',
          bottom: '5%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          axisLabel: { color: '#00d4ff' },
          axisLine: { lineStyle: { color: '#1a5c89' } },
          splitLine: { lineStyle: { color: '#1a5c89', type: 'dashed' } }
        },
        yAxis: {
          type: 'category',
          data: data.map(d => d.name),
          axisLabel: { color: '#fff', fontSize: 11 },
          axisLine: { lineStyle: { color: '#1a5c89' } }
        },
        series: [{
          type: 'bar',
          data: data.map(d => d.value),
          barWidth: '60%',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
              { offset: 0, color: '#ffa502' },
              { offset: 1, color: '#ff6b6b' }
            ]),
            borderRadius: [0, 4, 4, 0]
          },
          label: {
            show: true,
            position: 'right',
            color: '#ffa502'
          }
        }]
      }

      chart.setOption(option)
    },

    // 月度资金趋势
    initFundTrendChart() {
      const chartDom = document.getElementById('fundTrendChart')
      if (!chartDom) return

      const chart = echarts.init(chartDom)
      this.charts.fundTrendChart = chart

      const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
      const income = []
      const expense = []

      for (let i = 0; i < 12; i++) {
        income.push(Math.floor(Math.random() * 500 + 300))
        expense.push(Math.floor(Math.random() * 200 + 100))
      }

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          backgroundColor: 'rgba(0, 20, 60, 0.9)',
          borderColor: '#00d4ff',
          textStyle: { color: '#fff' }
        },
        legend: {
          data: ['收入', '支出'],
          textStyle: { color: '#fff' },
          top: '5%'
        },
        grid: {
          left: '8%',
          right: '5%',
          top: '20%',
          bottom: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: months,
          axisLabel: { color: '#00d4ff', fontSize: 10 },
          axisLine: { lineStyle: { color: '#1a5c89' } }
        },
        yAxis: {
          type: 'value',
          name: '万元',
          axisLabel: { color: '#00d4ff' },
          axisLine: { lineStyle: { color: '#1a5c89' } },
          splitLine: { lineStyle: { color: '#1a5c89', type: 'dashed' } },
          nameTextStyle: { color: '#00d4ff' }
        },
        series: [
          {
            name: '收入',
            type: 'line',
            data: income,
            smooth: true,
            lineStyle: { color: '#00ff88', width: 3 },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(0, 255, 136, 0.4)' },
                { offset: 1, color: 'rgba(0, 255, 136, 0)' }
              ])
            },
            itemStyle: { color: '#00ff88' }
          },
          {
            name: '支出',
            type: 'line',
            data: expense,
            smooth: true,
            lineStyle: { color: '#ff6b6b', width: 3 },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(255, 107, 107, 0.4)' },
                { offset: 1, color: 'rgba(255, 107, 107, 0)' }
              ])
            },
            itemStyle: { color: '#ff6b6b' }
          }
        ]
      }

      chart.setOption(option)
    },

    // 交易渠道占比
    initChannelChart() {
      const chartDom = document.getElementById('channelChart')
      if (!chartDom) return

      const chart = echarts.init(chartDom)
      this.charts.channelChart = chart

      const data = [
        { name: '银行转账', value: 45 },
        { name: '微信支付', value: 28 },
        { name: '支付宝', value: 18 },
        { name: '现金', value: 6 },
        { name: '其他', value: 3 }
      ]

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}%',
          backgroundColor: 'rgba(0, 20, 60, 0.9)',
          borderColor: '#00d4ff',
          textStyle: { color: '#fff' }
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center',
          textStyle: { color: '#fff', fontSize: 12 }
        },
        series: [{
          type: 'pie',
          radius: ['45%', '75%'],
          center: ['30%', '50%'],
          data: data,
          itemStyle: {
            borderRadius: 8,
            borderColor: '#0b1120',
            borderWidth: 2
          },
          label: {
            color: '#fff',
            fontSize: 12,
            formatter: '{b}\n{c}%'
          },
          labelLine: { lineStyle: { color: '#fff' } }
        }]
      }

      chart.setOption(option)
    },

    // 预警类型统计
    initWarningTypeChart() {
      const chartDom = document.getElementById('warningTypeChart')
      if (!chartDom) return

      const chart = echarts.init(chartDom)
      this.charts.warningTypeChart = chart

      const data = [
        { name: '大额动账预警', value: 15, level: 'high', itemStyle: { color: '#ff4757' } },
        { name: '最低余额预警', value: 28, level: 'medium', itemStyle: { color: '#ffa502' } },
        { name: '退款过高预警', value: 8, level: 'high', itemStyle: { color: '#ff4757' } },
        { name: '异常支出预警', value: 12, level: 'medium', itemStyle: { color: '#ffa502' } },
        { name: '资金流水异常', value: 5, level: 'low', itemStyle: { color: '#2ed573' } }
      ]

      const option = {
        backgroundColor: 'transparent',
        grid: {
          left: '5%',
          right: '5%',
          top: '5%',
          bottom: '5%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: data.map(d => d.name),
          axisLabel: {
            color: '#00d4ff',
            fontSize: 10,
            rotate: 20
          },
          axisLine: { lineStyle: { color: '#1a5c89' } }
        },
        yAxis: {
          type: 'value',
          axisLabel: { color: '#00d4ff' },
          axisLine: { lineStyle: { color: '#1a5c89' } },
          splitLine: { lineStyle: { color: '#1a5c89', type: 'dashed' } }
        },
        series: [{
          type: 'bar',
          data: data.map(d => d.value),
          barWidth: '50%',
          itemStyle: {
            color: (params) => data[params.dataIndex].itemStyle.color,
            borderRadius: [4, 4, 0, 0]
          },
          label: {
            show: true,
            position: 'top',
            color: '#fff',
            fontSize: 11
          }
        }]
      }

      chart.setOption(option)
    },

    // 初始化预警数据
    initWarningData() {
      const warningTypes = ['大额动账预警', '最低余额预警', '退款过高预警', '异常支出预警', '资金流水异常']
      const institutions = ['金水区养老中心', '二七区老年公寓', '中原区养老院', '管城敬老院', '惠济区康养中心']

      const warnings = []
      for (let i = 0; i < 10; i++) {
        const type = warningTypes[Math.floor(Math.random() * warningTypes.length)]
        const inst = institutions[Math.floor(Math.random() * institutions.length)]
        const amount = Math.floor(Math.random() * 50 + 5)
        warnings.push({
          name: inst,
          value: amount
        })
      }

      this.warningList = warnings
    }
  }
}
</script>

<style scoped lang="scss">
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.data-screen {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(180deg, #1a1f35 0%, #0f1419 100%);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
}

// 顶部标题 - 减小高度
.screen-header {
  display: flex;
  align-items: center;
  height: 70px;
  padding: 0 20px;
  flex-shrink: 0;
  background: linear-gradient(180deg, rgba(0, 30, 60, 0.3) 0%, rgba(0, 15, 40, 0.5) 100%);
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg,
      transparent 0%,
      #00d4ff 10%,
      #00ff88 30%,
      #00d4ff 50%,
      #00ff88 70%,
      #00d4ff 90%,
      transparent 100%);
    opacity: 0.8;
  }

  .header-left, .header-right {
    flex: 1;
    display: flex;
    align-items: center;
  }

  .header-center {
    flex: 2;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;

    .title-box {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      background: transparent;

      .title-content {
        display: flex;
        align-items: center;
        gap: 15px;

        .title-icon {
          .dv-digital-flop {
            width: 100px;
          }
        }

        .title-text {
          font-size: 36px;
          font-weight: bold;
          color: #00d4ff;
          letter-spacing: 8px;
          text-shadow: 0 0 30px rgba(0, 212, 255, 0.8),
                       0 0 60px rgba(0, 212, 255, 0.4);
          animation: titleGlow 2s ease-in-out infinite alternate;
        }

        .title-decoration {
          width: 180px;
          height: 8px;
        }

        @keyframes titleGlow {
          from {
            text-shadow: 0 0 20px rgba(0, 212, 255, 0.6);
          }
          to {
            text-shadow: 0 0 30px rgba(0, 212, 255, 1),
                         0 0 40px rgba(0, 212, 255, 0.8),
                         0 0 50px rgba(0, 212, 255, 0.6);
          }
        }
      }

      .datetime {
        font-size: 14px;
        color: #fff;
        margin-top: 2px;
        opacity: 0.9;
        font-family: 'Courier New', monospace;
        letter-spacing: 1px;
      }
    }
  }
}

// 主内容区 - 减小padding
.screen-content {
  flex: 1;
  display: flex;
  padding: 10px 15px;
  gap: 12px;
  overflow: hidden;
}

.left-panel, .center-panel, .right-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.left-panel, .right-panel {
  flex: 3;
}

.center-panel {
  flex: 5;
}

// 面板样式 - 简洁风格
.panel-item {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  min-height: 0;
  animation: panelFadeIn 0.6s ease-out;
  overflow: hidden;

  @keyframes panelFadeIn {
    from {
      opacity: 0;
      transform: translateY(15px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  &.flex-1 {
    flex: 1;
  }

  .panel-header {
    padding: 12px 15px;
    height: 44px;
    min-height: 44px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);

    .header-title {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #e8eaed;
      font-size: 15px;
      font-weight: 500;
      letter-spacing: 1px;
    }
  }

  .panel-content {
    padding: 12px 15px;
    height: calc(100% - 44px);
    min-height: 0;
  }

  .panel-body {
    padding: 12px 15px;
    height: calc(100% - 45px);
    min-height: 0;
  }
}

// 总体概况 - 简洁风格
.overview-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  height: 100%;

  .overview-item {
    .item-box {
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      padding: 14px 10px;
      border-radius: 8px;
      background: rgba(255, 255, 255, 0.06);
      border: 1px solid rgba(255, 255, 255, 0.08);
      transition: all 0.3s ease;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
        border-color: rgba(255, 255, 255, 0.15);
        transform: translateY(-2px);
      }

      .item-icon {
        font-size: 28px;
        margin-bottom: 8px;
        display: none;
      }

      .item-value {
        font-size: 26px;
        font-weight: 600;
        margin-bottom: 6px;
        color: #fff;
      }

      .item-label {
        font-size: 13px;
        color: #9ca3af;
        margin-bottom: 6px;
      }

      .item-change {
        font-size: 12px;
        display: flex;
        align-items: center;
        gap: 2px;

        &.up {
          color: #10b981;
        }

        &.down {
          color: #ef4444;
        }
      }
    }
  }
}

// 图表容器
.chart-box {
  width: 100%;
  height: 100%;
  min-height: 160px;
}

// 地图容器 - 优化居中显示
.map-container {
  flex: 2.2;
  min-height: 320px;
  background: rgba(10, 30, 70, 0.4);
  border-radius: 12px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 212, 255, 0.15);
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(10px);
  overflow: hidden;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg,
      transparent 0%,
      #00d4ff 20%,
      #00ff88 50%,
      #00d4ff 80%,
      transparent 100%);
    opacity: 0.8;
    z-index: 1;
  }

  .map-header {
    padding: 8px 15px;
    position: relative;
    z-index: 1;

    .map-title {
      padding: 6px 16px;
      text-align: center;
      color: #00d4ff;
      font-size: 16px;
      font-weight: bold;
      letter-spacing: 4px;
      text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
      background: linear-gradient(90deg,
        transparent 0%,
        rgba(0, 212, 255, 0.1) 50%,
        transparent 100%);
      border-radius: 8px;
    }
  }

  .map-body {
    flex: 1;
    position: relative;
    padding: 8px;

    .map-chart, .map-placeholder {
      width: 100%;
      height: 100%;
    }

    .map-placeholder {
      display: flex;
      align-items: center;
      justify-content: center;
      color: #00d4ff;
    }

    .map-data-overlay {
      position: absolute;
      top: 15px;
      right: 15px;
      display: flex;
      flex-direction: column;
      gap: 10px;
      z-index: 10;
      align-items: flex-end;

      .data-card {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 12px 16px;
        border-radius: 10px;
        backdrop-filter: blur(10px);
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        transition: all 0.3s ease;
        position: relative;
        overflow: hidden;
        border: 1px solid rgba(255, 255, 255, 0.1);

        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          opacity: 0.15;
          transition: opacity 0.3s ease;
        }

        // 不同颜色主题
        &:nth-child(1) {
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.8) 0%, rgba(118, 75, 162, 0.8) 100%);
          &::before { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        }
        &:nth-child(2) {
          background: linear-gradient(135deg, rgba(0, 212, 255, 0.8) 0%, rgba(0, 255, 136, 0.8) 100%);
          &::before { background: linear-gradient(135deg, #00d4ff 0%, #00ff88 100%); }
        }
        &:nth-child(3) {
          background: linear-gradient(135deg, rgba(255, 165, 2, 0.8) 0%, rgba(255, 107, 107, 0.8) 100%);
          &::before { background: linear-gradient(135deg, #ffa502 0%, #ff6b6b 100%); }
        }

        &:hover {
          transform: translateX(5px);
          box-shadow: 0 6px 25px rgba(0, 0, 0, 0.25);

          &::before {
            opacity: 0.25;
          }
        }

        .data-icon {
          font-size: 22px;
          position: relative;
          z-index: 1;
        }

        .data-info {
          position: relative;
          z-index: 1;

          .data-label {
            font-size: 11px;
            color: #fff;
            opacity: 0.8;
            margin-bottom: 2px;
          }

          .data-value {
            font-size: 18px;
            font-weight: bold;
            color: #fff;
            font-family: 'DIN', sans-serif;
          }

          &.no-icon {
            .data-label {
              font-size: 12px;
            }

            .data-value {
              font-size: 22px;
            }
          }
        }
      }
    }
  }
}

// 预警容器 - 更紧凑
.warning-container {
  flex: 1;
  background: rgba(10, 30, 70, 0.4);
  border-radius: 12px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 212, 255, 0.15);
  backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg,
      transparent 0%,
      #00d4ff 20%,
      #00ff88 50%,
      #00d4ff 80%,
      transparent 100%);
    opacity: 0.8;
    z-index: 1;
  }

  .warning-header {
    padding: 10px 15px;
    height: 42px;
    min-height: 42px;
    position: relative;
    z-index: 1;

    .header-title {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      position: relative;
      color: #00d4ff;
      font-size: 15px;
      font-weight: bold;
      letter-spacing: 3px;
      text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);

      &::before {
        content: '';
        position: absolute;
        left: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 14px;
        background: linear-gradient(180deg, #00d4ff 0%, #00ff88 100%);
        border-radius: 2px;
      }

      &::after {
        content: '';
        position: absolute;
        right: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 14px;
        background: linear-gradient(180deg, #00ff88 0%, #00d4ff 100%);
        border-radius: 2px;
      }
    }
  }

  .warning-body {
    flex: 1;
    padding: 10px 12px;
    overflow: hidden;
    position: relative;
    z-index: 1;

    .warning-table {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;

      .table-header {
        display: flex;
        background: linear-gradient(90deg, rgba(0, 50, 100, 0.6) 0%, rgba(0, 30, 60, 0.8) 100%);
        border-radius: 8px;
        padding: 8px 0;

        .header-col {
          flex: 1;
          padding: 8px 6px;
          text-align: center;
          color: #00d4ff;
          font-size: 12px;
          font-weight: bold;

          &:first-child {
            flex: 0.5;
          }

          &:last-child {
            flex: 0.8;
          }
        }
      }

      .table-body {
        flex: 1;
        overflow-y: auto;
        padding-right: 5px;

        /* 滚动条样式 */
        &::-webkit-scrollbar {
          width: 5px;
        }

        &::-webkit-scrollbar-track {
          background: rgba(0, 30, 60, 0.2);
          border-radius: 3px;
        }

        &::-webkit-scrollbar-thumb {
          background: rgba(0, 212, 255, 0.3);
          border-radius: 3px;

          &:hover {
            background: rgba(0, 212, 255, 0.5);
          }
        }

        .table-row {
          display: flex;
          padding: 10px 0;
          border-bottom: 1px solid rgba(0, 212, 255, 0.1);
          transition: all 0.3s ease;

          &:hover {
            background: rgba(0, 50, 100, 0.3);
          }

          .row-col {
            flex: 1;
            text-align: center;
            color: #fff;
            font-size: 12px;

            &.rank {
              flex: 0.5;
              font-weight: bold;

              .rank-number {
                display: inline-block;
                width: 22px;
                height: 22px;
                line-height: 22px;
                text-align: center;
                border-radius: 4px;

                &.top {
                  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
                  color: #fff;
                  box-shadow: 0 2px 8px rgba(238, 101, 36, 0.3);
                }
              }
            }

            &.count {
              flex: 0.8;
              font-weight: bold;
              color: #00ff88;
            }
          }

          .warning-tag {
            padding: 3px 10px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: 500;

            &.high {
              background: linear-gradient(135deg, #ff4757 0%, #ee5a24 100%);
              color: #fff;
              box-shadow: 0 2px 8px rgba(238, 71, 39, 0.3);
            }

            &.medium {
              background: linear-gradient(135deg, #ffa502 0%, #ff7f50 100%);
              color: #fff;
              box-shadow: 0 2px 8px rgba(255, 165, 2, 0.3);
            }

            &.low {
              background: linear-gradient(135deg, #2ed573 0%, #26af61 100%);
              color: #fff;
            }
          }
        }
      }
    }
  }
}

// 资金监管总览 - 渐变风格
.fund-overview {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  height: 100%;

  .fund-item {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 10px 4px;
    border-radius: 10px;
    border: 1px solid rgba(255, 255, 255, 0.1);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      opacity: 0.2;
      transition: opacity 0.3s ease;
    }

    // 不同颜色主题
    &:nth-child(1) {
      background: linear-gradient(135deg, rgba(0, 255, 136, 0.15) 0%, rgba(0, 212, 255, 0.15) 100%);
      &::before { background: linear-gradient(135deg, #00ff88 0%, #00d4ff 100%); }
    }
    &:nth-child(2) {
      background: linear-gradient(135deg, rgba(0, 212, 255, 0.15) 0%, rgba(0, 150, 255, 0.15) 100%);
      &::before { background: linear-gradient(135deg, #00d4ff 0%, #0096ff 100%); }
    }
    &:nth-child(3) {
      background: linear-gradient(135deg, rgba(255, 107, 107, 0.15) 0%, rgba(255, 50, 50, 0.15) 100%);
      &::before { background: linear-gradient(135deg, #ff6b6b 0%, #ff3232 100%); }
    }
    &:nth-child(4) {
      background: linear-gradient(135deg, rgba(255, 165, 2, 0.15) 0%, rgba(255, 140, 0, 0.15) 100%);
      &::before { background: linear-gradient(135deg, #ffa502 0%, #ff8c00 100%); }
    }
    &:nth-child(5) {
      background: linear-gradient(135deg, rgba(255, 71, 87, 0.15) 0%, rgba(220, 20, 60, 0.15) 100%);
      &::before { background: linear-gradient(135deg, #ff4757 0%, #dc143c 100%); }
    }

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 6px 20px rgba(0, 212, 255, 0.2);

      &::before {
        opacity: 0.3;
      }
    }

    .fund-label {
      font-size: 11px;
      color: #fff;
      opacity: 0.85;
      margin-bottom: 4px;
      text-align: center;
      position: relative;
      z-index: 1;
    }

    .fund-value {
      font-size: 15px;
      font-weight: bold;
      font-family: 'DIN', sans-serif;
      position: relative;
      z-index: 1;
    }
  }
}

// DataV 组件样式覆盖
:deep(.dv-border-box-10) {
  background: transparent !important;
  .border-box-content {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

:deep(.dv-border-box-12) {
  background: transparent !important;
  .border-box-content {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

:deep(.dv-border-box-13) {
  background: transparent !important;
}

:deep(.dv-decoration-3) {
  width: 30px !important;
  height: 30px !important;
}

:deep(.dv-decoration-6) {
  width: 200px !important;
  height: 10px !important;
}

:deep(.dv-decoration-8) {
  height: 100% !important;
}

:deep(.dv-digital-flop) {
  .dv-digital-flop {
    text-align: center;
  }
}

// 响应式调整
@media screen and (max-width: 1680px) {
  .left-panel, .right-panel {
    flex: 2.8;
  }

  .center-panel {
    flex: 4.5;
  }

  .overview-grid {
    gap: 8px;

    .overview-item .item-box {
      padding: 10px 6px;

      .item-icon {
        font-size: 22px;
      }

      .item-value {
        font-size: 24px;
      }

      .item-label {
        font-size: 12px;
      }
    }
  }
}

@media screen and (max-width: 1440px) {
  .screen-content {
    padding: 8px 12px;
    gap: 10px;
  }

  .left-panel, .right-panel {
    flex: 2.5;
  }

  .center-panel {
    flex: 4;
  }

  .map-container {
    min-height: 350px;
  }
}
</style>
