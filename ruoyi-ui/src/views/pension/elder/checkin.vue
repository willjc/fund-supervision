<template>
  <div class="app-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="title">
        <i class="el-icon-user-solid"></i>
        新增入驻
      </h2>
    </div>

    <!-- 步骤指示器 -->
    <el-steps :active="activeStep" finish-status="success" align-center style="margin-bottom: 30px;">
      <el-step title="基本信息" description="录入入住人身份信息"></el-step>
      <el-step title="床位选择" description="选择房间和床位"></el-step>
      <el-step title="费用设置" description="设置服务费和押金"></el-step>
      <el-step title="确认订单" description="确认信息并生成订单"></el-step>
      <el-step title="完成支付" description="选择支付方式完成支付"></el-step>
    </el-steps>

    <!-- 步骤1：基本信息 -->
    <div v-show="activeStep === 0" class="step-content">
      <el-card shadow="hover">
        <div slot="header" class="card-header">
          <span>入住人基本信息</span>
        </div>
        <el-form ref="basicForm" :model="basicForm" :rules="basicRules" label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="入住人姓名" prop="elderName">
                <el-input v-model="basicForm.elderName" placeholder="请输入入住人姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="basicForm.gender" placeholder="请选择性别">
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
                <el-input v-model="basicForm.idCard" placeholder="请输入身份证号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="出生日期" prop="birthDate">
                <el-date-picker
                  v-model="basicForm.birthDate"
                  type="date"
                  placeholder="请选择出生日期"
                  value-format="yyyy-MM-dd"
                  style="width: 100%;">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="年龄" prop="age">
                <el-input-number v-model="basicForm.age" :min="1" :max="120" style="width: 100%;" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="basicForm.phone" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="老人类型" prop="elderType">
                <el-select v-model="basicForm.elderType" placeholder="请选择老人类型">
                  <el-option label="自理老人" value="self_care"></el-option>
                  <el-option label="半失能老人" value="semi_disabled"></el-option>
                  <el-option label="失能老人" value="disabled"></el-option>
                  <el-option label="失智老人" value="dementia"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="护理等级" prop="careLevel">
                <el-select v-model="basicForm.careLevel" placeholder="请选择护理等级">
                  <el-option
                    v-for="dict in dict.type.elder_care_level"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="家庭住��" prop="address">
            <el-input v-model="basicForm.address" type="textarea" placeholder="请输入家庭住址" />
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="紧急联系人" prop="emergencyContact">
                <el-input v-model="basicForm.emergencyContact" placeholder="请输入紧急联系人姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="紧急联系电话" prop="emergencyPhone">
                <el-input v-model="basicForm.emergencyPhone" placeholder="请输入紧急联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="与老人关系" prop="emergencyRelation">
                <el-select v-model="basicForm.emergencyRelation" placeholder="请选择关系">
                  <el-option label="子女" value="子女"></el-option>
                  <el-option label="配偶" value="配偶"></el-option>
                  <el-option label="父母" value="父母"></el-option>
                  <el-option label="兄弟姐妹" value="兄弟姐妹"></el-option>
                  <el-option label="其他" value="其他"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="健康状况" prop="healthStatus">
                <el-input v-model="basicForm.healthStatus" placeholder="请输入健康状况" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="特殊需求" prop="specialNeeds">
            <el-input v-model="basicForm.specialNeeds" type="textarea" placeholder="请输入特殊需求或注意事项" />
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 步骤2：床位选择 -->
    <div v-show="activeStep === 1" class="step-content">
      <el-card shadow="hover">
        <div slot="header" class="card-header">
          <span>床位选择</span>
        </div>
        <el-form ref="bedForm" :model="bedForm" :rules="bedRules" label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="入驻起止时间" prop="checkInPeriod">
                <el-date-picker
                  v-model="bedForm.checkInPeriod"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="yyyy-MM-dd"
                  style="width: 100%;">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="���望入住日期" prop="expectedCheckInDate">
                <el-date-picker
                  v-model="bedForm.expectedCheckInDate"
                  type="date"
                  placeholder="请选择期望入住日期"
                  value-format="yyyy-MM-dd"
                  style="width: 100%;">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="选择床位" prop="bedId">
            <el-select v-model="bedForm.bedId" placeholder="请选择床位" filterable style="width: 100%">
              <el-option
                v-for="bed in availableBeds"
                :key="bed.bedId"
                :label="`${bed.roomNumber}-${bed.bedNumber} (${bed.bedType}) - ￥${bed.monthlyFee}/月`"
                :value="bed.bedId">
                <div style="display: flex; justify-content: space-between;">
                  <span>{{ bed.roomNumber }}-{{ bed.bedNumber }} ({{ bed.bedType }})</span>
                  <span style="color: #409EFF; font-weight: bold;">￥{{ bed.monthlyFee }}/月</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
          <el-alert
            v-if="selectedBed"
            :title="`已选择床位：${selectedBed.roomNumber}-${selectedBed.bedNumber}，类型：${selectedBed.bedType}，月费：￥${selectedBed.monthlyFee}`"
            type="success"
            :closable="false"
            style="margin-top: 10px;">
          </el-alert>
        </el-form>
      </el-card>
    </div>

    <!-- 步骤3：费用设置 -->
    <div v-show="activeStep === 2" class="step-content">
      <el-card shadow="hover">
        <div slot="header" class="card-header">
          <span>费用设置</span>
        </div>
        <el-form ref="feeForm" :model="feeForm" :rules="feeRules" label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="月服务费" prop="monthlyFee">
                <el-input-number v-model="feeForm.monthlyFee" :min="0" :precision="2" style="width: 100%;" />
                <span style="margin-left: 10px; color: #909399;">元/月</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="押金金额" prop="depositAmount">
                <el-input-number v-model="feeForm.depositAmount" :min="0" :precision="2" style="width: 100%;" />
                <span style="margin-left: 10px; color: #909399;">元</span>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="餐费标准" prop="mealFee">
                <el-input-number v-model="feeForm.mealFee" :min="0" :precision="2" style="width: 100%;" />
                <span style="margin-left: 10px; color: #909399;">元/月</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="其他服务费" prop="otherFee">
                <el-input-number v-model="feeForm.otherFee" :min="0" :precision="2" style="width: 100%;" />
                <span style="margin-left: 10px; color: #909399;">元/月</span>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="首月费用" prop="firstMonthFee">
                <el-input-number v-model="feeForm.firstMonthFee" :min="0" :precision="2" style="width: 100%;" disabled />
                <span style="margin-left: 10px; color: #909399;">元（自动计算）</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="总应收费用" prop="totalAmount">
                <el-input-number v-model="feeForm.totalAmount" :min="0" :precision="2" style="width: 100%;" disabled />
                <span style="margin-left: 10px; color: #909399;">元（自动计算）</span>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="费用说明">
            <el-input v-model="feeForm.feeDescription" type="textarea" placeholder="请输入费用说明" />
          </el-form-item>
        </el-form>

        <!-- 费用明细 -->
        <el-divider content-position="left">费用明细</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="月服务费">￥{{ formatMoney(feeForm.monthlyFee) }}</el-descriptions-item>
          <el-descriptions-item label="餐费">￥{{ formatMoney(feeForm.mealFee) }}</el-descriptions-item>
          <el-descriptions-item label="其他服务费">￥{{ formatMoney(feeForm.otherFee) }}</el-descriptions-item>
          <el-descriptions-item label="押金">￥{{ formatMoney(feeForm.depositAmount) }}</el-descriptions-item>
          <el-descriptions-item label="首月费用" :span="2">￥{{ formatMoney(feeForm.firstMonthFee) }}</el-descriptions-item>
          <el-descriptions-item label="总费用" :span="2">
            <span style="font-size: 18px; font-weight: bold; color: #F56C6C;">￥{{ formatMoney(feeForm.totalAmount) }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>

    <!-- 步骤4：确认订单 -->
    <div v-show="activeStep === 3" class="step-content">
      <el-card shadow="hover">
        <div slot="header" class="card-header">
          <span>确认订单信息</span>
        </div>
        <el-descriptions title="入住人信息" :column="2" border>
          <el-descriptions-item label="姓名">{{ basicForm.elderName }}</el-descriptions-item>
          <el-descriptions-item label="性别">
            <dict-tag :options="dict.type.elder_gender" :value="basicForm.gender"/>
          </el-descriptions-item>
          <el-descriptions-item label="年龄">{{ basicForm.age }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ basicForm.idCard }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ basicForm.phone }}</el-descriptions-item>
          <el-descriptions-item label="老人类型">{{ formatElderType(basicForm.elderType) }}</el-descriptions-item>
          <el-descriptions-item label="护理等级">
            <dict-tag :options="dict.type.elder_care_level" :value="basicForm.careLevel"/>
          </el-descriptions-item>
          <el-descriptions-item label="床位信息">{{ selectedBed ? `${selectedBed.roomNumber}-${selectedBed.bedNumber}` : '-' }}</el-descriptions-item>
          <el-descriptions-item label="入驻时间" :span="2">{{ bedForm.checkInPeriod ? `${bedForm.checkInPeriod[0]} 至 ${bedForm.checkInPeriod[1]}` : '-' }}</el-descriptions-item>
          <el-descriptions-item label="紧急联系人" :span="2">{{ basicForm.emergencyContact }} ({{ basicForm.emergencyRelation }}) - {{ basicForm.emergencyPhone }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="费用信息" :column="2" border style="margin-top: 20px;">
          <el-descriptions-item label="月服务费">￥{{ formatMoney(feeForm.monthlyFee) }}</el-descriptions-item>
          <el-descriptions-item label="餐费">￥{{ formatMoney(feeForm.mealFee) }}</el-descriptions-item>
          <el-descriptions-item label="其他服务费">￥{{ formatMoney(feeForm.otherFee) }}</el-descriptions-item>
          <el-descriptions-item label="押金">￥{{ formatMoney(feeForm.depositAmount) }}</el-descriptions-item>
          <el-descriptions-item label="首月费用">￥{{ formatMoney(feeForm.firstMonthFee) }}</el-descriptions-item>
          <el-descriptions-item label="总费用">
            <span style="font-size: 18px; font-weight: bold; color: #F56C6C;">￥{{ formatMoney(feeForm.totalAmount) }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-alert
          title="请仔细核对以上信息，确认无误后点击生成订单"
          type="warning"
          :closable="false"
          style="margin-top: 20px;">
        </el-alert>
      </el-card>
    </div>

    <!-- 步骤5：完成支付 -->
    <div v-show="activeStep === 4" class="step-content">
      <el-card shadow="hover">
        <div slot="header" class="card-header">
          <span>完成支付</span>
        </div>

        <div v-if="!paymentCompleted">
          <el-descriptions title="订单信息" :column="2" border>
            <el-descriptions-item label="订单���号">{{ orderInfo.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="入住人">{{ orderInfo.elderName }}</el-descriptions-item>
            <el-descriptions-item label="床位信息">{{ orderInfo.bedInfo }}</el-descriptions-item>
            <el-descriptions-item label="生成时间">{{ orderInfo.createTime }}</el-descriptions-item>
            <el-descriptions-item label="订单金额" :span="2">
              <span style="font-size: 20px; font-weight: bold; color: #F56C6C;">￥{{ formatMoney(orderInfo.totalAmount) }}</span>
            </el-descriptions-item>
          </el-descriptions>

          <el-divider content-position="left">选择支付方式</el-divider>
          <el-form ref="paymentForm" :model="paymentForm" :rules="paymentRules" label-width="120px">
            <el-form-item label="支付方式" prop="paymentMethod">
              <el-radio-group v-model="paymentForm.paymentMethod">
                <el-radio label="cash">
                  <i class="el-icon-money"></i> 现金支付
                </el-radio>
                <el-radio label="card">
                  <i class="el-icon-bank-card"></i> 刷卡支付
                </el-radio>
                <el-radio label="scan">
                  <i class="el-icon-mobile-phone"></i> 扫码支付
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="paymentForm.paymentMethod === 'scan'" label="扫码支付">
              <div style="text-align: center; padding: 20px;">
                <el-image
                  src="https://via.placeholder.com/200x200/409EFF/FFFFFF?text=扫码支付"
                  style="width: 200px; height: 200px;">
                </el-image>
                <div style="margin-top: 10px; color: #909399;">请使用微信或支付宝扫码支付</div>
              </div>
            </el-form-item>
            <el-form-item label="支付备注">
              <el-input v-model="paymentForm.remark" type="textarea" placeholder="请输入支付备注（可选）"></el-input>
            </el-form-item>
          </el-form>
        </div>

        <!-- 支付完成 -->
        <div v-else>
          <el-result
            icon="success"
            title="支付成功"
            :sub-title="`订单 ${orderInfo.orderNo} 支付完成，入住人 ${orderInfo.elderName} 已成功办理入驻手续`">
            <template slot="extra">
              <el-button type="primary" size="medium" @click="goToResidentList">查看入住人列表</el-button>
              <el-button size="medium" @click="handleNewCheckin">继续办理入驻</el-button>
            </template>
          </el-result>
        </div>
      </el-card>
    </div>

    <!-- 操作按钮 -->
    <div class="step-actions">
      <el-button v-if="activeStep > 0 && activeStep < 4" @click="prevStep">上一步</el-button>
      <el-button v-if="activeStep < 3" type="primary" @click="nextStep">下一步</el-button>
      <el-button v-if="activeStep === 3" type="primary" @click="generateOrder">生成订单</el-button>
      <el-button v-if="activeStep === 4 && !paymentCompleted" type="primary" @click="completePayment">确认支付</el-button>
    </div>
  </div>
</template>

<script>
import { addCheckin, generateOrderByCheckin, completePayment } from "@/api/elder/checkin";
import { listAvailableBeds } from "@/api/elder/bed";

export default {
  name: "ElderCheckin",
  dicts: ['elder_gender', 'elder_care_level'],
  data() {
    return {
      activeStep: 0,
      paymentCompleted: false,
      availableBeds: [],
      orderInfo: {
        orderNo: '',
        elderName: '',
        bedInfo: '',
        totalAmount: 0,
        createTime: ''
      },
      // 基本信息表单
      basicForm: {
        elderName: '',
        gender: '',
        idCard: '',
        birthDate: '',
        age: null,
        phone: '',
        elderType: '',
        careLevel: '',
        address: '',
        emergencyContact: '',
        emergencyPhone: '',
        emergencyRelation: '',
        healthStatus: '',
        specialNeeds: ''
      },
      // 床位选择表单
      bedForm: {
        checkInPeriod: [],
        expectedCheckInDate: '',
        bedId: null
      },
      // 费用设置表单
      feeForm: {
        monthlyFee: 0,
        depositAmount: 0,
        mealFee: 0,
        otherFee: 0,
        firstMonthFee: 0,
        totalAmount: 0,
        feeDescription: ''
      },
      // 支付表单
      paymentForm: {
        paymentMethod: 'cash',
        remark: ''
      },
      // 表单验证规则
      basicRules: {
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
        elderType: [
          { required: true, message: "老人类型不能为空", trigger: "change" }
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
        emergencyRelation: [
          { required: true, message: "与老人关系不能为空", trigger: "change" }
        ]
      },
      bedRules: {
        checkInPeriod: [
          { required: true, message: "入驻起止时间不能为空", trigger: "change" }
        ],
        expectedCheckInDate: [
          { required: true, message: "期望入住日期不能为空", trigger: "change" }
        ],
        bedId: [
          { required: true, message: "请选择床位", trigger: "change" }
        ]
      },
      feeRules: {
        monthlyFee: [
          { required: true, message: "月服务费不能为空", trigger: "blur" }
        ],
        depositAmount: [
          { required: true, message: "押金金额不能为空", trigger: "blur" }
        ]
      },
      paymentRules: {
        paymentMethod: [
          { required: true, message: "请选择支付方式", trigger: "change" }
        ]
      }
    };
  },
  computed: {
    selectedBed() {
      return this.availableBeds.find(bed => bed.bedId === this.bedForm.bedId);
    }
  },
  watch: {
    'feeForm.monthlyFee'() {
      this.calculateTotalAmount();
    },
    'feeForm.mealFee'() {
      this.calculateTotalAmount();
    },
    'feeForm.otherFee'() {
      this.calculateTotalAmount();
    },
    'feeForm.depositAmount'() {
      this.calculateTotalAmount();
    },
    'bedForm.bedId'(newBedId) {
      if (newBedId && this.selectedBed) {
        this.feeForm.monthlyFee = this.selectedBed.monthlyFee;
      }
    }
  },
  created() {
    this.loadAvailableBeds();
  },
  methods: {
    /** 加载可用床位 */
    loadAvailableBeds() {
      listAvailableBeds(1).then(response => {
        this.availableBeds = response.data || [
          { bedId: 1, roomNumber: 'A101', bedNumber: '01', bedType: '单人间', monthlyFee: 3500 },
          { bedId: 2, roomNumber: 'A101', bedNumber: '02', bedType: '单人间', monthlyFee: 3500 },
          { bedId: 3, roomNumber: 'B201', bedNumber: '01', bedType: '双人间', monthlyFee: 2800 },
          { bedId: 4, roomNumber: 'B201', bedNumber: '02', bedType: '双人间', monthlyFee: 2800 },
          { bedId: 5, roomNumber: 'C301', bedNumber: '01', bedType: '豪华间', monthlyFee: 5800 }
        ];
      });
    },
    /** 下一步 */
    nextStep() {
      let formName = '';
      if (this.activeStep === 0) formName = 'basicForm';
      else if (this.activeStep === 1) formName = 'bedForm';
      else if (this.activeStep === 2) formName = 'feeForm';

      this.$refs[formName].validate(valid => {
        if (valid) {
          this.activeStep++;
        }
      });
    },
    /** 上一步 */
    prevStep() {
      this.activeStep--;
    },
    /** 生成订单 */
    generateOrder() {
      const checkinData = {
        ...this.basicForm,
        ...this.bedForm,
        ...this.feeForm,
        bedInfo: this.selectedBed ? `${this.selectedBed.roomNumber}-${this.selectedBed.bedNumber}` : ''
      };

      addCheckin(checkinData).then(response => {
        const checkinId = response.data.checkinId;
        return generateOrderByCheckin(checkinId);
      }).then(response => {
        this.orderInfo = {
          orderNo: 'ORD' + Date.now(),
          elderName: this.basicForm.elderName,
          bedInfo: this.selectedBed ? `${this.selectedBed.roomNumber}-${this.selectedBed.bedNumber}` : '',
          totalAmount: this.feeForm.totalAmount,
          createTime: new Date().toLocaleString()
        };
        this.activeStep = 4;
        this.$message.success('订单生成成功');
      });
    },
    /** 完成支付 */
    completePayment() {
      this.$refs.paymentForm.validate(valid => {
        if (valid) {
          const paymentData = {
            orderNo: this.orderInfo.orderNo,
            paymentMethod: this.paymentForm.paymentMethod,
            amount: this.orderInfo.totalAmount,
            remark: this.paymentForm.remark
          };

          // 模拟支付过程
          this.$message.info('正在处理支付...');

          setTimeout(() => {
            this.paymentCompleted = true;
            this.$message.success('支付成功！');
          }, 2000);
        }
      });
    },
    /** 计算总费用 */
    calculateTotalAmount() {
      const monthlyTotal = this.feeForm.monthlyFee + this.feeForm.mealFee + this.feeForm.otherFee;
      this.feeForm.firstMonthFee = monthlyTotal;
      this.feeForm.totalAmount = monthlyTotal + this.feeForm.depositAmount;
    },
    /** 格式化老人类型 */
    formatElderType(type) {
      const typeMap = {
        'self_care': '自理老人',
        'semi_disabled': '半失能老人',
        'disabled': '失能老人',
        'dementia': '失智老人'
      };
      return typeMap[type] || type;
    },
    /** 格式化金额 */
    formatMoney(value) {
      if (!value) return '0.00';
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    },
    /** 跳转到入住人列表 */
    goToResidentList() {
      this.$router.push('/elder/resident');
    },
    /** 继续办理入驻 */
    handleNewCheckin() {
      // 重置所有表单
      this.resetAllForms();
      this.activeStep = 0;
      this.paymentCompleted = false;
    },
    /** 重置所有表单 */
    resetAllForms() {
      this.basicForm = {
        elderName: '',
        gender: '',
        idCard: '',
        birthDate: '',
        age: null,
        phone: '',
        elderType: '',
        careLevel: '',
        address: '',
        emergencyContact: '',
        emergencyPhone: '',
        emergencyRelation: '',
        healthStatus: '',
        specialNeeds: ''
      };
      this.bedForm = {
        checkInPeriod: [],
        expectedCheckInDate: '',
        bedId: null
      };
      this.feeForm = {
        monthlyFee: 0,
        depositAmount: 0,
        mealFee: 0,
        otherFee: 0,
        firstMonthFee: 0,
        totalAmount: 0,
        feeDescription: ''
      };
      this.paymentForm = {
        paymentMethod: 'cash',
        remark: ''
      };
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
  margin: 0;
  font-size: 24px;
  color: #303133;
  font-weight: 600;
}

.page-header .title i {
  margin-right: 10px;
  color: #409EFF;
}

.step-content {
  margin-bottom: 30px;
  min-height: 400px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.step-actions {
  text-align: center;
  margin-top: 30px;
}

.step-actions .el-button {
  margin: 0 10px;
  min-width: 100px;
}

.el-descriptions {
  margin-bottom: 20px;
}

.el-divider {
  margin: 30px 0 20px 0;
}
</style>