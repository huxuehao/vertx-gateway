package com.hxh.gateway.core.api.handler;

import com.hxh.gateway.common.enums.ApiAccessLimitType;
import com.hxh.gateway.common.option.ApiConfig;
import com.hxh.gateway.core.api.RouteHandler;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：路由访问控制处理
 *
 * @author huxuehao
 **/
public class RouteAccessLimitHandler extends RouteHandler {
    private LimitMonitor limitMonitor;

    /**
     * 限制次数为0时，表示不进行限制
     * 路由访问次数 和 IP访问次数不可同时大于0，若同时大于0有有策略上的冲突
     * @param rct 路由上下文
     */
    @Override
    public void handle(RoutingContext rct) {
        // 字段有效性检查
        if (checkFieldIsNull()) {
            endRoutingFieldNull(rct);
            return;
        }

        ApiConfig config = apiOption.getConfig();

        // 判断是否开启访问限制
        if (ApiAccessLimitType.NONE == config.getAccessLimitType()) {
            rct.next();
            return;
        }

        // 访问次数皆<=0，这不进行访问限制
        if (config.getIpTimes() <= 0 && config.getRouterTimes() <= 0) {
            config.setAccessLimitType(ApiAccessLimitType.NONE);
            rct.next();
            return;
        }

        // 初始化limitMonitor
        if (limitMonitor == null) {
            limitMonitor = new LimitMonitor(config.getIpTimes(), config.getRouterTimes());
            limitMonitor.setAccessTimes(1L);
            if (limitMonitor.getIpTopTimes() > 0) {
                String host = rct.request().remoteAddress().host();
                limitMonitor.addIpAccessTimes(host, 1L);
            }
            rct.next();
            return;
        }

        String host = rct.request().remoteAddress().host();

        // IP访问次数有效时
        if (limitMonitor.getIpTopTimes() > 0) {
            Map<String, Long> ipAccessTimes = limitMonitor.getIpAccessTimes();
            ipAccessTimes.putIfAbsent(host, 1L);
            // 成立条件：IP访问次数超出了上线
            if (ipAccessTimes.get(host) >= limitMonitor.ipTopTimes) {
                Instant oldTime = limitMonitor.getTimePoints().plusSeconds(config.getAccessLimitType().getSeconds());
                Duration between = Duration.between(Instant.now(), oldTime);
                // 成立条件：还在时间单位以内
                if (between.getSeconds() > 0) {
                    if (rct.response().ended()) {
                        endRouting(rct, 402, "请求次数超过访问限制，请稍后再试");
                    }
                } else {
                    limitMonitor.setTimePoints(Instant.now());
                    limitMonitor.clearIpAccessTimes();
                    limitMonitor.setAccessTimes(1L);
                    rct.next();
                }

            } else {
                limitMonitor.getIpAccessTimes().put(host, ipAccessTimes.get(host) + 1L);
                limitMonitor.setAccessTimes(limitMonitor.getAccessTimes() + 1L);
                rct.next();
            }
            return;
        }

        // 路由访问次数有效时
        if (limitMonitor.getApiTopTimes() > 0) {
            if (limitMonitor.getAccessTimes() > limitMonitor.getApiTopTimes()) {
                Instant oldTime = limitMonitor.getTimePoints().plusSeconds(config.getAccessLimitType().getSeconds());
                Duration between = Duration.between(Instant.now(), oldTime);
                // 成立条件：还在时间单位以内
                if (between.getSeconds() > 0) {
                    if (rct.response().ended()) {
                        endRouting(rct, 402, "请求次数超过访问限制，请稍后再试");
                    }
                } else {
                    limitMonitor.setTimePoints(Instant.now());
                    limitMonitor.clearIpAccessTimes();
                    limitMonitor.setAccessTimes(1L);
                    if (limitMonitor.getIpTopTimes() > 0) {
                        limitMonitor.addIpAccessTimes(host, 1L);
                    }
                    rct.next();
                }
            } else {
                limitMonitor.getIpAccessTimes().put(host, limitMonitor.getIpAccessTimes().get(host) + 1L);
                limitMonitor.setAccessTimes(limitMonitor.getAccessTimes() + 1L);
                rct.next();
            }
            return;
        }

        rct.next();
    }

    @Setter
    @Getter
    static class LimitMonitor {
        private Instant timePoints = Instant.now();// 统计时间
        private Map<String, Long> ipAccessTimes = new HashMap<>();// 用户ip统计
        private long ipTopTimes = -1;// ip限制的数量
        private long apiTopTimes = -1;// api的限制数量
        private long accessTimes;// 当前的数量

        public LimitMonitor(long ipTopTimes, long apiTopTimes) {
            this.ipTopTimes = ipTopTimes;
            this.apiTopTimes = apiTopTimes;
        }
        /**
         * 给当前连接用户连接数添加一个新用户,如果存在该用户将用户数据重置
         *
         * @param ip    访问IP
         * @param times 访问次数
         */
        public void addIpAccessTimes(String ip, long times) {
            this.ipAccessTimes.put(ip, times);
        }

        /**
         * 清空用户IP统计
         */
        public void clearIpAccessTimes() {
            this.ipAccessTimes.clear();
        }

    }
}
