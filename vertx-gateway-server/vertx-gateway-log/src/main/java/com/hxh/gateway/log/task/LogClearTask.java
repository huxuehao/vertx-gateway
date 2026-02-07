package com.hxh.gateway.log.task;

import com.hxh.gateway.log.service.GatewayApiLogService;
import com.hxh.gateway.params.core.ParamsAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述：日志清除
 *
 * @author huxuehao
 **/
@Component
public class LogClearTask {
    private static final Logger log = LoggerFactory.getLogger(LogClearTask.class);

    private final ParamsAdapter paramsAdapter;
    private final GatewayApiLogService gatewayApiLogService;

    public LogClearTask(ParamsAdapter paramsAdapter, GatewayApiLogService gatewayApiLogService) {
        this.paramsAdapter = paramsAdapter;
        this.gatewayApiLogService = gatewayApiLogService;
    }

    /**
     * 每天晚上2点0分执行一次
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void clear() {
        log.info("===== 日志清除任务开始执行 =====");
        Integer logSaveDays = null;
        try {
            String daysStr = paramsAdapter.getValue("LOG_SAVE_DAYS");
            log.info("从配置中获取日志保留天数参数：LOG_SAVE_DAYS = {}", daysStr);

            logSaveDays = Integer.parseInt(daysStr);
            if (logSaveDays <= 0) {
                log.warn("日志保留天数参数不合法（必须为正整数），当前值：{}", logSaveDays);
                return;
            }
            log.info("日志保留天数解析成功，将清除{}天前的日志", logSaveDays);

            long startTime = System.currentTimeMillis();
            int deleteCount = gatewayApiLogService.clearLogBeforeDays(logSaveDays);
            long costTime = System.currentTimeMillis() - startTime;

            log.info("日志清除完成：共删除{}条{}天前的日志，耗时{}ms", deleteCount, logSaveDays, costTime);
        } catch (NumberFormatException e) {
            log.error("日志保留天数参数格式错误（需为整数），原始值：{}",
                    paramsAdapter.getValue("LOG_SAVE_DAYS"), e);
        } catch (Exception e) {
            log.error("日志清除任务执行失败，保留天数：{}", logSaveDays, e);
        } finally {
            log.info("===== 日志清除任务执行结束 =====");
        }
    }
}
