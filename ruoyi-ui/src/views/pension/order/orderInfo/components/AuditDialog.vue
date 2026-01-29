<template>
  <el-dialog title="订单审核" :visible.sync="visible" width="700px" append-to-body @close="handleClose">
    <div v-loading="loading">
      <el-alert
        title="审核提示"
        type="info"
        description="请核对订单信息，确认无误后点击审核通过。如需修改订单信息，请直接编辑后保存。"
        show-icon
        :closable="false"
        style="margin-bottom: 15px"
      />

      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="订单号">
              <span>{{ form.orderNo }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="老人姓名">
              <span>{{ form.elderName }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">床位与护理信息</el-divider>

        <!-- 当前床位显示 -->
        <el-row>
          <el-col :span="12">
            <el-form-item label="当前床位">
              <span v-if="form.currentBedInfo" style="color: #409EFF; font-weight: bold">
                {{ form.currentBedInfo }}
              </span>
              <span v-else style="color: #909399">未分配</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="更换床位">
              <el-select v-model="form.newBedId" placeholder="选择新床位（可选）" clearable @change="onBedChange">
                <el-option
                  v-for="bed in availableBeds"
                  :key="bed.bedId"
                  :label="bed.roomNumber + '-' + bed.bedNumber"
                  :value="bed.bedId">
                  <span>{{ bed.roomNumber }}-{{ bed.bedNumber }}</span>
                  <span style="color: #8492a6; font-size: 12px; margin-left: 10px">
                    ¥{{ bed.price }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 如果没有可用床位，显示警告 -->
        <el-alert
          v-if="availableBeds.length === 0 && form.institutionId"
          title="警告"
          type="warning"
          description="该类型下暂无空置床位，无法完成审核"
          :closable="false"
          style="margin-bottom: 10px"
        />

        <el-row>
          <el-col :span="12">
            <el-form-item label="床位类型" prop="bedType">
              <el-select v-model="form.bedType" placeholder="请选择床位类型" @change="onBedTypeChange">
                <el-option label="普通床位" value="1" />
                <el-option label="豪华床位" value="2" />
                <el-option label="医疗床位" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理等级" prop="careLevel">
              <el-select v-model="form.careLevel" placeholder="请选择护理等级" @change="calculateTotal">
                <el-option label="自理" value="自理" />
                <el-option label="半护理" value="半护理" />
                <el-option label="全护理" value="全护理" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">费用信息</el-divider>

        <el-row>
          <el-col :span="12">
            <el-form-item label="床位费(元)" prop="bedFee">
              <el-input-number v-model="form.bedFee" :precision="2" :min="0" :max="99999" @change="calculateTotal" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理费(元)" prop="careFee">
              <el-input-number v-model="form.careFee" :precision="2" :min="0" :max="99999" @change="calculateTotal" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="缴纳月数" prop="monthCount">
              <el-input-number v-model="form.monthCount" :min="1" :max="120" @change="calculateTotal" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务费小计">
              <span style="color: #E6A23C; font-weight: bold">¥{{ monthlyServiceFee }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="押金(元)" prop="depositFee">
              <el-input-number v-model="form.depositFee" :precision="2" :min="0" :max="99999" @change="calculateTotal" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员费(元)" prop="memberFee">
              <el-input-number v-model="form.memberFee" :precision="2" :min="0" :max="99999" @change="calculateTotal" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="订单总金额">
              <span style="color: #ee0a24; font-size: 18px; font-weight: bold">¥{{ totalAmount }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="审核备注" prop="approveRemark">
          <el-input
            v-model="form.approveRemark"
            type="textarea"
            placeholder="请输入审核备注"
            :rows="3"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleReject" :loading="submitting" type="danger">审核拒绝</el-button>
      <el-button type="primary" @click="handleApprove" :loading="submitting">审核通过</el-button>
      <el-button @click="visible = false">取消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getOrder, approveOrder, rejectOrder } from "@/api/order/orderInfo";
import { listBed } from "@/api/elder/bed";

export default {
  name: "AuditDialog",
  data() {
    return {
      visible: false,
      loading: false,
      submitting: false,
      availableBeds: [],  // 可用床位列表
      form: {
        orderId: null,
        orderNo: '',
        elderName: '',
        currentBedInfo: '',  // 当前床位信息
        newBedId: null,      // 新选择的床位ID
        institutionId: null, // 机构ID
        originalBedId: null, // 原始床位ID
        bedType: '1',
        careLevel: '自理',
        bedFee: 500,
        careFee: 500,
        monthCount: 1,
        depositFee: 10000,
        memberFee: 5000,
        approveRemark: ''
      },
      rules: {
        bedType: [
          { required: true, message: "请选择床位类型", trigger: "change" }
        ],
        careLevel: [
          { required: true, message: "请选择护理等级", trigger: "change" }
        ],
        bedFee: [
          { required: true, message: "请输入床位费", trigger: "blur" }
        ],
        careFee: [
          { required: true, message: "请输入护理费", trigger: "blur" }
        ],
        monthCount: [
          { required: true, message: "请输入缴纳月数", trigger: "blur" }
        ],
        depositFee: [
          { required: true, message: "请输入押金", trigger: "blur" }
        ],
        memberFee: [
          { required: true, message: "请输入会员费", trigger: "blur" }
        ]
      }
    };
  },
  computed: {
    // 月服务费 = 床位费 + 护理费
    monthlyServiceFee() {
      const bedFee = this.form.bedFee || 0;
      const careFee = this.form.careFee || 0;
      return (bedFee + careFee).toFixed(2);
    },
    // 总金额 = 月服务费 * 月数 + 押金 + 会员费
    totalAmount() {
      const monthly = parseFloat(this.monthlyServiceFee);
      const monthCount = this.form.monthCount || 1;
      const deposit = this.form.depositFee || 0;
      const member = this.form.memberFee || 0;
      return (monthly * monthCount + deposit + member).toFixed(2);
    }
  },
  methods: {
    show(orderId) {
      this.visible = true;
      this.loading = true;
      this.reset();
      this.loadOrderInfo(orderId);
    },
    loadOrderInfo(orderId) {
      getOrder(orderId).then(response => {
        const order = response.data;
        this.form.orderId = order.orderId;
        this.form.orderNo = order.orderNo;
        this.form.elderName = order.elderName;
        this.form.institutionId = order.institutionId;

        // 显示当前床位信息
        this.form.currentBedInfo = order.roomNumber && order.bedNumber
          ? order.roomNumber + '-' + order.bedNumber
          : '';
        this.form.originalBedId = order.bedId;

        // 从remark中解析费用信息
        this.parseFeesFromRemark(order.remark);

        // 加载可用床位列表
        this.loadAvailableBeds(order.institutionId, order.bedId);

        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    // 加载可用床位列表
    loadAvailableBeds(institutionId, currentBedId) {
      if (!institutionId) return;

      // 查询该机构下空置的床位
      const query = {
        institutionId: institutionId,
        bedStatus: '0'  // 空置状态
      };
      listBed(query).then(response => {
        // 过滤掉当前床位（如果有的话）
        this.availableBeds = (response.rows || []).filter(bed =>
          !currentBedId || bed.bedId !== currentBedId
        );
      });
    },
    // 床位选择变化时更新床位费
    onBedChange(newBedId) {
      if (newBedId) {
        const selectedBed = this.availableBeds.find(b => b.bedId === newBedId);
        if (selectedBed) {
          this.form.bedFee = selectedBed.price || this.form.bedFee;
          this.form.depositFee = selectedBed.depositFee || this.form.depositFee;
          this.form.memberFee = selectedBed.memberFee || this.form.memberFee;
          this.calculateTotal();
        }
      }
    },
    // 从remark字段解析费用信息
    parseFeesFromRemark(remark) {
      if (!remark) {
        return;
      }

      try {
        // 解析床位费
        if (remark.includes("床位费：")) {
          const match = remark.match(/床位费：([\d.]+)元/);
          if (match) {
            this.form.bedFee = parseFloat(match[1]);
          }
        }

        // 解析护理费
        if (remark.includes("护理费：")) {
          const match = remark.match(/护理费：([\d.]+)元/);
          if (match) {
            this.form.careFee = parseFloat(match[1]);
          }
        }

        // 解析缴纳月数
        if (remark.includes("缴费月数：")) {
          const match = remark.match(/缴费月数：(\d+)个月/);
          if (match) {
            this.form.monthCount = parseInt(match[1]);
          }
        }

        // 解析押金
        if (remark.includes("押金：")) {
          const match = remark.match(/押金：([\d.]+)元/);
          if (match) {
            this.form.depositFee = parseFloat(match[1]);
          }
        }

        // 解析会员费
        if (remark.includes("会员费：")) {
          const match = remark.match(/会员费：([\d.]+)元/);
          if (match) {
            this.form.memberFee = parseFloat(match[1]);
          }
        }

        // 解析护理等级
        if (remark.includes("护理等级：")) {
          const match = remark.match(/护理等级：(\S+)/);
          if (match) {
            this.form.careLevel = match[1];
          }
        }
      } catch (e) {
        console.warn("解析费用信息失败", e);
      }
    },
    // 床位类型变化时更新费用
    onBedTypeChange(bedType) {
      // 根据床位类型设置默认费用
      const bedTypePrices = {
        '1': { bedFee: 350, depositFee: 8000, memberFee: 3000 },
        '2': { bedFee: 500, depositFee: 10000, memberFee: 5000 },
        '3': { bedFee: 1200, depositFee: 15000, memberFee: 10000 }
      };

      const prices = bedTypePrices[bedType] || bedTypePrices['1'];
      this.form.bedFee = prices.bedFee;
      this.form.depositFee = prices.depositFee;
      this.form.memberFee = prices.memberFee;
      this.calculateTotal();
    },
    // 计算总金额
    calculateTotal() {
      // 总金额在computed中自动计算
    },
    // 审核通过
    handleApprove() {
      // 检查是否有可用床位（如果要换床位）
      if (this.availableBeds.length === 0 && this.form.newBedId) {
        this.$modal.msgError("暂无可用床位，无法完成审核");
        return;
      }

      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitting = true;

          const submitData = {
            orderId: this.form.orderId,
            orderAmount: parseFloat(this.totalAmount),
            monthCount: this.form.monthCount,
            remark: this.buildFeeRemark()
          };

          // 如果选择了新床位，添加到提交数据
          if (this.form.newBedId) {
            submitData.bedId = this.form.newBedId;
          }

          approveOrder(submitData).then(() => {
            this.$modal.msgSuccess("审核通过成功");
            this.visible = false;
            this.$emit("success");
          }).finally(() => {
            this.submitting = false;
          });
        }
      });
    },
    // 审核拒绝
    handleReject() {
      this.$modal.confirm('确认拒绝该订单吗？').then(() => {
        this.submitting = true;

        const submitData = {
          orderId: this.form.orderId,
          remark: this.form.approveRemark || '审核拒绝'
        };

        rejectOrder(submitData).then(() => {
          this.$modal.msgSuccess("已拒绝该订单");
          this.visible = false;
          this.$emit("success");
        }).finally(() => {
          this.submitting = false;
        });
      }).catch(() => {});
    },
    // 构建费用说明remark
    buildFeeRemark() {
      const sb = [];
      sb.push("床位费：" + this.form.bedFee + "元/月\\n");
      sb.push("护理费：" + this.form.careFee + "元/月\\n");
      sb.push("护理等级：" + this.form.careLevel + "\\n");
      sb.push("服务费合计：" + this.monthlyServiceFee + "元/月\\n");
      sb.push("缴费月数：" + this.form.monthCount + "个月\\n");
      sb.push("押金：" + this.form.depositFee + "元\\n");
      if (this.form.memberFee > 0) {
        sb.push("会员费：" + this.form.memberFee + "元\\n");
      }
      sb.push("总计：" + this.totalAmount + "元");
      if (this.form.approveRemark) {
        sb.push("\\n审核备注：" + this.form.approveRemark);
      }
      return sb.join("");
    },
    reset() {
      this.form = {
        orderId: null,
        orderNo: '',
        elderName: '',
        currentBedInfo: '',
        newBedId: null,
        institutionId: null,
        originalBedId: null,
        bedType: '1',
        careLevel: '自理',
        bedFee: 500,
        careFee: 500,
        monthCount: 1,
        depositFee: 10000,
        memberFee: 5000,
        approveRemark: ''
      };
      this.availableBeds = [];
      if (this.$refs.form) {
        this.$refs.form.resetFields();
      }
    },
    handleClose() {
      this.reset();
    }
  }
};
</script>

<style scoped>
:deep(.el-divider__text) {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}
</style>
