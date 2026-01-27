<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="老人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入老人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCard">
        <el-input
          v-model="queryParams.idCard"
          placeholder="请输入身份证号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['pension:elder:manage:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['pension:elder:manage:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:elder:manage:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 老人档案列表表格 -->
    <el-table v-loading="loading" :data="elderList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="老人姓名" align="center" prop="elderName" min-width="100" />
      <el-table-column label="性别" align="center" prop="gender" width="60">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.elder_gender" :value="scope.row.gender"/>
        </template>
      </el-table-column>
      <el-table-column label="年龄" align="center" prop="age" width="60" />
      <el-table-column label="身份证号" align="center" prop="idCard" min-width="180" />
      <el-table-column label="联系电话" align="center" prop="phone" min-width="120" />
      <el-table-column label="紧急联系人" align="center" prop="emergencyContact" min-width="100" />
      <el-table-column label="紧急联系电话" align="center" prop="emergencyPhone" min-width="120" />
      <el-table-column label="健康状况" align="center" prop="healthStatus" min-width="100" show-overflow-tooltip />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="warning">未入住</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="success">已入住</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="info">已退住</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['pension:elder:manage:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pension:elder:manage:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pension:elder:manage:remove']"
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

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="老人姓名" prop="elderName">
              <el-input v-model="form.elderName" placeholder="请输入老人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
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
              <el-input v-model="form.idCard" placeholder="请输入身份证号" @blur="parseIdCard" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="form.birthDate"
                type="date"
                placeholder="请选择出生日期"
                value-format="yyyy-MM-dd"
                style="width: 100%;"
                @change="calculateAgeFromBirthDate">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="form.age" :min="1" :max="120" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系电话" prop="emergencyPhone">
              <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="护理等级" prop="careLevel">
              <el-select v-model="form.careLevel" placeholder="请选择护理等级" style="width: 100%">
                <el-option
                  v-for="dict in dict.type.elder_care_level"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状况" prop="healthStatus">
              <el-input v-model="form.healthStatus" placeholder="请输入健康状况" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="家庭住址" prop="address">
          <el-input v-model="form.address" type="textarea" placeholder="请输入家庭住址" />
        </el-form-item>
        <el-form-item label="特殊需求" prop="specialNeeds">
          <el-input v-model="form.specialNeeds" type="textarea" placeholder="请输入特殊需求或注意事项" />
        </el-form-item>

        <!-- 图片上传 -->
        <el-divider content-position="left">图片上传</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="老人照片">
              <image-upload
                v-model="form.photoPath"
                :limit="1"
                :file-size="2"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身份证正面">
              <image-upload
                v-model="form.idCardFrontPath"
                :limit="1"
                :file-size="2"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身份证反面">
              <image-upload
                v-model="form.idCardBackPath"
                :limit="1"
                :file-size="2"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="老人档案详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions title="基本信息" :column="2" border>
        <el-descriptions-item label="老人姓名">{{ elderDetail.elderName }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          <dict-tag :options="dict.type.elder_gender" :value="elderDetail.gender"/>
        </el-descriptions-item>
        <el-descriptions-item label="年龄">{{ elderDetail.age }}岁</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ elderDetail.idCard }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ elderDetail.birthDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ elderDetail.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ elderDetail.emergencyContact || '-' }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系电话">{{ elderDetail.emergencyPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="家庭住址" :span="2">{{ elderDetail.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="护理等级">
          <dict-tag :options="dict.type.elder_care_level" :value="elderDetail.careLevel"/>
        </el-descriptions-item>
        <el-descriptions-item label="健康状况">{{ elderDetail.healthStatus || '-' }}</el-descriptions-item>
        <el-descriptions-item label="特殊需求" :span="2">{{ elderDetail.specialNeeds || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 图片展示 -->
      <el-divider content-position="left">图片资料</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="image-preview">
            <div class="image-label">老人照片</div>
            <el-image
              v-if="elderDetail.photoPath"
              :src="getImageUrl(elderDetail.photoPath)"
              :preview-src-list="[getImageUrl(elderDetail.photoPath)]"
              fit="cover"
              style="width: 100%; height: 200px;">
            </el-image>
            <div v-else class="no-image">暂无照片</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="image-preview">
            <div class="image-label">身份证正面</div>
            <el-image
              v-if="elderDetail.idCardFrontPath"
              :src="getImageUrl(elderDetail.idCardFrontPath)"
              :preview-src-list="[getImageUrl(elderDetail.idCardFrontPath)]"
              fit="cover"
              style="width: 100%; height: 200px;">
            </el-image>
            <div v-else class="no-image">暂无照片</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="image-preview">
            <div class="image-label">身份证反面</div>
            <el-image
              v-if="elderDetail.idCardBackPath"
              :src="getImageUrl(elderDetail.idCardBackPath)"
              :preview-src-list="[getImageUrl(elderDetail.idCardBackPath)]"
              fit="cover"
              style="width: 100%; height: 200px;">
            </el-image>
            <div v-else class="no-image">暂无照片</div>
          </div>
        </el-col>
      </el-row>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listElderManage, getElderManage, addElderManage, updateElderManage, delElderManage, exportElderManage } from "@/api/pension/elderManage";
import ImageUpload from "@/components/ImageUpload";

export default {
  name: "PensionElderManage",
  dicts: ['elder_gender', 'elder_care_level'],
  components: {
    ImageUpload
  },
  data() {
    return {
      // 图片服务器地址
      baseUrl: process.env.VUE_APP_BASE_API,
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
      // 老人档案列表数据
      elderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        elderName: null,
        idCard: null,
        phone: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        elderName: [
          { required: true, message: "老人姓名不能为空", trigger: "blur" }
        ],
        gender: [
          { required: true, message: "性别不能为空", trigger: "change" }
        ],
        idCard: [
          { required: true, message: "身份证号不能为空", trigger: "blur" },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: "请输入正确的身份证号", trigger: "blur" }
        ],
        age: [
          { required: true, message: "年龄不能为空", trigger: "blur" }
        ],
        emergencyContact: [
          { required: true, message: "紧急联系人不能为空", trigger: "blur" }
        ],
        emergencyPhone: [
          { required: true, message: "紧急联系电话不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ]
      },
      // 老人详情
      elderDetail: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询老人档案列表 */
    getList() {
      this.loading = true;
      listElderManage(this.queryParams).then(response => {
        this.elderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        elderId: null,
        elderName: null,
        gender: null,
        idCard: null,
        birthDate: null,
        age: null,
        phone: null,
        address: null,
        emergencyContact: null,
        emergencyPhone: null,
        careLevel: '1',
        healthStatus: null,
        specialNeeds: null,
        photoPath: null,
        idCardFrontPath: null,
        idCardBackPath: null
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.elderId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "新增老人档案";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const elderId = row.elderId || this.ids[0];
      getElderManage(elderId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改老人档案";
      });
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      const elderId = row.elderId;
      getElderManage(elderId).then(response => {
        this.elderDetail = response.data;
        this.detailOpen = true;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.elderId != null) {
            updateElderManage(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addElderManage(this.form).then(response => {
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
      const elderIds = row.elderId || this.ids;
      this.$modal.confirm('是否确认删除老人档案编号为"' + elderIds + '"的数据项？').then(function() {
        return delElderManage(elderIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/elder/manage/export', {
        ...this.queryParams
      }, `老人档案_${new Date().getTime()}.xlsx`)
    },
    /** 获取完整的图片URL */
    getImageUrl(path) {
      if (!path) return '';
      // 如果路径已经是完整URL，直接返回
      if (path.startsWith('http://') || path.startsWith('https://')) {
        return path;
      }
      // 否则添加 baseUrl 前缀
      return this.baseUrl + path;
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
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
          age--;
        }
        this.form.age = age;
      }
    }
  }
};
</script>

<style scoped>
.image-preview {
  text-align: center;
}

.image-label {
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.no-image {
  width: 100%;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 14px;
  border-radius: 4px;
}

.el-divider {
  margin: 20px 0;
}
</style>
