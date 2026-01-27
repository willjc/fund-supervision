<template>
  <div class="app-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="title">
        <i class="el-icon-edit"></i>
        维护入住人信息
      </h2>
      <p class="subtitle">编辑入住人的基本信息</p>
    </div>

    <!-- 表单 -->
    <el-card shadow="hover" v-loading="loading">
      <el-form ref="updateForm" :model="form" :rules="rules" label-width="120px">
        <!-- 基本信息 -->
        <el-divider content-position="left">
          <i class="el-icon-user"></i> 入住人基本信息
        </el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住人姓名" prop="elderName">
              <el-input v-model="form.elderName" placeholder="请输入入住人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
                <el-option
                  v-for="dict in dict.type.elder_gender"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="form.birthDate"
                type="date"
                placeholder="请选择出生日期"
                value-format="yyyy-MM-dd"
                style="width: 100%;"
                @change="calculateAgeFromBirthDate">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="form.age" :min="1" :max="120" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="护理等级" prop="careLevel">
              <el-select v-model="form.careLevel" placeholder="请选择护理等级" style="width: 100%">
                <el-option
                  v-for="dict in dict.type.elder_care_level"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状况" prop="healthStatus">
              <el-input v-model="form.healthStatus" placeholder="请输入健康状况" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="家庭住址" prop="address">
          <el-input v-model="form.address" type="textarea" placeholder="请输入家庭住址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系电话" prop="emergencyPhone">
              <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="特殊需求" prop="specialNeeds">
          <el-input v-model="form.specialNeeds" type="textarea" placeholder="请输入特殊需求" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="form-footer">
        <el-button type="primary" size="medium" @click="submitForm">
          <i class="el-icon-check"></i> 保存
        </el-button>
        <el-button size="medium" @click="cancel">
          <i class="el-icon-close"></i> 取消
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getElderInfo, updateElderInfo } from "@/api/elder/elderInfo";

export default {
  name: "ElderUpdate",
  dicts: ['elder_gender', 'elder_care_level'],
  data() {
    return {
      loading: false,
      elderId: null,
      form: {
        elderId: null,
        elderName: null,
        gender: null,
        idCard: null,
        birthDate: null,
        age: null,
        phone: null,
        careLevel: null,
        healthStatus: null,
        address: null,
        emergencyContact: null,
        emergencyPhone: null,
        specialNeeds: null,
        remark: null
      },
      rules: {
        elderName: [
          { required: true, message: "请输入入住人姓名", trigger: "blur" }
        ],
        gender: [
          { required: true, message: "请选择性别", trigger: "change" }
        ],
        idCard: [
          { required: true, message: "请输入身份证号", trigger: "blur" },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: "身份证号格式不正确", trigger: "blur" }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" }
        ],
        emergencyPhone: [
          { required: true, message: "请输入紧急联系电话", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.elderId = this.$route.query.elderId;
    if (this.elderId) {
      this.getElderDetail();
    }
  },
  methods: {
    // 获取老人详细信息
    getElderDetail() {
      this.loading = true;
      getElderInfo(this.elderId).then(response => {
        this.form = response.data;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    // 出生日期变化时自动计算年龄
    calculateAgeFromBirthDate() {
      if (this.form.birthDate) {
        const birthDate = new Date(this.form.birthDate);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
          age--;
        }
        this.form.age = age;
      }
    },
    // 提交表单
    submitForm() {
      this.$refs["updateForm"].validate(valid => {
        if (valid) {
          this.loading = true;
          updateElderInfo(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.loading = false;
            this.$router.push('/pension/elder/list');
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    },
    // 取消
    cancel() {
      this.$router.push('/pension/elder/list');
    }
  }
};
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}

.page-header .title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.page-header .subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.form-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}
</style>
