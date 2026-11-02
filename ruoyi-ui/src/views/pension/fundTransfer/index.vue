<template>
  <div class="app-container payout-workbench">
    <header class="workbench-head">
      <div class="head-copy">
        <p class="eyebrow">BANK PAYOUT</p>
        <h1>拨付流程</h1>
        <p>从机构开户、商户验证、701 签约到收单入账、审批、702 发送、703 查单和 708/709 对账，按步骤核对每一步是否就绪。</p>
      </div>
      <div class="head-actions">
        <div class="selector-block">
          <span class="selector-label">养老机构</span>
          <el-select v-model="institutionId" placeholder="请选择机构" filterable style="width: 240px">
            <el-option v-for="item in institutions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </div>
        <el-button icon="el-icon-refresh" :disabled="!institutionId" @click="refresh">刷新</el-button>
      </div>
    </header>

    <el-alert v-if="loaded && !institutions.length" title="当前账号未关联任何养老机构，无法查看拨付流程。" type="warning" :closable="false" show-icon />
    <el-empty v-else-if="!institutionId" description="请选择养老机构查看拨付流程与单据" />

    <el-tabs v-else v-model="activeTab" class="workbench-tabs">
      <el-tab-pane label="流程总览" name="flow">
        <payout-flow v-if="activeTab === 'flow'" ref="flow" :institution-id="institutionId" @open-documents="activeTab = 'documents'" />
      </el-tab-pane>
      <el-tab-pane label="拨付单据" name="documents">
        <payout-list v-if="activeTab === 'documents'" ref="list" :institution-id="institutionId" @changed="onChanged" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { getUserInstitutions } from '@/api/pension/bank'
import PayoutFlow from './flow'
import PayoutList from './payoutList'

export default {
  name: 'FundTransfer',
  components: { PayoutFlow, PayoutList },
  data() {
    return { activeTab: 'flow', institutions: [], institutionId: null, loaded: false }
  },
  created() {
    getUserInstitutions().then(response => {
      this.institutions = (response.data || []).map(item => ({
        id: item.institution_id != null ? item.institution_id : item.institutionId,
        name: item.institution_name || item.institutionName
      }))
      if (this.institutions.length) this.institutionId = this.institutions[0].id
    }).finally(() => { this.loaded = true })
  },
  methods: {
    refresh() {
      if (this.activeTab === 'flow' && this.$refs.flow) this.$refs.flow.load()
      if (this.activeTab === 'documents' && this.$refs.list) this.$refs.list.getList()
    },
    onChanged() {
      if (this.$refs.flow) this.$refs.flow.load()
    }
  }
}
</script>

<style scoped>
.payout-workbench { color: #14273f; }
.workbench-head { display: flex; flex-wrap: wrap; gap: 18px; align-items: flex-end; justify-content: space-between; padding: 22px 24px; margin-bottom: 16px; background: linear-gradient(135deg, #ffffff 0%, #eef4fb 100%); border: 1px solid #e3eaf3; border-radius: 14px; }
.head-copy { max-width: 760px; }
.eyebrow { margin: 0 0 6px; color: #1f65bd; font-size: 11px; font-weight: 700; letter-spacing: .18em; }
.head-copy h1 { margin: 0 0 8px; font-family: "Songti SC", "STSong", serif; font-size: 30px; line-height: 1.2; }
.head-copy p { margin: 0; color: #5b6b80; font-size: 13px; line-height: 1.8; }
.head-actions { display: flex; gap: 12px; align-items: flex-end; }
.selector-label { display: block; margin-bottom: 5px; color: #7a889b; font-size: 12px; }
.workbench-tabs { padding: 0 2px; }
.workbench-tabs ::v-deep .el-tabs__header { margin-bottom: 16px; }
.workbench-tabs ::v-deep .el-tabs__item { height: 42px; font-size: 15px; }
@media (max-width: 768px) {
  .workbench-head { padding: 18px; }
  .head-actions { width: 100%; align-items: stretch; }
  .selector-block { flex: 1; }
  .head-actions ::v-deep .el-select { width: 100% !important; }
}
</style>
