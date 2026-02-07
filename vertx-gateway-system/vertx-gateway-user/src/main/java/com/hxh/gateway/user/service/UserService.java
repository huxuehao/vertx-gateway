package com.hxh.gateway.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxh.gateway.common.dto.UserDto;
import com.hxh.gateway.common.entity.LoginBody;
import com.hxh.gateway.common.entity.User;

/**
 * 描述：用户
 *
 * @author huxuehao
 **/
public interface UserService extends IService<User> {
    UserDto validateLogin(LoginBody body);
    void logout(String id);
    UserDto refreshToken(String refreshToken);
}
