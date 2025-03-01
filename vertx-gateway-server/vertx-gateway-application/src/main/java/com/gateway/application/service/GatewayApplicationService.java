package com.gateway.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gateway.common.entity.GatewayApplication;

import java.util.List;

/**
 * 描述：网关应用
 *
 * @author huxuehao
 **/
public interface GatewayApplicationService extends IService<GatewayApplication> {
    /**
     * 新增，判断端口是否重复
     */
    boolean saveV2(GatewayApplication application);

    /**
     * 更新，判断端口是否重复，判断应用是否启动
     */
    boolean updateByIdV2(GatewayApplication application);

    /**
     * 保存配置
     */
    boolean saveConfig(GatewayApplication application);


    /**
     * 删除，判断应用是否启动
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
