package com.gateway.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gateway.common.constant.DBConst;
import com.gateway.common.mp.annotation.QueryDefine;
import com.gateway.common.mp.support.QueryCondition;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：授权客户端
 *
 * @author huxuehao
 **/
@Data
@TableName(DBConst.GATEWAY_CLIENT)
public class GatewayClient implements Serializable {
    static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    private Long id;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String clientCode;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String clientName;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String remark;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String tokenSecret; // Token秘钥

    private Long tokenTtl;  // Token有效期（单位:ms）

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date clientTtl; // 客户端到期时间戳，-1表示无限

    @QueryDefine(condition = QueryCondition.EQ)
    private Integer online; // 状态（1表示正常，0表示禁用）

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
    private Integer delFlag;

}
