<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="机构" prop="institutionId">
        <el-select v-model="queryParams.institutionId" placeholder="请选择机构" filterable clearable>
          <el-option
            v-for="institution in institutionList"
            :key="institution.institutionId"
            :label="institution.institutionName"
            :value="institution.institutionId"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="档次" prop="mealLevel">
        <el-input v-model="queryParams.mealLevel" placeholder="请输入餐费档次" clearable />
      </el-form-item>
      <el-form-item label="状态" prop="isAvailable">
        <el-select v-model="queryParams.isAvailable" placeholder="请选择状态" clearable>
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
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
          v-hasPermi="['elder:meal:add']"
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
          v-hasPermi="['elder:meal:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['elder:meal:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="mealList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="机构名称" prop="institutionName" show-overflow-tooltip />
      <el-table-column label="餐费档次" prop="mealLevel" width="120" />
      <el-table-column label="餐费价格(元/月)" prop="price" width="140" align="center">
        <template slot-scope="scope">
          <span class="price-text">￥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column label="餐费说明" prop="mealDescription" show-overflow-tooltip />
      <el-table-column label="状态" prop="isAvailable" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isAvailable === '1'" type="success">启用</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['elder:meal:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['elder:meal:remove']"
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

    <!-- 添加或修改餐费配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择机构" prop="institutionId">
          <el-select v-model="form.institutionId" placeholder="请选择机构" filterable :disabled="form.configId != null" style="width: 100%">
            <el-option
              v-for="institution in institutionList"
              :key="institution.institutionId"
              :label="institution.institutionName"
              :value="institution.institutionId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="餐费档次" prop="mealLevelCode">
          <el-select v-model="form.mealLevelCode" placeholder="请选择餐费档次" style="width: 100%">
            <el-option label="一级餐" value="1">
              <span style="float: left">一级餐</span>
            </el-option>
            <el-option label="二级餐" value="2">
              <span style="float: left">二级餐</span>
            </el-option>
            <el-option label="三级餐" value="3">
              <span style="float: left">三级餐</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="餐费价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" controls-position="right" style="width: 100%" />
          <span style="margin-left: 10px; color: #909399">元/月</span>
        </el-form-item>
        <el-form-item label="是否启用" prop="isAvailable">
          <el-radio-group v-model="form.isAvailable">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="餐费说明" prop="mealDescription">
          <el-input v-model="form.mealDescription" type="textarea" placeholder="请输入餐费说明，如：包含三餐、水果、牛奶等" />
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
  </div>
</template>

<script>
import { listMeal, getMeal, delMeal, addMeal, updateMeal } from "@/api/pension/mealFeeConfig";
import { listInstitution } from "@/api/pension/institution";

export default {
  name: "MealConfig",
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
      // 餐费配置表格数据
      mealList: [],
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
        institutionId: null,
        mealLevel: null,
        isAvailable: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        institutionId: [
          { required: true, message: "请选择机构", trigger: "change" }
        ],
        mealLevelCode: [
          { required: true, message: "请选择餐费档次", trigger: "change" }
        ],
        price: [
          { required: true, message: "餐费价格不能为空", trigger: "blur" }
        ],
        isAvailable: [
          { required: true, message: "请选择是否启用", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.loadInstitutionList();
  },
  methods: {
    /** 查询餐费配置列表 */
    getList() {
      this.loading = true;
      listMeal(this.queryParams).then(response => {
        this.mealList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询机构列表 */
    loadInstitutionList() {
      listInstitution({ status: "1" }).then(response => {
        this.institutionList = response.rows || [];
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
        configId: null,
        institutionId: null,
        mealLevel: null,
        mealLevelCode: null,
        price: null,
        mealDescription: null,
        isAvailable: "1",
        sortOrder: 0,
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
      this.ids = selection.map(item => item.configId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加餐费配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const configId = row.configId || this.ids
      getMeal(configId).then(response => {
        this.form = response.data;
        // 设置档次代码对应的档次名称
        if (this.form.mealLevelCode === '1') {
          this.form.mealLevel = '一级餐';
        } else if (this.form.mealLevelCode === '2') {
          this.form.mealLevel = '二级餐';
        } else if (this.form.mealLevelCode === '3') {
          this.form.mealLevel = '三级餐';
        }
        this.open = true;
        this.title = "修改餐费配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 设置档次名称
          if (this.form.mealLevelCode === '1') {
            this.form.mealLevel = '一级餐';
          } else if (this.form.mealLevelCode === '2') {
            this.form.mealLevel = '二级餐';
          } else if (this.form.mealLevelCode === '3') {
            this.form.mealLevel = '三级餐';
          }

          if (this.form.configId != null) {
            updateMeal(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMeal(this.form).then(response => {
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
      const configIds = row.configId || this.ids;
      this.$modal.confirm('是否确认删除选中的餐费配置？').then(function() {
        return delMeal(configIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('elder/meal/export', {
        ...this.queryParams
      }, `餐费配置_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped>
.price-text {
  color: #f56c6c;
  font-weight: bold;
}
</style>
