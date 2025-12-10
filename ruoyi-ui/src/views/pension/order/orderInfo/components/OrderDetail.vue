<template>
  <el-dialog title="订单详情" :visible.sync="visible" width="800px" append-to-body>
    <div v-loading="loading">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ orderInfo.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">
          <dict-tag :options="dict.type.order_type" :value="orderInfo.orderType"/>
        </el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ orderInfo.elderName }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ orderInfo.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="床位信息">{{ orderInfo.roomNumber }}-{{ orderInfo.bedNumber }}</el-descriptions-item>
        <el-descriptions-item label="入驻月数">{{ orderInfo.monthCount || '-' }}个月</el-descriptions-item>
        <el-descriptions-item label="服务起始日期">{{ parseTime(orderInfo.serviceStartDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="服务结束日期">{{ parseTime(orderInfo.serviceEndDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="应收总计">
          <span style="color: #909399">¥{{ orderInfo.originalAmount || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="优惠金额">
          <span style="color: #F56C6C">-¥{{ orderInfo.discountAmount || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="实收金额">
          <span style="color: #E6A23C; font-weight: bold">¥{{ orderInfo.orderAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="已付金额">
          <span style="color: #67C23A; font-weight: bold">¥{{ orderInfo.paidAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <dict-tag :options="dict.type.order_status" :value="orderInfo.orderStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="支付方式">
          <dict-tag :options="dict.type.payment_method" :value="orderInfo.paymentMethod"/>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(orderInfo.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ orderInfo.createBy }}</el-descriptions-item>
        <el-descriptions-item label="费用说明" :span="2">{{ orderInfo.remark || '无' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 费用汇总 -->
      <el-divider content-position="left">费用汇总</el-divider>
      <el-descriptions :column="3" border class="fee-summary">
        <el-descriptions-item label="床位费小计">
          <span style="color: #409EFF; font-weight: bold">¥{{ getBedFeeTotal() }}</span>
          <div v-if="isLegacyFormat()" style="font-size: 12px; color: #909399;">*估算值</div>
        </el-descriptions-item>
        <el-descriptions-item label="护理费小计">
          <span style="color: #67C23A; font-weight: bold">¥{{ getCareFeeTotal() }}</span>
          <div v-if="isLegacyFormat()" style="font-size: 12px; color: #909399;">*估算值</div>
        </el-descriptions-item>
        <el-descriptions-item label="服务费小计">
          <span style="color: #E6A23C; font-weight: bold">¥{{ getServiceFeeTotal() }}</span>
          <div v-if="isLegacyFormat()" style="font-size: 12px; color: #E6A23C;">*包含床位费+护理费</div>
        </el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">订单明细</el-divider>
      <el-table :data="orderItems" style="width: 100%">
        <el-table-column prop="itemName" label="项目名称" width="200"/>
        <el-table-column prop="itemType" label="项目类型" width="120">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.item_type" :value="scope.row.itemType"/>
          </template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.unitPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80"/>
        <el-table-column prop="totalAmount" label="小计" width="120">
          <template slot-scope="scope">
            <span style="color: #E6A23C; font-weight: bold">¥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="servicePeriod" label="服务周期" width="150"/>
        <el-table-column prop="itemDescription" label="描述" show-overflow-tooltip/>
      </el-table>

      <el-divider content-position="left">支付记录</el-divider>
      <el-table :data="paymentRecords" style="width: 100%">
        <el-table-column prop="paymentNo" label="支付流水号" width="180"/>
        <el-table-column prop="paymentAmount" label="支付金额" width="120">
          <template slot-scope="scope">
            <span style="color: #67C23A; font-weight: bold">¥{{ scope.row.paymentAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.payment_method" :value="scope.row.paymentMethod"/>
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="支付状态" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.payment_status" :value="scope.row.paymentStatus"/>
          </template>
        </el-table-column>
        <el-table-column prop="thirdPartyTransactionId" label="第三方交易号" width="180" show-overflow-tooltip/>
        <el-table-column prop="paymentTime" label="支付时间" width="160">
          <template slot-scope="scope">
            {{ parseTime(scope.row.paymentTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip/>
      </el-table>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关 闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getOrder } from "@/api/order/orderInfo";
import { getOrderItemsByOrderId } from "@/api/order/orderItem";
import { listPayment } from "@/api/order/paymentRecord";

export default {
  name: "OrderDetail",
  dicts: ['order_type', 'order_status', 'payment_method', 'payment_status', 'item_type'],
  data() {
    return {
      visible: false,
      loading: false,
      orderInfo: {},
      orderItems: [],
      paymentRecords: []
    };
  },
  methods: {
    show(orderId) {
      this.visible = true;
      this.loading = true;
      this.loadOrderDetail(orderId);
    },
    loadOrderDetail(orderId) {
      Promise.all([
        getOrder(orderId),
        this.loadOrderItems(orderId),
        this.loadPaymentRecords(orderId)
      ]).then(([orderRes]) => {
        this.orderInfo = orderRes.data;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    loadOrderItems(orderId) {
      return getOrderItemsByOrderId(orderId).then(response => {
        this.orderItems = response.data;
      });
    },
    loadPaymentRecords(orderId) {
      return listPayment({ orderId: orderId }).then(response => {
        this.paymentRecords = response.rows;
      });
    },
    // 计算床位费小计
    getBedFeeTotal() {
      const bedItem = this.orderItems.find(item => item.itemType === 'bed_fee');
      if (bedItem) {
        return bedItem.totalAmount;
      }

      // 兼容旧格式：如果没有找到床位费，从月服务费中推算
      const serviceItem = this.orderItems.find(item => item.itemType === 'service_fee');
      if (serviceItem) {
        // 假设旧格式中床位费占月服务费的70%（这是估算值，实际应该从床位费中获取）
        const serviceTotal = parseFloat(serviceItem.totalAmount) || 0;
        const bedTotal = serviceTotal * 0.7;
        return bedTotal.toFixed(2);
      }

      return '0.00';
    },
    // 计算护理费小计
    getCareFeeTotal() {
      const careItem = this.orderItems.find(item => item.itemType === 'care_fee');
      if (careItem) {
        return careItem.totalAmount;
      }

      // 兼容旧格式：如果没有找到护理费，从月服务费中推算
      const serviceItem = this.orderItems.find(item => item.itemType === 'service_fee');
      if (serviceItem) {
        // 假设旧格式中护理费占月服务费的30%（这是估算值）
        const serviceTotal = parseFloat(serviceItem.totalAmount) || 0;
        const careTotal = serviceTotal * 0.3;
        return careTotal.toFixed(2);
      }

      return '0.00';
    },
    // 计算服务费小计（床位费 + 护理费）
    getServiceFeeTotal() {
      const bedTotal = parseFloat(this.getBedFeeTotal()) || 0;
      const careTotal = parseFloat(this.getCareFeeTotal()) || 0;
      return (bedTotal + careTotal).toFixed(2);
    },
    // 判断是否是旧格式的订单
    isLegacyFormat() {
      return this.orderItems.some(item => item.itemType === 'service_fee') &&
             !this.orderItems.some(item => item.itemType === 'bed_fee');
    }
  }
};
</script>

<style scoped>
.fee-summary >>> .el-descriptions__body .el-descriptions__item.is-bordered .el-descriptions__cell {
  padding: 12px 16px;
}

.fee-summary >>> .el-descriptions__body .el-descriptions__item.is-bordered .el-descriptions__cell.is-label {
  font-weight: bold;
  background-color: #f5f7fa;
}
</style>