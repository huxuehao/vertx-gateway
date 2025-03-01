package com.gateway.core.log;

import com.gateway.common.entity.GatewayApiLog;
import com.gateway.common.util.BeanUtil;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 描述：日志生产者
 *
 * @author huxuehao
 **/
public class ConcurrentLogProducer {
    /**
     * 最大10万条日志
     */
    private static final int MAX_CAPACITY = 100000;
    private static final LinkedBlockingQueue<GatewayApiLog> queue = new LinkedBlockingQueue<>(MAX_CAPACITY);

    private static ConcurrentLogConsumer logConsumer = null;

    public static LinkedBlockingQueue<GatewayApiLog> getQueue() {
        return queue;
    }

    public static void pushLog(GatewayApiLog log) {
        try {
            if (queue.size() >= MAX_CAPACITY - 100) {
                return;
            }

            if (logConsumer == null) {
                logConsumer = BeanUtil.getBean(ConcurrentLogConsumer.class);
            }

            log.setEndTime(System.currentTimeMillis());
            log.setCreateTime(new Date());
            queue.put(log);
            logConsumer.tryStart();
        } catch (InterruptedException ignored) {}
    }

}
