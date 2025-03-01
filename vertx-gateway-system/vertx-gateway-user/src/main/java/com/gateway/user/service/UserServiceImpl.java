package com.gateway.user.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gateway.common.constant.Constant;
import com.gateway.common.dto.UserDto;
import com.gateway.common.entity.LoginBody;
import com.gateway.common.entity.User;
import com.gateway.common.exception.NotAuthException;
import com.gateway.common.util.*;
import com.gateway.params.core.ParamsAdapter;
import com.gateway.user.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：用户
 *
 * @author huxuehao
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final CacheUtil cacheUtil;
    private final ParamsAdapter paramsAdapter;

    public UserServiceImpl(CacheUtil cacheUtil, ParamsAdapter paramsAdapter) {
        this.cacheUtil = cacheUtil;
        this.paramsAdapter = paramsAdapter;
    }

    @Override
    public UserDto validateLogin(LoginBody body) {
        /* 验证用户名和密码 */
        List<User> users = list(new QueryWrapper<User>().eq("account", body.getAccount()));
        if (users.size() > 1) {
            throw new RuntimeException("用户验证失败");
        }
        if (Func.isEmpty(users)) {
            throw new RuntimeException("用户不存在");
        }
        User user = users.get(0);
        if (!user.getPassword().equals(EncryptionUtil.md5(body.getPassword()))) {
            throw new RuntimeException("或密码错误");
        }

        return getTUserDto(user);
    }

    private UserDto getTUserDto(User user) {
        String uuid = EncryptionUtil.uuid();

        /* 获取系统参数 */
        String tokenTTL = paramsAdapter.getValue("TOKEN_LIVE_TIME");

        /* 存活时间 */
        long tokenLiveTime = Long.parseLong(tokenTTL);
        long refTokenLiveTime = tokenLiveTime + (1000 * 60 * 60);

        /* 生成token */
        String accessToken = TokenUtil.createToken(uuid, JSON.toJSONString(user), tokenLiveTime);
        String refreshToken = TokenUtil.createToken(uuid + "_refresh", JSON.toJSONString(user), refTokenLiveTime);

        // 补充用户信息
        UserDto tUserDto = new UserDto();
        BeanUtils.copyProperties(user, tUserDto);
        tUserDto.setAccessToken(accessToken);
        tUserDto.setRefreshToken(refreshToken);
        //cacheUtil.set(uuid, accessToken, tokenLiveTime);

        return tUserDto;
    }

    @Override
    public void logout(String id) {
        try {
            cacheUtil.del(Constant.API_AUTH_CACHE_PRE + AuthUtil.getUserId());
            cacheUtil.del(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("缓存服务异常");
        }
    }

    @Override
    public UserDto refreshToken(String refreshToken) {
        /* 解析Token */
        Claims claims;
        try {
            claims = TokenUtil.parseToken(refreshToken);
        } catch (Exception e) {
            throw new NotAuthException();
        }

        // 判断是否超时
        if (claims.getExpiration().getTime() < System.currentTimeMillis()) {
            cacheUtil.del(claims.getId());
            throw new NotAuthException("refreshToken无效");
        }

        User user = JSON.parseObject(claims.getSubject(), User.class);
        User user__ = getById(user.getId());
        if (user__ == null) {
            throw new NotAuthException("用户不存在");
        }

        return getTUserDto(user__);
    }
}
