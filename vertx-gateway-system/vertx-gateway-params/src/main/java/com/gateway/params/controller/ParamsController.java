package com.gateway.params.controller;

import com.gateway.common.annotation.MenuTag;
import com.gateway.common.entity.Params;
import com.gateway.common.mp.support.MP;
import com.gateway.common.mp.support.PageParams;
import com.gateway.common.r.R;
import com.gateway.params.service.ParamsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述：系统参数
 *
 * @author huxuehao
 **/
@RestController
@RequestMapping("/params")
@MenuTag(code = "system_params")
public class ParamsController {
    private final ParamsService paramsService;

    public ParamsController(ParamsService paramsService) {
        this.paramsService = paramsService;
    }

    @PreAuthorize("@ps.hasPermission('get::params:page')")
    @GetMapping(value = "/page", name = "分页")
    public R<?> page(Params params, PageParams pageParams) {
        return R.data(paramsService.page(MP.getPage(pageParams), MP.getQueryWrapper(params)));
    }

    @PreAuthorize("@ps.hasPermission('post::params:add')")
    @PostMapping(value = "/add", name = "新增")
    public R<?> add(@RequestBody Params params) {
        return R.data(paramsService.saveV2(params));
    }

    @PreAuthorize("@ps.hasPermission('post::params:update')")
    @PostMapping(value = "/update", name = "编辑")
    public R<?> update(@RequestBody Params params) {
        return R.data(paramsService.updateByIdV2(params));
    }

    @PreAuthorize("@ps.hasPermission('post::params:delete')")
    @PostMapping(value = "/delete", name = "删除")
    public R<?> delete(@RequestBody List<Long> ids) {
        return R.data(paramsService.removeBatchByIds(ids));
    }

    @PreAuthorize("@ps.hasPermission('get::params:selectOne')")
    @GetMapping(value = "/selectOne", name = "根据ID唯一获取")
    public R<?> selectOne(@RequestParam Long id) {
        return R.data(paramsService.getById(id));
    }

    @PreAuthorize("@ps.hasPermission('get::params:fetch-value-by-key')")
    @GetMapping(value = "/fetch-value-by-key", name = "根据key获取value")
    public R<?> fetchValueByKey(@RequestParam String key) {
        return R.data(paramsService.fetchValueByKey(key));
    }

}
