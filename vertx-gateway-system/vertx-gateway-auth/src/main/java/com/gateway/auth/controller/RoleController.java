package com.gateway.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gateway.auth.service.RoleService;
import com.gateway.common.annotation.MenuTag;
import com.gateway.common.constant.Constant;
import com.gateway.common.entity.Role;
import com.gateway.common.r.R;
import com.gateway.common.util.CacheUtil;
import com.gateway.common.vo.AuthConfig;
import com.gateway.common.vo.UserRoleConfig;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 描述：角色
 *
 * @author huxuehao
 **/
@RestController
@RequestMapping("/role")
@MenuTag(code = "system_rolemanager")
public class RoleController {
    private final RoleService roleService;
    private final CacheUtil cacheUtil;

    public RoleController(RoleService roleService, CacheUtil cacheUtil) {
        this.roleService = roleService;
        this.cacheUtil = cacheUtil;
    }

    /**
     * 根据角色清除接口权限缓存
     */
    private void clearApiAuthByRole(Long roleId) {
        List<String> userIds = roleService.getUserByRoleId(roleId);
        for (String userId : userIds) {
            cacheUtil.del(Constant.API_AUTH_CACHE_PRE + userId);
        }
    }


    @PreAuthorize("@ps.hasPermission('post::role:add')")
    @PostMapping(value = "/add", name = "新增")
    public R<?> add(@RequestBody Role body) {
        if ("administrator".equals(body.getCode())) {
            return R.fail(202, "超级管理员角色限制新增");
        }
        QueryWrapper<Role> qw = new QueryWrapper<>();
        qw.eq("code", body.getCode());
        List<Role> list = roleService.list(qw);
        if (list == null || list.isEmpty()) {
            return R.data(roleService.save(body));
        } else {
            return R.fail("角色编号已存在");
        }
    }

    @PreAuthorize("@ps.hasPermission('post::role:delete')")
    @PostMapping(value = "/delete", name = "删除")
    public R<?> delete(@RequestBody List<Long> ids) {
        if (ids.contains(1111111111111111111L)) {
            return R.fail(202, "超级管理员角色限制删除");
        }
        return R.data(roleService.deleteAllById(ids));
    }

    @PreAuthorize("@ps.hasPermission('post::role:update')")
    @PostMapping(value = "/update", name = "编辑")
    public R<?> update(@RequestBody Role body) {
        if (body.getId() == 1111111111111111111L || "administrator".equals(body.getCode())) {
            return R.fail(202, "超级管理员角色限制修改");
        }
        QueryWrapper<Role> qw = new QueryWrapper<>();
        qw.eq("code", body.getCode());
        qw.ne("id", body.getId());
        List<Role> list = roleService.list(qw);
        if (list == null || list.isEmpty()) {
            if (body.getParentId() == null) {
                body.setParentId(0L);
            }

            Role oldRole = roleService.getById(body.getId());
            roleService.updateById(body);

            if (!Objects.equals(oldRole.getValid(), body.getValid())) {
                clearApiAuthByRole(body.getId()); // 清除缓存
            }

            return R.data(true);
        } else {
            return R.fail("角色编号已存在");
        }
    }

    @PreAuthorize("@ps.hasPermission('get::role:tree')")
    @GetMapping(value = "/tree", name = "树形列表")
    public R<?> tree(Role org) {
        return R.data(roleService.tree(org));
    }

    @PreAuthorize("@ps.hasPermission('get::role:selectOne')")
    @GetMapping(value = "/selectOne", name = "根据ID唯一获取")
    public R<?> selectOne(@RequestParam Long id) {
        return R.data(roleService.getById(id));
    }

    @PreAuthorize("@ps.hasPermission('post::role:save-auth-config')")
    @PostMapping(value = "/save-auth-config", name = "保存权限配置")
    public R<?> saveAuthConfig(@RequestBody AuthConfig config) {
        roleService.saveAuthConfig(config);
        clearApiAuthByRole(config.getRoleId()); // 清除缓存

        return R.data(true);
    }

    @PreAuthorize("@ps.hasPermission('get::role:get-auth-config')")
    @GetMapping(value = "/get-auth-config", name = "根据RoleId获取权限配置")
    public R<?> getAuthConfig(@RequestParam Long roleId) {
        return R.data(roleService.getAuthConfig(roleId));
    }

    @PreAuthorize("@ps.hasPermission('post::role:save-user-role')")
    @PostMapping(value = "/save-user-role", name = "保存用户角色")
    public R<?> saveUserRole(@RequestBody UserRoleConfig config) {
        roleService.saveUserRole(config);
        cacheUtil.del(Constant.API_AUTH_CACHE_PRE + config.getUserId()); // 清除缓存
        return R.data(true);
    }

    @PreAuthorize("@ps.hasPermission('get::role:get-user-role')")
    @GetMapping(value = "/get-user-role", name = "根据UserId获取用户角色")
    public R<?> getUserRole(@RequestParam Long userId) {
        return R.data(roleService.getUserRole(userId));
    }

    @PreAuthorize("@ps.hasPermission('get::role:get-button-permissions')")
    @GetMapping(value = "/get-button-permissions", name = "根据用户按钮权限")
    public R<?> getButtonPermissions() {
        return R.data(roleService.getButtonPermissions());
    }
}
