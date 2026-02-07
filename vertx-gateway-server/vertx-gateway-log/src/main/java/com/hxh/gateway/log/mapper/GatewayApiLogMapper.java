package com.hxh.gateway.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxh.gateway.common.entity.GatewayApiLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 描述：API日志
 *
 * @author huxuehao
 **/
@Mapper
public interface GatewayApiLogMapper extends BaseMapper<GatewayApiLog> {
}
