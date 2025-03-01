package com.gateway.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gateway.auth.service.MenuApiService;
import com.gateway.common.annotation.MenuTag;
import com.gateway.common.constant.Constant;
import com.gateway.common.entity.MenuApi;
import com.gateway.common.entity.User;
import com.gateway.common.mp.support.MP;
import com.gateway.common.mp.support.PageParams;
import com.gateway.common.r.R;
import com.gateway.common.util.CacheUtil;
import com.gateway.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 描述：菜单接口
 *
 * @author huxuehao
 **/
@RestController
@RequestMapping("/menu-api")
@MenuTag(code = "system_apimanager")
public class MenuApiController {
    private final MenuApiService menuApiService;
    private final UserService userService;
    private final CacheUtil cacheUtil;

    public MenuApiController(MenuApiService menuApiService, UserService userService, CacheUtil cacheUtil) {
        this.menuApiService = menuApiService;
        this.userService = userService;
        this.cacheUtil = cacheUtil;
    }

    private void clearUserApiAuthCache() {
        List<User> users = userService.list();
        for (User user : users) {
            cacheUtil.del(Constant.API_AUTH_CACHE_PRE + user.getId());
        }
    }
    @PreAuthorize("@ps.hasPermission('post::menu-api:add')")
    @PostMapping(value = "/add", name = "新增")
    public R<?> add(@RequestBody MenuApi body) {
        QueryWrapper<MenuApi> qw = new QueryWrapper<>();
        qw.eq("code", body.getCode());
        List<MenuApi> list = menuApiService.list(qw);
        if (list == null || list.isEmpty()) {
            body.setId(null);
            body.setNextTimeMillis(System.currentTimeMillis());
            return R.data(menuApiService.save(body));
        } else if(list.get(0).getCode().equals(body.getCode())) {
            return R.fail("接口编号已存在");
        } else {
            return R.fail("未知字段重复");
        }
    }

    @PreAuthorize("@ps.hasPermission('post::menu-api:delete')")
    @PostMapping(value = "/delete", name = "删除")
    public R<?> delete(@RequestBody List<Long> ids) {
        menuApiService.removeBatchByIds(ids);
        clearUserApiAuthCache();
        return R.data(true);
    }

    @PreAuthorize("@ps.hasPermission('post::menu-api:valid')")
    @PostMapping(value = "/valid", name = "设置有效")
    public R<?> valid(@RequestBody List<Long> ids) {
        UpdateWrapper<MenuApi> uw = new UpdateWrapper<>();
        uw.in("id", ids);
        uw.set("valid", 1);
        uw.set("next_time_millis", System.currentTimeMillis());
        menuApiService.update(uw);
        clearUserApiAuthCache();
        return R.data(true);
    }

    @PreAuthorize("@ps.hasPermission('post::menu-api:unValid')")
    @PostMapping(value = "/unValid", name = "设置无效")
    public R<?> unValid(@RequestBody List<Long> ids) {
        UpdateWrapper<MenuApi> uw = new UpdateWrapper<>();
        uw.in("id", ids);
        uw.set("valid", 0);
        uw.set("next_time_millis", System.currentTimeMillis());
        menuApiService.update(uw);
        clearUserApiAuthCache();
        return R.data(true);
    }

    @PreAuthorize("@ps.hasPermission('post::menu-api:update')")
    @PostMapping(value = "/update", name = "编辑")
    public R<?> update(@RequestBody MenuApi body) {
        QueryWrapper<MenuApi> qw = new QueryWrapper<>();
        qw.eq("code", body.getCode());
        qw.ne("id", body.getId());
        List<MenuApi> list = menuApiService.list(qw);
        if(list == null || list.isEmpty() || list.get(0).getId().equals(body.getId())) {
            MenuApi api = menuApiService.getById(body.getId());
            body.setNextTimeMillis(System.currentTimeMillis());
            menuApiService.updateById(body);
            if (!Objects.equals(api.getValid(), body.getValid())) {
                clearUserApiAuthCache();
            }
            return R.data(true);
        } else if(list.get(0).getCode().equals(body.getCode())) {
            return R.fail("接口编号已存在");
        } else {
            return R.fail("未知字段重复");
        }
    }

    @PreAuthorize("@ps.hasPermission('get::menu-api:page')")
    @GetMapping(value = "/page", name = "分页")
    public R<?> tree(MenuApi api, PageParams pageParams) {
        return R.data(menuApiService.page(MP.getPage(pageParams),MP.getQueryWrapper(api)));
    }

    @PreAuthorize("@ps.hasPermission('get::menu-api:selectOne')")
    @GetMapping(value = "/selectOne", name = "根据ID唯一获取")
    public R<?> selectOne(@RequestParam Long id) {
        return R.data(menuApiService.getById(id));
    }

    @PreAuthorize("@ps.hasPermission('get::menu-api:menu-button-tree')")
    @GetMapping(value = "/menu-button-tree", name = "菜单接口树形列表")
    public R<?> tree() {
        return R.data(menuApiService.tree());
    }
}
