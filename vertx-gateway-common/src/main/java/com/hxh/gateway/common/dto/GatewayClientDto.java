package com.hxh.gateway.common.dto;

import com.hxh.gateway.common.entity.GatewayClient;
import com.hxh.gateway.common.entity.GatewayClientAuth;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 描述：授权客户端Dto
 *
 * @author huxuehao
 **/
@Setter
@Getter
public class GatewayClientDto extends GatewayClient {
    List<GatewayClientAuth> auths;
}
