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
 * 描述：用户角色表
 *
 * @author huxuehao
 **/
@Data
@TableName(DBConst.USER_ROLE)
public class UserRole implements Serializable {
    static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    private Long roleId;
}
