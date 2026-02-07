<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="入住人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入入住人姓名"
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
      <el-form-item label="房间号" prop="bedInfo">
        <el-input
          v-model="queryParams.bedInfo"
          placeholder="请输入房间号或床位号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属机构" prop="institutionId">
        <el-select v-model="queryParams.institutionId" placeholder="请选择养老机构" clearable @change="handleQuery">
          <el-option label="全部机构" :value="null" />
          <el-option
            v-for="inst in institutionList"
            :key="inst.institutionId"
            :label="inst.institutionName"
            :value="inst.institutionId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="入住状态" prop="checkInStatus">
        <el-select v-model="queryParams.checkInStatus" placeholder="请选择入住状态" clearable>
          <el-option label="待入住" value="0" />
          <el-option label="已入住" value="1" />
          <el-option label="已退住" value="2" />
          <el-option label="请假中" value="3" />
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['elder:resident:add']"
        >新增入驻</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['elder:resident:import']"
        >批量导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['elder:resident:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 入住人列表表格 -->
    <el-table v-loading="loading" :data="residentList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="入住人姓名" align="center" prop="elderName" width="100" />
      <el-table-column label="性别" align="center" prop="gender" width="60">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.elder_gender" :value="scope.row.gender"/>
        </template>
      </el-table-column>
      <el-table-column label="年龄" align="center" prop="age" width="60" />
      <el-table-column label="身份证号" align="center" prop="idCard" width="180" />
      <el-table-column label="联系电话" align="center" prop="phone" width="120" />
      <el-table-column label="房间号-床位号" align="center" prop="bedInfo" width="120" />
      <el-table-column label="所属机构" align="center" prop="institutionName" width="150" show-overflow-tooltip />
      <el-table-column label="入住状态" align="center" prop="checkInStatus" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.checkInStatus === '0'" type="warning">待入住</el-tag>
          <el-tag v-else-if="scope.row.checkInStatus === '1'" type="success">已入住</el-tag>
          <el-tag v-else-if="scope.row.checkInStatus === '2'" type="info">已退住</el-tag>
          <el-tag v-else-if="scope.row.checkInStatus === '3'" type="">请假中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="护理等级" align="center" prop="careLevel" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.elder_care_level" :value="scope.row.careLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="服务费余额" align="center" prop="serviceBalance" width="120">
        <template slot-scope="scope">
          <span :class="getBalanceClass(scope.row.serviceBalance)">
            ￥{{ formatMoney(scope.row.serviceBalance) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="押金余额" align="center" prop="depositBalance" width="120">
        <template slot-scope="scope">
          <span :class="getBalanceClass(scope.row.depositBalance)">
            ￥{{ formatMoney(scope.row.depositBalance) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="会员余额" align="center" prop="memberBalance" width="120">
        <template slot-scope="scope">
          <span :class="getBalanceClass(scope.row.memberBalance)">
            ￥{{ formatMoney(scope.row.memberBalance) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="入住日期" align="center" prop="checkInDate" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.checkInDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="到期日期" align="center" prop="dueDate" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.dueDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="紧急联系人" align="center" prop="emergencyContact" width="100" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="300">
        <template slot-scope="scope">
          <!-- 去支付:有未支付订单时显示 -->
          <el-button
            v-if="scope.row.hasUnpaidOrder"
            size="mini"
            type="text"
            icon="el-icon-wallet"
            style="color: #E6A23C; font-weight: bold;"
            @click="handlePayment(scope.row)"
            v-hasPermi="['elder:resident:payment']"
          >去支付</el-button>
          <!-- 详情:所有人都能查看 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['elder:resident:query']"
          >详情</el-button>
          <!-- 维护:所有人都能维护信息 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['elder:resident:edit']"
          >维护</el-button>
          <!-- 续费:没有未支付订单才显示(即至少支付过一次) -->
          <el-button
            v-if="!scope.row.hasUnpaidOrder"
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleRenew(scope.row)"
            v-hasPermi="['elder:resident:renew']"
          >续费</el-button>
          <!-- 退费:有任意余额才显示 -->
          <el-button
            v-if="scope.row.serviceBalance > 0 || scope.row.depositBalance > 0 || scope.row.memberBalance > 0"
            size="mini"
            type="text"
            icon="el-icon-minus"
            @click="handleRefund(scope.row)"
            v-hasPermi="['elder:resident:refund']"
          >退费</el-button>
          <!-- 押金使用:有押金余额才显示 -->
          <el-button
            v-if="scope.row.depositBalance > 0"
            size="mini"
            type="text"
            icon="el-icon-coin"
            @click="handleDepositUse(scope.row)"
            v-hasPermi="['elder:resident:deposit']"
          >押金使用</el-button>
          <!-- 家属管理:新增功能 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-user"
            @click="handleFamilyManage(scope.row)"
            v-hasPermi="['elder:resident:query']"
          >家属管理</el-button>
          <!-- 设置密码:新增功能 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-key"
            @click="handleSetPassword(scope.row)"
            v-hasPermi="['elder:resident:edit']"
          >设置密码</el-button>
          <!-- 拨付单详情 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-finance"
            @click="handleTransferDetail(scope.row)"
            v-hasPermi="['elder:resident:query']"
          >拨付单</el-button>
          <!-- 删除:所有人都能删除 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['elder:resident:remove']"
          >删除</el-button>
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

    <!-- 入住人详情对话框 -->
    <el-dialog title="入住人详情" :visible.sync="detailOpen" width="1200px" append-to-body>
      <div class="resident-detail">
        <!-- 照片信息 -->
        <div v-if="residentDetail.photoPath || residentDetail.idCardFrontPath || residentDetail.idCardBackPath" style="margin-bottom: 20px;">
          <h4 style="margin-bottom: 10px; color: #303133;">
            <i class="el-icon-picture-outline"></i> 照片信息
          </h4>
          <el-row :gutter="20">
            <el-col :span="8" v-if="residentDetail.photoPath">
              <div class="photo-item">
                <div class="photo-label">老人照片</div>
                <el-image
                  :src="getImageUrl(residentDetail.photoPath)"
                  :preview-src-list="[getImageUrl(residentDetail.photoPath)]"
                  fit="cover"
                  style="width: 200px; height: 200px; border-radius: 4px; cursor: pointer;"
                >
                  <div slot="error" class="image-slot">
                    <i class="el-icon-picture-outline" style="font-size: 50px; color: #C0C4CC;"></i>
                  </div>
                </el-image>
              </div>
            </el-col>
            <el-col :span="8" v-if="residentDetail.idCardFrontPath">
              <div class="photo-item">
                <div class="photo-label">身份证正面</div>
                <el-image
                  :src="getImageUrl(residentDetail.idCardFrontPath)"
                  :preview-src-list="[getImageUrl(residentDetail.idCardFrontPath)]"
                  fit="cover"
                  style="width: 200px; height: 200px; border-radius: 4px; cursor: pointer;"
                >
                  <div slot="error" class="image-slot">
                    <i class="el-icon-picture-outline" style="font-size: 50px; color: #C0C4CC;"></i>
                  </div>
                </el-image>
              </div>
            </el-col>
            <el-col :span="8" v-if="residentDetail.idCardBackPath">
              <div class="photo-item">
                <div class="photo-label">身份证反面</div>
                <el-image
                  :src="getImageUrl(residentDetail.idCardBackPath)"
                  :preview-src-list="[getImageUrl(residentDetail.idCardBackPath)]"
                  fit="cover"
                  style="width: 200px; height: 200px; border-radius: 4px; cursor: pointer;"
                >
                  <div slot="error" class="image-slot">
                    <i class="el-icon-picture-outline" style="font-size: 50px; color: #C0C4CC;"></i>
                  </div>
                </el-image>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 基本信息 -->
        <el-descriptions title="基本信息" :column="3" border>
          <el-descriptions-item label="姓名">{{ residentDetail.elderName }}</el-descriptions-item>
          <el-descriptions-item label="性别">
            <dict-tag :options="dict.type.elder_gender" :value="residentDetail.gender"/>
          </el-descriptions-item>
          <el-descriptions-item label="年龄">{{ residentDetail.age }}岁</el-descriptions-item>
          <el-descriptions-item label="所属机构" :span="2">
            <el-tag type="success">{{ residentDetail.institutionName || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ residentDetail.idCard }}</el-descriptions-item>
          <el-descriptions-item label="出生日期">{{ residentDetail.birthDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ residentDetail.phone }}</el-descriptions-item>
          <el-descriptions-item label="护理等级">
            <dict-tag :options="dict.type.elder_care_level" :value="residentDetail.careLevel"/>
          </el-descriptions-item>
          <el-descriptions-item label="健康状况">{{ residentDetail.healthStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="家庭住址" :span="3">{{ residentDetail.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="特殊需求" :span="3">{{ residentDetail.specialNeeds || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 床位信息 -->
        <el-descriptions title="床位信息" :column="3" border style="margin-top: 20px;">
          <el-descriptions-item label="房间床位">{{ residentDetail.bedInfo }}</el-descriptions-item>
          <el-descriptions-item label="入住日期">{{ parseTime(residentDetail.checkInDate, '{y}-{m}-{d}') }}</el-descriptions-item>
          <el-descriptions-item label="到期日期">{{ parseTime(residentDetail.dueDate, '{y}-{m}-{d}') }}</el-descriptions-item>
          <el-descriptions-item label="床位费">
            <span style="color: #409EFF; font-weight: bold;">￥{{ getBedFee() }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="护理费">
            <span style="color: #67C23A; font-weight: bold;">￥{{ getCareFee() }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="餐费">
            <span style="color: #F56C6C; font-weight: bold;">￥{{ getMealFee() }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="服务费(床位费+护理费+餐费)">
            <span style="color: #E6A23C; font-weight: bold;">￥{{ getServiceFee() }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 紧急联系人 -->
        <el-descriptions title="紧急联系人" :column="2" border style="margin-top: 20px;">
          <el-descriptions-item label="联系人姓名">{{ residentDetail.emergencyContact || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ residentDetail.emergencyPhone || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 账户余额 -->
        <el-descriptions title="账户余额" :column="3" border style="margin-top: 20px;">
          <el-descriptions-item label="服务费余额">
            <span class="balance-text" :class="getBalanceClass(residentDetail.serviceBalance)">
              ￥{{ formatMoney(residentDetail.serviceBalance) }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="押金余额">
            <span class="balance-text" :class="getBalanceClass(residentDetail.depositBalance)">
              ￥{{ formatMoney(residentDetail.depositBalance) }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="会员余额">
            <span class="balance-text" :class="getBalanceClass(residentDetail.memberBalance)">
              ￥{{ formatMoney(residentDetail.memberBalance) }}
            </span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 备注信息 -->
        <el-descriptions v-if="residentDetail.remark" title="备注信息" :column="1" border style="margin-top: 20px;">
          <el-descriptions-item label="备注">{{ residentDetail.remark }}</el-descriptions-item>
        </el-descriptions>

        <!-- 订单记录 -->
        <div style="margin-top: 20px;">
          <h4 style="margin-bottom: 10px; color: #303133;">
            <i class="el-icon-document"></i> 订单记录
            <span style="font-size: 12px; color: #909399; font-weight: normal;">(共{{ (residentDetail.orders || []).length }}条)</span>
          </h4>
          <el-table :data="residentDetail.orders || []" border style="width: 100%" max-height="300" :expand-row-keys="expandedOrderKeys" @expand-change="handleOrderExpand" row-key="orderId">
            <el-table-column type="expand">
              <template slot-scope="scope">
                <div style="padding: 10px; background-color: #f9f9f9;">
                  <!-- 价格变更汇总 -->
                  <div v-if="getOrderPriceModified(scope.row).length > 0" style="margin-bottom: 15px; padding: 10px; background-color: #fff7e6; border: 1px solid #ffd591; border-radius: 4px">
                    <div style="color: #e6a23c; font-weight: bold; margin-bottom: 5px">
                      <i class="el-icon-warning"></i> 价格变更记录
                    </div>
                    <div v-for="item in getOrderPriceModified(scope.row)" :key="item.itemId" style="font-size: 12px; color: #606266; margin-bottom: 3px">
                      {{ item.itemName }}：¥{{ item.originalUnitPrice }} → ¥{{ item.unitPrice }}
                      <span style="color: #f56c6c; margin-left: 10px">
                        差额：{{ (item.unitPrice - item.originalUnitPrice) >= 0 ? '+' : '' }}{{ (item.unitPrice - item.originalUnitPrice).toFixed(2) }}元
                      </span>
                    </div>
                  </div>
                  <!-- 订单明细表格 -->
                  <el-table :data="scope.row.orderItems || []" size="small" border>
                    <el-table-column prop="itemName" label="项目名称" width="120" />
                    <el-table-column label="单价" width="150">
                      <template slot-scope="itemScope">
                        <div v-if="itemScope.row.isPriceModified === '1' && itemScope.row.originalUnitPrice">
                          <span style="color: #909399; text-decoration: line-through; font-size: 12px;">¥{{ itemScope.row.originalUnitPrice }}</span>
                          <span style="color: #E6A23C; font-weight: bold; margin: 0 4px;">→</span>
                          <span style="color: #ee0a24; font-weight: bold;">¥{{ itemScope.row.unitPrice }}</span>
                          <el-tag type="warning" size="mini" style="margin-left: 5px">已修改</el-tag>
                        </div>
                        <div v-else>
                          <span style="color: #E6A23C; font-weight: bold;">¥{{ itemScope.row.unitPrice }}</span>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column prop="quantity" label="数量" width="60" align="center" />
                    <el-table-column prop="totalAmount" label="小计" width="100">
                      <template slot-scope="itemScope">
                        <span style="color: #E6A23C; font-weight: bold;">¥{{ formatMoney(itemScope.row.totalAmount) }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="servicePeriod" label="服务周期" width="100" />
                    <el-table-column prop="itemDescription" label="描述" show-overflow-tooltip />
                  </el-table>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="orderNo" label="订单号" width="170"></el-table-column>
            <el-table-column label="订单类型" width="90">
              <template slot-scope="scope">
                <el-tag v-if="scope.row && scope.row.orderType === '1'" type="success">入驻</el-tag>
                <el-tag v-else-if="scope.row && scope.row.orderType === '2'" type="primary">续费</el-tag>
                <el-tag v-else>其他</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="originalAmount" label="应收金额" width="110">
              <template slot-scope="scope">
                <span style="color: #E6A23C; font-weight: bold;">￥{{ formatMoney(scope.row.originalAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="orderAmount" label="实收金额" width="110">
              <template slot-scope="scope">
                <span style="color: #67C23A; font-weight: bold;">￥{{ formatMoney(scope.row.orderAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="discountAmount" label="优惠金额" width="100">
              <template slot-scope="scope">
                <span v-if="scope.row.discountAmount && scope.row.discountAmount > 0" style="color: #F56C6C; font-weight: bold;">
                  -￥{{ formatMoney(scope.row.discountAmount) }}
                </span>
                <span v-else style="color: #909399;">-</span>
              </template>
            </el-table-column>
            <el-table-column label="到期日期" width="150">
              <template slot-scope="scope">
                <div v-if="scope.row.serviceEndDate">
                  <div style="color: #409EFF; font-weight: bold;">
                    {{ parseTime(scope.row.serviceEndDate, '{y}-{m}-{d}') }}
                  </div>
                  <div v-if="scope.row.orderType === '2' && scope.row.monthCount && scope.row.monthCount > 0" style="font-size: 12px; color: #67C23A;">
                    <i class="el-icon-top"></i> 延长{{ scope.row.monthCount }}个月
                  </div>
                </div>
                <span v-else style="color: #909399;">-</span>
              </template>
            </el-table-column>
            <el-table-column label="订单状态" width="90">
              <template slot-scope="scope">
                <el-tag v-if="scope.row && scope.row.orderStatus === '0'" type="warning">未支付</el-tag>
                <el-tag v-else-if="scope.row && scope.row.orderStatus === '1'" type="success">已支付</el-tag>
                <el-tag v-else-if="scope.row && scope.row.orderStatus === '2'" type="info">已取消</el-tag>
                <el-tag v-else-if="scope.row && scope.row.orderStatus === '3'" type="danger">已退款</el-tag>
                <el-tag v-else-if="scope.row && scope.row.orderStatus === '4'" type="primary">待审核</el-tag>
                <el-tag v-else-if="scope.row && scope.row.orderStatus === '5'" type="warning">待付款</el-tag>
                <el-tag v-else type="info">-</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="paymentMethod" label="支付方式" width="90">
              <template slot-scope="scope">
                <span v-if="scope.row && scope.row.paymentMethod === 'cash'">现金</span>
                <span v-else-if="scope.row && scope.row.paymentMethod === 'card'">刷卡</span>
                <span v-else-if="scope.row && scope.row.paymentMethod === 'scan'">扫码</span>
                <span v-else-if="scope.row && scope.row.paymentMethod === 'later'">稍后支付</span>
                <span v-else>{{ (scope.row && scope.row.paymentMethod) || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="orderDate" label="订单日期" width="105">
              <template slot-scope="scope">
                {{ parseTime(scope.row.orderDate, '{y}-{m}-{d}') }}
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip></el-table-column>
          </el-table>
        </div>

        <!-- 系统信息 -->
        <el-descriptions title="系统信息" :column="2" border style="margin-top: 20px;">
          <el-descriptions-item label="创建时间">{{ parseTime(residentDetail.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ parseTime(residentDetail.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 续费对话框 -->
    <el-dialog title="入住人续费" :visible.sync="renewOpen" width="800px" append-to-body>
      <el-form ref="renewForm" :model="renewForm" :rules="renewRules" label-width="120px">
        <!-- 基本信息展示 -->
        <el-divider content-position="left">
          <i class="el-icon-user"></i> 入住人信息
        </el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="入住人">
              <el-input v-model="renewForm.elderName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="当前床位">
              <el-input v-model="renewForm.bedInfo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="当前月服务费">
              <el-input :value="'¥' + formatMoney(renewForm.monthlyFee)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="服务费余额">
              <el-input :value="'¥' + formatMoney(renewForm.serviceBalance)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="押金余额">
              <el-input :value="'¥' + formatMoney(renewForm.depositBalance)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="会员余额">
              <el-input :value="'¥' + formatMoney(renewForm.memberBalance)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住日期">
              <el-input :value="parseTime(renewForm.checkInDate, '{y}-{m}-{d}')" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前到期日期">
              <el-input :value="parseTime(renewForm.currentDueDate, '{y}-{m}-{d}')" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 费用设置 -->
        <el-divider content-position="left">
          <i class="el-icon-wallet"></i> 费用设置
        </el-divider>

        <!-- 当前价格展示卡片 -->
        <el-card shadow="never" style="margin-bottom: 15px; background: #f5f7fa;">
          <div slot="header" style="font-size: 14px; color: #606266; padding: 0;">
            <i class="el-icon-price-tag"></i> 当前价格（元/月）
          </div>
          <el-row :gutter="16">
            <el-col :span="8">
              <div class="fee-item">
                <div class="fee-label">床位费</div>
                <div class="fee-value">¥{{ formatMoney(renewForm.bedFee) }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="fee-item">
                <div class="fee-label">护理费</div>
                <div class="fee-value">¥{{ formatMoney(renewForm.careFee) }}</div>
                <div class="fee-extra" v-if="renewForm.careLevelText">{{ renewForm.careLevelText }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="fee-item">
                <div class="fee-label">餐费</div>
                <div class="fee-value" style="color: #F56C6C;">¥{{ formatMoney(renewForm.mealFee || 0) }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="fee-item">
                <div class="fee-label">月服务费</div>
                <div class="fee-value primary">¥{{ formatMoney(renewForm.monthlyFee) }}</div>
                <div class="fee-extra">床位费 + 护理费 + 餐费</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 续费配置 -->
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="续费月数" prop="monthCount">
              <el-input-number
                v-model="renewForm.monthCount"
                :min="0"
                :max="120"
                :precision="0"
                style="width: 160px;"
                @change="calculateRenewTotal" />
              <span style="margin-left: 8px; color: #909399;">个月</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务费小计">
              <span style="font-size: 16px; font-weight: bold; color: #409EFF;">¥{{ formatMoney(renewServiceFeeTotal) }}</span>
              <span style="margin-left: 8px; color: #909399; font-size: 12px;">
                (¥{{ formatMoney(renewForm.monthlyFee) }} × {{ renewForm.monthCount }}个月)
              </span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="补交押金" prop="depositAmount">
              <el-input-number
                v-model="renewForm.depositAmount"
                :min="0"
                :precision="2"
                style="width: 160px;"
                @change="calculateRenewTotal" />
              <span style="margin-left: 8px; color: #909399;">元</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="补交会员费" prop="memberFee">
              <el-input-number
                v-model="renewForm.memberFee"
                :min="0"
                :precision="2"
                style="width: 160px;"
                @change="calculateRenewTotal" />
              <span style="margin-left: 8px; color: #909399;">元</span>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 价格变更记录 -->
        <el-alert
          v-if="hasPriceModified"
          type="warning"
          :closable="false"
          style="margin-bottom: 15px;">
          <template slot="title">
            <i class="el-icon-warning"></i> 价格变更记录
            <span style="margin-left: 10px; font-size: 12px; opacity: 0.8;">最近支付时间: {{ currentPrice.lastPaymentTime || '无' }}</span>
          </template>
          <div slot="default" style="margin-top: 10px;">
            <!-- 第一行 -->
            <el-row :gutter="10" style="margin-bottom: 8px;">
              <el-col :span="12" v-if="currentPrice.bedFeeModified">
                <div class="price-change-item">
                  <span class="price-label">床位费</span>
                  <span class="price-old">¥{{ formatMoney(currentPrice.bedFeeOriginal) }}</span>
                  <i class="el-icon-right" style="margin: 0 6px;"></i>
                  <span class="price-new">¥{{ formatMoney(currentPrice.bedFee) }}</span>
                </div>
              </el-col>
              <el-col :span="12" v-if="currentPrice.careFeeModified">
                <div class="price-change-item">
                  <span class="price-label">护理费</span>
                  <span class="price-old">¥{{ formatMoney(currentPrice.careFeeOriginal) }}</span>
                  <i class="el-icon-right" style="margin: 0 6px;"></i>
                  <span class="price-new">¥{{ formatMoney(currentPrice.careFee) }}</span>
                </div>
              </el-col>
            </el-row>
            <!-- 第二行 -->
            <el-row :gutter="10" style="margin-bottom: 8px;">
              <el-col :span="12" v-if="currentPrice.mealFeeModified">
                <div class="price-change-item">
                  <span class="price-label">餐费</span>
                  <span class="price-old">¥{{ formatMoney(currentPrice.mealFeeOriginal) }}</span>
                  <i class="el-icon-right" style="margin: 0 6px;"></i>
                  <span class="price-new">¥{{ formatMoney(currentPrice.mealFee) }}</span>
                </div>
              </el-col>
            </el-row>
            <!-- 第三行 -->
            <el-row :gutter="10">
              <el-col :span="12" v-if="currentPrice.depositFeeModified">
                <div class="price-change-item">
                  <span class="price-label">押金</span>
                  <span class="price-old">¥{{ formatMoney(currentPrice.depositFeeOriginal) }}</span>
                  <i class="el-icon-right" style="margin: 0 6px;"></i>
                  <span class="price-new">¥{{ formatMoney(currentPrice.depositFee) }}</span>
                </div>
              </el-col>
              <el-col :span="12" v-if="currentPrice.memberFeeModified">
                <div class="price-change-item">
                  <span class="price-label">会员费</span>
                  <span class="price-old">¥{{ formatMoney(currentPrice.memberFeeOriginal) }}</span>
                  <i class="el-icon-right" style="margin: 0 6px;"></i>
                  <span class="price-new">¥{{ formatMoney(currentPrice.memberFee) }}</span>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-alert>

        <!-- 费用汇总 -->
        <el-card shadow="never" style="margin-bottom: 15px;">
          <div slot="header" style="font-size: 14px; color: #606266;">
            <i class="el-icon-s-finance"></i> 费用汇总
          </div>
          <el-row :gutter="16">
            <el-col :span="12">
              <div class="summary-item">
                <span class="summary-label">应收总计</span>
                <span class="summary-value blue">¥{{ formatMoney(renewCalculatedTotal) }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="summary-item">
                <span class="summary-label">实收总计</span>
                <el-input-number
                  v-model="renewForm.finalAmount"
                  :min="0"
                  :precision="2"
                  :controls-position="false"
                  size="small"
                  style="width: 140px;" />
                <span style="margin-left: 8px; color: #909399; font-size: 12px;">元</span>
                <el-tag v-if="renewDiscountAmount > 0" type="success" size="mini" style="margin-left: 8px;">
                  已优惠 ¥{{ formatMoney(renewDiscountAmount) }}
                </el-tag>
              </div>
            </el-col>
          </el-row>
          <el-divider style="margin: 12px 0;"></el-divider>
          <div v-if="renewForm.monthCount > 0" style="text-align: center;">
            <span style="color: #909399;">新到期日期</span>
            <span style="margin: 0 10px; font-size: 18px; font-weight: bold; color: #67C23A;">
              {{ parseTime(renewForm.newDueDate, '{y}-{m}-{d}') }}
            </span>
            <el-tag type="info" size="small">延长 {{ renewForm.monthCount }} 个月</el-tag>
          </div>
        </el-card>

        <!-- 支付方式 -->
        <el-divider content-position="left">
          <i class="el-icon-bank-card"></i> 支付方式
        </el-divider>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-radio-group v-model="renewForm.paymentMethod">
            <el-radio label="cash">
              <i class="el-icon-money"></i> 现金支付
            </el-radio>
            <el-radio label="card">
              <i class="el-icon-bank-card"></i> 刷卡支付
            </el-radio>
            <el-radio label="scan">
              <i class="el-icon-mobile-phone"></i> 扫码支付
            </el-radio>
            <el-radio label="online">
              <i class="el-icon-user"></i> 用户端支付
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="renewForm.remark" type="textarea" placeholder="请输入备注信息（可选）" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitRenew">确 定</el-button>
        <el-button @click="renewOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 退费对话框 -->
    <el-dialog title="入住人退费" :visible.sync="refundOpen" width="600px" append-to-body>
      <el-form ref="refundForm" :model="refundForm" :rules="refundRules" label-width="100px">
        <el-form-item label="入住人">
          <el-input v-model="refundForm.elderName" disabled />
        </el-form-item>
        <el-form-item label="退费类型" prop="refundType">
          <el-select v-model="refundForm.refundType" placeholder="请选择退费类型" style="width: 100%">
            <el-option label="服务费" value="service"></el-option>
            <el-option label="会员卡" value="member"></el-option>
            <el-option label="押金" value="deposit"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="可退余额">
          <el-input :value="formatMoney(refundForm.availableBalance)" disabled />
        </el-form-item>
        <el-form-item label="退费金额" prop="amount">
          <el-input-number v-model="refundForm.amount" :min="0" :max="refundForm.availableBalance" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="退费原因" prop="reason">
          <el-input v-model="refundForm.reason" type="textarea" placeholder="请输入退费原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitRefund">确 定</el-button>
        <el-button @click="refundOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 押金使用申请对话框 -->
    <el-dialog title="押金使用申请" :visible.sync="depositUseOpen" width="600px" append-to-body>
      <el-form ref="depositUseForm" :model="depositUseForm" :rules="depositUseRules" label-width="100px">
        <el-form-item label="入住人">
          <el-input v-model="depositUseForm.elderName" disabled />
        </el-form-item>
        <el-form-item label="可申请金额">
          <el-input :value="formatMoney(depositUseForm.availableBalance)" disabled />
        </el-form-item>
        <el-form-item label="申请金额" prop="amount">
          <el-input-number v-model="depositUseForm.amount" :min="0" :max="depositUseForm.availableBalance" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="使用事由" prop="purpose">
          <el-select v-model="depositUseForm.purpose" placeholder="请选择使用事由" style="width: 100%">
            <el-option label="医疗费用" value="医疗费用"></el-option>
            <el-option label="生活用品" value="生活用品"></el-option>
            <el-option label="特殊护理服务" value="特殊护理服务"></el-option>
            <el-option label="其他用途" value="其他用途"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明" prop="description">
          <el-input v-model="depositUseForm.description" type="textarea" placeholder="请详细说明使用事由" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitDepositUse">提 交</el-button>
        <el-button @click="depositUseOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 维护入住人信息对话框 -->
    <el-dialog title="维护入住人信息" :visible.sync="updateOpen" width="800px" append-to-body>
      <el-form ref="updateForm" :model="updateForm" :rules="updateRules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住人姓名" prop="elderName">
              <el-input v-model="updateForm.elderName" placeholder="请输入入住人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="updateForm.gender" placeholder="请选择性别" style="width: 100%">
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="updateForm.idCard" placeholder="请输入身份证号" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="updateForm.birthDate"
                type="date"
                placeholder="请选择出生日期"
                value-format="yyyy-MM-dd"
                style="width: 100%;"
                @change="calculateAgeFromBirthDate">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="updateForm.age" :min="1" :max="120" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="updateForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="护理等级" prop="careLevel">
              <el-select v-model="updateForm.careLevel" placeholder="请选择护理等级" style="width: 100%">
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
            <el-form-item label="健康状况" prop="healthStatus">
              <el-input v-model="updateForm.healthStatus" placeholder="请输入健康状况" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="家庭住址" prop="address">
          <el-input v-model="updateForm.address" type="textarea" placeholder="请输入家庭住址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="updateForm.emergencyContact" placeholder="请输入紧急联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系电话" prop="emergencyPhone">
              <el-input v-model="updateForm.emergencyPhone" placeholder="请输入紧急联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="特殊需求" prop="specialNeeds">
          <el-input v-model="updateForm.specialNeeds" type="textarea" placeholder="请输入特殊需求" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="updateForm.remark" type="textarea" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitUpdate">确 定</el-button>
        <el-button @click="updateOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog title="批量导入入住人" :visible.sync="importOpen" width="500px" append-to-body>
      <el-upload
        class="upload-demo"
        action="/api/elder/resident/import"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        :before-upload="beforeUpload"
        accept=".xlsx,.xls"
        :limit="1"
      >
        <el-button size="small" type="primary">点击上传</el-button>
        <div slot="tip" class="el-upload__tip">
          只能上传xlsx/xls文件，且不超过10MB
          <el-link type="primary" @click="downloadTemplate" style="margin-left: 10px;">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="importOpen = false">关 闭</el-button>
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
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEditFamily(scope.row)" v-hasPermi="['elder:resident:edit']">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDeleteFamily(scope.row)" v-hasPermi="['elder:resident:remove']" style="color: #F56C6C;">删除</el-button>
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
          <el-input v-model="passwordForm.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-alert
          title="说明: 设置密码后,家属可以使用老人的身份证号作为账号、密码进行H5登录"
          type="info"
          :closable="false">
        </el-alert>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitPasswordForm">确 定</el-button>
        <el-button @click="passwordDialogOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 拨付单详情对话框 -->
    <el-dialog :title="'拨付单详情 - ' + (currentElderForTransfer ? currentElderForTransfer.elderName : '')" :visible.sync="transferDialogOpen" width="900px" append-to-body>
      <el-table v-loading="transferLoading" :data="transferList" border>
        <el-table-column label="账单月份" prop="billingMonth" width="100" align="center" />
        <el-table-column label="划拨单号" prop="transferNo" width="180" show-overflow-tooltip />
        <el-table-column label="划拨金额" prop="transferAmount" width="120" align="center">
          <template slot-scope="scope">
            <span style="color: #E6A23C; font-weight: bold;">¥{{ formatMoney(scope.row.transferAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="划拨日期" prop="transferDate" width="110" align="center">
          <template slot-scope="scope">
            {{ parseTime(scope.row.transferDate, '{y}-{m}-{d}') }}
          </template>
        </el-table-column>
        <el-table-column label="划拨状态" prop="status" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 'pending'" type="warning">待划拨</el-tag>
            <el-tag v-else-if="scope.row.status === 'processing'" type="primary">划拨中</el-tag>
            <el-tag v-else-if="scope.row.status === 'completed'" type="success">已完成</el-tag>
            <el-tag v-else-if="scope.row.status === 'cancelled'" type="info">已取消</el-tag>
            <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="transferList.length === 0" style="text-align: center; padding: 20px; color: #909399;">
        暂无拨付单记录
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="transferDialogOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listResident, getResident, delResident, renewResident, refundResident, applyDepositUse, getCurrentPrice, getResidentTransfers } from "@/api/elder/resident";
import { updateElderInfo, setPassword } from "@/api/elder/elderInfo";
import { listPensionInstitution } from "@/api/pension/institution";
import { listFamily, addFamily, updateFamily, delFamily } from "@/api/elder/family";

export default {
  name: "ElderResident",
  dicts: ['elder_gender', 'elder_care_level', 'elder_relation_type'],
  data() {
    return {
      // 图片服务器地址
      baseUrl: process.env.VUE_APP_BASE_API,
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
      // 入住人列表数据
      residentList: [],
      // 养老机构列表
      institutionList: [],
      // 弹出层标题
      title: "",
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示续费弹出层
      renewOpen: false,
      // 是否显示退费弹出层
      refundOpen: false,
      // 是否显示押金使用申请弹出层
      depositUseOpen: false,
      // 是否显示导入弹出层
      importOpen: false,
      // 是否显示维护弹出层
      updateOpen: false,
      // 是否显示密码设置弹出层
      passwordDialogOpen: false,
      // 密码设置表单
      passwordForm: {
        elderId: null,
        elderName: '',
        idCard: '',
        password: '',
        confirmPassword: ''
      },
      // 密码设置表单校验规则
      passwordRules: {
        password: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          { min: 6, message: "密码长度不能少于6位", trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, message: "请确认密码", trigger: "blur" },
          { validator: (rule, value, callback) => {
              if (value !== this.passwordForm.password) {
                callback(new Error("两次输入的密码不一致"));
              } else {
                callback();
              }
            }, trigger: "blur" }
        ]
      },
      // 入住人详情
      residentDetail: {
        orders: [],
        payments: []
      },
      // 展开的订单ID列表
      expandedOrderKeys: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        elderName: null,
        gender: null,
        bedInfo: null,
        institutionId: null,
        checkInStatus: null,
      },
      // 续费表单参数
      renewForm: {
        elderId: null,
        elderName: null,
        bedInfo: null,
        monthlyFee: 0,
        bedFee: 0,
        careFee: 0,
        mealFee: 0,
        careLevelText: null,
        serviceBalance: 0,
        depositBalance: 0,
        memberBalance: 0,
        checkInDate: null,
        currentDueDate: null,
        newDueDate: null,
        monthCount: 0,
        depositAmount: 0,
        memberFee: 0,
        finalAmount: 0,
        paymentMethod: 'cash',
        remark: null
      },
      // 当前价格信息
      currentPrice: {
        bedFee: 0,
        bedFeeOriginal: null,
        bedFeeModified: false,
        careFee: 0,
        careFeeOriginal: null,
        careFeeModified: false,
        mealFee: 0,
        mealFeeOriginal: null,
        mealFeeModified: false,
        depositFee: 0,
        depositFeeOriginal: null,
        depositFeeModified: false,
        memberFee: 0,
        memberFeeOriginal: null,
        memberFeeModified: false,
        monthlyFeeTotal: 0,
        lastPaymentTime: null
      },
      // 退费表单参数
      refundForm: {
        residentId: null,
        elderName: null,
        refundType: 'service',
        amount: null,
        availableBalance: 0,
        reason: null
      },
      // 押金使用申请表单参数
      depositUseForm: {
        residentId: null,
        elderName: null,
        amount: null,
        availableBalance: 0,
        purpose: null,
        description: null
      },
      // 表单校验
      renewRules: {
        monthCount: [
          { required: true, message: "续费月数不能为空", trigger: "blur" },
          { type: 'number', min: 0, max: 120, message: "续费月数必须在0-120之间", trigger: "blur" }
        ],
        depositAmount: [
          { required: true, message: "补交押金金额不能为空", trigger: "blur" }
        ],
        memberFee: [
          { required: true, message: "补交会员费不能为空", trigger: "blur" }
        ],
        paymentMethod: [
          { required: true, message: "支付方式不能为空", trigger: "change" }
        ]
      },
      refundRules: {
        refundType: [
          { required: true, message: "退费类型不能为空", trigger: "change" }
        ],
        amount: [
          { required: true, message: "退费金额不能为空", trigger: "blur" }
        ],
        reason: [
          { required: true, message: "退费原因不能为空", trigger: "blur" }
        ]
      },
      depositUseRules: {
        amount: [
          { required: true, message: "申请金额不能为空", trigger: "blur" }
        ],
        purpose: [
          { required: true, message: "使用事由不能为空", trigger: "change" }
        ],
        description: [
          { required: true, message: "详细说明不能为空", trigger: "blur" }
        ]
      },
      // 维护表单参数
      updateForm: {
        elderId: null,
        elderName: null,
        gender: null,
        idCard: null,
        birthDate: null,
        age: null,
        phone: null,
        careLevel: null,
        healthStatus: null,
        address: null,
        emergencyContact: null,
        emergencyPhone: null,
        specialNeeds: null,
        remark: null
      },
      // 维护表单校验
      updateRules: {
        elderName: [
          { required: true, message: "请输入入住人姓名", trigger: "blur" }
        ],
        gender: [
          { required: true, message: "请选择性别", trigger: "change" }
        ],
        idCard: [
          { required: true, message: "请输入身份证号", trigger: "blur" },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: "身份证号格式不正确", trigger: "blur" }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" }
        ],
        emergencyPhone: [
          { required: true, message: "请输入紧急联系电话", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" }
        ]
      },
      // 家属管理相关
      familyManageOpen: false,
      currentElderForFamily: null,
      familyLoading: false,
      familyList: [],
      familyFormOpen: false,
      familyFormMode: 'add', // add 或 edit
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
      // 拨付单详情相关
      transferDialogOpen: false,
      currentElderForTransfer: null,
      transferLoading: false,
      transferList: []
    };
  },
  computed: {
    // 续费服务费小计 = 月服务费 × 续费月数
    renewServiceFeeTotal() {
      return (this.renewForm.monthlyFee || 0) * (this.renewForm.monthCount || 0);
    },
    // 续费应收总计 = 服务费小计 + 补交押金 + 补交会员费
    renewCalculatedTotal() {
      return this.renewServiceFeeTotal + (this.renewForm.depositAmount || 0) + (this.renewForm.memberFee || 0);
    },
    // 续费优惠金额 = 应收总计 - 实收总计
    renewDiscountAmount() {
      return Math.max(0, this.renewCalculatedTotal - (this.renewForm.finalAmount || 0));
    },
    // 是否有价格变更记录
    hasPriceModified() {
      return this.currentPrice.bedFeeModified || this.currentPrice.careFeeModified ||
             this.currentPrice.mealFeeModified || this.currentPrice.depositFeeModified || this.currentPrice.memberFeeModified;
    }
  },
  created() {
    this.loadInstitutions();
    this.getList();
  },
  methods: {
    /** 加载养老机构列表 */
    loadInstitutions() {
      listPensionInstitution().then(response => {
        this.institutionList = response.rows || [];
      });
    },
    /** 查询入住人列表 */
    getList() {
      this.loading = true;
      listResident(this.queryParams).then(response => {
        this.residentList = response.rows;
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
      this.ids = selection.map(item => item.residentId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增入驻按钮操作 */
    handleAdd() {
      this.$router.push('/pension/elder/checkin');
    },
    /** 批量导入按钮操作 */
    handleImport() {
      this.importOpen = true;
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      const elderId = row.elderId;
      getResident(elderId).then(response => {
        this.residentDetail = response.data;
        console.log('入住人详情数据:', response.data);
        console.log('订单列表:', response.data.orders);
        console.log('支付记录:', response.data.payments);
        this.detailOpen = true;
      });
    },
    /** 维护按钮操作 */
    handleUpdate(row) {
      const elderId = row.elderId;
      getResident(elderId).then(response => {
        this.updateForm = {
          elderId: response.data.elderId,
          elderName: response.data.elderName,
          gender: response.data.gender,
          idCard: response.data.idCard,
          birthDate: response.data.birthDate,
          age: response.data.age,
          phone: response.data.phone,
          careLevel: response.data.careLevel,
          healthStatus: response.data.healthStatus,
          address: response.data.address,
          emergencyContact: response.data.emergencyContact,
          emergencyPhone: response.data.emergencyPhone,
          specialNeeds: response.data.specialNeeds,
          remark: response.data.remark
        };
        this.updateOpen = true;
      });
    },
    /** 续费按钮操作 */
    handleRenew(row) {
      const elderId = row.elderId;
      // 并发获取老人详情和当前价格
      Promise.all([
        getResident(elderId),
        getCurrentPrice(elderId)
      ]).then(([residentRes, priceRes]) => {
        const data = residentRes.data;
        const priceData = priceRes.data;

        // 保存当前价格信息用于显示
        this.currentPrice = {
          bedFee: priceData.bedFee || 0,
          bedFeeOriginal: priceData.bedFeeOriginal,
          bedFeeModified: priceData.bedFeeModified || false,
          careFee: priceData.careFee || 0,
          careFeeOriginal: priceData.careFeeOriginal,
          careFeeModified: priceData.careFeeModified || false,
          mealFee: priceData.mealFee || 0,
          mealFeeOriginal: priceData.mealFeeOriginal,
          mealFeeModified: priceData.mealFeeModified || false,
          depositFee: priceData.depositFee || 0,
          depositFeeOriginal: priceData.depositFeeOriginal,
          depositFeeModified: priceData.depositFeeModified || false,
          memberFee: priceData.memberFee || 0,
          memberFeeOriginal: priceData.memberFeeOriginal,
          memberFeeModified: priceData.memberFeeModified || false,
          monthlyFeeTotal: priceData.monthlyFeeTotal || 0,
          lastPaymentTime: priceData.lastPaymentTime
        };

        // 获取护理等级文本
        let careLevelText = '未选择';
        if (data.careLevel) {
          switch (data.careLevel) {
            case '1':
              careLevelText = '自理';
              break;
            case '2':
              careLevelText = '半护理';
              break;
            case '3':
              careLevelText = '全护理';
              break;
            default:
              careLevelText = '未选择';
          }
        }

        this.renewForm = {
          elderId: data.elderId,
          elderName: data.elderName,
          bedInfo: data.bedInfo || '-',
          monthlyFee: this.currentPrice.monthlyFeeTotal || 0,
          bedFee: this.currentPrice.bedFee || 0,
          careFee: this.currentPrice.careFee || 0,
          mealFee: this.currentPrice.mealFee || 0,
          careLevelText: careLevelText,
          serviceBalance: data.serviceBalance || 0,
          depositBalance: data.depositBalance || 0,
          memberBalance: data.memberBalance || 0,
          checkInDate: data.checkInDate,
          currentDueDate: data.dueDate,
          newDueDate: data.dueDate,
          monthCount: 0,
          depositAmount: 0,
          memberFee: 0,
          finalAmount: 0,
          paymentMethod: 'cash',
          remark: null
        };
        this.renewOpen = true;
      });
    },
    /** 退费按钮操作 */
    handleRefund(row) {
      this.refundForm = {
        residentId: row.residentId,
        elderName: row.elderName,
        refundType: 'service',
        amount: null,
        availableBalance: row.serviceBalance,
        reason: null
      };
      this.refundOpen = true;
    },
    /** 去支付按钮操作 */
    handlePayment(row) {
      // 跳转到订单列表,并筛选该老人的待支付订单
      this.$router.push({
        path: '/pension/order/list',
        query: {
          elderName: row.elderName,
          orderStatus: '0'  // 0=待支付
        }
      });
    },
    /** 押金使用申请按钮操作 */
    handleDepositUse(row) {
      this.$router.push({
        path: '/elder/depositApply',
        query: { residentId: row.residentId }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const residentIds = row.residentId || this.ids;
      this.$modal.confirm('是否确认删除入住人编号为"' + residentIds + '"的数据项？').then(function() {
        return delResident(residentIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('elder/resident/export', {
        ...this.queryParams
      }, `入住人信息_${new Date().getTime()}.xlsx`)
    },
    /** 提交续费 */
    submitRenew() {
      this.$refs["renewForm"].validate(valid => {
        if (valid) {
          // 验证至少有一项续费内容
          if (this.renewForm.monthCount === 0 && this.renewForm.depositAmount === 0 && this.renewForm.memberFee === 0) {
            this.$modal.msgWarning("请至少填写一项续费内容(续费月数、补交押金或补交会员费)");
            return;
          }

          // 构建续费数据
          const renewData = {
            elderId: this.renewForm.elderId,
            monthCount: this.renewForm.monthCount,
            depositAmount: this.renewForm.depositAmount,
            memberFee: this.renewForm.memberFee,
            finalAmount: this.renewForm.finalAmount,
            paymentMethod: this.renewForm.paymentMethod,
            remark: this.renewForm.remark
          };

          renewResident(renewData).then(response => {
            this.$modal.msgSuccess("续费成功");
            this.renewOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 提交退费 */
    submitRefund() {
      this.$refs["refundForm"].validate(valid => {
        if (valid) {
          refundResident(this.refundForm).then(response => {
            this.$modal.msgSuccess("退费成功");
            this.refundOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 提交押金使用申请 */
    submitDepositUse() {
      this.$refs["depositUseForm"].validate(valid => {
        if (valid) {
          applyDepositUse(this.depositUseForm).then(response => {
            this.$modal.msgSuccess("申请提交成功");
            this.depositUseOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 下载导入模板 */
    downloadTemplate() {
      this.download('elder/resident/template', {}, `入住人导入模板.xlsx`);
    },
    /** 导入前检查 */
    beforeUpload(file) {
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                     file.type === 'application/vnd.ms-excel';
      const isLt10M = file.size / 1024 / 1024 < 10;

      if (!isExcel) {
        this.$message.error('只能上传 Excel 文件!');
        return false;
      }
      if (!isLt10M) {
        this.$message.error('上传文件大小不能超过 10MB!');
        return false;
      }
      return true;
    },
    /** 导入成功 */
    handleImportSuccess(response, file, fileList) {
      this.$message.success('导入成功');
      this.importOpen = false;
      this.getList();
    },
    /** 导入失败 */
    handleImportError(err, file, fileList) {
      this.$message.error('导入失败，请检查文件格式');
    },
    /** 格式化金额 */
    formatMoney(value) {
      if (!value) return '0.00'
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    },
    /** 获取余额样式类 */
    getBalanceClass(balance) {
      if (balance < 1000) return 'balance-warning'
      if (balance < 100) return 'balance-danger'
      return 'balance-normal'
    },
    /** 提交维护表单 */
    submitUpdate() {
      this.$refs["updateForm"].validate(valid => {
        if (valid) {
          updateElderInfo(this.updateForm).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.updateOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 出生日期变化时自动计算年龄 */
    calculateAgeFromBirthDate() {
      if (this.updateForm.birthDate) {
        const birthDate = new Date(this.updateForm.birthDate);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
          age--;
        }
        this.updateForm.age = age;
      }
    },
    /** 计算续费总额和新到期日期 */
    calculateRenewTotal() {
      // 计算新到期日期:在当前到期日期基础上增加续费月数
      if (this.renewForm.currentDueDate && this.renewForm.monthCount > 0) {
        const currentDue = new Date(this.renewForm.currentDueDate);
        const newDue = new Date(currentDue);
        newDue.setMonth(newDue.getMonth() + parseInt(this.renewForm.monthCount));
        this.renewForm.newDueDate = this.parseTime(newDue, '{y}-{m}-{d}');
      } else {
        // 如果月数为0,到期日期不变
        this.renewForm.newDueDate = this.renewForm.currentDueDate;
      }
      // 自动更新实收总计为应收总计(用户可手动调整)
      this.renewForm.finalAmount = this.renewCalculatedTotal;
    },
    /** 家属管理 */
    handleFamilyManage(row) {
      this.currentElderForFamily = row;
      this.familyManageOpen = true;
      this.getFamilyList(row.elderId);
    },
    /** 设置密码按钮操作 */
    handleSetPassword(row) {
      this.resetPasswordForm();
      this.passwordForm.elderId = row.elderId;
      this.passwordForm.elderName = row.elderName;
      this.passwordForm.idCard = row.idCard;
      this.passwordDialogOpen = true;
    },
    /** 重置密码表单 */
    resetPasswordForm() {
      this.passwordForm = {
        elderId: null,
        elderName: '',
        idCard: '',
        password: '',
        confirmPassword: ''
      };
      if (this.$refs.passwordForm) {
        this.$refs.passwordForm.resetFields();
      }
    },
    /** 提交密码设置 */
    submitPasswordForm() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          // 调用后端API设置密码
          const data = {
            elderId: this.passwordForm.elderId,
            password: this.passwordForm.password
          };

          setPassword(data).then(response => {
            if (response.code === 200) {
              this.$modal.msgSuccess("密码设置成功");
              this.passwordDialogOpen = false;
            } else {
              this.$modal.msgError(response.msg || "密码设置失败");
            }
          }).catch(error => {
            this.$modal.msgError("密码设置失败，请重试");
          });
        }
      });
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
    // 获取床位费
    getBedFee() {
      // 如果有订单数据，从订单中获取床位费
      if (this.residentDetail.orders && this.residentDetail.orders.length > 0) {
        const latestOrder = this.residentDetail.orders[0]; // 假设第一个订单是最新订单
        if (latestOrder.orderItems) {
          const bedItem = latestOrder.orderItems.find(item => item.itemType === 'bed_fee');
          if (bedItem) {
            return this.formatMoney(bedItem.unitPrice);
          }
        }
      }

      // 如果没有订单数据，尝试从每月费用中推算（兼容旧数据）
      if (this.residentDetail.monthlyFee) {
        // 假设床位费占月服务费的70%（这是��算值，实际应该从床位费中获取）
        const monthlyFee = parseFloat(this.residentDetail.monthlyFee) || 0;
        const bedFee = monthlyFee * 0.7;
        return this.formatMoney(bedFee);
      }

      return '0.00';
    },
    // 获取护理费
    getCareFee() {
      // 如果有订单数据，从订单中获取护理费
      if (this.residentDetail.orders && this.residentDetail.orders.length > 0) {
        const latestOrder = this.residentDetail.orders[0]; // 假设第一个订单是最新订单
        if (latestOrder.orderItems) {
          const careItem = latestOrder.orderItems.find(item => item.itemType === 'care_fee');
          if (careItem) {
            return this.formatMoney(careItem.unitPrice);
          }
        }
      }

      // 如果没有订单数据，尝试从每月费用中推算（兼容旧数据）
      if (this.residentDetail.monthlyFee) {
        // 假设护理费占月服务费的30%（这是估算值）
        const monthlyFee = parseFloat(this.residentDetail.monthlyFee) || 0;
        const careFee = monthlyFee * 0.3;
        return this.formatMoney(careFee);
      }

      return '0.00';
    },
    // 获取餐费
    getMealFee() {
      // 如果有订单数据，从订单中获取餐费
      if (this.residentDetail.orders && this.residentDetail.orders.length > 0) {
        const latestOrder = this.residentDetail.orders[0]; // 假设第一个订单是最新订单
        if (latestOrder.orderItems) {
          const mealItem = latestOrder.orderItems.find(item => item.itemType === 'meal_fee');
          if (mealItem) {
            return this.formatMoney(mealItem.unitPrice);
          }
        }
      }
      return '0.00';
    },
    // 获取服务费（床位费 + 护理费 + 餐费）
    getServiceFee() {
      // 直接从订单原始数据计算，使用单价作为月度价格
      let total = 0;
      if (this.residentDetail.orders && this.residentDetail.orders.length > 0) {
        const latestOrder = this.residentDetail.orders[0];
        if (latestOrder.orderItems) {
          const bedItem = latestOrder.orderItems.find(item => item.itemType === 'bed_fee');
          const careItem = latestOrder.orderItems.find(item => item.itemType === 'care_fee');
          const mealItem = latestOrder.orderItems.find(item => item.itemType === 'meal_fee');
          total = (bedItem ? parseFloat(bedItem.unitPrice) || 0 : 0) +
                   (careItem ? parseFloat(careItem.unitPrice) || 0 : 0) +
                   (mealItem ? parseFloat(mealItem.unitPrice) || 0 : 0);
        }
      }
      return this.formatMoney(total);
    },
    // 获取订单中被修改价格的项目
    getOrderPriceModified(order) {
      if (!order || !order.orderItems) return [];
      return order.orderItems.filter(item => item.isPriceModified === '1' && item.originalUnitPrice);
    },
    // 处理订单展开/折叠
    handleOrderExpand(row, expandedRows) {
      if (expandedRows.length > 0) {
        this.expandedOrderKeys = [row.orderId];
      } else {
        this.expandedOrderKeys = [];
      }
    },
    /** 拨付单详情 */
    handleTransferDetail(row) {
      this.currentElderForTransfer = row;
      this.transferDialogOpen = true;
      this.transferLoading = true;
      getResidentTransfers(row.elderId).then(response => {
        this.transferList = response.data || [];
      }).catch(() => {
        this.transferList = [];
        this.$message.error('获取拨付单失败');
      }).finally(() => {
        this.transferLoading = false;
      });
    },
    /** 获取完整的图片URL */
    getImageUrl(path) {
      if (!path) return '';
      // 如果路径已经是完整URL，直接返回
      if (path.startsWith('http://') || path.startsWith('https://')) {
        return path;
      }
      // 否则添加 baseUrl 前缀
      return this.baseUrl + path;
    }
  }
};
</script>

<style scoped>
.resident-detail {
  max-height: 500px;
  overflow-y: auto;
}

.balance-text {
  font-weight: 600;
}

.balance-normal {
  color: #67C23A;
}

.balance-warning {
  color: #E6A23C;
}

.balance-danger {
  color: #F56C6C;
}

.photo-item {
  text-align: center;
}

.photo-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
  font-weight: 500;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
}

/* 续费弹窗费用设置样式 */
.fee-item {
  text-align: center;
  padding: 12px 8px;
  border-radius: 4px;
  background: #fff;
  transition: all 0.3s;
  min-height: 80px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.fee-item:hover {
  background: #fff9f0;
}

.fee-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
  flex-shrink: 0;
}

.fee-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  flex-shrink: 0;
}

.fee-value.primary {
  color: #409EFF;
}

.fee-extra {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  flex-shrink: 0;
}

/* 价格变更记录样式 */
.price-change-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  background: #fef0e6;
  border-radius: 4px;
}

.price-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  min-width: 50px;
}

.price-old {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
  margin-left: auto;
}

.price-new {
  font-size: 14px;
  color: #E6A23C;
  font-weight: bold;
}

/* 费用汇总样式 */
.summary-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.summary-label {
  font-size: 14px;
  color: #606266;
  margin-right: 16px;
  min-width: 80px;
}

.summary-value {
  font-size: 20px;
  font-weight: bold;
}

.summary-value.blue {
  color: #409EFF;
}
</style>