# AGENTS.md

本文件为在本仓库工作的编码代理提供长期项目约束。它不参与程序运行；如与用户当前明确指令冲突，以用户指令为准。

## 项目概况

- 养老机构预收费资金监管平台，基于 RuoYi 3.9.0。
- 后端：Java 8、Spring Boot 2.5.15、Spring Security、MyBatis、Maven 多模块。
- 管理端：`ruoyi-ui/`，Vue 2 + Element UI。
- H5 端：`ruoyi-h5/`，Vue 3 + Vue CLI + Vant。
- 当前数据库为 MySQL；达梦仅是未来迁移方向，不得把规划描述成现状。
- 业务包括机构、老人、入住、床位、订单、账户、押金、资金划拨及银行对接。

## 目录与职责

- `ruoyi-admin/`：启动模块和主要业务代码。
  - `src/main/java/com/ruoyi/web/controller/`：REST 控制器。
  - `src/main/java/com/ruoyi/service/`：业务服务。
  - `src/main/java/com/ruoyi/domain/`：领域对象。
  - `src/main/java/com/ruoyi/mapper/`：MyBatis Mapper 接口。
  - `src/main/resources/mapper/`：MyBatis XML。
  - `src/test/`：后端测试。
- `ruoyi-common/`、`ruoyi-framework/`、`ruoyi-system/`、`ruoyi-quartz/`、`ruoyi-generator/`：RuoYi 基础模块。
- `ruoyi-ui/src/api/`、`ruoyi-ui/src/views/`：管理端接口与页面。
- `ruoyi-h5/src/api/`、`ruoyi-h5/src/views/`：H5 接口与页面。
- `sql/`：数据库变更脚本；银行第一阶段脚本为 `sql/zzbank_integration_v1.sql`。
- `docs/`：项目文档，入口为 `docs/README.md`。
- `scripts/deploy/`：生产构建与部署脚本。

## 当前银行对接状态

- 已实现机构与银行商户号、监管账户的绑定模型和管理接口。
- 已实现可切换的 `BankGateway`、银行交易审计记录及完整模拟支付流程。
- `BANK_INTEGRATION_MODE` 默认是 `disabled`；本地联调时才显式使用 `mock`。
- `ZhengzhouBankGateway` 仍是保护性占位，会拒绝真实请求；不得宣称已经接通郑州银行。
- 不得直接采用 H5 传入的 `merId`；必须由后端根据 `institutionId` 查询机构绑定关系。
- 未取得并验收银行测试地址、测试商户、签名材料、回调规则和网络条件前，不得补写或尝试真实支付调用。
- 真实支付、退款、划拨必须保持幂等、事务一致性、金额精度、状态审计和回调验签。

## 开发约定

- 修改前先沿控制器、服务、Mapper、SQL 和前端调用追踪真实流程；修复共享根因，不在多个调用方重复打补丁。
- 优先复用 RuoYi 现有组件和项目已有模式，避免无必要的依赖、抽象和新文件。
- 金额统一使用 `BigDecimal`，禁止使用浮点数处理资金。
- 写操作要考虑事务、并发、重复请求和部分失败；银行流水号、平台请求号及业务主键必须保持可追溯。
- 后端接口使用 `@PreAuthorize`；管理端按钮使用 `v-hasPermi`，权限标识保持一致。
- 养老机构数据必须受机构/用户数据范围约束；超级管理员例外必须显式且可审计。
- 前端请求放在对应的 `src/api/`，页面放在对应的 `src/views/`，不要在组件中散落重复请求封装。
- 不提交密码、Token、私钥、银行签名材料或生产环境变量。发现仓库已有明文敏感信息时，先报告，不擅自轮换生产凭据。

## 数据库变更

- 先检查实际表结构和现有迁移脚本，不能仅凭实体类或旧文档推断生产结构。
- SQL 放入 `sql/`，说明目的、执行顺序、影响范围和回滚方式。
- 生产数据库写入、表结构修改或批量修复前，必须先给出备份、SQL、影响评估、回滚和验证方案，并取得用户明确同意。
- 涉及中文数据时显式使用 `utf8mb4`；涉及资金时验证精度、默认值、唯一约束和索引。
- 不在命令、日志、提交或文档中暴露数据库密码。

## 构建与验证

按改动范围运行最小且足够的验证。除非用户明确要求，不要为了文档或纯静态改动启动完整服务。

```bash
# 后端测试
mvn test -pl ruoyi-admin -am

# 后端构建
mvn clean package -pl ruoyi-admin -am -DskipTests

# 管理端
cd ruoyi-ui
npm ci
npm run build:prod

# H5
cd ruoyi-h5
npm ci
npm run lint
npm run build
```

- 银行、金额、权限、数据范围和事务改动至少保留一项可重复执行的针对性测试。
- 构建通过只证明可编译/打包，不代表真实银行联调、生产部署或业务验收完成。
- 验证失败时报告准确命令和错误，不通过删除测试或放宽校验掩盖问题。

## Git 与文件安全

- 工作区经常包含用户未提交的文档、素材和临时成果；开始和结束时都执行 `git status --short`。
- 不覆盖、清理、暂存或提交与当前任务无关的改动。
- 页面重构、数据库、支付、登录和部署类工作通常使用独立的 `codex/` 分支；小型低风险修改可由用户决定直接在当前分支完成。
- 提交前精确检查 `git diff --summary` 和目标文件差异，不使用宽泛的 `git add .`。
- 未经明确授权，不执行 merge、push、部署、数据库写入或删除运行数据。

## 部署约束

- 推送 `master` 会按路径触发 GitHub Actions：后端和两个前端可能自动部署到生产环境。
- 后端生产服务由 systemd 管理，应用目录为 `/opt/fund-supervision`；生产配置由启动脚本和环境变量覆盖仓库配置。
- 管理端和 H5 是 OpenResty/1Panel 静态站点；管理端 API 前缀为 `/prod-api`，H5 API 前缀为 `/api`。
- `/opt/fund-supervision/uploadPath` 是持久化用户文件，任何发布或清理都必须保护。
- 部署前说明变更、备份与回滚；部署后检查服务状态、健康接口、日志和受影响页面。
- 不把本地配置、仓库默认值或单次健康检查当作生产业务正确性的证据。

## 文档口径

- 明确区分“已实现代码”“模拟流程”“银行待提供条件”“已联调”和“已上线”。
- 外部接口原始文档放在 `docs/06-外部接口/`；项目结论和实施说明不要直接改写银行原始材料。
- 更新功能或部署方式时同步维护 `docs/README.md` 及对应分类文档，避免在根目录继续堆放说明文件。
