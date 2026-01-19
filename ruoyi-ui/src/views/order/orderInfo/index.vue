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
      <el-form-item label="订单类型" prop="orderType">
        <el-select v-model="queryParams.orderType" placeholder="请选择订单类型" clearable>
          <el-option
            v-for="dict in dict.type.order_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select v-model="queryParams.orderStatus" placeholder="请选择订单状态" clearable>
          <el-option
            v-for="dict in dict.type.order_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="支付方式" prop="paymentMethod">
        <el-select v-model="queryParams.paymentMethod" placeholder="请选择支付方式" clearable>
          <el-option
            v-for="dict in dict.type.payment_method"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
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
          @click="handleAdd"
          v-hasPermi="['order:info:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['order:info:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['order:info:remove']"
        >删除</el-button>
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
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-s-check"
          size="mini"
          @click="handleGenerateOrder"
          v-hasPermi="['order:info:add']"
        >生成订单</el-button>
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
            icon="el-icon-close"
            @click="handleCancel(scope.row)"
            v-if="scope.row.orderStatus === '5' || scope.row.orderStatus === '1'"
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

    <!-- 订单详情对话框 -->
    <order-detail ref="orderDetail" />

    <!-- 支付对话框 -->
    <payment-dialog ref="paymentDialog" @success="getList" />

    <!-- 生成订单对话框 -->
    <generate-order-dialog ref="generateOrderDialog" @success="getList" />

    <!-- 审核对话框 -->
    <audit-dialog ref="auditDialog" @success="getList" />

  </div>
</template>

<script>
import { listOrder, getOrder, delOrder, addOrder, updateOrder, payOrder, cancelOrder, exportOrder } from "@/api/order/orderInfo";
import OrderDetail from './components/OrderDetail'
import PaymentDialog from './components/PaymentDialog'
import GenerateOrderDialog from './components/GenerateOrderDialog'
import AuditDialog from './components/AuditDialog'

export default {
  name: "OrderInfo",
  dicts: ['order_type', 'order_status', 'payment_method'],
  components: {
    OrderDetail,
    PaymentDialog,
    GenerateOrderDialog,
    AuditDialog
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
      }
    };
  },
  created() {
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加订单";
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
    }
  }
};
</script>