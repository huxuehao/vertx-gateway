package com.hxh.gateway.common.option;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：网关接口
 *
 * @author huxuehao
 **/
@Getter
@Setter
public class GatewayApiOption {
    private Long id;
    private Long appId;
    private ApiConfig config;

    public GatewayApiOption() {
    }

    public GatewayApiOption(Long id, Long appId, ApiConfig config) {
        this.id = id;
        this.appId = appId;
        this.config = config;
    }
}
