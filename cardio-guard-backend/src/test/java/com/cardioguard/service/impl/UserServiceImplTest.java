package com.cardioguard.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cardioguard.entity.Device;
import com.cardioguard.entity.User;
import com.cardioguard.mapper.DeviceMapper;
import com.cardioguard.mapper.UserMapper;
import com.cardioguard.utils.JwtUtil;
import com.cardioguard.utils.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private DeviceMapper deviceMapper;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private User testUser;
    private Device testDevice;
    
    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword123");
        testUser.setRealName("测试用户");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setRole("PATIENT");
        testUser.setStatus(1);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
        
        // 初始化测试设备
        testDevice = new Device();
        testDevice.setId(1L);
        testDevice.setSerialNumber("SN123456789");
        testDevice.setDeviceName("心率监测仪");
        testDevice.setModel("HR-100");
        testDevice.setDeviceType("HEART_RATE");
        testDevice.setManufacturer("Test Manufacturer");
        testDevice.setStatus(1);
        testDevice.setBatteryLevel(85);
        testDevice.setCreateTime(LocalDateTime.now());
        testDevice.setUpdateTime(LocalDateTime.now());
    }
    
    @Test
    void testRegister_Success() {
        // 准备数据
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setPhone("13900139000");
        
        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.selectByPhone("13900139000")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);
        
        // 执行测试
        User result = userService.register(newUser);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(userMapper, times(1)).insert(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }
    
    @Test
    void testRegister_UsernameExists() {
        // 准备数据
        User existingUser = new User();
        existingUser.setUsername("testuser");
        
        when(userMapper.selectByUsername("testuser")).thenReturn(existingUser);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(testUser);
        });
        
        assertEquals("用户名已存在", exception.getMessage());
    }
    
    @Test
    void testRegister_PhoneExists() {
        // 准备数据
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setPhone("13800138000");
        
        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.selectByPhone("13800138000")).thenReturn(testUser);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(newUser);
        });
        
        assertEquals("手机号已被注册", exception.getMessage());
    }
    
    @Test
    void testLogin_Success() {
        // 准备数据
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "testuser", "PATIENT")).thenReturn("jwt-token-123");
        
        // 执行测试
        String token = userService.login("testuser", "password123");
        
        // 验证结果
        assertNotNull(token);
        assertEquals("jwt-token-123", token);
        verify(userMapper, times(1)).updateById(any(User.class));
    }
    
    @Test
    void testLogin_UserNotFound() {
        // 准备数据
        when(userMapper.selectByUsername("nonexistent")).thenReturn(null);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("nonexistent", "password123");
        });
        
        assertEquals("用户名或密码错误", exception.getMessage());
    }
    
    @Test
    void testLogin_AccountDisabled() {
        // 准备数据
        User disabledUser = new User();
        disabledUser.setId(1L);
        disabledUser.setUsername("testuser");
        disabledUser.setStatus(0); // 禁用状态
        
        when(userMapper.selectByUsername("testuser")).thenReturn(disabledUser);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("testuser", "password123");
        });
        
        assertEquals("账号已被禁用", exception.getMessage());
    }
    
    @Test
    void testLogin_WrongPassword() {
        // 准备数据
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword123")).thenReturn(false);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("testuser", "wrongpassword");
        });
        
        assertEquals("用户名或密码错误", exception.getMessage());
    }
    
    @Test
    void testRefreshToken_Success() {
        // 准备数据
        String token = "valid-token";
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(1L);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(jwtUtil.generateToken(1L, "testuser", "PATIENT")).thenReturn("new-token");
        
        // 执行测试
        String newToken = userService.refreshToken(token);
        
        // 验证结果
        assertNotNull(newToken);
        assertEquals("new-token", newToken);
    }
    
    @Test
    void testRefreshToken_InvalidToken() {
        // 准备数据
        String invalidToken = "invalid-token";
        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.refreshToken(invalidToken);
        });
        
        assertEquals("Token无效", exception.getMessage());
    }
    
    @Test
    void testGetById_Success() {
        // 准备数据
        when(userMapper.selectById(1L)).thenReturn(testUser);
        
        // 执行测试
        User result = userService.getById(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getPassword()); // 密码应该被清除
    }
    
    @Test
    void testGetById_UserNotFound() {
        // 准备数据
        when(userMapper.selectById(999L)).thenReturn(null);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getById(999L);
        });
        
        assertEquals("用户不存在", exception.getMessage());
    }
    
    @Test
    void testGetByUsername_Success() {
        // 准备数据
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        
        // 执行测试
        User result = userService.getByUsername("testuser");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getPassword()); // 密码应该被清除
    }
    
    @Test
    void testList_Success() {
        // 准备数据
        Page<User> page = new Page<>(1, 10);
        page.setRecords(java.util.Arrays.asList(testUser));
        page.setTotal(1);
        
        when(userMapper.selectPage(any(Page.class), any())).thenReturn(page);
        
        // 执行测试
        Page<User> result = userService.list(1, 10, "PATIENT");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertNull(result.getRecords().get(0).getPassword()); // 密码应该被清除
    }
    
    @Test
    void testUpdate_Success() {
        // 准备数据
        User updateUser = new User();
        updateUser.setId(1L);
        updateUser.setRealName("更新后的姓名");
        
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);
        
        // 执行测试
        boolean result = userService.update(updateUser);
        
        // 验证结果
        assertTrue(result);
        verify(userMapper, times(1)).updateById(any(User.class));
    }
    
    @Test
    void testUpdate_UserNotFound() {
        // 准备数据
        User updateUser = new User();
        updateUser.setId(999L);
        
        when(userMapper.selectById(999L)).thenReturn(null);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.update(updateUser);
        });
        
        assertEquals("用户不存在", exception.getMessage());
    }
    
    @Test
    void testDelete_Success() {
        // 准备数据
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.deleteById(1L)).thenReturn(1);
        
        // 执行测试
        boolean result = userService.delete(1L);
        
        // 验证结果
        assertTrue(result);
        verify(userMapper, times(1)).deleteById(1L);
    }
    
    @Test
    void testDelete_UserNotFound() {
        // 准备数据
        when(userMapper.selectById(999L)).thenReturn(null);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.delete(999L);
        });
        
        assertEquals("用户不存在", exception.getMessage());
    }
    
    @Test
    void testBindDevice_Success() {
        // 准备数据
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(deviceMapper.selectBySerialNumber("SN123456789")).thenReturn(testDevice);
        when(deviceMapper.updateById(any(Device.class))).thenReturn(1);
        
        // 执行测试
        boolean result = userService.bindDevice(1L, "SN123456789");
        
        // 验证结果
        assertTrue(result);
        verify(deviceMapper, times(1)).updateById(any(Device.class));
    }
    
    @Test
    void testBindDevice_UserNotFound() {
        // 准备数据
        when(userMapper.selectById(999L)).thenReturn(null);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.bindDevice(999L, "SN123456789");
        });
        
        assertEquals("用户不存在", exception.getMessage());
    }
    
    @Test
    void testBindDevice_DeviceNotFound() {
        // 准备数据
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(deviceMapper.selectBySerialNumber("NONEXISTENT")).thenReturn(null);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.bindDevice(1L, "NONEXISTENT");
        });
        
        assertEquals("设备不存在", exception.getMessage());
    }
    
    @Test
    void testBindDevice_DeviceAlreadyBound() {
        // 准备数据
        Device boundDevice = new Device();
        boundDevice.setId(1L);
        boundDevice.setUserId(2L); // 已被其他用户绑定
        
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(deviceMapper.selectBySerialNumber("SN123456789")).thenReturn(boundDevice);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.bindDevice(1L, "SN123456789");
        });
        
        assertEquals("设备已被绑定", exception.getMessage());
    }
    
    @Test
    void testUnbindDevice_Success() {
        // 准备数据
        testDevice.setUserId(1L);
        when(deviceMapper.selectById(1L)).thenReturn(testDevice);
        when(deviceMapper.updateById(any(Device.class))).thenReturn(1);
        
        // 执行测试
        boolean result = userService.unbindDevice(1L, 1L);
        
        // 验证结果
        assertTrue(result);
        verify(deviceMapper, times(1)).updateById(any(Device.class));
    }
    
    @Test
    void testUnbindDevice_DeviceNotFound() {
        // 准备数据
        when(deviceMapper.selectById(999L)).thenReturn(null);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.unbindDevice(1L, 999L);
        });
        
        assertEquals("设备不存在", exception.getMessage());
    }
    
    @Test
    void testUnbindDevice_DeviceNotOwned() {
        // 准备数据
        testDevice.setUserId(2L); // 属于其他用户
        when(deviceMapper.selectById(1L)).thenReturn(testDevice);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.unbindDevice(1L, 1L);
        });
        
        assertEquals("设备不属于该用户", exception.getMessage());
    }
}
