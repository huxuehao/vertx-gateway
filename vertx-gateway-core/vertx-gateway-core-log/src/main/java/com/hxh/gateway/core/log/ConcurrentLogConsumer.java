package com.hxh.gateway.core.log;

import com.hxh.gateway.common.entity.GatewayApiLog;
import com.hxh.gateway.log.service.GatewayApiLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    /** 批量消费的最大条数 */
    private static final int BATCH_SIZE = 200;
    /** 队列为空时的等待时间（毫秒），避免CPU空转 */
    private static final int POLL_TIMEOUT_MS = 500;

    private final BlockingQueue<GatewayApiLog> queue;
    private final GatewayApiLogService gatewayApiLogService;

    public ConcurrentLogConsumer(GatewayApiLogService gatewayApiLogService) {
        this.gatewayApiLogService = gatewayApiLogService;
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
     * 执行日志消费 - 批量模式
     * 循环从队列中取数据，积攒到 BATCH_SIZE 或等待超时后，一次性批量插入
     */
    private void consumeLog() {
        List<GatewayApiLog> batch = new ArrayList<>(BATCH_SIZE);

        while (!Thread.currentThread().isInterrupted() && running.get()) {
            try {
                // 先阻塞取一条，保证队列为空时不会CPU空转
                GatewayApiLog first = queue.poll(POLL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (first != null) {
                    batch.add(first);
                }

                // 非阻塞地继续取，积攒到 BATCH_SIZE 或队列为空为止
                queue.drainTo(batch, BATCH_SIZE - batch.size());

                if (!batch.isEmpty()) {
                    doBatchInsert(batch);
                    batch.clear();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 退出前尝试把剩余日志入库
                if (!batch.isEmpty()) {
                    doBatchInsert(batch);
                }
                break;
            }
        }
    }

    /**
     * 执行批量插入，失败时降级为逐条插入以保证尽可能多的日志入库
     */
    private void doBatchInsert(List<GatewayApiLog> batch) {
        if (batch.isEmpty()) {
            return;
        }
        try {
            gatewayApiLogService.saveBatch(batch, BATCH_SIZE);
        } catch (Exception e) {
            log.error("批量插入日志失败(batchSize={})，降级为逐条插入: {}", batch.size(), e.getMessage());
            // 降级：逐条插入，尽量挽救更多日志
            for (GatewayApiLog apiLog : batch) {
                try {
                    gatewayApiLogService.save(apiLog);
                } catch (Exception ex) {
                    log.error("日志插入失败: {}", ex.getMessage());
                }
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
