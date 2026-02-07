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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

/**
 * 描述：用户表
 *
 * @author huxuehao
 */
@Data
@TableName(DBConst.USER)
public class User implements UserDetails, Serializable  {
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @QueryDefine(condition = QueryCondition.EQ)
    private Long id;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String code;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String name;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String nickname;

    @QueryDefine(condition = QueryCondition.LIKE)
    private Long phone;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String email;

    @QueryDefine(condition = QueryCondition.EQ)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orgId;

    @QueryDefine(condition = QueryCondition.LIKE)
    private String account;

    private String password;

    @QueryDefine(condition = QueryCondition.EQ)
    private Integer valid;

    private Integer sort;

    @TableLogic
    private Integer delFlag;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return account;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
