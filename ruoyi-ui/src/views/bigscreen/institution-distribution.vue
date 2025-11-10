<template>
  <div class="bigscreen-container">
    <div class="header">
      <h1 class="title">养老机构分布大数据平台</h1>
      <div class="time">{{ currentTime }}</div>
    </div>

    <div class="main-content">
      <!-- 左侧区域 -->
      <div class="left-panel">
        <!-- 机构统计卡片 -->
        <div class="stat-cards">
          <div class="card">
            <div class="card-title">机构总数</div>
            <div class="card-value">{{ statistics.totalInstitutions || 0 }}</div>
            <div class="card-unit">家</div>
          </div>
          <div class="card">
            <div class="card-title">已审批机构</div>
            <div class="card-value">{{ statistics.approvedInstitutions || 0 }}</div>
            <div class="card-unit">家</div>
          </div>
          <div class="card">
            <div class="card-title">入住老人</div>
            <div class="card-value">{{ statistics.totalElders || 0 }}</div>
            <div class="card-unit">人</div>
          </div>
          <div class="card">
            <div class="card-title">床位总数</div>
            <div class="card-value">{{ statistics.totalBeds || 0 }}</div>
            <div class="card-unit">张</div>
          </div>
        </div>

        <!-- 机构等级分布饼图 -->
        <div class="chart-container">
          <div class="chart-title">机构等级分布</div>
          <div id="levelChart" class="chart"></div>
        </div>

        <!-- 床位使用情况饼图 -->
        <div class="chart-container">
          <div class="chart-title">床位使用情况</div>
          <div id="bedChart" class="chart"></div>
        </div>
      </div>

      <!-- 中间区域 - 地图 -->
      <div class="center-panel">
        <div class="map-container">
          <div class="map-title">养老机构地理分布</div>
          <div id="mapChart" class="map-chart"></div>
        </div>

        <!-- 实时数据条 -->
        <div class="realtime-bar">
          <div class="realtime-item">
            <span class="realtime-label">今日入住</span>
            <span class="realtime-value">{{ realtimeData.todayCheckins || 0 }}</span>
          </div>
          <div class="realtime-item">
            <span class="realtime-label">今日退住</span>
            <span class="realtime-value">{{ realtimeData.todayCheckouts || 0 }}</span>
          </div>
          <div class="realtime-item">
            <span class="realtime-label">今日缴费</span>
            <span class="realtime-value">{{ realtimeData.todayPayments || 0 }}</span>
          </div>
          <div class="realtime-item">
            <span class="realtime-label">活跃预警</span>
            <span class="realtime-value warning">{{ realtimeData.activeWarnings || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧区域 -->
      <div class="right-panel">
        <!-- 老人年龄分布柱状图 -->
        <div class="chart-container">
          <div class="chart-title">老人年龄分布</div>
          <div id="ageChart" class="chart"></div>
        </div>

        <!-- 床位使用率趋势图 -->
        <div class="chart-container">
          <div class="chart-title">近7天床位使用率趋势</div>
          <div id="trendChart" class="chart"></div>
        </div>

        <!-- 机构审批状态 -->
        <div class="status-container">
          <div class="status-title">机构审批状态</div>
          <div class="status-list">
            <div class="status-item">
              <span class="status-dot pending"></span>
              <span class="status-label">待审批</span>
              <span class="status-value">{{ statistics.pendingInstitutions || 0 }}</span>
            </div>
            <div class="status-item">
              <span class="status-dot approved"></span>
              <span class="status-label">已审批</span>
              <span class="status-value">{{ statistics.approvedInstitutions || 0 }}</span>
            </div>
            <div class="status-item">
              <span class="status-dot rejected"></span>
              <span class="status-label">已驳回</span>
              <span class="status-value">{{ statistics.rejectedInstitutions || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import {
  getInstitutionDistribution,
  getInstitutionStatistics,
  getInstitutionLevels,
  getBedUsage,
  getElderAgeDistribution,
  getRealtimeData
} from '@/api/pension/bigscreen'

export default {
  name: 'InstitutionDistribution',
  data() {
    return {
      currentTime: '',
      timer: null,
      statistics: {},
      realtimeData: {},
      charts: {}
    }
  },
  mounted() {
    this.initTime()
    this.initCharts()
    this.loadData()

    // 定时刷新数据
    this.timer = setInterval(() => {
      this.updateTime()
      this.loadRealtimeData()
    }, 30000) // 30秒刷新一次
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
    // 销毁图表实例
    Object.values(this.charts).forEach(chart => {
      chart && chart.dispose()
    })
  },
  methods: {
    initTime() {
      this.updateTime()
    },

    updateTime() {
      const now = new Date()
      this.currentTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },

    initCharts() {
      this.$nextTick(() => {
        this.initMapChart()
        this.initLevelChart()
        this.initBedChart()
        this.initAgeChart()
        this.initTrendChart()
      })
    },

    initMapChart() {
      const chartDom = document.getElementById('mapChart')
      if (chartDom) {
        this.charts.mapChart = echarts.init(chartDom)
      }
    },

    initLevelChart() {
      const chartDom = document.getElementById('levelChart')
      if (chartDom) {
        this.charts.levelChart = echarts.init(chartDom)
      }
    },

    initBedChart() {
      const chartDom = document.getElementById('bedChart')
      if (chartDom) {
        this.charts.bedChart = echarts.init(chartDom)
      }
    },

    initAgeChart() {
      const chartDom = document.getElementById('ageChart')
      if (chartDom) {
        this.charts.ageChart = echarts.init(chartDom)
      }
    },

    initTrendChart() {
      const chartDom = document.getElementById('trendChart')
      if (chartDom) {
        this.charts.trendChart = echarts.init(chartDom)
      }
    },

    async loadData() {
      try {
        // 并行加载所有数据
        const [
          distributionRes,
          statisticsRes,
          levelsRes,
          bedRes,
          ageRes,
          realtimeRes
        ] = await Promise.all([
          getInstitutionDistribution(),
          getInstitutionStatistics(),
          getInstitutionLevels(),
          getBedUsage(),
          getElderAgeDistribution(),
          getRealtimeData()
        ])

        this.statistics = statisticsRes.data
        this.realtimeData = realtimeRes.data

        // 更新图表
        this.updateMapChart(distributionRes.data)
        this.updateLevelChart(levelsRes.data)
        this.updateBedChart(bedRes.data)
        this.updateAgeChart(ageRes.data)
        this.updateTrendChart()
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },

    loadRealtimeData() {
      getRealtimeData().then(response => {
        this.realtimeData = response.data
      }).catch(error => {
        console.error('加载实时数据失败:', error)
      })
    },

    updateMapChart(data) {
      if (!this.charts.mapChart || !data) return

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}家'
        },
        series: [{
          name: '机构数量',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '50%'],
          roseType: 'radius',
          data: data,
          itemStyle: {
            borderRadius: 8
          },
          label: {
            show: true,
            formatter: '{b}\n{c}家'
          }
        }],
        color: ['#37a2da', '#32c5e9', '#9fe6b8', '#ffdb5c', '#ff9f7f', '#fb7293', '#e7bcf3', '#8378ea']
      }

      this.charts.mapChart.setOption(option)
    },

    updateLevelChart(data) {
      if (!this.charts.levelChart || !data) return

      const option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          textStyle: { color: '#fff' }
        },
        series: [{
          name: '机构等级',
          type: 'pie',
          radius: '50%',
          data: data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }],
        color: ['#5470c6', '#91cc75', '#fac858', '#ee6666']
      }

      this.charts.levelChart.setOption(option)
    },

    updateBedChart(data) {
      if (!this.charts.bedChart || !data) return

      const option = {
        tooltip: {
          trigger: 'item'
        },
        series: [{
          name: '床位使用',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '18',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: data
        }],
        color: ['#3ba272', '#fc8452', '#9a60b4']
      }

      this.charts.bedChart.setOption(option)
    },

    updateAgeChart(data) {
      if (!this.charts.ageChart || !data) return

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.name),
          axisLabel: { color: '#fff' },
          axisLine: { lineStyle: { color: '#fff' } }
        },
        yAxis: {
          type: 'value',
          axisLabel: { color: '#fff' },
          axisLine: { lineStyle: { color: '#fff' } },
          splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
        },
        series: [{
          name: '老人数量',
          type: 'bar',
          data: data.map(item => item.value),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#83bff6' },
              { offset: 0.5, color: '#188df0' },
              { offset: 1, color: '#188df0' }
            ])
          }
        }]
      }

      this.charts.ageChart.setOption(option)
    },

    updateTrendChart() {
      if (!this.charts.trendChart) return

      // 生成最近7天的模拟数据
      const dates = []
      const values = []
      const today = new Date()

      for (let i = 6; i >= 0; i--) {
        const date = new Date(today)
        date.setDate(today.getDate() - i)
        dates.push(date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }))
        values.push((75 + Math.random() * 15).toFixed(1)) // 75-90%的使用率
      }

      const option = {
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates,
          axisLabel: { color: '#fff' },
          axisLine: { lineStyle: { color: '#fff' } }
        },
        yAxis: {
          type: 'value',
          max: 100,
          axisLabel: {
            color: '#fff',
            formatter: '{value}%'
          },
          axisLine: { lineStyle: { color: '#fff' } },
          splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
        },
        series: [{
          name: '床位使用率',
          type: 'line',
          smooth: true,
          data: values,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(130, 194, 255, 0.5)' },
              { offset: 1, color: 'rgba(130, 194, 255, 0.1)' }
            ])
          },
          lineStyle: {
            color: '#82c2ff'
          }
        }]
      }

      this.charts.trendChart.setOption(option)
    }
  }
}
</script>

<style scoped>
.bigscreen-container {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #0c1445 0%, #1e3c72 50%, #2a5298 100%);
  color: #fff;
  font-family: 'Microsoft YaHei', sans-serif;
  overflow: hidden;
}

.header {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  background: rgba(0, 0, 0, 0.2);
}

.title {
  font-size: 36px;
  font-weight: bold;
  background: linear-gradient(90deg, #00d4ff, #ffffff, #00d4ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
}

.time {
  font-size: 18px;
  color: #00d4ff;
}

.main-content {
  display: flex;
  height: calc(100vh - 80px);
  padding: 20px;
  gap: 20px;
}

.left-panel, .right-panel {
  width: 25%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.center-panel {
  width: 50%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stat-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.card {
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 10px;
  padding: 20px;
  text-align: center;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 212, 255, 0.3);
  border-color: rgba(0, 212, 255, 0.6);
}

.card-title {
  font-size: 14px;
  color: #aaa;
  margin-bottom: 10px;
}

.card-value {
  font-size: 32px;
  font-weight: bold;
  color: #00d4ff;
  margin-bottom: 5px;
}

.card-unit {
  font-size: 12px;
  color: #888;
}

.chart-container, .map-container, .status-container {
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 10px;
  padding: 20px;
  backdrop-filter: blur(10px);
  flex: 1;
}

.chart-title, .map-title, .status-title {
  font-size: 16px;
  color: #00d4ff;
  margin-bottom: 15px;
  text-align: center;
}

.chart {
  height: 250px;
}

.map-chart {
  height: 400px;
}

.realtime-bar {
  display: flex;
  justify-content: space-around;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 10px;
  padding: 20px;
  backdrop-filter: blur(10px);
}

.realtime-item {
  text-align: center;
}

.realtime-label {
  display: block;
  font-size: 12px;
  color: #aaa;
  margin-bottom: 8px;
}

.realtime-value {
  font-size: 24px;
  font-weight: bold;
  color: #00ff88;
}

.realtime-value.warning {
  color: #ff6b6b;
}

.status-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.status-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 5px;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 10px;
}

.status-dot.pending {
  background: #ffc107;
}

.status-dot.approved {
  background: #28a745;
}

.status-dot.rejected {
  background: #dc3545;
}

.status-label {
  flex: 1;
  font-size: 14px;
}

.status-value {
  font-size: 16px;
  font-weight: bold;
  color: #00d4ff;
}
</style>