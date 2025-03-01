package com.gateway.auth.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gateway.auth.mapper.RoleAuthMapper;
import com.gateway.common.entity.RoleAuth;
import org.springframework.stereotype.Service;

/**
 * 描述：角色权限
 *
 * @author huxuehao
 **/
@Service
public class RoleAuthServiceImpl extends ServiceImpl<RoleAuthMapper, RoleAuth> implements RoleAuthService {
}
