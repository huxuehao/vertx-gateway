package com.gateway.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gateway.common.util.tree.TreeNode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 描述：客户端授权树
 *
 * @author huxuehao
 **/
@Data
public class ClientAuthTreeDto implements Serializable, TreeNode<Long> {
    static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    private String name;
    private Integer type;
    private Boolean disabled;
    private Integer online;
    private Integer port;
    private String path;
    private List<ClientAuthTreeDto> children;

    @Override
    public void setChildren(List<? extends TreeNode<?>> children) {
        this.children = (List<ClientAuthTreeDto>) children;
    }

    @Override
    public List<? extends TreeNode<Long>> getChildren() {
        return children;
    }

    @Override
    public Object clone() {
        ClientAuthTreeDto dto;
        try {
            dto = (ClientAuthTreeDto)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientAuthTreeDto that = (ClientAuthTreeDto) o;
        return Objects.equals(id, that.id);
    }
}
