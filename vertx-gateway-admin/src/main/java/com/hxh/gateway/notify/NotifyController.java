package com.hxh.gateway.notify;

import com.hxh.gateway.common.r.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：ws测试
 *
 * @author huxuehao
 **/
@RestController
public class NotifyController {

    @PostMapping("/trigger-notify")
    public R<?> triggerNotify(@RequestBody NotifyMessage notifyMessage) {
        // 广播告警
        NotifyWebSocketHandler.sendToUsers(notifyMessage);
        return R.success("OK");
    }
}
