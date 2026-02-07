package com.hxh.gateway.menu.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hxh.gateway.common.dto.MenuDto;
import com.hxh.gateway.common.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述：菜单
 *
 * @author huxuehao
 **/
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    List<MenuDto> listV2(@Param("dbName")String dbName, @Param(Constants.WRAPPER) Wrapper<Menu> queryWrapper);
}
