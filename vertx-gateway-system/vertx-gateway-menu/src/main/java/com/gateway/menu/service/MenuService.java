package com.gateway.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gateway.common.dto.MenuDto;
import com.gateway.common.entity.Menu;

import java.util.List;

/**
 * 描述：菜单
 *
 * @author huxuehao
 **/
public interface MenuService extends IService<Menu> {
    List<MenuDto> tree(Menu menu);
    boolean deleteAllById(List<Long> ids);

}
