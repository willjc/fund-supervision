<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="老人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入老人姓名"
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
      <el-form-item label="身份证号" prop="idCard">
        <el-input
          v-model="queryParams.idCard"
          placeholder="请输入身份证号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="护理等级" prop="careLevel">
        <el-select v-model="queryParams.careLevel" placeholder="请选择护理等级" clearable>
          <el-option
            v-for="dict in dict.type.elder_care_level"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.elder_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
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
          v-hasPermi="['elder:info:add']"
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
          v-hasPermi="['elder:info:edit']"
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
          v-hasPermi="['elder:info:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['elder:info:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="elderInfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="老人姓名" align="center" prop="elderName" />
      <el-table-column label="性别" align="center" prop="gender">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.elder_gender" :value="scope.row.gender"/>
        </template>
      </el-table-column>
      <el-table-column label="年龄" align="center" prop="age" />
      <el-table-column label="身份证号" align="center" prop="idCard" />
      <el-table-column label="联系电话" align="center" prop="phone" />
      <el-table-column label="护理等级" align="center" prop="careLevel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.elder_care_level" :value="scope.row.careLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.elder_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="出生日期" align="center" prop="birthDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.birthDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['elder:info:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['elder:info:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-user"
            @click="handleFamilyManage(scope.row)"
            v-hasPermi="['elder:info:query']"
          >家属管理</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-key"
            @click="handleSetPassword(scope.row)"
            v-hasPermi="['elder:info:edit']"
          >设置密码</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleCheckIn(scope.row)"
            v-hasPermi="['elder:checkin:add']"
          >入住申请</el-button>
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

    <!-- 添加或修改老人基础信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="老人姓名" prop="elderName">
              <el-input v-model="form.elderName" placeholder="请输入老人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择性别">
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
        <el-row>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker clearable
                v-model="form.birthDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择出生日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input v-model="form.age" placeholder="请输入年龄" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="护理等级" prop="careLevel">
              <el-select v-model="form.careLevel" placeholder="请选择护理等级">
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
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option
                  v-for="dict in dict.type.elder_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="家庭住址" prop="address">
          <el-input v-model="form.address" type="textarea" placeholder="请输入家庭住址" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系电话" prop="emergencyPhone">
              <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="健康状况" prop="healthStatus">
          <el-input v-model="form.healthStatus" type="textarea" placeholder="请输入健康状况" />
        </el-form-item>
        <el-form-item label="特殊需求" prop="specialNeeds">
          <el-input v-model="form.specialNeeds" type="textarea" placeholder="请输入特殊需求" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 家属管理对话框 -->
    <el-dialog :title="'家属管理 - ' + (currentElderForFamily ? currentElderForFamily.elderName : '')" :visible.sync="familyManageOpen" width="800px" append-to-body>
      <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAddFamily" style="margin-bottom: 15px;">添加家属</el-button>

      <el-table v-loading="familyLoading" :data="familyList" border>
        <el-table-column label="家属姓名" prop="nickName" align="center" />
        <el-table-column label="手机号" prop="phonenumber" align="center" />
        <el-table-column label="关系类型" align="center">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.elder_relation_type" :value="scope.row.relationType"/>
          </template>
        </el-table-column>
        <el-table-column label="关系描述" prop="relationName" align="center" />
        <el-table-column label="默认老人" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isDefault === '1'" type="success">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="主要联系人" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isMainContact === '1'" type="warning">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEditFamily(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDeleteFamily(scope.row)" style="color: #F56C6C;">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="familyManageOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加/编辑家属对话框 -->
    <el-dialog :title="familyFormTitle" :visible.sync="familyFormOpen" width="500px" append-to-body>
      <el-form ref="familyForm" :model="familyForm" :rules="familyRules" label-width="100px">
        <el-form-item label="家属手机号" prop="phonenumber">
          <el-input v-model="familyForm.phonenumber" placeholder="请输入家属手机号" :disabled="familyFormMode === 'edit'" />
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">输入手机号后系统会自动查找对应用户</div>
        </el-form-item>
        <el-form-item label="关系类型" prop="relationType">
          <el-select v-model="familyForm.relationType" placeholder="请选择关系类型" style="width: 100%;">
            <el-option label="子女" value="1" />
            <el-option label="配偶" value="2" />
            <el-option label="兄弟姐妹" value="3" />
            <el-option label="其他亲属" value="4" />
            <el-option label="朋友" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="关系描述" prop="relationName">
          <el-input v-model="familyForm.relationName" placeholder="如:儿子、女儿、配偶等" />
        </el-form-item>
        <el-form-item label="默认老人" prop="isDefault">
          <el-radio-group v-model="familyForm.isDefault">
            <el-radio label="1">是</el-radio>
            <el-radio label="0">否</el-radio>
          </el-radio-group>
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">家属登录后默认展示的老人</div>
        </el-form-item>
        <el-form-item label="主要联系人" prop="isMainContact">
          <el-radio-group v-model="familyForm.isMainContact">
            <el-radio label="1">是</el-radio>
            <el-radio label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="familyForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFamilyForm">确 定</el-button>
        <el-button @click="familyFormOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 设置密码对话框 -->
    <el-dialog title="设置密码" :visible.sync="passwordDialogOpen" width="500px" append-to-body>
      <el-form ref="passwordForm" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="老人姓名">
          <el-input v-model="passwordForm.elderName" disabled />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="passwordForm.idCard" disabled />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="passwordForm.password" type="password" placeholder="请输入新密码(6-20位)" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-alert
          title="说明: 设置密码后,家属可以使用老人的身份证号作为账号、密码进行H5登录"
          type="info"
          :closable="false"
          style="margin-bottom: 15px;">
        </el-alert>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitPasswordForm">确 定</el-button>
        <el-button @click="passwordDialogOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listElder, getElder, delElder, addElder, updateElder } from "@/api/elder/info";
import { listFamily, addFamily, updateFamily, delFamily } from "@/api/elder/family";

export default {
  name: "ElderInfo",
  dicts: ['elder_gender', 'elder_care_level', 'elder_status', 'elder_relation_type'],
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
      // 老人基础信息表格数据
      elderInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        elderName: null,
        gender: null,
        idCard: null,
        phone: null,
        careLevel: null,
        status: null,
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
        birthDate: [
          { required: true, message: "出生日期不能为空", trigger: "blur" }
        ],
        age: [
          { required: true, message: "年龄不能为空", trigger: "blur" },
          { pattern: /^(?:[1-9]\d?|1[01]\d|120)$/, message: "请输入正确的年龄(1-120)", trigger: "blur" }
        ],
        careLevel: [
          { required: true, message: "护理等级不能为空", trigger: "change" }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ],
        emergencyPhone: [
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ]
      },
      // 家属管理相关
      familyManageOpen: false,
      currentElderForFamily: null,
      familyLoading: false,
      familyList: [],
      familyFormOpen: false,
      familyFormMode: 'add',
      familyFormTitle: '添加家属',
      familyForm: {
        familyId: null,
        elderId: null,
        phonenumber: null,
        relationType: '1',
        relationName: null,
        isDefault: '0',
        isMainContact: '0',
        remark: null
      },
      familyRules: {
        phonenumber: [
          { required: true, message: "请输入家属手机号", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" }
        ],
        relationType: [
          { required: true, message: "请选择关系类型", trigger: "change" }
        ],
        relationName: [
          { required: true, message: "请输入关系描述", trigger: "blur" }
        ]
      },
      // 设置密码相关
      passwordDialogOpen: false,
      passwordForm: {
        elderId: null,
        elderName: null,
        idCard: null,
        password: null,
        confirmPassword: null
      },
      passwordRules: {
        password: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          { min: 6, max: 20, message: "密码长度为6-20位", trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, message: "请再次输入密码", trigger: "blur" },
          { validator: (rule, value, callback) => {
              if (value !== this.passwordForm.password) {
                callback(new Error('两次输入的密码不一致'));
              } else {
                callback();
              }
            }, trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询老人基础信息列表 */
    getList() {
      this.loading = true;
      listElder(this.queryParams).then(response => {
        this.elderInfoList = response.rows;
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
        healthStatus: null,
        careLevel: "1",
        specialNeeds: null,
        photoPath: null,
        status: "0",
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.elderId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加老人基础信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const elderId = row.elderId || this.ids
      getElder(elderId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改老人基础信息";
      });
    },
    /** 入住申请按钮操作 */
    handleCheckIn(row) {
      this.$router.push({
        path: '/elder/checkin',
        query: { elderId: row.elderId, elderName: row.elderName }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.elderId != null) {
            updateElder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addElder(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除老人基础信息编号为"' + elderIds + '"的数据项？').then(function() {
        return delElder(elderIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('elder/info/export', {
        ...this.queryParams
      }, `elder_info_${new Date().getTime()}.xlsx`)
    },
    /** 家属管理 */
    handleFamilyManage(row) {
      this.currentElderForFamily = row;
      this.familyManageOpen = true;
      this.getFamilyList(row.elderId);
    },
    /** 获取家属列表 */
    getFamilyList(elderId) {
      this.familyLoading = true;
      listFamily(elderId).then(response => {
        this.familyList = response.rows || [];
        this.familyLoading = false;
      }).catch(() => {
        this.familyLoading = false;
        this.$message.error('获取家属列表失败');
      });
    },
    /** 添加家属 */
    handleAddFamily() {
      this.resetFamilyForm();
      this.familyFormMode = 'add';
      this.familyFormTitle = '添加家属';
      this.familyForm.elderId = this.currentElderForFamily.elderId;
      this.familyFormOpen = true;
    },
    /** 编辑家属 */
    handleEditFamily(row) {
      this.resetFamilyForm();
      this.familyFormMode = 'edit';
      this.familyFormTitle = '编辑家属';
      this.familyForm = {
        familyId: row.familyId,
        elderId: row.elderId,
        phonenumber: row.phonenumber,
        relationType: row.relationType,
        relationName: row.relationName,
        isDefault: row.isDefault,
        isMainContact: row.isMainContact,
        remark: row.remark
      };
      this.familyFormOpen = true;
    },
    /** 删除家属 */
    handleDeleteFamily(row) {
      this.$confirm('确定要删除该家属关联吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delFamily(row.familyId).then(response => {
          this.$message.success('删除成功');
          this.getFamilyList(this.currentElderForFamily.elderId);
        }).catch(() => {
          this.$message.error('删除失败');
        });
      }).catch(() => {});
    },
    /** 提交家属表单 */
    submitFamilyForm() {
      this.$refs.familyForm.validate(valid => {
        if (valid) {
          if (this.familyFormMode === 'add') {
            addFamily(this.familyForm).then(response => {
              this.$message.success('添加成功');
              this.familyFormOpen = false;
              this.getFamilyList(this.currentElderForFamily.elderId);
            }).catch(() => {
              this.$message.error('添加失败');
            });
          } else {
            updateFamily(this.familyForm).then(response => {
              this.$message.success('修改成功');
              this.familyFormOpen = false;
              this.getFamilyList(this.currentElderForFamily.elderId);
            }).catch(() => {
              this.$message.error('修改失败');
            });
          }
        }
      });
    },
    /** 重置家属表单 */
    resetFamilyForm() {
      this.familyForm = {
        familyId: null,
        elderId: null,
        phonenumber: null,
        relationType: '1',
        relationName: null,
        isDefault: '0',
        isMainContact: '0',
        remark: null
      };
      if (this.$refs.familyForm) {
        this.$refs.familyForm.resetFields();
      }
    },
    /** 设置密码 */
    handleSetPassword(row) {
      this.passwordForm = {
        elderId: row.elderId,
        elderName: row.elderName,
        idCard: row.idCard,
        password: null,
        confirmPassword: null
      };
      this.passwordDialogOpen = true;
      this.$nextTick(() => {
        if (this.$refs.passwordForm) {
          this.$refs.passwordForm.clearValidate();
        }
      });
    },
    /** 提交密码设置 */
    submitPasswordForm() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          // TODO: 调用后端API设置密码
          this.$axios.post('/elder/info/setPassword', {
            elderId: this.passwordForm.elderId,
            password: this.passwordForm.password
          }).then(response => {
            this.$message.success('密码设置成功');
            this.passwordDialogOpen = false;
          }).catch(error => {
            this.$message.error(error.message || '密码设置失败');
          });
        }
      });
    }
  }
};
</script>