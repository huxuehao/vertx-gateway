package com.hxh.gateway.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hxh.gateway.common.constant.DBConst;
import com.hxh.gateway.common.enums.AppProtocolType;
import com.hxh.gateway.common.mp.annotation.QueryDefine;
import com.hxh.gateway.common.mp.support.QueryCondition;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：网关应用配置
 *
 * @author huxuehao
 **/
@Data
@TableName(DBConst.GATEWAY_APPLICATION)
public class GatewayApplication implements Serializable {
    static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    private Long id;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String name;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String remark;

    @QueryDefine(condition = QueryCondition.LIKE)
    private AppProtocolType protocol;

    @QueryDefine(condition = QueryCondition.LIKE)
    private Integer port;

    private String config;

    @QueryDefine(condition = QueryCondition.EQ)
    private Integer online;

    @QueryDefine(condition = QueryCondition.EQ)
    private Long createUser;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @QueryDefine(condition = QueryCondition.EQ)
    private Long updateUser;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @TableLogic
    private Integer del_flag;
}
