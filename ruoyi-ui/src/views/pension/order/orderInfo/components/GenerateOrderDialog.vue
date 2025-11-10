<template>
  <el-dialog title="根据入住申请生成订单" :visible.sync="visible" width="600px" append-to-body>
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="入住申请ID" prop="checkInId">
        <el-input v-model="form.checkInId" placeholder="请输入入住申请ID">
          <el-button slot="append" icon="el-icon-search" @click="handleSearchCheckIn">查询</el-button>
        </el-input>
      </el-form-item>

      <el-form-item label="老人信息" v-if="checkInInfo.elderName">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="老人姓名">{{ checkInInfo.elderName }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ checkInInfo.idCard }}</el-descriptions-item>
          <el-descriptions-item label="机构名称">{{ checkInInfo.institutionName }}</el-descriptions-item>
          <el-descriptions-item label="床位号">{{ checkInInfo.bedNumber }}</el-descriptions-item>
          <el-descriptions-item label="入住日期">{{ parseTime(checkInInfo.checkInDate) }}</el-descriptions-item>
          <el-descriptions-item label="护理等级">{{ checkInInfo.careLevel }}</el-descriptions-item>
        </el-descriptions>
      </el-form-item>

      <el-form-item label="生成订单类型" v-if="checkInInfo.elderName">
        <el-checkbox-group v-model="orderTypes">
          <el-checkbox label="bed_fee">床位费订单</el-checkbox>
          <el-checkbox label="care_fee">护理费订单</el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <el-form-item label="费用设置" v-if="checkInInfo.elderName && orderTypes.length > 0">
        <el-card>
          <div slot="header">
            <span>床位费设置</span>
          </div>
          <el-row v-if="orderTypes.includes('bed_fee')">
            <el-col :span="12">
              <el-form-item label="床位单价" label-width="80px">
                <el-input-number
                  v-model="bedFee.unitPrice"
                  :precision="2"
                  :min="0"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="收费周期" label-width="80px">
                <el-select v-model="bedFee.period" placeholder="请选择">
                  <el-option label="按月" value="monthly"></el-option>
                  <el-option label="按季" value="quarterly"></el-option>
                  <el-option label="按年" value="yearly"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <el-card style="margin-top: 10px;">
          <div slot="header">
            <span>护理费设置</span>
          </div>
          <el-row v-if="orderTypes.includes('care_fee')">
            <el-col :span="12">
              <el-form-item label="护理单价" label-width="80px">
                <el-input-number
                  v-model="careFee.unitPrice"
                  :precision="2"
                  :min="0"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="收费周期" label-width="80px">
                <el-select v-model="careFee.period" placeholder="请选择">
                  <el-option label="按月" value="monthly"></el-option>
                  <el-option label="按季" value="quarterly"></el-option>
                  <el-option label="按年" value="yearly"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm" :loading="submitLoading" :disabled="!canSubmit">生成订单</el-button>
      <el-button @click="visible = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { generateOrderByCheckIn } from "@/api/order/orderInfo";
import { getElderCheckIn } from "@/api/pension/elderCheckIn";

export default {
  name: "GenerateOrderDialog",
  data() {
    return {
      visible: false,
      submitLoading: false,
      form: {
        checkInId: null
      },
      checkInInfo: {},
      orderTypes: [],
      bedFee: {
        unitPrice: 2000,
        period: 'monthly'
      },
      careFee: {
        unitPrice: 1500,
        period: 'monthly'
      },
      rules: {
        checkInId: [
          { required: true, message: "入住申请ID不能为空", trigger: "blur" }
        ]
      }
    };
  },
  computed: {
    canSubmit() {
      return this.checkInInfo.elderName && this.orderTypes.length > 0;
    }
  },
  methods: {
    show() {
      this.visible = true;
      this.reset();
    },
    reset() {
      this.form = {
        checkInId: null
      };
      this.checkInInfo = {};
      this.orderTypes = [];
      this.bedFee = {
        unitPrice: 2000,
        period: 'monthly'
      };
      this.careFee = {
        unitPrice: 1500,
        period: 'monthly'
      };
      this.$nextTick(() => {
        this.$refs["form"].resetFields();
      });
    },
    handleSearchCheckIn() {
      if (!this.form.checkInId) {
        this.$modal.msgError("请输入入住申请ID");
        return;
      }

      getElderCheckIn(this.form.checkInId).then(response => {
        const checkIn = response.data;
        if (checkIn.checkInStatus !== '1') {
          this.$modal.msgError("只能为已通过的入住申请生成订单");
          return;
        }
        this.checkInInfo = checkIn;
      }).catch(() => {
        this.$modal.msgError("查询入住申请失败");
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (!this.canSubmit) {
            this.$modal.msgError("请完善订单生成信息");
            return;
          }

          this.submitLoading = true;
          generateOrderByCheckIn(this.form.checkInId)
            .then(response => {
              this.$modal.msgSuccess("订单生成成功");
              this.visible = false;
              this.$emit('success');
            })
            .catch(() => {
              this.$modal.msgError("订单生成失败");
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