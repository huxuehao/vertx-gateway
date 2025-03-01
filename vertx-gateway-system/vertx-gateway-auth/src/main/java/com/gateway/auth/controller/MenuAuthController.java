package com.gateway.auth.controller;

import com.gateway.auth.service.MenuAuthService;
import com.gateway.common.annotation.MenuTag;
import com.gateway.common.r.R;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：菜单权限
 *
 * @author huxuehao
 **/
@RestController
@RequestMapping("/menu")
@MenuTag(code = "system_menu")
public class MenuAuthController {
    private final MenuAuthService menuAuthService;

    public MenuAuthController(MenuAuthService menuAuthService) {
        this.menuAuthService = menuAuthService;
    }

    @PreAuthorize("@ps.hasPermission('get::menu:permission-tree')")
    @GetMapping(value = "/permission-tree", name = "用户树形列表权限")
    public R<?> permissionTree() {
        return R.data(menuAuthService.permissionTree());
    }

}
