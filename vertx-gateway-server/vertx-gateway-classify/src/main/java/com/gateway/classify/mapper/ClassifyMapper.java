package com.gateway.classify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gateway.common.dto.ClientAuthTreeDto;
import com.gateway.common.entity.GatewayApiClassify;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述：任务分类
 *
 * @author huxuehao
 **/
@Mapper
public interface ClassifyMapper extends BaseMapper<GatewayApiClassify> {
    List<ClientAuthTreeDto> getTreeList(@Param("dbName") String dbName);

    Integer hasChildren(@Param("apiDbName") String apiDbName, @Param("ids")List<Long> id);
}
