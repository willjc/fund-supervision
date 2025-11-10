<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="统一信用代码" prop="creditCode">
        <el-input
          v-model="queryParams.creditCode"
          placeholder="请输入统一信用代码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson">
        <el-input
          v-model="queryParams.contactPerson"
          placeholder="请输入联系人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input
          v-model="queryParams.contactPhone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="机构状态" clearable>
          <el-option label="草稿" value="4" />
          <el-option label="待审批" value="0" />
          <el-option label="已入驻" value="1" />
          <el-option label="已驳回" value="2" />
          <el-option label="解除监管" value="3" />
        </el-select>
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
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="multiple"
          @click="handleApprove"
        >审批</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="institutionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="机构名称" align="center" prop="institutionName" :show-overflow-tooltip="true" />
      <el-table-column label="统一信用代码" align="center" prop="creditCode" />
      <el-table-column label="机构类型" align="center" prop="institutionType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pension_institution_type" :value="scope.row.institutionType"/>
        </template>
      </el-table-column>
      <el-table-column label="床位数" align="center" prop="bedCount" />
      <el-table-column label="联系人" align="center" prop="contactPerson" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pension_institution_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="applyTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
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

    <!-- 查看详情对话框 -->
    <el-dialog v-if="isView" title="机构详情" :visible.sync="open" width="1200px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="机构名称">{{ form.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="统一信用代码">{{ form.creditCode }}</el-descriptions-item>
        <el-descriptions-item label="机构备案号">{{ form.recordNumber }}</el-descriptions-item>
        <el-descriptions-item label="注册资金">{{ form.registeredCapital }}万元</el-descriptions-item>
        <el-descriptions-item label="注册地址" :span="2">{{ form.registeredAddress }}</el-descriptions-item>
        <el-descriptions-item label="所属街道/区域">{{ form.street }}</el-descriptions-item>
        <el-descriptions-item label="机构联系人">{{ form.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ form.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="成立日期">{{ parseTime(form.establishedDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="实际经营地址" :span="2">{{ form.actualAddress }}</el-descriptions-item>
        <el-descriptions-item label="兴办机构">{{ form.organizer }}</el-descriptions-item>
      </el-descriptions>

      <!-- 负责人信息 -->
      <el-divider content-position="left">负责人信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="负责人姓名">{{ form.responsibleName }}</el-descriptions-item>
        <el-descriptions-item label="负责人身份证号">{{ form.responsibleIdCard }}</el-descriptions-item>
        <el-descriptions-item label="负责人居住地" :span="2">{{ form.responsibleAddress }}</el-descriptions-item>
        <el-descriptions-item label="负责人电话">{{ form.responsiblePhone }}</el-descriptions-item>
      </el-descriptions>

      <!-- 经营信息 -->
      <el-divider content-position="left">经营信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="养老机构类型">{{ getInstitutionTypeText(form.institutionType) }}</el-descriptions-item>
        <el-descriptions-item label="床位数">{{ form.bedCount }}张</el-descriptions-item>
        <el-descriptions-item label="收费区间">{{ form.feeRange }}</el-descriptions-item>
        <el-descriptions-item label="固定资产净额">{{ form.fixedAssets }}万元</el-descriptions-item>
        <el-descriptions-item label="监管账户">{{ form.superviseAccount }}</el-descriptions-item>
        <el-descriptions-item label="基本账户">{{ form.bankAccount }}</el-descriptions-item>
      </el-descriptions>

      <!-- 申请状态信息 -->
      <el-divider content-position="left">申请信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请状态">
          <el-tag v-if="form.status === '4'" type="info">草稿</el-tag>
          <el-tag v-else-if="form.status === '0'" type="warning">待审批</el-tag>
          <el-tag v-else-if="form.status === '1'" type="success">已入驻</el-tag>
          <el-tag v-else-if="form.status === '2'" type="danger">已驳回</el-tag>
          <el-tag v-else-if="form.status === '5'" type="primary">维护中</el-tag>
          <el-tag v-else-if="form.status === '6'" type="warning">维护待审批</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ parseTime(form.applyTime, '{y}-{m}-{d} {h}:{i}') }}</el-descriptions-item>
        <el-descriptions-item label="审批人" v-if="form.approveUser">{{ form.approveUser }}</el-descriptions-item>
        <el-descriptions-item label="审批时间" v-if="form.approveTime">{{ parseTime(form.approveTime, '{y}-{m}-{d} {h}:{i}') }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2" v-if="form.approveRemark">{{ form.approveRemark }}</el-descriptions-item>
      </el-descriptions>

      <!-- 上传材料 -->
      <el-divider content-position="left">上传材料</el-divider>
      <el-row :gutter="20">
        <el-col :span="8" v-if="form.businessLicense">
          <el-card shadow="hover">
            <div slot="header">营业执照</div>
            <el-image
              :src="form.businessLicense"
              style="width: 100%; height: 200px;"
              fit="contain"
              :preview-src-list="[form.businessLicense]"
            ></el-image>
          </el-card>
        </el-col>
        <el-col :span="8" v-if="form.approvalCertificate">
          <el-card shadow="hover">
            <div slot="header">社会福利机构设置批准证书</div>
            <el-image
              :src="form.approvalCertificate"
              style="width: 100%; height: 200px;"
              fit="contain"
              :preview-src-list="[form.approvalCertificate]"
            ></el-image>
          </el-card>
        </el-col>
        <el-col :span="8" v-if="form.supervisionAgreement">
          <el-card shadow="hover">
            <div slot="header">三方监管协议</div>
            <el-image
              :src="form.supervisionAgreement"
              style="width: 100%; height: 200px;"
              fit="contain"
              :preview-src-list="[form.supervisionAgreement]"
            ></el-image>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!form.businessLicense && !form.approvalCertificate && !form.supervisionAgreement" description="暂无上传材料"></el-empty>

      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改养老机构对话框 -->
    <el-dialog v-else :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="机构名称" prop="institutionName">
              <el-input v-model="form.institutionName" placeholder="请输入机构名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统一信用代码" prop="creditCode">
              <el-input v-model="form.creditCode" placeholder="请输入统一信用代码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="注册资金(万元)" prop="registeredCapital">
              <el-input-number v-model="form.registeredCapital" :precision="2" :min="0" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机构类型" prop="institutionType">
              <el-select v-model="form.institutionType" placeholder="请选择机构类型">
                <el-option label="民办" value="1" />
                <el-option label="公办" value="2" />
                <el-option label="公建民营" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="注册地址" prop="registeredAddress">
              <el-input v-model="form.registeredAddress" type="textarea" placeholder="请输入注册地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属街道/区域" prop="street">
              <el-input v-model="form.street" placeholder="请输入所属街道/区域" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="兴办机构" prop="organizer">
              <el-input v-model="form.organizer" placeholder="请输入兴办机构" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="实际经营地址" prop="actualAddress">
              <el-input v-model="form.actualAddress" type="textarea" placeholder="请输入实际经营地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="法定代表人" prop="legalPerson">
              <el-input v-model="form.legalPerson" placeholder="请输入法定代表人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">负责人信息</el-divider>
        <el-row>
          <el-col :span="12">
            <el-form-item label="负责人姓名" prop="responsibleName">
              <el-input v-model="form.responsibleName" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人身份证号" prop="responsibleIdCard">
              <el-input v-model="form.responsibleIdCard" placeholder="请输入负责人身份证号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="负责人居住地" prop="responsibleAddress">
              <el-input v-model="form.responsibleAddress" placeholder="请输入负责人居住地" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人电话" prop="responsiblePhone">
              <el-input v-model="form.responsiblePhone" placeholder="请输入负责人电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="form.contactEmail" placeholder="请输入联系邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="床位数" prop="bedCount">
              <el-input-number v-model="form.bedCount" :min="0" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="收费区间" prop="feeRange">
              <el-input v-model="form.feeRange" placeholder="请输入收费区间" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="成立日期" prop="establishedDate">
              <el-date-picker clearable
                v-model="form.establishedDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择成立日期"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="备案号" prop="recordNumber">
              <el-input v-model="form.recordNumber" placeholder="请输入备案号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="固定资产净额" prop="fixedAssets">
              <el-input-number v-model="form.fixedAssets" :precision="2" :min="0" style="width: 100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="基本结算账户" prop="bankAccount">
              <el-input v-model="form.bankAccount" placeholder="请输入基本结算账户" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="监管账户" prop="superviseAccount">
              <el-input v-model="form.superviseAccount" placeholder="请输入监管账户" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="经营范围" prop="businessScope">
              <el-input v-model="form.businessScope" type="textarea" placeholder="请输入经营范围" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog title="审批机构入驻申请" :visible.sync="approveOpen" width="500px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" :rules="approveRules" label-width="80px">
        <el-form-item label="机构名称" prop="institutionName">
          <el-input v-model="approveForm.institutionName" :disabled="true" />
        </el-form-item>
        <el-form-item label="审批结果" prop="status">
          <el-radio-group v-model="approveForm.status">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="approveRemark">
          <el-input v-model="approveForm.approveRemark" type="textarea" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApprove">确 定</el-button>
        <el-button @click="approveOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInstitution, getInstitution, delInstitution, addInstitution, updateInstitution, approveInstitution } from "@/api/pension/institution";

export default {
  name: "PensionInstitution",
  dicts: ['pension_institution_type', 'pension_institution_status'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      institutionList: [],
      title: "",
      open: false,
      approveOpen: false,
      isView: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        creditCode: null,
        contactPerson: null,
        contactPhone: null,
        status: null
      },
      // 表单参数
      form: {},
      // 审批表单参数
      approveForm: {},
      // 表单校验
      rules: {
        institutionName: [
          { required: true, message: "机构名称不能为空", trigger: "blur" }
        ],
        creditCode: [
          { required: true, message: "统一信用代码不能为空", trigger: "blur" }
        ],
        contactPerson: [
          { required: true, message: "联系人不能为空", trigger: "blur" }
        ],
        contactPhone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" }
        ],
        institutionType: [
          { required: true, message: "机构类型不能为空", trigger: "change" }
        ]
      },
      // 审批表单校验
      approveRules: {
        status: [
          { required: true, message: "审批结果不能为空", trigger: "change" }
        ],
        approveRemark: [
          { required: true, message: "审批意见不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询养老机构列表 */
    getList() {
      this.loading = true;
      listInstitution(this.queryParams).then(response => {
        this.institutionList = response.rows;
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
        institutionId: null,
        institutionName: null,
        creditCode: null,
        registeredCapital: null,
        registeredAddress: null,
        actualAddress: null,
        legalPerson: null,
        contactPerson: null,
        contactPhone: null,
        contactEmail: null,
        businessScope: null,
        institutionType: null,
        bedCount: null,
        establishedDate: null,
        recordNumber: null,
        fixedAssets: null,
        bankAccount: null,
        superviseAccount: null,
        status: null,
        applyTime: null,
        approveTime: null,
        approveUser: null,
        approveRemark: null,
        blacklistFlag: null,
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.institutionId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加养老机构信息";
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.reset();
      const institutionId = row.institutionId || this.ids
      getInstitution(institutionId).then(response => {
        this.form = response.data;
        this.isView = true;
        this.open = true;
        this.title = "查看养老机构信息";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      // 如果机构状态为草稿(4)、已驳回(2)或已入驻(1),跳转到apply.vue页面进行完整编辑
      if (row.status === '4' || row.status === '2' || row.status === '1') {
        this.$router.push({
          path: '/pension/institution/apply',
          query: { id: row.institutionId }
        });
        return;
      }

      // 其他状态,弹出对话框编辑
      this.reset();
      const institutionId = row.institutionId || this.ids
      getInstitution(institutionId).then(response => {
        this.form = response.data;
        this.isView = false;
        this.open = true;
        this.title = "修改养老机构信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.institutionId != null) {
            updateInstitution(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addInstitution(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 审批按钮操作 */
    handleApprove() {
      if (this.ids.length !== 1) {
        this.$modal.msgError("请选择一条数据进行审批");
        return;
      }
      const institutionId = this.ids[0];
      getInstitution(institutionId).then(response => {
        this.approveForm = {
          institutionId: institutionId,
          institutionName: response.data.institutionName,
          status: '1',
          approveRemark: ''
        };
        this.approveOpen = true;
      });
    },
    /** 提交审批 */
    submitApprove() {
      this.$refs["approveForm"].validate(valid => {
        if (valid) {
          approveInstitution(this.approveForm).then(response => {
            this.$modal.msgSuccess("审批成功");
            this.approveOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const institutionIds = row.institutionId || this.ids;
      this.$modal.confirm('是否确认删除养老机构信息编号为"' + institutionIds + '"的数据项？').then(function() {
        return delInstitution(institutionIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/institution/export', {
        ...this.queryParams
      }, `institution_${new Date().getTime()}.xlsx`)
    },
    /** 获取机构类型文本 */
    getInstitutionTypeText(type) {
      const typeMap = {
        '1': '民办',
        '2': '公办',
        '3': '公建民营'
      };
      return typeMap[type] || type;
    }
  }
};
</script>