package com.cardioguard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cardioguard.entity.Device;
import com.cardioguard.entity.User;
import com.cardioguard.mapper.DeviceMapper;
import com.cardioguard.mapper.UserMapper;
import com.cardioguard.service.UserService;
import com.cardioguard.utils.JwtUtil;
import com.cardioguard.utils.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final DeviceMapper deviceMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(User user) {
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (user.getPhone() != null && userMapper.selectByPhone(user.getPhone()) != null) {
            throw new RuntimeException("手机号已被注册");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        log.info("用户注册成功: {}", user.getUsername());
        
        return user;
    }
    
    @Override
    public String login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        log.info("用户登录成功: {}", username);
        
        return token;
    }
    
    @Override
    public String refreshToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token无效");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }
    
    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return user;
    }
    
    @Override
    public User getByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
    
    @Override
    public Page<User> list(Integer pageNum, Integer pageSize, String role) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (role != null && !role.isEmpty()) {
            wrapper.eq(User::getRole, role);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(page, wrapper);
        
        // 清除密码信息
        result.getRecords().forEach(user -> user.setPassword(null));
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(User user) {
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 如果修改密码,需要加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }
        
        user.setUpdateTime(LocalDateTime.now());
        int rows = userMapper.updateById(user);
        return rows > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        int rows = userMapper.deleteById(id);
        log.info("用户删除成功: {}", user.getUsername());
        return rows > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindDevice(Long userId, String serialNumber) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        Device device = deviceMapper.selectBySerialNumber(serialNumber);
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }
        
        if (device.getUserId() != null) {
            throw new RuntimeException("设备已被绑定");
        }
        
        device.setUserId(userId);
        device.setUpdateTime(LocalDateTime.now());
        
        int rows = deviceMapper.updateById(device);
        log.info("设备绑定成功: 用户ID={}, 设备序列号={}", userId, serialNumber);
        return rows > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindDevice(Long userId, Long deviceId) {
        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }
        
        if (!userId.equals(device.getUserId())) {
            throw new RuntimeException("设备不属于该用户");
        }
        
        device.setUserId(null);
        device.setUpdateTime(LocalDateTime.now());
        
        int rows = deviceMapper.updateById(device);
        log.info("设备解绑成功: 用户ID={}, 设备ID={}", userId, deviceId);
        return rows > 0;
    }
}
