package com.cardioguard.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数
 */
@Data
public class PageRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 当前页码(从1开始)
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 排序字段
     */
    private String sortBy;
    
    /**
     * 排序方向(asc/desc)
     */
    private String sortOrder = "desc";
    
    public PageRequest() {
    }
    
    public PageRequest(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
    
    /**
     * 获取偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
