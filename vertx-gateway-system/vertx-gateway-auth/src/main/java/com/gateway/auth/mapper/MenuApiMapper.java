package com.gateway.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gateway.common.dto.MenuApiDto;
import com.gateway.common.entity.MenuApi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述：菜单接口
 *
 * @author huxuehao
 **/
@Mapper
public interface MenuApiMapper extends BaseMapper<MenuApi> {
    List<MenuApiDto> listV2(
            @Param("menuDbName")String menuDbName,
            @Param("menuApiDbName")String menuApiDbName
    );
}
