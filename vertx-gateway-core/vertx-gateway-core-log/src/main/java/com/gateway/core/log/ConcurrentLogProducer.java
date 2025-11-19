package com.gateway.core.log;

import com.gateway.common.entity.GatewayApiLog;
import com.gateway.common.util.BeanUtil;
import lombok.Getter;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 描述：日志生产者
 *
 * @author huxuehao
 **/
public class ConcurrentLogProducer {
    private static final Object INIT_LOCK = new Object();
    @Getter
    private static final LinkedBlockingQueue<GatewayApiLog> queue = new LinkedBlockingQueue<>(50000);
    private static volatile ConcurrentLogConsumer logConsumer = null;

    public static void pushLog(GatewayApiLog log) {
        if (logConsumer == null) {
            synchronized (INIT_LOCK) {
                if (logConsumer == null) {
                    logConsumer = BeanUtil.getBean(ConcurrentLogConsumer.class);
                    logConsumer.tryStart();
                }
            }
        }

        log.setEndTime(System.currentTimeMillis());
        log.setCreateTime(new Date());

        try {
            // 尝试加入队列, 如果队列已满，则阻塞, 等待3毫秒
            queue.offer(log, 3, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {}
    }

}
