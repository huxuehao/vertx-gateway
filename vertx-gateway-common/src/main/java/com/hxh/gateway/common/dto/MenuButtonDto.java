package com.hxh.gateway.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hxh.gateway.common.entity.MenuButton;
import com.hxh.gateway.common.util.tree.TreeNode;
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
public class MenuButtonDto extends MenuButton implements TreeNode<Long> {
    private List<MenuButtonDto> children;
    @JsonSerialize(using = ToStringSerializer.class)
    public Long parentId;
    private boolean disabled;

    @Override
    public void setChildren(List<? extends TreeNode<?>> children) {
        this.children = (List<MenuButtonDto>)children;
    }

    @Override
    public Object clone() {
        MenuButtonDto organization;
        try {
            organization = (MenuButtonDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuButtonDto that = (MenuButtonDto) o;
        return Objects.equals(id, that.id);
    }
}
