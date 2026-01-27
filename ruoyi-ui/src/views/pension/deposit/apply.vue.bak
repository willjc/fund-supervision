<template>
  <div class="app-container">
    <h1>入住人押金使用申请</h1>

    <el-card>
      <div slot="header">
        <span>基本信息</span>
      </div>

      <el-form :model="form" label-width="120px">
        <el-form-item label="选择入住人">
          <el-select v-model="form.residentId" placeholder="请选择入住人" style="width: 100%">
            <el-option
              v-for="resident in residentList"
              :key="resident.residentId"
              :label="resident.elderName"
              :value="resident.residentId">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="申请金额">
          <el-input-number v-model="form.amount" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>

        <el-form-item label="使用事由">
          <el-select v-model="form.purpose" placeholder="请选择使用事由" style="width: 100%">
            <el-option label="医疗费用" value="医疗费用"></el-option>
            <el-option label="个人物品购买" value="个人物品购买"></el-option>
            <el-option label="特殊护理服务" value="特殊护理服务"></el-option>
            <el-option label="其他用途" value="其他用途"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="申请原因">
          <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请详细说明申请原因"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitApplication">提交申请</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { listResident } from "@/api/elder/resident";
import { addDepositUse } from "@/api/elder/depositUse";

export default {
  name: "PensionDepositApply",
  data() {
    return {
      // 入住人列表
      residentList: [],

      // 表单数据
      form: {
        residentId: null,
        amount: null,
        purpose: '',
        reason: ''
      }
    };
  },
  created() {
    this.getResidentList();
  },
  methods: {
    /** 获取入住人列表 */
    getResidentList() {
      listResident({}).then(response => {
        this.residentList = response.rows;
      });
    },

    /** 提交申请 */
    submitApplication() {
      if (!this.form.residentId) {
        this.$message.error("请选择入住人");
        return;
      }
      if (!this.form.amount) {
        this.$message.error("请输入申请金额");
        return;
      }
      if (!this.form.purpose) {
        this.$message.error("请选择使用事由");
        return;
      }
      if (!this.form.reason) {
        this.$message.error("请填写申请原因");
        return;
      }

      const applicationData = {
        ...this.form
      };

      addDepositUse(applicationData).then(response => {
        this.$message.success("申请提交成功");
        this.resetForm();
      }).catch(() => {
        this.$message.error("提交失败，请重试");
      });
    },

    /** 重置表单 */
    resetForm() {
      this.form = {
        residentId: null,
        amount: null,
        purpose: '',
        reason: ''
      };
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

h1 {
  margin-bottom: 20px;
  color: #303133;
}
</style>