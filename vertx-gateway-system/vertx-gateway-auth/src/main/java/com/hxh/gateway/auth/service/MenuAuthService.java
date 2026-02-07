package com.hxh.gateway.auth.service;

import com.hxh.gateway.common.dto.MenuDto;

import java.util.List;

/**
 * 描述：菜单权限
 *
 * @author huxuehao
 **/
public interface MenuAuthService {
    List<MenuDto> permissionTree();
}
