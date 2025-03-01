package com.gateway.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gateway.common.dto.ClientAuthTreeDto;
import com.gateway.common.entity.GatewayApi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述：网关API
 *
 * @author huxuehao
 **/
@Mapper
public interface GatewayApiMapper extends BaseMapper<GatewayApi> {
    List<ClientAuthTreeDto> getTreeList(@Param("dbName") String dbName);
}
