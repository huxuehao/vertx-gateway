package com.gateway.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gateway.common.constant.DBConst;
import com.gateway.common.mp.annotation.QueryDefine;
import com.gateway.common.mp.support.QueryCondition;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：客户端网关授权
 *
 * @author huxuehao
 **/
@Data
@TableName(DBConst.GATEWAY_CLIENT_AUTH)
public class GatewayClientAuth implements Serializable {
    static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    private Long clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    private Long authId;

    @QueryDefine(condition = QueryCondition.EQ)
    private Integer type; // 类型: 0-应用，1-接口
}
