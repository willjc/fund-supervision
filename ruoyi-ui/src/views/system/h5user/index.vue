<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="姓名" prop="nickName">
        <el-input
          v-model="queryParams.nickName"
          placeholder="请输入姓名"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号码" prop="phonenumber">
        <el-input
          v-model="queryParams.phonenumber"
          placeholder="请输入手机号码"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="用户状态" clearable style="width: 200px">
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
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
        />
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
          v-hasPermi="['h5user:add']"
          v-if="false"
        >新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="用户编号" align="center" prop="userId" width="80" />
      <el-table-column label="头像" align="center" width="80">
        <template slot-scope="scope">
          <el-image
            v-if="scope.row.avatar"
            :src="getAvatarUrl(scope.row.avatar)"
            :preview-src-list="[getAvatarUrl(scope.row.avatar)]"
            style="width: 40px; height: 40px; border-radius: 50%;"
            fit="cover"
          />
          <el-avatar v-else icon="el-icon-user" :size="40"></el-avatar>
        </template>
      </el-table-column>
      <el-table-column label="昵称" align="center" prop="nickName" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="真实姓名" align="center" prop="realName" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
      <el-table-column label="关联老人" align="center" prop="elderList" width="200" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span v-if="scope.row.elderList && scope.row.elderList.length > 0">
            <span v-for="(elder, index) in scope.row.elderList" :key="elder.elderId">
              {{ elder.elderName }}({{ elder.relationName }})<span v-if="index < scope.row.elderList.length - 1">、</span>
            </span>
          </span>
          <span v-else style="color: #909399;">未关联</span>
        </template>
      </el-table-column>
      <el-table-column label="性别" align="center" prop="sex" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_user_sex" :value="scope.row.sex"/>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="最后登录IP" align="center" prop="loginIp" width="130" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="260" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-info"
            @click="handleDetail(scope.row)"
            v-hasPermi="['h5user:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['h5user:edit']"
            v-if="false"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-key"
            @click="handleResetPwd(scope.row)"
            v-hasPermi="['h5user:resetPwd']"
            v-if="false"
          >重置密码</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['h5user:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改用户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="家属姓名" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入家属姓名" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" :disabled="form.userId != null" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="家属性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option
                  v-for="dict in dict.type.sys_user_sex"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.userId == null">
          <el-col :span="24">
            <el-alert
              title="提示"
              type="info"
              :closable="false"
              style="margin-bottom: 10px;"
            >
              新增用户后系统将自动生成6位数字密码，请在保存后查看。
            </el-alert>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 新增成功显示密码对话框 -->
    <el-dialog title="用户创建成功" :visible.sync="pwdDialogOpen" width="500px" append-to-body :close-on-click-modal="false">
      <el-result icon="success" title="用户创建成功" sub-title="请保存以下密码信息">
        <template slot="extra">
          <el-card shadow="never" style="background: #f0f9ff;">
            <div style="text-align: center;">
              <div style="margin-bottom: 10px; color: #606266;">
                <i class="el-icon-mobile-phone"></i> 手机号
              </div>
              <div style="font-size: 18px; font-weight: bold; color: #303133; margin-bottom: 15px;">
                {{ createdUser.phonenumber }}
              </div>
              <div style="margin-bottom: 10px; color: #606266;">
                <i class="el-icon-key"></i> 初始密码
              </div>
              <div style="font-size: 24px; font-weight: bold; color: #409EFF; letter-spacing: 5px;">
                {{ createdUser.password }}
              </div>
            </div>
          </el-card>
        </template>
      </el-result>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="pwdDialogOpen = false">我知道了</el-button>
      </div>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog title="重置密码" :visible.sync="resetPwdOpen" width="400px" append-to-body>
      <el-form ref="resetPwdForm" :model="resetPwdForm" label-width="80px">
        <el-form-item label="用户名称">
          <el-input v-model="resetPwdForm.nickName" disabled />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="resetPwdForm.phonenumber" disabled />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="resetPwdOpen = false">取 消</el-button>
        <el-button type="primary" @click="submitResetPwd">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 用户详情对话框 -->
    <el-dialog title="用户详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="头像" :span="2">
          <el-image
            v-if="userInfo.avatar"
            :src="getAvatarUrl(userInfo.avatar)"
            :preview-src-list="[getAvatarUrl(userInfo.avatar)]"
            style="width: 80px; height: 80px; border-radius: 50%;"
            fit="cover"
          />
          <el-avatar v-else icon="el-icon-user" :size="80"></el-avatar>
        </el-descriptions-item>
        <el-descriptions-item label="用户编号">{{ userInfo.userId }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userInfo.nickName || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ userInfo.realName || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="手机号码">{{ userInfo.phonenumber }}</el-descriptions-item>
        <el-descriptions-item label="家属性别">
          <dict-tag :options="dict.type.sys_user_sex" :value="userInfo.sex"/>
        </el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <dict-tag :options="dict.type.sys_normal_disable" :value="userInfo.status"/>
        </el-descriptions-item>
        <el-descriptions-item label="最后登录IP">{{ userInfo.loginIp || '未登录' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(userInfo.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="关联老人数">{{ userInfo.elderList ? userInfo.elderList.length : 0 }}人</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">关联老人信息</el-divider>

      <el-table :data="userInfo.elderList" style="width: 100%">
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column label="老人姓名" align="center" prop="elderName" width="100" />
        <el-table-column label="关系" align="center" prop="relationName" width="100" />
        <el-table-column label="身份证号" align="center" prop="idCard" width="180" :show-overflow-tooltip="true" />
        <el-table-column label="年龄" align="center" prop="age" width="80" />
        <el-table-column label="性别" align="center" prop="gender" width="80">
          <template slot-scope="scope">
            {{ scope.row.gender === '1' ? '男' : scope.row.gender === '2' ? '女' : '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="联系电话" align="center" prop="phone" width="120" />
        <el-table-column label="入住状态" align="center" prop="status" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '0'" type="info">未入住</el-tag>
            <el-tag v-else-if="scope.row.status === '1'" type="success">已入住</el-tag>
            <el-tag v-else type="danger">已退住</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listH5User, getH5User, addH5User, updateH5User, delH5User, resetH5UserPwd } from "@/api/h5user";

export default {
  name: "H5User",
  dicts: ['sys_normal_disable', 'sys_user_sex'],
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
      // 用户表格数据
      userList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示密码对话框
      pwdDialogOpen: false,
      // 是否显示重置密码对话框
      resetPwdOpen: false,
      // 是否显示详情对话框
      detailOpen: false,
      // 用户详情信息
      userInfo: {
        userId: null,
        nickName: null,
        phonenumber: null,
        sex: null,
        status: null,
        loginIp: null,
        createTime: null,
        elderList: []
      },
      // 创建的用户信息
      createdUser: {
        phonenumber: null,
        password: null
      },
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nickName: null,
        phonenumber: null,
        status: null
      },
      // 表单参数
      form: {},
      // 重置密码表单
      resetPwdForm: {
        userId: null,
        nickName: null,
        phonenumber: null
      },
      // 表单校验
      rules: {
        nickName: [
          { required: true, message: "家属姓名不能为空", trigger: "blur" }
        ],
        phonenumber: [
          { required: true, message: "手机号码不能为空", trigger: "blur" },
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询H5用户列表 */
    getList() {
      this.loading = true;
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.params = {
          beginTime: this.dateRange[0],
          endTime: this.dateRange[1]
        };
      }
      listH5User(this.queryParams).then(response => {
        this.userList = response.rows;
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
        userId: null,
        nickName: null,
        phonenumber: null,
        sex: "0",
        status: "0",
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
      this.ids = selection.map(item => item.userId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      const userId = row.userId;
      getH5User(userId).then(response => {
        console.log('详情接口返回:', response);
        if (response && response.user) {
          this.userInfo = {
            userId: response.user.userId,
            nickName: response.user.nickName,
            realName: response.user.realName,
            phonenumber: response.user.phonenumber,
            sex: response.user.sex,
            avatar: response.user.avatar,
            status: response.user.status,
            loginIp: response.user.loginIp,
            createTime: response.user.createTime,
            elderList: response.user.elderList || []
          };
          this.detailOpen = true;
        } else {
          this.$modal.msgError('获取用户详情失败');
        }
      }).catch(error => {
        console.error('获取用户详情错误:', error);
        this.$modal.msgError('获取用户详情失败');
      });
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加H5用户";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const userId = row.userId || this.ids;
      getH5User(userId).then(response => {
        this.form = response.user;
        this.open = true;
        this.title = "修改H5用户";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.userId != null) {
            updateH5User(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addH5User(this.form).then(response => {
              if (response.code === 200) {
                this.$modal.msgSuccess("新增成功");
                this.createdUser = {
                  phonenumber: this.form.phonenumber,
                  password: response.data.password
                };
                this.open = false;
                this.pwdDialogOpen = true;
                this.getList();
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const userIds = row.userId || this.ids;
      this.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？').then(function() {
        return delH5User(userIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 重置密码按钮操作 */
    handleResetPwd(row) {
      this.resetPwdForm = {
        userId: row.userId,
        nickName: row.nickName,
        phonenumber: row.phonenumber
      };
      this.resetPwdOpen = true;
    },
    /** 提交重置密码 */
    submitResetPwd() {
      this.$modal.confirm('确认要重置该用户的密码吗？重置后将生成新的6位数字密码。').then(() => {
        return resetH5UserPwd(this.resetPwdForm);
      }).then(response => {
        if (response.code === 200) {
          this.$modal.msgSuccess("重置成功，新密码为：" + response.data.password);
          this.resetPwdOpen = false;
        }
      }).catch(() => {});
    },
    /** 获取头像URL */
    getAvatarUrl(avatar) {
      if (!avatar) return '';
      // 如果已经是完整URL，直接返回
      if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
        return avatar;
      }
      // 拼接开发环境API地址
      return process.env.VUE_APP_BASE_API + avatar;
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

/* 确保表格撑满容器宽度 */
.el-table {
  width: 100% !important;
}
</style>
