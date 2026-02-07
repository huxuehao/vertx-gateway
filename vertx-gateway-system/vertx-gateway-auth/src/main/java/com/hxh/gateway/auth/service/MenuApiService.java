package com.hxh.gateway.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxh.gateway.common.dto.MenuApiDto;
import com.hxh.gateway.common.entity.MenuApi;

import java.util.List;

/**
 * 描述：菜单接口
 *
 * @author huxuehao
 **/
public interface MenuApiService extends IService<MenuApi> {
    List<MenuApiDto> tree();
}
