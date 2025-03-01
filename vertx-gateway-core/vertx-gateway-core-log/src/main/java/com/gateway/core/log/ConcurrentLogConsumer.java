package com.gateway.core.log;

import com.gateway.common.entity.GatewayApiLog;
import com.gateway.log.mapper.GatewayApiLogMapper;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 描述：日志消费者
 *
 * @author huxuehao
 **/
@Component
public class ConcurrentLogConsumer {
    // 日志线程
    private static volatile Thread thread;
    private static final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final BlockingQueue<GatewayApiLog> queue;
    private final GatewayApiLogMapper gatewayApiLogMapper;

    public ConcurrentLogConsumer(GatewayApiLogMapper gatewayApiLogMapper) {
        this.queue = ConcurrentLogProducer.getQueue();
        this.gatewayApiLogMapper = gatewayApiLogMapper;
    }

    /**
     * 其他线程调用该类，将尝试启动日志消费者消费
     */
    public void tryStart() {
        if (isRunning.get()) {
            return;
        }
        if (thread == null || !thread.isAlive()) {
            if (isRunning.compareAndSet(false, true)) {
                thread = new Thread(this::doLogConsume);
                thread.start();
            }
        }
    }

    /**
     * 执行日志消费
     */
    private void doLogConsume() {
        while (!Thread.interrupted()) {
            try {
                GatewayApiLog apiLog = queue.take();
                try {
                    gatewayApiLogMapper.insert(apiLog);
                } catch (Exception e) {
                    System.err.println("日志插入失败: " + e.getMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
