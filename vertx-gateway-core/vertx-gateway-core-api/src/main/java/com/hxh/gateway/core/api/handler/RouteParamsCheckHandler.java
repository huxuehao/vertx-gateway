package com.hxh.gateway.core.api.handler;

import com.hxh.gateway.common.constant.GatewayRouteConst;
import com.hxh.gateway.common.entity.GatewayApiLog;
import com.hxh.gateway.common.enums.ApiParamPosition;
import com.hxh.gateway.common.enums.ApiParamType;
import com.hxh.gateway.common.option.RouteParam;
import com.hxh.gateway.common.util.Func;
import com.hxh.gateway.core.api.RouteHandler;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * 描述：路由参数检查处理器
 *
 * @author huxuehao
 **/
public class RouteParamsCheckHandler extends RouteHandler {
    @Override
    public void handle(RoutingContext rct) {
        // 字段有效性检查
        if (checkFieldIsNull()) {
            endRoutingFieldNull(rct);
            return;
        }

        // 用户请求
        HttpServerRequest request = rct.request();

        // 存储请求参数（path、query、header）的Map
        MultiMap headerParamsMap = MultiMap.caseInsensitiveMultiMap();
        MultiMap pathParamsMap = MultiMap.caseInsensitiveMultiMap();
        QueryStringEncoder queryParamsMap = new QueryStringEncoder("");

        // 遍历定义参数，进行相关的参数赋值&校验
        for (RouteParam routeParam : apiOption.getConfig().getRouteParams()) {
            ApiParamPosition position = routeParam.getPosition();
            String key = routeParam.getKey();
            ApiParamType type = routeParam.getType();
            Boolean required = routeParam.getRequired();
            String defaultVal = routeParam.getDefaultVal();

            // 根据位置获取参数值
            String value;
            switch (position) {
                case QUERY:
                    value = request.getParam(key);
                    break;
                case PATH:
                    value = rct.pathParam(key);
                    break;
                case HEADER:
                    value = request.getHeader(key);
                    break;
                default:
                    endRoutingError(rct, key + "（"+position+"）参数的位置错误");
                    return;
            }

            // 检查参数是否必填
            if (Func.isEmpty(value) && required) {
                endRoutingError(rct, "缺失必要的参数:" + key);
                return;
            }

            // 如果参数为空且非必填，使用默认值
            if (Func.isEmpty(value)) {
                value = defaultVal;
            }

            // 参数值不为空时进行参数类型校验
            if (Func.isEmpty(value)) {
                continue;
            }

            // 根据类型转换参数值
            try {
                switch (type) {
                    case STRING:
                        break;
                    case INTEGER:
                        Integer.parseInt(value);
                        break;
                    case LONG:
                        Long.parseLong(value);
                        break;
                    case DOUBLE:
                        Double.parseDouble(value);
                        break;
                    case BOOLEAN:
                        Boolean.parseBoolean(value);
                        break;
                    default:
                        endRoutingError(rct, key + "（"+type+"）参数的类型错误");
                        return;
                }
            } catch (Exception e) {
                endRoutingError(rct, key + "（"+type+"）参数的类型错误");
                return;
            }

            // 记录参数内容
            switch (position) {
                case QUERY:
                    queryParamsMap.addParam(key, value);
                    break;
                case PATH:
                    pathParamsMap.add(key, value);
                    break;
                case HEADER:
                    headerParamsMap.add(key, value);
                    break;
                default:
                    break;
            }
        }

        // 日志采集
        GatewayApiLog log = rct.get(GatewayRouteConst.API_LOG_BODY);
        log.setPathParams(pathParamsMap.toString());
        log.setQueryParams(queryParamsMap.toString());
        log.setHeaderParams(headerParamsMap.toString());

        // 将参数信息保存到路由上下文中
        rct.put(GatewayRouteConst.PATH_PARAMS_KEY, pathParamsMap);
        rct.put(GatewayRouteConst.QUERY_PARAMS_KEY, queryParamsMap);
        rct.put(GatewayRouteConst.HEADER_PARAMS_KEY, headerParamsMap);

        rct.next();
    }
}
