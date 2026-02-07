package com.hxh.gateway.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxh.gateway.common.entity.GatewayClientAuth;
import org.apache.ibatis.annotations.Mapper;

/**
 * 描述：客户端网关授权
 *
 * @author huxuehao
 **/
@Mapper
public interface GatewayClientAuthMapper extends BaseMapper<GatewayClientAuth> {
}
