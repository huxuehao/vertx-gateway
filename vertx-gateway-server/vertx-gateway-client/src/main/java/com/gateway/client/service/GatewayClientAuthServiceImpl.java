package com.gateway.client.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gateway.client.mapper.GatewayClientAuthMapper;
import com.gateway.common.entity.GatewayClientAuth;
import org.springframework.stereotype.Service;

/**
 * 描述：客户端网关授权
 *
 * @author huxuehao
 **/
@Service
public class GatewayClientAuthServiceImpl extends ServiceImpl<GatewayClientAuthMapper, GatewayClientAuth> implements GatewayClientAuthService {
}
