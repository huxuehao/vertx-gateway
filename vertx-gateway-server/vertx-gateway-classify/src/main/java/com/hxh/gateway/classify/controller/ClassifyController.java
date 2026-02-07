package com.hxh.gateway.classify.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hxh.gateway.common.annotation.MenuTag;
import com.hxh.gateway.common.entity.GatewayApiClassify;
import com.hxh.gateway.common.mp.support.MP;
import com.hxh.gateway.common.mp.support.PageParams;
import com.hxh.gateway.common.r.R;
import com.hxh.gateway.classify.service.ClassifyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 描述：任务分类
 *
 * @author huxuehao
 **/
@RestController
@RequestMapping(value = "/gateway/classify")
@MenuTag(code = "gateway_classify")
public class ClassifyController {
    private final ClassifyService classifyService;

    public ClassifyController(ClassifyService classifyService) {
        this.classifyService = classifyService;
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:classify:add')")
    @PostMapping(value = "/add", name = "新增")
    public R<?> add(@RequestBody GatewayApiClassify body) {
        QueryWrapper<GatewayApiClassify> qw = new QueryWrapper<>();
        qw.eq("code", body.getCode());
        List<GatewayApiClassify> list = classifyService.list(qw);
        if (list == null || list.isEmpty()) {
            return R.data(classifyService.save(body));
        } else {
            return R.fail("分类编号已存在");
        }
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:classify:delete')")
    @PostMapping(value = "/delete", name = "删除")
    public R<?> delete(@RequestBody List<Long> ids) {
        return R.data(classifyService.deleteAllById(ids));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:classify:update')")
    @PostMapping(value = "/update", name = "编辑")
    public R<?> update(@RequestBody GatewayApiClassify body) {
        QueryWrapper<GatewayApiClassify> qw = new QueryWrapper<>();
        qw.eq("code", body.getCode());
        qw.ne("id", body.getId());
        List<GatewayApiClassify> list = classifyService.list(qw);
        if (list == null || list.isEmpty()) {
            if (body.getParentId() == null) {
                body.setParentId(0L);
            }
            GatewayApiClassify classify = classifyService.getById(body.getId());
            if (!Objects.equals(classify.getParentId(), body.getParentId())) {
                return R.fail("已保存的分类禁止移动层级关系");
            } else {
                return R.data(classifyService.updateById(body));
            }
        } else {
            return R.fail("分类编号已存在");
        }
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:classify:page')")
    @GetMapping(value = "/page", name = "树形列表")
    public R<?> page(PageParams pageParams, GatewayApiClassify classify) {
        return R.data(classifyService.page(MP.getPage(pageParams), MP.getQueryWrapper(classify)));
    }

    @PreAuthorize("@ps.hasPermission('get::gateway:classify:selectOne')")
    @GetMapping(value = "/selectOne", name = "根据ID唯一获取")
    public R<?> selectOne(@RequestParam Long id) {
        return R.data(classifyService.getById(id));
    }
}
