package com.gateway.log.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gateway.common.annotation.MenuTag;
import com.gateway.common.entity.GatewayApiLog;
import com.gateway.common.mp.support.MP;
import com.gateway.common.mp.support.PageParams;
import com.gateway.common.r.R;
import com.gateway.log.service.GatewayApiLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述：API日志
 *
 * @author huxuehao
 **/
@RestController
@RequestMapping("/gateway/log")
@MenuTag(code = "gateway_log")
public class GatewayApiLogController {
    private final GatewayApiLogService gatewayApiLogService;

    public GatewayApiLogController(GatewayApiLogService gatewayApiLogService) {
        this.gatewayApiLogService = gatewayApiLogService;
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:log:page')")
    @GetMapping(value = "/page", name = "分页")
    public R<?> page(GatewayApiLog log, PageParams pageParams, String[] createTime) {
        QueryWrapper<GatewayApiLog> qw = MP.getQueryWrapper(log);
        qw.select("id","api_id","app_id","req_method","req_path","proxy_method","start_time","end_time","req_ip","status","create_time");
        if (createTime != null && createTime.length == 2) {
            qw.between("create_time", createTime[0], createTime[1]);
        }
        qw.last("ORDER BY create_time DESC");
        return R.data(gatewayApiLogService.page(MP.getPage(pageParams), qw));
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:log:selectOne')")
    @GetMapping(value = "/selectOne", name = "根据ID唯一获取")
    public R<?> selectOne(Integer id) {
        return R.data(gatewayApiLogService.getById(id));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:log:delete')")
    @PostMapping(value = "/delete", name = "删除")
    public R<?> delete(List<Integer> ids) {
        return R.data(gatewayApiLogService.removeBatchByIds(ids));
    }
}
