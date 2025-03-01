package com.gateway.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gateway.common.constant.DBConst;
import com.gateway.common.mp.annotation.QueryDefine;
import com.gateway.common.mp.support.QueryCondition;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：角色表
 *
 * @author huxuehao
 **/
@Data
@TableName(DBConst.ROLE)
public class Role implements Serializable {
    static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    public Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @QueryDefine(condition = QueryCondition.EQ)
    public Long parentId;

    @QueryDefine(condition = QueryCondition.LIKE)
    String name;

    @QueryDefine(condition = QueryCondition.LIKE)
    String code;

    @QueryDefine(condition = QueryCondition.EQ)
    Integer valid;

    Integer sort;

    @TableLogic
    Integer delFlag;
}
