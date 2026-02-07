package com.hxh.gateway.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxh.gateway.common.entity.GatewayApiLog;

/**
 * 描述：API日志
 *
 * @author huxuehao
 **/
public interface GatewayApiLogService extends IService<GatewayApiLog> {
    /**
     * 清除指定天数前的日志
     * @param days 保留最近的天数
     */
    int clearLogBeforeDays(Integer days);
}
