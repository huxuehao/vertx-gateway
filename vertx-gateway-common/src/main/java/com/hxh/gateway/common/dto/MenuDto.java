package com.hxh.gateway.common.dto;

import com.hxh.gateway.common.entity.Menu;
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
public class MenuDto extends Menu implements TreeNode<Long> {
    private List<MenuDto> children;

    @Override
    public void setChildren(List<? extends TreeNode<?>> children) {
        this.children = (List<MenuDto>)children;
    }

    @Override
    public Object clone() {
        MenuDto organization;
        try {
            organization = (MenuDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuDto that = (MenuDto) o;
        return Objects.equals(id, that.id);
    }
}
