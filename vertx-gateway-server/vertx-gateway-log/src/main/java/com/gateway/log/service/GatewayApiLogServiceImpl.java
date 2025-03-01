package com.gateway.log.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gateway.common.entity.GatewayApiLog;
import com.gateway.log.mapper.GatewayApiLogMapper;
import org.springframework.stereotype.Service;

/**
 * 描述：API日志
 *
 * @author huxuehao
 **/
@Service
public class GatewayApiLogServiceImpl extends ServiceImpl<GatewayApiLogMapper, GatewayApiLog> implements GatewayApiLogService {
}
