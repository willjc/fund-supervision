package com.ruoyi.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.ReleaseSupervisionMapper;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.ReleaseSupervision;
import com.ruoyi.service.IReleaseSupervisionService;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.pension.ISupervisionAccountLogService;
import com.ruoyi.common.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 机构解除监管申请Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-04
 */
@Service
public class ReleaseSupervisionServiceImpl implements IReleaseSupervisionService
{
    private static final Logger logger = LoggerFactory.getLogger(ReleaseSupervisionServiceImpl.class);

    @Autowired
    private ReleaseSupervisionMapper releaseSupervisionMapper;

    @Autowired
    private ISupervisionAccountLogService supervisionAccountLogService;

    @Autowired
    private IPensionInstitutionService pensionInstitutionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询机构解除监管申请
     *
     * @param releaseId 机构解除监管申请主键
     * @return 机构解除监管申请
     */
    @Override
    public ReleaseSupervision selectReleaseSupervisionByReleaseId(Long releaseId)
    {
        ReleaseSupervision supervision = releaseSupervisionMapper.selectReleaseSupervisionByReleaseId(releaseId);
        if (supervision != null && supervision.getInstitutionId() != null) {
            // 动态查询机构监管账户余额
            fillInstitutionBalance(supervision);
        }
        return supervision;
    }

    /**
     * 动态查询并填充机构监管账户余额
     * 从 account_info 表汇总该机构所有老人的账户余额
     *
     * @param supervision 解除监管申请对象
     */
    private void fillInstitutionBalance(ReleaseSupervision supervision)
    {
        Long institutionId = supervision.getInstitutionId();
        String sql = "SELECT " +
                     "COALESCE(SUM(total_balance), 0) as total, " +
                     "COALESCE(SUM(service_balance), 0) as service, " +
                     "COALESCE(SUM(deposit_balance), 0) as deposit, " +
                     "COALESCE(SUM(member_balance), 0) as member " +
                     "FROM account_info WHERE institution_id = ?";

        Map<String, Object> balance = jdbcTemplate.queryForMap(sql, institutionId);

        supervision.setSupervisionBalance((BigDecimal) balance.get("total"));
        supervision.setServiceFeeBalance((BigDecimal) balance.get("service"));
        supervision.setDepositBalance((BigDecimal) balance.get("deposit"));
        supervision.setMemberFeeBalance((BigDecimal) balance.get("member"));
    }

    /**
     * 动态查询机构监管账户余额
     * 用于审批时获取当前实时余额
     *
     * @param institutionId 机构ID
     * @return 余额信息 map
     */
    public Map<String, BigDecimal> getInstitutionBalance(Long institutionId)
    {
        String sql = "SELECT " +
                     "COALESCE(SUM(total_balance), 0) as total, " +
                     "COALESCE(SUM(service_balance), 0) as service, " +
                     "COALESCE(SUM(deposit_balance), 0) as deposit, " +
                     "COALESCE(SUM(member_balance), 0) as member " +
                     "FROM account_info WHERE institution_id = ?";

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, institutionId);
        Map<String, BigDecimal> balance = Map.of(
            "total", (BigDecimal) result.get("total"),
            "service", (BigDecimal) result.get("service"),
            "deposit", (BigDecimal) result.get("deposit"),
            "member", (BigDecimal) result.get("member")
        );
        return balance;
    }

    /**
     * 查询机构解除监管申请列表
     *
     * @param releaseSupervision 机构解除监管申请
     * @return 机构解除监管申请
     */
    @Override
    public List<ReleaseSupervision> selectReleaseSupervisionList(ReleaseSupervision releaseSupervision)
    {
        List<ReleaseSupervision> list = releaseSupervisionMapper.selectReleaseSupervisionList(releaseSupervision);
        // 为列表中的每条记录动态填充余额
        if (list != null && !list.isEmpty()) {
            for (ReleaseSupervision supervision : list) {
                if (supervision.getInstitutionId() != null) {
                    fillInstitutionBalance(supervision);
                }
            }
        }
        return list;
    }

    /**
     * 新增机构解除监管申请
     *
     * @param releaseSupervision 机构解除监管申请
     * @return 结果
     */
    @Override
    public int insertReleaseSupervision(ReleaseSupervision releaseSupervision)
    {
        releaseSupervision.setCreateTime(new Date());
        return releaseSupervisionMapper.insertReleaseSupervision(releaseSupervision);
    }

    /**
     * 修改机构解除监管申请
     *
     * @param releaseSupervision 机构解除监管申请
     * @return 结果
     */
    @Override
    public int updateReleaseSupervision(ReleaseSupervision releaseSupervision)
    {
        releaseSupervision.setUpdateTime(new Date());
        return releaseSupervisionMapper.updateReleaseSupervision(releaseSupervision);
    }

    /**
     * 批量删除机构解除监管申请
     *
     * @param releaseIds 需要删除的机构解除监管申请主键
     * @return 结果
     */
    @Override
    public int deleteReleaseSupervisionByReleaseIds(Long[] releaseIds)
    {
        return releaseSupervisionMapper.deleteReleaseSupervisionByReleaseIds(releaseIds);
    }

    /**
     * 删除机构解除监管申请信息
     *
     * @param releaseId 机构解除监管申请主键
     * @return 结果
     */
    @Override
    public int deleteReleaseSupervisionByReleaseId(Long releaseId)
    {
        return releaseSupervisionMapper.deleteReleaseSupervisionByReleaseId(releaseId);
    }

    /**
     * 批准解除监管
     *
     * @param releaseId 申请ID
     * @param releaseSupervision 审批信息
     * @return 结果
     */
    @Override
    @Transactional
    public int approveRelease(Long releaseId, ReleaseSupervision releaseSupervision)
    {
        try {
            // 1. 查询申请信息
            ReleaseSupervision apply = releaseSupervisionMapper.selectReleaseSupervisionByReleaseId(releaseId);
            if (apply == null) {
                logger.warn("批准解除监管失败：申请不存在, releaseId={}", releaseId);
                throw new RuntimeException("解除监管申请不存在");
            }
            if (!"0".equals(apply.getApplyStatus())) {
                logger.warn("批准解除监管失败：申请已被处理, releaseId={}, status={}", releaseId, apply.getApplyStatus());
                throw new RuntimeException("该申请已被处理，无法再次审批");
            }

            Long institutionId = apply.getInstitutionId();
            if (institutionId == null) {
                logger.error("批准解除监管失败：申请的机构ID为空, releaseId={}", releaseId);
                throw new RuntimeException("解除监管申请的机构ID为空");
            }

            // 2. 动态查询当前监管账户余额（实时数据）
            Map<String, BigDecimal> balance = getInstitutionBalance(institutionId);
            BigDecimal supervisionBalance = balance.get("total");

            // 3. 记录监管账户支出流水
            if (supervisionBalance != null && supervisionBalance.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    supervisionAccountLogService.recordTransferOut(
                            institutionId,
                            releaseId,
                            supervisionBalance,
                            "解除监管划拨-" + apply.getApplyNo(),
                            "基本账户"
                    );
                    logger.info("记录监管账户支出流水成功: institutionId={}, amount={}", institutionId, supervisionBalance);
                } catch (Exception e) {
                    logger.error("记录监管账户支出流水失败: institutionId={}, amount={}, error={}",
                            institutionId, supervisionBalance, e.getMessage(), e);
                    // 流水记录失败不影响主流程
                }

                // 3.1 生成fund_transfer拨付单，使解除监管记录在监管账户流水页面可见
                createReleaseTransferOrder(institutionId, releaseId, supervisionBalance, apply);
            }

            // 4. 更新机构状态为"解除监管"
            PensionInstitution institution = new PensionInstitution();
            institution.setInstitutionId(institutionId);
            institution.setStatus("3"); // 3=解除监管
            pensionInstitutionService.updatePensionInstitution(institution);

            // 5. 更新申请状态为已批准
            ReleaseSupervision update = new ReleaseSupervision();
            update.setReleaseId(releaseId);
            update.setApplyStatus("1"); // 已批准
            update.setApprover(SecurityUtils.getUsername());
            update.setApproveTime(new Date());
            update.setApproveRemark(releaseSupervision.getApproveRemark());
            update.setUpdateTime(new Date());

            int result = releaseSupervisionMapper.updateReleaseSupervision(update);
            logger.info("批准解除监管成功: releaseId={}, institutionId={}", releaseId, institutionId);
            return result;

        } catch (RuntimeException e) {
            logger.error("批准解除监管失败: releaseId={}, error={}", releaseId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("批准解除监管异常: releaseId={}, error={}", releaseId, e.getMessage(), e);
            throw new RuntimeException("批准解除监管失败: " + e.getMessage(), e);
        }
    }

    /**
     * 驳回解除监管申请
     *
     * @param releaseId 申请ID
     * @param releaseSupervision 驳回信息
     * @return 结果
     */
    @Override
    @Transactional
    public int rejectRelease(Long releaseId, ReleaseSupervision releaseSupervision)
    {
        // 查询申请信息，获取机构ID
        ReleaseSupervision apply = releaseSupervisionMapper.selectReleaseSupervisionByReleaseId(releaseId);
        if (apply == null) {
            throw new RuntimeException("解除监管申请不存在");
        }

        // 更新申请状态为已驳回
        ReleaseSupervision update = new ReleaseSupervision();
        update.setReleaseId(releaseId);
        update.setApplyStatus("2"); // 已驳回
        update.setApprover(SecurityUtils.getUsername());
        update.setApproveTime(new Date());
        update.setRejectReason(releaseSupervision.getRejectReason());
        update.setUpdateTime(new Date());
        int result = releaseSupervisionMapper.updateReleaseSupervision(update);

        // 恢复机构状态为已入驻
        if (result > 0) {
            PensionInstitution institution = new PensionInstitution();
            institution.setInstitutionId(apply.getInstitutionId());
            institution.setStatus("1"); // 恢复为已入驻
            pensionInstitutionService.updatePensionInstitution(institution);
        }

        return result;
    }

    /**
     * 获取统计数据
     *
     * @return 统计数据
     */
    @Override
    public Map<String, Object> getReleaseStatistics()
    {
        return releaseSupervisionMapper.selectReleaseStatistics();
    }

    /**
     * 查询解除监管申请的附件列表
     *
     * @param releaseId 解除监管申请ID
     * @return 附件列表
     */
    @Override
    public List<Map<String, Object>> selectAttachmentsByReleaseId(Long releaseId)
    {
        return releaseSupervisionMapper.selectAttachmentsByReleaseId(releaseId);
    }

    /**
     * 保存解除监管申请的附件
     *
     * @param releaseId 解除监管申请ID
     * @param attachments 附件列表
     * @return 结果
     */
    @Override
    public int saveAttachments(Long releaseId, List<Map<String, String>> attachments)
    {
        if (attachments == null || attachments.isEmpty()) {
            return 0;
        }

        String insertSql = "INSERT INTO release_supervision_attach (release_id, file_name, file_path, upload_time) VALUES (?, ?, ?, NOW())";
        int count = 0;

        for (Map<String, String> attachment : attachments) {
            String fileName = attachment.get("fileName");
            String filePath = attachment.get("filePath");
            jdbcTemplate.update(insertSql, releaseId, fileName, filePath);
            count++;
        }

        return count;
    }

    /**
     * 创建解除监管拨付单
     * 用于在批准解除监管时生成fund_transfer记录，使划拨记录在监管账户流水页面可见
     *
     * @param institutionId 机构ID
     * @param releaseId 解除监管申请ID
     * @param amount 划拨金额
     * @param apply 解除监管申请信息
     */
    private void createReleaseTransferOrder(Long institutionId, Long releaseId,
                                           BigDecimal amount, ReleaseSupervision apply)
    {
        try {
            // 生成拨付单号
            String transferNo = "TRF" + System.currentTimeMillis();

            // 插入fund_transfer记录
            // transfer_type='3' 表示特殊申请（解除监管）
            // elder_id=NULL 表示这是机构级别的划拨，不关联具体老人
            String sql = "INSERT INTO fund_transfer " +
                    "(institution_id, transfer_no, transfer_type, transfer_amount, " +
                    "transfer_date, transfer_status, is_paid, paid_time, paid_method, " +
                    "elder_id, order_id, billing_month, status, remark, create_by, create_time) " +
                    "VALUES (?, ?, ?, ?, CURDATE(), '0', '1', NOW(), '银行转账', " +
                    "NULL, NULL, NULL, 'completed', ?, 'system', NOW())";

            jdbcTemplate.update(sql,
                    institutionId,
                    transferNo,
                    "3",  // transfer_type='3' 表示特殊申请（解除监管）
                    amount,
                    "解除监管全额划拨-" + apply.getApplyNo()
            );

            logger.info("创建解除监管拨付单成功: transferNo={}, amount={}, institutionId={}",
                    transferNo, amount, institutionId);
        } catch (Exception e) {
            logger.error("创建解除监管拨付单失败: institutionId={}, amount={}, error={}",
                    institutionId, amount, e.getMessage(), e);
            // 拨付单创建失败不影响主流程，记录日志即可
        }
    }
}
