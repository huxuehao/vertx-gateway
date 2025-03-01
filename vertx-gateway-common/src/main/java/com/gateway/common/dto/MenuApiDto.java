package com.gateway.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gateway.common.entity.MenuApi;
import com.gateway.common.util.tree.TreeNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * 描述：
 *
 * @author huxuehao
 **/
@Getter
@Setter
public class MenuApiDto extends MenuApi implements TreeNode<Long> {
    private List<MenuApiDto> children;
    @JsonSerialize(using = ToStringSerializer.class)
    public Long parentId;
    private boolean disabled;

    @Override
    public void setChildren(List<? extends TreeNode<?>> children) {
        this.children = (List<MenuApiDto>)children;
    }

    @Override
    public Object clone() {
        MenuApiDto organization;
        try {
            organization = (MenuApiDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuApiDto that = (MenuApiDto) o;
        return Objects.equals(id, that.id);
    }
}
