<template>
  <el-dialog title="线下支付" :visible.sync="visible" width="500px" append-to-body>
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="订单号">
        <el-input v-model="orderInfo.orderNo" disabled />
      </el-form-item>
      <el-form-item label="订单金额">
        <el-input v-model="orderInfo.orderAmount" disabled>
          <template slot="prepend">¥</template>
        </el-input>
      </el-form-item>
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
        </el-radio-group>
      </el-form-item>
      <el-form-item label="支付凭证" prop="paymentProof">
        <el-upload
          class="upload-demo"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="handleUploadSuccess"
          :before-upload="beforeUpload"
          accept="image/*"
        >
          <el-button size="small" icon="el-icon-upload" v-if="!form.paymentProof">上传凭证图片</el-button>
          <div v-else class="image-preview">
            <el-image
              :src="paymentProofUrl"
              :preview-src-list="[paymentProofUrl]"
              fit="cover"
              style="width: 100px; height: 100px;"
            />
            <el-button type="danger" icon="el-icon-delete" circle size="mini" class="delete-btn" @click.stop="removeImage" />
          </div>
        </el-upload>
        <div class="el-upload__tip">只能上传jpg/png图片文件，且不超过5MB</div>
      </el-form-item>
      <el-form-item label="凭证备注" prop="paymentProofRemark">
        <el-input v-model="form.paymentProofRemark" type="textarea" placeholder="请输入支付凭证备注（可选）" :rows="3" />
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm" :loading="submitLoading">确认支付</el-button>
      <el-button @click="visible = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { offlinePay } from "@/api/order/orderInfo";
import { getToken } from "@/utils/auth";

export default {
  name: "OfflinePayDialog",
  data() {
    return {
      visible: false,
      submitLoading: false,
      orderInfo: {},
      baseUrl: process.env.VUE_APP_BASE_API,
      form: {
        paymentMethod: 'cash',
        paymentProof: '',
        paymentProofRemark: ''
      },
      uploadUrl: process.env.VUE_APP_BASE_API + "/common/upload",
      uploadHeaders: {
        Authorization: "Bearer " + getToken()
      },
      rules: {
        paymentMethod: [
          { required: true, message: "支付方式不能为空", trigger: "change" }
        ],
        paymentProof: [
          { required: true, message: "请上传支付凭证图片", trigger: "change" }
        ]
      }
    };
  },
  computed: {
    // 获取完整的图片URL（拼接API前缀）
    paymentProofUrl() {
      if (!this.form.paymentProof) return '';
      // 如果是完整URL（http/https开头），直接返回
      if (this.form.paymentProof.startsWith('http://') || this.form.paymentProof.startsWith('https://')) {
        return this.form.paymentProof;
      }
      // 否则拼接API前缀
      return this.baseUrl + this.form.paymentProof;
    }
  },
  methods: {
    show(orderInfo) {
      this.visible = true;
      this.orderInfo = { ...orderInfo };
      this.form = {
        paymentMethod: 'cash',
        paymentProof: '',
        paymentProofRemark: ''
      };
      // 重置表单验证
      this.$nextTick(() => {
        if (this.$refs.form) {
          this.$refs.form.clearValidate();
        }
      });
    },
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/');
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!isImage) {
        this.$modal.msgError('只能上传图片文件!');
        return false;
      }
      if (!isLt5M) {
        this.$modal.msgError('图片大小不能超过 5MB!');
        return false;
      }
      return true;
    },
    handleUploadSuccess(response) {
      if (response.code === 200) {
        this.form.paymentProof = response.url;
        this.$modal.msgSuccess("图片上传成功");
      } else {
        this.$modal.msgError(response.msg || "图片上传失败");
      }
    },
    removeImage() {
      this.form.paymentProof = '';
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.submitLoading = true;
          offlinePay(this.orderInfo.orderId, this.form)
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

<style scoped>
.upload-demo {
  text-align: left;
}

.image-preview {
  position: relative;
  display: inline-block;
}

.delete-btn {
  position: absolute;
  top: -8px;
  right: -8px;
}

.el-upload__tip {
  line-height: 1.5;
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}
</style>
