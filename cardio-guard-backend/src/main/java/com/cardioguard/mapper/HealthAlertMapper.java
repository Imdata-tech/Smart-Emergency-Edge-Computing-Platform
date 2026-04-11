package com.cardioguard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cardioguard.entity.HealthAlert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 健康预警Mapper接口
 */
@Mapper
public interface HealthAlertMapper extends BaseMapper<HealthAlert> {
    
    /**
     * 根据用户ID查询预警列表
     */
    List<HealthAlert> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 查询未处理的预警列表
     */
    List<HealthAlert> selectUnprocessedAlerts();
    
    /**
     * 统计用户未读预警数量
     */
    Integer countUnreadByUserId(@Param("userId") Long userId);
}
