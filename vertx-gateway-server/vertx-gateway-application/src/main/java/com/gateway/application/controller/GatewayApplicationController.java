package com.gateway.application.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gateway.application.service.GatewayApplicationService;
import com.gateway.common.annotation.MenuTag;
import com.gateway.common.entity.GatewayApplication;
import com.gateway.common.mp.support.MP;
import com.gateway.common.mp.support.PageParams;
import com.gateway.common.r.R;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述：网关应用
 *
 * @author huxuehao
 **/
@RestController
@RequestMapping("/gateway/application")
@MenuTag(code = "gateway_application")
public class GatewayApplicationController {
    private final GatewayApplicationService gatewayApplicationService;

    public GatewayApplicationController(GatewayApplicationService gatewayApplicationService) {
        this.gatewayApplicationService = gatewayApplicationService;
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:application:page')")
    @GetMapping(value = "/page", name = "分页")
    public R<?> page(GatewayApplication application, PageParams pageParams) {
        QueryWrapper<GatewayApplication> qw = MP.getQueryWrapper(application);
        qw.select("id", "name", "protocol", "port", "online", "del_flag");
        return R.data(gatewayApplicationService.page(MP.getPage(pageParams), qw));
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:application:list')")
    @GetMapping(value = "/list", name = "列表")
    public R<?> list(GatewayApplication application) {
        QueryWrapper<GatewayApplication> qw = MP.getQueryWrapper(application);
        qw.select("id", "name", "remark", "protocol", "port", "online", "create_user", "create_time", "update_user","update_time", "del_flag");
        return R.data(gatewayApplicationService.list(qw));
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:api:selectOne')")
    @GetMapping(value = "/selectOne", name = "根据ID唯一获取")
    public R<?> selectOne(@RequestParam Long id) {
        QueryWrapper<GatewayApplication> qw = new QueryWrapper<>();
        qw.eq("id", id);
        qw.select("id", "name", "remark", "protocol", "port", "online", "create_user", "create_time", "update_user","update_time", "del_flag");
        return R.data(gatewayApplicationService.getOne(qw));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:application:add')")
    @PostMapping(value = "/add", name = "新增")
    public R<?> add(@RequestBody GatewayApplication application) {
        return R.data(gatewayApplicationService.saveV2(application));
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:application:get-config')")
    @GetMapping(value = "/get-config", name = "保存配置")
    public R<?> getConfig(@RequestParam Long id) {
        return R.data(gatewayApplicationService.getById(id));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:application:save-config')")
    @PostMapping(value = "/save-config", name = "保存配置")
    public R<?> saveConfig(@RequestBody GatewayApplication application) {
        return R.data(gatewayApplicationService.saveConfig(application));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:application:update')")
    @PostMapping(value = "/update", name = "编辑")
    public R<?> update(@RequestBody GatewayApplication application) {
        return R.data(gatewayApplicationService.updateByIdV2(application));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:application:delete')")
    @PostMapping(value = "/delete", name = "删除")
    public R<?> delete(@RequestBody List<Long> ids) {
        return R.data(gatewayApplicationService.removeByIdV2(ids));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:application:online')")
    @PostMapping(value = "/online", name = "上线")
    public R<?> online(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            gatewayApplicationService.online(id);
        }
        return R.data(true);
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:application:offline')")
    @PostMapping(value = "/offline", name = "下线")
    public R<?> offline(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            gatewayApplicationService.offline(id);
        }
        return R.data(true);
    }
}
