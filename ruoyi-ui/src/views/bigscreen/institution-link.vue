<template>
  <div class="bigscreen-link">
    <div class="link-container">
      <div class="header-info">
        <h2>养老机构分布大屏</h2>
        <p>点击下方按钮打开全屏数据大屏</p>
      </div>

      <div class="action-buttons">
        <el-button
          type="primary"
          size="large"
          icon="el-icon-monitor"
          @click="openBigScreen">
          打开全屏大屏
        </el-button>

        <el-button
          type="success"
          size="large"
          icon="el-icon-copy-document"
          @click="copyLink">
          复制大屏链接
        </el-button>
      </div>

      <div class="info-cards">
        <el-card class="info-card">
          <div slot="header">
            <span><i class="el-icon-data-line"></i> 大屏功能</span>
          </div>
          <ul>
            <li>✅ 实时数据展示</li>
            <li>✅ 机构分布统计</li>
            <li>✅ 床位使用情况</li>
            <li>✅ 老人年龄分布</li>
            <li>✅ 30秒自动刷新</li>
          </ul>
        </el-card>

        <el-card class="info-card">
          <div slot="header">
            <span><i class="el-icon-setting"></i> 技术特性</span>
          </div>
          <ul>
            <li>🖥️ 全屏显示优化</li>
            <li>📱 响应式设计</li>
            <li>🎨 炫酷视觉效果</li>
            <li>⚡ 实时数据更新</li>
            <li>🔗 无需登录访问</li>
          </ul>
        </el-card>
      </div>

      <div class="usage-tips">
        <el-alert
          title="使用说明"
          type="info"
          :closable="false"
          show-icon>
          <template slot="default">
            <p>1. 点击"打开全屏大屏"按钮，将在新窗口中打开数据大屏</p>
            <p>2. 大屏页面无需登录即可访问，适合在电视墙、监控中心展示</p>
            <p>3. 大屏页面会每30秒自动刷新数据，确保数据实时性</p>
            <p>4. 建议使用Chrome、Edge等现代浏览器以获得最佳显示效果</p>
          </template>
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'InstitutionLink',
  data() {
    return {
      bigScreenUrl: window.location.origin + '/screen/institution-distribution.html'
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

      if (!newWindow) {
        this.$message({
          message: '无法打开新窗口，请检查浏览器弹窗设置',
          type: 'warning'
        });
      } else {
        this.$message({
          message: '数据大屏已在新窗口中打开',
          type: 'success'
        });
      }
    },

    copyLink() {
      // 复制大屏链接到剪贴板
      const textArea = document.createElement('textarea');
      textArea.value = this.bigScreenUrl;
      document.body.appendChild(textArea);
      textArea.select();

      try {
        document.execCommand('copy');
        this.$message({
          message: '大屏链接已复制到剪贴板',
          type: 'success'
        });
      } catch (err) {
        this.$message({
          message: '复制失败，请手动复制链接：' + this.bigScreenUrl,
          type: 'error'
        });
      }

      document.body.removeChild(textArea);
    }
  }
}
</script>

<style scoped>
.bigscreen-link {
  padding: 20px;
  min-height: calc(100vh - 120px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.link-container {
  max-width: 1200px;
  margin: 0 auto;
}

.header-info {
  text-align: center;
  margin-bottom: 40px;
}

.header-info h2 {
  font-size: 32px;
  color: #2c3e50;
  margin-bottom: 10px;
  background: linear-gradient(45deg, #3498db, #2980b9);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-info p {
  font-size: 16px;
  color: #7f8c8d;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 40px;
}

.action-buttons .el-button {
  padding: 15px 30px;
  font-size: 16px;
  border-radius: 25px;
  transition: all 0.3s ease;
}

.action-buttons .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.info-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.info-card {
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.info-card .el-card__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: bold;
  border-radius: 15px 15px 0 0;
}

.info-card ul {
  list-style: none;
  padding: 0;
}

.info-card li {
  padding: 8px 0;
  border-bottom: 1px solid #ecf0f1;
  transition: all 0.3s ease;
}

.info-card li:last-child {
  border-bottom: none;
}

.info-card li:hover {
  background: #f8f9fa;
  padding-left: 10px;
  border-radius: 5px;
}

.usage-tips {
  margin-top: 20px;
}

.usage-tips .el-alert {
  border-radius: 10px;
  border: none;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
}

.usage-tips p {
  margin: 5px 0;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .info-cards {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
    align-items: center;
  }

  .action-buttons .el-button {
    width: 200px;
  }
}
</style>