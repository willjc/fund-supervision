package com.ruoyi.service;

import java.util.List;
import com.ruoyi.domain.RenewDTO;
import com.ruoyi.domain.vo.ResidentVO;

/**
 * 入住人Service接口
 *
 * @author ruoyi
 * @date 2025-11-11
 */
public interface IResidentService
{
    /**
     * 查询入住人列表
     *
     * @param queryVO 查询条件
     * @return 入住人列表
     */
    public List<ResidentVO> selectResidentList(ResidentVO queryVO);

    /**
     * 查询入住人详细信息
     *
     * @param elderId 老人ID
     * @return 入住人详细信息
     */
    public ResidentVO selectResidentDetail(Long elderId);

    /**
     * 入住人续费
     *
     * @param renewDTO 续费信息
     * @param userId 操作用户ID
     * @return 结果
     */
    public int renewResident(RenewDTO renewDTO, Long userId);
}
