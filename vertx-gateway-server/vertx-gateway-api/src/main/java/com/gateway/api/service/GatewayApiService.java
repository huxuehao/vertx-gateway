package com.gateway.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gateway.common.dto.ClientAuthTreeDto;
import com.gateway.common.entity.GatewayApi;

import java.util.List;

/**
 * 描述：网关API
 *
 * @author huxuehao
 **/
public interface GatewayApiService extends IService<GatewayApi> {
    /**
     * 获取分类树
     */
    List<ClientAuthTreeDto> classifyTree();
    /**
     * 新增，判断path是否重复
     */
    boolean saveV2(GatewayApi api);

    /**
     * 更新，判断path是否重复，同时更新上线的API
     */
    boolean updateByIdV2(GatewayApi api);

    /**
     * 保存配置
     */
    boolean saveConfig(GatewayApi api);

    /**
     * 删除，同时下线上线的API
     */
    boolean removeByIdV2(List<Long> ids);

    /**
     * 上线
     */
    boolean online(Long id);

    /**
     * 下线
     */
    boolean offline(Long id);
}
