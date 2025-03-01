package com.gateway.notify;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：通知消息实体
 *
 * @author huxuehao
 **/
@Data
public class NotifyMessage implements Serializable {
    static final long serialVersionUID = 1L;
    // 通知标题
    private String title;
    // 通知内容，支持html
    private String message;
    // 通知类型：'success' | 'warning' | 'info' | 'error'
    private String type;
    // 被通知的用户
    private List<String> users;
    // 是否启用语音播报
    private boolean speech;
    // 语音播报次数
    private int speechTimes;
    // 通知显示时长，0表示不一致显示，单位：毫秒
    private Long duration;
    // 通知弹窗的位置：'top-right' | 'top-left' | 'bottom-right' | 'bottom-left'
    private String position;
}
