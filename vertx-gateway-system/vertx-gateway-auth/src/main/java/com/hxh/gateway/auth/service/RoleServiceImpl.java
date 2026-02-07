package com.hxh.gateway.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxh.gateway.auth.mapper.RoleMapper;
import com.hxh.gateway.auth.mapper.UserRoleMapper;
import com.hxh.gateway.common.constant.DBConst;
import com.hxh.gateway.common.dto.RoleDto;
import com.hxh.gateway.common.entity.MenuButton;
import com.hxh.gateway.common.entity.Role;
import com.hxh.gateway.common.entity.RoleAuth;
import com.hxh.gateway.common.entity.UserRole;
import com.hxh.gateway.common.mp.support.MP;
import com.hxh.gateway.common.util.AuthUtil;
import com.hxh.gateway.common.util.Func;
import com.hxh.gateway.common.util.tree.TreeUtil;
import com.hxh.gateway.common.vo.AuthConfig;
import com.hxh.gateway.common.vo.UserRoleConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 描述：角色
 *
 * @author huxuehao
 **/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    private final RoleAuthService roleAuthService;
    private final UserRoleMapper userRoleMapper;
    private final MenuButtonService menuButtonService;

    public RoleServiceImpl(RoleAuthService roleAuthService, UserRoleMapper userRoleMapper, MenuButtonService menuButtonService) {
        this.roleAuthService = roleAuthService;
        this.userRoleMapper = userRoleMapper;
        this.menuButtonService = menuButtonService;
    }

    @Override
    public List<RoleDto> tree(Role role) {
        List<RoleDto> list = baseMapper.listV2(DBConst.ROLE, MP.getQueryWrapper(role));
        return TreeUtil.convertTree(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAllById(List<Long> ids) {
        QueryWrapper<Role> qw = new QueryWrapper<>();
        qw.in("parent_id", ids);
        qw.eq("del_flag", 0);
        List<Role> roleList = list(qw);

        if (Func.isEmpty(roleList)) {
            return removeBatchByIds(ids);
        }
        // 有子级的ID集合
        List<Long> hasChildIds = roleList.stream().map(Role::getParentId).collect(Collectors.toList());
        // 没有子级的ID集合
        List<Long> noneChildIds = ids.stream().filter(id -> !hasChildIds.contains(id)).collect(Collectors.toList());
        removeBatchByIds(noneChildIds);

        return deleteAllById(hasChildIds);
    }

    @Override
    @Transactional
    public boolean saveAuthConfig(AuthConfig config) {
        Long roleId = config.getRoleId();
        List<Long> menuIds = config.getMenuIds();
        List<Long> buttonIds = config.getButtonIds();
        List<Long> apiIds = config.getApiIds();

        ArrayList<RoleAuth> menuRoleAuths = new ArrayList<>();
        for (Long menuId : menuIds) {
            RoleAuth menuRoleAuth = new RoleAuth();
            menuRoleAuth.setRoleId(roleId);
            menuRoleAuth.setAuthId(menuId);
            menuRoleAuth.setAuthType(1);
            menuRoleAuths.add(menuRoleAuth);
        }

        ArrayList<RoleAuth> buttonRoleAuths = new ArrayList<>();
        for (Long buttonId : buttonIds) {
            RoleAuth buttonRoleAuth = new RoleAuth();
            buttonRoleAuth.setRoleId(roleId);
            buttonRoleAuth.setAuthId(buttonId);
            buttonRoleAuth.setAuthType(2);
            buttonRoleAuths.add(buttonRoleAuth);
        }

        ArrayList<RoleAuth> apiRoleAuths = new ArrayList<>();
        for (Long apiId : apiIds) {
            RoleAuth apiRoleAuth = new RoleAuth();
            apiRoleAuth.setRoleId(roleId);
            apiRoleAuth.setAuthId(apiId);
            apiRoleAuth.setAuthType(3);
            apiRoleAuths.add(apiRoleAuth);
        }

        QueryWrapper<RoleAuth> qw = new QueryWrapper<>();
        qw.eq("role_id", roleId);
        roleAuthService.remove(qw);

        if (!menuRoleAuths.isEmpty()) {
            roleAuthService.saveBatch(menuRoleAuths);
        }

        if (!buttonRoleAuths.isEmpty()) {
            roleAuthService.saveBatch(buttonRoleAuths);
        }

        if (!apiRoleAuths.isEmpty()) {
            roleAuthService.saveBatch(apiRoleAuths);
        }

        return true;
    }

    @Override
    public AuthConfig getAuthConfig(Long roleId) {
        QueryWrapper<RoleAuth> qw = new QueryWrapper<>();
        qw.eq("role_id", roleId);
        List<RoleAuth> list = roleAuthService.list(qw);

        AuthConfig authConfig = new AuthConfig();
        authConfig.setRoleId(roleId);

        List<Long> menuIds = list.stream()
                .filter(item -> item.getAuthType() == 1)
                .map(RoleAuth::getAuthId)
                .collect(Collectors.toList());
        authConfig.setMenuIds(menuIds);

        List<Long> buttonIds = list.stream()
                .filter(item -> item.getAuthType() == 2)
                .map(RoleAuth::getAuthId)
                .collect(Collectors.toList());
        authConfig.setButtonIds(buttonIds);

        List<Long> apiIds = list.stream()
                .filter(item -> item.getAuthType() == 3)
                .map(RoleAuth::getAuthId)
                .collect(Collectors.toList());
        authConfig.setApiIds(apiIds);

        return authConfig;
    }

    @Override
    @Transactional
    public boolean saveUserRole(UserRoleConfig config) {
        Long userId = config.getUserId();
        List<Long> roleIds = config.getRoleIds();

        QueryWrapper<UserRole> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        userRoleMapper.delete(qw);

        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setRoleId(roleId);
            userRole.setUserId(userId);
            userRoleMapper.insert(userRole);
        }

        return true;
    }

    @Override
    public List<String> getUserRole(Long userId) {
        QueryWrapper<UserRole> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        List<UserRole> userRoles = userRoleMapper.selectList(qw);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getButtonPermissions() {
        // 获取用户配置的角色
        List<String> userRole = getUserRole(AuthUtil.getUserId());

        // 获取有效的用户角色
        QueryWrapper<Role> roleQw = new QueryWrapper<>();
        roleQw.in("id", userRole);
        roleQw.eq("valid", 1);
        roleQw.eq("del_flag", 0);
        List<Role> roleList = list(roleQw);
        userRole = roleList.stream().map(Role::getId).map(Objects::toString).collect(Collectors.toList());

        if (userRole.isEmpty()) {
            return new ArrayList<>();
        }

        // 处理超级管理员角色
        if (userRole.contains("1111111111111111111")) {
            QueryWrapper<MenuButton> qw = new QueryWrapper<>();
            qw.eq("valid", 1);
            qw.eq("del_flag", 0);
            List<MenuButton> validButton = menuButtonService.list(qw);

            return validButton.stream().map(MenuButton::getCode).collect(Collectors.toList());
        }

        // 获取角色配置的按钮权限
        QueryWrapper<RoleAuth> authQw = new QueryWrapper<>();
        authQw.in("role_id", userRole);
        authQw.eq("auth_type", 2);
        List<RoleAuth> auths = roleAuthService.list(authQw);
        if (auths == null || auths.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取有效的按钮配置
        QueryWrapper<MenuButton> qw = new QueryWrapper<>();
        qw.in("id", auths.stream().map(RoleAuth::getAuthId).collect(Collectors.toList()));
        qw.eq("valid", 1);
        qw.eq("del_flag", 0);
        List<MenuButton> validButton = menuButtonService.list(qw);

        return validButton.stream().map(MenuButton::getCode).collect(Collectors.toList());
    }

    @Override
    public List<String> getUserByRoleId(Long roleId) {
        QueryWrapper<UserRole> qw = new QueryWrapper<>();
        qw.eq("role_id", roleId);
        List<UserRole> userRoles = userRoleMapper.selectList(qw);
        return userRoles.stream()
                .map(UserRole::getUserId)
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
