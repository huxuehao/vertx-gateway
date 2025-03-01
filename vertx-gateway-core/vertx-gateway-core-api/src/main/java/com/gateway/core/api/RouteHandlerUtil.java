package com.gateway.core.api;

import com.gateway.common.annotation.RouteHandlerDefaultField;
import com.gateway.common.enums.ApiHttpMethod;
import com.gateway.common.option.GatewayApiOption;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 描述：路由处理器工厂
 *
 * @author huxuehao
 **/
public class RouteHandlerUtil {
    public static void setHandler(Class<?> routeHandlerClass, Route route, GatewayApiOption option, HttpClient httpClient) {
        // 类型检验
        Class<?> superclass = routeHandlerClass.getSuperclass();
        if (superclass != RouteHandler.class) {
            throw new RuntimeException("["+routeHandlerClass.getSimpleName()+"]未继承自RouteHandler");
        }

        // 设置路由path和consumes
        route.path(option.getConfig().getRoutePath());
        if (ApiHttpMethod.ALL != option.getConfig().getRouteMethod()) {
            route.method(HttpMethod.valueOf(option.getConfig().getRouteMethod().name()));
        }
        for (String consume : option.getConfig().getRouteContentType()) {
            route.consumes(consume);
        }

        RouteHandler instance;
        try {
            // 实例化处理器并为参数赋值
            instance = (RouteHandler) routeHandlerClass.newInstance();

            Field[] declaredFields = instance.getClass().getSuperclass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                // 排除静态字段
                if (Modifier.isStatic(declaredField.getModifiers())) {
                    continue;
                }

                if (AnnotationUtils.getAnnotation(declaredField, RouteHandlerDefaultField.class) == null) {
                    continue;
                }

                if (declaredField.getType() == GatewayApiOption.class) {
                    declaredField.setAccessible(true);
                    declaredField.set(instance, option);
                }
                if (declaredField.getType() == HttpClient.class) {
                    declaredField.setAccessible(true);
                    declaredField.set(instance, httpClient);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 设置路由处理器
        route.handler(instance);
    }
}
