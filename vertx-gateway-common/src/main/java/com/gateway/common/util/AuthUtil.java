package com.gateway.common.util;

import com.alibaba.fastjson2.JSON;
import com.gateway.common.entity.User;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述：
 *
 * @author huxuehao
 **/
public class AuthUtil {
    public static User getUser() {

        HttpServletRequest request = WebUtil.getRequest();
        if (request == null) {
            return null;
        }

        String token = request.getHeader("Authorization");
        if(Func.isEmpty(token)) {
            token = request.getParameter("Authorization");
            if (Func.isEmpty(token)) {
                return null;
            }
        }
        try {
            Claims claims = TokenUtil.parseToken(token);
            return JSON.parseObject(claims.getSubject(), User.class);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getUserCode() {
        return getUser() == null ? "" : getUser().getCode();
    }
    public static Long getUserId() {
        return getUser() == null ? -1L : getUser().getId();
    }
    public static String getUserName() {
        return getUser() == null ? null : getUser().getName();
    }
}
