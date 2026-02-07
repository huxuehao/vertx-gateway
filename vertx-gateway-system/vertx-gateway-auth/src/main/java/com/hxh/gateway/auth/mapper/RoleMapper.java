package com.hxh.gateway.auth.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hxh.gateway.common.dto.RoleDto;
import com.hxh.gateway.common.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述：角色
 *
 * @author huxuehao
 **/
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    List<RoleDto> listV2(@Param("dbName")String dbName, @Param(Constants.WRAPPER) Wrapper<Role> queryWrapper);
}
