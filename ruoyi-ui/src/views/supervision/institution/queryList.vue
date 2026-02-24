<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
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
      <el-form-item label="机构状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择机构状态" clearable>
          <el-option label="待审批" value="0" />
          <el-option label="已入驻" value="1" />
          <el-option label="已驳回" value="2" />
          <el-option label="解除监管" value="3" />
          <el-option label="维护中" value="5" />
          <el-option label="维护待审批" value="6" />
        </el-select>
      </el-form-item>
      <el-form-item label="监管账户" prop="hasSupervisionAccount">
        <el-select v-model="queryParams.hasSupervisionAccount" placeholder="监管账户状态" clearable>
          <el-option label="已开户" :value="true" />
          <el-option label="未开户" :value="false" />
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
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handleApprove"
          v-hasPermi="['pension:institution:approve']"
        >审批</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-s-custom"
          size="mini"
          :disabled="multiple"
          @click="handleAddToBlacklist"
          v-hasPermi="['pension:institution:blacklist']"
        >移入黑名单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-warning-outline"
          size="mini"
          @click="handleWarningInstitutions"
        >预警机构</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:institution:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="institutionList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="机构名称" align="center" prop="institutionName" min-width="150" show-overflow-tooltip />
      <el-table-column label="统一信用代码" align="center" prop="creditCode" width="180" />
      <el-table-column label="预收服务费" align="center" width="120">
        <template slot-scope="scope">
          <span style="color: #409EFF; font-weight: bold;">¥{{ formatMoney(scope.row.serviceFeeBalance || 0) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预收押金" align="center" width="120">
        <template slot-scope="scope">
          <span style="color: #67C23A; font-weight: bold;">¥{{ formatMoney(scope.row.depositBalance || 0) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预收会员费" align="center" width="120">
        <template slot-scope="scope">
          <span style="color: #E6A23C; font-weight: bold;">¥{{ formatMoney(scope.row.memberFeeBalance || 0) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="监管账户" align="center" width="130">
        <template slot-scope="scope">
          <div v-if="scope.row.hasSupervisionAccount" style="color: #67C23A;">
            <i class="el-icon-check"></i> 已开户
          </div>
          <div v-else style="color: #F56C6C;">
            <i class="el-icon-close"></i> 未开户
          </div>
        </template>
      </el-table-column>
      <el-table-column label="入驻状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pension_institution_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="床位使用率" align="center" width="120">
        <template slot-scope="scope">
          <el-progress
            :percentage="getOccupancyRate(scope.row)"
            :color="getOccupancyColor(getOccupancyRate(scope.row))"
            :stroke-width="6"
            :show-text="false"
            style="margin-bottom: 4px;"
          ></el-progress>
          <span style="font-size: 12px;">{{ getOccupancyRate(scope.row) }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="联系人" align="center" prop="contactPerson" width="100" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="120" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['pension:institution:query']"
          >详情</el-button>
          <el-dropdown @command="(command) => handleCommand(command, scope.row)" style="margin-left: 5px;">
            <el-button size="mini" type="text">
              更多<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="editAccount">维护监管账号</el-dropdown-item>
              <el-dropdown-item command="blacklist">移入黑名单</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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

    <!-- 机构详情对话框 -->
    <el-dialog title="机构详情" :visible.sync="detailOpen" width="1000px" append-to-body>
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="机构名称">{{ currentInstitution.institutionName }}</el-descriptions-item>
            <el-descriptions-item label="统一信用代码">{{ currentInstitution.creditCode }}</el-descriptions-item>
            <el-descriptions-item label="法定代表人">{{ currentInstitution.legalPerson || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ currentInstitution.contactPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentInstitution.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="联系邮箱">{{ currentInstitution.contactEmail || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="注册资金">{{ currentInstitution.registeredCapital }}万元</el-descriptions-item>
            <el-descriptions-item label="成立日期">{{ parseTime(currentInstitution.establishedDate, '{y}-{m}-{d}') || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="注册地址" :span="2">{{ currentInstitution.registeredAddress || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="实际经营地址" :span="2">{{ currentInstitution.actualAddress || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="机构状态">
              <dict-tag :options="dict.type.pension_institution_status" :value="currentInstitution.status"/>
            </el-descriptions-item>
            <el-descriptions-item label="机构评级">
              <el-rate
                v-model="currentInstitution.rating"
                disabled
                show-score
                text-color="#ff9900"
                :max="5"
              ></el-rate>
            </el-descriptions-item>
            <el-descriptions-item label="入驻时间">
              {{ parseTime(currentInstitution.applyTime || currentInstitution.createTime) || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="监管账户">
              <span v-if="currentInstitution.hasSupervisionAccount" style="color: #67C23A;">
                <i class="el-icon-check"></i> 已开户
              </span>
              <span v-else style="color: #F56C6C;">
                <i class="el-icon-close"></i> 未开户
              </span>
            </el-descriptions-item>
            <el-descriptions-item label="床位数">{{ currentInstitution.bedCount || 0 }}张</el-descriptions-item>
            <el-descriptions-item label="入住老人数">{{ currentInstitution.actualElders || 0 }}人</el-descriptions-item>
            <el-descriptions-item label="床位使用率" :span="2">
              <el-progress
                :percentage="getOccupancyRate(currentInstitution)"
                :color="getOccupancyColor(getOccupancyRate(currentInstitution))"
                :stroke-width="10"
                style="width: 200px;"
              ></el-progress>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 资金信息 -->
        <el-tab-pane label="资金信息" name="financial">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card shadow="hover" class="financial-card">
                <div class="financial-content">
                  <div class="financial-icon service">
                    <i class="el-icon-coin"></i>
                  </div>
                  <div class="financial-info">
                    <div class="financial-title">预收服务费</div>
                    <div class="financial-amount">¥{{ formatMoney(currentInstitution.serviceFeeBalance || 0) }}</div>
                    <div class="financial-desc">用于服务费用的预收资金</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="financial-card">
                <div class="financial-content">
                  <div class="financial-icon deposit">
                    <i class="el-icon-lock"></i>
                  </div>
                  <div class="financial-info">
                    <div class="financial-title">预收押金</div>
                    <div class="financial-amount">¥{{ formatMoney(currentInstitution.depositBalance || 0) }}</div>
                    <div class="financial-desc">老人入住押金监管资金</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="financial-card">
                <div class="financial-content">
                  <div class="financial-icon member">
                    <i class="el-icon-star-on"></i>
                  </div>
                  <div class="financial-info">
                    <div class="financial-title">预收会员费</div>
                    <div class="financial-amount">¥{{ formatMoney(currentInstitution.memberFeeBalance || 0) }}</div>
                    <div class="financial-desc">VIP会员费用预收资金</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-descriptions :column="2" border style="margin-top: 20px;">
            <el-descriptions-item label="监管账户总余额" :span="2">
              <span style="color: #409EFF; font-size: 18px; font-weight: bold;">
                ¥{{ formatMoney((currentInstitution.serviceFeeBalance || 0) + (currentInstitution.depositBalance || 0) + (currentInstitution.memberFeeBalance || 0)) }}
              </span>
            </el-descriptions-item>
            <el-descriptions-item label="基本账户">{{ currentInstitution.bankAccount || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="基本开户行">{{ currentInstitution.basicBank || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="监管账户">{{ currentInstitution.superviseAccount || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="监管开户行">{{ currentInstitution.supervisionBank || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="账户状态" :span="2">
              <el-tag v-if="currentInstitution.hasSupervisionAccount" type="success">正常监管</el-tag>
              <el-tag v-else type="danger">未开户</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 公示信息 -->
        <el-tab-pane label="公示信息" name="publicity">
          <el-alert
            v-if="!publicityInfo"
            title="该机构暂未维护公示信息"
            type="info"
            show-icon
            :closable="false"
            style="margin-bottom: 20px;">
          </el-alert>
          <div v-else class="publicity-detail">
            <!-- 基本信息 -->
            <el-descriptions title="基本信息" :column="2" border>
              <el-descriptions-item label="机构名称" :span="2">{{ publicityInfo.institutionName || currentInstitution.institutionName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="统一信用代码">{{ currentInstitution.creditCode || '-' }}</el-descriptions-item>
              <el-descriptions-item label="机构备案号">{{ publicityInfo.recordNumber || currentInstitution.recordNumber || '-' }}</el-descriptions-item>
              <el-descriptions-item label="机构地址" :span="2">{{ currentInstitution.actualAddress || '-' }}</el-descriptions-item>
              <el-descriptions-item label="联系人">{{ currentInstitution.contactPerson || '-' }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ currentInstitution.contactPhone || '-' }}</el-descriptions-item>
              <el-descriptions-item label="监管账户" :span="2">{{ currentInstitution.superviseAccount || '-' }}</el-descriptions-item>
            </el-descriptions>

            <!-- 规模信息 -->
            <el-descriptions title="规模信息" :column="3" border style="margin-top: 20px;">
              <el-descriptions-item label="占地面积">
                <span v-if="publicityInfo.landArea">{{ publicityInfo.landArea }}㎡</span>
                <span v-else>-</span>
              </el-descriptions-item>
              <el-descriptions-item label="建筑面积">
                <span v-if="publicityInfo.buildingArea">{{ publicityInfo.buildingArea }}㎡</span>
                <span v-else>-</span>
              </el-descriptions-item>
              <el-descriptions-item label="床位数">
                <span v-if="currentInstitution.bedCount">{{ currentInstitution.bedCount }}张</span>
                <span v-else>-</span>
              </el-descriptions-item>
            </el-descriptions>

            <!-- 服务信息 -->
            <el-descriptions title="服务信息" :column="2" border style="margin-top: 20px;">
              <el-descriptions-item label="收住类型" :span="2">
                <span v-if="publicityInfo.acceptElderType">{{ formatAcceptElderType(publicityInfo.acceptElderType) }}</span>
                <span v-else-if="currentInstitution.careLevels">{{ formatCareLevels(currentInstitution.careLevels) }}</span>
                <span v-else>暂无</span>
              </el-descriptions-item>
            </el-descriptions>

            <!-- 费用信息 -->
            <el-descriptions title="费用信息(元/月)" :column="2" border style="margin-top: 20px;">
              <el-descriptions-item label="护理费">
                <span v-if="publicityInfo.nursingFeeMin != null && publicityInfo.nursingFeeMax != null">
                  {{ publicityInfo.nursingFeeMin }} - {{ publicityInfo.nursingFeeMax }}
                </span>
                <span v-else>-</span>
              </el-descriptions-item>
              <el-descriptions-item label="床位费">
                <span v-if="publicityInfo.bedFeeMin != null && publicityInfo.bedFeeMax != null">
                  {{ publicityInfo.bedFeeMin }} - {{ publicityInfo.bedFeeMax }}
                </span>
                <span v-else>-</span>
              </el-descriptions-item>
              <el-descriptions-item label="膳食费">
                <span v-if="publicityInfo.mealFeeMin != null && publicityInfo.mealFeeMax != null">
                  {{ publicityInfo.mealFeeMin }} - {{ publicityInfo.mealFeeMax }}
                </span>
                <span v-else>-</span>
              </el-descriptions-item>
              <el-descriptions-item label="总费用">
                <span v-if="publicityInfo.nursingFeeMin != null && publicityInfo.nursingFeeMax != null &&
                          publicityInfo.bedFeeMin != null && publicityInfo.bedFeeMax != null &&
                          publicityInfo.mealFeeMin != null && publicityInfo.mealFeeMax != null">
                  {{ (Number(publicityInfo.nursingFeeMin) + Number(publicityInfo.bedFeeMin) + Number(publicityInfo.mealFeeMin)).toFixed(2) }} -
                  {{ (Number(publicityInfo.nursingFeeMax) + Number(publicityInfo.bedFeeMax) + Number(publicityInfo.mealFeeMax)).toFixed(2) }}
                </span>
                <span v-else>-</span>
              </el-descriptions-item>
            </el-descriptions>

            <!-- 机构简介 -->
            <div v-if="publicityInfo.institutionIntro" class="detail-section">
              <h4>机构简介</h4>
              <p>{{ publicityInfo.institutionIntro }}</p>
            </div>

            <!-- 服务范围 -->
            <div v-if="publicityInfo.serviceScope" class="detail-section">
              <h4>服务范围</h4>
              <p>{{ publicityInfo.serviceScope }}</p>
            </div>

            <!-- 特色服务 -->
            <div v-if="publicityInfo.serviceFeatures" class="detail-section">
              <h4>特色服务</h4>
              <p>{{ publicityInfo.serviceFeatures }}</p>
            </div>

            <!-- 机构主图 -->
            <div v-if="publicityInfo.mainPicture" class="detail-section">
              <h4>机构主图</h4>
              <div class="main-image-container">
                <el-image
                  :src="processImageUrl(publicityInfo.mainPicture)"
                  :preview-src-list="[processImageUrl(publicityInfo.mainPicture)]"
                  fit="cover"
                  style="width: 300px; height: 200px; border-radius: 5px;"
                ></el-image>
              </div>
            </div>

            <!-- VR全景图片 -->
            <div v-if="publicityInfo.vrImage" class="detail-section">
              <h4>VR全景图片</h4>
              <div class="main-image-container">
                <el-image
                  :src="processImageUrl(publicityInfo.vrImage)"
                  :preview-src-list="[processImageUrl(publicityInfo.vrImage)]"
                  fit="cover"
                  style="width: 300px; height: 200px; border-radius: 5px;"
                ></el-image>
              </div>
            </div>

            <!-- 环境图片 -->
            <div v-if="publicityInfo.environmentImgs" class="detail-section">
              <h4>环境图片</h4>
              <div class="image-gallery">
                <el-image
                  v-for="(img, index) in getImageList(publicityInfo.environmentImgs)"
                  :key="index"
                  :src="img"
                  :preview-src-list="getImageList(publicityInfo.environmentImgs)"
                  fit="cover"
                  class="gallery-image"
                ></el-image>
              </div>
            </div>

            <!-- 设施图片 -->
            <div v-if="publicityInfo.roomFacilities && getImageList(publicityInfo.roomFacilities).length > 0" class="detail-section">
              <h4>房间设施图片</h4>
              <div class="image-gallery">
                <el-image
                  v-for="(img, index) in getImageList(publicityInfo.roomFacilities)"
                  :key="index"
                  :src="img"
                  :preview-src-list="getImageList(publicityInfo.roomFacilities)"
                  fit="cover"
                  class="gallery-image"
                ></el-image>
              </div>
            </div>

            <div v-if="publicityInfo.basicFacilities && getImageList(publicityInfo.basicFacilities).length > 0" class="detail-section">
              <h4>基础设施图片</h4>
              <div class="image-gallery">
                <el-image
                  v-for="(img, index) in getImageList(publicityInfo.basicFacilities)"
                  :key="index"
                  :src="img"
                  :preview-src-list="getImageList(publicityInfo.basicFacilities)"
                  fit="cover"
                  class="gallery-image"
                ></el-image>
              </div>
            </div>

            <div v-if="publicityInfo.parkFacilities && getImageList(publicityInfo.parkFacilities).length > 0" class="detail-section">
              <h4>园址设施图片</h4>
              <div class="image-gallery">
                <el-image
                  v-for="(img, index) in getImageList(publicityInfo.parkFacilities)"
                  :key="index"
                  :src="img"
                  :preview-src-list="getImageList(publicityInfo.parkFacilities)"
                  fit="cover"
                  class="gallery-image"
                ></el-image>
              </div>
            </div>

            <!-- 生活设施 -->
            <div v-if="parseJsonArray(publicityInfo.lifeFacilities).length > 0" class="detail-section">
              <h4>生活设施</h4>
              <div class="facility-list">
                <el-tag
                  v-for="(facility, index) in parseJsonArray(publicityInfo.lifeFacilities)"
                  :key="index"
                  type="success"
                  size="medium"
                  style="margin: 2px 5px 2px 0;">
                  {{ facility }}
                </el-tag>
              </div>
            </div>

            <!-- 医疗设施 -->
            <div v-if="parseJsonArray(publicityInfo.medicalFacilities).length > 0" class="detail-section">
              <h4>医疗设施</h4>
              <div class="facility-list">
                <el-tag
                  v-for="(facility, index) in parseJsonArray(publicityInfo.medicalFacilities)"
                  :key="index"
                  type="primary"
                  size="medium"
                  style="margin: 2px 5px 2px 0;">
                  {{ facility }}
                </el-tag>
              </div>
            </div>

            <!-- 每日服务时间安排 -->
            <div v-if="parseJsonArray(publicityInfo.dailyServices).length > 0" class="detail-section">
              <h4>每日服务时间安排</h4>
              <div class="service-schedule">
                <div v-for="(service, index) in parseJsonArray(publicityInfo.dailyServices)" :key="index" class="service-item">
                  <el-tag type="info" size="small">{{ service.time }}</el-tag>
                  <span class="service-content">{{ service.content }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>


    <!-- 维���监管账号对话框 -->
    <el-dialog title="维护监管账号" :visible.sync="accountEditOpen" width="500px" append-to-body>
      <el-form ref="accountForm" :model="accountForm" label-width="120px">
        <el-form-item label="监管账户开户行">
          <el-input v-model="accountForm.superviseBank" placeholder="请输入监管账户开户行" />
        </el-form-item>
        <el-form-item label="监管账户">
          <el-input v-model="accountForm.superviseAccount" placeholder="请输入监管账户" />
        </el-form-item>
        <el-form-item label="基本账户开户行">
          <el-input v-model="accountForm.basicBank" placeholder="请输入基本账户开户行" />
        </el-form-item>
        <el-form-item label="基本账户">
          <el-input v-model="accountForm.bankAccount" placeholder="请输入基本账户" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="accountEditOpen = false">取 消</el-button>
        <el-button type="primary" @click="submitAccountEdit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInstitution, getInstitution, approveInstitution, updateInstitution, addToBlacklist as addToBlacklistApi } from "@/api/supervision/institution";
import { getPublicityByInstitutionId } from "@/api/pension/publicityManage";

export default {
  name: "InstitutionQueryList",
  dicts: ['pension_institution_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 机构表格数据
      institutionList: [],
      // 弹出层标题
      title: "",
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示维护监管账号对话框
      accountEditOpen: false,
      // 当前标签页
      activeTab: 'basic',
      // 当前机构信息
      currentInstitution: {},
      // 公示信息
      publicityInfo: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        creditCode: null,
        status: null,
        hasSupervisionAccount: null
      },
      // 账户编辑表单
      accountForm: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询机构信息列表 */
    getList() {
      this.loading = true;
      listInstitution(this.queryParams).then(response => {
        this.institutionList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
      this.ids = selection.map(item => item.institutionId);
      this.multiple = !selection.length;
    },
    /** 查看详情操作 */
    handleDetail(row) {
      const institutionId = row.institutionId;
      getInstitution(institutionId).then(response => {
        this.currentInstitution = response.data;
        this.detailOpen = true;
        this.activeTab = 'basic';

        // 加载公示信息
        this.loadPublicityInfo(institutionId);
      });
    },
    /** 加载公示信息 */
    loadPublicityInfo(institutionId) {
      getPublicityByInstitutionId(institutionId).then(response => {
        if (response.data) {
          this.publicityInfo = response.data;
        } else {
          this.publicityInfo = null;
        }
      }).catch(() => {
        this.publicityInfo = null;
      });
    },
    /** 下拉菜单命令 */
    handleCommand(command, row) {
      switch (command) {
        case 'editAccount':
          this.handleEditAccount(row);
          break;
        case 'blacklist':
          this.handleAddToBlacklistSingle(row);
          break;
      }
    },
    /** 维护监管账号 */
    handleEditAccount(row) {
      this.accountForm = {
        institutionId: row.institutionId,
        superviseBank: row.superviseBank || '',
        superviseAccount: row.superviseAccount || '',
        basicBank: row.basicBank || '',
        bankAccount: row.bankAccount || ''
      };
      this.accountEditOpen = true;
    },
    /** 提交账户编辑 */
    submitAccountEdit() {
      updateInstitution(this.accountForm).then(() => {
        this.$modal.msgSuccess("账户信息修改成功");
        this.accountEditOpen = false;
        this.getList();
      });
    },
    /** 批量移入黑名单 */
    handleAddToBlacklist() {
      const institutionIds = this.ids;
      const institutionNames = this.institutionList.filter(item => institutionIds.includes(item.institutionId))
        .map(item => item.institutionName).join('、');

      this.$prompt('请输入移入黑名单的原因', '批量移入黑名单', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入黑名单原因...',
        inputValidator: (value) => {
          if (!value) {
            return '黑名单原因不能为空';
          }
          return true;
        }
      }).then(({ value }) => {
        return this.addToBlacklist(institutionIds, value);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("移入黑名单成功");
      }).catch(() => {});
    },
    /** 单个移入黑名单 */
    handleAddToBlacklistSingle(row) {
      this.$prompt('请输入将"' + row.institutionName + '"移入黑名单的原因', '移入黑名单', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入黑名单原因...',
        inputValidator: (value) => {
          if (!value) {
            return '黑名单原因不能为空';
          }
          return true;
        }
      }).then(({ value }) => {
        return this.addToBlacklist([row.institutionId], value);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("移入黑名单成功");
      }).catch(() => {});
    },
    /** 移入黑名单通用方法 */
    addToBlacklist(institutionIds, reason) {
      return addToBlacklistApi({
        institutionIds: institutionIds,
        reason: reason,
        blacklistType: '其他违规'
      });
    },
    /** 预警机构列表 */
    handleWarningInstitutions() {
      this.queryParams.status = '4';
      this.handleQuery();
    },
    /** 审批操作 */
    handleApprove() {
      const institutionIds = this.ids;
      if (institutionIds.length === 0) {
        this.$modal.msgWarning("请选择要审批的机构");
        return;
      }

      // 检查选中的机构状态
      const invalidInstitutions = this.institutionList.filter(item =>
        institutionIds.includes(item.institutionId) && item.status !== '0' && item.status !== '6'
      );

      if (invalidInstitutions.length > 0) {
        const names = invalidInstitutions.map(i => i.institutionName).join('、');
        this.$modal.msgWarning(`以下机构不是待审批或维护待审批状态，无法审批：${names}`);
        return;
      }

      // 检查是否包含维护待审批状态的机构
      const maintainInstitutions = this.institutionList.filter(item =>
        institutionIds.includes(item.institutionId) && item.status === '6'
      );

      let confirmMsg = '是否确认审批通过选中的机构？';
      if (maintainInstitutions.length > 0) {
        const maintainNames = maintainInstitutions.map(i => i.institutionName).join('、');
        confirmMsg = `检测到以下机构处于"维护待审批"状态：\n${maintainNames}\n\n审批通过后，机构信息将更新并恢复"正常运营"状态。\n\n是否继续？`;
      }

      this.$modal.confirm(confirmMsg).then(() => {
        return approveInstitution(institutionIds[0]);
      }).then(() => {
        // 如果是多个，逐个审批
        const promises = institutionIds.map(id => approveInstitution(id));
        return Promise.all(promises);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("审批成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supervision/institution/export', {
        ...this.queryParams
      }, `机构信息查询_${new Date().getTime()}.xlsx`)
    },
    /** 计算床位使用率 */
    getOccupancyRate(row) {
      if (!row.bedCount || row.bedCount === 0) return 0;
      const rate = Math.round((row.actualElders || 0) / row.bedCount * 100);
      return Math.min(rate, 100);
    },
    /** 获取床位使用率颜色 */
    getOccupancyColor(rate) {
      if (rate >= 95) return '#F56C6C';      // 红色 - 满员或超员
      else if (rate >= 80) return '#E6A23C'; // 橙色 - 高使用率
      else if (rate >= 50) return '#409EFF'; // 蓝色 - 中等使用率
      else return '#67C23A';                 // 绿色 - 低使用率
    },
    /** 格式化金额显示 */
    formatMoney(amount) {
      if (!amount) return '0.00';
      return Number(amount).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    },
    /** 格式化收住类型（从acceptElderType） */
    formatAcceptElderType(acceptElderType) {
      if (!acceptElderType) return '';
      // 判断是旧格式（英文代码）还是新格式（数字代码）
      const isNewFormat = /^\d+(,\d+)*$/.test(acceptElderType);
      if (isNewFormat) {
        // 新格式：使用数字代码映射
        const typeMap = {
          '1': '自理',
          '2': '半护��',
          '3': '全护理',
          '4': '失能',
          '5': '失智'
        };
        return acceptElderType.split(',').map(item => typeMap[item] || item).join('、');
      } else {
        // 旧格式：英文代码
        const typeMap = {
          'self_care': '自理',
          'semi_disabled': '半护理',
          'disabled': '失能',
          'dementia': '失智'
        };
        return acceptElderType.split(',').map(item => typeMap[item] || item).join('、');
      }
    },
    /** 格式化收住类型（从careLevels） */
    formatCareLevels(careLevels) {
      if (!careLevels) return '';
      const typeMap = {
        '1': '自理',
        '2': '半护理',
        '3': '全护理',
        '4': '失能',
        '5': '失智'
      };
      return careLevels.split(',').map(item => typeMap[item] || item).join('、');
    },
    /** 解析图片列表（逗号分隔） */
    getImageList(imageStr) {
      if (!imageStr) return [];
      return imageStr.split(',').map(img => this.processImageUrl(img));
    },
    /** 处理图片URL（添加服务器前缀） */
    processImageUrl(imgUrl) {
      if (!imgUrl) return '';
      // 如果已经是完整URL，直接返回
      if (imgUrl.startsWith('http') || imgUrl.startsWith('/profile')) {
        return process.env.VUE_APP_BASE_API + imgUrl;
      }
      return imgUrl;
    },
    /** 解析JSON数组 */
    parseJsonArray(jsonStr) {
      if (!jsonStr) return [];
      try {
        const parsed = JSON.parse(jsonStr);
        return Array.isArray(parsed) ? parsed : [];
      } catch (e) {
        return [];
      }
    }
  }
};
</script>

<style scoped>
/* 资金信息卡片样式 */
.financial-card {
  height: 120px;
}

.financial-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 20px;
}

.financial-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  margin-right: 20px;
  flex-shrink: 0;
}

.financial-icon.service {
  background: linear-gradient(135deg, #409EFF, #66B3FF);
}

.financial-icon.deposit {
  background: linear-gradient(135deg, #67C23A, #85CE61);
}

.financial-icon.member {
  background: linear-gradient(135deg, #E6A23C, #EEBE77);
}

.financial-info {
  flex: 1;
}

.financial-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.financial-amount {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.financial-desc {
  font-size: 12px;
  color: #C0C4CC;
}

/* 公示信息详情样式 */
.publicity-detail {
  padding: 10px 0;
}

.detail-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 5px;
}

.detail-section h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.detail-section p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.main-image-container {
  display: inline-block;
}

.image-gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.gallery-image {
  width: 120px;
  height: 120px;
  border-radius: 5px;
  overflow: hidden;
}

.facility-list {
  margin-top: 10px;
}

.service-schedule {
  margin-top: 10px;
}

.service-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.service-content {
  margin-left: 10px;
  color: #606266;
}
</style>