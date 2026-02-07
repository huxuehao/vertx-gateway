package com.hxh.gateway.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hxh.gateway.common.constant.DBConst;
import com.hxh.gateway.common.mp.annotation.QueryDefine;
import com.hxh.gateway.common.mp.support.QueryCondition;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：网关白名单
 *
 * @author huxuehao
 **/
@Data
@TableName(DBConst.GATEWAY_WHITE_LIST)
public class GatewayWhiteList implements Serializable {
    static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    public Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    public Long parentId;

    @QueryDefine(condition = QueryCondition.LIKE)
    String ip;

    @QueryDefine(condition = QueryCondition.LIKE)
    String remark;

    @TableLogic
    Integer delFlag;
}
