package com.hxh.gateway.common.dto;

import com.hxh.gateway.common.entity.Role;
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
public class RoleDto extends Role implements TreeNode<Long> {
    private List<RoleDto> children;

    @Override
    public void setChildren(List<? extends TreeNode<?>> children) {
        this.children = (List<RoleDto>)children;
    }

    @Override
    public Object clone() {
        RoleDto organization;
        try {
            organization = (RoleDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleDto that = (RoleDto) o;
        return Objects.equals(id, that.id);
    }
}
