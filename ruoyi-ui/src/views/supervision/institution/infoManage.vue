<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-primary">
              <i class="el-icon-office-building"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">入驻机构总数</div>
              <div class="stat-value">{{ statistics.totalInstitutions }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-success">
              <i class="el-icon-circle-check"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">正常运营</div>
              <div class="stat-value">{{ statistics.normalCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-warning">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">预警机构</div>
              <div class="stat-value">{{ statistics.warningCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-danger">
              <i class="el-icon-close"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">已停用</div>
              <div class="stat-value">{{ statistics.disabledCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 查询表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="88px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="机构类型" prop="institutionType">
        <el-select v-model="queryParams.institutionType" placeholder="请选择机构类型" clearable style="width: 150px">
          <el-option label="民办" value="1" />
          <el-option label="公办" value="2" />
          <el-option label="公建民营" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="运营状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择运营状态" clearable style="width: 150px">
          <el-option label="正常" value="1" />
          <el-option label="预警" value="2" />
          <el-option label="已停用" value="3" />
        </el-select>
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
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="institutionList" border>
      <el-table-column label="机构编号" align="center" prop="institutionId" width="100" />
      <el-table-column label="机构名称" align="center" prop="institutionName" width="200" show-overflow-tooltip />
      <el-table-column label="机构类型" align="center" prop="institutionType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.institutionType === '1'" type="primary">民办</el-tag>
          <el-tag v-else-if="scope.row.institutionType === '2'" type="success">公办</el-tag>
          <el-tag v-else type="warning">公建民营</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="统一信用代码" align="center" prop="creditCode" width="180" />
      <el-table-column label="床位数" align="center" prop="bedCount" width="80">
        <template slot-scope="scope">
          <span style="color: #409EFF; font-weight: bold">{{ scope.row.bedCount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="在住老人" align="center" prop="elderCount" width="80">
        <template slot-scope="scope">
          <el-link type="primary" @click="handleViewElders(scope.row)">{{ scope.row.elderCount }}人</el-link>
        </template>
      </el-table-column>
      <el-table-column label="床位使用率" align="center" prop="occupancyRate" width="100">
        <template slot-scope="scope">
          <el-progress :percentage="scope.row.occupancyRate" :color="getOccupancyColor(scope.row.occupancyRate)" />
        </template>
      </el-table-column>
      <el-table-column label="账户余额" align="center" prop="accountBalance" width="120">
        <template slot-scope="scope">
          <span style="color: #67C23A; font-weight: bold">¥{{ scope.row.accountBalance }}</span>
        </template>
      </el-table-column>
      <el-table-column label="运营状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '1'" type="success">正常</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="warning">预警</el-tag>
          <el-tag v-else type="danger">已停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="联系人" align="center" prop="contactPerson" width="100" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="120" />
      <el-table-column label="入驻日期" align="center" prop="joinDate" width="110" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="260" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)">
            <el-button size="mini" type="text">
              更多<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="account" icon="el-icon-bank-card">账户信息</el-dropdown-item>
              <el-dropdown-item command="elders" icon="el-icon-user">在住老人</el-dropdown-item>
              <el-dropdown-item command="freeze" icon="el-icon-lock" v-if="scope.row.status === '1'">冻结</el-dropdown-item>
              <el-dropdown-item command="unfreeze" icon="el-icon-unlock" v-if="scope.row.status === '3'">启用</el-dropdown-item>
              <el-dropdown-item command="history" icon="el-icon-time">操作记录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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

    <!-- 详情对话框 -->
    <el-dialog title="机构详细信息" :visible.sync="detailOpen" width="1000px" append-to-body>
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="机构编号">{{ detailData.institutionId }}</el-descriptions-item>
            <el-descriptions-item label="机构名称">{{ detailData.institutionName }}</el-descriptions-item>
            <el-descriptions-item label="机构类型">
              <el-tag v-if="detailData.institutionType === '1'" type="primary">民办</el-tag>
              <el-tag v-else-if="detailData.institutionType === '2'" type="success">公办</el-tag>
              <el-tag v-else type="warning">公建民营</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="运营状态">
              <el-tag v-if="detailData.status === '1'" type="success">正常</el-tag>
              <el-tag v-else-if="detailData.status === '2'" type="warning">预警</el-tag>
              <el-tag v-else type="danger">已停用</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="统一信用代码">{{ detailData.creditCode }}</el-descriptions-item>
            <el-descriptions-item label="机构备案号">{{ detailData.registrationNo }}</el-descriptions-item>
            <el-descriptions-item label="注册资金">{{ detailData.registeredCapital }}万元</el-descriptions-item>
            <el-descriptions-item label="成立日期">{{ detailData.establishDate }}</el-descriptions-item>
            <el-descriptions-item label="注册地址" :span="2">{{ detailData.registeredAddress }}</el-descriptions-item>
            <el-descriptions-item label="经营地址" :span="2">{{ detailData.businessAddress }}</el-descriptions-item>
            <el-descriptions-item label="法定代表人">{{ detailData.legalPerson }}</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{ detailData.idCard }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ detailData.contactPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="经营信息" name="business">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="床位总数">{{ detailData.bedCount }}张</el-descriptions-item>
            <el-descriptions-item label="已用床位">{{ detailData.usedBeds }}张</el-descriptions-item>
            <el-descriptions-item label="在住老人">{{ detailData.elderCount }}人</el-descriptions-item>
            <el-descriptions-item label="床位使用率">{{ detailData.occupancyRate }}%</el-descriptions-item>
            <el-descriptions-item label="收费区间">{{ detailData.feeRange }}</el-descriptions-item>
            <el-descriptions-item label="固定资产净额">{{ detailData.fixedAssets }}万元</el-descriptions-item>
            <el-descriptions-item label="监管账户">{{ detailData.supervisionAccount }}</el-descriptions-item>
            <el-descriptions-item label="账户余额">¥{{ detailData.accountBalance }}</el-descriptions-item>
            <el-descriptions-item label="基本结算账户">{{ detailData.basicAccount }}</el-descriptions-item>
            <el-descriptions-item label="开户银行">{{ detailData.bankName }}</el-descriptions-item>
            <el-descriptions-item label="入驻日期">{{ detailData.joinDate }}</el-descriptions-item>
            <el-descriptions-item label="签约日期">{{ detailData.contractDate }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="附件材料" name="attachments">
          <el-table :data="attachmentList" border>
            <el-table-column label="材料名称" align="center" prop="fileName" />
            <el-table-column label="材料类型" align="center" prop="fileType" width="120">
              <template slot-scope="scope">
                <el-tag size="small">{{ scope.row.fileType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="上传时间" align="center" prop="uploadTime" width="160" />
            <el-table-column label="操作" align="center" width="150">
              <template slot-scope="scope">
                <el-button size="mini" type="text" icon="el-icon-view" @click="handlePreview(scope.row)">预览</el-button>
                <el-button size="mini" type="text" icon="el-icon-download" @click="handleDownload(scope.row)">下载</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog :title="title" :visible.sync="editOpen" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="机构名称" prop="institutionName">
              <el-input v-model="form.institutionName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机构类型" prop="institutionType">
              <el-select v-model="form.institutionType" placeholder="请选择机构类型" style="width: 100%">
                <el-option label="民办" value="1" />
                <el-option label="公办" value="2" />
                <el-option label="公建民营" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="床位总数" prop="bedCount">
              <el-input-number v-model="form.bedCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费区间" prop="feeRange">
              <el-input v-model="form.feeRange" placeholder="如：2000-8000元/月" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="经营地址" prop="businessAddress">
          <el-input v-model="form.businessAddress" placeholder="请输入实际经营地址" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 在住老人列表对话框 -->
    <el-dialog title="在住老人列表" :visible.sync="eldersOpen" width="1000px" append-to-body>
      <div style="margin-bottom: 15px">
        <el-tag type="primary">机构：{{ currentInstitutionName }}</el-tag>
        <el-tag type="success" style="margin-left: 10px">在住老人：{{ eldersList.length }}人</el-tag>
      </div>
      <el-table :data="eldersList" border>
        <el-table-column label="老人姓名" align="center" prop="elderName" width="100" />
        <el-table-column label="身份证号" align="center" prop="idCard" width="180" />
        <el-table-column label="性别" align="center" prop="gender" width="60">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.gender === '1'" type="primary" size="small">男</el-tag>
            <el-tag v-else type="danger" size="small">女</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="年龄" align="center" prop="age" width="60" />
        <el-table-column label="入住床位" align="center" prop="bedNo" width="120" />
        <el-table-column label="入住日期" align="center" prop="checkInDate" width="110" />
        <el-table-column label="账户余额" align="center" prop="accountBalance" width="120">
          <template slot-scope="scope">
            <span style="color: #67C23A; font-weight: bold">¥{{ scope.row.accountBalance }}</span>
          </template>
        </el-table-column>
        <el-table-column label="联系人" align="center" prop="contactPerson" width="100" />
        <el-table-column label="联系电话" align="center" prop="contactPhone" width="120" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="eldersOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'InstitutionInfoManage',
  data() {
    return {
      // 统计数据
      statistics: {
        totalInstitutions: 156,
        normalCount: 128,
        warningCount: 18,
        disabledCount: 10
      },
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 机构列表
      institutionList: [],
      // 附件列表
      attachmentList: [],
      // 在住老人列表
      eldersList: [],
      // 当前机构名称
      currentInstitutionName: '',
      // 弹出层标题
      title: '',
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示编辑弹出层
      editOpen: false,
      // 是否显示老人列表弹出层
      eldersOpen: false,
      // 当前选项卡
      activeTab: 'basic',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        institutionType: null,
        status: null
      },
      // 表单参数
      form: {},
      // 详情数据
      detailData: {},
      // 表单校验
      rules: {
        institutionType: [
          { required: true, message: '机构类型不能为空', trigger: 'change' }
        ],
        bedCount: [
          { required: true, message: '床位总数不能为空', trigger: 'blur' }
        ],
        contactPerson: [
          { required: true, message: '联系人不能为空', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '联系电话不能为空', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询机构列表 */
    getList() {
      this.loading = true
      // 模拟数据
      setTimeout(() => {
        this.institutionList = [
          {
            institutionId: 'I10001',
            institutionName: '北京市朝阳区幸福养老院',
            institutionType: '1',
            creditCode: '91110105MA01234567',
            registrationNo: '京民养备20240001',
            registeredCapital: 500,
            establishDate: '2020-01-01',
            registeredAddress: '北京市朝阳区幸福街道123号',
            businessAddress: '北京市朝阳区幸福街道123号',
            legalPerson: '张三',
            idCard: '110101196001011234',
            contactPerson: '李四',
            contactPhone: '13800138001',
            bedCount: 200,
            usedBeds: 156,
            elderCount: 156,
            occupancyRate: 78,
            feeRange: '2000-8000元/月',
            fixedAssets: 1000,
            supervisionAccount: '622202XXXXXXXXXX1234',
            accountBalance: 2856900,
            basicAccount: '622202XXXXXXXXXX5678',
            bankName: '中国工商银行朝阳支行',
            status: '1',
            joinDate: '2024-01-15',
            contractDate: '2024-01-20',
            remark: '正常运营'
          },
          {
            institutionId: 'I10002',
            institutionName: '上海市浦东新区康乐养老中心',
            institutionType: '3',
            creditCode: '91310115MA02345678',
            registrationNo: '沪民养备20240002',
            registeredCapital: 800,
            establishDate: '2019-06-15',
            registeredAddress: '上海市浦东新区康乐路456号',
            businessAddress: '上海市浦东新区康乐路456号',
            legalPerson: '王五',
            idCard: '310101197001011234',
            contactPerson: '赵六',
            contactPhone: '13800138002',
            bedCount: 300,
            usedBeds: 285,
            elderCount: 285,
            occupancyRate: 95,
            feeRange: '3000-10000元/月',
            fixedAssets: 2000,
            supervisionAccount: '622202XXXXXXXXXX2345',
            accountBalance: 4562000,
            basicAccount: '622202XXXXXXXXXX6789',
            bankName: '中国建设银行浦东支行',
            status: '2',
            joinDate: '2024-02-20',
            contractDate: '2024-02-25',
            remark: '床位紧张预警'
          },
          {
            institutionId: 'I10003',
            institutionName: '广州市天河区爱心养老服务中心',
            institutionType: '2',
            creditCode: '91440106MA03456789',
            registrationNo: '粤民养备20240003',
            registeredCapital: 1000,
            establishDate: '2018-03-20',
            registeredAddress: '广州市天河区爱心路789号',
            businessAddress: '广州市天河区爱心路789号',
            legalPerson: '李明',
            idCard: '440101196501011234',
            contactPerson: '张华',
            contactPhone: '13800138003',
            bedCount: 150,
            usedBeds: 98,
            elderCount: 98,
            occupancyRate: 65,
            feeRange: '1500-6000元/月',
            fixedAssets: 800,
            supervisionAccount: '622202XXXXXXXXXX3456',
            accountBalance: 1982000,
            basicAccount: '622202XXXXXXXXXX7890',
            bankName: '中国农业银行天河支行',
            status: '1',
            joinDate: '2024-03-10',
            contractDate: '2024-03-15',
            remark: '公办养老机构'
          }
        ]
        this.total = this.institutionList.length
        this.loading = false
      }, 500)
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.detailData = { ...row }
      // 模拟附件数据
      this.attachmentList = [
        {
          fileName: '营业执照.pdf',
          fileType: 'PDF',
          uploadTime: '2024-01-10 10:30:00'
        },
        {
          fileName: '社会福利机构设置批准证书.pdf',
          fileType: 'PDF',
          uploadTime: '2024-01-10 10:31:00'
        },
        {
          fileName: '三方监管协议.pdf',
          fileType: 'PDF',
          uploadTime: '2024-01-20 14:20:00'
        }
      ]
      this.detailOpen = true
      this.activeTab = 'basic'
    },
    /** 编辑按钮操作 */
    handleEdit(row) {
      this.form = { ...row }
      this.editOpen = true
      this.title = '编辑机构信息'
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          this.$modal.msgSuccess('修改成功')
          this.editOpen = false
          this.getList()
        }
      })
    },
    /** 取消按钮 */
    cancel() {
      this.editOpen = false
    },
    /** 查看在住老人 */
    handleViewElders(row) {
      this.currentInstitutionName = row.institutionName
      // 模拟数据
      this.eldersList = [
        {
          elderName: '王大爷',
          idCard: '110101195001011234',
          gender: '1',
          age: 74,
          bedNo: 'A区101-1',
          checkInDate: '2024-03-15',
          accountBalance: 58600,
          contactPerson: '王小明',
          contactPhone: '13900139001'
        },
        {
          elderName: '李奶奶',
          idCard: '110101195203021234',
          gender: '2',
          age: 72,
          bedNo: 'A区102-2',
          checkInDate: '2024-04-20',
          accountBalance: 62000,
          contactPerson: '李小华',
          contactPhone: '13900139002'
        }
      ]
      this.eldersOpen = true
    },
    /** 更多操作 */
    handleCommand(command, row) {
      switch (command) {
        case 'account':
          this.$router.push({ path: '/supervision/account/institutionAccount', query: { institutionId: row.institutionId } })
          break
        case 'elders':
          this.handleViewElders(row)
          break
        case 'freeze':
          this.$modal.confirm('是否确认停用"' + row.institutionName + '"？停用后该机构将无法继续接收新老人入住。').then(() => {
            this.$modal.msgSuccess('停用成功')
            this.getList()
          })
          break
        case 'unfreeze':
          this.$modal.confirm('是否确认启用"' + row.institutionName + '"？').then(() => {
            this.$modal.msgSuccess('启用成功')
            this.getList()
          })
          break
        case 'history':
          this.$modal.msgSuccess('操作记录功能开发中')
          break
      }
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.msgSuccess('导出功能开发中')
    },
    /** 获取床位使用率颜色 */
    getOccupancyColor(rate) {
      if (rate >= 90) {
        return '#F56C6C'
      } else if (rate >= 70) {
        return '#E6A23C'
      } else {
        return '#67C23A'
      }
    },
    /** 预览附件 */
    handlePreview(row) {
      this.$modal.msgSuccess('预览功能开发中')
    },
    /** 下载附件 */
    handleDownload(row) {
      this.$modal.msgSuccess('下载功能开发中')
    }
  }
}
</script>

<style scoped>
.mb20 {
  margin-bottom: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon i {
  font-size: 30px;
  color: #fff;
}

.bg-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.bg-success {
  background: linear-gradient(135deg, #56ab2f 0%, #a8e063 100%);
}

.bg-warning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.bg-danger {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
</style>
