<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="房间号" prop="roomNumber">
        <el-input
          v-model="queryParams.roomNumber"
          placeholder="请输入房间号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="床位号" prop="bedNumber">
        <el-input
          v-model="queryParams.bedNumber"
          placeholder="请输入床位号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="床位类型" prop="bedType">
        <el-select v-model="queryParams.bedType" placeholder="请选择床位类型" clearable>
          <el-option
            v-for="dict in dict.type.bed_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="床位状态" prop="bedStatus">
        <el-select v-model="queryParams.bedStatus" placeholder="请选择床位状态" clearable>
          <el-option
            v-for="dict in dict.type.bed_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="楼层" prop="floorNumber">
        <el-input
          v-model="queryParams.floorNumber"
          placeholder="请输入楼层"
          clearable
          @keyup.enter.native="handleQuery"
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
          v-hasPermi="['elder:bed:add']"
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
          v-hasPermi="['elder:bed:edit']"
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
          v-hasPermi="['elder:bed:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['elder:bed:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bedInfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="机构名称" align="center" prop="institutionName" />
      <el-table-column label="房间号" align="center" prop="roomNumber" />
      <el-table-column label="床位号" align="center" prop="bedNumber" />
      <el-table-column label="床位类型" align="center" prop="bedType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bed_type" :value="scope.row.bedType"/>
        </template>
      </el-table-column>
      <el-table-column label="床位状态" align="center" prop="bedStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bed_status" :value="scope.row.bedStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="价格" align="center" prop="price">
        <template slot-scope="scope">
          <span>￥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column label="楼层" align="center" prop="floorNumber" />
      <el-table-column label="房间面积" align="center" prop="roomArea">
        <template slot-scope="scope">
          <span>{{ scope.row.roomArea }}㎡</span>
        </template>
      </el-table-column>
      <el-table-column label="独立卫浴" align="center" prop="hasBathroom">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.hasBathroom"/>
        </template>
      </el-table-column>
      <el-table-column label="阳台" align="center" prop="hasBalcony">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.hasBalcony"/>
        </template>
      </el-table-column>
      <el-table-column label="设施配置" align="center" prop="facilities" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['elder:bed:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['elder:bed:remove']"
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

    <!-- 添加或修改床位信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="选择机构" prop="institutionId">
              <el-select v-model="form.institutionId" placeholder="请选择机构" filterable clearable style="width: 100%">
                <el-option
                  v-for="institution in institutionList"
                  :key="institution.institutionId"
                  :label="institution.institutionName"
                  :value="institution.institutionId"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-input v-model="form.roomNumber" placeholder="请输入房间号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="床位号" prop="bedNumber">
              <el-input v-model="form.bedNumber" placeholder="请输入床位号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床位类型" prop="bedType">
              <el-select v-model="form.bedType" placeholder="请选择床位类型">
                <el-option
                  v-for="dict in dict.type.bed_type"
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
            <el-form-item label="床位状态" prop="bedStatus">
              <el-select v-model="form.bedStatus" placeholder="请选择床位状态">
                <el-option
                  v-for="dict in dict.type.bed_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input v-model="form.price" placeholder="请输入价格" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="楼层" prop="floorNumber">
              <el-input v-model="form.floorNumber" placeholder="请输入楼层" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间面积" prop="roomArea">
              <el-input v-model="form.roomArea" placeholder="请输入房间面积" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="独立卫浴" prop="hasBathroom">
              <el-select v-model="form.hasBathroom" placeholder="请选择独立卫浴">
                <el-option
                  v-for="dict in dict.type.sys_yes_no"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="阳台" prop="hasBalcony">
              <el-select v-model="form.hasBalcony" placeholder="请选择阳台">
                <el-option
                  v-for="dict in dict.type.sys_yes_no"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="设施配置" prop="facilities">
          <el-input v-model="form.facilities" type="textarea" placeholder="请输入设施配置" />
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
  </div>
</template>

<script>
import { listBed, getBed, delBed, addBed, updateBed } from "@/api/elder/bed";
import { listInstitution } from "@/api/pension/institution";

export default {
  name: "BedInfo",
  dicts: ['bed_type', 'bed_status', 'sys_yes_no'],
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
      // 床位信息表格数据
      bedInfoList: [],
      // 机构列表
      institutionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        roomNumber: null,
        bedNumber: null,
        bedType: null,
        bedStatus: null,
        floorNumber: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        institutionId: [
          { required: true, message: "机构ID不能为空", trigger: "blur" }
        ],
        roomNumber: [
          { required: true, message: "房间号不能为空", trigger: "blur" }
        ],
        bedNumber: [
          { required: true, message: "床位号不能为空", trigger: "blur" }
        ],
        bedType: [
          { required: true, message: "床位类型不能为空", trigger: "change" }
        ],
        bedStatus: [
          { required: true, message: "床位状态不能为空", trigger: "change" }
        ],
        price: [
          { required: true, message: "价格不能为空", trigger: "blur" },
          { pattern: /^\d+(\.\d{1,2})?$/, message: "请输入正确的价格", trigger: "blur" }
        ],
        floorNumber: [
          { pattern: /^\d+$/, message: "请输入正确的楼层", trigger: "blur" }
        ],
        roomArea: [
          { pattern: /^\d+(\.\d{1,2})?$/, message: "请输入正确的房间面积", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.loadInstitutionList();
  },
  methods: {
    /** 查询床位信息列表 */
    getList() {
      this.loading = true;
      listBed(this.queryParams).then(response => {
        this.bedInfoList = response.rows;
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
        bedId: null,
        institutionId: null,
        roomNumber: null,
        bedNumber: null,
        bedType: null,
        bedStatus: "0",
        price: null,
        floorNumber: null,
        roomArea: null,
        hasBathroom: "0",
        hasBalcony: "0",
        facilities: null,
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
      this.ids = selection.map(item => item.bedId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加床位信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const bedId = row.bedId || this.ids
      getBed(bedId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改床位信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.bedId != null) {
            updateBed(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addBed(this.form).then(response => {
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
      const bedIds = row.bedId || this.ids;
      this.$modal.confirm('是否确认删除床位信息编号为"' + bedIds + '"的数据项？').then(function() {
        return delBed(bedIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('elder/bed/export', {
        ...this.queryParams
      }, `bed_info_${new Date().getTime()}.xlsx`)
    },
    /** 加载机构列表 */
    loadInstitutionList() {
      listInstitution({pageNum: 1, pageSize: 1000, status: '1'}).then(response => {
        this.institutionList = response.rows || [];
      });
    }
  }
};
</script>