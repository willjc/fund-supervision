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

