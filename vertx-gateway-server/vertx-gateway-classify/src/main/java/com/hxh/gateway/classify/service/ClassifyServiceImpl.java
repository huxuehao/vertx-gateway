package com.hxh.gateway.classify.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxh.gateway.common.constant.DBConst;
import com.hxh.gateway.common.entity.GatewayApiClassify;
import com.hxh.gateway.classify.mapper.ClassifyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述：任务分类
 *
 * @author huxuehao
 **/
@Service
public class ClassifyServiceImpl extends ServiceImpl<ClassifyMapper, GatewayApiClassify> implements ClassifyService {

    @Override
    @Transactional
    public boolean deleteAllById(List<Long> ids) {
        Integer num = baseMapper.hasChildren(DBConst.GATEWAY_API, ids);
        if (num != null && num > 0) {
            throw new RuntimeException("请先删除分类下的应用");
        }

        return removeBatchByIds(ids);
    }
}
