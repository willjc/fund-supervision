<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span style="font-size: 18px; font-weight: bold;">资金监管实时大屏</span>
      </div>

      <div class="dashboard-intro">
        <p>资金监管实时大屏展示系统内资金流转、账户余额、审批状态等关键信息的实时监控界面。</p>
        <div class="feature-list">
          <h4>主要功能：</h4>
          <ul>
            <li>📊 资金总览 - 实时显示总余额、今日收支、待审批金额等</li>
            <li>💰 资金流向分析 - 24小时资金流入流出趋势图</li>
            <li>🏢 各机构资金分布 - 各养老机构账户余额排行</li>
            <li>📈 30天资金趋势 - 近一个月资金变化趋势</li>
            <li>✅ 资金审批统计 - 审通过率和审批时效分析</li>
            <li>⚠️ 预警资金监控 - 异常资金预警和处理状态</li>
          </ul>
        </div>
      </div>

      <div class="action-section">
        <el-button
          type="primary"
          size="large"
          icon="el-icon-monitor"
          @click="openBigScreen"
          class="bigscreen-button">
          打开资金监管大屏
        </el-button>

        <div class="tips">
          <el-alert
            title="使用提示"
            type="info"
            :closable="false"
            show-icon>
            <template slot="default">
              <p>• 大屏页面将在新窗口中打开，建议使用全屏模式查看</p>
              <p>• 大屏数据每30秒自动刷新一次</p>
              <p>• 建议使用1920x1080分辨率显示器以获得最佳显示效果</p>
              <p>• 大屏页面无需登录即可访问，可直接投屏到监控中心</p>
            </template>
          </el-alert>
        </div>
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
  name: 'FundSupervisionLink',
  data() {
    return {
      // 使用相对路径，避免硬编码域名导致跨环境问题
      bigScreenUrl: '/screen/fund-supervision.html'
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
          message: '资金监管大屏已在新窗口中打开',
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
  max-width: 800px;
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
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.feature-list h4 {
  margin: 0 0 15px 0;
  color: #303133;
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
}

.tips {
  margin-top: 20px;
  text-align: left;
}

.tips .el-alert {
  max-width: 600px;
  margin: 0 auto;
}

.tips p {
  margin: 5px 0;
  font-size: 14px;
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