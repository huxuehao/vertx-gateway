package com.gateway.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gateway.common.dto.MenuApiDto;
import com.gateway.common.entity.MenuApi;

import java.util.List;

/**
 * 描述：菜单接口
 *
 * @author huxuehao
 **/
public interface MenuApiService extends IService<MenuApi> {
    List<MenuApiDto> tree();
}
