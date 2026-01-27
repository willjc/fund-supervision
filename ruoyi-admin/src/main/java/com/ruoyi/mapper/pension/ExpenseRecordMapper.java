package com.ruoyi.mapper.pension;

import java.util.List;
import com.ruoyi.domain.pension.ExpenseRecord;

/**
 * 费用记录Mapper接口
 *
 * @author ruoyi
 * @date 2025-12-18
 */
public interface ExpenseRecordMapper
{
    /**
     * 查询费用记录
     *
     * @param recordId 费用记录主键
     * @return 费用记录
     */
    public ExpenseRecord selectExpenseRecordByRecordId(Long recordId);

    /**
     * 查询费用记录列表
     *
     * @param expenseRecord 费用记录
     * @return 费用记录集合
     */
    public List<ExpenseRecord> selectExpenseRecordList(ExpenseRecord expenseRecord);

    /**
     * 新增费用记录
     *
     * @param expenseRecord 费用记录
     * @return 结果
     */
    public int insertExpenseRecord(ExpenseRecord expenseRecord);

    /**
     * 修改费用记录
     *
     * @param expenseRecord 费用记录
     * @return 结果
     */
    public int updateExpenseRecord(ExpenseRecord expenseRecord);

    /**
     * 删除费用记录
     *
     * @param recordId 费用记录主键
     * @return 结果
     */
    public int deleteExpenseRecordByRecordId(Long recordId);

    /**
     * 批量删除费用记录
     *
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteExpenseRecordByRecordIds(Long[] recordIds);

    /**
     * 根据老人ID查询费用记录列表
     *
     * @param elderId 老人ID
     * @return 费用记录集合
     */
    public List<ExpenseRecord> selectExpenseRecordByElderId(Long elderId);

    /**
     * 根据账户ID查询费用记录列表
     *
     * @param accountId 账户ID
     * @return 费用记录集合
     */
    public List<ExpenseRecord> selectExpenseRecordByAccountId(Long accountId);

    /**
     * 根据关联ID和类型查询费用记录
     *
     * @param relatedId 关联ID
     * @param relatedType 关联类型
     * @return 费用记录
     */
    public ExpenseRecord selectExpenseRecordByRelatedId(Long relatedId, String relatedType);

    /**
     * 获取老人最新费用记录
     *
     * @param elderId 老人ID
     * @param expenseType 费用类型
     * @return 费用记录
     */
    public ExpenseRecord selectLatestExpenseRecord(Long elderId, String expenseType);
}