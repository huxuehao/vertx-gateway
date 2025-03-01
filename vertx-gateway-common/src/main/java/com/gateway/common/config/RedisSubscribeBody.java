package com.gateway.common.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：redis订阅body
 *
 * @author huxuehao
 **/
@Data
public class RedisSubscribeBody implements Serializable {
    static final long serialVersionUID = 1L;

    private String uuid;
    private List<Long> ids;
    private List<?> entity;
    private String remark;

    public RedisSubscribeBody() {
    }

    public RedisSubscribeBody(String uuid, List<Long> ids) {
        this.uuid = uuid;
        this.ids = ids;
    }
}
