package com.gateway.client.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gateway.client.service.GatewayClientService;
import com.gateway.common.annotation.MenuTag;
import com.gateway.common.dto.GatewayClientDto;
import com.gateway.common.entity.GatewayClient;
import com.gateway.common.mp.support.MP;
import com.gateway.common.mp.support.PageParams;
import com.gateway.common.r.R;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述：授权客户端
 *
 * @author huxuehao
 **/
@RestController
@RequestMapping("/gateway/client")
@MenuTag(code = "gateway_client")
public class GatewayClientController {
    private final GatewayClientService gatewayClientService;

    public GatewayClientController(GatewayClientService gatewayClientService) {
        this.gatewayClientService = gatewayClientService;
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:client:page')")
    @GetMapping(value = "/page", name = "分页")
    public R<?> page(GatewayClient client, PageParams pageParams) {
        QueryWrapper<GatewayClient> qw = MP.getQueryWrapper(client);
        qw.select("id", "client_code", "client_name", "remark", "online","del_flag","create_user","create_time","update_user","update_time");
        return R.data(gatewayClientService.page(MP.getPage(pageParams), qw));
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:client:selectOne')")
    @GetMapping(value = "/selectOne", name = "根据ID唯一获取")
    public R<?> selectOne(@RequestParam Long id) {
        return R.data(gatewayClientService.getOneV2(id));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:client:add')")
    @PostMapping(value = "/add", name = "新增")
    public R<?> add(@RequestBody GatewayClientDto client) {
        return R.data(gatewayClientService.addV2(client));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:client:update')")
    @PostMapping(value = "/update", name = "编辑")
    public R<?> update(@RequestBody GatewayClientDto client) {
        return R.data(gatewayClientService.updateV2(client));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:client:delete')")
    @PostMapping(value = "/delete", name = "删除")
    public R<?> delete(@RequestBody List<Long> ids) {
        return R.data(gatewayClientService.deleteV2(ids));
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:client:tree')")
    @GetMapping(value = "/tree", name = "获取树形结构")
    public R<?> tree() {
        return R.data(gatewayClientService.tree());
    }
}
