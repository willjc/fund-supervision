# 若依项目初始化和修改记录

## 项目基本信息
- 项目名称：若依(RuoYi) 管理系统
- 版本：v3.9.0

## 2025-12-04 机构列表页数据真实化改造

### 修改背景
H5机构列表页中图片、月参考价格、床位数显示的不是真实数据，需要参考首页优选机构的接口实现，使用真实数据。

### 后端修改
**文件**: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/H5InstitutionController.java`

1. **修改 `/list` 接口方法**
   - 添加数据转换逻辑，调用 `convertToH5Format` 方法
   - 使用 `TableDataInfo` 包装转换后的数据返回给前端

2. **优化 `convertToH5Format` 方法**
   - 添加真实的床位统计数据获取：调用 `bedInfoService.getBedStatistics()`
   - 获取 `totalBeds` 和 `availableBeds` 真实数据
   - 添加异常处理，确保数据完整性

### 前端修改
**文件**: `ruoyi-h5/src/views/institution/index.vue`

1. **更新数据处理逻辑**
   - 使用后端返回的 `priceRanges.total.min/max` 而非简单的 `priceRangeMin/Max`
   - 使用后端返回的 `coverImage` 字段
   - 将 `availableBeds` 默认值从8改为0，显示真实数据

2. **移除模拟数据**
   - 删除 `mockInstitutions` 模拟数据数组
   - 简化错误处理逻辑，不再依赖模拟数据

### 改造效果
- **图片**: 显示机构真实封面图片（mainPicture -> environmentImgs -> 默认图片）
- **价格**: 显示详细的分项价格（总费用、床位费、护理费、膳食费）
- **床位**: 显示真实的总床位和可定床位数统计
- **数据一致性**: 机构列表页与首页使用相同的数据结构和处理逻辑

### 2025-12-04 机构列表页数据源优化

#### 问题描述
机构列表页仍然显示模拟数据（图片、价格不真实，床位数部分真实），原因是：
- 首页使用 `pension_institution_public` 表（公示信息，包含真实图片和详细价格）
- 机构列表页使用 `pension_institution` 表（基础信息，缺少详细数据）

#### 解决方案
**文件**: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/H5InstitutionController.java`

1. **数据源统一**
   - 将机构列表页的数据源从 `pension_institution` 改为 `pension_institution_public`
   - 使用与首页相同的查询逻辑和数据结构

2. **H5InstitutionController.list() 方法重构**
   - 使用 `H5InstitutionService.list()` 方法获取数据
   - 保持原有的分页和搜索功能

3. **SQL查询优化**
   - 只查询已公示的机构（status = '1'）
   - 保留价格信息、环境图片等关键字段

#### 修改的代码
```java
// 原来使用的基础查询（缺少详细信息）
List<PensionInstitution> list = pensionInstitutionService.list(/* 简单查询 */);

// 修改后使用公示信息查询（包含完整信息）
List<PensionInstitutionPublic> list = h5InstitutionService.list(/* 包含价格的完整查询 */);
```

#### 效果验证
- 机构列表页显示真实的机构图片
- 价格信息准确反映各机构的收费情况
- 床位数基于真实的统计数据

### 2025-12-05 用户收藏功能开发

### 需求分析
为H5端添加机构收藏功能，让用户可以收藏感兴趣的养老机构，便于后续查看和管理。

### 数据库设计
**表名**: `user_favorite`
```sql
CREATE TABLE user_favorite (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  user_id bigint NOT NULL COMMENT '用户ID',
  institution_id bigint NOT NULL COMMENT '机构ID',
  create_time datetime COMMENT '创建时间',
  INDEX idx_user_institution (user_id, institution_id),
  INDEX idx_user_id (user_id)
);
```

### 后端实现

#### 1. 实体类 (UserFavorite.java)
- 使用标准的JPA注解
- 继承BaseEntity获取create_time等公共字段
- 添加复合唯一索引 @Table.UniqueColumn

#### 2. Mapper接口 (UserFavoriteMapper.java)
- 提供基础的CRUD操作
- selectByUserIdAndInstitutionId：查询是否已收藏
- deleteByUserIdAndInstitutionId：取消收藏
- selectCountByInstitutionId：统计机构收藏数

#### 3. Service层 (IUserFavoriteService.java / UserFavoriteServiceImpl.java)
- 添加收藏：addFavorite() - 先检查是否已收藏
- 取消收藏：cancelFavorite() - 先检查是否存在
- 查询收藏列表：getFavoriteList()
- 检查收藏状态：isFavorite()
- 获取收藏数量：getFavoriteCount()

#### 4. Controller层 (UserFavoriteController.java)
- POST /favorites/add：添加收藏
- DELETE /favorites/cancel：取消收藏
- GET /favorites/list：查询收藏列表
- GET /favorites/check/{institutionId}：检查收藏状态

### 前端实现

#### 1. API接口 (institution.js)
```javascript
// 添加到收藏列表
export function addFavorite(data) {
  return request({
    url: '/favorites/add',
    method: 'post',
    data: data
  })
}

// 取消收藏
export function cancelFavorite(data) {
  return request({
    url: '/favorites/cancel',
    method: 'delete',
    data: data
  })
}

// 获取我的收藏列表
export function getFavoriteList() {
  return request({
    url: '/favorites/list',
    method: 'get'
  })
}
```

#### 2. 机构详情页 (institution/detail.vue)
- 添加收藏按钮组件
- 实现收藏/取消收藏功能
- 实时更新收藏状态和数量

#### 3. 我的收藏页 (user/collection/index.vue)
- 新建收藏列表页面
- 使用grid布局展示收藏的机构卡片
- 支持搜索和取消收藏操作
- 添加空状态提示

### 功能特点
1. **交互友好**: 一键收藏/取消，即时反馈
2. **数据同步**: 收藏状态实时更新
3. **搜索功能**: 收藏列表支持搜索过滤

### 技术特点
1. **完整的MVC架构**: 分层设计，职责清晰
2. **数据一致性**: 数据库约束 + 业务逻辑双重保障
3. **用户体验**: 加载状态、操作反馈、错误提示
4. **安全性**: 用户身份验证和数据隔离
5. **性能优化**: 数据库索引、并发删除、防重复操作
6. **向后兼容**: 保持原有UI界面不变，只替换数据层

### 影响范围
- **用户体验**: 机构收藏功能完全可用，提升用户粘性
- **数据管理**: 收藏数据可统计分析，为运营提供数据支持
- **功能扩展**: 为后续推荐算法、用户画像等功能奠定基础
- **系统完整性**: 完善H5端核心功能闭环

### 2025-12-09 机构收藏功能图片显示问题修复

### 问题描述
用户反馈"我的收藏"页面中的机构图片不显示

### 问题原因
在UserFavoriteController中，直接使用了`institution.getCoverImages()`返回的图片字符串，但该字段可能包含多个图片URL（逗号分隔），需要从中提取第一张图片才能正常显示

### 修复内容
1. **图片处理逻辑优化**
   - 在UserFavoriteController的list方法中添加图片解析逻辑
   - 从coverImages字段中按逗号分割，取第一张图片作为封面
   - 添加异常处理，确保解析失败时有默认图片

2. **调试日志增强**
   - 添加详细的调试日志，跟踪图片处理过程
   - 输出原始coverImages和处理后的coverImage
   - 便于排查图片显示问题

### 修改的文件
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/UserFavoriteController.java`
  - 在list方法中完善图片处理逻辑
  - 添加详细的调试日志输出

### 技术改进
- **图片解析**: 支持多图片格式，自动提取第一张有效图片
- **容错处理**: 完善的异常处理机制，确保页面正常显示
- **调试支持**: 详细的日志输出，便于问题定位

### 预期效果
- 用户收藏列表中的机构图片能正常显示
- 即使机构没有上传图片，也会显示默认占位图
- 系统具有更好的调试和错误处理能力

## 2025-12-09 创建OrderRequest订单请求对象

### 需求描述
为H5订单提交功能创建数据传输对象（DTO），用于接收前端提交的订单数据。

### 创建内容
**文件**: `ruoyi-admin/src/main/java/com/ruoyi/dto/OrderRequest.java`

1. **包结构**
   - 包名：`com.ruoyi.dto`
   - 遵循项目现有的DTO类命名规范

2. **字段定义**
   - `elderId`: 老人ID (Long)
   - `institutionId`: 机构ID (Long)
   - `roomType`: 房间类型 (String) - single/double/triple/vip
   - `careLevel`: 护理等级 (String) - 自理/半自理/不能自理/特护
   - `mealType`: 餐费类型 (String) - 普通餐/定制餐
   - `months`: 缴纳月数 (Integer)
   - `selectedPackages`: 选择的套餐列表 (List<Long>)
   - `contactPhone`: 联系电话 (String)
   - `remark`: 备注信息 (String)

3. **编码规范**
   - 遵循项目风格，手动编写getter/setter方法
   - 不使用@Data注解，保持与现有代码一致
   - 添加完整的字段注释说明

### 技术特点
- **类型安全**: 使用正确的Java类型定义字段
- **数据完整性**: 所有字段提供完整的访问器方法
- **向后兼容**: 遵循项目现有编码规范和风格
- **文档完善**: 每个字段都有清晰的注释说明

### 后续使用
该DTO类将用于：
- H5端订单提交接口的参数接收
- 订单创建流程的数据传递
- 订单验证逻辑的输入参数

## 2025-12-09 H5订单确认页面功能优化

### 需求描述
优化H5订单确认页面，实现房间类型和护理等级选项优化、动态床位价格获取、费用明细展示等功能。

### 修改内容

#### 1. 优化房间类型选项
**文件**: `ruoyi-h5/src/views/order/confirm.vue`

更新房间类型选项：
- 单人间（独立床位）
- 双人间（两人一间）
- 三人间（三人一间）
- VIP房间（豪华床位）

#### 2. 扩展护理等级选项
更新护理等级选项，包含价格信息：
- 自理（300元/月）
- 半自理（800元/月）
- 不能自理（1500元/月）
- 特护（2500元/月）

#### 3. 动态床位价格获取
- 根据选择的机构ID和房间类型，调用后端API获取最优床位价格
- 添加 getBedPrice() 函数调用真实API
- 添加API失败后的备用价格映射

#### 4. 优化费用明细展示
重构费用计算逻辑，分别显示：
- 床位费：根据房间类型动态获取
- 护理费：根据选择的护理等级计算
- 餐费：根据选择的餐费类型计算
- 其他费用：固定费用（空调费、水电费）
- 月服务费合计：所有月费的总和
- 押金：一次性缴纳
- 会员费：一次性缴纳
- 总金额：一次性费用+月服务费×月数

#### 5. 添加API接口
**文件**: `ruoyi-h5/src/api/order.js`

新增两个API接口：
```javascript
// 提交订单
export function submitOrder(data)

// 获取床位价格
export function getBedPrice(institutionId, roomType)
```

#### 6. 修改提交订单逻辑
- 准备完整的订单数据对象，包含费用明细
- 调用真实的订单提交API
- 使用API返回的订单ID和订单号
- 添加加载状态和错误处理

### 技术特点
1. **动态价格计算**：根据选择的选项实时计算各项费用
2. **API集成**：调用真实API获取床位价格和提交订单
3. **容错处理**：API调用失败时使用模拟数据备用
4. **用户体验**：添加加载提示和操作反馈
5. **代码优化**：使用computed计算属性优化性能

### 效果
- 用户可以清楚地看到各项费用明细
- 根据房间类型自动获取最优床位价格
- 护理等级选择直接影响总费用
- 订单数据完整传递给后端系统## 数据库表结构更新 - 2025-12-09 16:33:43

**更新内容**：为 elder_check_in 表增加订单来源字段

**SQL语句**：
```sql
ALTER TABLE elder_check_in 
ADD COLUMN order_source VARCHAR(10) DEFAULT '1' 
COMMENT '订单来源：1-线下（管理后台），2-H5线上';
```

**字段说明**：
- 字段名：order_source
- 类型：VARCHAR(10)
- 默认值：'1'（线下）
- 取值：
  - '1'：线下订单（管理后台创建）
  - '2'：H5线上订单（小程序创建）

**目的**：
- 区分订单来源，便于数据统计分析
- 支持线上线下的业务运营分析
- 为后续功能扩展预留基础

## 护理等级定价系统实现 - 2025-12-10 08:58:16

### 1. 数据库结构更新
**更新内容**：为 bed_info 表增加护理等级价格字段

**新增字段**：
```sql
-- 自理护理价格(元/月)
ALTER TABLE bed_info ADD COLUMN self_care_price DECIMAL(10,2) DEFAULT NULL;

-- 半护理价格(元/月)
ALTER TABLE bed_info ADD COLUMN half_care_price DECIMAL(10,2) DEFAULT NULL;

-- 全护理价格(元/月)
ALTER TABLE bed_info ADD COLUMN full_care_price DECIMAL(10,2) DEFAULT NULL;

-- 会员费(一次性)
ALTER TABLE bed_info ADD COLUMN member_fee DECIMAL(10,2) DEFAULT NULL;

-- 押金(一次性)
ALTER TABLE bed_info ADD COLUMN deposit_fee DECIMAL(10,2) DEFAULT NULL;
```

### 2. 实体类更新
**文件**：`ruoyi-admin/src/main/java/com/ruoyi/domain/BedInfo.java`

**新增属性**：
- `selfCarePrice` - 自理护理价格
- `halfCarePrice` - 半护理价格
- `fullCarePrice` - 全护理价格
- `memberFee` - 会员费
- `depositFee` - 押金

**更新内容**：
- 添加对应的getter/setter方法
- 更新toString方法包含新字段
- 添加Excel注解支持导入导出

### 3. H5订单控制器更新
**文件**：`ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/H5OrderController.java`

**主要修改**：
1. **床位价格查询接口优化**：
   - 返回完整的护理等级价格信息
   - 包含床位费、各等级护理费、会员费、押金

2. **新增护理费用计算方法**：
   ```java
   private BigDecimal getCareFeeByLevel(BedInfo bed, String careLevel)
   ```
   - 根据护理等级返回对应价格
   - 支持自理/半护理/全护理三个等级

3. **订单提交逻辑更新**：
   - 使用新的定价模式：服务费 = 床位费 + 护理费
   - 使用床位自带的会员费和押金
   - 更新费用说明，清晰展示费用构成

### 4. H5前端页面重构
**文件**：`ruoyi-h5/src/views/order/confirm.vue`

**主要更新**：
1. **界面结构调整**：
   - 移除套餐选择区域
   - 新增护理等级选择器
   - 简化费用明细展示

2. **数据模型重构**：
   - `bedInfo` 对象存储完整床位价格信息
   - `careLevelOptions` 定义护理等级选项
   - `careLevelText` 计算属性显示选中等级

3. **费用计算逻辑**：
   - 床位费：`bedInfo.bedFee`
   - 护理费：根据等级选择对应价格
   - 服务费合计：床位费 + 护理费
   - 押金和会员费从床位信息获取

4. **API交互优化**：
   - 调用 `/h5/bed/optimal-price` 获取完整价格信息
   - 提交订单时包含护理等级信息
   - 增强错误处理和默认值处理

### 5. 定价模式说明

**新定价模式**：
- **服务费 = 床位费 + 护理费**
- **床位费**：每个床位的独立价格
- **护理费**：根据护理等级确定（自理/半护理/全护理）
- **一次性费用**：会员费 + 押金（每个床位可能不同）

**价格示例**：
- 豪华床位A：床位费1000元，自理500元，半护理800元，全护理1200元
- 月服务费（自理等级）：1000 + 500 = 1500元
- 总费用（3个月）：1500×3 + 会员费 + 押金

### 6. 技术优势
1. **灵活定价**：每个床位可独立设置各项价格
2. **透明收费**：用户清楚看到费用构成
3. **动态计算**：选择不同护理等级实时更新价格
4. **数据完整**：后端存储完整的价格信息
5. **扩展性强**：支持未来增加更多护理等级


## H5订单系统真实数据集成 - 2025-12-10 09:15:42

### 1. 创建老人列表API接口
**文件**：`ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/H5OrderController.java`

**新增接口**：
```java
@GetMapping("/elder/list")
public AjaxResult getElderList()
```

**功能说明**：
- 查询所有未入住的老人信息（status != '1'）
- 返回老人的详细信息：ID、姓名、性别、年龄、护理等级等
- 将护理等级代码转换为文字描述

### 2. 前端API集成
**文件**：`ruoyi-h5/src/api/order.js`

**新增方法**：
```javascript
export function getElderList() {
  return request({
    url: '/h5/elder/list',
    method: 'get'
  })
}
```

### 3. H5页面数据集成
**文件**：`ruoyi-h5/src/views/order/confirm.vue`

**主要修改**：
1. **真实老人数据**：
   - 从API获取真实老人列表
   - 显示老人姓名、性别、年龄、护理等级
   - 自动关联老人ID和护理等级

2. **智能关联**：
   - 选择老人后自动设置能力等级和护理等级
   - 建立数据库护理等级代码与显示文字的映射
   - 修复elderId传递，使用真实老人ID

3. **页面优化**：
   - 页面加载时自动获取老人列表
   - 老人选择器显示完整信息
   - 选择老人后自动设置相关字段

### 4. 数据完整性验证

**床位价格数据更新**：
```sql
-- 普通床位 (bed_type=1)
UPDATE bed_info SET
  self_care_price=500,
  half_care_price=800,
  full_care_price=1200,
  member_fee=5000,
  deposit_fee=10000
WHERE institution_id=16 AND bed_type='1';

-- 豪华床位 (bed_type=2)
UPDATE bed_info SET
  self_care_price=600,
  half_care_price=1000,
  full_care_price=1500,
  member_fee=8000,
  deposit_fee=15000
WHERE institution_id=16 AND bed_type='2';

-- 医疗床位 (bed_type=3)
UPDATE bed_info SET
  self_care_price=800,
  half_care_price=1500,
  full_care_price=2000,
  member_fee=10000,
  deposit_fee=20000
WHERE institution_id=16 AND bed_type='3';
```

### 5. 系统集成效果

**完全真实数据支持**：
- ✅ 老人列表：从elder_info表获取
- ✅ 床位价格：从bed_info表获取
- ✅ 护理等级价格：根据床位类型动态定价
- ✅ 护理等级关联：老人信息自动映射到护理选择
- ✅ 订单提交：使用真实elderId创建订单

**数据流程**：
1. H5页面加载 → 获取老人列表 → 显示可选择的老人
2. 选择老人 → 自动设置护理等级 → 获取床位价格
3. 选择房间类型 → 实时更新价格 → 计算总费用
4. 提交订单 → 使用真实数据 → 创建入住申请

### 6. 用户体验提升
- **信息完整**：显示老人详细信息和护理等级
- **操作便捷**：选择老人后自动设置相关选项
- **价格透明**：实时显示床位费和护理费明细
- **数据准确**：所有数据来源于真实数据库


## 后台床位管理页面护理价格字段添加 - 2025-12-10 09:25:18

### 1. 问题描述
后台床位管理页面（http://localhost/pension/bed/list）的添加和编辑床位表单中缺少护理等级价格相关字段，无法设置自理、半护理、全护理的价格以及会员费和押金。

### 2. 修改文件
**文件**：`ruoyi-ui/src/views/pension/bed/list.vue`

### 3. 主要修改内容

#### 3.1 表单字段扩展
**在reset方法中添加护理价格字段**：
```javascript
selfCarePrice: null,    // 自理护理价格
halfCarePrice: null,    // 半护理价格  
fullCarePrice: null,   // 全护理价格
memberFee: null,       // 会员费
depositFee: null       // 押金
```

#### 3.2 表单界面优化
**新增护理价格设置区域**：
- 添加分隔线：`护理等级价格设置`
- 添加五个价格输入字段，包含单位显示
- 调整对话框宽度从600px到900px以容纳更多字段
- 为价格字段添加数值类型验证

**表单字段详情**：
1. 床位费（原价格字段）：添加元/月单位
2. 自理护理价：自理等级护理费用
3. 半护理价：半护理等级护理费用
4. 全护理价：全护理等级护理费用
5. 会员费：一次性会员费用
6. 押金：一次性押金费用

#### 3.3 列表显示优化
**床位列表增加价格信息显示**：
- 将原"价格"列改为"床位费"，显示元/月单位
- 新增"护理价格"列：显示三个护理等级的价格
- 新增"一次性费用"列：显示会员费和押金

**列表显示效果**：
```
床位费      | 护理价格              | 一次性费用
￥2000/月   | 自理: ￥500/月       | 会员费: ￥5000
           | 半护理: ￥800/月     | 押金: ￥10000  
           | 全护理: ￥1200/月    |
```

#### 3.4 字段单位统一
- 床位费：元/月
- 护理价格：元/月
- 会员费：元(一次性)
- 押金：元(一次性)
- 房间面积：㎡

### 4. 功能效果
- ✅ 后台可以完整设置床位的各项价格
- ✅ 列表清晰展示床位的价格构成
- ✅ 与H5端护理等级定价系统完全对接
- ✅ 支持不同床位类型的差异化定价

### 5. 注意事项
- 导入功能暂未更新，如需支持导入护理价格需要修改导入模板
- 所有价格字段都设置为数字输入类型
- 列表显示时对空值做了友好处理（显示为0）

### 6. 业务价值
- 完善了床位价格管理体系
- 为精细化运营提供数据支持
- 提升了价格设置的透明度和准确性


## 床位管理表单布局优化 - 2025-12-10 09:30:05

### 问题描述
护理等级价格设置中的输入框过长，导致"自理护理价"等标签文字换行，影响页面美观。

### 优化方案
**文件**：`ruoyi-ui/src/views/pension/bed/list.vue`

### 具体修改

#### 1. 输入框尺寸调整
将所有护理价格相关的输入框都设置为 `size="small"`，让输入框更紧凑：

```javascript
// 修改前
<el-input v-model="form.selfCarePrice" placeholder="请输入自理护理价格" type="number">

// 修改后  
<el-input v-model="form.selfCarePrice" placeholder="自理护理价" type="number" size="small">
```

#### 2. 占位符文字精简
精简了所有价格输入框的占位符文字，去掉"请输入"前缀：
- "请输入自理护理价格" → "自理护理价"
- "请输入半护理价格" → "半护理价"
- "请输入全护理价格" → "全护理价"
- "请输入会员费" → "会员费"
- "请输入押金" → "押金"

#### 3. 表单布局优化
**第一行**：三个护理价格字段，每列占用 1/3 空间（span="8"）
- 自理护理价 | 半护理价 | 全护理价

**第二行**：两个一次性费用字段，每列占用 1/2 空间（span="12"）  
- 会员费 | 押金

#### 4. 标签宽度调整
将表单标签宽度从 80px 调整为 100px，为文字较长的标签提供更多空间。

### 优化效果
- ✅ 解决了标签换行问题
- ✅ 表单布局更加紧凑美观
- ✅ 保持了功能的完整性
- ✅ 提升了用户体验

## 2025-12-10 修复床位管理功能问题

### 问题描述
1. 床位列表中操作栏缺少详情按钮，无法查看床位详细信息
2. 护理价格（自理/半护理/全护理）和一次性费用（会员费/押金）无法保存成功

### 修改内容

#### 1. 添加床位详情功能（ruoyi-ui/src/views/pension/bed/list.vue）

**前端页面修改**：
- 在操作栏添加详情按钮，使用 `el-icon-view` 图标
- 在 data 中添加详情弹窗状态变量：`detailOpen` 和 `detailForm`
- 添加 `handleDetail` 方法处理详情查看逻辑
- 添加 `cancelDetail` 方法关闭详情弹窗
- 创建床位详情弹窗，使用 `el-descriptions` 组件展示完整床位信息
- 详情弹窗包含基本信息和费用明细两个部分

**详情弹窗展示内容**：
- 基本信息：房间号、床位号、床位类型、床位状态、床位费、楼层、房间面积、独立卫浴、阳台、设施配置
- 费用明细：自理护理价、半护理价格、全护理价格、会员费、押金、创建时间

#### 2. 修复护理价格保存问题（ruoyi-admin/src/main/resources/mapper/BedInfoMapper.xml）

**数据库映射修复**：
- 在 `resultMap` 中添加缺失的字段映射：
  - `selfCarePrice` → `self_care_price`
  - `halfCarePrice` → `half_care_price`
  - `fullCarePrice` → `full_care_price`
  - `memberFee` → `member_fee`
  - `depositFee` → `deposit_fee`

**SQL查询更新**：
- 更新 `selectBedInfoVo` 查询语句，添加护理价格字段
- 更新 `insertBedInfo` 插入语句，添加护理价格和一次性费用字段的插入逻辑
- 更新 `updateBedInfo` 更新语句，添加护理价格和一次性费用字段的更新逻辑
- 更新 `selectBedByRoomAndBedNumber` 查询语句，添加护理价格字段

### 修复原理
问题的根本原因是 MyBatis 映射文件中缺少了对新增护理价格字段的映射，导致：
1. 数据库查询时无法获取这些字段的值
2. 表单提交时无法将这些字段的值保存到数据库

通过完善 MyBatis 映射配置，确保了数据在数据库和前端之间的正确传输。

### 修复效果
- ✅ 床位列表操作栏新增详情按钮，可查看完整床位信息
- ✅ 详情弹窗展示床位基本信息和费用明细
- ✅ 护理价格字段（自理/半护理/全护理）可正常保存
- ✅ 一次性费用字段（会员费/押金）可正常保存
- ✅ 数据库映射完整，数据传输正常
- ✅ 提升了床位管理功能的完整性和用户体验

