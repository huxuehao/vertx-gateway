package com.gateway.auth.service;

import com.gateway.common.dto.MenuDto;

import java.util.List;

/**
 * 描述：菜单权限
 *
 * @author huxuehao
 **/
public interface MenuAuthService {
    List<MenuDto> permissionTree();
}
