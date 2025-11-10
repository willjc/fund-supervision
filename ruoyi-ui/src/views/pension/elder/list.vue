<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="入住人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入入住人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-select v-model="queryParams.gender" placeholder="请选择性别" clearable>
          <el-option
            v-for="dict in dict.type.elder_gender"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="房间号" prop="roomNumber">
        <el-input
          v-model="queryParams.roomNumber"
          placeholder="请输入房间号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="入住状态" prop="checkInStatus">
        <el-select v-model="queryParams.checkInStatus" placeholder="请选择入住状态" clearable>
          <el-option label="已入住" value="1" />
          <el-option label="已退住" value="2" />
          <el-option label="请假中" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.totalResidents || 0 }}</div>
            <div class="stat-label">入住总人数</div>
            <div class="stat-icon total">
              <i class="el-icon-user"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">￥{{ formatMoney(statistics.totalServiceBalance || 0) }}</div>
            <div class="stat-label">服务费总余额</div>
            <div class="stat-icon service">
              <i class="el-icon-wallet"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">￥{{ formatMoney(statistics.totalDepositBalance || 0) }}</div>
            <div class="stat-label">押金总余额</div>
            <div class="stat-icon deposit">
              <i class="el-icon-coin"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">￥{{ formatMoney(statistics.totalMemberBalance || 0) }}</div>
            <div class="stat-label">会员卡总余额</div>
            <div class="stat-icon member">
              <i class="el-icon-credit-card"></i>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['elder:resident:add']"
        >新增入驻</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['elder:resident:import']"
        >批量导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['elder:resident:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 入住人列表表格 -->
    <el-table v-loading="loading" :data="residentList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="入住人姓名" align="center" prop="elderName" width="100" />
      <el-table-column label="性别" align="center" prop="gender" width="60">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.elder_gender" :value="scope.row.gender"/>
        </template>
      </el-table-column>
      <el-table-column label="年龄" align="center" prop="age" width="60" />
      <el-table-column label="身份证号" align="center" prop="idCard" width="150" />
      <el-table-column label="联系电话" align="center" prop="phone" width="120" />
      <el-table-column label="房间号-床位号" align="center" prop="bedInfo" width="120" />
      <el-table-column label="入住状态" align="center" prop="checkInStatus" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.checkInStatus === '1'" type="success">已入住</el-tag>
          <el-tag v-else-if="scope.row.checkInStatus === '2'" type="info">已退住</el-tag>
          <el-tag v-else-if="scope.row.checkInStatus === '3'" type="warning">请假中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="护理等级" align="center" prop="careLevel" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.elder_care_level" :value="scope.row.careLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="服务费余额" align="center" prop="serviceBalance" width="120">
        <template slot-scope="scope">
          <span :class="getBalanceClass(scope.row.serviceBalance)">
            ￥{{ formatMoney(scope.row.serviceBalance) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="押金余额" align="center" prop="depositBalance" width="120">
        <template slot-scope="scope">
          <span :class="getBalanceClass(scope.row.depositBalance)">
            ￥{{ formatMoney(scope.row.depositBalance) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="会员余额" align="center" prop="memberBalance" width="120">
        <template slot-scope="scope">
          <span :class="getBalanceClass(scope.row.memberBalance)">
            ￥{{ formatMoney(scope.row.memberBalance) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="入住日期" align="center" prop="checkInDate" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.checkInDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="紧急联系人" align="center" prop="emergencyContact" width="100" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['elder:resident:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['elder:resident:edit']"
          >维护</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleRenew(scope.row)"
            v-hasPermi="['elder:resident:renew']"
          >续费</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-minus"
            @click="handleRefund(scope.row)"
            v-hasPermi="['elder:resident:refund']"
          >退费</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-coin"
            @click="handleDepositUse(scope.row)"
            v-hasPermi="['elder:resident:deposit']"
          >押金使用</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['elder:resident:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 入住人详情对话框 -->
    <el-dialog title="入住人详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <div class="resident-detail">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="姓名">{{ residentDetail.elderName }}</el-descriptions-item>
          <el-descriptions-item label="性别">
            <dict-tag :options="dict.type.elder_gender" :value="residentDetail.gender"/>
          </el-descriptions-item>
          <el-descriptions-item label="年龄">{{ residentDetail.age }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ residentDetail.idCard }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ residentDetail.phone }}</el-descriptions-item>
          <el-descriptions-item label="房间床位">{{ residentDetail.bedInfo }}</el-descriptions-item>
          <el-descriptions-item label="入住日期">{{ parseTime(residentDetail.checkInDate, '{y}-{m}-{d}') }}</el-descriptions-item>
          <el-descriptions-item label="护理等级">
            <dict-tag :options="dict.type.elder_care_level" :value="residentDetail.careLevel"/>
          </el-descriptions-item>
          <el-descriptions-item label="紧急联系人" :span="2">{{ residentDetail.emergencyContact }}</el-descriptions-item>
          <el-descriptions-item label="家庭住址" :span="2">{{ residentDetail.address }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="费用信息" :column="3" border style="margin-top: 20px;">
          <el-descriptions-item label="服务费余额">
            <span class="balance-text" :class="getBalanceClass(residentDetail.serviceBalance)">
              ￥{{ formatMoney(residentDetail.serviceBalance) }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="押金余额">
            <span class="balance-text" :class="getBalanceClass(residentDetail.depositBalance)">
              ￥{{ formatMoney(residentDetail.depositBalance) }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="会员余额">
            <span class="balance-text" :class="getBalanceClass(residentDetail.memberBalance)">
              ￥{{ formatMoney(residentDetail.memberBalance) }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="月服务费" :span="3">￥{{ formatMoney(residentDetail.monthlyFee) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 续费对话框 -->
    <el-dialog title="入住人续费" :visible.sync="renewOpen" width="600px" append-to-body>
      <el-form ref="renewForm" :model="renewForm" :rules="renewRules" label-width="100px">
        <el-form-item label="入住人">
          <el-input v-model="renewForm.elderName" disabled />
        </el-form-item>
        <el-form-item label="续费类型" prop="renewType">
          <el-select v-model="renewForm.renewType" placeholder="请选择续费类型" style="width: 100%">
            <el-option label="服务费" value="service"></el-option>
            <el-option label="会员卡" value="member"></el-option>
            <el-option label="押金补缴" value="deposit"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="续费金额" prop="amount">
          <el-input-number v-model="renewForm.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-radio-group v-model="renewForm.paymentMethod">
            <el-radio label="cash">现金</el-radio>
            <el-radio label="card">刷卡</el-radio>
            <el-radio label="scan">扫码</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="renewForm.remark" type="textarea" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitRenew">确 定</el-button>
        <el-button @click="renewOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 退费对话框 -->
    <el-dialog title="入住人退费" :visible.sync="refundOpen" width="600px" append-to-body>
      <el-form ref="refundForm" :model="refundForm" :rules="refundRules" label-width="100px">
        <el-form-item label="入住人">
          <el-input v-model="refundForm.elderName" disabled />
        </el-form-item>
        <el-form-item label="退费类型" prop="refundType">
          <el-select v-model="refundForm.refundType" placeholder="请选择退费类型" style="width: 100%">
            <el-option label="服务费" value="service"></el-option>
            <el-option label="会员卡" value="member"></el-option>
            <el-option label="押金" value="deposit"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="可退余额">
          <el-input :value="formatMoney(refundForm.availableBalance)" disabled />
        </el-form-item>
        <el-form-item label="退费金额" prop="amount">
          <el-input-number v-model="refundForm.amount" :min="0" :max="refundForm.availableBalance" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="退费原因" prop="reason">
          <el-input v-model="refundForm.reason" type="textarea" placeholder="请输入退费原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitRefund">确 定</el-button>
        <el-button @click="refundOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 押金使用申请对话框 -->
    <el-dialog title="押金使用申请" :visible.sync="depositUseOpen" width="600px" append-to-body>
      <el-form ref="depositUseForm" :model="depositUseForm" :rules="depositUseRules" label-width="100px">
        <el-form-item label="入住人">
          <el-input v-model="depositUseForm.elderName" disabled />
        </el-form-item>
        <el-form-item label="可申请金额">
          <el-input :value="formatMoney(depositUseForm.availableBalance)" disabled />
        </el-form-item>
        <el-form-item label="申请金额" prop="amount">
          <el-input-number v-model="depositUseForm.amount" :min="0" :max="depositUseForm.availableBalance" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="使用事由" prop="purpose">
          <el-select v-model="depositUseForm.purpose" placeholder="请选择使用事由" style="width: 100%">
            <el-option label="医疗费用" value="medical"></el-option>
            <el-option label="个人物品购买" value="personal"></el-option>
            <el-option label="其他用途" value="other"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明" prop="description">
          <el-input v-model="depositUseForm.description" type="textarea" placeholder="请详细说明使用事由" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitDepositUse">提 交</el-button>
        <el-button @click="depositUseOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog title="批量导入入住人" :visible.sync="importOpen" width="500px" append-to-body>
      <el-upload
        class="upload-demo"
        action="/api/elder/resident/import"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        :before-upload="beforeUpload"
        accept=".xlsx,.xls"
        :limit="1"
      >
        <el-button size="small" type="primary">点击上传</el-button>
        <div slot="tip" class="el-upload__tip">
          只能上传xlsx/xls文件，且不超过10MB
          <el-link type="primary" @click="downloadTemplate" style="margin-left: 10px;">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="importOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listResident, getResident, delResident, renewResident, refundResident, applyDepositUse } from "@/api/elder/resident";

export default {
  name: "ElderResident",
  dicts: ['elder_gender', 'elder_care_level'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 入住人列表数据
      residentList: [],
      // 统计数据
      statistics: {},
      // 弹出层标题
      title: "",
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示续费弹出层
      renewOpen: false,
      // 是否显示退费弹出层
      refundOpen: false,
      // 是否显示押金使用申请弹出层
      depositUseOpen: false,
      // 是否显示导入弹出层
      importOpen: false,
      // 入住人详情
      residentDetail: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        elderName: null,
        gender: null,
        roomNumber: null,
        checkInStatus: null,
      },
      // 续费表单参数
      renewForm: {
        residentId: null,
        elderName: null,
        renewType: 'service',
        amount: null,
        paymentMethod: 'cash',
        remark: null
      },
      // 退费表单参数
      refundForm: {
        residentId: null,
        elderName: null,
        refundType: 'service',
        amount: null,
        availableBalance: 0,
        reason: null
      },
      // 押金使用申请表单参数
      depositUseForm: {
        residentId: null,
        elderName: null,
        amount: null,
        availableBalance: 0,
        purpose: null,
        description: null
      },
      // 表单校验
      renewRules: {
        renewType: [
          { required: true, message: "续费类型不能为空", trigger: "change" }
        ],
        amount: [
          { required: true, message: "续费金额不能为空", trigger: "blur" }
        ],
        paymentMethod: [
          { required: true, message: "支付方式不能为空", trigger: "change" }
        ]
      },
      refundRules: {
        refundType: [
          { required: true, message: "退费类型不能为空", trigger: "change" }
        ],
        amount: [
          { required: true, message: "退费金额不能为空", trigger: "blur" }
        ],
        reason: [
          { required: true, message: "退费原因不能为空", trigger: "blur" }
        ]
      },
      depositUseRules: {
        amount: [
          { required: true, message: "申请金额不能为空", trigger: "blur" }
        ],
        purpose: [
          { required: true, message: "使用事由不能为空", trigger: "change" }
        ],
        description: [
          { required: true, message: "详细说明不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getStatistics();
  },
  methods: {
    /** 查询入住人列表 */
    getList() {
      this.loading = true;
      listResident(this.queryParams).then(response => {
        this.residentList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询统计数据 */
    getStatistics() {
      // TODO: 调用统计API
      this.statistics = {
        totalResidents: 156,
        totalServiceBalance: 456789.50,
        totalDepositBalance: 234567.00,
        totalMemberBalance: 123456.78
      };
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.residentId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增入驻按钮操作 */
    handleAdd() {
      this.$router.push('/elder/checkin');
    },
    /** 批量导入按钮操作 */
    handleImport() {
      this.importOpen = true;
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      const residentId = row.residentId;
      getResident(residentId).then(response => {
        this.residentDetail = response.data;
        this.detailOpen = true;
      });
    },
    /** 维护按钮操作 */
    handleUpdate(row) {
      const residentId = row.residentId;
      this.$router.push({
        path: '/elder/resident/edit',
        query: { residentId: residentId }
      });
    },
    /** 续费按钮操作 */
    handleRenew(row) {
      this.renewForm = {
        residentId: row.residentId,
        elderName: row.elderName,
        renewType: 'service',
        amount: null,
        paymentMethod: 'cash',
        remark: null
      };
      this.renewOpen = true;
    },
    /** 退费按钮操作 */
    handleRefund(row) {
      this.refundForm = {
        residentId: row.residentId,
        elderName: row.elderName,
        refundType: 'service',
        amount: null,
        availableBalance: row.serviceBalance,
        reason: null
      };
      this.refundOpen = true;
    },
    /** 押金使用申请按钮操作 */
    handleDepositUse(row) {
      this.$router.push({
        path: '/elder/depositApply',
        query: { residentId: row.residentId }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const residentIds = row.residentId || this.ids;
      this.$modal.confirm('是否确认删除入住人编号为"' + residentIds + '"的数据项？').then(function() {
        return delResident(residentIds);
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('elder/resident/export', {
        ...this.queryParams
      }, `入住人信息_${new Date().getTime()}.xlsx`)
    },
    /** 提交续费 */
    submitRenew() {
      this.$refs["renewForm"].validate(valid => {
        if (valid) {
          renewResident(this.renewForm).then(response => {
            this.$modal.msgSuccess("续费成功");
            this.renewOpen = false;
            this.getList();
            this.getStatistics();
          });
        }
      });
    },
    /** 提交退费 */
    submitRefund() {
      this.$refs["refundForm"].validate(valid => {
        if (valid) {
          refundResident(this.refundForm).then(response => {
            this.$modal.msgSuccess("退费成功");
            this.refundOpen = false;
            this.getList();
            this.getStatistics();
          });
        }
      });
    },
    /** 提交押金使用申请 */
    submitDepositUse() {
      this.$refs["depositUseForm"].validate(valid => {
        if (valid) {
          applyDepositUse(this.depositUseForm).then(response => {
            this.$modal.msgSuccess("申请提交成功");
            this.depositUseOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 下载导入模板 */
    downloadTemplate() {
      this.download('elder/resident/template', {}, `入住人导入模板.xlsx`);
    },
    /** 导入前检查 */
    beforeUpload(file) {
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                     file.type === 'application/vnd.ms-excel';
      const isLt10M = file.size / 1024 / 1024 < 10;

      if (!isExcel) {
        this.$message.error('只能上传 Excel 文件!');
        return false;
      }
      if (!isLt10M) {
        this.$message.error('上传文件大小不能超过 10MB!');
        return false;
      }
      return true;
    },
    /** 导入成功 */
    handleImportSuccess(response, file, fileList) {
      this.$message.success('导入成功');
      this.importOpen = false;
      this.getList();
      this.getStatistics();
    },
    /** 导入失败 */
    handleImportError(err, file, fileList) {
      this.$message.error('导入失败，请检查文件格式');
    },
    /** 格式化金额 */
    formatMoney(value) {
      if (!value) return '0.00'
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    },
    /** 获取余额样式类 */
    getBalanceClass(balance) {
      if (balance < 1000) return 'balance-warning'
      if (balance < 100) return 'balance-danger'
      return 'balance-normal'
    }
  }
};
</script>

<style scoped>
.stat-card {
  height: 100px;
}

.stat-content {
  position: relative;
  height: 100%;
  padding: 20px;
  display: flex;
  align-items: center;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-left: 15px;
}

.stat-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #42A5F5, #66B3FF);
}

.stat-icon.service {
  background: linear-gradient(135deg, #4CAF50, #66BB6A);
}

.stat-icon.deposit {
  background: linear-gradient(135deg, #FF9800, #FFB74D);
}

.stat-icon.member {
  background: linear-gradient(135deg, #9C27B0, #BA68C8);
}

.resident-detail {
  max-height: 500px;
  overflow-y: auto;
}

.balance-text {
  font-weight: 600;
}

.balance-normal {
  color: #67C23A;
}

.balance-warning {
  color: #E6A23C;
}

.balance-danger {
  color: #F56C6C;
}
</style>