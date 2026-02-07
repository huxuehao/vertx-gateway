package com.hxh.gateway.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hxh.gateway.common.constant.DBConst;
import com.hxh.gateway.common.mp.annotation.QueryDefine;
import com.hxh.gateway.common.mp.support.QueryCondition;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：网关API日志
 *
 * @author huxuehao
 **/
@Data
@TableName(DBConst.GATEWAY_API_LOG)
public class GatewayApiLog implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    private Long appId;

    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    private Long apiId;

    @QueryDefine(condition = QueryCondition.EQ)
    private String reqMethod;
    @QueryDefine(condition = QueryCondition.LIKE)
    private String reqPath;
    private String reqBody;
    private String respBody;
    @QueryDefine(condition = QueryCondition.LIKE)
    private String proxyPath;
    @QueryDefine(condition = QueryCondition.EQ)
    private String proxyMethod;
    private String proxyConstParams;
    private String headerParams;
    private String pathParams;
    private String queryParams;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long startTime;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long endTime;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String reqIp;
    @QueryDefine(condition = QueryCondition.EQ)
    private Integer status;
    private String errorContent;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
