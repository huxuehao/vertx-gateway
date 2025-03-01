package com.gateway.white.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gateway.common.entity.GatewayWhiteList;
import com.gateway.white.mapper.GatewayWhiteListMapper;
import org.springframework.stereotype.Service;

/**
 * 描述：网关白名单
 *
 * @author huxuehao
 **/
@Service
public class GatewayWhiteListServiceImpl extends ServiceImpl<GatewayWhiteListMapper, GatewayWhiteList> implements GatewayWhiteListService {
}
