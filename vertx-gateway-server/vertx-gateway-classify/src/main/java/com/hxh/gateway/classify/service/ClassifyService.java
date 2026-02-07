package com.hxh.gateway.classify.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxh.gateway.common.entity.GatewayApiClassify;

import java.util.List;

/**
 * 描述：任务分类
 *
 * @author huxuehao
 **/
public interface ClassifyService extends IService<GatewayApiClassify> {
    boolean deleteAllById(List<Long> ids);
}
