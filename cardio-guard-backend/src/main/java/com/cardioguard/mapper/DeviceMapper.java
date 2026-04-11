package com.cardioguard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cardioguard.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备Mapper接口
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
    
    /**
     * 根据用户ID查询设备列表
     */
    List<Device> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据序列号查询设备
     */
    Device selectBySerialNumber(@Param("serialNumber") String serialNumber);
    
    /**
     * 查询在线设备列表
     */
    List<Device> selectOnlineDevices();
}
