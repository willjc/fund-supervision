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
      <el-form-item label="入住状态" prop="checkInStatus">
        <el-select v-model="queryParams.checkInStatus" placeholder="请选择入住状态" clearable>
          <el-option
            v-for="dict in dict.type.check_in_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
      <el-form-item label="审批人" prop="approvalUser">
        <el-input
          v-model="queryParams.approvalUser"
          placeholder="请输入审批人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
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
          v-hasPermi="['elder:checkin:add']"
        >新增申请</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['elder:checkin:edit']"
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
          v-hasPermi="['elder:checkin:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['elder:checkin:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="checkInList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="老人姓名" align="center" prop="elderName" />
      <el-table-column label="机构名称" align="center" prop="institutionName" />
      <el-table-column label="房间号" align="center" prop="roomNumber" />
      <el-table-column label="床位号" align="center" prop="bedNumber" />
      <el-table-column label="入住状态" align="center" prop="checkInStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.check_in_status" :value="scope.row.checkInStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="护理等级" align="center" prop="careLevel">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.elder_care_level" :value="scope.row.careLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="月费用" align="center" prop="monthlyFee">
        <template slot-scope="scope">
          <span>￥{{ scope.row.monthlyFee }}</span>
        </template>
      </el-table-column>
      <el-table-column label="押金" align="center" prop="depositAmount">
        <template slot-scope="scope">
          <span>￥{{ scope.row.depositAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="期望入住日期" align="center" prop="expectedCheckInDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expectedCheckInDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实际入住日期" align="center" prop="actualCheckInDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.actualCheckInDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="申请日期" align="center" prop="applyDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审批人" align="center" prop="approvalUser" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['elder:checkin:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['elder:checkin:remove']"
          >删除</el-button>
          <el-button
            v-if="scope.row.checkInStatus === '0'"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleApprove(scope.row)"
            v-hasPermi="['elder:checkin:approve']"
          >审批</el-button>
          <el-button
            v-if="scope.row.checkInStatus === '1'"
            size="mini"
            type="text"
            icon="el-icon-document-add"
            @click="handleGenerateOrder(scope.row)"
            v-hasPermi="['order:info:add']"
          >生成订单</el-button>
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

    <!-- 添加或修改老人入住申请对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="选择老人" prop="elderId">
              <el-select v-model="form.elderId" placeholder="请选择老人" filterable clearable style="width: 100%">
                <el-option
                  v-for="elder in elderList"
                  :key="elder.elderId"
                  :label="`${elder.elderName} - ${elder.idCard}`"
                  :value="elder.elderId"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选择机构" prop="institutionId">
              <el-select v-model="form.institutionId" placeholder="请选择机构" filterable clearable style="width: 100%" @change="handleInstitutionChange">
                <el-option
                  v-for="institution in institutionList"
                  :key="institution.institutionId"
                  :label="`${institution.institutionName}`"
                  :value="institution.institutionId"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="选择床位" prop="bedId">
              <el-select v-model="form.bedId" placeholder="请选择床位" filterable clearable style="width: 100%">
                <el-option
                  v-for="bed in bedList"
                  :key="bed.bedId"
                  :label="`${bed.roomNumber}-${bed.bedNumber} (${bed.bedType})`"
                  :value="bed.bedId"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
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
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="期望入住日期" prop="expectedCheckInDate">
              <el-date-picker clearable
                v-model="form.expectedCheckInDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择期望入住日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入住状态" prop="checkInStatus">
              <el-select v-model="form.checkInStatus" placeholder="请选择入住状态">
                <el-option
                  v-for="dict in dict.type.check_in_status"
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
            <el-form-item label="月费用" prop="monthlyFee">
              <el-input v-model="form.monthlyFee" placeholder="请输入月费用" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="押金金额" prop="depositAmount">
              <el-input v-model="form.depositAmount" placeholder="请输入押金金额" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="支付方式" prop="paymentMethod">
              <el-input v-model="form.paymentMethod" placeholder="请输入支付方式" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面谈时间" prop="interviewDate">
              <el-date-picker clearable
                v-model="form.interviewDate"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择面谈时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="面谈结果" prop="interviewResult">
          <el-input v-model="form.interviewResult" type="textarea" placeholder="请输入面谈结果" />
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

    <!-- 审批对话框 -->
    <el-dialog title="审批入住申请" :visible.sync="approveOpen" width="500px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" :rules="approveRules" label-width="80px">
        <el-form-item label="审批结果" prop="checkInStatus">
          <el-radio-group v-model="approveForm.checkInStatus">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="approvalRemark">
          <el-input v-model="approveForm.approvalRemark" type="textarea" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApprove">确 定</el-button>
        <el-button @click="cancelApprove">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCheckIn, getCheckIn, delCheckIn, addCheckIn, updateCheckIn, approveCheckIn } from "@/api/elder/checkin";
import { listElder } from "@/api/elder/info";
import { listInstitution } from "@/api/pension/institution";
import { listBed, listAvailableBeds } from "@/api/elder/bed";
import { generateOrderByCheckIn } from "@/api/order/orderInfo";

export default {
  name: "ElderCheckIn",
  dicts: ['check_in_status', 'elder_care_level'],
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
      // 老人入住申请表格数据
      checkInList: [],
      // 老人列表
      elderList: [],
      // 机构列表
      institutionList: [],
      // 床位列表
      bedList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示审批弹出层
      approveOpen: false,
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        elderName: null,
        checkInStatus: null,
        careLevel: null,
        approvalUser: null,
      },
      // 表单参数
      form: {},
      // 审批表单参数
      approveForm: {
        checkInId: null,
        checkInStatus: "1",
        approvalRemark: null
      },
      // 表单校验
      rules: {
        elderId: [
          { required: true, message: "老人ID不能为空", trigger: "blur" }
        ],
        institutionId: [
          { required: true, message: "机构ID不能为空", trigger: "blur" }
        ],
        expectedCheckInDate: [
          { required: true, message: "期望入住日期不能为空", trigger: "blur" }
        ],
        careLevel: [
          { required: true, message: "护理等级不能为空", trigger: "change" }
        ],
        monthlyFee: [
          { required: true, message: "月费用不能为空", trigger: "blur" },
          { pattern: /^\d+(\.\d{1,2})?$/, message: "请输入正确的月费用", trigger: "blur" }
        ],
        depositAmount: [
          { required: true, message: "押金金额不能为空", trigger: "blur" },
          { pattern: /^\d+(\.\d{1,2})?$/, message: "请输入正确的押金金额", trigger: "blur" }
        ]
      },
      // 审批表单校验
      approveRules: {
        checkInStatus: [
          { required: true, message: "审批结果不能为空", trigger: "change" }
        ],
        approvalRemark: [
          { required: true, message: "审批意见不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    // 获取路由参数中的老人信息
    const { elderId, elderName } = this.$route.query;
    if (elderId) {
      this.queryParams.elderId = elderId;
      this.queryParams.elderName = elderName;
    }
    this.getList();
    this.loadElderList();
    this.loadInstitutionList();
  },
  methods: {
    /** 查询老人入住申请列表 */
    getList() {
      this.loading = true;
      const params = this.addDateRange(this.queryParams, this.dateRange);
      listCheckIn(params).then(response => {
        this.checkInList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 取消审批按钮
    cancelApprove() {
      this.approveOpen = false;
      this.resetApprove();
    },
    // 表单重置
    reset() {
      this.form = {
        checkInId: null,
        elderId: null,
        institutionId: null,
        bedId: null,
        applyDate: null,
        expectedCheckInDate: null,
        actualCheckInDate: null,
        checkInStatus: "0",
        careLevel: "1",
        monthlyFee: null,
        depositAmount: null,
        paymentMethod: null,
        interviewDate: null,
        interviewResult: null,
        approvalUser: null,
        approvalTime: null,
        approvalRemark: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      };
      this.resetForm("form");
    },
    // 审批表单重置
    resetApprove() {
      this.approveForm = {
        checkInId: null,
        checkInStatus: "1",
        approvalRemark: null
      };
      this.resetForm("approveForm");
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
      this.ids = selection.map(item => item.checkInId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      // 从路由参数中获取老人ID
      const { elderId } = this.$route.query;
      if (elderId) {
        this.form.elderId = parseInt(elderId);
      }
      this.open = true;
      this.title = "添加老人入住申请";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const checkInId = row.checkInId || this.ids
      getCheckIn(checkInId).then(response => {
        this.form = response.data;
        // 加载该机构的床位列表
        if (this.form.institutionId) {
          this.loadBedList(this.form.institutionId);
        }
        this.open = true;
        this.title = "修改老人入住申请";
      });
    },
    /** 审批按钮操作 */
    handleApprove(row) {
      this.resetApprove();
      const checkIn = row.checkInId || this.ids[0];
      this.approveForm.checkInId = checkIn;
      this.approveOpen = true;
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.checkInId != null) {
            updateCheckIn(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCheckIn(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 提交审批按钮 */
    submitApprove() {
      this.$refs["approveForm"].validate(valid => {
        if (valid) {
          approveCheckIn(this.approveForm).then(response => {
            this.$modal.msgSuccess("审批成功");
            this.approveOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const checkInIds = row.checkInId || this.ids;
      this.$modal.confirm('是否确认删除老人入住申请编号为"' + checkInIds + '"的数据项？').then(function() {
        return delCheckIn(checkInIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('elder/checkin/export', {
        ...this.queryParams
      }, `check_in_${new Date().getTime()}.xlsx`)
    },
    /** 加载老人列表 */
    loadElderList() {
      listElder({pageNum: 1, pageSize: 1000}).then(response => {
        this.elderList = response.rows || [];
      });
    },
    /** 加载机构列表 */
    loadInstitutionList() {
      listInstitution({pageNum: 1, pageSize: 1000, status: '1'}).then(response => {
        this.institutionList = response.rows || [];
      });
    },
    /** 加载床位列表 */
    loadBedList(institutionId) {
      if (!institutionId) {
        this.bedList = [];
        return;
      }
      listAvailableBeds(institutionId).then(response => {
        this.bedList = response.data || [];
      });
    },
    /** 机构变更时加载床位 */
    handleInstitutionChange(institutionId) {
      this.form.bedId = null;
      this.loadBedList(institutionId);
    },
    /** 生成订单按钮操作 */
    handleGenerateOrder(row) {
      const checkInId = row.checkInId;
      this.$modal.confirm('是否确认为"' + row.elderName + '"生成订单？').then(() => {
        return generateOrderByCheckIn(checkInId);
      }).then(() => {
        this.$modal.msgSuccess("订单生成成功");
        this.getList();
      }).catch(() => {});
    }
  }
};
</script>