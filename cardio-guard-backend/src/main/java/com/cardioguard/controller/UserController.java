package com.cardioguard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cardioguard.common.Result;
import com.cardioguard.entity.User;
import com.cardioguard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、登录、信息查询等接口")
public class UserController {
    
    private final UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public Result<User> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            registeredUser.setPassword(null);
            return Result.success("注册成功", registeredUser);
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口,返回JWT Token")
    public Result<Map<String, Object>> login(
            @Parameter(description = "用户名") @RequestParam String username,
            @Parameter(description = "密码") @RequestParam String password) {
        try {
            String token = userService.login(username, password);
            User user = userService.getByUsername(username);
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            
            return Result.success("登录成功", data);
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token", description = "使用旧Token刷新新Token")
    public Result<String> refreshToken(@Parameter(description = "旧Token") @RequestParam String token) {
        try {
            String newToken = userService.refreshToken(token);
            return Result.success("刷新成功", newToken);
        } catch (Exception e) {
            log.error("刷新Token失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息", description = "根据ID获取用户详细信息")
    public Result<User> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        try {
            User user = userService.getById(id);
            return Result.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping
    @Operation(summary = "更新用户信息", description = "更新用户个人信息")
    public Result<String> updateUser(@RequestBody User user) {
        try {
            boolean success = userService.update(user);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据ID删除用户(逻辑删除)")
    public Result<String> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        try {
            boolean success = userService.delete(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户列表", description = "分页查询用户列表,支持按角色筛选")
    public Result<Page<User>> listUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "角色") @RequestParam(required = false) String role) {
        try {
            Page<User> page = userService.list(pageNum, pageSize, role);
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 绑定设备
     */
    @PostMapping("/bind-device")
    @Operation(summary = "绑定设备", description = "为用户绑定可穿戴设备")
    public Result<String> bindDevice(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "设备序列号") @RequestParam String serialNumber) {
        try {
            boolean success = userService.bindDevice(userId, serialNumber);
            if (success) {
                return Result.success("设备绑定成功");
            } else {
                return Result.error("设备绑定失败");
            }
        } catch (Exception e) {
            log.error("绑定设备失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 解绑设备
     */
    @PostMapping("/unbind-device")
    @Operation(summary = "解绑设备", description = "为用户解绑可穿戴设备")
    public Result<String> unbindDevice(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "设备ID") @RequestParam Long deviceId) {
        try {
            boolean success = userService.unbindDevice(userId, deviceId);
            if (success) {
                return Result.success("设备解绑成功");
            } else {
                return Result.error("设备解绑失败");
            }
        } catch (Exception e) {
            log.error("解绑设备失败", e);
            return Result.error(e.getMessage());
        }
    }
}
