package com.gateway.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gateway.common.dto.MenuButtonDto;
import com.gateway.common.entity.MenuButton;

import java.util.List;

/**
 * 描述：菜单按钮
 *
 * @author huxuehao
 **/
public interface MenuButtonService extends IService<MenuButton> {
    List<MenuButtonDto> tree();
}
