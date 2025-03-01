package com.gateway.common.dto;

import com.gateway.common.entity.User;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：用户
 *
 * @author huxuehao
 **/
@Setter
@Getter
public class UserDto extends User {
    private String accessToken;
    private String refreshToken;
}
