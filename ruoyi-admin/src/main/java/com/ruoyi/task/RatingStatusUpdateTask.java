package com.ruoyi.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.service.IInstitutionRatingService;

/**
 * 机构评级状态更新定时任务
 *
 * @author ruoyi
 */
@Component("ratingStatusUpdateTask")
public class RatingStatusUpdateTask
{
    private static final Logger log = LoggerFactory.getLogger(RatingStatusUpdateTask.class);

    @Autowired
    private IInstitutionRatingService ratingService;

    /**
     * 更新过期的评级状态
     * 将有效期已过但状态仍为有效的评级记录更新为已过期状态
     * 建议每天凌晨1点执行一次
     */
    public void updateExpiredRatingStatus()
    {
        log.info("========== 开始执行机构评级过期状态更新任务 ==========");

        try
        {
            // 先查询需要更新的数量
            int expiredCount = ratingService.countExpiredRatings();

            if (expiredCount > 0)
            {
                // 执行更新
                int updatedCount = ratingService.updateExpiredRatingStatus();
                log.info("========== 机构评级过期状态更新任务执行完成，共更新 {} 条记录 ==========", updatedCount);
            }
            else
            {
                log.info("========== 没有需要更新的过期评级记录 ==========");
            }
        }
        catch (Exception e)
        {
            log.error("机构评级过期状态更新任务执行失败", e);
        }
    }
}
