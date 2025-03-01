package com.gateway.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gateway.common.dto.ClientAuthTreeDto;
import com.gateway.common.entity.GatewayApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述：网关应用
 *
 * @author huxuehao
 **/
@Mapper
public interface GatewayApplicationMapper extends BaseMapper<GatewayApplication> {
    List<ClientAuthTreeDto> getTreeList(@Param("dbName") String dbName);
}
