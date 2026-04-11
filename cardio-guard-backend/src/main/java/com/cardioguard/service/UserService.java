package com.cardioguard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cardioguard.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    User register(User user);
    
    /**
     * 用户登录
     */
    String login(String username, String password);
    
    /**
     * 刷新Token
     */
    String refreshToken(String token);
    
    /**
     * 根据ID查询用户
     */
    User getById(Long id);
    
    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
    
    /**
     * 分页查询用户列表
     */
    Page<User> list(Integer pageNum, Integer pageSize, String role);
    
    /**
     * 更新用户信息
     */
    boolean update(User user);
    
    /**
     * 删除用户
     */
    boolean delete(Long id);
    
    /**
     * 绑定设备
     */
    boolean bindDevice(Long userId, String serialNumber);
    
    /**
     * 解绑设备
     */
    boolean unbindDevice(Long userId, Long deviceId);
}
