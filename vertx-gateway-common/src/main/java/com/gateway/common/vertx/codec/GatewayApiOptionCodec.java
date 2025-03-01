package com.gateway.common.vertx.codec;

import com.alibaba.fastjson2.JSON;
import com.gateway.common.option.GatewayApiOption;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

/**
 * 描述：GatewayApiOption编解码器
 *
 * @author huxuehao
 **/
public class GatewayApiOptionCodec implements MessageCodec<GatewayApiOption, GatewayApiOption> {

    /**
     * 将 GatewayApiOption 对象编码为二进制数据并写入 Buffer
     */
    @Override
    public void encodeToWire(Buffer buffer, GatewayApiOption gatewayApiOption) {
        String json = JSON.toJSONString(gatewayApiOption);
        byte[] encoded = json.getBytes();
        // 写入数据长度
        buffer.appendInt(encoded.length);
        // 写入数据
        buffer.appendBytes(encoded);
    }

    /**
     * 从 Buffer 中读取二进制数据并解码为 GatewayApiOption 对象
     */
    @Override
    public GatewayApiOption decodeFromWire(int pos, Buffer buffer) {
        // 读取数据长度
        int length = buffer.getInt(pos);
        // 读取数据
        byte[] encoded = buffer.getBytes(pos + 4, pos + 4 + length);
        String json = new String(encoded);
        return JSON.parseObject(json, GatewayApiOption.class);
    }

    @Override
    public GatewayApiOption transform(GatewayApiOption gatewayAppOption) {
        return gatewayAppOption;
    }

    @Override
    public String name() {
        //解码器唯一名称
        return "GatewayApiOptionCodec";
    }

    @Override
    public byte systemCodecID() {
        // 返回 -1 表示这是一个自定义编解码器
        return -1;
    }
}
