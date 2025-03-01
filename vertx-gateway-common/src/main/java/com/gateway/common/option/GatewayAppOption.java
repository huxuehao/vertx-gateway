package com.gateway.common.option;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：网关应用
 *
 * @author huxuehao
 **/
@Getter
@Setter
public class GatewayAppOption  {
    private Long id;
    private AppConfig config;

    public GatewayAppOption(Long id, AppConfig config) {
        this.id = id;
        this.config = config;
    }
}
