package com.gateway.classify.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gateway.common.entity.GatewayApiClassify;

import java.util.List;

/**
 * 描述：任务分类
 *
 * @author huxuehao
 **/
public interface ClassifyService extends IService<GatewayApiClassify> {
    boolean deleteAllById(List<Long> ids);
}
