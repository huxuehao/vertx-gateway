package com.gateway.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gateway.api.service.GatewayApiService;
import com.gateway.common.annotation.MenuTag;
import com.gateway.common.entity.GatewayApi;
import com.gateway.common.mp.support.MP;
import com.gateway.common.mp.support.PageParams;
import com.gateway.common.r.R;
import com.gateway.common.util.Func;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述：网关API
 *
 * @author huxuehao
 **/
@RestController
@RequestMapping("/gateway/api")
@MenuTag(code = "gateway_api")
public class GatewayApiController {
    private final GatewayApiService gatewayApiService;

    public GatewayApiController(GatewayApiService gatewayApiService) {
        this.gatewayApiService = gatewayApiService;
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:api:page')")
    @GetMapping(value = "/page", name = "分页")
    public R<?> page(GatewayApi api, PageParams pageParams, Long classifyId) {
        QueryWrapper<GatewayApi> qw = MP.getQueryWrapper(api);
        qw.select("id", "parent_id", "app_id", "name", "path", "online", "create_user", "create_time", "update_user","update_time", "del_flag");
        if (Func.isNotEmpty(classifyId)) {
            qw.eq("parent_id", classifyId);
            qw.or().eq("app_id", classifyId);
        }
        qw.last("ORDER BY id");

        return R.data(gatewayApiService.page(MP.getPage(pageParams), qw));
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:api:selectOne')")
    @GetMapping(value = "/selectOne", name = "根据ID唯一获取")
    public R<?> selectOne(@RequestParam Long id) {
        QueryWrapper<GatewayApi> qw = new QueryWrapper<>();
        qw.eq("id", id);
        qw.select("id", "parent_id", "app_id", "name", "path", "online", "create_user", "create_time", "update_user","update_time", "del_flag");
        return R.data(gatewayApiService.getOne(qw));
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:api:classify-tree')")
    @GetMapping(value = "/classify-tree", name = "新增")
    public R<?> classifyTree() {
        return R.data(gatewayApiService.classifyTree());
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:api:add')")
    @PostMapping(value = "/add", name = "新增")
    public R<?> add(@RequestBody GatewayApi api) {
        return R.data(gatewayApiService.saveV2(api));
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:api:save-config')")
    @GetMapping(value = "/get-config", name = "保存配置")
    public R<?> getConfig(@RequestParam Long id) {
        return R.data(gatewayApiService.getById(id));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:api:save-config')")
    @PostMapping(value = "/save-config", name = "保存配置")
    public R<?> saveConfig(@RequestBody GatewayApi api) {
        return R.data(gatewayApiService.saveConfig(api));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:api:update')")
    @PostMapping(value = "/update", name = "编辑")
    public R<?> update(@RequestBody GatewayApi api) {
        return R.data(gatewayApiService.updateByIdV2(api));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:api:delete')")
    @PostMapping(value = "/delete", name = "删除")
    public R<?> delete(@RequestBody List<Long> ids) {
        return R.data(gatewayApiService.removeByIdV2(ids));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:api:online')")
    @PostMapping(value = "/online", name = "上线")
    public R<?> online(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            gatewayApiService.online(id);
        }

        return R.data(true);
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:api:offline')")
    @PostMapping(value = "/offline", name = "下线")
    public R<?> offline(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            gatewayApiService.offline(id);
        }

        return R.data(true);
    }
}
