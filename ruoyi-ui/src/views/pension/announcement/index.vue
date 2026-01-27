<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 公告列表 -->
      <el-col :span="16">
        <el-card>
          <div slot="header">
            <span>公告列表</span>
          </div>

          <!-- 搜索 -->
          <el-form :model="queryParams" ref="queryForm" :inline="true">
            <el-form-item label="公告标题">
              <el-input v-model="queryParams.title" placeholder="请输入公告标题" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <!-- 公告列表 -->
          <div v-loading="loading" class="announcement-list">
            <div
              v-for="item in dataList"
              :key="item.id"
              class="announcement-item"
              :class="{ 'unread': !item.isRead }"
              @click="handleView(item)">
              <div class="announcement-header">
                <span class="announcement-title">
                  <el-badge v-if="!item.isRead" is-dot class="badge">{{ item.title }}</el-badge>
                  <span v-else>{{ item.title }}</span>
                </span>
                <el-tag v-if="item.isImportant" type="danger" size="mini">重要</el-tag>
              </div>
              <div class="announcement-meta">
                <span><i class="el-icon-time"></i> {{ item.publishTime }}</span>
                <span><i class="el-icon-user"></i> {{ item.publisher }}</span>
              </div>
              <div class="announcement-summary">{{ item.summary }}</div>
            </div>
          </div>

          <!-- 分页 -->
          <pagination
            v-show="total>0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getList"
          />
        </el-card>
      </el-col>

      <!-- 公告详情 -->
      <el-col :span="8">
        <el-card>
          <div slot="header">
            <span>公告详情</span>
          </div>

          <div v-if="currentAnnouncement" class="announcement-detail">
            <h3>{{ currentAnnouncement.title }}</h3>
            <div class="detail-meta">
              <div><i class="el-icon-time"></i> 发布时间：{{ currentAnnouncement.publishTime }}</div>
              <div><i class="el-icon-user"></i> 发布人：{{ currentAnnouncement.publisher }}</div>
            </div>
            <el-divider></el-divider>
            <div class="detail-content" v-html="currentAnnouncement.content"></div>

            <div v-if="currentAnnouncement.attachments && currentAnnouncement.attachments.length > 0">
              <el-divider></el-divider>
              <div class="detail-attachments">
                <div class="attachment-title">附件：</div>
                <div
                  v-for="(file, index) in currentAnnouncement.attachments"
                  :key="index"
                  class="attachment-item"
                  @click="downloadFile(file)">
                  <i class="el-icon-document"></i>
                  <span>{{ file.name }}</span>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-else description="请选择公告查看详情"></el-empty>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'AnnouncementList',
  data() {
    return {
      loading: false,
      dataList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null
      },
      currentAnnouncement: null
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询列表
    getList() {
      this.loading = true
      // TODO: 调用后端API
      setTimeout(() => {
        this.dataList = [
          {
            id: 1,
            title: '关于加强养老机构安全管理的通知',
            summary: '为进一步加强养老机构安全管理工作，保障老年人生命财产安全，现就有关事项通知如下...',
            content: '<p>各养老机构：</p><p>为进一步加强养老机构安全管理工作，保障老年人生命财产安全，现就有关事项通知如下：</p><p>一、提高安全意识...</p><p>二、加强消防安全...</p><p>三、强化食品安全...</p>',
            publishTime: '2025-01-03 10:00:00',
            publisher: '民政监管部门',
            isRead: false,
            isImportant: true,
            attachments: [
              { name: '安全管理规范.pdf', url: '/files/safety.pdf' }
            ]
          },
          {
            id: 2,
            title: '2025年度养老机构年检工作安排',
            summary: '根据相关规定，现将2025年度养老机构年检工作有关事项通知如下...',
            content: '<p>各养老机构：</p><p>根据相关规定，现将2025年度养老机构年检工作有关事项通知如下：</p><p>一、年检时间：2025年3月1日至5月31日...</p>',
            publishTime: '2025-01-02 09:00:00',
            publisher: '民政监管部门',
            isRead: true,
            isImportant: false,
            attachments: []
          }
        ]
        this.total = 2
        this.loading = false

        // 默认显示第一条
        if (this.dataList.length > 0 && !this.currentAnnouncement) {
          this.currentAnnouncement = this.dataList[0]
        }
      }, 500)
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 重置
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 查看公告
    handleView(item) {
      this.currentAnnouncement = item
      // 标记为已读
      if (!item.isRead) {
        item.isRead = true
        // TODO: 调用后端API标记已读
      }
    },
    // 下载附件
    downloadFile(file) {
      this.$message.success('下载文件：' + file.name)
      // TODO: 实现文件下载
    }
  }
}
</script>

<style scoped lang="scss">
.announcement-list {
  min-height: 400px;

  .announcement-item {
    padding: 15px;
    border-bottom: 1px solid #ebeef5;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: #f5f7fa;
    }

    &.unread {
      background: #ecf5ff;
    }
  }

  .announcement-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
  }

  .announcement-title {
    font-size: 16px;
    font-weight: bold;
    color: #303133;

    .badge {
      margin-right: 5px;
    }
  }

  .announcement-meta {
    display: flex;
    gap: 20px;
    font-size: 12px;
    color: #909399;
    margin-bottom: 10px;

    i {
      margin-right: 5px;
    }
  }

  .announcement-summary {
    font-size: 14px;
    color: #606266;
    line-height: 1.5;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
}

.announcement-detail {
  h3 {
    font-size: 20px;
    color: #303133;
    margin-bottom: 15px;
  }

  .detail-meta {
    font-size: 13px;
    color: #909399;

    div {
      margin-bottom: 8px;

      i {
        margin-right: 5px;
      }
    }
  }

  .detail-content {
    font-size: 14px;
    line-height: 1.8;
    color: #606266;

    ::v-deep p {
      margin-bottom: 10px;
    }
  }

  .detail-attachments {
    .attachment-title {
      font-size: 14px;
      font-weight: bold;
      margin-bottom: 10px;
    }

    .attachment-item {
      display: flex;
      align-items: center;
      padding: 8px 12px;
      background: #f5f7fa;
      border-radius: 4px;
      margin-bottom: 8px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: #e6e9ee;
      }

      i {
        margin-right: 8px;
        color: #409eff;
      }
    }
  }
}
</style>
