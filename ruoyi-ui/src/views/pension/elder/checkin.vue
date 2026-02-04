<template>
  <div class="app-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="title">
        <i class="el-icon-user-solid"></i>
        新增入驻
      </h2>
      <p class="subtitle">填写入住人信息、选择床位并设置费用后提交,系统将自动生成订单</p>
    </div>

    <!-- 单页面表单 -->
    <el-card shadow="hover">
      <el-form ref="checkinForm" :model="form" :rules="rules" label-width="120px">
        <!-- 基本信息 -->
        <el-divider content-position="left">
          <i class="el-icon-user"></i> 入住人基本信息
        </el-divider>
        <el-form-item label="选择老人">
          <el-select
            v-model="selectedElderId"
            placeholder="可选择已录入的老人信息自动填充"
            filterable
            clearable
            style="width: 100%"
            @change="handleElderChange">
            <el-option
              v-for="elder in elderOptions"
              :key="elder.elderId"
              :label="`${elder.elderName} (${elder.idCard})`"
              :value="elder.elderId">
            </el-option>
          </el-select>
          <div style="font-size: 12px; color: #909399; margin-top: 4px;">
            <i class="el-icon-info"></i> 请先选择老人，信息将自动填充
          </div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住人姓名" prop="elderName">
              <el-input v-model="form.elderName" placeholder="请先选择老人" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-input v-model="genderLabel" placeholder="请先选择老人" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请先选择老人" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-input v-model="form.birthDate" placeholder="请先选择老人" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input v-model="form.age" placeholder="请先选择老人" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请先选择老人" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="护理等级" prop="careLevel">
              <el-input v-model="careLevelLabel" placeholder="请先选择老人" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状况" prop="healthStatus">
              <el-input v-model="form.healthStatus" placeholder="请先选择老人" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="家庭住址" prop="address">
          <el-input v-model="form.address" type="textarea" placeholder="请先选择老人" disabled />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="请先选择老人" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系电话" prop="emergencyPhone">
              <el-input v-model="form.emergencyPhone" placeholder="请先选择老人" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="特殊需求" prop="specialNeeds">
          <el-input v-model="form.specialNeeds" type="textarea" placeholder="请先选择老人" disabled />
        </el-form-item>

        <!-- 床位选择 -->
        <el-divider content-position="left">
          <i class="el-icon-s-home"></i> 床位选择
        </el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择养老机构" prop="institutionId">
              <el-select
                v-model="form.institutionId"
                placeholder="请先选择养老机构"
                filterable
                style="width: 100%"
                @change="handleInstitutionChange">
                <el-option
                  v-for="inst in institutionList"
                  :key="inst.institutionId"
                  :label="inst.institutionName"
                  :value="inst.institutionId">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选择床位" prop="bedId">
              <el-select
                v-model="form.bedId"
                placeholder="请先选择养老机构"
                filterable
                :disabled="!form.institutionId"
                style="width: 100%"
                @change="handleBedChange">
                <el-option
                  v-for="bed in availableBeds"
                  :key="bed.bedId"
                  :label="`${bed.roomNumber}-${bed.bedNumber} (${getBedTypeName(bed.bedType)}) - ¥${bed.price}/月`"
                  :value="bed.bedId">
                  <div style="display: flex; justify-content: space-between;">
                    <span>{{ bed.roomNumber }}-{{ bed.bedNumber }} ({{ getBedTypeName(bed.bedType) }})</span>
                    <span style="color: #409EFF; font-weight: bold;">¥{{ bed.price }}/月</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住日期" prop="checkInDate">
              <el-date-picker
                v-model="form.checkInDate"
                type="date"
                placeholder="请选择入住日期"
                value-format="yyyy-MM-dd"
                style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-alert
          v-if="selectedBed"
          :title="`已选择床位：${selectedBed.roomNumber}-${selectedBed.bedNumber}，类型：${getBedTypeName(selectedBed.bedType)}，月费：¥${selectedBed.price}`"
          type="success"
          :closable="false"
          style="margin-bottom: 20px;">
        </el-alert>

        <!-- 费用设置 -->
        <el-divider content-position="left">
          <i class="el-icon-wallet"></i> 费用设置
        </el-divider>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="床位费" prop="bedFee">
              <el-input-number
                v-model="form.bedFee"
                :min="0"
                :precision="2"
                style="width: 100%;"
                @change="onBedFeeChange" />
              <span style="margin-left: 10px; color: #909399;">元/月</span>
              <div style="font-size: 12px; color: #909399; margin-top: 4px;" v-if="selectedBed">
                标准价格：¥{{ selectedBed.price }}/月
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="护理费" prop="careFee">
              <el-input-number
                v-model="form.careFee"
                :min="0"
                :precision="2"
                style="width: 100%;"
                @change="onCareFeeChange" />
              <span style="margin-left: 10px; color: #909399;">元/月</span>
              <div style="font-size: 12px; color: #909399; margin-top: 4px;" v-if="selectedBed">
                标准价格：¥{{ getCurrentCareFee() }}/月 ({{ getCareLevelText() }})
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="餐费档次" prop="mealConfigId">
              <el-select
                v-model="form.mealConfigId"
                placeholder="请选择餐费档次"
                style="width: 100%;"
                @change="handleMealChange"
                clearable>
                <el-option
                  v-for="meal in availableMeals"
                  :key="meal.configId"
                  :label="`${meal.mealLevel} - ¥${meal.price}/月`"
                  :value="meal.configId">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="月服务费">
              <el-input
                :value="formatMoney(monthlyFeeTotal)"
                disabled
                style="width: 100%;" />
              <span style="margin-left: 10px; color: #909399;">元/月</span>
              <div style="font-size: 12px; color: #409EFF; margin-top: 4px;">
                = 床位费 + 护理费 + 餐费
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="入驻月数" prop="monthCount">
              <el-input-number
                v-model="form.monthCount"
                :min="1"
                :max="120"
                :precision="0"
                style="width: 100%;"
                @change="calculateTotal" />
              <span style="margin-left: 10px; color: #909399;">个月</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="服务费小计">
              <el-input
                :value="formatMoney(serviceFeeTotal)"
                disabled
                style="width: 100%;" />
              <span style="margin-left: 10px; color: #909399;">元</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="押金金额" prop="depositAmount">
              <el-input-number
                v-model="form.depositAmount"
                :min="0"
                :precision="2"
                style="width: 100%;"
                @change="calculateTotal" />
              <span style="margin-left: 10px; color: #909399;">元</span>
              <div style="font-size: 12px; color: #909399; margin-top: 4px;" v-if="selectedBed && selectedBed.depositFee">
                建议金额：¥{{ selectedBed.depositFee }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员费" prop="memberFee">
              <el-input-number
                v-model="form.memberFee"
                :min="0"
                :precision="2"
                style="width: 100%;"
                @change="calculateTotal" />
              <span style="margin-left: 10px; color: #909399;">元</span>
              <div style="font-size: 12px; color: #909399; margin-top: 4px;" v-if="selectedBed && selectedBed.memberFee">
                建议金额：¥{{ selectedBed.memberFee }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="费用说明">
          <el-input v-model="form.feeDescription" type="textarea" placeholder="请输入费用说明（可选）" />
        </el-form-item>

        <!-- 费用汇总 -->
        <el-card shadow="never" class="fee-summary">
          <div slot="header">
            <i class="el-icon-s-finance"></i> 费用汇总
          </div>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="床位费">
              <span style="color: #409EFF; font-weight: bold;">¥{{ formatMoney(form.bedFee) }}</span>/月
            </el-descriptions-item>
            <el-descriptions-item label="护理费">
              <span style="color: #409EFF; font-weight: bold;">¥{{ formatMoney(form.careFee) }}</span>/月
              <span style="margin-left: 8px; color: #909399;">({{ getCareLevelText() }})</span>
            </el-descriptions-item>
            <el-descriptions-item label="餐费">
              <span style="color: #409EFF; font-weight: bold;">¥{{ formatMoney(form.mealFee) }}</span>/月
              <span style="margin-left: 8px; color: #909399;">{{ form.mealLevel || '未选择' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="月服务费">
              <span style="font-weight: bold; color: #409EFF;">¥{{ formatMoney(monthlyFeeTotal) }} × {{ form.monthCount }}个月</span>
            </el-descriptions-item>
            <el-descriptions-item label="服务费小计">¥{{ formatMoney(serviceFeeTotal) }}</el-descriptions-item>
            <el-descriptions-item label="押金">¥{{ formatMoney(form.depositAmount) }}</el-descriptions-item>
            <el-descriptions-item label="会员费">¥{{ formatMoney(form.memberFee) }}</el-descriptions-item>
            <el-descriptions-item label="应收总计" :span="2">
              <span style="font-size: 18px; font-weight: bold; color: #409EFF;">¥{{ formatMoney(calculatedTotal) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="实收总计" :span="2">
              <el-input-number
                v-model="form.finalAmount"
                :min="0"
                :precision="2"
                controls-position="right"
                style="width: 200px;" />
              <span style="margin-left: 10px; color: #909399;">元（可手动调整优惠）</span>
              <span v-if="discountAmount > 0" style="margin-left: 10px; color: #67C23A;">
                已优惠: ¥{{ formatMoney(discountAmount) }}
              </span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 支付方式 -->
        <el-divider content-position="left">
          <i class="el-icon-bank-card"></i> 支付方式
        </el-divider>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-radio-group v-model="form.paymentMethod">
            <el-radio label="cash">
              <i class="el-icon-money"></i> 现金支付
            </el-radio>
            <el-radio label="card">
              <i class="el-icon-bank-card"></i> 刷卡支付
            </el-radio>
            <el-radio label="scan">
              <i class="el-icon-mobile-phone"></i> 扫码支付
            </el-radio>
            <el-radio label="later">
              <i class="el-icon-time"></i> 稍后支付
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注信息（可选）" />
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="form-actions">
        <el-button type="primary" size="medium" @click="submitForm" :loading="submitLoading">
          <i class="el-icon-check"></i> 提交并生成订单
        </el-button>
        <el-button size="medium" @click="resetForm">
          <i class="el-icon-refresh-left"></i> 重置
        </el-button>
        <el-button size="medium" @click="goBack">
          <i class="el-icon-back"></i> 返回
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { listPensionInstitution } from "@/api/pension/institution";
import { listBedInfo } from "@/api/pension/bed";
import { createCheckin } from "@/api/pension/checkin";
import { getElderOptions, getElderManage } from "@/api/pension/elderManage";
import { listAvailableMeals } from "@/api/pension/mealFeeConfig";

export default {
  name: "PensionElderCheckin",
  dicts: ['elder_gender', 'elder_care_level'],
  data() {
    return {
      submitLoading: false,
      availableBeds: [],
      availableMeals: [],
      institutionList: [],
      elderOptions: [],
      selectedElderId: null,
      // 表单数据
      form: {
        // 老人信息
        elderName: '',
        gender: '',
        idCard: '',
        birthDate: '',
        age: null,
        phone: '',
        careLevel: '',
        healthStatus: '',
        address: '',
        emergencyContact: '',
        emergencyPhone: '',
        specialNeeds: '',
        // 床位信息
        institutionId: null,
        bedId: null,
        checkInDate: '',
        // 费用信息
        bedFee: 0,           // 床位费
        careFee: 0,          // 护理费
        mealConfigId: null,   // 餐费配置ID
        mealFee: 0,          // 餐费
        mealLevel: '',       // 餐费档次
        monthlyFee: 0,       // 月服务费(自动计算=床位费+护理费+餐费)
        monthCount: 1,
        depositAmount: 0,
        memberFee: 0,
        finalAmount: 0,
        feeDescription: '',
        // 支付方式
        paymentMethod: 'later',
        remark: ''
      },
      // 表单验证规则
      rules: {
        elderName: [
          { required: true, message: "入住人姓名不能为空", trigger: "blur" }
        ],
        gender: [
          { required: true, message: "性别不能为空", trigger: "change" }
        ],
        idCard: [
          { required: true, message: "身份证号不能为空", trigger: "blur" },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: "请输入正确的身份证号", trigger: "blur" }
        ],
        birthDate: [
          { required: true, message: "出生日期不能为空", trigger: "change" }
        ],
        age: [
          { required: true, message: "年龄不能为空", trigger: "blur" }
        ],
        phone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ],
        careLevel: [
          { required: true, message: "护理等级不能为空", trigger: "change" }
        ],
        emergencyContact: [
          { required: true, message: "紧急联系人不能为空", trigger: "blur" }
        ],
        emergencyPhone: [
          { required: true, message: "紧急联系电话不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ],
        institutionId: [
          { required: true, message: "请选择养老机构", trigger: "change" }
        ],
        bedId: [
          { required: true, message: "请选择床位", trigger: "change" }
        ],
        checkInDate: [
          { required: true, message: "入住日期不能为空", trigger: "change" }
        ],
        monthlyFee: [
          { required: true, message: "月服务费不能为空", trigger: "blur" }
        ],
        monthCount: [
          { required: true, message: "入驻月数不能为空", trigger: "blur" }
        ],
        depositAmount: [
          { required: true, message: "押金金额不能为空", trigger: "blur" }
        ],
        finalAmount: [
          { required: true, message: "实收总计不能为空", trigger: "blur" }
        ],
        paymentMethod: [
          { required: true, message: "请选择支付方式", trigger: "change" }
        ]
      }
    };
  },
  computed: {
    selectedBed() {
      return this.availableBeds.find(bed => bed.bedId === this.form.bedId);
    },
    // 性别显示标签
    genderLabel() {
      if (!this.form.gender) return '';
      const dict = this.dict.type.elder_gender;
      const item = dict.find(d => d.value === this.form.gender);
      return item ? item.label : '';
    },
    // 护理等级显示标签
    careLevelLabel() {
      if (!this.form.careLevel) return '';
      const dict = this.dict.type.elder_care_level;
      const item = dict.find(d => d.value === this.form.careLevel);
      return item ? item.label : '';
    },
    // 月服务费 = 床位费 + 护理费 + 餐费
    monthlyFeeTotal() {
      return (this.form.bedFee || 0) + (this.form.careFee || 0) + (this.form.mealFee || 0);
    },
    // 服务费小计 = 月服务费 × 入驻月数
    serviceFeeTotal() {
      return this.monthlyFeeTotal * (this.form.monthCount || 1);
    },
    // 应收总计 = 服务费小计 + 押金 + 会员费
    calculatedTotal() {
      return this.serviceFeeTotal + (this.form.depositAmount || 0) + (this.form.memberFee || 0);
    },
    // 优惠金额 = 应收总计 - 实收总计
    discountAmount() {
      return Math.max(0, this.calculatedTotal - (this.form.finalAmount || 0));
    }
  },
  created() {
    this.loadInstitutions();
    this.loadElderOptions();
    // 默认入住日期为今天
    this.form.checkInDate = this.parseTime(new Date(), '{y}-{m}-{d}');
  },
  methods: {
    /** 加载养老机构列表 */
    loadInstitutions() {
      listPensionInstitution().then(response => {
        this.institutionList = response.rows || [];
      });
    },
    /** 加载老人选项列表 */
    loadElderOptions() {
      getElderOptions().then(response => {
        this.elderOptions = response.data || [];
      });
    },
    /** 选择老人时自动填充表单 */
    handleElderChange(elderId) {
      if (!elderId) {
        // 清空选择时，重置老人信息字段
        this.form.elderName = '';
        this.form.gender = '';
        this.form.idCard = '';
        this.form.birthDate = '';
        this.form.age = null;
        this.form.phone = '';
        this.form.address = '';
        this.form.emergencyContact = '';
        this.form.emergencyPhone = '';
        this.form.healthStatus = '';
        this.form.specialNeeds = '';
        this.form.careLevel = '';
        return;
      }

      // 获取老人详细信息并填充表单
      getElderManage(elderId).then(response => {
        const elder = response.data;
        if (elder) {
          this.form.elderName = elder.elderName || '';
          this.form.gender = elder.gender || '';
          this.form.idCard = elder.idCard || '';
          this.form.birthDate = elder.birthDate || '';
          this.form.age = elder.age || null;
          this.form.phone = elder.phone || '';
          this.form.address = elder.address || '';
          this.form.emergencyContact = elder.emergencyContact || '';
          this.form.emergencyPhone = elder.emergencyPhone || '';
          this.form.healthStatus = elder.healthStatus || '';
          this.form.specialNeeds = elder.specialNeeds || '';
          // 护理等级也要填充
          this.form.careLevel = elder.careLevel || '';
          this.$modal.msgSuccess("已自动填充老人信息");
        }
      });
    },
    /** 养老机构改变 */
    handleInstitutionChange(institutionId) {
      // 清空床位选择和餐费选择
      this.form.bedId = null;
      this.form.mealConfigId = null;
      this.form.mealFee = 0;
      this.form.monthlyFee = 0;
      this.availableBeds = [];
      this.availableMeals = [];

      // 根据所选机构加载床位和餐费配置
      if (institutionId) {
        this.loadAvailableBeds(institutionId);
        this.loadAvailableMeals(institutionId);
      }
    },
    /** 加载可用床位(根据机构ID过滤) */
    loadAvailableBeds(institutionId) {
      listBedInfo({
        bedStatus: '0',
        institutionId: institutionId
      }).then(response => {
        this.availableBeds = response.rows || [];
      });
    },
    /** 加载可用餐费配置(根据机构ID过滤) */
    loadAvailableMeals(institutionId) {
      listAvailableMeals(institutionId).then(response => {
        this.availableMeals = response.data || [];
        // 默认选择第一个餐费档次
        if (this.availableMeals.length > 0) {
          this.form.mealConfigId = this.availableMeals[0].configId;
          this.form.mealFee = this.availableMeals[0].price || 0;
          this.form.mealLevel = this.availableMeals[0].mealLevel || '';
          this.calculateMonthlyFee();
        }
      });
    },
    /** 床位改变 */
    handleBedChange(bedId) {
      const bed = this.selectedBed;
      if (bed && bed.price) {
        // 床位费改变时重新计算服务费
        this.calculateMonthlyFee();
      }
    },
    /** 计算月服务费（床位费 + 护理费） */
    calculateMonthlyFee() {
      const bed = this.selectedBed;
      if (!bed || !bed.price) {
        this.form.bedFee = 0;
        this.form.careFee = 0;
        this.calculateTotal();
        return;
      }

      // 获取床位费
      this.form.bedFee = bed.price ?? 0;

      // 根据护理等级获取护理费
      let careFee = 0;
      if (this.form.careLevel) {
        switch (this.form.careLevel) {
          case '1': // 自理
            careFee = bed.selfCarePrice ?? 0;
            break;
          case '2': // 半护理
            careFee = bed.halfCarePrice ?? 0;
            break;
          case '3': // 全护理
            careFee = bed.fullCarePrice ?? 0;
            break;
          default:
            careFee = 0;
        }
      }
      this.form.careFee = careFee;

      // 同步更新 monthlyFee（用于后端存储）
      this.form.monthlyFee = this.form.bedFee + this.form.careFee + this.form.mealFee;

      // 使用床位信息中设置的押金和会员费
      this.form.depositAmount = bed.depositFee ?? 0;
      this.form.memberFee = bed.memberFee ?? 0;

      // 重新计算总费用
      this.calculateTotal();
    },
    /** 餐费档次改变 */
    handleMealChange(configId) {
      const meal = this.availableMeals.find(m => m.configId === configId);
      if (meal) {
        this.form.mealFee = meal.price || 0;
        this.form.mealLevel = meal.mealLevel || '';
        // 同步更新 monthlyFee（用于后端存储）
        this.form.monthlyFee = this.form.bedFee + this.form.careFee + this.form.mealFee;
        this.calculateTotal();
      } else {
        this.form.mealFee = 0;
        this.form.mealLevel = '';
        this.form.monthlyFee = this.form.bedFee + this.form.careFee;
        this.calculateTotal();
      }
    },
    /** 床位费改变 */
    onBedFeeChange() {
      // 同步更新 monthlyFee
      this.form.monthlyFee = this.form.bedFee + this.form.careFee + this.form.mealFee;
      this.calculateTotal();
    },
    /** 护理费改变 */
    onCareFeeChange() {
      // 同步更新 monthlyFee
      this.form.monthlyFee = this.form.bedFee + this.form.careFee + this.form.mealFee;
      this.calculateTotal();
    },
    /** 计算总费用 */
    calculateTotal() {
      // 自动更新实收总计为应收总计(用户可手动调整)
      this.form.finalAmount = this.calculatedTotal;
    },
    /** 解析身份证号 */
    parseIdCard() {
      const idCard = this.form.idCard;
      if (idCard && idCard.length >= 15) {
        // 提取出生日期
        let birthday = '';
        if (idCard.length === 18) {
          birthday = idCard.substring(6, 14);
        } else if (idCard.length === 15) {
          birthday = '19' + idCard.substring(6, 12);
        }
        if (birthday) {
          this.form.birthDate = birthday.substring(0, 4) + '-' + birthday.substring(4, 6) + '-' + birthday.substring(6, 8);
          // 调用计算年龄方法
          this.calculateAgeFromBirthDate();
        }
      }
    },
    /** 根据出生日期计算年龄 */
    calculateAgeFromBirthDate() {
      if (this.form.birthDate) {
        const birthDate = new Date(this.form.birthDate);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        // 如果当前月份小于出生月份,或者当前月份等于出生月份但当前日期小于出生日期,则年龄减1
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
          age--;
        }
        this.form.age = age;
      }
    },
    /** 获取床位类型名称 */
    getBedTypeName(bedType) {
      const typeMap = {
        '1': '普通床位',
        '2': '豪华床位',
        '3': '医疗床位'
      };
      return typeMap[bedType] || bedType;
    },
    /** 提交表单 */
    submitForm() {
      this.$refs.checkinForm.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          createCheckin(this.form).then(response => {
            this.$modal.msgSuccess("入驻办理成功,订单已生成");
            this.$router.push('/pension/elder/list');
          }).catch(() => {
            this.submitLoading = false;
          });
        }
      });
    },
    /** 重置表单 */
    resetForm() {
      this.$refs.checkinForm.resetFields();
      this.form.checkInDate = this.parseTime(new Date(), '{y}-{m}-{d}');
    },
    /** 返回 */
    goBack() {
      this.$router.push('/pension/elder/list');
    },
    /** 获取当前护理费 */
    getCurrentCareFee() {
      const bed = this.selectedBed;
      if (!bed) return 0;

      switch (this.form.careLevel) {
        case '1': // 自理
          return bed.selfCarePrice ?? 0;
        case '2': // 半护理
          return bed.halfCarePrice ?? 0;
        case '3': // 全护理
          return bed.fullCarePrice ?? 0;
        default:
          return 0;
      }
    },
    /** 获取护理等级文本 */
    getCareLevelText() {
      switch (this.form.careLevel) {
        case '1':
          return '自理';
        case '2':
          return '半护理';
        case '3':
          return '全护理';
        default:
          return '未选择';
      }
    },
    /** 格式化金额 */
    formatMoney(value) {
      if (!value) return '0.00';
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    }
  }
};
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
  padding: 20px 0;
  border-bottom: 2px solid #f0f0f0;
}

.page-header .title {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
  font-weight: 600;
}

.page-header .title i {
  margin-right: 10px;
  color: #409EFF;
}

.page-header .subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.el-divider {
  margin: 30px 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.el-divider i {
  margin-right: 5px;
  color: #409EFF;
}

.fee-summary {
  margin: 20px 0;
  background-color: #f5f7fa;
}

.fee-summary >>> .el-card__header {
  font-weight: 600;
  color: #303133;
}

.form-actions {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}

.form-actions .el-button {
  margin: 0 10px;
  min-width: 120px;
}
</style>
