package com.gateway.params.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gateway.common.entity.Params;

/**
 * 描述：系统参数
 *
 * @author huxuehao
 **/
public interface ParamsService extends IService<Params> {
    String fetchValueByKey(String key);

    boolean saveV2(Params params);
    boolean updateByIdV2(Params params);
}
