package com.gateway.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gateway.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 描述：用户
 *
 * @author huxuehao
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
