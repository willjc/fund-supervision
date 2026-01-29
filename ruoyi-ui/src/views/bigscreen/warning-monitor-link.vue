<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span style="font-size: 18px; font-weight: bold;">预警监控大屏</span>
      </div>

      <div class="dashboard-intro">
        <p>预警监控大屏是养老机构智能预警系统的核心监控界面，实时展示各类预警信息、处理状态和趋势分析。</p>
        <div class="feature-list">
          <h4>主要功能：</h4>
          <ul>
            <li>🚨 实时预警监控 - 今日预警数量、处理状态、高风险预警统计</li>
            <li>📊 预警类型分布 - 资金异常、人员安全、设备故障等类型分析</li>
            <li>📈 预警趋势分析 - 7天/30天预警数量和处理趋势</li>
            <li>🏢 机构预警排行 - 各养老机构预警数量和处理效率排名</li>
            <li>⚡ 实时预警列表 - 最新预警信息的实时滚动展示</li>
            <li>📋 预警处理统计 - 处理率、平均处理时间、处理效果分析</li>
          </ul>
        </div>
      </div>

      <div class="action-section">
        <el-button
          type="danger"
          size="large"
          icon="el-icon-warning"
          @click="openBigScreen"
          class="bigscreen-button">
          打开预警监控大屏
        </el-button>

        <div class="tips">
          <el-alert
            title="使用提示"
            type="warning"
            :closable="false"
            show-icon>
            <template slot="default">
              <p>• 大屏页面将在新窗口中打开，建议使用全屏模式查看</p>
              <p>• 大屏数据每30秒自动刷新一次，确保信息实时性</p>
              <p>• 建议使用1920x1080分辨率显示器以获得最佳显示效果</p>
              <p>• 大屏页面无需登录即可访问，可直接投屏到监控中心</p>
              <p>• 高风险预警信息会以红色突出显示，需要及时处理</p>
            </template>
          </el-alert>
        </div>
      </div>

      <div class="warning-types">
        <h4>预警类型说明：</h4>
        <el-row :gutter="15">
          <el-col :span="8">
            <div class="warning-type-item high">
              <i class="el-icon-warning-outline"></i>
              <span>资金异常</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="warning-type-item medium">
              <i class="el-icon-user"></i>
              <span>人员安全</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="warning-type-item low">
              <i class="el-icon-setting"></i>
              <span>设备故障</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="15" style="margin-top: 15px;">
          <el-col :span="8">
            <div class="warning-type-item medium">
              <i class="el-icon-phone"></i>
              <span>服务投诉</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="warning-type-item high">
              <i class="el-icon-first-aid-kit"></i>
              <span>健康风险</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="warning-type-item low">
              <i class="el-icon-info"></i>
              <span>其他</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="technical-info">
        <h4>技术特性：</h4>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="tech-item">
              <i class="el-icon-time"></i>
              <span>实时数据更新</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="tech-item">
              <i class="el-icon-refresh"></i>
              <span>自动刷新机制</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="tech-item">
              <i class="el-icon-full-screen"></i>
              <span>全屏显示优化</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'WarningMonitorLink',
  data() {
    return {
      // 使用相对路径，避免硬编码域名导致跨环境问题
      bigScreenUrl: '/screen/warning-monitor.html'
    }
  },
  methods: {
    openBigScreen() {
      // 打开新窗口显示大屏
      const newWindow = window.open(
        this.bigScreenUrl,
        '_blank',
        'width=1920,height=1080,scrollbars=yes,resizable=yes,fullscreen=yes'
      );

      // 检查弹窗是否被阻止
      if (!newWindow) {
        this.$message({
          message: '无法打开新窗口，请检查浏览器弹窗设置',
          type: 'warning'
        });
      } else {
        this.$message({
          message: '预警监控大屏已在新窗口中打开',
          type: 'success'
        });

        // 尝试让新窗口全屏（需要用户交互）
        setTimeout(() => {
          try {
            newWindow.document.documentElement.requestFullscreen();
          } catch (e) {
            console.log('无法自动全屏，请手动按F11全屏');
          }
        }, 1000);
      }
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.box-card {
  max-width: 1000px;
  margin: 0 auto;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}

.dashboard-intro {
  margin-bottom: 30px;
}

.dashboard-intro p {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20px;
}

.feature-list {
  background: #fef2f2;
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid #ef4444;
}

.feature-list h4 {
  margin: 0 0 15px 0;
  color: #dc2626;
  font-size: 16px;
}

.feature-list ul {
  margin: 0;
  padding-left: 20px;
}

.feature-list li {
  margin-bottom: 8px;
  line-height: 1.5;
}

.action-section {
  text-align: center;
  margin: 40px 0;
}

.bigscreen-button {
  font-size: 18px;
  padding: 15px 40px;
  height: auto;
  margin-bottom: 20px;
  background: linear-gradient(45deg, #ef4444, #dc2626);
  border: none;
}

.bigscreen-button:hover {
  background: linear-gradient(45deg, #dc2626, #b91c1c);
}

.tips {
  margin-top: 20px;
  text-align: left;
}

.tips .el-alert {
  max-width: 700px;
  margin: 0 auto;
}

.tips p {
  margin: 5px 0;
  font-size: 14px;
}

.warning-types {
  margin: 40px 0;
}

.warning-types h4 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 16px;
}

.warning-type-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-radius: 8px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.warning-type-item.high {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #dc2626;
}

.warning-type-item.medium {
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.3);
  color: #d97706;
}

.warning-type-item.low {
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.3);
  color: #059669;
}

.warning-type-item i {
  font-size: 20px;
  margin-right: 8px;
}

.warning-type-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.technical-info {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.technical-info h4 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 16px;
}

.tech-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #f0f9ff;
  border-radius: 8px;
  border: 1px solid #e1f5fe;
}

.tech-item i {
  font-size: 20px;
  color: #409eff;
  margin-right: 10px;
}

.tech-item span {
  font-size: 14px;
  color: #606266;
}
</style>