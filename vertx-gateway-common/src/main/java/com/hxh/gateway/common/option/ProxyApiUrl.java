package com.hxh.gateway.common.option;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：代理接口url
 *
 * @author huxuehao
 **/
@Getter
@Setter
public class ProxyApiUrl {
    // 代理路径
    private String url;
    // 权重
    private Integer weight;

    public ProxyApiUrl() {
    }

    public ProxyApiUrl(String url, Integer weight) {
        this.url = url;
        this.weight = weight;
    }
}
