package com.gateway.log.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gateway.common.entity.GatewayApiLog;
import com.gateway.log.mapper.GatewayApiLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 描述：API日志
 *
 * @author huxuehao
 **/
@Service
public class GatewayApiLogServiceImpl extends ServiceImpl<GatewayApiLogMapper, GatewayApiLog> implements GatewayApiLogService {
    @Override
    public int clearLogBeforeDays(Integer days) {
        // 1. 参数校验
        if (days == null || days <= 0) {
            throw new IllegalArgumentException("清除天数必须为正整数（days > 0）");
        }

        // 2. 计算“days天前”的时间阈值
        LocalDateTime thresholdLdt = LocalDateTime.now().minusDays(days);
        Date thresholdTime = Date.from(thresholdLdt.atZone(ZoneId.systemDefault()).toInstant());

        // 3. 构造删除条件：createTime < 阈值时间
        LambdaQueryWrapper<GatewayApiLog> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.lt(GatewayApiLog::getCreateTime, thresholdTime);
        // 分批次删除（每次删1000条，避免大事务锁表）
        deleteWrapper.last("LIMIT 1000");

        // 4. 循环删除直到无数据
        int totalDelete = 0;
        int batchDelete;
        do {
            batchDelete = baseMapper.delete(deleteWrapper);
            totalDelete += batchDelete;
        } while (batchDelete > 0);

        return totalDelete;
    }
}
