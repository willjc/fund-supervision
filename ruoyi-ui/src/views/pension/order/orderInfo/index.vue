<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道来源" prop="channel">
        <el-select v-model="queryParams.channel" placeholder="请选择渠道来源" clearable>
          <el-option label="线上" value="线上"></el-option>
          <el-option label="线下" value="线下"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="订单类型" prop="orderType">
        <el-select v-model="queryParams.orderType" placeholder="请选择订单类型" clearable>
          <el-option label="入住" value="1"></el-option>
          <el-option label="续费" value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select v-model="queryParams.orderStatus" placeholder="请选择订单状态" clearable>
          <el-option
            v-for="dict in dict.type.order_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="老人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入老人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAddOrder"
          v-hasPermi="['order:info:add']"
        >新增订单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['order:info:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单ID" align="center" prop="orderId" width="80"/>
      <el-table-column label="订单号" align="center" prop="orderNo" width="180"/>
      <el-table-column label="订单类型" align="center" prop="orderType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.order_type" :value="scope.row.orderType"/>
        </template>
      </el-table-column>
      <el-table-column label="老人姓名" align="center" prop="elderName" width="100"/>
      <el-table-column label="机构名称" align="center" prop="institutionName" width="120"/>
      <el-table-column label="订单金额" align="center" prop="orderAmount" width="120">
        <template slot-scope="scope">
          <span style="color: #E6A23C">¥{{ scope.row.orderAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="已付金额" align="center" prop="paidAmount" width="120">
        <template slot-scope="scope">
          <span style="color: #67C23A">¥{{ scope.row.paidAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="未付金额" align="center" prop="unpaidAmount" width="120">
        <template slot-scope="scope">
          <span style="color: #F56C6C">¥{{ scope.row.unpaidAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" align="center" prop="orderStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.order_status" :value="scope.row.orderStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="支付方式" align="center" prop="paymentMethod" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.payment_method" :value="scope.row.paymentMethod"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="260">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['order:info:edit']"
            v-if="scope.row.orderStatus !== '1'"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleAudit(scope.row)"
            v-if="scope.row.orderStatus === '4'"
            v-hasPermi="['order:info:audit']"
          >审核</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-money"
            @click="handlePay(scope.row)"
            v-if="scope.row.orderStatus === '5'"
            v-hasPermi="['order:info:pay']"
          >支付</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-wallet"
            @click="handleOfflinePay(scope.row)"
            v-if="scope.row.orderStatus === '0' || scope.row.orderStatus === '5'"
            v-hasPermi="['order:info:offlinePay']"
          >线下支付</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleCancel(scope.row)"
            v-if="scope.row.orderStatus === '4' || scope.row.orderStatus === '5'"
            v-hasPermi="['order:info:cancel']"
          >取消</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['order:info:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="订单类型" prop="orderType">
              <el-select v-model="form.orderType" placeholder="请选择订单类型">
                <el-option
                  v-for="dict in dict.type.order_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付方式" prop="paymentMethod">
              <el-select v-model="form.paymentMethod" placeholder="请选择支付方式">
                <el-option
                  v-for="dict in dict.type.payment_method"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="订单金额" prop="orderAmount">
              <el-input-number v-model="form.orderAmount" :precision="2" :min="0" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单状态" prop="orderStatus">
              <el-select v-model="form.orderStatus" placeholder="请选择订单状态">
                <el-option
                  v-for="dict in dict.type.order_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="订单备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 新增订单对话框 - 选择入住人 -->
    <el-dialog title="新增订单 - 选择入住人" :visible.sync="selectResidentOpen" width="900px" append-to-body>
      <el-form :model="residentQueryParams" ref="residentQueryForm" size="small" :inline="true">
        <el-form-item label="姓名">
          <el-input
            v-model="residentQueryParams.elderName"
            placeholder="请输入老人姓名"
            clearable
            style="width: 200px"
            @keyup.enter.native="handleResidentQuery"
          />
        </el-form-item>
        <el-form-item label="所属机构">
          <el-select v-model="residentQueryParams.institutionId" placeholder="请选择机构" clearable style="width: 200px">
            <el-option
              v-for="inst in institutionList"
              :key="inst.institutionId"
              :label="inst.institutionName"
              :value="inst.institutionId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleResidentQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetResidentQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="residentList" border style="width: 100%" max-height="400" @row-click="handleSelectResident" highlight-current-row>
        <el-table-column label="姓名" prop="elderName" width="100" />
        <el-table-column label="性别" prop="gender" width="60">
          <template slot-scope="scope">
            <span>{{ scope.row.gender === '0' ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="年龄" prop="age" width="60" />
        <el-table-column label="床位" prop="bedInfo" width="120" />
        <el-table-column label="所属机构" prop="institutionName" min-width="150" show-overflow-tooltip />
        <el-table-column label="服务费余额" prop="serviceBalance" width="120">
          <template slot-scope="scope">
            <span>¥{{ formatMoney(scope.row.serviceBalance) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="到期日期" prop="dueDate" width="110">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.dueDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleSelectResident(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="residentTotal>0"
        :total="residentTotal"
        :page.sync="residentQueryParams.pageNum"
        :limit.sync="residentQueryParams.pageSize"
        @pagination="loadResidentList"
      />
    </el-dialog>

    <!-- 新增订单对话框 - 续费表单 -->
    <el-dialog title="新增订单 - 续费" :visible.sync="renewOrderOpen" width="800px" append-to-body>
      <el-form ref="renewOrderForm" :model="renewOrderForm" :rules="renewOrderRules" label-width="120px">
        <!-- 基本信息展示 -->
        <el-divider content-position="left">
          <i class="el-icon-user"></i> 入住人信息
        </el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="入住人">
              <el-input v-model="renewOrderForm.elderName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="当前床位">
              <el-input v-model="renewOrderForm.bedInfo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="当前月服务费">
              <el-input :value="'¥' + formatMoney(renewOrderForm.monthlyFee)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="服务费余额">
              <el-input :value="'¥' + formatMoney(renewOrderForm.serviceBalance)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="押金余额">
              <el-input :value="'¥' + formatMoney(renewOrderForm.depositBalance)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="会员余额">
              <el-input :value="'¥' + formatMoney(renewOrderForm.memberBalance)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住日期">
              <el-input :value="parseTime(renewOrderForm.checkInDate, '{y}-{m}-{d}')" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前到期日期">
              <el-input :value="parseTime(renewOrderForm.currentDueDate, '{y}-{m}-{d}')" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 费用设置 -->
        <el-divider content-position="left">
          <i class="el-icon-wallet"></i> 费用设置
        </el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="月服务费">
              <el-input-number
                v-model="renewOrderForm.monthlyFee"
                :min="0"
                :precision="2"
                style="width: 100%;"
                disabled />
              <span style="margin-left: 10px; color: #909399;">元/月</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="续费月数" prop="monthCount">
              <el-input-number
                v-model="renewOrderForm.monthCount"
                :min="0"
                :max="120"
                :precision="0"
                style="width: 100%;"
                @change="calculateRenewOrderTotal" />
              <span style="margin-left: 10px; color: #909399;">个月</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="服务费小计">
              <el-input
                :value="formatMoney(renewOrderServiceFeeTotal)"
                disabled
                style="width: 100%;" />
              <span style="margin-left: 10px; color: #909399;">元</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="补交押金金额" prop="depositAmount">
              <el-input-number
                v-model="renewOrderForm.depositAmount"
                :min="0"
                :precision="2"
                style="width: 100%;"
                @change="calculateRenewOrderTotal" />
              <span style="margin-left: 10px; color: #909399;">元</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="补交会员费" prop="memberFee">
              <el-input-number
                v-model="renewOrderForm.memberFee"
                :min="0"
                :precision="2"
                style="width: 100%;"
                @change="calculateRenewOrderTotal" />
              <span style="margin-left: 10px; color: #909399;">元</span>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 费用汇总 -->
        <el-card shadow="never" style="margin-bottom: 20px;">
          <div slot="header">
            <i class="el-icon-s-finance"></i> 费用汇总
          </div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="月服务费">¥{{ formatMoney(renewOrderForm.monthlyFee) }} × {{ renewOrderForm.monthCount }}个月</el-descriptions-item>
            <el-descriptions-item label="服务费小计">¥{{ formatMoney(renewOrderServiceFeeTotal) }}</el-descriptions-item>
            <el-descriptions-item label="补交押金">¥{{ formatMoney(renewOrderForm.depositAmount) }}</el-descriptions-item>
            <el-descriptions-item label="补交会员费">¥{{ formatMoney(renewOrderForm.memberFee) }}</el-descriptions-item>
            <el-descriptions-item label="应收总计" :span="2">
              <span style="font-size: 18px; font-weight: bold; color: #409EFF;">¥{{ formatMoney(renewOrderCalculatedTotal) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="实收总计" :span="2">
              <el-input-number
                v-model="renewOrderForm.finalAmount"
                :min="0"
                :precision="2"
                controls-position="right"
                style="width: 200px;" />
              <span style="margin-left: 10px; color: #909399;">元（可手动调整优惠）</span>
              <span v-if="renewOrderDiscountAmount > 0" style="margin-left: 10px; color: #67C23A;">
                已优惠: ¥{{ formatMoney(renewOrderDiscountAmount) }}
              </span>
            </el-descriptions-item>
            <el-descriptions-item label="新到期日期" :span="2" v-if="renewOrderForm.monthCount > 0">
              <span style="font-size: 16px; font-weight: bold; color: #67C23A;">
                {{ parseTime(renewOrderForm.newDueDate, '{y}-{m}-{d}') }}
              </span>
              <span style="margin-left: 10px; color: #909399;">
                (延长{{ renewOrderForm.monthCount }}个月)
              </span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 支付方式 -->
        <el-divider content-position="left">
          <i class="el-icon-bank-card"></i> 支付方式
        </el-divider>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-radio-group v-model="renewOrderForm.paymentMethod">
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

        <el-form-item label="备注">
          <el-input v-model="renewOrderForm.remark" type="textarea" placeholder="请输入备注信息（可选）" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitRenewOrder">确 定</el-button>
        <el-button @click="renewOrderOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 订单详情对话框 -->
    <order-detail ref="orderDetail" />

    <!-- 支付对话框 -->
    <payment-dialog ref="paymentDialog" @success="getList" />

    <!-- 生成订单对话框 -->
    <generate-order-dialog ref="generateOrderDialog" @success="getList" />

    <!-- 审核对话框 -->
    <audit-dialog ref="auditDialog" @success="getList" />

    <!-- 线下支付对话框 -->
    <offline-pay-dialog ref="offlinePayDialog" @success="getList" />

  </div>
</template>

<script>
import { listOrder, getOrder, delOrder, addOrder, updateOrder, payOrder, cancelOrder, exportOrder, approveOrder, rejectOrder } from "@/api/order/orderInfo";
import { listResident, getResident, renewResident } from "@/api/elder/resident";
import { listPensionInstitution } from "@/api/pension/institution";
import OrderDetail from './components/OrderDetail'
import PaymentDialog from './components/PaymentDialog'
import GenerateOrderDialog from './components/GenerateOrderDialog'
import AuditDialog from './components/AuditDialog'
import OfflinePayDialog from './components/OfflinePayDialog'

export default {
  name: "OrderInfo",
  dicts: ['order_type', 'order_status', 'payment_method'],
  components: {
    OrderDetail,
    PaymentDialog,
    GenerateOrderDialog,
    AuditDialog,
    OfflinePayDialog
  },
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
      // 订单表格数据
      orderList: [],
      // 日期范围
      dateRange: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: null,
        channel: null,
        orderType: null,
        orderStatus: null,
        paymentMethod: null,
        elderName: null,
        institutionName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderType: [
          { required: true, message: "订单类型不能为空", trigger: "change" }
        ],
        orderAmount: [
          { required: true, message: "订单金额不能为空", trigger: "blur" }
        ],
        orderStatus: [
          { required: true, message: "订单状态不能为空", trigger: "change" }
        ],
        paymentMethod: [
          { required: true, message: "支付方式不能为空", trigger: "change" }
        ]
      },
      // 新增订单相关数据
      selectResidentOpen: false,
      renewOrderOpen: false,
      residentList: [],
      residentTotal: 0,
      institutionList: [],
      residentQueryParams: {
        pageNum: 1,
        pageSize: 10,
        elderName: null,
        institutionId: null,
        checkInStatus: '1' // 只显示已入住的老人
      },
      renewOrderForm: {
        elderId: null,
        elderName: null,
        bedInfo: null,
        monthlyFee: 0,
        serviceBalance: 0,
        depositBalance: 0,
        memberBalance: 0,
        checkInDate: null,
        currentDueDate: null,
        newDueDate: null,
        monthCount: 0,
        depositAmount: 0,
        memberFee: 0,
        finalAmount: 0,
        paymentMethod: 'cash',
        remark: null
      },
      renewOrderRules: {
        monthCount: [
          { required: true, message: "续费月数不能为空", trigger: "blur" },
          { type: 'number', min: 0, max: 120, message: "续费月数必须在0-120之间", trigger: "blur" }
        ],
        depositAmount: [
          { required: true, message: "补交押金金额不能为空", trigger: "blur" }
        ],
        memberFee: [
          { required: true, message: "补交会员费不能为空", trigger: "blur" }
        ],
        paymentMethod: [
          { required: true, message: "支付方式不能为空", trigger: "change" }
        ]
      }
    };
  },
  computed: {
    // 续费服务费小计 = 月服务费 × 续费月数
    renewOrderServiceFeeTotal() {
      return (this.renewOrderForm.monthlyFee || 0) * (this.renewOrderForm.monthCount || 0);
    },
    // 续费应收总计 = 服务费小计 + 补交押金 + 补交会员费
    renewOrderCalculatedTotal() {
      return this.renewOrderServiceFeeTotal + (this.renewOrderForm.depositAmount || 0) + (this.renewOrderForm.memberFee || 0);
    },
    // 续费优惠金额 = 应收总计 - 实收总计
    renewOrderDiscountAmount() {
      return Math.max(0, this.renewOrderCalculatedTotal - (this.renewOrderForm.finalAmount || 0));
    }
  },
  created() {
    // 从URL参数中获取筛选条件
    if (this.$route.query.elderName) {
      this.queryParams.elderName = this.$route.query.elderName;
    }
    if (this.$route.query.orderStatus) {
      this.queryParams.orderStatus = this.$route.query.orderStatus;
    }
    this.getList();
  },
  methods: {
    /** 查询订单列表 */
    getList() {
      this.loading = true;
      this.addDateRange(this.queryParams, this.dateRange);
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        orderId: null,
        orderNo: null,
        orderType: null,
        elderId: null,
        institutionId: null,
        orderAmount: null,
        paidAmount: null,
        unpaidAmount: null,
        orderStatus: "1",
        paymentMethod: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.orderId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增订单 - 打开选择入住人对话框 */
    handleAddOrder() {
      // 加载养老机构列表
      listPensionInstitution().then(response => {
        this.institutionList = response.rows || [];
      });
      // 加载入住人列表
      this.loadResidentList();
      this.selectResidentOpen = true;
    },
    /** 加载入住人列表 */
    loadResidentList() {
      listResident(this.residentQueryParams).then(response => {
        this.residentList = response.rows;
        this.residentTotal = response.total;
      });
    },
    /** 搜索入住人 */
    handleResidentQuery() {
      this.residentQueryParams.pageNum = 1;
      this.loadResidentList();
    },
    /** 重置入住人搜索 */
    resetResidentQuery() {
      this.residentQueryParams = {
        pageNum: 1,
        pageSize: 10,
        elderName: null,
        institutionId: null,
        checkInStatus: '1'
      };
      this.loadResidentList();
    },
    /** 选择入住人 */
    handleSelectResident(row) {
      // 关闭选择入住人对话框
      this.selectResidentOpen = false;

      // 获取入住人详细信息
      getResident(row.elderId).then(response => {
        const data = response.data;
        this.renewOrderForm = {
          elderId: data.elderId,
          elderName: data.elderName,
          bedInfo: data.bedInfo || '-',
          monthlyFee: data.monthlyFee || 0,
          serviceBalance: data.serviceBalance || 0,
          depositBalance: data.depositBalance || 0,
          memberBalance: data.memberBalance || 0,
          checkInDate: data.checkInDate,
          currentDueDate: data.dueDate,
          newDueDate: data.dueDate,
          monthCount: 0,
          depositAmount: 0,
          memberFee: 0,
          finalAmount: 0,
          paymentMethod: 'cash',
          remark: null
        };
        // 打开续费表单对话框
        this.renewOrderOpen = true;
      });
    },
    /** 计算续费总额和新到期日期 */
    calculateRenewOrderTotal() {
      // 计算新到期日期:在当前到期日期基础上增加续费月数
      if (this.renewOrderForm.currentDueDate && this.renewOrderForm.monthCount > 0) {
        const currentDue = new Date(this.renewOrderForm.currentDueDate);
        const newDue = new Date(currentDue);
        newDue.setMonth(newDue.getMonth() + parseInt(this.renewOrderForm.monthCount));
        this.renewOrderForm.newDueDate = this.parseTime(newDue, '{y}-{m}-{d}');
      } else {
        // 如果月数为0,到期日期不变
        this.renewOrderForm.newDueDate = this.renewOrderForm.currentDueDate;
      }
      // 自动更新实收总计为应收总计(用户可手动调整)
      this.renewOrderForm.finalAmount = this.renewOrderCalculatedTotal;
    },
    /** 提交续费订单 */
    submitRenewOrder() {
      this.$refs["renewOrderForm"].validate(valid => {
        if (valid) {
          // 验证至少有一项续费内容
          if (this.renewOrderForm.monthCount === 0 && this.renewOrderForm.depositAmount === 0 && this.renewOrderForm.memberFee === 0) {
            this.$modal.msgWarning("请至少填写一项续费内容(续费月数、补交押金或补交会员费)");
            return;
          }

          // 构建续费数据
          const renewData = {
            elderId: this.renewOrderForm.elderId,
            monthCount: this.renewOrderForm.monthCount,
            depositAmount: this.renewOrderForm.depositAmount,
            memberFee: this.renewOrderForm.memberFee,
            finalAmount: this.renewOrderForm.finalAmount,
            paymentMethod: this.renewOrderForm.paymentMethod,
            remark: this.renewOrderForm.remark
          };

          renewResident(renewData).then(response => {
            this.$modal.msgSuccess("订单创建成功");
            this.renewOrderOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 格式化金额 */
    formatMoney(value) {
      if (value == null || value === '') {
        return '0.00';
      }
      return Number(value).toFixed(2);
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const orderId = row.orderId || this.ids
      getOrder(orderId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改订单";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.orderId != null) {
            updateOrder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOrder(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const orderIds = row.orderId || this.ids;
      this.$modal.confirm('是否确认删除订单编号为"' + orderIds + '"的数据项？').then(function() {
        return delOrder(orderIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('order/info/export', {
        ...this.queryParams
      }, `order_${new Date().getTime()}.xlsx`)
    },
    /** 查看详情 */
    handleDetail(row) {
      this.$refs.orderDetail.show(row.orderId);
    },
    /** 支付操作 */
    handlePay(row) {
      this.$refs.paymentDialog.show(row);
    },
    /** 线下支付操作 */
    handleOfflinePay(row) {
      this.$refs.offlinePayDialog.show(row);
    },
    /** 取消订单 */
    handleCancel(row) {
      this.$modal.confirm('确认取消订单"' + row.orderNo + '"吗？').then(() => {
        return cancelOrder(row.orderId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("取消成功");
      }).catch(() => {});
    },
    /** 生成订单 */
    handleGenerateOrder() {
      this.$refs.generateOrderDialog.show();
    },
    /** 审核订单 */
    handleAudit(row) {
      this.$refs.auditDialog.show(row.orderId);
    },
    /** 批量支付 */
    handleBatchPay() {
      if (this.ids.length === 0) {
        this.$modal.msgError("请选择要支付的订单");
        return;
      }
      this.$modal.msgSuccess("批量支付功能开发中...");
    },
    /** 批量退费 */
    handleBatchRefund() {
      if (this.ids.length === 0) {
        this.$modal.msgError("请选择要退费的订单");
        return;
      }
      this.$modal.msgSuccess("批量退费功能开发中...");
    },
    /** 批量续费 */
    handleBatchRenew() {
      if (this.ids.length === 0) {
        this.$modal.msgError("请选择要续费的订单");
        return;
      }
      this.$modal.msgSuccess("批量续费功能开发中...");
    }
  }
};
</script>