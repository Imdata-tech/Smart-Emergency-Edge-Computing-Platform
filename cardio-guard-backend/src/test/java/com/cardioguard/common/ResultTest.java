package com.cardioguard.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 统一响应结果测试类
 */
class ResultTest {
    
    @Test
    void testSuccess() {
        Result<String> result = Result.success("测试数据");
        
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("测试数据", result.getData());
        assertNotNull(result.getTimestamp());
    }
    
    @Test
    void testSuccessWithMessage() {
        Result<String> result = Result.success("自定义消息", "测试数据");
        
        assertEquals(200, result.getCode());
        assertEquals("自定义消息", result.getMessage());
        assertEquals("测试数据", result.getData());
    }
    
    @Test
    void testError() {
        Result<String> result = Result.error("错误消息");
        
        assertEquals(500, result.getCode());
        assertEquals("错误消息", result.getMessage());
        assertNull(result.getData());
    }
    
    @Test
    void testErrorWithCode() {
        Result<String> result = Result.error(400, "参数错误");
        
        assertEquals(400, result.getCode());
        assertEquals("参数错误", result.getMessage());
        assertNull(result.getData());
    }
    
    @Test
    void testBadRequest() {
        Result<String> result = Result.badRequest("请求参数错误");
        
        assertEquals(400, result.getCode());
        assertEquals("请求参数错误", result.getMessage());
    }
    
    @Test
    void testUnauthorized() {
        Result<String> result = Result.unauthorized("未授权");
        
        assertEquals(401, result.getCode());
        assertEquals("未授权", result.getMessage());
    }
    
    @Test
    void testNotFound() {
        Result<String> result = Result.notFound("资源不存在");
        
        assertEquals(404, result.getCode());
        assertEquals("资源不存在", result.getMessage());
    }
}
