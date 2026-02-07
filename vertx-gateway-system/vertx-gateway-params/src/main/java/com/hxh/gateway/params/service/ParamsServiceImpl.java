package com.hxh.gateway.params.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxh.gateway.common.entity.Params;
import com.hxh.gateway.common.util.Func;
import com.hxh.gateway.params.core.ParamsAdapter;
import com.hxh.gateway.params.mapper.ParamsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：系统参数
 *
 * @author huxuehao
 **/
@Service
public class ParamsServiceImpl extends ServiceImpl<ParamsMapper, Params> implements ParamsService {
    private final ParamsAdapter paramsAdapter;

    public ParamsServiceImpl(ParamsAdapter paramsAdapter) {
        this.paramsAdapter = paramsAdapter;
    }

    @Override
    public String fetchValueByKey(String key) {
        return paramsAdapter.getValue(key);
        //QueryWrapper<TParams> qw = new QueryWrapper<>();
        //qw.eq("param_key", key);
        //qw.eq("del_flag", 0);
        //try {
        //    return getOne(qw).getParamValue();
        //} catch (Exception e) {
        //    throw new RuntimeException("不存在唯一Key:" + key, e);
        //}
    }

    @Override
    public boolean saveV2(Params params) {
        if(Func.isEmpty(params.getParamValue())) {
            throw new RuntimeException("value值不可为空");
        }
        QueryWrapper<Params> qw = new QueryWrapper<>();
        qw.eq("param_key", params.getParamKey());
        qw.eq("del_flag", 0);
        List<Params> list = list(qw);
        if (list == null || list.isEmpty()) {
            return paramsAdapter.saveParams(params) > 0;
            //return save(params);
        } else {
            throw new RuntimeException("Key已存在");
        }
    }


    @Override
    public boolean updateByIdV2(Params params) {
        if(Func.isEmpty(params.getParamValue())) {
            throw new RuntimeException("value值不可为空");
        }
        QueryWrapper<Params> qw = new QueryWrapper<>();
        qw.eq("param_key", params.getParamKey());
        qw.eq("del_flag", 0);
        qw.ne("id", params.getId());
        List<Params> list = list(qw);
        if (list == null || list.isEmpty()) {
            return paramsAdapter.updateParams(params) > 0;
            //return updateById(params);
        } else {
            throw new RuntimeException("Key已存在");
        }
    }
}
