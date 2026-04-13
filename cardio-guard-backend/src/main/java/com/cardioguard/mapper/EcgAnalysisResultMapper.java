package com.cardioguard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cardioguard.entity.EcgAnalysisResult;
import org.apache.ibatis.annotations.Mapper;

/**
 * ECG分析结果Mapper接口
 */
@Mapper
public interface EcgAnalysisResultMapper extends BaseMapper<EcgAnalysisResult> {
}
