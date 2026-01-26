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
        <el-form-item label="VR全景图片">
          <image-upload v-model="form.vrImage" :limit="1" />
          <div style="color: #999; font-size: 12px; margin-top: 8px;">上传360°全景图片，最多1张</div>
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

        <!-- 设施图片上传 -->
        <el-divider content-position="left">设施图片</el-divider>

        <el-form-item label="房间设施图片">
          <image-upload v-model="form.roomFacilities" :limit="5" />
          <div style="color: #999; font-size: 12px;">上传房间设施图片（单人间、双人间、套间等），最多5张</div>
        </el-form-item>

        <el-form-item label="基础设施图片">
          <image-upload v-model="form.basicFacilities" :limit="5" />
          <div style="color: #999; font-size: 12px;">上传基础设施图片（建筑外观、公共区域、配套设施等），最多5张</div>
        </el-form-item>

        <el-form-item label="园址设施图片">
          <image-upload v-model="form.parkFacilities" :limit="5" />
          <div style="color: #999; font-size: 12px;">上传园址设施图片（花园、活动场地、休闲区等），最多5张</div>
        </el-form-item>

        <!-- 设施选项 -->
        <el-divider content-position="left">设施选项</el-divider>

        <el-form-item label="生活设施">
          <el-checkbox-group v-model="selectedLifeFacilities">
            <el-checkbox
              v-for="facility in lifeFacilities"
              :key="facility.id"
              :label="facility.facilityName">
              <svg-icon v-if="facility.iconName" :icon-class="facility.iconName" />
              {{ facility.facilityName }}
            </el-checkbox>
          </el-checkbox-group>
          <div style="color: #999; font-size: 12px;">请选择机构拥有的生活设施</div>
        </el-form-item>

        <el-form-item label="医疗设施">
          <el-checkbox-group v-model="selectedMedicalFacilities">
            <el-checkbox
              v-for="facility in medicalFacilities"
              :key="facility.id"
              :label="facility.facilityName">
              <svg-icon v-if="facility.iconName" :icon-class="facility.iconName" />
              {{ facility.facilityName }}
            </el-checkbox>
          </el-checkbox-group>
          <div style="color: #999; font-size: 12px;">请选择机构拥有的医疗设施</div>
        </el-form-item>

        <!-- 每日服务时间安排 -->
        <el-divider content-position="left">每日服务时间安排</el-divider>
        <el-form-item label="服务安排">
          <div style="margin-bottom: 15px;">
            <div v-for="(service, index) in dailyServices" :key="index" style="display: flex; align-items: center; margin-bottom: 10px;">
              <el-time-picker
                v-model="service.time"
                placeholder="选择时间"
                format="HH:mm"
                value-format="HH:mm"
                style="width: 120px; margin-right: 10px;"></el-time-picker>
              <el-input
                v-model="service.content"
                placeholder="请输入服务内容"
                style="flex: 1; margin-right: 10px;"></el-input>
              <el-button
                type="danger"
                icon="el-icon-delete"
                size="small"
                @click="removeService(index)">删除</el-button>
            </div>
          </div>
          <el-button
            type="primary"
            icon="el-icon-plus"
            @click="addService"
            size="small">添加服务项</el-button>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="info" icon="el-icon-view" @click="handlePreview">预 览</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="公示信息详情" :visible.sync="viewOpen" width="1200px" append-to-body>
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

        <!-- VR全景图片 -->
        <div class="detail-section" v-if="viewData.vrImage">
          <h4>VR全景图片</h4>
          <div class="main-image-container">
            <el-image
              :src="getImageUrl(viewData.vrImage)"
              :preview-src-list="[getImageUrl(viewData.vrImage)]"
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

        <!-- 设施图片 -->
        <div class="detail-section" v-if="viewData.roomFacilities && getImageList(viewData.roomFacilities).length > 0">
          <h4>房间设施图片</h4>
          <div class="image-gallery">
            <el-image
              v-for="(img, index) in getImageList(viewData.roomFacilities)"
              :key="index"
              :src="img"
              :preview-src-list="getImageList(viewData.roomFacilities)"
              fit="cover"
              class="gallery-image"
            ></el-image>
          </div>
        </div>

        <div class="detail-section" v-if="viewData.basicFacilities && getImageList(viewData.basicFacilities).length > 0">
          <h4>基础设施图片</h4>
          <div class="image-gallery">
            <el-image
              v-for="(img, index) in getImageList(viewData.basicFacilities)"
              :key="index"
              :src="img"
              :preview-src-list="getImageList(viewData.basicFacilities)"
              fit="cover"
              class="gallery-image"
            ></el-image>
          </div>
        </div>

        <div class="detail-section" v-if="viewData.parkFacilities && getImageList(viewData.parkFacilities).length > 0">
          <h4>园址设施图片</h4>
          <div class="image-gallery">
            <el-image
              v-for="(img, index) in getImageList(viewData.parkFacilities)"
              :key="index"
              :src="img"
              :preview-src-list="getImageList(viewData.parkFacilities)"
              fit="cover"
              class="gallery-image"
            ></el-image>
          </div>
        </div>

        <!-- 设施选项 -->
        <div class="detail-section" v-if="viewData.parsedLifeFacilities && viewData.parsedLifeFacilities.length > 0">
          <h4>生活设施</h4>
          <div class="facility-list">
            <el-tag
              v-for="(facility, index) in viewData.parsedLifeFacilities"
              :key="index"
              type="success"
              size="medium"
              style="margin: 2px 5px 2px 0;">
              {{ facility }}
            </el-tag>
          </div>
        </div>

        <div class="detail-section" v-if="viewData.parsedMedicalFacilities && viewData.parsedMedicalFacilities.length > 0">
          <h4>医疗设施</h4>
          <div class="facility-list">
            <el-tag
              v-for="(facility, index) in viewData.parsedMedicalFacilities"
              :key="index"
              type="primary"
              size="medium"
              style="margin: 2px 5px 2px 0;">
              {{ facility }}
            </el-tag>
          </div>
        </div>

        <!-- 每日服务时间安排 -->
        <div class="detail-section" v-if="viewData.parsedDailyServices && viewData.parsedDailyServices.length > 0">
          <h4>每日服务时间安排</h4>
          <div class="service-schedule">
            <div v-for="(service, index) in viewData.parsedDailyServices" :key="index" class="service-item">
              <el-tag type="info" size="small">{{ service.time }}</el-tag>
              <span class="service-content">{{ service.content }}</span>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog title="公示信息预览" :visible.sync="previewOpen" width="1200px" append-to-body>
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

        <!-- 费用信息 -->
        <el-descriptions title="费用信息(元/月)" :column="2" border style="margin-top: 20px;">
          <el-descriptions-item label="护理费">
            <span v-if="form.nursingFeeMin != null && form.nursingFeeMax != null">
              {{ form.nursingFeeMin }} - {{ form.nursingFeeMax }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="床位费">
            <span v-if="form.bedFeeMin != null && form.bedFeeMax != null">
              {{ form.bedFeeMin }} - {{ form.bedFeeMax }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="膳食费">
            <span v-if="form.mealFeeMin != null && form.mealFeeMax != null">
              {{ form.mealFeeMin }} - {{ form.mealFeeMax }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="总费用">
            <span v-if="form.nursingFeeMin != null && form.nursingFeeMax != null &&
                      form.bedFeeMin != null && form.bedFeeMax != null &&
                      form.mealFeeMin != null && form.mealFeeMax != null">
              {{ totalFeeMin }} - {{ totalFeeMax }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
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

        <!-- 机构主图 -->
        <div class="detail-section" v-if="form.mainPicture">
          <h4>机构主图</h4>
          <div class="main-image-container">
            <el-image
              :src="getImageUrl(form.mainPicture)"
              :preview-src-list="[getImageUrl(form.mainPicture)]"
              fit="cover"
              style="width: 300px; height: 200px; border-radius: 5px;"
            ></el-image>
          </div>
        </div>

        <!-- VR全景图片 -->
        <div class="detail-section" v-if="form.vrImage">
          <h4>VR全景图片</h4>
          <div class="main-image-container">
            <el-image
              :src="getImageUrl(form.vrImage)"
              :preview-src-list="[getImageUrl(form.vrImage)]"
              fit="cover"
              style="width: 300px; height: 200px; border-radius: 5px;"
            ></el-image>
          </div>
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

        <!-- 设施图片 -->
        <div class="detail-section" v-if="form.roomFacilities && getImageList(form.roomFacilities).length > 0">
          <h4>房间设施图片</h4>
          <div class="image-gallery">
            <el-image
              v-for="(img, index) in getImageList(form.roomFacilities)"
              :key="index"
              :src="img"
              :preview-src-list="getImageList(form.roomFacilities)"
              fit="cover"
              class="gallery-image"
            ></el-image>
          </div>
        </div>

        <div class="detail-section" v-if="form.basicFacilities && getImageList(form.basicFacilities).length > 0">
          <h4>基础设施图片</h4>
          <div class="image-gallery">
            <el-image
              v-for="(img, index) in getImageList(form.basicFacilities)"
              :key="index"
              :src="img"
              :preview-src-list="getImageList(form.basicFacilities)"
              fit="cover"
              class="gallery-image"
            ></el-image>
          </div>
        </div>

        <div class="detail-section" v-if="form.parkFacilities && getImageList(form.parkFacilities).length > 0">
          <h4>园址设施图片</h4>
          <div class="image-gallery">
            <el-image
              v-for="(img, index) in getImageList(form.parkFacilities)"
              :key="index"
              :src="img"
              :preview-src-list="getImageList(form.parkFacilities)"
              fit="cover"
              class="gallery-image"
            ></el-image>
          </div>
        </div>

        <!-- 设施选项 -->
        <div class="detail-section" v-if="selectedLifeFacilities && selectedLifeFacilities.length > 0">
          <h4>生活设施</h4>
          <div class="facility-list">
            <el-tag
              v-for="(facility, index) in selectedLifeFacilities"
              :key="index"
              type="success"
              size="medium"
              style="margin: 2px 5px 2px 0;">
              {{ facility }}
            </el-tag>
          </div>
        </div>

        <div class="detail-section" v-if="selectedMedicalFacilities && selectedMedicalFacilities.length > 0">
          <h4>医疗设施</h4>
          <div class="facility-list">
            <el-tag
              v-for="(facility, index) in selectedMedicalFacilities"
              :key="index"
              type="primary"
              size="medium"
              style="margin: 2px 5px 2px 0;">
              {{ facility }}
            </el-tag>
          </div>
        </div>

        <!-- 每日服务时间安排 -->
        <div class="detail-section" v-if="dailyServices && dailyServices.filter(service => service.time && service.content).length > 0">
          <h4>每日服务时间安排</h4>
          <div class="service-schedule">
            <div v-for="(service, index) in dailyServices.filter(service => service.time && service.content)" :key="index" class="service-item">
              <el-tag type="info" size="small">{{ service.time }}</el-tag>
              <span class="service-content">{{ service.content }}</span>
            </div>
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
import { getLifeFacilities, getMedicalFacilities } from '@/api/pension/facility/icon';
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
      // 新增设施数据
      selectedLifeFacilities: [],
      selectedMedicalFacilities: [],
      dailyServices: [],
      // 设施图标配置数据
      lifeFacilities: [],
      medicalFacilities: [],
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
  mounted() {
    this.loadFacilityIconConfig();
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
        vrImage: null,
        // 设施图片字段
        roomFacilities: null,
        basicFacilities: null,
        parkFacilities: null,
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
      this.selectedLifeFacilities = [];
      this.selectedMedicalFacilities = [];
      this.dailyServices = [];
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
        // 解析生活设施选择
        if (this.form.lifeFacilities) {
          try {
            this.selectedLifeFacilities = JSON.parse(this.form.lifeFacilities);
          } catch (e) {
            this.selectedLifeFacilities = [];
          }
        }
        // 解析医疗设施选择
        if (this.form.medicalFacilities) {
          try {
            this.selectedMedicalFacilities = JSON.parse(this.form.medicalFacilities);
          } catch (e) {
            this.selectedMedicalFacilities = [];
          }
        }
        // 解析每日服务
        if (this.form.dailyServices) {
          try {
            this.dailyServices = JSON.parse(this.form.dailyServices);
          } catch (e) {
            this.dailyServices = [];
          }
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

        // 调试信息：打印原始数据
        console.log("查看对话框原始数据:", this.viewData);

        // 为查看对话框解析设施数据（因为查��对话框中显示的是原始数据，需要处理）
        // 生活设施数据处理
        if (this.viewData.lifeFacilities) {
          try {
            this.viewData.parsedLifeFacilities = JSON.parse(this.viewData.lifeFacilities);
            console.log("生活设施解析结果:", this.viewData.parsedLifeFacilities);
          } catch (e) {
            this.viewData.parsedLifeFacilities = [];
            console.error("生活设施解析失败:", e);
          }
        } else {
          this.viewData.parsedLifeFacilities = [];
        }

        // 医疗设施数据处理
        if (this.viewData.medicalFacilities) {
          try {
            this.viewData.parsedMedicalFacilities = JSON.parse(this.viewData.medicalFacilities);
            console.log("医疗设施解析结果:", this.viewData.parsedMedicalFacilities);
          } catch (e) {
            this.viewData.parsedMedicalFacilities = [];
            console.error("医疗设施解析失败:", e);
          }
        } else {
          this.viewData.parsedMedicalFacilities = [];
        }

        // 每日服务数据处理
        if (this.viewData.dailyServices) {
          try {
            this.viewData.parsedDailyServices = JSON.parse(this.viewData.dailyServices);
            console.log("每日服务解析结果:", this.viewData.parsedDailyServices);
          } catch (e) {
            this.viewData.parsedDailyServices = [];
            console.error("每日服务解析失败:", e);
          }
        } else {
          this.viewData.parsedDailyServices = [];
        }

        // 调试信息：打印图片数据
        console.log("房间设施图片:", this.viewData.roomFacilities);
        console.log("基础设施图片:", this.viewData.basicFacilities);
        console.log("园址设施图片:", this.viewData.parkFacilities);

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

          // 将设施选择数组转为JSON字符串
          this.form.lifeFacilities = this.selectedLifeFacilities.length > 0 ? JSON.stringify(this.selectedLifeFacilities) : null;
          this.form.medicalFacilities = this.selectedMedicalFacilities.length > 0 ? JSON.stringify(this.selectedMedicalFacilities) : null;

          // 将每日服务数组转为JSON字符串
          this.form.dailyServices = this.dailyServices.length > 0 ? JSON.stringify(this.dailyServices.filter(service => service.time && service.content)) : null;

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
    },

    // 添加每日服务项
    addService() {
      this.dailyServices.push({ time: '', content: '' });
    },

    // 删除每日服务项
    removeService(index) {
      this.dailyServices.splice(index, 1);
    },

    // 加载设施图标配置
    loadFacilityIconConfig() {
      console.log('开始加载设施图标配置')

      // 并行加载生活和医疗设施
      Promise.all([
        getLifeFacilities(),
        getMedicalFacilities()
      ]).then(([lifeResponse, medicalResponse]) => {
        console.log('生活设施API响应:', lifeResponse)
        console.log('医疗设施API响应:', medicalResponse)

        if (lifeResponse.code === 200) {
          this.lifeFacilities = lifeResponse.data || []
          console.log('生活设施数量:', this.lifeFacilities.length)
          console.log('生活设施详情:', this.lifeFacilities)
        } else {
          this.lifeFacilities = []
          console.error('获取生活设施失败:', lifeResponse.msg)
        }

        if (medicalResponse.code === 200) {
          this.medicalFacilities = medicalResponse.data || []
          console.log('医疗设施数量:', this.medicalFacilities.length)
          console.log('医疗设施详情:', this.medicalFacilities)
        } else {
          this.medicalFacilities = []
          console.error('获取医疗设施失败:', medicalResponse.msg)
        }
      }).catch(error => {
        console.error('加载设施图标配置失败:', error)
        this.$message.error('加载设施图标配置失败')
        this.lifeFacilities = []
        this.medicalFacilities = []
      })
    }
  }
};
</script>

<style scoped>
.publicity-detail,
.publicity-preview {
  max-height: 700px;
  overflow-y: auto;
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
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
  margin-bottom: 16px;
  padding: 14px 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.25);
}

.detail-header h2,
.preview-header h2 {
  margin: 0;
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
}

.detail-section {
  margin-top: 16px;
  padding: 16px;
  background: #ffffff;
  border-radius: 8px;
  border-left: 3px solid #667eea;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.detail-section:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.detail-section h4 {
  margin: 0 0 10px 0;
  color: #667eea;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.detail-section h4::before {
  content: '';
  width: 3px;
  height: 14px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  margin-right: 6px;
}

.detail-section p {
  margin: 0;
  color: #303133;
  line-height: 1.6;
  font-size: 13px;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 描述列表样式优化 */
.publicity-detail >>> .el-descriptions,
.publicity-preview >>> .el-descriptions {
  background: #ffffff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 12px;
}

.publicity-detail >>> .el-descriptions__body,
.publicity-preview >>> .el-descriptions__body {
  padding: 0;
}

.publicity-detail >>> .el-descriptions-item,
.publicity-preview >>> .el-descriptions-item {
  padding: 8px 12px !important;
}

.publicity-detail >>> .el-descriptions-item__label,
.publicity-preview >>> .el-descriptions-item__label {
  padding: 8px 12px !important;
}

.publicity-detail >>> .el-descriptions-item__content,
.publicity-preview >>> .el-descriptions-item__content {
  padding: 8px 12px !important;
}

.publicity-detail >>> .el-descriptions__title,
.publicity-preview >>> .el-descriptions__title {
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
  margin-bottom: 10px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
}

.publicity-detail >>> .el-descriptions-item__label,
.publicity-preview >>> .el-descriptions-item__label {
  font-weight: 600;
  background-color: #f5f7fa;
  color: #606266;
}

.publicity-detail >>> .el-descriptions-item__content,
.publicity-preview >>> .el-descriptions-item__content {
  color: #303133;
}

/* 图片画廊样式 */
.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 10px;
}

.gallery-image {
  width: 100%;
  height: 140px;
  border-radius: 8px;
  background-color: #ebeef5;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
}

.gallery-image:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.gallery-image >>> .el-image {
  width: 100%;
  height: 100%;
}

.gallery-image >>> .el-image__inner {
  width: 100%;
  height: 100%;
}

.main-image-container {
  margin-bottom: 15px;
  text-align: center;
}

.main-image-container >>> .el-image {
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
}

.main-image-container >>> .el-image:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
}

/* 设施列表样式 */
.facility-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.facility-list >>> .el-tag {
  padding: 8px 15px;
  border-radius: 20px;
  font-size: 13px;
}

/* 服务时间安排样式 */
.service-schedule {
  margin-top: 8px;
}

.service-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
  border-radius: 6px;
  border-left: 3px solid #667eea;
  transition: all 0.3s ease;
}

.service-item:hover {
  background: linear-gradient(135deg, #e8eef5 0%, #d5d9e8 100%);
  transform: translateX(3px);
}

.service-content {
  margin-left: 8px;
  font-size: 13px;
  color: #333;
  flex: 1;
}

/* 滚动条美化 */
.publicity-detail::-webkit-scrollbar,
.publicity-preview::-webkit-scrollbar {
  width: 8px;
}

.publicity-detail::-webkit-scrollbar-track,
.publicity-preview::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.publicity-detail::-webkit-scrollbar-thumb,
.publicity-preview::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
}

.publicity-detail::-webkit-scrollbar-thumb:hover,
.publicity-preview::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #5568d3 0%, #667eea 100%);
}
</style>
