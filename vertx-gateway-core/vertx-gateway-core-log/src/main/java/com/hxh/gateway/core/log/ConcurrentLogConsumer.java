package com.hxh.gateway.core.log;

import com.hxh.gateway.common.entity.GatewayApiLog;
import com.hxh.gateway.log.mapper.GatewayApiLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 描述：日志消费者
 *
 * @author huxuehao
 **/
@Slf4j
@Component
public class ConcurrentLogConsumer {
    private static final Object START_LOCK = new Object();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile ExecutorService consumerExecutor;

    private final BlockingQueue<GatewayApiLog> queue;
    private final GatewayApiLogMapper gatewayApiLogMapper;


    public ConcurrentLogConsumer(GatewayApiLogMapper gatewayApiLogMapper) {
        this.gatewayApiLogMapper = gatewayApiLogMapper;
        this.queue = ConcurrentLogProducer.getQueue();
    }

    /**
     * 其他线程调用该类，将尝试启动日志消费者消费
     */
    public void tryStart() {
        if (!running.get()) {
            synchronized (START_LOCK) {
                if (!running.get()) {
                    consumerExecutor = Executors.newSingleThreadExecutor(r -> {
                        Thread t = new Thread(r);
                        t.setName("Gateway-Log-Consumer");
                        t.setDaemon(true);
                        return t;
                    });

                    consumerExecutor.submit(this::consumeLog);
                    running.set(true);
                }
            }
        }
    }

    /**
     * 执行日志消费
     */
    private void consumeLog() {
        while (!Thread.currentThread().isInterrupted() && running.get()) {
            try {
                GatewayApiLog apiLog = queue.take();
                try {
                    gatewayApiLogMapper.insert(apiLog);
                } catch (Exception e) {
                    log.error("日志插入失败: {}", e.getMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @PreDestroy
    public void destroy() {
        running.set(false);
        if (consumerExecutor != null) {
            consumerExecutor.shutdownNow();
        }
    }
}
