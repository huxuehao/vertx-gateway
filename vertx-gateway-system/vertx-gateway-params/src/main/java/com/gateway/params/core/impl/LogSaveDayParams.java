package com.gateway.params.core.impl;

import com.gateway.common.constant.Constant;
import com.gateway.common.util.Func;
import com.gateway.params.core.ParamsAdapter;
import com.gateway.params.core.ParamsCore;
import org.springframework.stereotype.Component;

/**
 * 描述：token存活时长
 *
 * @author huxuehao
 **/
@Component
public class LogSaveDayParams implements ParamsCore {
    @Override
    public String checkAndFormatValue(String value) {
        if(Func.isEmpty(value)) {
            throw new RuntimeException("value值不可为空");
        }
        String trim = value.trim();
        try {
            Integer.parseInt(trim);
        } catch (NumberFormatException e) {
            throw new RuntimeException("系统参数LOG_SAVE_DAYS不合法",e);
        }
        return trim;
    }

    @Override
    public String getDefaultValue() {
        return String.valueOf(Constant.LOG_SAVE_DAYS);
    }

    @Override
    public void register(ParamsAdapter adapter) {
        adapter.register("LOG_SAVE_DAYS", new LogSaveDayParams());
    }
}
