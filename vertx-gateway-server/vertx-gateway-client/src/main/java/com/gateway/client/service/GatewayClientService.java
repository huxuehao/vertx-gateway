package com.gateway.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gateway.common.dto.ClientAuthTreeDto;
import com.gateway.common.dto.GatewayClientDto;
import com.gateway.common.entity.GatewayClient;

import java.util.List;

/**
 * 描述：授权客户端
 *
 * @author huxuehao
 **/
public interface GatewayClientService extends IService<GatewayClient> {
    /**
     * 新增
     */
    boolean addV2(GatewayClientDto client);
    /**
     * 编辑
     */
    boolean updateV2(GatewayClientDto client);
    /**
     * 删除
     */
    boolean deleteV2(List<Long> ids);

    /**
     * 根据ID唯一获取
     */
    GatewayClientDto getOneV2(Long id);

    /**
     * 客户端授权树
     */
    List<ClientAuthTreeDto> tree();
}
