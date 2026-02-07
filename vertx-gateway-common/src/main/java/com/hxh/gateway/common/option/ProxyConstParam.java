package com.hxh.gateway.common.option;

import com.hxh.gateway.common.enums.ApiParamPosition;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：代理接口常量参数
 *
 * @author huxuehao
 **/
@Getter
@Setter
public class ProxyConstParam {
    // 位置（query、path、header）
    private ApiParamPosition position;
    // 键
    private String key;
    // 值
    private String value;
    // 描述
    private String remark;
}
