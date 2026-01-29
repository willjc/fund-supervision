<template>
  <div class="app-container warning-config">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input
          v-model="queryParams.ruleName"
          placeholder="请输入规则名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预警类型" prop="warningType">
        <el-select v-model="queryParams.warningType" placeholder="请选择预警类型" clearable>
          <el-option label="预收费用超额" value="1" />
          <el-option label="押金超额" value="2" />
          <el-option label="入住人数超额" value="3" />
          <el-option label="预收总额超额" value="4" />
          <el-option label="风险保证金超低" value="5" />
          <el-option label="大额支出" value="6" />
          <el-option label="交易对方风险" value="7" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="enabled">
        <el-select v-model="queryParams.enabled" placeholder="请选择状态" clearable>
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-finished"
          size="mini"
          @click="handleBatchSave"
          v-hasPermi="['supervision:warningConfig:edit']"
        >批量保存</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 卡片式配置界面 -->
    <el-card v-loading="loading" shadow="never" class="config-card">
      <div slot="header" class="card-header">
        <span>预警规则配置</span>
        <el-tag type="info" size="small">共 {{ configList.length }} 条规则</el-tag>
      </div>

      <el-row :gutter="20">
        <el-col :span="12" v-for="item in configList" :key="item.ruleId" class="config-item-col">
          <el-card shadow="hover" class="rule-card" :class="{'switch-only': item.warningType === '7'}">
            <div slot="header" class="rule-card-header">
              <div class="rule-title">
                <i :class="getWarningIcon(item.warningType)" :style="{color: getWarningColor(item.warningType)}"></i>
                <span class="rule-name">{{ item.ruleName }}</span>
              </div>
              <el-switch
                v-model="item.enabled"
                active-value="1"
                inactive-value="0"
                @change="handleStatusChange(item)"
              ></el-switch>
            </div>

            <!-- 交易对方高风险预警只显示说明，不显示阈值配置 -->
            <div v-if="item.warningType === '7'" class="switch-only-content">
              <p class="remark-text">{{ item.remark }}</p>
              <p class="status-text">当前状态: {{ item.enabled === '1' ? '已启用' : '已禁用' }}</p>
            </div>

            <!-- 其他预警类型显示阈值配置 -->
            <el-form v-else label-width="100px" size="small" class="rule-form">
              <el-form-item :label="getThresholdLabel(item.warningType)">
                <el-input-number
                  v-model="item.thresholdValue"
                  :min="item.minValue"
                  :max="item.maxValue"
                  :precision="getPrecision(item.thresholdUnit)"
                  :disabled="item.enabled !== '1'"
                  controls-position="right"
                  style="width: 200px"
                />
                <span class="unit-label">{{ item.thresholdUnit }}</span>
              </el-form-item>

              <el-form-item label="阈值范围">
                <span class="range-text">最小: {{ item.minValue }} ~ 最大: {{ item.maxValue }} {{ item.thresholdUnit }}</span>
              </el-form-item>

              <el-form-item label="说明">
                <span class="remark-text">{{ item.remark }}</span>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { listWarningConfig, updateWarningConfig, batchUpdateWarningConfig } from '@/api/supervision/warningConfig'

export default {
  name: 'WarningConfig',
  data() {
    return {
      loading: true,
      showSearch: true,
      configList: [],
      queryParams: {
        ruleName: null,
        warningType: null,
        enabled: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listWarningConfig(this.queryParams).then(response => {
        this.configList = response.rows
        this.loading = false
      })
    },
    handleQuery() {
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleStatusChange(row) {
      const text = row.enabled === '1' ? '启用' : '禁用'
      this.$modal.confirm('确认要"' + text + '""' + row.ruleName + '"规则吗？').then(() => {
        return updateWarningConfig(row)
      }).then(() => {
        this.$modal.msgSuccess(text + '成功')
      }).catch(() => {
        row.enabled = row.enabled === '1' ? '0' : '1'
      })
    },
    handleBatchSave() {
      this.$modal.confirm('确认要批量保存所有预警规则配置吗？').then(() => {
        return batchUpdateWarningConfig(this.configList)
      }).then(() => {
        this.$modal.msgSuccess('保存成功')
        this.getList()
      })
    },
    getWarningIcon(type) {
      const iconMap = {
        '1': 'el-icon-money',
        '2': 'el-icon-coin',
        '3': 'el-icon-user-solid',
        '4': 'el-icon-bank-card',
        '5': 'el-icon-warning',
        '6': 'el-icon-wallet',
        '7': 'el-icon-warning'
      }
      return iconMap[type] || 'el-icon-bell'
    },
    getWarningColor(type) {
      const colorMap = {
        '1': '#F56C6C',
        '2': '#E6A23C',
        '3': '#F56C6C',
        '4': '#E6A23C',
        '5': '#F56C6C',
        '6': '#E6A23C',
        '7': '#F56C6C'
      }
      return colorMap[type] || '#409EFF'
    },
    getThresholdLabel(type) {
      const labelMap = {
        '1': '预收费倍数',
        '2': '押金倍数阈值',
        '3': '床位倍数阈值',
        '4': '净额倍数阈值',
        '5': '保证金比例',
        '6': '金额阈值',
        '7': '启用状态'
      }
      return labelMap[type] || '阈值参数'
    },
    getPrecision(unit) {
      if (unit === '倍') return 2
      if (unit === '百分比') return 1
      return 0
    }
  }
}
</script>

<style lang="scss" scoped>
.warning-config {
  .config-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .config-item-col {
    margin-bottom: 20px;
  }

  .rule-card {
    height: 100%;
    min-height: 240px;
    display: flex;
    flex-direction: column;

    :deep(.el-card__body) {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
    }

    &.switch-only {
      min-height: 180px;

      .switch-only-content {
        text-align: center;
        padding: 10px 0;

        .remark-text {
          color: #606266;
          font-size: 14px;
          margin-bottom: 10px;
        }

        .status-text {
          color: #409EFF;
          font-size: 14px;
          font-weight: 500;
        }
      }
    }

    .rule-form {
      width: 100%;
    }

    .rule-card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .rule-title {
        display: flex;
        align-items: center;
        gap: 8px;

        .rule-name {
          font-weight: 500;
          font-size: 14px;
        }

        i {
          font-size: 18px;
        }
      }
    }

    .unit-label {
      margin-left: 10px;
      color: #909399;
    }

    .range-text {
      color: #909399;
      font-size: 12px;
    }

    .remark-text {
      color: #606266;
      font-size: 13px;
    }
  }
}
</style>
