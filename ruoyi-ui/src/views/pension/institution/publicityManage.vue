<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公示状态" prop="isPublished">
        <el-select v-model="queryParams.isPublished" placeholder="请选择公示状态" clearable>
          <el-option label="未公示" value="0" />
          <el-option label="已公示" value="1" />
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
          v-hasPermi="['pension:publicity:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-upload2"
          size="mini"
          :disabled="multiple"
          @click="handleBatchPublish"
          v-hasPermi="['pension:publicity:publish']"
        >批量发布</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['pension:publicity:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:publicity:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="publicityList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" :selectable="checkSelectable" />
      <el-table-column label="机构名称" align="center" prop="institutionName" :show-overflow-tooltip="true" />
      <el-table-column label="统一信用代码" align="center" prop="creditCode" width="180" />
      <el-table-column label="备案号" align="center" prop="recordNumber" width="140" />
      <el-table-column label="床位数" align="center" prop="bedCount" width="90">
        <template slot-scope="scope">
          <span>{{ scope.row.bedCount }}张</span>
        </template>
      </el-table-column>
        <el-table-column label="公示状态" align="center" prop="isPublished" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isPublished === '1'" type="success">已公示</el-tag>
          <el-tag v-else-if="scope.row.publicId" type="info">未公示</el-tag>
          <el-tag v-else type="warning">未维护</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="260">
        <template slot-scope="scope">
          <!-- 如果还没有公示记录,只显示新增按钮 -->
          <template v-if="!scope.row.publicId">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-plus"
              @click="handleAddForInstitution(scope.row)"
              v-hasPermi="['pension:publicity:add']"
            >维护公示信息</el-button>
          </template>
          <!-- 如果已有公示记录,显示完整操作按钮 -->
          <template v-else>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row)"
              v-hasPermi="['pension:publicity:query']"
            >查看</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['pension:publicity:edit']"
            >编辑</el-button>
            <el-button
              v-if="scope.row.isPublished === '0'"
              size="mini"
              type="text"
              icon="el-icon-upload2"
              @click="handlePublish(scope.row)"
              v-hasPermi="['pension:publicity:publish']"
            >发布</el-button>
            <el-button
              v-else-if="scope.row.isPublished === '1'"
              size="mini"
              type="text"
              icon="el-icon-download"
              @click="handleUnpublish(scope.row)"
              v-hasPermi="['pension:publicity:unpublish']"
            >取消公示</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['pension:publicity:remove']"
            >删除</el-button>
          </template>
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

    <!-- 添加或修改公示信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="养老机构" prop="institutionName">
              <el-input v-model="form.institutionName" disabled placeholder="机构名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="占地面积(㎡)" prop="landArea">
              <el-input-number v-model="form.landArea" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="建筑面积(㎡)" prop="buildingArea">
              <el-input-number v-model="form.buildingArea" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 结构化费用信息 -->
        <el-divider content-position="left">费用信息</el-divider>

        <!-- 护理费 -->
        <el-form-item label="护理费(元/月)" prop="nursingFee">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-input-number v-model="form.nursingFeeMin" :min="0" :precision="2" placeholder="最低护理费用" style="width: 100%;" />
            </el-col>
            <el-col :span="8">
              <el-input-number v-model="form.nursingFeeMax" :min="0" :precision="2" placeholder="最高护理费用" style="width: 100%;" />
            </el-col>
            <el-col :span="8">
              <div class="form-tip" style="line-height: 32px;">护理费最低和最高费用</div>
            </el-col>
          </el-row>
        </el-form-item>

        <!-- 床位费 -->
        <el-form-item label="床位费(元/月)" prop="bedFee">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-input-number v-model="form.bedFeeMin" :min="0" :precision="2" placeholder="最低床位费用" style="width: 100%;" />
            </el-col>
            <el-col :span="8">
              <el-input-number v-model="form.bedFeeMax" :min="0" :precision="2" placeholder="最高床位费用" style="width: 100%;" />
            </el-col>
            <el-col :span="8">
              <div class="form-tip" style="line-height: 32px;">床位费最低和最高费用</div>
            </el-col>
          </el-row>
        </el-form-item>

        <!-- 膳食费 -->
        <el-form-item label="膳食费(元/月)" prop="mealFee">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-input-number v-model="form.mealFeeMin" :min="0" :precision="2" placeholder="最低膳食费用" style="width: 100%;" />
            </el-col>
            <el-col :span="8">
              <el-input-number v-model="form.mealFeeMax" :min="0" :precision="2" placeholder="最高膳食费用" style="width: 100%;" />
            </el-col>
            <el-col :span="8">
              <div class="form-tip" style="line-height: 32px;">膳食费最低和最高费用</div>
            </el-col>
          </el-row>
        </el-form-item>

        <!-- 总费用 -->
        <el-form-item label="总费用(元/月)">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-input v-model="totalFeeMin" readonly placeholder="最低总费用(自动计算)" style="width: 100%;" />
            </el-col>
            <el-col :span="6">
              <el-input v-model="totalFeeMax" readonly placeholder="最高总费用(自动计算)" style="width: 100%;" />
            </el-col>
            <el-col :span="12">
              <div class="form-tip" style="line-height: 32px;">
                <strong>总费用 = 护理费 + 床位费 + 膳食费</strong>
              </div>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="收住对象能力" prop="acceptElderType">
          <el-checkbox-group v-model="acceptElderTypeList">
            <el-checkbox label="self_care">自理老人</el-checkbox>
            <el-checkbox label="semi_disabled">半失能老人</el-checkbox>
            <el-checkbox label="disabled">失能老人</el-checkbox>
            <el-checkbox label="dementia">失智老人</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="机构主图">
          <image-upload v-model="form.mainPicture" :limit="1" />
          <div style="color: #999; font-size: 12px; margin-top: 8px;">只能上传1张图片，作为机构主要展示图片，建议尺寸800x600</div>
        </el-form-item>
        <el-form-item label="机构简介" prop="institutionIntro">
          <el-input v-model="form.institutionIntro" type="textarea" :rows="4" placeholder="请输入机构简介" />
        </el-form-item>
        <el-form-item label="服务范围" prop="serviceScope">
          <el-input v-model="form.serviceScope" type="textarea" :rows="3" placeholder="请输入服务范围，如：生活照料、医疗护理、康复训练等" />
        </el-form-item>
        <el-form-item label="特色服务" prop="serviceFeatures">
          <el-input v-model="form.serviceFeatures" type="textarea" :rows="3" placeholder="请输入特色服务，如：中医理疗、文化娱乐、营养膳食等" />
        </el-form-item>
        <el-form-item label="环境图片">
          <image-upload v-model="form.environmentImgs" :limit="9" />
          <div style="color: #999; font-size: 12px;">最多上传9张图片</div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="info" icon="el-icon-view" @click="handlePreview">预 览</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="公示信息详情" :visible.sync="viewOpen" width="1000px" append-to-body>
      <div class="publicity-detail" v-if="viewData.publicId">
        <div class="detail-header">
          <h2>{{ viewData.institutionName || '-' }}</h2>
          <el-tag v-if="viewData.isPublished === '1'" type="success" size="medium">已公示</el-tag>
          <el-tag v-else type="info" size="medium">未公示</el-tag>
        </div>

        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="机构名称" :span="2">{{ viewData.institutionName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统一信用代码">{{ viewData.creditCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="机构备案号">{{ viewData.recordNumber || '-' }}</el-descriptions-item>
          <el-descriptions-item label="机构地址" :span="2">{{ viewData.actualAddress || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ viewData.contactPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ viewData.contactPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系邮箱" :span="2">{{ viewData.contactEmail || '-' }}</el-descriptions-item>
          <el-descriptions-item label="监管账户" :span="2">{{ viewData.superviseAccount || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="规模信息" :column="3" border style="margin-top: 20px;">
          <el-descriptions-item label="占地面积">
            <span v-if="viewData.landArea">{{ viewData.landArea }}㎡</span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="建筑面积">
            <span v-if="viewData.buildingArea">{{ viewData.buildingArea }}㎡</span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="床位数">
            <span v-if="viewData.bedCount">{{ viewData.bedCount }}张</span>
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="服务信息" :column="2" border style="margin-top: 20px;">
          <el-descriptions-item label="收住对象能力">
            {{ formatAcceptElderType(viewData.acceptElderType) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 费用信息 -->
        <el-descriptions title="费用信息(元/月)" :column="2" border style="margin-top: 20px;">
          <el-descriptions-item label="护理费">
            <span v-if="viewData.nursingFeeMin != null && viewData.nursingFeeMax != null">
              {{ viewData.nursingFeeMin }} - {{ viewData.nursingFeeMax }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="床位费">
            <span v-if="viewData.bedFeeMin != null && viewData.bedFeeMax != null">
              {{ viewData.bedFeeMin }} - {{ viewData.bedFeeMax }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="膳食费">
            <span v-if="viewData.mealFeeMin != null && viewData.mealFeeMax != null">
              {{ viewData.mealFeeMin }} - {{ viewData.mealFeeMax }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="总费用">
            <span v-if="viewData.nursingFeeMin != null && viewData.nursingFeeMax != null &&
                      viewData.bedFeeMin != null && viewData.bedFeeMax != null &&
                      viewData.mealFeeMin != null && viewData.mealFeeMax != null">
              {{ (viewData.nursingFeeMin + viewData.bedFeeMin + viewData.mealFeeMin).toFixed(2) }} -
              {{ (viewData.nursingFeeMax + viewData.bedFeeMax + viewData.mealFeeMax).toFixed(2) }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-section" v-if="viewData.institutionIntro">
          <h4>机构简介</h4>
          <p>{{ viewData.institutionIntro }}</p>
        </div>

        <div class="detail-section" v-if="viewData.serviceScope">
          <h4>服务范围</h4>
          <p>{{ viewData.serviceScope }}</p>
        </div>

        <div class="detail-section" v-if="viewData.serviceFeatures">
          <h4>特色服务</h4>
          <p>{{ viewData.serviceFeatures }}</p>
        </div>

        <!-- 机构主图 -->
        <div class="detail-section" v-if="viewData.mainPicture">
          <h4>机构主图</h4>
          <div class="main-image-container">
            <el-image
              :src="getImageUrl(viewData.mainPicture)"
              :preview-src-list="[getImageUrl(viewData.mainPicture)]"
              fit="cover"
              style="width: 300px; height: 200px; border-radius: 5px;"
            ></el-image>
          </div>
        </div>

        <div class="detail-section" v-if="viewData.environmentImgs">
          <h4>环境图片</h4>
          <div class="image-gallery">
            <el-image
              v-for="(img, index) in getImageList(viewData.environmentImgs)"
              :key="index"
              :src="img"
              :preview-src-list="getImageList(viewData.environmentImgs)"
              fit="cover"
              class="gallery-image"
            ></el-image>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog title="公示信息预览" :visible.sync="previewOpen" width="1000px" append-to-body>
      <div class="publicity-preview">
        <div class="preview-header">
          <h2>{{ form.institutionName || '养老机构' }}</h2>
          <el-tag type="info" size="medium">预览模式</el-tag>
        </div>

        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="机构名称" :span="2">{{ form.institutionName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统一信用代码">{{ previewData.creditCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="机构备案号">{{ previewData.recordNumber || '-' }}</el-descriptions-item>
          <el-descriptions-item label="机构地址" :span="2">{{ previewData.actualAddress || '-' }}</el-descriptions-item>
          <el-descriptions-item label="监管账户" :span="2">{{ previewData.superviseAccount || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="规模信息" :column="3" border style="margin-top: 20px;">
          <el-descriptions-item label="占地面积">{{ form.landArea ? form.landArea + '㎡' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="建筑面积">{{ form.buildingArea ? form.buildingArea + '㎡' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="床位数">{{ previewData.bedCount ? previewData.bedCount + '张' : '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="服务信息" :column="2" border style="margin-top: 20px;">
          <el-descriptions-item label="收住对象能力">
            {{ previewAcceptElderType() }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-section" v-if="form.institutionIntro">
          <h4>机构简介</h4>
          <p>{{ form.institutionIntro }}</p>
        </div>

        <div class="detail-section" v-if="form.serviceScope">
          <h4>服务范围</h4>
          <p>{{ form.serviceScope }}</p>
        </div>

        <div class="detail-section" v-if="form.serviceFeatures">
          <h4>特色服务</h4>
          <p>{{ form.serviceFeatures }}</p>
        </div>

        <div class="detail-section" v-if="form.environmentImgs">
          <h4>环境图片</h4>
          <div class="image-gallery">
            <el-image
              v-for="(img, index) in getImageList(form.environmentImgs)"
              :key="index"
              :src="img"
              :preview-src-list="getImageList(form.environmentImgs)"
              fit="cover"
              class="gallery-image"
            ></el-image>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="previewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listPublicity, getPublicity, addPublicity, updatePublicity, delPublicity, publishPublicity, unpublishPublicity, batchPublish } from "@/api/pension/publicityManage";
import { listInstitution } from "@/api/pension/institution";
import ImageUpload from '@/components/ImageUpload';
import ImagePreview from '@/components/ImagePreview';

export default {
  name: "PublicityManage",
  components: {
    ImageUpload,
    ImagePreview
  },
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
      // 公示信息表格数据
      publicityList: [],
      // 机构列表
      institutionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示查看弹出层
      viewOpen: false,
      // 查看数据
      viewData: {},
      // 是否显示预览弹出层
      previewOpen: false,
      // 预览数据(机构基础信息)
      previewData: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        isPublished: null
      },
      // 表单参数
      form: {},
      // 收住对象能力列表
      acceptElderTypeList: [],
      // 表单校验
      rules: {
        landArea: [
          { required: true, message: "请输入占地面积", trigger: "blur" }
        ],
        buildingArea: [
          { required: true, message: "请输入建筑面积", trigger: "blur" }
        ],
        institutionIntro: [
          { required: true, message: "请输入机构简介", trigger: "blur" }
        ]
      }
    };
  },
  computed: {
    // 计算总费用最低价
    totalFeeMin() {
      const nursing = this.form.nursingFeeMin || 0;
      const bed = this.form.bedFeeMin || 0;
      const meal = this.form.mealFeeMin || 0;
      return (nursing + bed + meal).toFixed(2);
    },
    // 计算总费用最高价
    totalFeeMax() {
      const nursing = this.form.nursingFeeMax || 0;
      const bed = this.form.bedFeeMax || 0;
      const meal = this.form.mealFeeMax || 0;
      return (nursing + bed + meal).toFixed(2);
    }
  },
  watch: {
    // 监听费用变化，实时更新总费用显示
    'form.nursingFeeMin': {
      handler() {
        this.$forceUpdate();
      },
      deep: true
    },
    'form.nursingFeeMax': {
      handler() {
        this.$forceUpdate();
      },
      deep: true
    },
    'form.bedFeeMin': {
      handler() {
        this.$forceUpdate();
      },
      deep: true
    },
    'form.bedFeeMax': {
      handler() {
        this.$forceUpdate();
      },
      deep: true
    },
    'form.mealFeeMin': {
      handler() {
        this.$forceUpdate();
      },
      deep: true
    },
    'form.mealFeeMax': {
      handler() {
        this.$forceUpdate();
      },
      deep: true
    }
  },
  created() {
    this.getList();
    this.getInstitutionList();
  },
  methods: {
    /** 查询公示信息列表 */
    getList() {
      this.loading = true;
      listPublicity(this.queryParams).then(response => {
        this.publicityList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询机构列表(只查询已入驻的机构) */
    getInstitutionList() {
      listInstitution({ status: '1' }).then(response => {
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
        publicId: null,
        institutionId: null,
        institutionName: null,
        institutionIntro: null,
        serviceScope: null,
        serviceFeatures: null,
        landArea: null,
        buildingArea: null,
        environmentImgs: null,
        mainPicture: null,
        // 结构化费用字段
        nursingFeeMin: 0,
        nursingFeeMax: 0,
        bedFeeMin: 0,
        bedFeeMax: 0,
        mealFeeMin: 0,
        mealFeeMax: 0,
        acceptElderType: null,
        isPublished: '0'
      };
      this.acceptElderTypeList = [];
      this.previewData = {};
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
    // 复选框是否可选
    checkSelectable(row) {
      // 只有已有公示记录的行才能被选中
      return row.publicId != null;
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.publicId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加公示信息";
    },
    /** 为指定机构新增公示信息 */
    handleAddForInstitution(row) {
      this.reset();
      // 预填充机构ID和名称
      this.form.institutionId = row.institutionId;
      this.form.institutionName = row.institutionName;
      // 填充预览数据(机构基础信息)
      this.previewData = {
        creditCode: row.creditCode,
        recordNumber: row.recordNumber,
        actualAddress: row.actualAddress,
        bedCount: row.bedCount,
        superviseAccount: row.superviseAccount,
        feeRange: row.feeRange
      };
      this.open = true;
      this.title = "维护公示信息 - " + row.institutionName;
    },
    /** 机构选择改变 */
    handleInstitutionChange(institutionId) {
      // 可以根据选择的机构自动填充一些基础信息
      const institution = this.institutionList.find(item => item.institutionId === institutionId);
      if (institution) {
        // 这里可以预填充一些信息
      }
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const publicId = row.publicId || this.ids[0];
      getPublicity(publicId).then(response => {
        this.form = response.data;
        // 解析收住对象能力
        if (this.form.acceptElderType) {
          this.acceptElderTypeList = this.form.acceptElderType.split(',');
        }
        // 填充机构名称和预览数据
        this.form.institutionName = response.data.institutionName;
        this.previewData = {
          creditCode: response.data.creditCode,
          recordNumber: response.data.recordNumber,
          actualAddress: response.data.actualAddress,
          bedCount: response.data.bedCount,
          superviseAccount: response.data.superviseAccount,
          feeRange: response.data.feeRange
        };
        this.open = true;
        this.title = "修改公示信息";
      });
    },
    /** 查看按钮操作 */
    handleView(row) {
      // 如果没有公示记录,不能查看
      if (!row.publicId) {
        this.$modal.msgWarning("该机构还未维护公示信息");
        return;
      }
      const publicId = row.publicId;
      getPublicity(publicId).then(response => {
        this.viewData = response.data;
        this.viewOpen = true;
      });
    },
    /** 预览按钮操作 */
    handlePreview() {
      // 打开预览对话框
      this.previewOpen = true;
    },
    /** 格式化预览的收住对象能力 */
    previewAcceptElderType() {
      if (!this.acceptElderTypeList || this.acceptElderTypeList.length === 0) return '-';
      const typeMap = {
        'self_care': '自理老人',
        'semi_disabled': '半失能老人',
        'disabled': '失能老人',
        'dementia': '失智老人'
      };
      return this.acceptElderTypeList.map(item => typeMap[item] || item).join('、');
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 将收住对象能力数组转为逗号分隔字符串
          this.form.acceptElderType = this.acceptElderTypeList.join(',');

          if (this.form.publicId != null) {
            updatePublicity(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPublicity(this.form).then(response => {
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
      const publicIds = row.publicId ? [row.publicId] : this.ids;
      this.$modal.confirm('是否确认删除选中的公示信息？').then(function() {
        return delPublicity(publicIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 发布公示 */
    handlePublish(row) {
      const publicId = row.publicId;
      this.$modal.confirm('是否确认发布"' + row.institutionName + '"的公示信息？发布后信息将对公众可见。').then(function() {
        return publishPublicity(publicId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("发布成功");
      }).catch(() => {});
    },
    /** 取消公示 */
    handleUnpublish(row) {
      const publicId = row.publicId;
      this.$modal.confirm('是否确认取消"' + row.institutionName + '"的公示？取消后信息将不再对公众可见。').then(function() {
        return unpublishPublicity(publicId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("取消成功");
      }).catch(() => {});
    },
    /** 批量发布公示 */
    handleBatchPublish() {
      this.$modal.confirm('是否确认批量发布选中的公示信息？').then(() => {
        return batchPublish(this.ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("批量发布成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/publicity/export', {
        ...this.queryParams
      }, `publicity_${new Date().getTime()}.xlsx`)
    },
    /** 格式化收住对象能力 */
    formatAcceptElderType(acceptElderType) {
      if (!acceptElderType) return '-';
      const typeMap = {
        'self_care': '自理老人',
        'semi_disabled': '半失能老人',
        'disabled': '失能老人',
        'dementia': '失智老人'
      };
      return acceptElderType.split(',').map(item => typeMap[item] || item).join('、');
    },
    /** 获取图片列表 */
    getImageList(imgStr) {
      if (!imgStr) return [];
      const imgList = imgStr.split(',');
      return imgList.map(img => {
        // 如果是外部链接,直接返回
        if (img.startsWith('http://') || img.startsWith('https://')) {
          return img;
        }
        // 否则拼接基础路径
        return process.env.VUE_APP_BASE_API + img;
      });
    },
    // 获取单个图片URL
    getImageUrl(imgStr) {
      if (!imgStr) return '';
      // 如果是外部链接,直接返回
      if (imgStr.startsWith('http://') || imgStr.startsWith('https://')) {
        return imgStr;
      }
      // 否则拼接基础路径
      return process.env.VUE_APP_BASE_API + imgStr;
    }
  }
};
</script>

<style scoped>
.publicity-detail,
.publicity-preview {
  max-height: 700px;
  overflow-y: auto;
  padding: 10px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  line-height: 1.4;
}

.detail-header,
.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #409EFF;
}

.detail-header h2,
.preview-header h2 {
  margin: 0;
  color: #303133;
  font-size: 22px;
  font-weight: 600;
}

.detail-section {
  margin-top: 25px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #409EFF;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #409EFF;
  font-size: 15px;
  font-weight: 600;
}

.detail-section p {
  margin: 0;
  color: #303133;
  line-height: 1.8;
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 描述列表样式优化 */
.publicity-detail >>> .el-descriptions__title,
.publicity-preview >>> .el-descriptions__title {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
  margin-bottom: 15px;
}

.publicity-detail >>> .el-descriptions-item__label,
.publicity-preview >>> .el-descriptions-item__label {
  font-weight: 600;
  background-color: #f5f7fa;
}

/* 图片画廊样式 */
.image-gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.gallery-image {
  width: 150px;
  height: 150px;
  border-radius: 5px;
  background-color: #ebeef5;
  box-shadow: 0 0 5px 1px #ccc;
  cursor: pointer;
  transition: all 0.3s;
}

.gallery-image:hover {
  transform: scale(1.05);
  box-shadow: 0 0 10px 2px #999;
}

.main-image-container {
  margin-bottom: 15px;
  text-align: center;
}
</style>
