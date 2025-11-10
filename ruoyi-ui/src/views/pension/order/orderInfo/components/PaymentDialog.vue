<template>
  <el-dialog title="订单支付" :visible.sync="visible" width="500px" append-to-body>
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="订单号">
        <el-input v-model="orderInfo.orderNo" disabled />
      </el-form-item>
      <el-form-item label="订单金额">
        <el-input v-model="orderInfo.orderAmount" disabled>
          <template slot="prepend">¥</template>
        </el-input>
      </el-form-item>
      <el-form-item label="未付金额">
        <el-input v-model="orderInfo.unpaidAmount" disabled>
          <template slot="prepend">¥</template>
        </el-input>
      </el-form-item>
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
      <el-form-item label="支付金额" prop="paymentAmount">
        <el-input-number
          v-model="form.paymentAmount"
          :precision="2"
          :min="0.01"
          :max="orderInfo.unpaidAmount"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="支付备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入支付备注" />
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm" :loading="submitLoading">确认支付</el-button>
      <el-button @click="visible = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { simulatePayment } from "@/api/order/paymentRecord";

export default {
  name: "PaymentDialog",
  dicts: ['payment_method'],
  data() {
    return {
      visible: false,
      submitLoading: false,
      orderInfo: {},
      form: {
        paymentMethod: null,
        paymentAmount: null,
        remark: ''
      },
      rules: {
        paymentMethod: [
          { required: true, message: "支付方式不能为空", trigger: "change" }
        ],
        paymentAmount: [
          { required: true, message: "支付金额不能为空", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    show(orderInfo) {
      this.visible = true;
      this.orderInfo = { ...orderInfo };
      this.form = {
        paymentMethod: null,
        paymentAmount: orderInfo.unpaidAmount,
        remark: ''
      };
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.paymentAmount > this.orderInfo.unpaidAmount) {
            this.$modal.msgError("支付金额不能超过未付金额");
            return;
          }

          this.submitLoading = true;
          simulatePayment(this.orderInfo.orderId, this.form.paymentMethod)
            .then(response => {
              this.$modal.msgSuccess("支付成功");
              this.visible = false;
              this.$emit('success');
            })
            .catch(() => {
              this.$modal.msgError("支付失败");
            })
            .finally(() => {
              this.submitLoading = false;
            });
        }
      });
    }
  }
};
</script>