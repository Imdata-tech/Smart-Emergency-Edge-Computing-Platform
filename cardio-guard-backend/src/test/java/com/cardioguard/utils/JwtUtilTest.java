package com.cardioguard.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 */
class JwtUtilTest {
    
    private JwtUtil jwtUtil;
    private final Long testUserId = 1L;
    private final String testUsername = "testuser";
    private final String testRole = "PATIENT";
    
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 设置测试配置
        setField(jwtUtil, "secret", "testSecretKeyForJWTTokenGeneration2024");
        setField(jwtUtil, "expiration", 86400000L);
    }
    
    @Test
    void testGenerateAndValidateToken() {
        // 生成Token
        String token = jwtUtil.generateToken(testUserId, testUsername, testRole);
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // 验证Token
        assertTrue(jwtUtil.validateToken(token));
        
        // 从Token中获取信息
        assertEquals(testUserId, jwtUtil.getUserIdFromToken(token));
        assertEquals(testUsername, jwtUtil.getUsernameFromToken(token));
        assertEquals(testRole, jwtUtil.getRoleFromToken(token));
    }
    
    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtUtil.validateToken(invalidToken));
        assertNull(jwtUtil.getUserIdFromToken(invalidToken));
    }
    
    @Test
    void testRefreshToken() {
        String oldToken = jwtUtil.generateToken(testUserId, testUsername, testRole);
        String newToken = jwtUtil.refreshToken(oldToken);
        
        assertNotNull(newToken);
        assertNotEquals(oldToken, newToken);
        assertTrue(jwtUtil.validateToken(newToken));
    }
    
    /**
     * 通过反射设置字段值
     */
    private void setField(Object obj, String fieldName, Object value) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
