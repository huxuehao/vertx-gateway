package com.gateway.common.option;

import com.gateway.common.enums.AppCertType;
import com.gateway.common.enums.AppProtocolType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 描述：应用配置
 *
 * @author huxuehao
 **/
@Getter
@Setter
public class AppConfig {
    /**
     * HTTP/HTTPS
     */
    private AppProtocolType protocol; // 服务协议（HTTP、HTTPS）
    private Integer port; // 端口号
    private AppCertType certificateType; // 证书类型（pem、pfx）
    private String certificateKey; // 证书Key
    private String certificatePath; // 证书Path

    /**
     * 跨域配置
     */
    private Boolean corsOpen; // 是否开启跨域
    private Boolean allowCredentials; // 允许的凭证
    private String allowedOrigin; // 允许的来源
    private Integer maxAgeSeconds; // 最大描述
    private List<String> allowedMethods; // 允许的方法

    /**
     * 参数配置
     */
    private String serverUrlPrefix; // 后端服务前缀
    private Long contentLength; // Content-Length的最大长度限制
    private Long sessionTimeOut; // 会话超时时间
    private String sessionCookieName; // 会话cookie名称
    private int decoderInitialBufferSize; // HTTP对象解码器初始化缓冲区大小
    private int maxPoolSize; // API与后台服务器交互的连接池数量
    private int maxInitialLineLength; // 请求后端URL参数值最大总长度
    private int maxHeaderSize; // 后端Header参数值最大总长度
    private boolean keepAlive; // API与后台服务是否使用KeepAlive
}
