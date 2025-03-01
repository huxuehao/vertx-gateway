package com.gateway.common.option;

import com.gateway.common.enums.ApiParamPosition;
import com.gateway.common.enums.ApiParamType;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：路由请求参数
 *
 * @author huxuehao
 **/
@Getter
@Setter
public class RouteParam {
    // 参数位置（QUERY、PATH、HEADER）
    private ApiParamPosition position;
    // 键（名称）
    private String key;
    // 类型（String、Long、Integer、Float、Double、Boolean）
    private ApiParamType type;
    // 是否必填
    private Boolean required;
    // 默认值
    private String defaultVal;
    // 描述
    private String remark;
}
